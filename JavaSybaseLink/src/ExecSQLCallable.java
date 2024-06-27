
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.Date;
import java.util.concurrent.Callable;
import java.text.DateFormat;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

/**
 * @author rod
 * modified by DarkJ24
 */
public class ExecSQLCallable implements Callable<String> {

	private ConnectionPool pool;
	private DateFormat df;
	private SQLRequest request;

	/**
	 * Constructor for ExecSQLCallable 
	 * @param pool The connection pool to use for the sql request
	 * @param df The date format to use for the sql request
	 * @param request The SQLRequest to execute
	 */
	public ExecSQLCallable(ConnectionPool pool, DateFormat df, SQLRequest request) {
		this.pool = pool;
		this.df = df;
		this.request = request;
	}

	/**
	 * Call method to execute the transaction sql request
	 * @return The result of the transaction sql request
	 * @throws Exception 
	 */
	public String call() throws Exception {
		String result = execSQLJsonSimple();
		System.out.println(result);
		return result;
	}

	/**
	 * Execute the transaction sql request
	 * @return The result of the transaction sql request
	 */
	public String execSQLJsonSimple()
	{
		JSONObject response = new JSONObject();
		response.put("msgId", request.msgId);
		JSONArray rss = new JSONArray();
		response.put("result", rss);
		Statement stmt = null;
		ResultSet rs = null;
		Connection conn = null;

		try {
			conn = pool.getConnection();
			stmt = conn.createStatement();
			boolean isRS = stmt.execute(request.sql);
			while (isRS || (stmt.getUpdateCount() != -1))
			{
				if (!isRS)
				{
					isRS = stmt.getMoreResults();
					continue;
				}
				rs = stmt.getResultSet();
				ResultSetMetaData meta = rs.getMetaData();

				// get column names;
				int colCount = meta.getColumnCount();
				String[] columns = new String[colCount+1];
				for (int c = 1; c<colCount+1; c++)
					columns[c] = meta.getColumnLabel(c);

				JSONArray jsonRS = new JSONArray();
				rss.add(jsonRS);
				while (rs.next())
				{
					JSONObject row = new JSONObject();
					jsonRS.add(row);
					for (int c = 1; c< colCount+1; c++)
					{
						Object val = rs.getObject(c);
						if (val == null) continue;
						
						int dataType = meta.getColumnType(c);
						switch (dataType)
						{
							case SybaseDB.TYPE_TIME_STAMP:
							case SybaseDB.TYPE_DATE:
								String my8601formattedDate = df.format(new Date(rs.getTimestamp(c).getTime()));
								row.put(columns[c], my8601formattedDate);
								break;
							case SybaseDB.TYPE_TIME:
								String timeFromRS = rs.getTime(c).toString();
								String my8601formattedTime = "1970-01-01T" + timeFromRS + ".000Z";
								row.put(columns[c], my8601formattedTime);
								break;
							default:
								row.put(columns[c], rs.getObject(c));
						}
					}
				}
				rs.close(); // Close the result set
				isRS = stmt.getMoreResults();
			}
			stmt.close(); // Close the statement
            conn.close(); // Close the connection
		} catch (Exception ex) {
			response.put("error", ex.getMessage());
			// Close the connection in case of an error
            try {
				if (conn != null) conn.close();
			} catch (Exception ex2) {
			    //Ignore
		            //response.put("error", ex.getMessage());
			}
		} finally {
			// Close the result set if not already closed
			try {
				if (rs != null) rs.close();
			} catch (Exception ex) {
				//Ignore
				//response.put("error", ex.getMessage());
			}
			// Close the statement if not already closed
			try {
				if (stmt != null) stmt.close();
			} catch (Exception ex) {
				//Ignore
				//response.put("error", ex.getMessage());
			}
		}

		response.put("javaStartTime", request.javaStartTime);
		long beforeParse = System.currentTimeMillis();
		response.put("javaEndTime", beforeParse);
		String jsonResult = response.toJSONString();
		return jsonResult;
	}

	/**
	 * Print the given string to stdout
	 * @param s The string to print
	 */
	public void safePrintln(String s) {
		synchronized (System.out) {
			System.out.println(s);
		}
	}
}
