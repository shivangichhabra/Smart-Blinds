/*
 * Activity to display rules on it
 * 
 * @author1 Ruturaj Hagawane
 * @author2 FNU Shivangi
 */

package edu.rit.csci759.mobile;

import java.util.ArrayList;

import org.json.*;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class RulesActivity extends Activity {

	Button addRule_button;	
	ListView rules;
	int request_Code = 10;
	String rules_JSON;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rules);

		rules = (ListView)findViewById(R.id.rules);

		Intent intent = getIntent();
		rules_JSON = intent.getStringExtra("rules");


		makeList();

		// on click of each rule
		// open updateRule activity
		rules.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Intent intent = new Intent(RulesActivity.this,UpdateActivity.class);
				intent.putExtra("Number", position);
				intent.putExtra("Rule", rules_JSON);
				startActivityForResult(intent, request_Code);

			}
		});


		addRule_button = (Button)findViewById(R.id.addRule);


		// on click of add rule button
		// open addRule activity
		addRule_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(RulesActivity.this, AddRuleActivity.class);
				intent.putExtra("Rule", rules_JSON);
				startActivityForResult(intent, request_Code);
			}
		});

	}

	void makeList()
	{
		ArrayList<String> list;

		list = new ArrayList<String>();

		if(null != rules_JSON )
		{
			//get rules from JSON array and print them
			try {
				JSONArray rulesArray;
				rulesArray = new JSONArray(rules_JSON);

				for(int i = 0; i < rulesArray.length(); i++)
				{
					JSONObject rule = rulesArray.getJSONObject(i);

					String term1variable,term1value,relation,term2variable, term2value,result;

					term1variable = rule.getString("term1variable");
					term1value = rule.getString("term1value");

					result = rule.getString("result");
					relation = "";
					term2variable = "";
					term2value = "";

					if(false == rule.getString("relation").equals(""))
					{
						relation = rule.getString("relation");
						term2variable = rule.getString("term2variable");
						term2value = rule.getString("term2value");
						list.add("Rule " + (i+1) + ": IF " + term1variable + " IS " + term1value + " " + relation + " " + term2variable + " IS " + term2value + " THEN BLIND IS " + result);
					}
					else
					{
						list.add("Rule " + (i+1) + ": IF " + term1variable + " IS " + term1value + " THEN BLIND IS " + result);
					}

					//list.add(rulesArray.get(i).toString());
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		ArrayAdapter<String> l = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
		rules.setAdapter(l);

	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.rules, menu);
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == request_Code)
		{
			if(resultCode == RESULT_OK)
			{
				rules_JSON = data.getData().toString();
				makeList();
			}
		}
	}
}
