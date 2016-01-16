

package edu.rit.csci759.mobile;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import android.util.Log;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2Session;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2SessionException;

public class JSONHandler {

	public static String testJSONRequest(String server_URL_text, String method){
		// Creating a new session to a JSON-RPC 2.0 web service at a specified URL

		Log.d("Debug serverURL", server_URL_text);
		
		// The JSON-RPC 2.0 server URL
		URL serverURL = null;

		try {
			serverURL = new URL("http://"+server_URL_text);

		} catch (MalformedURLException e) {
		// handle exception...
		}

		// Create new JSON-RPC 2.0 client session
		JSONRPC2Session mySession = new JSONRPC2Session(serverURL);


		// Once the client session object is created, you can use to send a series
		// of JSON-RPC 2.0 requests and notifications to it.

		// Sending an example "getTime" request:
		// Construct new request

		int requestID = 0;
		JSONRPC2Request request = new JSONRPC2Request(method, requestID);
		//JSONRPC2Request request2 = new 
		// Send request
		JSONRPC2Response response = null;

		try {
			response = mySession.send(request);

		} catch (JSONRPC2SessionException e) {

		Log.e("error", e.getMessage().toString());
		// handle exception...
		}

		// Print response result / error
		if (response.indicatesSuccess())
			Log.d("debug", response.getResult().toString());
		else
			Log.e("error", response.getError().getMessage().toString());
		
	
		return response.getResult().toString();
	}
	
	
	/*
	 * Edited the method to pass parameters in JSON Request
	 * 
	 * @author1 Ruturaj Hagawane
	 * @author2 FNU Shivangi
	 */
	public static String testJSONRequest(String server_URL_text, String method, Map<String,Object> param){
		// Creating a new session to a JSON-RPC 2.0 web service at a specified URL

		Log.d("Debug serverURL", server_URL_text);
		
		// The JSON-RPC 2.0 server URL
		URL serverURL = null;

		try {
			serverURL = new URL("http://"+server_URL_text);

		} catch (MalformedURLException e) {
		// handle exception...
		}

		// Create new JSON-RPC 2.0 client session
		JSONRPC2Session mySession = new JSONRPC2Session(serverURL);


		// Once the client session object is created, you can use to send a series
		// of JSON-RPC 2.0 requests and notifications to it.

		// Sending an example "getTime" request:
		// Construct new request

		int requestID = 0;
		JSONRPC2Request request = new JSONRPC2Request(method, param , requestID);
		//JSONRPC2Request request2 = new 
		// Send request
		JSONRPC2Response response = null;

		try {
			response = mySession.send(request);

		} catch (JSONRPC2SessionException e) {

		Log.e("error", e.getMessage().toString());
		// handle exception...
		}

		// Print response result / error
		if (response.indicatesSuccess())
			Log.d("debug", response.getResult().toString());
		else
			Log.e("error", response.getError().getMessage().toString());
		
	
		return response.getResult().toString();
	}

	
}
