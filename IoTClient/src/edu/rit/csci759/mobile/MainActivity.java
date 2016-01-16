
/*
 * Main Actitvity (Home Page) of our application
 * 
 * @author1 Ruturaj Hagawane
 * @author2 FNU Shivangi
 */

package edu.rit.csci759.mobile;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;

import org.json.*;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import org.apache.http.conn.util.InetAddressUtils;

public class MainActivity extends Activity {

	String FileName = "SmartBlindNotifications";
	String RPCServerPort = "8081";

	Button rule_button;
	Button notify_button;
	TextView time,temp,light;
	ProgressBar pb;
	String rules;
	ArrayList<String> notifications;
	NotificationReceiver notificationReceiver;
	IntentFilter intentFilter;
	double temperature = 0;
	final Context context = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		notificationReceiver = new NotificationReceiver();
		
		rule_button = (Button) findViewById(R.id.buttonRule);
		notify_button = (Button)findViewById(R.id.notify_button);
		time = (TextView) findViewById(R.id.time);
		temp = (TextView) findViewById(R.id.temp);
		light = (TextView) findViewById(R.id.ambience);
		pb = (ProgressBar) findViewById(R.id.progressBar);
		notifications = new ArrayList<String>();
		
		intentFilter = new IntentFilter("SmartBlindNotifications");

		Intent intent = new Intent(MainActivity.this,NotificationService.class);
		intent.putExtra("RPCServerPort", RPCServerPort);
		
		Log.d("DEBUG", "Starting server");
		
		// 
		registerReceiver(notificationReceiver, intentFilter);
		
		startService(intent);
		
		new SendJSONRequest().execute();

		//on click, open rule activity		
		rule_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {		
				Intent intent = new Intent(MainActivity.this,RulesActivity.class);
				intent.putExtra("rules", rules);
				startActivity(intent);			
			}
		});

		// on click, open notification activity
		notify_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent= new Intent(MainActivity.this,NotificationActivity.class);
				intent.putStringArrayListExtra("notifications", notifications);
				startActivity(intent);
			}
		});
		
		// on click, open dialog box 
		// display temperature both in celcius and fahrenheit
		temp.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.custom);
                dialog.setTitle("Temperature Dialog");

                TextView Celcius = (TextView) dialog.findViewById(R.id.tempC);
                Celcius.setText(temperature+"");
                
                TextView Fahrenheit = (TextView) dialog.findViewById(R.id.tempF);
                Fahrenheit.setText(((9.0/5.0)*temperature + 32)+"");

                

                ImageView imageC = (ImageView) dialog.findViewById(R.id.imageC);
                imageC.setImageResource(R.drawable.celcius);

                ImageView imageF = (ImageView) dialog.findViewById(R.id.imageF);
                imageF.setImageResource(R.drawable.fahrenheit);

                Button dialogButton = (Button) dialog.findViewById(R.id.OK);

                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
			}
		});
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(notificationReceiver);
		super.onDestroy();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	class SendJSONRequest extends AsyncTask<Void, String, String> {
		String response_txt;

		/**
		 * Get IP address from first non-localhost interface
		 * @param ipv4  true=return ipv4, false=return ipv6
		 * @return  address or empty string
		 */
		@SuppressLint("DefaultLocale")
		public String getIPAddress() 
		{
			try {
				List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
				for (NetworkInterface intf : interfaces) {
					List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
					for (InetAddress addr : addrs) {
						if (!addr.isLoopbackAddress()) {
							String sAddr = addr.getHostAddress().toUpperCase();
							if (true == InetAddressUtils.isIPv4Address(sAddr))
							{ 
								return sAddr;
							}
						}
					}
				}
			} catch (Exception ex) { } // for now eat exceptions
			return "";
		}

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected String doInBackground(Void... params) {
			String serverURL_text = "10.10.10.106:8080";//et_server_url.getText().toString();
			String request_method = "getinfo";//et_requst_method.getText().toString();
			Map<String,Object> temp = new HashMap<String, Object>();

			temp.put("ip", getIPAddress());
			temp.put("port", RPCServerPort);

			response_txt = JSONHandler.testJSONRequest(serverURL_text, request_method,temp);

			return response_txt;
		}

		protected void onProgressUpdate(Integer... progress) {
			//setProgressPercent(progress[0]);
		}

		protected void onPostExecute(String result) {

			try {
				JSONObject jo = new JSONObject(result);
				pb.setProgress(jo.getInt("temp"));
				temperature = Double.parseDouble(jo.get("temp").toString());
				temp.setText(jo.get("temp").toString()+"C");
				time.setText(jo.get("time").toString());
				light.setText(jo.get("light").toString());

				rules = jo.get("rules").toString();

			} catch (JSONException e) {
				e.printStackTrace();
			}

		}

	}

	//in broadcast listener add message to notification array list
	public class NotificationReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			
			Log.d("DEBUG", "broadcast received");
			
			Bundle data = intent.getExtras();

			pb.setProgress(data.getInt("temp"));
			temp.setText(data.get("temp").toString()+"C");
			time.setText(data.get("time").toString());
			light.setText(data.get("light").toString());
			notifications.add(data.get("time").toString() + ", " + data.getInt("temp"));
		}
	}
}
