
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

/**
 *
 * @author rod
 * modified by DarkJ24
 */
public class StdInputReader {

	private List<SQLRequestListener> listeners = new ArrayList<SQLRequestListener>();
	private BufferedReader inputBuffer = new BufferedReader(new InputStreamReader(System.in));

	/**
	 * Constructor for StdInputReader
	 */
	public StdInputReader() {
	}

	/**
	 * Start the read loop from stdin and send the event to the listeners
	 */
	public void startReadLoop()
	{
		String nextLine;
		try {
			while ((nextLine = inputBuffer.readLine()) != null) {
				nextLine = nextLine.replaceAll("\\n", "\n");
				sendEvent(nextLine);
			}
		} catch (IOException ex) {
			System.err.println("IO exception: " + ex);
		}
	}

	/**
	 * Send the event to the listeners
	 * @param sqlRequest The SQLRequest in json format received from stdin
	 */
	private void sendEvent(String sqlRequest)
	{
		long startTime = System.currentTimeMillis();
		SQLRequest request;
		try {
			JSONObject val = (JSONObject) JSONValue.parse(sqlRequest);
			request = new SQLRequest();
			request.msgId = (Integer)val.get("msgId"); // Indicates the message id
			request.transId = (Integer)val.get("transId"); // Indicates the transaction id
			request.finishTrans = (boolean)val.get("finishTrans"); // Indicates if the transaction is finished (commit or rollback)
			request.sql = (String)val.get("sql"); // The SQL to execute
			request.javaStartTime = startTime; // The start time of the request
		} catch (Exception e)
		{
			request = null;
		}
		if (request == null || request.sql == null)
		{
			System.err.println("Error parsing json not a valid SQLRequest object. " + sqlRequest);
			return;
		}

		for (SQLRequestListener l : listeners)
			l.sqlRequest(request);
	}

	/**
	 * Add a listener to the list of listeners
	 * @param l The listener to add
	 * @return true if the listener was added, false otherwise
	 */
	public boolean addListener(SQLRequestListener l)
	{
		if (listeners.contains(l))
			return false;

		listeners.add(l);
		return true;
	}

	/**
	 * Remove a listener from the list of listeners
	 * @param l The listener to remove
	 * @return true if the listener was removed, false otherwise
	 */
	public boolean removeListener(SQLRequestListener l)
	{
		return listeners.remove(l);
	}
}
