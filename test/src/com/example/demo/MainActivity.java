package com.example.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.example.demo.R;

import me.linkcube.library.core.bluetooth.BTConst;
import me.linkcube.library.core.bluetooth.LinkcubeBT;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {

	private String TAG = "MainActivity";

	private Button openBluetoothBtn, closeBluetoothBtn, startSearchBtn,
			stopSearchBtn, discoveryDeviceBtn, bondDeviceBtn, connectDeviceBtn,
			startLogBtn, stopLogBtn;

	private TextView currentStateTv,toyStateTv;

	private ListView findDeviceLv, bondDeviceLv;

	private UnbondedDeviceAdapter unbondedDeviceAdapter;

	private BondedDeviceAdapter bondedDeviceAdapter;

	private List<String> unbondedDevieNames, bondedDevieNames;

	private int toyState;

	private int btState = -1;

	private static Handler bluetoothHandler = new Handler();
	private static Handler toyHandler = new Handler();

	private Timer logtTimer;

	private boolean isShowUnbonded = false, isShowBonded = false,
			refresh = false;

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

				bluetoothHandler.post(new Runnable() {

					@Override
					public void run() {
						switch (LinkcubeBT.getBTState()) {
						case 0:
							currentStateTv.setText("蓝牙已打开");
							if (!isShowBonded) {
								String string = LinkcubeBT
										.getBondedToyNameList();
								if (string != null) {
									System.out.println(string);
									String[] strings = string.split("\\|");
									for (int i = 0; i < strings.length; i++) {
										bondedDevieNames.add(strings[i]);
										System.out.println(strings[i]);
									}
									bondedDeviceAdapter.notifyDataSetChanged();
									bondDeviceLv.invalidate();
									isShowBonded = true;
								}
							}
							break;
						case 1:
							currentStateTv.setText("蓝牙已关闭");
							if (isShowBonded) {
								bondedDevieNames.clear();
								bondedDeviceAdapter.notifyDataSetChanged();
								bondDeviceLv.invalidate();
								isShowBonded = false;
							}
							if (isShowUnbonded) {
								unbondedDevieNames.clear();
								unbondedDeviceAdapter.notifyDataSetChanged();
								findDeviceLv.invalidate();
								isShowUnbonded = false;
							}
							break;
						case 2:
							currentStateTv.setText("蓝牙正在打开中");
							break;
						case 3:
							currentStateTv.setText("蓝牙正在关闭中");
							break;
						case 4:
							currentStateTv.setText("正在扫描设备中");
							break;
						case 5:
							currentStateTv.setText("发现一个设备");
							if (!isShowUnbonded) {
								String string = LinkcubeBT
										.getUnbondedToyNameList();
								if (string != null) {
									System.out.println(string);
									String[] strings = string.split("\\|");
									for (int i = 0; i < strings.length; i++) {
										unbondedDevieNames.add(strings[i]);
										System.out.println(strings[i]);
									}
									unbondedDeviceAdapter
											.notifyDataSetChanged();
									findDeviceLv.invalidate();
									isShowUnbonded = true;
								}
							}
							break;
						case 6:
							currentStateTv.setText("扫描设备过程结束");
							break;
						default:
							break;
						}
					}
				});

				toyHandler.post(new Runnable() {

					@Override
					public void run() {
						System.out.println("LinkcubeBT.getToyState():"+LinkcubeBT.getToyState());
						switch (LinkcubeBT.getToyState()) {
						case 0:
							toyStateTv.setText("配对成功");
							if (!refresh) {
								refresh();
								refresh = true;
							}
							break;
						case 1:
							toyStateTv.setText("正在配对中");
							break;
						case 2:
							//toyStateTv.setText("没有配对");
							break;
						case 3:
							toyStateTv.setText("已连接");
							break;
						case 4:
							toyStateTv.setText("连接失败");
							break;
						case 5:
							toyStateTv.setText("连接失败");
							break;

						default:
							break;
						}
					}
				});

			}

		}, 0, 500);

	}

	private void initView() {
		currentStateTv = (TextView) findViewById(R.id.bluetooth_state);
		toyStateTv = (TextView) findViewById(R.id.toy_state);
		openBluetoothBtn = (Button) findViewById(R.id.open_bluetooth_btn);
		openBluetoothBtn.setOnClickListener(this);
		closeBluetoothBtn = (Button) findViewById(R.id.close_bluetoth_btn);
		closeBluetoothBtn.setOnClickListener(this);
		startSearchBtn = (Button) findViewById(R.id.start_search_btn);
		startSearchBtn.setOnClickListener(this);
		stopSearchBtn = (Button) findViewById(R.id.stop_search_btn);
		stopSearchBtn.setOnClickListener(this);
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
		findDeviceLv = (ListView) findViewById(R.id.find_device_lv);
		unbondedDevieNames = new ArrayList<String>();
		unbondedDeviceAdapter = new UnbondedDeviceAdapter(this,
				unbondedDevieNames);
		findDeviceLv.setAdapter(unbondedDeviceAdapter);
		findDeviceLv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int arg2,
					long arg3) {
				TextView unbondDeviceNameTv = (TextView) view
						.findViewById(R.id.device_name_tv);
				String[] deviceNameAndAddress = unbondDeviceNameTv.getText()
						.toString().split("@");
				String deviceAddress = deviceNameAndAddress[1];
				System.out.println("address:" + deviceAddress);
				bond(deviceAddress);
			}
		});
		bondDeviceLv = (ListView) findViewById(R.id.bond_device_lv);
		bondedDevieNames = new ArrayList<String>();
		bondedDeviceAdapter = new BondedDeviceAdapter(this, bondedDevieNames);
		bondDeviceLv.setAdapter(bondedDeviceAdapter);
		bondDeviceLv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int arg2,
					long arg3) {
				TextView unbondDeviceNameTv = (TextView) view
						.findViewById(R.id.device_name_tv);
				String[] deviceNameAndAddress = unbondDeviceNameTv.getText()
						.toString().split("@");
				String deviceAddress = deviceNameAndAddress[1];
				System.out.println("address:" + deviceAddress);
				connect(deviceAddress);
			}
		});
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
		case R.id.open_bluetooth_btn:

			setBluetoothEnabled(true);

			break;
		case R.id.close_bluetoth_btn:

			setBluetoothEnabled(false);

			break;

		case R.id.start_search_btn:
			unbondedDevieNames.clear();
			unbondedDeviceAdapter.notifyDataSetChanged();
			findDeviceLv.invalidate();
			isShowUnbonded = false;
			startDiscovery();

			break;

		case R.id.stop_search_btn:

			cancelDiscovery();

			break;

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
	 * 打开或者关闭蓝牙
	 * 
	 * @param enabled
	 */
	private void setBluetoothEnabled(boolean enabled) {
		LinkcubeBT.setBluetoothEnable(enabled);
	}

	/**
	 * 发现设备
	 */
	private void startDiscovery() {
		LinkcubeBT.startDiscovery();
	}

	private void cancelDiscovery() {
		LinkcubeBT.cancelDiscovery();
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

	private void getCommond() {
		Thread thread = new Thread() {

			@Override
			public void run() {
				String toyCommond = LinkcubeBT.getCommond();
				// System.out.println("toyCommond:"+toyCommond);
			}

		};
		thread.start();
	}

	public void refresh() {
		bondedDevieNames.clear();
		unbondedDevieNames.clear();
		
		String string = LinkcubeBT
				.getBondedToyNameList();
		if (string != null) {
			System.out.println(string);
			String[] strings = string.split("\\|");
			for (int i = 0; i < strings.length; i++) {
				bondedDevieNames.add(strings[i]);
				System.out.println(strings[i]);
			}
			bondedDeviceAdapter.notifyDataSetChanged();
			bondDeviceLv.invalidate();
		}
		
		String string2 = LinkcubeBT
				.getUnbondedToyNameList();
		if (string2 != null) {
			System.out.println(string2);
			String[] strings = string2.split("\\|");
			for (int i = 0; i < strings.length; i++) {
				unbondedDevieNames.add(strings[i]);
				System.out.println(strings[i]);
			}
			unbondedDeviceAdapter
					.notifyDataSetChanged();
			findDeviceLv.invalidate();
		}
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
						getCommond();
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
