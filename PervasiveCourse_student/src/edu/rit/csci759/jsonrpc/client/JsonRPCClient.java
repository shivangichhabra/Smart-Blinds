/*
 * Client
 * 
 * @author1 Ruturaj Hagawane
 * @author2 FNU Shivangi
 */

package edu.rit.csci759.jsonrpc.client;

//The Client sessions package
import java.net.MalformedURLException;
//For creating URLs
import java.net.URL;
import java.util.Map;

//The Base package for representing JSON-RPC 2.0 messages
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2Session;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2SessionException;
//The JSON Smart package for JSON encoding/decoding (optional)



public class JsonRPCClient {
	
	public static String ip;
	public static String port;
	public static boolean set = false;
	
	public void set(String ip, String port) {
		JsonRPCClient.ip = ip;
		JsonRPCClient.port = port;
		set = true;
		System.out.println("client connection set");
	}

	public boolean is_set()
	{
		return set;
	}
	
	
	//send notification for changes
	public static void send_notifications(Map<String,Object> data) 
	{
		// Creating a new session to a JSON-RPC 2.0 web service at a specified URL

		// The JSON-RPC 2.0 server URL
		URL serverURL = null;
		String method = null;
		int requestID = 0;
		JSONRPC2Request request = null;
		JSONRPC2Response response = null;

		try {
			serverURL = new URL("http://" + ip + ":" + port);

		} catch (MalformedURLException e) {
			// handle exception...
		}

		// Create new JSON-RPC 2.0 client session
		JSONRPC2Session mySession = new JSONRPC2Session(serverURL);


		// Once the client session object is created, you can use to send a series
		// of JSON-RPC 2.0 requests and notifications to it.

		method = "notifications";
		requestID = 1;
		request = new JSONRPC2Request(method,data, requestID);

		// Send request
		response = null;

		try {
			response = mySession.send(request);

		} catch (JSONRPC2SessionException e) {

			System.err.println(e.getMessage());
			// handle exception...
		}

		// Print response result / error
		if (response.indicatesSuccess())
			System.out.println("from phone " + response.getResult());
		else
			System.out.println("from phone " + response.getError().getMessage());
	}
}