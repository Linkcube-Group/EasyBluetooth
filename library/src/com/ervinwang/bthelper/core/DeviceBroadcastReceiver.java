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

	private OnDeviceDiscoveryListener mListener;

	public DeviceBroadcastReceiver(OnDeviceDiscoveryListener listener) {
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
					mListener.onDeviceDiscoveryOne(device);
				}
			}

		}
		if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
			Log.d(TAG, "onReceive:finish discovery");
			mListener.onDeviceDiscoveryFinished();
		}
		if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
			Log.d(TAG, "onReceive:bluetooth bond state changed");
			BluetoothDevice device = intent
					.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			if (BTHelper.isLinkCubeDevice(device)) {
				int connectState = device.getBondState();
				switch (connectState) {
				case BluetoothDevice.BOND_NONE:
					mListener.onDeviceStateBondNone();
					break;
				case BluetoothDevice.BOND_BONDING:
					mListener.onDeviceStateBonding();
					break;
				case BluetoothDevice.BOND_BONDED:
					mListener.onDeviceStateBonded();
					break;
				}
			}
		}
		if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
			int state = intent.getIntExtra(EXTRA_STATE, -1);
			Log.d(TAG, "Extra_state " + state);
			switch (state) {
			case BluetoothAdapter.STATE_TURNING_ON:
				mListener.onDeviceStateTuringOn();
				break;
			case BluetoothAdapter.STATE_ON:
				mListener.onDeviceStateOn();
				break;
			case BluetoothAdapter.STATE_TURNING_OFF:
				mListener.onDeviceStateTuringOff();
				break;
			case BluetoothAdapter.STATE_OFF:
				mListener.onDeviceStateOff();
				break;
			default:
				break;
			}
		}

	}

}
