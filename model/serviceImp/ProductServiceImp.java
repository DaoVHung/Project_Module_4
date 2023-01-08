package ra.dev.model.serviceImp;import org.springframework.beans.factory.annotation.Autowired;import org.springframework.data.domain.Page;import org.springframework.data.domain.Pageable;import org.springframework.data.domain.Sort;import org.springframework.stereotype.Service;import ra.dev.model.dto.request.product.ProductCreat;import ra.dev.model.entity.Product;import ra.dev.model.repository.ProductRepository;import ra.dev.model.service.ProductService;import javax.transaction.Transactional;import java.sql.SQLException;import java.util.List;@Service@Transactional(rollbackOn = SQLException.class)public class ProductServiceImp implements ProductService {    @Autowired    ProductRepository productRepository;    @Override    public List<Product> findAll() {        return productRepository.findAll();    }    @Override    public Product findByID(int productID) {        return productRepository.findById(productID).get();    }    @Override    public Product saveOrUpdate(Product product) {        return productRepository.save(product);    }    public ProductCreat save(ProductCreat product) {        return productRepository.save(product);    }    @Override    public void delete(int productID) {        productRepository.deleteById(productID);    }    @Override    public List<Product> searchByName(String productName) {        return null;    }    @Override    public List<Product> sortBookByBookName(String direction) {        return null;    }    @Override    public List<Product> sortByNameAndPrice(String directionName, String directionPrice) {        if (directionName.equals("asc")) {            if (directionPrice.equals("asc")) {                return productRepository.findAll(Sort.by("productName").and(Sort.by("price")));            } else {                return productRepository.findAll(Sort.by("productName").and(Sort.by("price").descending()));            }        } else {            if (directionPrice.equals("asc")) {                return productRepository.findAll(Sort.by("productName").descending().and(Sort.by("price")));            } else {                return productRepository.findAll(Sort.by("productName").descending().and(Sort.by("price").descending()));            }        }    }    @Override    public List<Product> sortByName_Price_Id(String directionName, String directionPrice, String directionId) {        return null;    }    @Override    public Page<Product> getPagging(Pageable pageable) {        return productRepository.findAll(pageable);    }}