
import com.sybase.jdbc4.jdbc.SybDataSource;
import com.sybase.jdbc4.jdbc.SybDriver;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Properties;

/**
 *
 * @author DarkJ24
 */
public class ConnectionPoolTransaction3 implements ConnectionPoolTransaction {
    
    private String url;
    Properties props;
    private int transactionConnections;
    private HashMap<Integer, Connection> transactions = new HashMap<Integer, Connection>();
    
    /**
     * Creates a new ConnectionPoolTransaction with the given parameters
     * @param host The host of the database
     * @param port The port of the database
     * @param dbName The name of the database
     * @param username The username to connect to the database
     * @param password The password to connect to the database
     * @param transactionConnections The number of connections for transactions in the pool
     * @return
     * @throws SQLException
     */
    public static ConnectionPoolTransaction3 create(
      String host, int port, String dbName, String username, String password, int transactionConnections
      ) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
 
        // Register the driver
        String url = "jdbc:sybase:Tds:" + host + ":" + port + "/" + dbName;
        SybDriver sybDriver = (SybDriver) Class.forName("com.sybase.jdbc4.jdbc.SybDriver").newInstance();
        DriverManager.registerDriver((Driver) sybDriver);

        // Set the properties
        Properties props = new Properties();
        props.put("user", username);
		props.put("password", password);
                props.put("autoCommit", false);

        // Return the ConnectionPool
        return new ConnectionPoolTransaction3(url, props, transactionConnections);
    }
    
    /**
     * Constructor for the ConnectionPoolTransaction
     * @param url The URL of the database
     * @param props The properties of the connection (username and password)
     * @param transactionConnections The number of connections for transactions in the pool
     */
    private ConnectionPoolTransaction3(String url, Properties props,
      int transactionConnections) {
        this.url = url;
        this.props = props;
        this.transactionConnections = transactionConnections;
    }

    /**
     * Creates a new connection with the given parameters
     * @param url The URL of the database
     * @param props The properties of the connection (username and password)
     * @return The new connection
     * @throws SQLException Thrown if there is an error creating the connection
     */
    private static Connection createConnection(
        String url, Properties props) 
        throws SQLException {
          Connection connection = DriverManager.getConnection(url, props);
          connection.setAutoCommit(false); // Defaults to autocommit false for managing transactions
          return connection;
    }
    
    /**
     * Gets a connection from the pool or creates a new one if the pool is empty
     * @param transactionId The transaction id to get the connection for
     * @return The connection from the pool
     * @throws SQLException Thrown if there is an error getting or creating a new connection
     */
    public Connection getConnection(int transactionId) throws SQLException {

        Connection connection = this.transactions.get(transactionId);
        if (connection == null) {
            connection = createConnection(this.url, this.props);
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
        for (Connection connection : transactions.values()) {
            connection.close();
        }
    }
}
