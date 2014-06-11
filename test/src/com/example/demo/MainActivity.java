package com.example.demo;

import java.util.Timer;
import java.util.TimerTask;

import com.example.demo.R;
import com.unity3d.player.UnityPlayerActivity;
import com.unity3d.player.UnityPlayerNativeActivity;

import me.linkcube.library.core.bluetooth.BTConst;
import me.linkcube.library.core.bluetooth.BTManager;
import me.linkcube.library.core.bluetooth.LinkcubeBT;
import android.R.integer;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {

	private String TAG = "MainActivity";

	private Button discoveryDeviceBtn;

	private Button bondDeviceBtn;

	private Button connectDeviceBtn;

	private Button startLogBtn;

	private Button stopLogBtn;

	private TextView nameTextView;

	private int toyState;

	private int btState = -1;

	private static Handler handler = new Handler();

	private Timer logtTimer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LinkcubeBT.onCreate(this);
		setContentView(R.layout.activity_main);

		initView();

		final Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {

				if (LinkcubeBT.getBTState() == BTConst.BT_STATE.DISCOVER_ONE) {
					handler.post(new Runnable() {

						@Override
						public void run() {
							nameTextView.setText("发现设备-未绑定列表："
									+ LinkcubeBT.getUnbondedToyNameList());
							nameTextView.setText("发现设备-绑定列表："
									+ LinkcubeBT.getUnbondedToyNameList());
						}
					});

				}
				if (LinkcubeBT.getToyState() == BTConst.TOY_STATE.CONNECTED) {

					handler.post(new Runnable() {

						@Override
						public void run() {
							nameTextView.setText(LinkcubeBT
									.getConnectedDevice() + "->已连接");
							LinkcubeBT.setCommond();
						}
					});
				}

			}
		}, 1000, 2000);

	}

	private void initView() {
		nameTextView = (TextView) findViewById(R.id.name);
		discoveryDeviceBtn = (Button) findViewById(R.id.discovery_device_btn);
		discoveryDeviceBtn.setOnClickListener(this);
		bondDeviceBtn = (Button) findViewById(R.id.bond_device_btn);
		bondDeviceBtn.setOnClickListener(this);
		connectDeviceBtn = (Button) findViewById(R.id.connect_device_btn);
		connectDeviceBtn.setOnClickListener(this);
		startLogBtn = (Button) findViewById(R.id.start_log);
		startLogBtn.setOnClickListener(this);
		stopLogBtn = (Button) findViewById(R.id.stop_log);
		stopLogBtn.setOnClickListener(this);
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.discovery_device_btn:
			startDiscovery();
			break;

		case R.id.bond_device_btn:
			// bond();
			break;

		case R.id.connect_device_btn:
			// connect();
			break;

		case R.id.start_log:
			startBtLog();
			break;

		case R.id.stop_log:
			stopBtLog();
			break;

		default:
			break;
		}
	}

	/**
	 * 发现设备
	 */
	private void startDiscovery() {
		LinkcubeBT.startDiscover();
	}

	/**
	 * 绑定设备
	 */
	private void bond(String address) {
		LinkcubeBT.bond(address);
	}

	/**
	 * 连接设备
	 */
	private void connect(String address) {
		LinkcubeBT.connect(address);
	}

	/**
	 * 打开或者关闭蓝牙
	 * 
	 * @param enabled
	 */
	private void setBluetoothEnabled(boolean enabled) {
		LinkcubeBT.setBluetoothEnable(enabled);
	}

	private void startBtLog() {
		if (logtTimer == null) {
			logtTimer = new Timer();
			logtTimer.schedule(new TimerTask() {

				@Override
				public void run() {
					int btStateLog = LinkcubeBT.getBTState();
					int toyStateLog = LinkcubeBT.getToyState();
					if (btStateLog == BTConst.BT_STATE.ON) {
						Log.d(TAG, "btState:" + btStateLog + "--蓝牙已打开");
					} else if (btStateLog == BTConst.BT_STATE.OFF) {
						Log.d(TAG, "btState:" + btStateLog + "--蓝牙已关闭");
					} else if (btStateLog == BTConst.BT_STATE.TURNING_ON) {
						Log.d(TAG, "btState:" + btStateLog + "--蓝牙正在打开");
					} else if (btStateLog == BTConst.BT_STATE.TURNING_OFF) {
						Log.d(TAG, "btState:" + btStateLog + "--蓝牙正在关闭");
					} else if (btStateLog == BTConst.BT_STATE.DISCOVERING) {
						Log.d(TAG, "btState:" + btStateLog + "--正在发现设备");
					} else if (btStateLog == BTConst.BT_STATE.DISCOVER_ONE) {
						Log.d(TAG, "btState:" + btStateLog + "--发现一个设备");
					}

					if (toyStateLog == BTConst.TOY_STATE.BOND_NONE) {
						Log.i(TAG, "toyState:" + toyStateLog + "--未绑定玩具");
					} else if (toyStateLog == BTConst.TOY_STATE.BONDING) {
						Log.i(TAG, "toyState:" + toyStateLog + "--正在绑定玩具");
					} else if (toyStateLog == BTConst.TOY_STATE.BONDED) {
						Log.i(TAG, "toyState:" + toyStateLog + "--绑定玩具成功");
					} else if (toyStateLog == BTConst.TOY_STATE.CONNECTING) {
						Log.i(TAG, "toyState:" + toyStateLog + "--正在连接玩具");
					} else if (toyStateLog == BTConst.TOY_STATE.CONNECTED) {
						Log.i(TAG, "toyState:" + toyStateLog + "--连接玩具成功");
					}
				}
			}, 0, 2000);
		}
	}

	private void stopBtLog() {
		if (logtTimer != null) {
			logtTimer.cancel();
			logtTimer = null;
		}

	}

}
