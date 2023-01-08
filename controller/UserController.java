package ra.dev.controller;import org.apache.catalina.User;import org.springframework.beans.factory.annotation.Autowired;import org.springframework.http.ResponseEntity;import org.springframework.security.access.prepost.PreAuthorize;import org.springframework.security.authentication.AuthenticationManager;import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;import org.springframework.security.core.Authentication;import org.springframework.security.core.context.SecurityContextHolder;import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;import org.springframework.security.crypto.password.PasswordEncoder;import org.springframework.web.bind.annotation.*;import ra.dev.jwt.JwtTokenProvider;import ra.dev.model.dto.request.cart.UpdateCart;import ra.dev.model.dto.request.user.ChangePass;import ra.dev.model.dto.request.user.UserList;import ra.dev.model.dto.request.user.UserUpdate;import ra.dev.model.entity.ERole;import ra.dev.model.entity.Product;import ra.dev.model.entity.Roles;import ra.dev.model.entity.Users;import ra.dev.model.service.RoleService;import ra.dev.model.service.UserService;import ra.dev.payload.request.LoginRequest;import ra.dev.payload.request.SignupRequest;import ra.dev.payload.response.JwtResponse;import ra.dev.payload.response.MessageResponse;import ra.dev.security.CustomUserDetails;import ra.dev.security.CustomUserDetailsService;import ra.dev.sendMail.ProvideSendEmail;import java.util.ArrayList;import java.util.HashSet;import java.util.List;import java.util.Set;import java.util.stream.Collectors;@CrossOrigin(origins = "*")@RestController@RequestMapping("api/v1/auth")public class UserController {    @Autowired    private UserService userService;    @Autowired    private AuthenticationManager authenticationManager;    @Autowired    private JwtTokenProvider tokenProvider;    @Autowired    private RoleService roleService;    @Autowired    private PasswordEncoder encoder;    @Autowired    private CustomUserDetailsService customUserDetailsService;    @Autowired    private ProvideSendEmail provideSendEmail;    @GetMapping("/getToken")    public ResponseEntity<?> sendEmail(@RequestParam("email") String email) {        try {            String jwt = tokenProvider.generateTokenEmail(email);            provideSendEmail.sendSimpleMessage(email, "Token", jwt);            return ResponseEntity.ok("Send email successfully");        } catch (Exception e) {            return ResponseEntity.ok("Failed");        }    }    @PostMapping("/resetPass")    public Users resetPass(@RequestParam("token") String token, @RequestBody String newPass) {        String userName = tokenProvider.getUserNameFromJwt(token);        Users users = userService.findByUserName(userName);        users.setPassword(encoder.encode(newPass));        return userService.saveOrUpdate(users);    }    @GetMapping("/listUser")    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")    public List<UserList> getListUser() {        List<Users> usersList = userService.findAll();        List<UserList> listUser = new ArrayList<>();        for (Users users : usersList) {            if (users.getListRoles().size() == 1) {                UserList userList = new UserList();                userList.setUserId(users.getUserId());                userList.setUserName(users.getUserName());                userList.setFullName(users.getFullName());                userList.setPhoneNumber(users.getPhoneNumber());                userList.setAddress(users.getAddress());                userList.setEmail(users.getEmail());                listUser.add(userList);            }        }        return listUser;    }    @GetMapping("/listMod")    @PreAuthorize("hasRole('ADMIN')")    public List<UserList> getListMod() {        List<Users> usersList = userService.findAll();        List<UserList> listUser = new ArrayList<>();        for (Users users : usersList) {            if (users.getListRoles().size() == 2) {                UserList userList = new UserList();                userList.setUserId(users.getUserId());                userList.setUserName(users.getUserName());                userList.setFullName(users.getFullName());                userList.setPhoneNumber(users.getPhoneNumber());                userList.setAddress(users.getAddress());                userList.setEmail(users.getEmail());                listUser.add(userList);            }        }        return listUser;    }    @PutMapping("/blockAcc/{userID}")    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")    public ResponseEntity<?> blockAcc(@PathVariable("userID") int userID, @RequestBody Users users) {        Users findUser = userService.findByID(userID);        findUser.setUserStatus(users.isUserStatus());        userService.saveOrUpdate(findUser);        return ResponseEntity.ok("Update successfully !");    }    @PostMapping("/signup")    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signupRequest) {        if (userService.existsByUserName(signupRequest.getUserName())) {            return ResponseEntity.badRequest().body(new MessageResponse("Error: Usermame is already"));        }        if (userService.existsByEmail(signupRequest.getEmail())) {            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already"));        }        Users user = new Users();        user.setUserName(signupRequest.getUserName());        user.setPassword(encoder.encode(signupRequest.getPassword()));        user.setFullName(signupRequest.getFullName());        user.setEmail(signupRequest.getEmail());        user.setPhoneNumber(signupRequest.getPhoneNumber());        user.setAddress(signupRequest.getAddress());        user.setUserStatus(true);        Set<String> strRoles = signupRequest.getListRoles();        Set<Roles> listRoles = new HashSet<>();        if (strRoles == null) {            //User quyen mac dinh            Roles userRole = roleService.findByRoleName(ERole.ROLE_USER).orElseThrow(() -> new RuntimeException("Error: Role is not found"));            listRoles.add(userRole);        } else {            strRoles.forEach(role -> {                switch (role) {                    case "admin":                        Roles adminRole = roleService.findByRoleName(ERole.ROLE_ADMIN)                                .orElseThrow(() -> new RuntimeException("Error: Role is not found"));                        listRoles.add(adminRole);                    case "moderator":                        Roles modRole = roleService.findByRoleName(ERole.ROLE_MODERATOR)                                .orElseThrow(() -> new RuntimeException("Error: Role is not found"));                        listRoles.add(modRole);                    case "user":                        Roles userRole = roleService.findByRoleName(ERole.ROLE_USER)                                .orElseThrow(() -> new RuntimeException("Error: Role is not found"));                        listRoles.add(userRole);                }            });        }        user.setListRoles(listRoles);        userService.saveOrUpdate(user);        return ResponseEntity.ok(new MessageResponse("User registered successfully"));    }    @PutMapping("/changePass")    public ResponseEntity<?> changePassword(@RequestBody ChangePass changePass) {        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();        Users users = userService.findByID(userDetails.getUserId());        BCryptPasswordEncoder bc = new BCryptPasswordEncoder();        boolean passChecker = bc.matches(changePass.getOldPassword(), users.getPassword());        if (passChecker) {            boolean checkDuplicate = bc.matches(changePass.getPassword(), users.getPassword());            if (checkDuplicate) {                return ResponseEntity.ok(new MessageResponse("The new password must be different from the old password !"));            } else {                users.setPassword(encoder.encode(changePass.getPassword()));                userService.saveOrUpdate(users);                return ResponseEntity.ok(new MessageResponse("Change password successfully !"));            }        } else {            return ResponseEntity.ok(new MessageResponse("Password does not match ! Change password fail"));        }    }//    @PostMapping("/updateInfo")//    public Users updateInfo(@RequestBody Users users) {//        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();//        Users usersUpdate = userService.findByID(userDetails.getUserId());//        usersUpdate.setFullName(users.getFullName());//        usersUpdate.setAddress(users.getAddress());//////    }    @PostMapping("/updateUser/{userID}")    @PreAuthorize("hasRole('ADMIN')")    public Users updateUser(@PathVariable("userID") int userID, @RequestBody UserUpdate userUpdate) {        Users users = userService.findByID(userID);        Set<String> strRoles = userUpdate.getListRoles();        Set<Roles> listRoles = new HashSet<>();        if (strRoles == null) {            //User quyen mac dinh            Roles userRole = roleService.findByRoleName(ERole.ROLE_USER).orElseThrow(() -> new RuntimeException("Error: Role is not found"));            listRoles.add(userRole);        } else {            strRoles.forEach(role -> {                switch (role) {                    case "admin":                        Roles adminRole = roleService.findByRoleName(ERole.ROLE_ADMIN)                                .orElseThrow(() -> new RuntimeException("Error: Role is not found"));                        listRoles.add(adminRole);                    case "moderator":                        Roles modRole = roleService.findByRoleName(ERole.ROLE_MODERATOR)                                .orElseThrow(() -> new RuntimeException("Error: Role is not found"));                        listRoles.add(modRole);                    case "user":                        Roles userRole = roleService.findByRoleName(ERole.ROLE_USER)                                .orElseThrow(() -> new RuntimeException("Error: Role is not found"));                        listRoles.add(userRole);                }            });        }        users.setListRoles(listRoles);        return userService.saveOrUpdate(users);    }    @PostMapping("/signin")    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {        Authentication authentication = authenticationManager.authenticate(                new UsernamePasswordAuthenticationToken(loginRequest.getUserName(), loginRequest.getPassword())        );        SecurityContextHolder.getContext().setAuthentication(authentication);        CustomUserDetails customUserDetail = (CustomUserDetails) authentication.getPrincipal();        if (!customUserDetail.isUserStatus()) {            return ResponseEntity.ok("Your account have been block !");        } else {            //Sinh JWT tra ve client            String jwt = tokenProvider.generateToken(customUserDetail);            //Lay cac quyen cua user            List<String> listRoles = customUserDetail.getAuthorities().stream()                    .map(item -> item.getAuthority()).collect(Collectors.toList());            return ResponseEntity.ok(new JwtResponse(jwt, customUserDetail.getUsername(), customUserDetail.getEmail(),                    customUserDetail.getPhone(), listRoles));        }    }    @PostMapping("/test")    public ResponseEntity<?> test() {        return ResponseEntity.ok("Connected");    }}