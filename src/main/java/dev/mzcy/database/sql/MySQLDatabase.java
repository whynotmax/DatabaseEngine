package dev.mzcy.database.sql;

import dev.mzcy.database.Database;
import dev.mzcy.database.credentials.DatabaseCredentials;
import dev.mzcy.database.sql.annotation.TableField;
import dev.mzcy.database.sql.table.TableBuilder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

import java.lang.reflect.Field;
import java.sql.*;

/**
 * Implementation of the Database interface for MySQL.
 *
 * @param <E> the type of the entity
 */
@Getter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class MySQLDatabase<E> implements Database<E> {

    Connection connection;
    Class<? extends E> entityClass;
    @NonFinal
    String currentTable;

    /**
     * Constructs a MySQLDatabase instance.
     *
     * @param credentials the database credentials
     * @param currentTable the current table name
     * @param entityClass the class of the entity
     */
    public MySQLDatabase(DatabaseCredentials credentials, String currentTable, Class<? extends E> entityClass) {
        connection = createMySQLConnection(credentials);
        this.currentTable = currentTable;
        this.entityClass = entityClass;
    }

    /**
     * Creates a MySQL connection using the provided credentials.
     *
     * @param credentials the database credentials
     * @return the MySQL connection
     */
    private Connection createMySQLConnection(DatabaseCredentials credentials) {
        try {
            String url = "jdbc:mysql://" + credentials.getHost() + ":" + credentials.getPort() + "/" + credentials.getDatabase();
            return DriverManager.getConnection(url, credentials.getUsername(), credentials.getPassword());
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Creates a table using the provided TableBuilder.
     *
     * @param tableBuilder the table builder
     */
    public void createTable(TableBuilder tableBuilder) {
        try {
            connection.createStatement().execute(tableBuilder.build());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Switches the current table to the specified table.
     *
     * @param table the new table name
     */
    public void switchTable(String table) {
        this.currentTable = table;
        try {
            connection.createStatement().execute("USE " + table);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves the provided entity to the database.
     *
     * @param entity the entity to save
     */
    @Override
    public void save(E entity) {
        Class<?> clazz = entity.getClass();
        StringBuilder columns = new StringBuilder();
        StringBuilder values = new StringBuilder();
        String tableName = clazz.getSimpleName().toLowerCase(); // Assuming table name is the class name in lowercase

        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(TableField.class)) {
                field.setAccessible(true);
                try {
                    columns.append(field.getName()).append(",");
                    values.append("'").append(field.get(entity)).append("',");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        if (!columns.isEmpty()) columns.setLength(columns.length() - 1);
        if (!values.isEmpty()) values.setLength(values.length() - 1);

        String sql = String.format("INSERT INTO %s (%s) VALUES (%s);", tableName, columns, values);

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves an entity from the database using the provided primary key.
     *
     * @param key the primary key of the entity
     * @return the retrieved entity, or null if not found
     */
    @Override
    public E getEntity(String key) {
        Class<? extends E> clazz = entityClass;
        StringBuilder columns = new StringBuilder();
        String tableName = currentTable;

        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(TableField.class)) {
                columns.append(field.getName()).append(",");
            }
        }

        if (!columns.isEmpty()) columns.setLength(columns.length() - 1);

        String sql = String.format("SELECT %s FROM %s WHERE id = ?;", columns, tableName);

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, key);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                E entity = (E) clazz.getDeclaredConstructor().newInstance();
                for (Field field : clazz.getDeclaredFields()) {
                    if (field.isAnnotationPresent(TableField.class)) {
                        field.setAccessible(true);
                        field.set(entity, resultSet.getObject(field.getName()));
                    }
                }
                return entity;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Closes the database connection.
     */
    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes the specified table from the database.
     *
     * @param table the table name to delete
     */
    public void deleteTable(String table) {
        try {
            connection.createStatement().execute("DROP TABLE " + table);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes an entity from the database using the provided primary key.
     *
     * @param key the primary key of the entity to delete
     */
    public void deleteEntity(String key) {
        String tableName = currentTable;

        String sql = String.format("DELETE FROM %s WHERE id = ?;", tableName);

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, key);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}