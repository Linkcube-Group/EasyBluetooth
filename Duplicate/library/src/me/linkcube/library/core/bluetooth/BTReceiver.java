package me.linkcube.library.core.bluetooth;

import static android.bluetooth.BluetoothAdapter.EXTRA_STATE;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * 搜索蓝牙设备广播接收器
 * 
 * @author orange
 * 
 */
public class BTReceiver extends BroadcastReceiver {

	private String TAG = "BluetoothDeviceReceiver";

	private OnBTStateListener mListener;

	public BTReceiver(OnBTStateListener listener) {
		mListener = listener;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Log.d(TAG, "action:" + action);
		if (BluetoothDevice.ACTION_FOUND.equals(action)) {
			Log.d(TAG, "onReceive:discover a device");
			BluetoothDevice device = intent
					.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			if (BTUtils.isLinkCubeDevice(device)) {
				Log.d(TAG,
						"onReceive:discover a linkcube device and the bond state is "
								+ device.getBondState());
				if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
					Log.d(TAG,
							"onReceive:discover a linkcube device and the device is not bonded");
					mListener.onDiscoveryOne(device);
				}
			}

		}
		if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
			Log.d(TAG, "onReceive:finish discovery");
			mListener.onDiscoveryFinished();
		}
		if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
			Log.d(TAG, "onReceive:bluetooth bond state changed");
			BluetoothDevice device = intent
					.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			if (BTUtils.isLinkCubeDevice(device)) {
				int connectState = device.getBondState();
				switch (connectState) {
				case BluetoothDevice.BOND_NONE:
					mListener.onStateBondNone();
					break;
				case BluetoothDevice.BOND_BONDING:
					mListener.onStateBonding();
					break;
				case BluetoothDevice.BOND_BONDED:
					mListener.onStateBonded();
					break;
				}
			}
		}
		if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
			int state = intent.getIntExtra(EXTRA_STATE, -1);
			Log.d(TAG, "Extra_state " + state);
			switch (state) {
			case BluetoothAdapter.STATE_TURNING_ON:
				mListener.onStateTuringOn();
				break;
			case BluetoothAdapter.STATE_ON:
				mListener.onStateOn();
				break;
			case BluetoothAdapter.STATE_TURNING_OFF:
				mListener.onStateTuringOff();
				break;
			case BluetoothAdapter.STATE_OFF:
				mListener.onStateOff();
				break;
			default:
				break;
			}
		}

	}

}
