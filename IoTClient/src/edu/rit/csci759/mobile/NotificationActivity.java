
/*
 * This activity display all notification on temperature change
 * (if temperature >= 2 degree)
 * 
 * @author1 Ruturaj Hagawane
 * @author2 FNU Shivangi
 */

package edu.rit.csci759.mobile;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class NotificationActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notification);
		
		//creating list and printing notifications in them
		
		ListView notifications_list;
		Intent intent = getIntent();
		ArrayList<String> notifications = intent.getStringArrayListExtra("notifications");
		
		notifications_list = (ListView)findViewById(R.id.notification);
		
		ArrayAdapter<String> l = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, notifications);
		notifications_list.setAdapter(l);
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.notification, menu);
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
}
