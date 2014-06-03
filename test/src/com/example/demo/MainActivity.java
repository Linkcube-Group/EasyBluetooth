package com.example.demo;

import java.util.Timer;
import java.util.TimerTask;

import com.example.demo.R;
import com.unity3d.player.UnityPlayerActivity;
import com.unity3d.player.UnityPlayerNativeActivity;

import me.linkcube.library.LinkcubeBT;
import me.linkcube.library.core.bluetooth.BTConst;
import me.linkcube.library.core.bluetooth.BTManager;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

	private String TAG = "MainActivity";

	private Button connectBtn;

	private TextView nameTextView;

	private int toyState;

	private int btState;

	private static Handler handler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LinkcubeBT.onCreate(this);
		setContentView(R.layout.activity_main);
		startDiscovery();
		connectBtn = (Button) findViewById(R.id.connect);
		nameTextView = (TextView) findViewById(R.id.name);
		connectBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				btState = BTManager.getInstance().getBTState();
				Log.d(TAG, "BtState:" + btState);
				if (btState == BTConst.BT_STATE.DISCOVER_ONE) {
					bond();
				}
			}
		});

		final Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {

				if (LinkcubeBT.getBTState() == BTConst.BT_STATE.DISCOVER_ONE) {
					handler.post(new Runnable() {

						@Override
						public void run() {
							nameTextView.setText(LinkcubeBT.getToyName());

						}
					});

				}
				if (LinkcubeBT.getToyState() == BTConst.TOY_STATE.CONNECTED) {

					handler.post(new Runnable() {

						@Override
						public void run() {
							nameTextView.setText(LinkcubeBT.getToyName()
									+ "->已连接");
							LinkcubeBT.setCommond();
						}
					});
				}

			}
		}, 1000, 2000);

	}

	@Override
	protected void onResume() {
		super.onResume();
		LinkcubeBT.onResume(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LinkcubeBT.onDestroy(this);
	}

	/**
	 * 绑定设备
	 */
	private void bond() {
		LinkcubeBT.bond();
	}

	/**
	 * 连接设备
	 */
	private void connect() {
		LinkcubeBT.connect();
	}

	/**
	 * 打开或者关闭蓝牙
	 * 
	 * @param enabled
	 */
	private void setBluetoothEnabled(boolean enabled) {
		LinkcubeBT.setBluetoothEnable(enabled);
	}

	/**
	 * 需要发现设备
	 */
	private void startDiscovery() {
		LinkcubeBT.startDiscover();
	}

}
