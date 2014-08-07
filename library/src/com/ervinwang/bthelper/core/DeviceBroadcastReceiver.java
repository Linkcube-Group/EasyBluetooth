package com.ervinwang.bthelper.core;

import static android.bluetooth.BluetoothAdapter.EXTRA_STATE;

import com.ervinwang.bthelper.BTHelper;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class DeviceBroadcastReceiver extends BroadcastReceiver {

	private String TAG = "BTDeviceReceiver";

	private OnBTDiscoveryListener mListener;

	public DeviceBroadcastReceiver(OnBTDiscoveryListener listener) {
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
			if (BTHelper.isLinkCubeDevice(device)) {
				Log.d(TAG,
						"onReceive:discover a linkcube device and the bond state is "
								+ device.getBondState());
				if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
					Log.d(TAG,
							"onReceive:discover a linkcube device and the device is not bonded");
					mListener.onBTDiscoveryOne(device);
				}
			}

		}
		if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
			Log.d(TAG, "onReceive:finish discovery");
			mListener.onBTDiscoveryFinished();
		}
		if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
			Log.d(TAG, "onReceive:bluetooth bond state changed");
			BluetoothDevice device = intent
					.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			if (BTHelper.isLinkCubeDevice(device)) {
				int connectState = device.getBondState();
				switch (connectState) {
				case BluetoothDevice.BOND_NONE:
					mListener.onBTStateBondNone();
					break;
				case BluetoothDevice.BOND_BONDING:
					mListener.onBTStateBonding();
					break;
				case BluetoothDevice.BOND_BONDED:
					mListener.onBTStateBonded();
					break;
				}
			}
		}
		if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
			int state = intent.getIntExtra(EXTRA_STATE, -1);
			Log.d(TAG, "Extra_state " + state);
			switch (state) {
			case BluetoothAdapter.STATE_TURNING_ON:
				mListener.onBTStateTuringOn();
				break;
			case BluetoothAdapter.STATE_ON:
				mListener.onBTStateOn();
				break;
			case BluetoothAdapter.STATE_TURNING_OFF:
				mListener.onBTStateTuringOff();
				break;
			case BluetoothAdapter.STATE_OFF:
				mListener.onBTStateOff();
				break;
			default:
				break;
			}
		}

	}

}
