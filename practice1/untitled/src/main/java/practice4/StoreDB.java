package practice4;

import java.util.List;
import java.util.Optional;

public interface StoreDB {
    int insertGroup(ProductGroup group);
    int insertProduct(Product product);

    Optional<Product> getProductById(int id);
    Optional<ProductGroup> getGroupById(int id);
    int getProductQuantity(int productId);

    boolean setPrice(int productId, double newPrice);
    boolean updateQuantity(int productId, int amount);
    boolean updateProduct(Product product);

    boolean deleteProduct(int id);
    boolean deleteGroup(int id);

    List<Product> searchProducts(Filter filter);
}
