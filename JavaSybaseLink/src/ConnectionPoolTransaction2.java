
import com.sybase.jdbc4.jdbc.SybDataSource;
import com.sybase.jdbc4.jdbc.SybDriver;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author DarkJ24
 */
public class ConnectionPoolTransaction2 {
    
    private HashMap<Integer, Connection> transactions = new HashMap<Integer, Connection>();
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
     * @param acquireTimeout The timeout to acquire a connection
     * @param idleTimeout The timeout for an idle connection
     * @param autoCommit Indicates if autoCommit is on or not
     * @return
     * @throws SQLException
     */
    public static ConnectionPoolTransaction2 create(
      String host, int port, String dbName, String username, String password,
      int minConnections, int maxConnections,
      int acquireTimeout, int idleTimeout, boolean autoCommit
      ) throws SQLException {
 
//        Properties props = new Properties();
//        props.setProperty("dataSourceClassName", "com.sybase.jdbc4.jdbc.SybDataSource");
//        props.setProperty("dataSource.serverName", host);
//        props.setProperty("dataSource.port", Integer.toString(port));
//        props.setProperty("dataSource.user", username);
//        props.setProperty("dataSource.password", password);
//        props.setProperty("dataSource.databaseName", dbName);
//        props.setProperty("idleTimeout", Integer.toString(idleTimeout));
//        props.setProperty("connectionTimeout", "300000");
//        props.setProperty("maximumPoolSize", Integer.toString(maxConnections));
//        props.setProperty("minimumIdle", Integer.toString(minConnections));
//        props.setProperty("autoCommit", Boolean.toString(autoCommit));
        

//        HikariConfig config = new HikariConfig(props);
        HikariConfig config = new HikariConfig();
        config.setDataSourceClassName("com.sybase.jdbc4.jdbc.SybDataSource");
        config.addDataSourceProperty("serverName", host);
        config.addDataSourceProperty("portNumber", port);
        config.addDataSourceProperty("user", username);
        config.addDataSourceProperty("password", password);
//        config.addDataSourceProperty("url", "jdbc:sybase:Tds:"+host+":"+port+"/"+dbName);
        config.addDataSourceProperty("databaseName", dbName);
        config.setIdleTimeout(idleTimeout);
        config.setConnectionTimeout(300000);
        config.setMaximumPoolSize(maxConnections);
        config.setMinimumIdle(minConnections);
        config.setAutoCommit(autoCommit);
//        config.setJdbcUrl("jdbc:sybase:Tds:"+host+":"+Integer.toString(port)+"/"+dbName);
//        config.setUsername(username);
//        config.setPassword(password);
        HikariDataSource ds = new HikariDataSource(config);

        // Return the ConnectionPool
        return new ConnectionPoolTransaction2(ds);
    }
    
    private ConnectionPoolTransaction2(HikariDataSource ds) {
        this.dataSource = ds;
    }
    
    /**
     * Gets a connection from the pool or creates a new one if the pool is empty
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
     * Shuts down the connection pool and closes all connections
     * @throws SQLException Thrown if there is an error closing the connections
     */
    public void shutdown() throws SQLException {
        this.dataSource.close();
    }
    
    public void releaseConnection(int transactionId) {
        this.transactions.remove(transactionId);
    }
}
