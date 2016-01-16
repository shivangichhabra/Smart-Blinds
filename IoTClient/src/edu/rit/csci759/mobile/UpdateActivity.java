/*
 * Activity for rule update
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

import android.annotation.SuppressLint;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

public class UpdateActivity extends Activity {

	Spinner choiceSpinner1Value;
	Spinner choiceSpinner2Value;
	Spinner blindSpinner;
	Spinner relSpinner;
	Spinner choiceSpinner1 ;
	Spinner choiceSpinner2 ;
	int term1variableIndex = 0, term1valueIndex = 0, term2variableIndex = 0, term2valueIndex = 0, relationIndex = 0, resultIndex = 0;
	Button update;
	Button delete;
	JSONArray rule_array;
	JSONObject jo = null;
	int current_rule;
	String all_rules;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update);

		Intent intent = getIntent();

		all_rules = intent.getStringExtra("Rule");
		current_rule = intent.getIntExtra("Number", 1);	

		try {
			rule_array = new JSONArray(all_rules);
			jo = rule_array.getJSONObject(current_rule);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}

		String choices[] = {"TEMPERATURE","AMBIENT"};
		String tempValues[] = {"FREEZING","COLD","COMFORT","WARM","HOT"};
		String ambienceValues[] = {"DARK","DIM","BRIGHT"};
		String result[] = {"OPEN","HALF","CLOSE"};
		String relation[] = {"BLANK","AND","OR"};

		choiceSpinner1Value = (Spinner) findViewById(R.id.choiceSpinner1Value);
		choiceSpinner2Value = (Spinner) findViewById(R.id.choiceSpinner2Value);
		blindSpinner = (Spinner) findViewById(R.id.blindSpinner);
		relSpinner = (Spinner) findViewById(R.id.relSpinner);
		choiceSpinner1 = (Spinner) findViewById(R.id.choiceSpinner1);
		choiceSpinner2 = (Spinner) findViewById(R.id.choiceSpinner2);
		update = (Button)findViewById(R.id.update);
		delete = (Button)findViewById(R.id.delete);

		try {

			if(false == jo.getString("relation").equals(""))
			{
				relationIndex = getIndex(jo.getString("relation"), relation);
			}

			relationIndex = getIndex(jo.getString("relation"), relation);
			
			term1variableIndex = getIndex(jo.getString("term1variable"),choices);
			if(true == "TEMPERATURE".equals(choices[term1variableIndex]))
			{
				term1valueIndex = getIndex(jo.getString("term1value"), tempValues);
			}
			else
			{
				term1valueIndex = getIndex(jo.getString("term1value"), ambienceValues);
			}

			if(false == "BLANK".equals(relation[relationIndex]))
			{
				term2variableIndex = getIndex(jo.getString("term2variable"),choices);
				if(true == "TEMPERATURE".equals(choices[term2variableIndex]))
				{
					term2valueIndex = getIndex(jo.getString("term2value"), tempValues);
				}
				else
				{
					term2valueIndex = getIndex(jo.getString("term2value"), ambienceValues);
				}
			}

			resultIndex = getIndex(jo.getString("result"), result);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}

		//set spinner adapter
		
		ArrayAdapter<CharSequence> blindAdapter = ArrayAdapter.createFromResource(this,R.array.blind_position,android.R.layout.simple_list_item_1);
		blindAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		blindSpinner.setAdapter(blindAdapter);
		blindSpinner.setSelection(resultIndex);


		ArrayAdapter<CharSequence> relAdapter = ArrayAdapter.createFromResource(this,R.array.relation,android.R.layout.simple_list_item_1);
		blindAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		relSpinner.setAdapter(relAdapter);
		relSpinner.setSelection(relationIndex);


		ArrayAdapter<CharSequence> choiceAdapter1 = ArrayAdapter.createFromResource(this,R.array.choice,android.R.layout.simple_list_item_1);
		choiceAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		choiceSpinner1.setAdapter(choiceAdapter1);
		choiceSpinner1.setSelection(term1variableIndex);

		//on relation choice click
		relSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if(parent.getItemAtPosition(position).toString().equals("")){
					ArrayAdapter<CharSequence> choiceAdapter2 = ArrayAdapter.createFromResource(UpdateActivity.this,R.array.blank,android.R.layout.simple_list_item_1);
					choiceAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					choiceSpinner2.setAdapter(choiceAdapter2);					
				}
				else{
					ArrayAdapter<CharSequence> choiceAdapter2 = ArrayAdapter.createFromResource(UpdateActivity.this,R.array.choice,android.R.layout.simple_list_item_1);
					choiceAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					choiceSpinner2.setAdapter(choiceAdapter2);
					if(position == relationIndex)
					{
						choiceSpinner2.setSelection(term2variableIndex);
					}
				}
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {}});

		//on selection of choice 1
		choiceSpinner1.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) 
			{
				// TODO Auto-generated method stub
				//Toast.makeText(getApplicationContext(), parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
				if(parent.getItemAtPosition(position).toString().equals("TEMPERATURE")){
					ArrayAdapter<CharSequence> tempAdapter = ArrayAdapter.createFromResource(UpdateActivity.this,R.array.temperature,android.R.layout.simple_list_item_1);
					tempAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					choiceSpinner1Value.setAdapter(tempAdapter);
				}
				else 
				{
					ArrayAdapter<CharSequence> lightAdapter = ArrayAdapter.createFromResource(UpdateActivity.this,R.array.ambience,android.R.layout.simple_list_item_1);
					lightAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					choiceSpinner1Value.setAdapter(lightAdapter);
				}
				if(position == term1variableIndex)
				{
					choiceSpinner1Value.setSelection(term1valueIndex);
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {}});

		// on selection of choice 2
		choiceSpinner2.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				//Toast.makeText(getApplicationContext(), parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
				if(parent.getItemAtPosition(position).toString().equals("TEMPERATURE")){
					ArrayAdapter<CharSequence> tempAdapter = ArrayAdapter.createFromResource(UpdateActivity.this,R.array.temperature,android.R.layout.simple_list_item_1);
					tempAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					choiceSpinner2Value.setAdapter(tempAdapter);
					if(position == term2variableIndex)
					{
						choiceSpinner2Value.setSelection(term2valueIndex);
					}
				}
				else if(parent.getItemAtPosition(position).toString().equals("AMBIENT")){
					ArrayAdapter<CharSequence> lightAdapter = ArrayAdapter.createFromResource(UpdateActivity.this,R.array.ambience,android.R.layout.simple_list_item_1);
					lightAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					choiceSpinner2Value.setAdapter(lightAdapter);
					if(position == term2variableIndex)
					{
						choiceSpinner2Value.setSelection(term2valueIndex);
					}
				}
				else{
					ArrayAdapter<CharSequence> lightAdapter = ArrayAdapter.createFromResource(UpdateActivity.this,R.array.blank,android.R.layout.simple_list_item_1);
					lightAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					choiceSpinner2Value.setAdapter(lightAdapter);
				}
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {}});

		// on click
		// update current rule 
		update.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
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
					
					rule_array.put(current_rule,jo);
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
		
		//on click, remove current rule

		delete.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				rule_array.remove(current_rule);
				
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

	private int getIndex(String string, String[] choices) {

		for(int i=0; i < choices.length; i++)
		{
			if(true == string.contains(choices[i]))
			{
				return i;
			}
		}

		return 0;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.update, menu);
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
	
	
	//Async task update rule
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
