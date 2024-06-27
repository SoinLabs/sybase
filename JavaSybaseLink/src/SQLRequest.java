/**
 * This class is used to store the information of a SQL request.
 * @author rod
 * modified by DarkJ24
 */
public class SQLRequest {
	int msgId; // The message id of the request
    int transId; // The transaction id of the request
	boolean finishTrans; // Indicates if the transaction needs to be finished
	String sql; // The sql statement to be executed
	long sentTime; // The time the request was sent
	long javaStartTime; // The time the request was received
}
