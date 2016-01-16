/*
 * JSON Handler for temperature and light
 * 
 * @author1 Ruturaj Hagawane
 * @author2 FNU Shivangi
 */

package edu.rit.csci759.mobile;

import java.util.Map;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Error;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.server.MessageContext;
import com.thetransactioncompany.jsonrpc2.server.RequestHandler;

// Implements a handler for "updateSensorReadings" JSON-RPC methods that broadcasts
// a message that updated sensor readings were received from the Pi
public class TempLightHandler implements RequestHandler {
	private Context context;

	public TempLightHandler(Context context) {
		Log.d("DEBUG", "Accepted connection");
		this.context = context;
	}

	// Reports the method names of the handled requests
	public String[] handledRequests() {
		return new String[] { "notifications" };
	}

	// Processes the requests
	public JSONRPC2Response process(JSONRPC2Request req, MessageContext ctx) {

		if (req.getMethod().equals("notifications")) {
			Map<String, Object> params = req.getNamedParams();
			int temp = (int)(long) params.get("temp");
			String light = (String) params.get("light");
			String time = (String) params.get("time");
			
			Intent intent = new Intent("SmartBlindNotifications");
			
			intent.putExtra("temp", temp);
			intent.putExtra("light", light);
			intent.putExtra("time", time);
			
			Log.d("DEBUG", "Broadcasting");
			
			context.sendBroadcast(intent);
			
			return new JSONRPC2Response("Updated sensor readings successfully received!", req.getID());

		} else {
			return new JSONRPC2Response(JSONRPC2Error.METHOD_NOT_FOUND,	req.getID());
		}
	}
}