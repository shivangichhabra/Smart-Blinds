
package edu.rit.csci759.rspi;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: Java Examples
 * FILENAME      :  ControlGpioExample.java  
 * 
 * This file is part of the Pi4J project. More information about 
 * this project can be found here:  http://www.pi4j.com/
 * **********************************************************************
 * %%
 * Copyright (C) 2012 - 2014 Pi4J
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

public class BlinkIcrementaly {

	public static void main(String[] args) throws InterruptedException {
		
		System.out.println("<--Pi4J--> GPIO Control Example ... started.");
        
        // create gpio controller
        final GpioController gpio = GpioFactory.getInstance();
        
        // provision gpio pin #01 as an output pin and turn on by default
        final GpioPinDigitalOutput pin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_27, "MyLED", PinState.HIGH);
        System.out.println("--> GPIO state should be: ON");
        
        Thread.sleep(5000);
        
        int i = 10;
        int times = 0;
        while(times<10){
        	
        	while(i>0)
        	{        	
        		// turn off gpio pin #01
                pin.low();
                System.out.println("--> GPIO state should be: OFF for "+i+" seconds");

                Thread.sleep(1000 * i);
                
             // turn off gpio pin #01
                pin.high();
                System.out.println("--> GPIO state should be: ON for "+1+" second");

                Thread.sleep(1000);

                i--;

        	}
        	
        	pin.low();
            System.out.println("--> GPIO state should be: OFF for "+5+" seconds");

            Thread.sleep(5000);
        	
        	while(i<11)
        	{        	
        		// turn off gpio pin #01
                pin.low();
                System.out.println("--> GPIO state should be: OFF for "+i+" seconds");

                Thread.sleep(1000 * i);
                
             // turn on gpio pin #01
                pin.high();
                System.out.println("--> GPIO state should be: ON for "+1+" second");

                Thread.sleep(1000);

                i++;

        	}
        	times++;
        }
        
        gpio.shutdown();

	}

}
