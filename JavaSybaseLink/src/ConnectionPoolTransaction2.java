import com.sybase.jdbc4.jdbc.SybDataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

/**
 *
 * @author DarkJ24
 */
public class ConnectionPoolTransaction2 {
    
    private final HikariDataSource dataSource;
    private HashMap<Integer, Connection> transactions = new HashMap<Integer, Connection>();
    
    /**
     * Creates a new ConnectionPoolTransaction2 with the given parameters
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
    public static ConnectionPoolTransaction2 create(
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

        // Return the ConnectionPoolTransaction2
        return new ConnectionPoolTransaction2(ds);
    }
    
    private ConnectionPoolTransaction2(HikariDataSource ds) {
        this.dataSource = ds;
    }

    /**
     * Gets a connection from the pool or creates a new one if the pool is empty
     * @param transactionId The id of the transaction to associate the connection to
     * @return The connection from the pool
     * @throws SQLException Thrown if there is an error getting or creating a new connection
     */
    public Connection getConnection(int transactionId) throws SQLException {
        Connection connection = this.transactions.get(transactionId);
        if (connection == null) {
            connection = this.dataSource.getConnection();
            this.transactions.put(transactionId, connection);
        }
        return connection;
    }
    
        /**
     * Releases the connection from the transaction and adds it back to the pool
     * @param transactionId The transaction id to release the connection for
     * @throws SQLException Thrown if there is an error releasing the connection
     */
    public void releaseConnection(int transactionId) throws SQLException {
        Connection connection = this.transactions.get(transactionId);
        this.transactions.remove(transactionId);
        connection.close();
    }
    
    /**
     * Shuts down the connection pool and closes all connections
     * @throws SQLException Thrown if there is an error closing the connections
     */
    public void shutdown() throws SQLException {
        this.dataSource.close();
    }
}
