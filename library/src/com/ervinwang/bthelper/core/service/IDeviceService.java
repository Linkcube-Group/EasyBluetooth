package com.ervinwang.bthelper.core.service;

import com.ervinwang.bthelper.core.IReceiveData;

interface IDeviceService {

	boolean connectToy(String deviceName, String macAddress);

	boolean disconnectToy(String deviceName, String macAddress);

	boolean checkConnection();

	void startReadData(IReceiveData receiveData);

	void clearDataBuffer();

	void stopReadData();
}
