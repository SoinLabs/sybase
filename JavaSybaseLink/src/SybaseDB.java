import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.Handler;

/**
 *
 * @author rod
 * modified by DarkJ24
 */
public class SybaseDB {

	public static final int TYPE_TIME_STAMP = 93;
	public static final int TYPE_DATE = 91;
	public static final int TYPE_TIME = 92;
	public static final int NUMBER_OF_THREADS = 10;

	private String host;
	private Integer port;
	private String dbname;
	private String username;
	private String password;
        private ConnectionPool pool;
	private ConnectionPoolTransaction transactionPool;
//        private ConnectionPoolTransaction2 transactionPool;
	private int minConnections;
	private int maxConnections;
	private int connectionTimeout;
	private int idleTimeout;
	private int keepaliveTime;
	private int maxLifetime;
	private int transactionConnections;
	DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S'Z'");
	ExecutorService executor;

	/**
	 * Creates a new SybaseDB object with the given parameters
	 * @param host The host of the database
	 * @param port The port of the database
	 * @param dbname The name of the database
	 * @param username The username to connect to the database
	 * @param password The password to connect to the database
	 * @param minConnections The minimum number of connections in the pool
	 * @param maxConnections The maximum number of connections in the pool
	 * @param connectionTimeout The timeout to wait for a connection
	 * @param idleTimeout The timeout for an idle connection
	 * @param keepaliveTime The time to keep a connection alive
	 * @param maxLifetime The maximum time a connection can be alive
	 * @param transactionConnections The maximum number of transaction connections
	 */        
	public SybaseDB(String host, Integer port, String dbname, String username, String password, int minConnections, int maxConnections, int connectionTimeout, int idleTimeout, int keepaliveTime, int maxLifetime, int transactionConnections)
	{
		this.host = host;
		this.port = port;
		this.dbname = dbname;
		this.username = username;
		this.password = password;
		this.minConnections = minConnections;
		this.maxConnections = maxConnections;
                this.executor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
		this.connectionTimeout = connectionTimeout;
		this.idleTimeout = idleTimeout;
		this.keepaliveTime = keepaliveTime;
		this.maxLifetime = maxLifetime;
		this.transactionConnections = transactionConnections;
		df.setTimeZone(TimeZone.getTimeZone("UTC"));
	}

	/**
	 * Connects to the database
	 * @return true if the connection was successful, false otherwise
	 */
	public boolean connect()
	{
            try {

                Logger rootLogger = LogManager.getLogManager().getLogger("");
                rootLogger.setLevel(Level.SEVERE);
                for (Handler h : rootLogger.getHandlers()) {
                    h.setLevel(Level.SEVERE);
                }
                ConnectionPool pool = ConnectionPool.create(this.host, this.port, this.dbname, this.username, this.password, this.minConnections, this.maxConnections, this.connectionTimeout, this.idleTimeout, this.keepaliveTime, this.maxLifetime, true);
                ConnectionPoolTransaction transactionPool = ConnectionPoolTransaction.create(this.host, this.port, this.dbname, this.username, this.password, transactionConnections);
//                ConnectionPoolTransaction2 transactionPool = ConnectionPoolTransaction2.create(this.host, this.port, this.dbname, this.username, this.password, this.minConnections, 10, this.connectionTimeout, this.idleTimeout, this.keepaliveTime, this.maxLifetime, false);
                this.pool = pool;
                this.transactionPool = transactionPool;

                //Atach shutdown hook to close the connection
                Runtime.getRuntime().addShutdownHook(new Thread() {
                        @Override
                        public void run() {
                                try {
                                        pool.shutdown();
                                } catch (Exception ex) {
                                        System.err.println(ex);
                                        System.err.println(ex.getMessage());
                                }
                                try {
                                        transactionPool.shutdown();
                                } catch (Exception ex) {
                                        System.err.println(ex);
                                        System.err.println(ex.getMessage());
                                }
                        }
                });

                return true;

            } catch (Exception ex) {
                System.err.println(ex);
                System.err.println(ex.getMessage());
                return false;
            }
	}

	/**
	 * Disconnects from the database
	 */
	public void disconnect()
	{
		try {
			this.pool.shutdown();
		} catch (Exception ex) {
			System.err.println(ex);
			System.err.println(ex.getMessage());
		}
	}

	/**
	 * Executes the given SQL request
	 * @param request The SQL request to execute
	 */
	public void execSQL(SQLRequest request)
	{
		// Create a new thread to execute the SQL request
            if (request.transId != -1) {
				// If the request is a transaction request, execute it in the transaction pool
                Future f = executor.submit(new ExecSQLTransactionCallable(this.transactionPool, df, request));
            } else {
				// If the request is not a transaction request, execute it in the normal pool 
                Future f = executor.submit(new ExecSQLCallable(this.pool, df, request));
            }
	}

}
