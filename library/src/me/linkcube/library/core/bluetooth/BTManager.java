package me.linkcube.library.core.bluetooth;

import static android.bluetooth.BluetoothAdapter.ACTION_DISCOVERY_FINISHED;
import static android.bluetooth.BluetoothAdapter.ACTION_STATE_CHANGED;
import static android.bluetooth.BluetoothDevice.ACTION_BOND_STATE_CHANGED;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import me.linkcube.library.service.IToyServiceCall;

public class BTManager {

	public static IToyServiceCall toyServiceCall;

	private Timer timer;

	private int BTstate;

	private int toyState = BTConst.TOY_STATE.BOND_NONE;

	private List<BluetoothDevice> bondedDevices;

	private List<BluetoothDevice> unbondedDevices = new ArrayList<BluetoothDevice>();

	private String currentDevice;

	private static BTManager instance;

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

	}

	protected void initBTState() {
		BTstate = BTUtils.isBluetoothEnabled() ? BTConst.BT_STATE.ON
				: BTConst.BT_STATE.OFF;
	}

	protected void regiserReceiver(Activity activity, BTReceiver receiver) {
		IntentFilter filter = new IntentFilter();
		filter.addAction(BluetoothDevice.ACTION_FOUND);
		filter.addAction(ACTION_BOND_STATE_CHANGED);
		filter.addAction(ACTION_DISCOVERY_FINISHED);
		filter.addAction(ACTION_STATE_CHANGED);
		activity.registerReceiver(receiver, filter);
	}

	protected void unregisterReceiver(Activity activity, BTReceiver receiver) {
		activity.unregisterReceiver(receiver);
	}

	protected OnBTStateListener getBTStateListener() {
		return listener;
	}

	/**
	 * 开始扫描设备
	 */
	protected void startDiscovery() {
		BluetoothAdapter madapter = BluetoothAdapter.getDefaultAdapter();
		madapter.startDiscovery();
		BTstate = BTConst.BT_STATE.DISCOVERING;
	}

	/**
	 * 取消扫描设备
	 */
	protected void cancelDiscovery() {
		BluetoothAdapter madapter = BluetoothAdapter.getDefaultAdapter();
		madapter.cancelDiscovery();
		BTstate = BTConst.BT_STATE.DISCOVER_FINISHED;
	}

	protected String getBondedToyNameList() {
		bondedDevices = BTUtils.getBondedDevices();
		return BTUtils.convertListToString(bondedDevices);
	}

	protected String getUnbondedToyNameList() {
		return BTUtils.convertListToString(unbondedDevices);
	}

	protected int getBTState() {
		return BTstate;
	}

	protected int getToyState() {
		return toyState;
	}

	protected void bond(String address) {
		BluetoothDevice device = convertUnbondedAddressToDevice(address);
		if (BTUtils.bondDevice(device)) {
			unbondedDevices.remove(device);
		}
	}

	protected void connect(String address) {
		BluetoothDevice device = convertBondedAddressToDevice(address);
		ConnectToyThread thread = new ConnectToyThread(device);
		new Thread(thread).start();
	}

	protected String getData() throws RemoteException {
		String data = BTManager.toyServiceCall.getData();
		BTManager.toyServiceCall.clearDataBuffer();
		return data;
	}

	protected String getConnectedDevice() {
		return currentDevice;
	}

	private class ConnectToyThread implements Runnable {

		private BluetoothDevice device;

		public ConnectToyThread(BluetoothDevice device) {
			this.device = device;
		}

		@Override
		public void run() {
			toyState = BTConst.TOY_STATE.CONNECTING;
			boolean success;
			try {
				success = BTManager.toyServiceCall.connectToy(device.getName(),
						device.getAddress());
			} catch (RemoteException e) {
				success = false;
				e.printStackTrace();
			}
			if (success) {
				toyState = BTConst.TOY_STATE.CONNECTED;
				currentDevice = device.getName() + "@" + device.getAddress();
				;
			} else {
				toyState = BTConst.TOY_STATE.CONNECT_FAIL;
				currentDevice = null;
			}
		}

	}

	private class CheckConnectionTask extends TimerTask {

		@Override
		public void run() {

			if (toyState == BTConst.TOY_STATE.CONNECTED) {
				try {
					if (!BTManager.toyServiceCall.checkConnection()) {
						toyState = BTConst.TOY_STATE.INTERRUPTED;
						cancelCheckConnectionTask();
					}
				} catch (RemoteException e) {
					toyState = BTConst.TOY_STATE.INTERRUPTED;
					cancelCheckConnectionTask();
					e.printStackTrace();
				}
			}

		}

	}

	protected void startCheckConnetionTask() {
		timer = new Timer();
		timer.schedule(new CheckConnectionTask(), 3000, 3000);
	}

	protected void cancelCheckConnectionTask() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

	private OnBTStateListener listener = new OnBTStateListener() {

		@Override
		public void onDiscoveryOne(BluetoothDevice device) {
			filter(device);
			BTstate = BTConst.BT_STATE.DISCOVER_ONE;
		}

		@Override
		public void onDiscoveryFinished() {
			BTstate = BTConst.BT_STATE.DISCOVER_FINISHED;
		}

		@Override
		public void onStateTuringOn() {
			BTstate = BTConst.BT_STATE.TURNING_ON;
		}

		@Override
		public void onStateTuringOff() {
			BTstate = BTConst.BT_STATE.TURNING_OFF;

		}

		@Override
		public void onStateOn() {
			BTstate = BTConst.BT_STATE.ON;
		}

		@Override
		public void onStateOff() {
			BTstate = BTConst.BT_STATE.OFF;

		}

		@Override
		public void onStateBonded() {
			toyState = BTConst.TOY_STATE.BONDED;
		}

		@Override
		public void onStateBondNone() {
			toyState = BTConst.TOY_STATE.BOND_NONE;
		}

		@Override
		public void onStateBonding() {
			toyState = BTConst.TOY_STATE.BONDING;
		}
	};

	private boolean filter(BluetoothDevice device) {

		for (int i = 0; i < bondedDevices.size(); i++) {
			BluetoothDevice temp = bondedDevices.get(i);
			if (temp.getAddress().equals(device.getAddress()))
				return true;
		}

		for (int i = 0; i < unbondedDevices.size(); i++) {
			BluetoothDevice temp = unbondedDevices.get(i);
			if (temp.getAddress().equals(device.getAddress()))
				return true;
		}

		unbondedDevices.add(device);
		return false;

	}

	private BluetoothDevice convertBondedAddressToDevice(String address) {
		for (int i = 0; i < bondedDevices.size(); i++) {
			BluetoothDevice temp = bondedDevices.get(i);
			if (temp.getAddress().equals(address))
				return temp;
		}
		// XXX
		return null;
	}

	private BluetoothDevice convertUnbondedAddressToDevice(String address) {
		for (int i = 0; i < unbondedDevices.size(); i++) {
			BluetoothDevice temp = unbondedDevices.get(i);
			if (temp.getAddress().equals(address))
				return temp;
		}
		// XXX
		return null;
	}

}
