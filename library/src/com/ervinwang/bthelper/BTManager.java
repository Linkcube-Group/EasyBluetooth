package com.ervinwang.bthelper;

import java.util.Timer;
import java.util.TimerTask;

import com.ervinwang.bthelper.core.service.DeviceService;

public class BTManager {

	public DeviceService toyService;

	private Timer timer;

	private String currentDevice;

	private static BTManager instance;

	private int toyState;

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
		toyService = new DeviceService();
	}

	public DeviceService getDeviceService() {
		return toyService;
	}

	public int getDeviceState() {
		return toyState;
	}

	public String getDeviceConnected() {
		return currentDevice;
	}

	public class CheckConnectionTask extends TimerTask {

		@Override
		public void run() {

			if (toyState == Const.DEVICE_STATE.CONNECTED) {
				if (!toyService.checkConnection()) {
					toyState = Const.DEVICE_STATE.INTERRUPTED;
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
