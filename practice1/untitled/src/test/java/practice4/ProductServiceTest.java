package practice4;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ProductServiceTest
{
    private ProductService db;

    @BeforeEach
    void setUp() {
        db = new ProductService(":memory:");
    }

    @Test
    void testInsertAndGetGroup() {
        ProductGroup group = new ProductGroup("Cables");

        int groupId = db.insertGroup(group);
        Optional<ProductGroup> retrievedGroup = db.getGroupById(groupId);

        assertTrue(retrievedGroup.isPresent(), "Group must be found in the DB");
        assertEquals("Cables", retrievedGroup.get().getName(), "Group names must match");
        assertEquals(groupId, retrievedGroup.get().getId(), "IDs must match");
    }

    @Test
    void testInsertAndGetProduct() {
        int groupId = db.insertGroup(new ProductGroup("Tools"));
        Product product = new Product("Hammer", groupId, 250.50, 10);

        int productId = db.insertProduct(product);
        Optional<Product> retrievedProduct = db.getProductById(productId);

        assertTrue(retrievedProduct.isPresent());
        assertEquals("Hammer", retrievedProduct.get().getName());
        assertEquals(250.50, retrievedProduct.get().getPrice());
        assertEquals(10, retrievedProduct.get().getQuantity());
        assertEquals(groupId, retrievedProduct.get().getGroupId());
    }

    @Test
    void testUpdateQuantity() {
        int groupId = db.insertGroup(new ProductGroup("Electrical"));
        int productId = db.insertProduct(new Product("Lamp", groupId, 50.0, 100));

        boolean success = db.updateQuantity(productId, -15);
        assertTrue(success, "Update should return true");

        int newQuantity = db.getProductQuantity(productId);
        assertEquals(85, newQuantity, "Quantity should decrease to 85");
    }

    @Test
    void testSearchProductsWithFilter() {
        int groupId = db.insertGroup(new ProductGroup("Electronics"));

        db.insertProduct(new Product("Resistor 1", groupId, 5.0, 1000));
        db.insertProduct(new Product("Resistor 12", groupId, 5.5, 500));
        db.insertProduct(new Product("Capacitor", groupId, 15.0, 200));

        Filter filter = new Filter.FilterBuilder()
                .name("Resistor")
                .maxPrice(10.0)
                .build();

        List<Product> results = db.searchProducts(filter);
        assertEquals(2, results.size(), "Should find exactly 2 resistors");
        assertTrue(results.stream().allMatch(p -> p.getName().contains("Resistor")), "All found products must contain the word 'Resistor'");
        assertTrue(results.stream().allMatch(p -> p.getPrice() <= 10.0), "Price of all found products must be <= 10.0");
    }

    @Test
    void testDeleteProduct() {
        int groupId = db.insertGroup(new ProductGroup("Test"));
        int productId = db.insertProduct(new Product("Test product", groupId, 10.0, 1));

        boolean deleteSuccess = db.deleteProduct(productId);
        assertTrue(deleteSuccess);

        Optional<Product> afterDelete = db.getProductById(productId);
        assertFalse(afterDelete.isPresent(), "Optional must be empty after deletion");
    }
}