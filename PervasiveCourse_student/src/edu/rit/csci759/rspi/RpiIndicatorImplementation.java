/*
 * Implementtaion of RpiIndicatorInterface
 * contains function for led reaction
 * and reads light and temperature value
 * 
 * @author1 Ruturaj Hagawane
 * @author2 FNU Shivangi
 */
package edu.rit.csci759.rspi;

import java.util.ArrayList;
import java.util.Collections;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

import edu.rit.csci759.rspi.utils.MCP3008ADCReader;

public class RpiIndicatorImplementation implements RpiIndicatorInterface{

	static GpioController gpio;
	static GpioPinDigitalOutput greenPin;
	static GpioPinDigitalOutput yellowPin;
	static GpioPinDigitalOutput redPin;

	public RpiIndicatorImplementation() {
		
		gpio = GpioFactory.getInstance();
		greenPin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_27, "green", PinState.LOW);
		yellowPin  = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_28, "yellow",  PinState.LOW);
		redPin   = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_29, "red",   PinState.LOW);
		MCP3008ADCReader.initSPI(gpio);
	}

	@Override
	public void led_all_off() {
		redPin.low();
		yellowPin.low();
		greenPin.low();
		
	}

	@Override
	public void led_all_on() {
		redPin.high();
		yellowPin.high();
		greenPin.high();
	}

	@Override
	public void led_error(int blink_count) throws InterruptedException {
		while(blink_count != 0){
			redPin.high();
			Thread.sleep(1000);
			redPin.low();
			Thread.sleep(1000);
			blink_count--;
		}
	}

	@Override
	public void led_when_low() {
		greenPin.high();
	}

	@Override
	public void led_when_mid() {
		yellowPin.high();
	}

	@Override
	public void led_when_high() {
		redPin.high();
	}

	@Override
	//reads light intensity
	public int read_ambient_light_intensity() {
		int attempts = 5;
		ArrayList<Integer> readings = new ArrayList<Integer>();
		
		for(int i = 0; i < attempts; i++)
		{
			readings.add(MCP3008ADCReader.readAdc(1));
		}
		
		Collections.sort(readings);
		
		int ambient = (int)(readings.get((int)((attempts+1)/2)) / 10.24);
		return ambient;
	}

	@Override
	//reads temperature
	public int read_temperature() {
		int attempts = 5;
		ArrayList<Integer> readings = new ArrayList<Integer>();
		
		for(int i = 0; i < attempts; i++)
		{
			readings.add(MCP3008ADCReader.readAdc(0));
		}
		
		Collections.sort(readings);
		
		int avg_temperature = readings.get((int)((attempts+1)/2));
		
		float tmp36_mVolts =(float) (avg_temperature * (3300.0/1024.0));
		// 10 mv per degree
        float temp_C = (float) (((tmp36_mVolts - 100.0) / 10.0) - 40.0);
		
		return (int)temp_C;
	}

}
