/*
 * Tipper Class to get fuzzy logic values from blinds.fcl
 * 
 * @author1 Ruturaj Hagawane
 * @author2 FNU Shivangi
 */

package edu.rit.csci759.fuzzylogic;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

import edu.rit.csci759.jsonrpc.client.JsonRPCClient;
import edu.rit.csci759.jsonrpc.server.JsonRPCServer;
import edu.rit.csci759.rspi.RpiIndicatorImplementation;
import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;

public class MyTipperClass {

	static int WAIT_TIME = 30;

	//add function
	//edit rule function
	//remove rule function

	public static void main(String[] args) throws Exception {

		Semaphore lock = new Semaphore(1);

		RpiIndicatorImplementation rpi = new RpiIndicatorImplementation();

		int previous_temp = 0; 
		int current_temp = 0;
		int current_light = 0;
		String currentBlindState = ""; 
		JsonRPCClient jsonRPCClient = new JsonRPCClient();

		String filename = "FuzzyLogic/Blinds.fcl";
		FIS fis = FIS.load(filename, true);

		if (fis == null) {
			System.err.println("Can't load file: '" + filename + "'");
			System.exit(1);
		}

		// Get default function block
		FunctionBlock fb = fis.getFunctionBlock(null);

		Runnable server = new JsonRPCServer(fb,rpi,jsonRPCClient,lock);
		Thread thread = new Thread(server);
		thread.start();

		rpi.led_all_on();

		//start processing here

		while(true)
		{
			try
			{
				System.out.println("checking on blind");
				// Synchronize on lock
				// start sync

				lock.acquire();

				// get inputs
				current_temp = rpi.read_temperature();
				current_light = rpi.read_ambient_light_intensity();

				fb.setVariable("TEMPERATURE", current_temp);
				fb.setVariable("AMBIENT", current_light);

				// Evaluate
				fb.evaluate();

				// Show output variable's chart
				fb.getVariable("BLIND").defuzzify();

				//move blind as per rule

				double blind_half = fb.getVariable("BLIND").getMembership("HALF");
				double blind_open = fb.getVariable("BLIND").getMembership("OPEN");
				double blind_close = fb.getVariable("BLIND").getMembership("CLOSE");

				if(blind_close >= blind_open && blind_close >= blind_half)
				{
					if(false == currentBlindState.equals("close"))
					{
						//close blind
						rpi.led_all_off();
						rpi.led_when_high();
						currentBlindState = "close";
					}
				}
				else if(blind_open >= blind_half)
				{
					if(false == currentBlindState.equals("open"))
					{
						//open blind
						rpi.led_all_off();
						rpi.led_when_low();
						currentBlindState = "open";
					}
				}
				else
				{
					//open or close half
					if(false == currentBlindState.equals("half"))
					{
						// open half
						rpi.led_all_off();
						rpi.led_when_mid();
						currentBlindState = "half";
					}

				}

				// notify if necessary
				// if temp change by 2 degree or big change in light then notify
				if(Math.abs(current_temp-previous_temp) >= 2)
				{
					if(true == jsonRPCClient.is_set())
					{
						System.out.println("Sending update to phone");
						//notify temp and light
						Map<String,Object> param = new HashMap<String,Object>();
						DateFormat df = DateFormat.getTimeInstance();
						String time = df.format(new Date());
						String light_string;

						//get ambience as string
						double darkvalue = fb.getVariable("AMBIENT").getMembership("DARK");
						double dimvalue = fb.getVariable("AMBIENT").getMembership("DIM");
						double brightvalue = fb.getVariable("AMBIENT").getMembership("BRIGHT");

						if(darkvalue >= brightvalue && darkvalue >= dimvalue)
						{
							light_string = "DARK";
						}
						else if(dimvalue >= brightvalue)
						{
							light_string = "DIM";
						}
						else
						{
							light_string = "BRIGHT";
						}

						param.put("temp", current_temp);
						param.put("light", light_string);
						param.put("time", time);

						JsonRPCClient.send_notifications(param);
						previous_temp = current_temp;
					}
				}

				//end sync
				lock.release();

				//wait for sometime
				Thread.sleep(WAIT_TIME * 1000);
			}catch (Exception e) {
				e.printStackTrace();
				lock.acquire();
				rpi.led_error(5);
				lock.release();
			}
		}
	}
}
