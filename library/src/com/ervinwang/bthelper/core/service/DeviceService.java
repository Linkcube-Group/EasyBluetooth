package com.ervinwang.bthelper.core.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import com.ervinwang.bthelper.BTHelper;
import com.ervinwang.bthelper.core.DeviceConnectionManager;
import com.ervinwang.bthelper.core.IReceiveData;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Build;
import android.util.Log;
import static com.ervinwang.bthelper.Const.DEVICE_STATE.*;

public class DeviceService implements IDeviceService {

	private String TAG = "DeviceService";

	private BluetoothDevice currentDevice = null;

	private BluetoothSocket curSocket = null;

	private byte[] checkData = { 0x35, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x35 };

	private String dataBuffer = "";

	private Thread mReadThread;

	private int deviceState;

	public DeviceService() {

	}

	public boolean bondDevice(BluetoothDevice dev) {
		Method createBondMethod;
		Log.d(TAG, "createBondMethod--bondDevice");
		try {
			createBondMethod = dev.getClass().getMethod("createBond");
			try {
				createBondMethod.invoke(dev);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				deviceState = BOND_NONE;
				return false;
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				deviceState = BOND_NONE;
				return false;
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				deviceState = BOND_NONE;
				return false;
			}
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			deviceState = BOND_NONE;
			return false;
		}
		deviceState = BONDED;
		return true;
	}

	@SuppressLint("NewApi")
	private boolean connectDevice() {
		UUID suuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
		if (currentDevice == null) {
			return false;
		}
		if (curSocket != null) {
			try {
				curSocket.close();
				curSocket = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		BluetoothSocket tmp = null;
		try {
			if (Build.VERSION.SDK_INT >= 10) {
				tmp = currentDevice
						.createInsecureRfcommSocketToServiceRecord(suuid);
			} else {
				// Method m;
				// m = curDevice.getClass()
				// .getMethod("createRfcommSocket",
				// new Class[] { int.class });
				// tmp = (BluetoothSocket) m.invoke(curDevice, 1);
				tmp = currentDevice.createRfcommSocketToServiceRecord(suuid);
			}

		} catch (IOException e) {
			return false;
		}

		curSocket = tmp;

		try {
			BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
			adapter.cancelDiscovery();
			curSocket.connect();
		} catch (IOException e) {
			try {
				curSocket.close();
				curSocket = null;
			} catch (IOException e2) {
				return false;
			}
			e.printStackTrace();
			return false;
		}
		DeviceConnectionManager.getInstance().setmIsConnected(true, currentDevice);
		deviceState = CONNECTED;
		return true;
	}

	@Override
	public boolean connectDevice(String name, String macaddr) {

		Log.d(TAG, "connectToy:");
		currentDevice = null;

		if (!BTHelper.isBluetoothEnabled()) {
			return false;
		}

		Set<BluetoothDevice> devices = BluetoothAdapter.getDefaultAdapter()
				.getBondedDevices();

		if (devices.size() <= 0) {
			return false;
		}

		for (Iterator<BluetoothDevice> iterator = devices.iterator(); iterator
				.hasNext();) {
			BluetoothDevice bluetoothDevice = (BluetoothDevice) iterator.next();
			String deviceName = bluetoothDevice.getName();
			Log.d(TAG, "device mac address = " + bluetoothDevice.getAddress());
			Log.d(TAG, "device mac name = " + deviceName);
			if (deviceName.contains(name)) {
				if (bluetoothDevice.getAddress().equalsIgnoreCase(macaddr)) {
					currentDevice = bluetoothDevice;
					return connectDevice();
				}
			}

		}

		return false;
	}

	@Override
	public boolean disconnectDevice(String name, String macaddr) {
		if (currentDevice == null || curSocket == null) {
			return false;
		}
		try {
			curSocket.close();
			curSocket = null;
		} catch (IOException e2) {
			return false;
		}
		DeviceConnectionManager.getInstance().setmIsConnected(false, currentDevice);
		return true;
	}

	private class ReadDataThread implements Runnable {

		private BluetoothSocket mSocket;

		private IReceiveData mReceiveData;

		public ReadDataThread(BluetoothSocket socket, IReceiveData receiveData) {
			mSocket = socket;
			mReceiveData = receiveData;
		}

		public void run() {

			byte[] buffer = new byte[1024];
			int bytes;
			InputStream mmInStream = null;
			try {
				mmInStream = mSocket.getInputStream();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			while (true) {
				try {
					if ((bytes = mmInStream.read(buffer)) > 0) {
						byte[] buf_data = new byte[bytes];
						for (int i = 0; i < bytes; i++) {
							buf_data[i] = buffer[i];
						}
						mReceiveData.receiveData(buf_data);
						String data = bytesToHexString(buf_data);
						if (dataBuffer.equals("")) {
							dataBuffer = data + "_"
									+ System.currentTimeMillis();
						} else {
							dataBuffer = dataBuffer + "|" + data + "_"
									+ System.currentTimeMillis();
						}
						Log.d("data from byte to hex", dataBuffer);
					}
				} catch (IOException e) {
					try {
						mmInStream.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					break;
				}
			}
		}
	}

	@Override
	public boolean checkConnection() {
		if (currentDevice == null || curSocket == null) {
			deviceState = INTERRUPTED;
			return false;
		}

		OutputStream tmpOut = null;
		try {
			tmpOut = curSocket.getOutputStream();
		} catch (IOException e) {
			Log.d(TAG, "sockets not created", e);
			deviceState = INTERRUPTED;
			return false;
		}

		try {
			tmpOut.write(checkData);
		} catch (IOException e) {
			e.printStackTrace();
			Log.d(TAG, "Toy is disconnected.");
			deviceState = CONNECTED;
			return false;
		}

		Log.d(TAG, "Toy is connected.");

		return true;
	}

	@Override
	public void startReceiveData(IReceiveData receiveData) {
		if (receiveData == null) {
			throw new IllegalArgumentException("IReceiveData is null");
		}
		mReadThread = new Thread(new ReadDataThread(curSocket, receiveData));
		mReadThread.start();
	}

	@Override
	public void clearDataBuffer() {
		dataBuffer = "";
	}

	@Override
	public void stopReceiveData() {
		mReadThread.interrupt();
		mReadThread = null;
	}

	public static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	@Override
	public BluetoothDevice getBluetoothDevice() {
		return currentDevice;
	}

}
