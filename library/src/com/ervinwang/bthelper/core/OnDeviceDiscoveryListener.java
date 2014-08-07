package com.ervinwang.bthelper.core;

import android.bluetooth.BluetoothDevice;

/**
 * 搜索蓝牙设备的回调接口
 * 
 * @author Ervin
 * 
 */
public interface OnDeviceDiscoveryListener {

	/**
	 * 发现一个设备的回调接口
	 */
	void onDeviceDiscoveryOne(BluetoothDevice device);

	/**
	 * 查找蓝牙设备完毕的回调接口
	 */
	void onDeviceDiscoveryFinished();

	/**
	 * 蓝牙正在打开
	 */
	void onDeviceStateTuringOn();

	/**
	 * 蓝牙正在关闭
	 */
	void onDeviceStateTuringOff();

	/**
	 * 蓝牙已经打开
	 */
	void onDeviceStateOn();

	/**
	 * 蓝牙已经关闭
	 */
	void onDeviceStateOff();

	/**
	 * 绑定设备成功
	 */
	void onDeviceStateBonded();

	/**
	 * 没有绑定设备
	 */
	void onDeviceStateBondNone();

	/**
	 * 正在绑定蓝牙设备
	 */
	void onDeviceStateBonding();

}
