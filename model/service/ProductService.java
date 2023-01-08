package ra.dev.model.service;import org.springframework.data.domain.Page;import org.springframework.data.domain.Pageable;import ra.dev.model.dto.request.product.ProductCreat;import ra.dev.model.entity.Product;import java.util.List;public interface ProductService {    List<Product> findAll();    Product findByID(int productID);    ProductCreat save(ProductCreat product);    Product saveOrUpdate(Product product);    void delete(int productID);    List<Product> searchByName(String productName);    List<Product> sortBookByBookName(String direction);    List<Product> sortByNameAndPrice(String directionName, String directionPrice);    List<Product> sortByName_Price_Id(String directionName, String directionPrice, String directionId);    Page<Product> getPagging(Pageable pageable);}