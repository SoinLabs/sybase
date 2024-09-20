
import com.sybase.jdbc4.jdbc.SybDataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 * @author DarkJ24
 */
public class ConnectionPool {
    
    private final HikariDataSource dataSource;
    
    /**
     * Creates a new ConnectionPool with the given parameters
     * @param host The host of the database
     * @param port The port of the database
     * @param dbName The name of the database
     * @param username The username to connect to the database
     * @param password The password to connect to the database
     * @param minConnections The minimum number of connections in the pool
     * @param maxConnections The maximum number of connections in the pool
     * @param connectionTimeout The timeout to wait for a connection
     * @param idleTimeout The timeout for an idle connection
     * @param keepaliveTime The time to keep a connection alive
     * @param maxLifetime The maximum time a connection can be alive
     * @param autoCommit Indicates if autoCommit is on or not
     * @return
     * @throws SQLException
     */
    public static ConnectionPool create(
      String host, int port, String dbName, String username, String password,
      int minConnections, int maxConnections,
      int connectionTimeout, int idleTimeout, int keepaliveTime, int maxLifetime, boolean autoCommit
      ) throws SQLException {
 
        HikariConfig config = new HikariConfig();
        config.setDataSourceClassName("com.sybase.jdbc4.jdbc.SybDataSource");
        config.addDataSourceProperty("serverName", host);
        config.addDataSourceProperty("portNumber", port);
        config.addDataSourceProperty("user", username);
        config.addDataSourceProperty("password", password);
        config.addDataSourceProperty("databaseName", dbName);
        config.setIdleTimeout(idleTimeout);
        config.setConnectionTimeout(connectionTimeout);
        config.setMaximumPoolSize(maxConnections);
        config.setMinimumIdle(minConnections);
        config.setKeepaliveTime(keepaliveTime);
        config.setMaxLifetime(maxLifetime);
        config.setAutoCommit(autoCommit);
        HikariDataSource ds = new HikariDataSource(config);

        // Return the ConnectionPool
        return new ConnectionPool(ds);
    }
    
    private ConnectionPool(HikariDataSource ds) {
        this.dataSource = ds;
    }
    
    /**
     * Gets a connection from the pool or creates a new one if the pool is empty
     * @return The connection from the pool
     * @throws SQLException Thrown if there is an error getting or creating a new connection
     */
    public Connection getConnection() throws SQLException {
        return this.dataSource.getConnection();
    }
    
    /**
     * Shuts down the connection pool and closes all connections
     * @throws SQLException Thrown if there is an error closing the connections
     */
    public void shutdown() throws SQLException {
        this.dataSource.close();
    }
}
