package practice4;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductService implements StoreDB {
    private final Connection connection;

    public ProductService(String dbName) {
        try {
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + dbName);
        } catch (SQLException e) {
            throw new RuntimeException("Can't create SQLite DB", e);
        }

        init();
    }

    public void init() {
        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON;");
            statement.execute("""
                CREATE TABLE IF NOT EXISTS product_groups (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL UNIQUE
                )
                """);
            statement.execute("""
                CREATE TABLE IF NOT EXISTS products (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    group_id INTEGER,
                    price REAL NOT NULL,
                    quantity INTEGER NOT NULL DEFAULT 0,
                    FOREIGN KEY (group_id) REFERENCES product_groups (id) ON DELETE SET NULL
                )
                """);
            System.out.println("db tables created");
        } catch (SQLException e) {
            throw new RuntimeException("Exception while DB init", e);
        }
    }

    public Connection getConnection() {
        return connection;
    }

    @Override
    public int insertGroup(ProductGroup group) {
        String str = "INSERT INTO product_groups(name) VALUES (?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(str, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, group.getName());
            int inserted = preparedStatement.executeUpdate();
            if (inserted < 1) {
                throw new RuntimeException("group inserting failed");
            }
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            return generatedKeys.getInt(1);

        } catch (SQLException e) {
            throw new RuntimeException("can't insert group: ", e);
        }
    }

    @Override
    public int insertProduct(Product product) {
        String str = "INSERT INTO products(name, group_id, price, quantity) VALUES (?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(str, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, product.getName());

            if (product.getGroupId() != null) {
                preparedStatement.setInt(2, product.getGroupId());
            } else {
                preparedStatement.setNull(2, Types.INTEGER);
            }

            preparedStatement.setDouble(3, product.getPrice());
            preparedStatement.setInt(4, product.getQuantity());

            int inserted = preparedStatement.executeUpdate();
            if (inserted < 1) {
                throw new RuntimeException("product insertion failed");
            }
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            return generatedKeys.getInt(1);

        } catch (SQLException e) {
            throw new RuntimeException("couldn't insert product: ", e);
        }
    }

    @Override
    public Optional<Product> getProductById(int id) {
        String str = "SELECT * FROM products WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(str)) {
            preparedStatement.setInt(1, id);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    Integer groupId = rs.getInt("group_id");
                    if (rs.wasNull()) {
                        groupId = null;
                    }
                    Product product = new Product(
                            rs.getInt("id"),
                            rs.getString("name"),
                            groupId,
                            rs.getDouble("price"),
                            rs.getInt("quantity")
                    );
                    return Optional.of(product);
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("couldn't get product by id: " + id, e);
        }
    }

    @Override
    public Optional<ProductGroup> getGroupById(int id) {
        String str = "SELECT * FROM product_groups WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(str)) {
            preparedStatement.setInt(1, id);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    ProductGroup group = new ProductGroup(
                            rs.getInt("id"),
                            rs.getString("name")
                    );
                    return Optional.of(group);
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("couldn't get group by id: " + id, e);
        }
    }

    @Override
    public boolean setPrice(int productId, double newPrice) {
        String str = "UPDATE products SET price = ? WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(str)) {
            preparedStatement.setDouble(1, newPrice);
            preparedStatement.setInt(2, productId);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("couldn't change price for product id: " + productId, e);
        }
    }

    @Override
    public int getProductQuantity(int productId) {
        String str = "SELECT quantity FROM products WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(str)) {
            preparedStatement.setInt(1, productId);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("quantity");
                }
            }
            throw new RuntimeException("couldn't find product with id: " + productId);
        } catch (SQLException e) {
            throw new RuntimeException("couldn't get quantity for product id: " + productId, e);
        }
    }

    @Override
    public boolean updateQuantity(int productId, int amount) {
        String str = "UPDATE products SET quantity = quantity + ? WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(str)) {
            preparedStatement.setInt(1, amount);
            preparedStatement.setInt(2, productId);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("couldn't update quantity for product id: " + productId, e);
        }
    }

    @Override
    public boolean updateProduct(Product product) {
        String sql = "UPDATE products SET name = ?, group_id = ?, price = ?, quantity = ? WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, product.getName());

            if (product.getGroupId() != null) {
                preparedStatement.setInt(2, product.getGroupId());
            } else {
                preparedStatement.setNull(2, Types.INTEGER);
            }

            preparedStatement.setDouble(3, product.getPrice());
            preparedStatement.setInt(4, product.getQuantity());
            preparedStatement.setInt(5, product.getId());

            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("couldn't update product: " + product, e);
        }
    }

    @Override
    public boolean deleteProduct(int id) {
        String str = "DELETE FROM products WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(str)) {
            preparedStatement.setInt(1, id);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("couldn't delete product with id: " + id, e);
        }
    }

    @Override
    public boolean deleteGroup(int id) {
        String str = "DELETE FROM product_groups WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(str)) {
            preparedStatement.setInt(1, id);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("couldn't delete group with id: " + id, e);
        }
    }

    @Override
    public List<Product> searchProducts(Filter filter) {
        SQLWrapper queryBuilder = new SQLWrapper("SELECT * FROM products WHERE 1=1")
                .appendLikeCondition("AND name LIKE ?", filter.getName())
                .appendCondition("AND group_id = ?", filter.getGroupId())
                .appendCondition("AND price >= ?", filter.getMinPrice())
                .appendCondition("AND price <= ?", filter.getMaxPrice())
                .appendCondition("AND quantity >= ?", filter.getMinQuantity())
                .appendCondition("AND quantity <= ?", filter.getMaxQuantity())
                .appendRaw("LIMIT ? OFFSET ?")
                .appendCondition("", filter.getLimit())
                .appendCondition("", filter.getOffset());

        List<Product> resultList = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(queryBuilder.getSql())) {
            queryBuilder.applyParameters(ps);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Integer groupId = rs.getInt("group_id");
                    if (rs.wasNull()) {
                        groupId = null;
                    }
                    Product product = new Product(
                            rs.getInt("id"),
                            rs.getString("name"),
                            groupId,
                            rs.getDouble("price"),
                            rs.getInt("quantity")
                    );
                    resultList.add(product);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while filtering", e);
        }
        return resultList;
    }
}
