package me.linkcube.library.core.bluetooth;

import android.bluetooth.BluetoothDevice;

/**
 * 搜索蓝牙设备的回调接口
 * 
 * @author orange
 * 
 */
public interface OnBTStateListener {

	/**
	 * 发现一个设备的回调接口
	 */
	void onDiscoveryOne(BluetoothDevice device);

	/**
	 * 查找蓝牙设备完毕的回调接口
	 */
	void onDiscoveryFinished();

	/**
	 * 蓝牙正在开启
	 */
	void onStateTuringOn();

	/**
	 * 蓝牙正在关闭
	 */
	void onStateTuringOff();

	/**
	 * 蓝牙已经打开
	 */
	void onStateOn();

	/**
	 * 蓝牙已经关闭
	 */
	void onStateOff();

	/**
	 * 绑定设备成功
	 */
	void onStateBonded();

	/**
	 * 没有绑定设备
	 */
	void onStateBondNone();

	/**
	 * 正在绑定
	 */
	void onStateBonding();

}
