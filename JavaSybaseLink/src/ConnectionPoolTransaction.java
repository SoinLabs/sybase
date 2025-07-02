
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
public interface ConnectionPoolTransaction {
    
    /**
     * Gets a connection from the pool or creates a new one if the pool is empty
     * @param transactionId The transaction id to get the connection for
     * @return The connection from the pool
     * @throws SQLException Thrown if there is an error getting or creating a new connection
     */
    public Connection getConnection(int transactionId) throws SQLException;
    
    /**
     * Releases the connection from the transaction and adds it back to the pool
     * @param transactionId The transaction id to release the connection for
     * @throws SQLException Thrown if there is an error releasing the connection
     */
    public void releaseConnection(int transactionId) throws SQLException;
    
    /**
     * Shuts down the connection pool and closes all connections
     * @throws SQLException Thrown if there is an error closing the connections
     */
    public void shutdown() throws SQLException;
}
