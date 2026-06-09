package practice4;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SQLWrapper {
    private final StringBuilder sql;
    private final List<Object> parameters;

    public SQLWrapper(String baseQuery) {
        this.sql = new StringBuilder(baseQuery);
        this.parameters = new ArrayList<>();
    }

    public SQLWrapper appendCondition(String condition, Object value) {
        if (value != null) {
            sql.append(" ").append(condition);
            parameters.add(value);
        }
        return this;
    }

    public SQLWrapper appendLikeCondition(String condition, String value) {
        if (value != null && !value.trim().isEmpty()) {
            sql.append(" ").append(condition);
            parameters.add("%" + value + "%");
        }
        return this;
    }

    public SQLWrapper appendRaw(String rawSql) {
        sql.append(" ").append(rawSql);
        return this;
    }

    public String getSql() {
        return sql.toString();
    }

    public void applyParameters(PreparedStatement ps) throws SQLException {
        for (int i = 0; i < parameters.size(); i++) {
            ps.setObject(i + 1, parameters.get(i));
        }
    }
}
