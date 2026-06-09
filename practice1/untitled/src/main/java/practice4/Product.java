package practice4;

import java.util.Objects;

public class Product {
    private Integer id;
    private String name;
    private Integer groupId;
    private double price;
    private int quantity;

    public Product(String name, Integer groupId, double price, int quantity) {
        this(null, name, groupId, price, quantity);
    }

    public Product(Integer id, String name, Integer groupId, double price, int quantity) {
        this.id = id;
        this.name = name;
        this.groupId = groupId;
        this.price = price;
        this.quantity = quantity;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Double.compare(price, product.price) == 0 &&
                quantity == product.quantity &&
                Objects.equals(id, product.id) &&
                Objects.equals(name, product.name) &&
                Objects.equals(groupId, product.groupId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, groupId, price, quantity);
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", groupId=" + groupId +
                ", price=" + price +
                ", quantity=" + quantity +
                '}';
    }
}
