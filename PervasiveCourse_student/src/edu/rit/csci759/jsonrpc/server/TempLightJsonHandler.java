/*
 * JSON Handler for light and temperature
 * 
 * @author1 Ruturaj Hagawane
 * @author2 FNU Shivangi
 */
package edu.rit.csci759.jsonrpc.server;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;


import org.json.*;

import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.rule.Rule;
import net.sourceforge.jFuzzyLogic.rule.RuleBlock;
import net.sourceforge.jFuzzyLogic.rule.RuleExpression;
import net.sourceforge.jFuzzyLogic.rule.RuleTerm;
import net.sourceforge.jFuzzyLogic.rule.Variable;
import net.sourceforge.jFuzzyLogic.ruleConnectionMethod.RuleConnectionMethodAndMin;
import net.sourceforge.jFuzzyLogic.ruleConnectionMethod.RuleConnectionMethodOrMax;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Error;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.server.MessageContext;
import com.thetransactioncompany.jsonrpc2.server.RequestHandler;

import edu.rit.csci759.jsonrpc.client.JsonRPCClient;
import edu.rit.csci759.rspi.RpiIndicatorImplementation;

public class TempLightJsonHandler implements RequestHandler 
{

	static RpiIndicatorImplementation rpi;
	static FunctionBlock fb;
	static Semaphore lock;
	static String phones_ip;
	static String phones_port;
	static JsonRPCClient jsonRPCClient;
	
	public TempLightJsonHandler(RpiIndicatorImplementation indicator, FunctionBlock functionalblock, JsonRPCClient client, Semaphore sync) 
	{
		rpi = indicator;
		fb = functionalblock;
		lock = sync;
		jsonRPCClient = client;
	}

	// Reports the method names of the handled requests
	public String[] handledRequests() 
	{

		return new String[]{"getinfo","updaterules","startnotifications"};
	}


	// Processes the requests
	public JSONRPC2Response process(JSONRPC2Request req, MessageContext ctx) 
	{

		//static RpiIndicatorImplementation ;
		if (req.getMethod().equals("getinfo")) 
		{
			try {
				lock.acquire();
				String choices[] = {"TEMPERATURE","AMBIENT"};
				String tempValues[] = {"FREEZING","COLD","COMFORT","WARM","HOT"};
				String ambienceValues[] = {"DARK","DIM","BRIGHT"};
				//String result[] = {"OPEN","HALF","CLOSE"};
				String relation[] = {"BLANK","AND","OR"};

				int temp = rpi.read_temperature();
				int light = rpi.read_ambient_light_intensity();
				DateFormat df = DateFormat.getTimeInstance();
				String time = df.format(new Date());
				JSONObject jo = new JSONObject();

				HashMap<String, Object> params = (HashMap<String, Object>) req.getNamedParams();

				phones_ip = (String) params.get("ip"); 
				phones_port = (String) params.get("port");

				jsonRPCClient.set(phones_ip, phones_port);
				
				System.out.println("PhonesIP "+phones_ip + ":" + phones_port);
				
				//connect to phone in separate class
				
				fb.setVariable("AMBIENT", light);

				// Evaluate
				fb.evaluate();

				// Show output variable's chart
				//fb.getVariable("TEMPERATURE").defuzzify();
				double darkvalue = fb.getVariable("AMBIENT").getMembership("DARK");
				double dimvalue = fb.getVariable("AMBIENT").getMembership("DIM");
				double brightvalue = fb.getVariable("AMBIENT").getMembership("BRIGHT");
				
				if(darkvalue >= brightvalue && darkvalue >= dimvalue)
				{
					jo.put("light", "DARK");
				}
				else if(dimvalue >= brightvalue)
				{
					jo.put("light", "DIM");
				}
				else
				{
					jo.put("light", "BRIGHT");
				}
				
				jo.put("temp", temp);
				jo.put("time", time);

				RuleBlock s = fb.getFuzzyRuleBlock(null);
				List<Rule> rules = s.getRules();

				JSONArray ruleArray = new JSONArray();

				for(int i = 0; i < rules.size(); i++)
				{
					JSONObject one = new JSONObject();

					Rule rule = rules.get(i);

					RuleExpression a = rule.getAntecedents();

					if(null != a.getTerm1())
					{
						int term1variableIndex = getIndex(a.getTerm1().toString(), choices);
						one.put("term1variable",choices[term1variableIndex]);

						int term1valueIndex;
						if(true == "TEMPERATURE".equals(choices[term1variableIndex]))
						{
							term1valueIndex = getIndex(a.getTerm1().toString(), tempValues);
							one.put("term1value", tempValues[term1valueIndex]);
						}
						else
						{
							term1valueIndex = getIndex(a.getTerm1().toString(), ambienceValues);
							one.put("term1value", ambienceValues[term1valueIndex]);
						}
					}
					else
					{
						one.put("term1variable","");
						one.put("term1value","");
					}

					if(null != a.getTerm2())
					{
						int term2variableIndex = getIndex(a.getTerm2().toString(), choices);
						one.put("term2variable",choices[term2variableIndex]);

						int term2valueIndex;
						if(true == "TEMPERATURE".equals(choices[term2variableIndex]))
						{
							term2valueIndex = getIndex(a.getTerm2().toString(), tempValues);
							one.put("term2value", tempValues[term2valueIndex]);
						}
						else
						{
							term2valueIndex = getIndex(a.getTerm2().toString(), ambienceValues);
							one.put("term2value", ambienceValues[term2valueIndex]);
						}

						int relationIndex = getIndex(a.getRuleConnectionMethod().toString(), relation);
						one.put("relation", relation[relationIndex]);
					}
					else
					{
						one.put("term2variable","");
						one.put("term2value","");
						one.put("relation","");
					}

					LinkedList<RuleTerm> b = rule.getConsequents();

					RuleTerm b1 = b.get(0);


					one.put("result",b1.getTermName().toString());


					ruleArray.put(one);
				}


				jo.put("rules", ruleArray);

				lock.release();
				return new JSONRPC2Response(jo.toString(), req.getID());
			} catch (JSONException | InterruptedException e) {
				e.printStackTrace();
				return new JSONRPC2Response(JSONRPC2Error.INTERNAL_ERROR, req.getID());
			}
		}
		else if(req.getMethod().equals("updaterules"))
		{
			System.out.println("updates rules called, updating");
			RuleBlock rules = fb.getFuzzyRuleBlock(null);
			HashMap<String, Object> params = (HashMap<String, Object>) req.getNamedParams();

			String received = (String) params.get("update"); 

			try {
				lock.acquire();
				JSONObject object = new JSONObject(received); 

				//System.out.println(object.toString());

				JSONArray new_rules = (JSONArray) object.get("rules");

				List<Rule> previous_rules = rules.getRules();

				while(0 < previous_rules.size())
				{
					rules.remove(previous_rules.get(0));
				}

				for(int i =0; i < new_rules.length(); i++ )
				{
					//get new rule
					JSONObject current;

					current = new_rules.getJSONObject(i);

					Rule newrule = new Rule("Rule "+i, rules);

					Variable term1variable = rules.getFunctionBlock().getVariable(current.getString("term1variable"));

					RuleTerm term1 = new RuleTerm(term1variable,current.getString("term1value"), false);

					RuleExpression antecedents;

					if("".equals(current.get("relation")))
					{
						antecedents = new RuleExpression(term1, null, RuleConnectionMethodOrMax.get());
					}
					else if("AND".equals(current.getString("relation")))
					{
						Variable term2variable = rules.getFunctionBlock().getVariable(current.getString("term2variable"));
						RuleTerm term2 = new RuleTerm(term2variable,current.getString("term2value"), false);
						antecedents = new RuleExpression(term1, term2, RuleConnectionMethodAndMin.get());
					}
					else
					{
						Variable term2variable = rules.getFunctionBlock().getVariable(current.getString("term2variable"));
						RuleTerm term2 = new RuleTerm(term2variable,current.getString("term2value"), false);
						antecedents = new RuleExpression(term1, term2, RuleConnectionMethodOrMax.get());
					}

					newrule.setAntecedents(antecedents);
					//newrule.addConsequent(variable, termName, negated)
					Variable var = rules.getFunctionBlock().getVariable("BLIND");
					newrule.addConsequent(var,current.getString("result"), false);

					// put it in rules
					rules.add(newrule);

				}
				lock.release();
				return new JSONRPC2Response("Rules updated", req.getID());
			} catch (JSONException | InterruptedException e) {
				e.printStackTrace();
				return new JSONRPC2Response(JSONRPC2Error.INTERNAL_ERROR, req.getID());
			}
		}
		else 
		{
			return new JSONRPC2Response(JSONRPC2Error.METHOD_NOT_FOUND, req.getID());
		}
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
	
	
}
