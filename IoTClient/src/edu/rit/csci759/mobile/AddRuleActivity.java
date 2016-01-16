/*
 * Activity to initiate addition of new rule
 * 
 * @author1 Ruturaj Hagawane
 * @author2 FNU Shivangi
 */

package edu.rit.csci759.mobile;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class AddRuleActivity extends Activity {

	Spinner choiceSpinner1Value;
	Spinner choiceSpinner2Value;
	Spinner blindSpinner;
	Spinner relSpinner;
	Spinner choiceSpinner1 ;
	Spinner choiceSpinner2 ;
	Button add;
	JSONArray rule_array;
	String all_rules;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_rule);
		
		Intent intent = getIntent();
		
		all_rules = intent.getStringExtra("Rule");
		
		try {
			rule_array = new JSONArray(all_rules);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		
		add = (Button) findViewById(R.id.add);
		
		choiceSpinner1Value = (Spinner) findViewById(R.id.choiceSpinner1Value);

		choiceSpinner2Value = (Spinner) findViewById(R.id.choiceSpinner2Value);

		//set adapter for spinner
		blindSpinner = (Spinner) findViewById(R.id.blindSpinner);
		ArrayAdapter<CharSequence> blindAdapter = ArrayAdapter.createFromResource(this,R.array.blind_position,android.R.layout.simple_list_item_1);
		blindAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		blindSpinner.setAdapter(blindAdapter);

		relSpinner = (Spinner) findViewById(R.id.relSpinner);
		ArrayAdapter<CharSequence> relAdapter = ArrayAdapter.createFromResource(this,R.array.relation,android.R.layout.simple_list_item_1);
		blindAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		relSpinner.setAdapter(relAdapter);

		choiceSpinner1 = (Spinner) findViewById(R.id.choiceSpinner1);
		ArrayAdapter<CharSequence> choiceAdapter1 = ArrayAdapter.createFromResource(this,R.array.choice,android.R.layout.simple_list_item_1);
		choiceAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		choiceSpinner1.setAdapter(choiceAdapter1);

		choiceSpinner2 = (Spinner) findViewById(R.id.choiceSpinner2);
	
		//on relation choice click 
		relSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				if(parent.getItemAtPosition(position).toString().equals("")){
					ArrayAdapter<CharSequence> choiceAdapter2 = ArrayAdapter.createFromResource(AddRuleActivity.this,R.array.blank,android.R.layout.simple_list_item_1);
					choiceAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					choiceSpinner2.setAdapter(choiceAdapter2);					
				}
				else{
					ArrayAdapter<CharSequence> choiceAdapter2 = ArrayAdapter.createFromResource(AddRuleActivity.this,R.array.choice,android.R.layout.simple_list_item_1);
					choiceAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					choiceSpinner2.setAdapter(choiceAdapter2);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {}});
		
		//on choice 1 click (temperature or ambient)
		choiceSpinner1.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				//Toast.makeText(getApplicationContext(), parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
			if(parent.getItemAtPosition(position).toString().equals("TEMPERATURE")){
					ArrayAdapter<CharSequence> tempAdapter = ArrayAdapter.createFromResource(AddRuleActivity.this,R.array.temperature,android.R.layout.simple_list_item_1);
					tempAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					choiceSpinner1Value.setAdapter(tempAdapter);
				}
				else 
				{
					ArrayAdapter<CharSequence> lightAdapter = ArrayAdapter.createFromResource(AddRuleActivity.this,R.array.ambience,android.R.layout.simple_list_item_1);
					lightAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					choiceSpinner1Value.setAdapter(lightAdapter);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {}});
		
		//on choice 2 click (temperature or ambient)
		choiceSpinner2.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				//Toast.makeText(getApplicationContext(), parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
				if(parent.getItemAtPosition(position).toString().equals("TEMPERATURE")){
					ArrayAdapter<CharSequence> tempAdapter = ArrayAdapter.createFromResource(AddRuleActivity.this,R.array.temperature,android.R.layout.simple_list_item_1);
					tempAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					choiceSpinner2Value.setAdapter(tempAdapter);
				}
				else if(parent.getItemAtPosition(position).toString().equals("AMBIENT"))
				{
					ArrayAdapter<CharSequence> lightAdapter = ArrayAdapter.createFromResource(AddRuleActivity.this,R.array.ambience,android.R.layout.simple_list_item_1);
					lightAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					choiceSpinner2Value.setAdapter(lightAdapter);
				}
				else{
					ArrayAdapter<CharSequence> lightAdapter = ArrayAdapter.createFromResource(AddRuleActivity.this,R.array.blank,android.R.layout.simple_list_item_1);
					lightAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					choiceSpinner2Value.setAdapter(lightAdapter);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {}});
		
		//on click
		//open activity to add new rule
		add.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				try {
					JSONObject jo = new JSONObject();
					jo.put("term1variable", choiceSpinner1.getSelectedItem());
					jo.put("term1value", choiceSpinner1Value.getSelectedItem());
					
					if(0 != relSpinner.getSelectedItemPosition() )
					{
						jo.put("relation", relSpinner.getSelectedItem());
						jo.put("term2variable", choiceSpinner2.getSelectedItem());
						jo.put("term2value", choiceSpinner2Value.getSelectedItem());
					}
					else
					{
						jo.put("relation", "");
						jo.put("term2variable", "");
						jo.put("term2value", "");
					}
					jo.put("result", blindSpinner.getSelectedItem());
					
					rule_array.put(jo);
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				
				all_rules = rule_array.toString();
				
				Log.d("updated array",rule_array.toString());
				//push updates in async task
				new updateRules().execute();
				
				Intent data = new Intent();
				data.setData(Uri.parse(rule_array.toString()));
				setResult(RESULT_OK,data);
				finish();
				
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.update_rule, menu);
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
	
	//Async task rule update
	class updateRules extends AsyncTask<Void, String, String> {
		String response_txt;

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected String doInBackground(Void... params) {
			String serverURL_text = "10.10.10.106:8080";//et_server_url.getText().toString();
			String request_method = "updaterules";//et_requst_method.getText().toString();
			Map<String,Object> temp = new HashMap<String, Object>();
			JSONObject update = new JSONObject();
			
			try {
				update.put("rules", rule_array);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			temp.put("update",update.toString());
			
			response_txt = JSONHandler.testJSONRequest(serverURL_text, request_method, temp);
			
			return response_txt;
		}

		protected void onProgressUpdate(Integer... progress) {
			//setProgressPercent(progress[0]);
		}

		protected void onPostExecute(String result) {

			Log.d("DEBUG","RULES UPDATED");

		}

	}
}
