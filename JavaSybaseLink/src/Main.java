
/*
 * The idea is to recive json messages in containing
 * { "msgId" : 1, "sql" : "select * from blar"}   on standard in.
 *
 * Then on standard out send
 * { "msgId" : 1, "rows" : [{},{}]}  back on standard out where the msgId matches the sent message.
 */

public class Main implements SQLRequestListener {

	String host;
	Integer port;
	String dbname;
	String username;
	String password;
	SybaseDB db;
	StdInputReader input;

	/**
	 * Main method to start the program
	 * @param args The arguments to start the program
	 */
    public static void main(String[] args) {

		Main m;
		if (args.length != 12)
		{
			System.err.println("Expecting the arguments: host, port, dbname, username, password, minConnections, maxConnections, connectionTimeout, idleTimeout,  keepaliveTime, maxLifetime, transactionConnections");
			System.exit(1);
		}

		m = new Main(
			args[0], // host 
			Integer.parseInt(args[1]), // port
			args[2], // dbname
			args[3], // username
			args[4], // password
			Integer.parseInt(args[5]), // minConnections
			Integer.parseInt(args[6]), // maxConnections
			Integer.parseInt(args[7]), // connectionTimeout
			Integer.parseInt(args[8]), // idleTimeout
			Integer.parseInt(args[9]), // keepaliveTime
			Integer.parseInt(args[10]), // maxLifetime
			Integer.parseInt(args[11]) // transactionConnections
		);
    }

	/**
	 * MAIN constructor
	 * @param host The host to connect to
	 * @param port The port to connect to
	 * @param dbname The database name
	 * @param username The username to connect
	 * @param password The password to connect
	 * @param minConnections The minimum number of connections of the pool
	 * @param maxConnections The maximum number of connections of the pool
	 * @param connectionTimeout The timeout to wait for a connection
     * @param idleTimeout The timeout for an idle connection
     * @param keepaliveTime The time to keep a connection alive
     * @param maxLifetime The maximum time a connection can be alive
	 * @param transactionConnections The number of connections for transactions pool
	 */
	public Main(String host, Integer port, String dbname, String username, String password, Integer minConnections, Integer maxConnections, Integer connectionTimeout, Integer idleTimeout, Integer keepaliveTime, Integer maxLifetime, Integer transactionConnections) {
		this.host = host;
		this.port = port;
		this.dbname = dbname;
		this.username = username;
		this.password = password;

		input = new StdInputReader();
		input.addListener(this);
                
		db = new SybaseDB(host, port, dbname, username, password, minConnections, maxConnections, connectionTimeout, idleTimeout, keepaliveTime, maxLifetime, transactionConnections);
		if (!db.connect())
			System.exit(1);

		// send the connected message.
		System.out.println("connected");

		// blocking call don't do anything under here.
		input.startReadLoop();
	}

	/**
	 * Called when a message is received from the input, this is where the SQL is executed on the db class.
	 */
	public void sqlRequest(SQLRequest request)
	{
		db.execSQL(request);
		//System.out.println(result);
	}


}