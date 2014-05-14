package me.linkcube.library;

import me.linkcube.library.core.bluetooth.BTManager;
import me.linkcube.library.core.bluetooth.BTReceiver;
import me.linkcube.library.service.ToyService;
import me.linkcube.library.service.ToyServiceConnection;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import android.util.Log;

public class LinkcubeBT {
	
	private static String TAG="LinkcubeBT";

	private static BTReceiver receiver;

	public static void onCreate(Activity activity) {
		ToyServiceConnection toyServiceConnection = new ToyServiceConnection();
		Intent toyintent = new Intent(activity, ToyService.class);
		activity.startService(toyintent);
		activity.bindService(toyintent, toyServiceConnection,
				Context.BIND_AUTO_CREATE);
		receiver = new BTReceiver(BTManager.getInstance());
		Log.d(TAG,"onCreate");
	}

	public static void onResume(Activity activity) {
		BTManager.getInstance().regiserReceiver(activity, receiver);
	}

	public static void onDestroy(Activity activity) {
		BTManager.getInstance().unregisterReceiver(activity, receiver);
	}

	public static void startDiscover() {
		Log.d(TAG,"startDiscover");
		BluetoothAdapter madapter = BluetoothAdapter.getDefaultAdapter();
		madapter.startDiscovery();
	}

	public static void bondAndConnect() {
		Log.d(TAG,"bondAndConnect");
		BTManager.getInstance().bondAndConnect();
	}

	public static String getToyName() {
		return BTManager.getInstance().getToyName();
	}

	public static int getBTState() {
		return BTManager.getInstance().getBTState();
	}

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
