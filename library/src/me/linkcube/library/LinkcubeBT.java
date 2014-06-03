package me.linkcube.library;

import me.linkcube.library.core.bluetooth.BTManager;
import me.linkcube.library.core.bluetooth.BTReceiver;
import me.linkcube.library.core.bluetooth.BTUtils;
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
		Log.d(TAG, "onCreate");
	}

	public static void onResume(Activity activity) {
		BTManager.getInstance().regiserReceiver(activity, receiver);
	}

	public static void onDestroy(Activity activity) {
		BTManager.getInstance().unregisterReceiver(activity, receiver);
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
	public static void startDiscover() {
		Log.d(TAG, "startDiscover");
		BluetoothAdapter madapter = BluetoothAdapter.getDefaultAdapter();
		madapter.startDiscovery();
	}

	/**
	 * 配对设备
	 */
	public static void bond() {
		Log.d(TAG, "bond");
		BTManager.getInstance().bond();
	}

	/**
	 * 连接设备
	 */
	public static void connect() {
		BTManager.getInstance().connect();
	}

	/**
	 * 获取绑定设备名称
	 * 
	 * @return
	 */
	public static String getToyName() {
		return BTManager.getInstance().getToyName();
	}

	/**
	 * 返回蓝牙状态：0-蓝牙已打开；1-蓝牙已关闭；2-蓝牙正在打开中；3-蓝牙正在关闭中；4-正在扫描设备中
	 * 
	 * @return
	 */
	public static int getBTState() {
		return BTManager.getInstance().getBTState();
	}

	/**
	 * 返回和玩具的连接状态：0-已配对；1-配对中；2-没有配对；3-已连接；4-连接失败
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
}
