package dev.mzcy.database.credentials;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/**
 * Database credentials class. Used to store database connection information.
 */
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Setter
public class DatabaseCredentials {

    String host;
    int port;
    String database;
    String username;
    String password;

    // Used for MongoDB connection
    String connectionUrl;

    /**
     * Constructor
     * @param host              Host
     * @param port              Port
     * @param database          Database
     * @param username          Username
     * @param password          Password
     * @param connectionUrl     Connection URL
     */
    private DatabaseCredentials(String host, int port, String database, String username, String password, String connectionUrl) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
        this.connectionUrl = connectionUrl;
    }

    /**
     * Create database credentials from host, port, database, username and password.
     * @param host      Host
     * @param port      Port
     * @param database  Database
     * @param username  Username
     * @param password  Password
     * @return          Database credentials
     */
    public static DatabaseCredentials createMySQLDatabase(String host, int port, String database, String username, String password) {
        return new DatabaseCredentials(host, port, database, username, password, null);
    }

    /**
     * Create database credentials from database name. This is used for SQLite connections.
     * @param database  Database name
     * @return          Database credentials
     */
    public static DatabaseCredentials createSQLiteDatabase(String database) {
        return new DatabaseCredentials(null, 0, database, null, null, null);
    }

    /**
     * Create database credentials from connection URL. This is used for MongoDB connections.
     * @param connectionUrl Connection URL
     * @return              Database credentials
     */
    public static DatabaseCredentials createMongoDatabase(String connectionUrl, String database) {
        return new DatabaseCredentials(null, 0, database, null, null, connectionUrl);
    }

    /**
     * Checks if the database is MongoDB.
     * @return  True if MongoDB, false otherwise
     */
    public boolean isMongoDB() {
        return connectionUrl != null;
    }

    /**
     * Gets the host of the database.
     * @return  the host
     */
    public String getHost() {
        return host;
    }

    /**
     * Gets the port of the database.
     * @return  the port
     */
    public int getPort() {
        return port;
    }

    /**
     * Gets the database name.
     * @return  the database name
     */
    public String getDatabase() {
        return database;
    }

    /**
     * Gets the username of the database.
     * @return  the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the password of the database.
     * @return  the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Gets the connection URL of the database.
     * @return  the connection URL
     */
    public String getConnectionUrl() {
        return connectionUrl;
    }
}
