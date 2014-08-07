package com.ervinwang.bthelper.core.service;

import android.bluetooth.BluetoothDevice;

import com.ervinwang.bthelper.core.IReceiveData;

interface IDeviceService {

	boolean bondDevice(BluetoothDevice device);

	boolean connectDevice(String deviceName, String macAddress);

	boolean disconnectDevice(String deviceName, String macAddress);

	BluetoothDevice getBluetoothDevice();

	boolean checkConnection();

	void startReceiveData(IReceiveData receiveData);

	void clearDataBuffer();

	void stopReceiveData();
}
