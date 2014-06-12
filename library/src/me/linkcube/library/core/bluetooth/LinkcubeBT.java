package me.linkcube.library.core.bluetooth;

import me.linkcube.library.service.ToyService;
import me.linkcube.library.service.ToyServiceConnection;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import android.util.Log;

public class LinkcubeBT {

	private static String TAG = "LinkcubeBT";

	private static BTReceiver receiver;

	public static void onCreate(Activity activity) {
		ToyServiceConnection toyServiceConnection = new ToyServiceConnection();
		Intent toyintent = new Intent(activity, ToyService.class);
		activity.startService(toyintent);
		activity.bindService(toyintent, toyServiceConnection,
				Context.BIND_AUTO_CREATE);
		receiver = new BTReceiver(BTManager.getInstance().getBTStateListener());
		BTManager.getInstance().initBTState();
		Log.d(TAG, "onCreate");
	}

	public static void onResume(Activity activity) {
		BTManager.getInstance().regiserReceiver(activity, receiver);
	}

	public static void onDestroy(Activity activity) {
		BTManager.getInstance().unregisterReceiver(activity, receiver);
	}

	/**
	 * 蓝牙是否开启
	 * 
	 * @return 如果开启返回true，否则false
	 */
	public static boolean isBluetoothEnabled() {
		return BTUtils.isBluetoothEnabled();
	}

	/**
	 * 打开/关闭蓝牙设备
	 * 
	 * @param enabled
	 */
	public static void setBluetoothEnable(boolean enabled) {
		BTUtils.setBluetoothEnabled(enabled);
	}

	/**
	 * 开始扫描设备
	 */
	public static void startDiscovery() {
		Log.d(TAG, "startDiscovery");
		BTManager.getInstance().startDiscovery();
	}

	/**
	 * 取消扫描设备
	 */
	public static void cancelDiscovery() {
		Log.d(TAG, "cancelDiscovery");
		BTManager.getInstance().cancelDiscovery();
	}

	/**
	 * 绑定指定Mac地址设备
	 * 
	 * @param macAdrress
	 */
	public static void bond(String address) {
		Log.d(TAG, "bond");
		BTManager.getInstance().bond(address);
	}

	/**
	 * 连接指定Mac地址设备
	 * 
	 * @param macAdrress
	 */
	public static void connect(String adrress) {
		BTManager.getInstance().connect(adrress);
	}

	/**
	 * 获取已连接设备，名称规则为Name@MacAdrress;若无绑定设备，则返回null
	 * 
	 * @return Name@MacAdrress
	 */
	public static String getConnectedDevice() {
		return BTManager.getInstance().getConnectedDevice();
	}

	/**
	 * 获取绑定设备名称列表，名称规则为Name@MacAdrress，多个设备则以'|'来分割。如Name1@MacAdrress1|Name2@MacAdrress2
	 * 
	 * @return Name@MacAdrress
	 */
	public static String getBondedToyNameList() {
		return BTManager.getInstance().getBondedToyNameList();
	}

	/**
	 * 获取绑定设备名称列表，名称规则为Name@MacAdrress，多个设备则以'|'来分割。如Name1@MacAdrress1|Name2@MacAdrress2
	 * 
	 * @return Name@MacAdrress
	 */
	public static String getUnbondedToyNameList() {
		return BTManager.getInstance().getUnbondedToyNameList();
	}

	/**
	 * 返回蓝牙状态：0-蓝牙已打开；1-蓝牙已关闭；2-蓝牙正在打开中；3-蓝牙正在关闭中；4-正在扫描设备中;5-发现一个设备；6-扫描设备过程结束
	 * 
	 * @return
	 */
	public static int getBTState() {
		return BTManager.getInstance().getBTState();
	}

	/**
	 * 返回和玩具的连接状态：0-已配对；1-配对中；2-没有配对；3-已连接；4-连接失败；5-连接失败；6-其他原因失去连接
	 * 
	 * @return
	 */
	public static int getToyState() {
		return BTManager.getInstance().getToyState();
	}

	public static void setCommond() {
		try {
			BTManager.toyServiceCall.setShakeMode(1, 1);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public static String getCommond() {
		try {
			String toyCommond = BTManager.toyServiceCall.getCommand();
			return toyCommond;
		} catch (RemoteException e) {
			e.printStackTrace();
			return null;
		}

	}
}
