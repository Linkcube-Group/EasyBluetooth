package me.linkcube.library.core.bluetooth;

import static android.bluetooth.BluetoothAdapter.ACTION_DISCOVERY_FINISHED;
import static android.bluetooth.BluetoothAdapter.ACTION_STATE_CHANGED;
import static android.bluetooth.BluetoothDevice.ACTION_BOND_STATE_CHANGED;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.IntentFilter;
import android.os.RemoteException;
import me.linkcube.library.service.IToyServiceCall;

public class BTManager {

	public static IToyServiceCall toyServiceCall;

	private Timer timer;

	private int BTstate;

	private int toyState = BTConst.TOY_STATE.BOND_NONE;

	private BluetoothDevice device;

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

	public void initBTState() {
		BTstate = BTUtils.isBluetoothEnabled() ? BTConst.BT_STATE.ON
				: BTConst.BT_STATE.OFF;
	}

	public void regiserReceiver(Activity activity, BTReceiver receiver) {
		IntentFilter filter = new IntentFilter();
		filter.addAction(BluetoothDevice.ACTION_FOUND);
		filter.addAction(ACTION_BOND_STATE_CHANGED);
		filter.addAction(ACTION_DISCOVERY_FINISHED);
		filter.addAction(ACTION_STATE_CHANGED);
		activity.registerReceiver(receiver, filter);
	}

	public void unregisterReceiver(Activity activity, BTReceiver receiver) {
		activity.unregisterReceiver(receiver);
	}

	public OnBTStateListener getBTStateListener() {
		return listener;
	}

	/**
	 * 返回蓝牙状态：0-蓝牙已打开；1-蓝牙已关闭；2-蓝牙正在打开中；3-蓝牙正在关闭中；4-正在扫描设备中
	 * 
	 * @return
	 */
	public int getBTState() {
		return BTstate;
	}

	/**
	 * 返回和玩具的连接状态：0-已配对；1-配对中；2-没有配对；3-已连接；4-连接失败
	 * 
	 * @return
	 */
	public int getToyState() {
		return toyState;
	}

	/**
	 * 获取玩具名称
	 * 
	 * @return
	 */
	public String getToyName() {
		return device.getName();
	}

	/**
	 * 配对玩具
	 */
	public void bond() {
		BTUtils.bondDevice(device);
	}

	/**
	 * 连接玩具
	 */
	public void connect() {
		ConnectToyThread thread = new ConnectToyThread();
		new Thread(thread).start();
	}

	private class ConnectToyThread implements Runnable {

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
			toyState = success ? BTConst.TOY_STATE.CONNECTED
					: BTConst.TOY_STATE.CONNECT_FAIL;
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

	public void startCheckConnetionTask() {
		timer = new Timer();
		timer.schedule(new CheckConnectionTask(), 3000, 3000);
	}

	private void cancelCheckConnectionTask() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

	private OnBTStateListener listener = new OnBTStateListener() {

		@Override
		public void onDiscoveryOne(BluetoothDevice device) {
			BTManager.this.device = device;
			BTstate = BTConst.BT_STATE.DISCOVER_ONE;
			BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
			adapter.cancelDiscovery();
		}

		@Override
		public void onDiscoveryFinished() {

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

}
