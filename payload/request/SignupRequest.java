package ra.dev.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequest {
    private String userName;
    private String password;
    private String email;
    private String phoneNumber;
    private String fullName;
    private String address;
    private boolean userStatus;
    private Set<String> listRoles;


}
