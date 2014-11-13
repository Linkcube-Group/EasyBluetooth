package com.ervinwang.bthelper;

import java.util.Timer;
import java.util.TimerTask;

import android.bluetooth.BluetoothDevice;

import com.ervinwang.bthelper.core.IReceiveData;
import com.ervinwang.bthelper.core.service.DeviceService;

public class BTManager {

	public DeviceService deviceService;

	private Timer timer;

	private static BTManager instance;

	private int deviceState;

	public static BTManager getInstance() {
		if (instance == null) {
			synchronized (BTManager.class) {
				if (instance == null) {
					instance = new BTManager();
				}
			}
		}

		return instance;
	}

	private BTManager() {
		deviceService = new DeviceService();
	}

	public DeviceService getDeviceService() {
		return deviceService;
	}

	public int getDeviceState() {
		return deviceState;
	}

	public boolean bondDevice(BluetoothDevice dev) {
		return deviceService.bondDevice(dev);
	}

	public BluetoothDevice getDeviceConnected() {
		return null;
	}

	public void startReceiveData(IReceiveData receiveData) {
		deviceService.startReceiveData(receiveData);
	}

	public void stopReceiveData() {
		deviceService.stopReceiveData();
	}

	// //////////////////////////////////////////////////
	public class CheckConnectionTask extends TimerTask {

		@Override
		public void run() {

			if (deviceState == Const.DEVICE_STATE.CONNECTED) {
				if (!deviceService.checkConnection()) {
					deviceState = Const.DEVICE_STATE.INTERRUPTED;
					cancelCheckConnectionTask();
				}
			}
		}

	}

	public void startCheckConnetionTask() {
		timer = new Timer();
		timer.schedule(new CheckConnectionTask(), 3000, 3000);
	}

	public void cancelCheckConnectionTask() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}
}
