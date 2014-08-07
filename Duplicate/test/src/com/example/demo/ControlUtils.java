package com.example.demo;

import me.linkcube.library.core.bluetooth.LinkcubeBT;

public class ControlUtils {

	public static void openBluetooth(){
		LinkcubeBT.setBluetoothEnable(true);
	}
	
	public static void closeBluetooth(){
		LinkcubeBT.setBluetoothEnable(false);
	}
}
