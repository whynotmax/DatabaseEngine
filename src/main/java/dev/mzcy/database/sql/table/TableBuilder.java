package dev.mzcy.database.sql.table;

import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

/**
 * A builder class for constructing SQL table creation statements.
 */
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class TableBuilder {

    /** The name of the table to be created. */
    String tableName;

    /** The list of column definitions for the table. */
    List<String> columns;

    /**
     * Constructs a new TableBuilder for the specified table name.
     *
     * @param tableName the name of the table to be created
     */
    public TableBuilder(String tableName) {
        this.tableName = tableName;
        this.columns = new ArrayList<>();
    }

    /**
     * Adds a column definition to the table.
     *
     * @param columnDefinition the SQL definition of the column
     * @return the current TableBuilder instance for method chaining
     */
    public TableBuilder addColumn(String columnDefinition) {
        columns.add(columnDefinition);
        return this;
    }

    /**
     * Builds the SQL CREATE TABLE statement.
     *
     * @return the SQL CREATE TABLE statement as a String
     */
    public String build() {
        StringBuilder sql = new StringBuilder("CREATE TABLE ");
        sql.append(tableName).append(" (");
        for (int i = 0; i < columns.size(); i++) {
            sql.append(columns.get(i));
            if (i < columns.size() - 1) {
                sql.append(", ");
            }
        }
        sql.append(");");
        return sql.toString();
    }
}