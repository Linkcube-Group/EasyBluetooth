package com.ervinwang.bthelper.core;

import android.bluetooth.BluetoothDevice;

/**
 * 搜索蓝牙设备的回调接口
 * 
 * @author Ervin
 * 
 */
public interface OnBTDiscoveryListener {

	/**
	 * 发现一个设备的回调接口
	 */
	void onBTDiscoveryOne(BluetoothDevice device);

	/**
	 * 查找蓝牙设备完毕的回调接口
	 */
	void onBTDiscoveryFinished();

	/**
	 * 蓝牙正在打开
	 */
	void onBTStateTuringOn();

	/**
	 * 蓝牙正在关闭
	 */
	void onBTStateTuringOff();

	/**
	 * 蓝牙已经打开
	 */
	void onBTStateOn();

	/**
	 * 蓝牙已经关闭
	 */
	void onBTStateOff();

	/**
	 * 绑定设备成功
	 */
	void onBTStateBonded();

	/**
	 * 没有绑定设备
	 */
	void onBTStateBondNone();

	/**
	 * 正在绑定蓝牙设备
	 */
	void onBTStateBonding();

}
