package practice4;

public class Filter {
    private final String name;
    private final Integer groupId;
    private final Double minPrice;
    private final Double maxPrice;
    private final Integer minQuantity;
    private final Integer maxQuantity;
    private final int limit;
    private final int offset;

    private Filter(FilterBuilder builder) {
        this.name = builder.name;
        this.groupId = builder.groupId;
        this.minPrice = builder.minPrice;
        this.maxPrice = builder.maxPrice;
        this.minQuantity = builder.minQuantity;
        this.maxQuantity = builder.maxQuantity;
        this.limit = builder.limit;
        this.offset = builder.offset;
    }

    public String getName() { return name; }
    public Integer getGroupId() { return groupId; }
    public Double getMinPrice() { return minPrice; }
    public Double getMaxPrice() { return maxPrice; }
    public Integer getMinQuantity() { return minQuantity; }
    public Integer getMaxQuantity() { return maxQuantity; }
    public int getLimit() { return limit; }
    public int getOffset() { return offset; }

    public static class FilterBuilder {
        private String name;
        private Integer groupId;
        private Double minPrice;
        private Double maxPrice;
        private Integer minQuantity;
        private Integer maxQuantity;
        private int limit = 10;
        private int offset = 0;

        public FilterBuilder name(String name) {
            this.name = name;
            return this;
        }

        public FilterBuilder groupId(Integer groupId) {
            this.groupId = groupId;
            return this;
        }

        public FilterBuilder minPrice(Double minPrice) {
            this.minPrice = minPrice;
            return this;
        }

        public FilterBuilder maxPrice(Double maxPrice) {
            this.maxPrice = maxPrice;
            return this;
        }

        public FilterBuilder minQuantity(Integer minQuantity) {
            this.minQuantity = minQuantity;
            return this;
        }

        public FilterBuilder maxQuantity(Integer maxQuantity) {
            this.maxQuantity = maxQuantity;
            return this;
        }

        public FilterBuilder limit(int limit) {
            this.limit = limit;
            return this;
        }

        public FilterBuilder offset(int offset) {
            this.offset = offset;
            return this;
        }

        public Filter build() {
            return new Filter(this);
        }
    }
}