package me.linkcube.library.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ToyService extends Service {
	private IBinder binder;

	@Override
	public void onCreate() {
		binder = new ToyServiceCallImpl();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	@Override
	public void onDestroy() {

	}
}
