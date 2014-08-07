package me.linkcube.library.service;

import me.linkcube.library.core.bluetooth.BTManager;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

/**
 * 玩具启动服务与activity通信
 * 
 * 
 */
public class ToyServiceConnection implements ServiceConnection {

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		BTManager.toyServiceCall = (IToyServiceCall) service;
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		BTManager.toyServiceCall = null;
	}
}
