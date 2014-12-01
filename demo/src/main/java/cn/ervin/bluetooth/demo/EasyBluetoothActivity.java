package cn.ervin.bluetooth.demo;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.List;

import cn.ervin.bluetooth.EasyBluetooth;
import cn.ervin.bluetooth.EasyBluetoothConfig;

public class EasyBluetoothActivity extends ActionBarActivity implements EasyBluetooth.OnDeviceDiscoveryListener, EasyBluetooth.OnDeviceConnectListener {

    private final static String TAG = "EasyBluetoothActivity";

    private ToggleButton bluetoothTb;

    private ListView listView;

    private List<BluetoothDevice> deviceList;

    private EasyBluetoothDeviceAdapter deviceAdapter;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EasyBluetoothConfig.FILTER_DEVICES = false;
        setContentView(R.layout.activity_main);
        deviceList = EasyBluetooth.getInstance().getPairedDevices();
        initViews();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EasyBluetooth.getInstance().onStart(this);
        EasyBluetooth.getInstance().setOnDeviceDiscoveryListener(this);
        EasyBluetooth.getInstance().setOnDeviceConnectListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EasyBluetooth.getInstance().onPause(this);
    }

    private void initViews() {

        bluetoothTb = (ToggleButton) findViewById(R.id.bluetooth_tb);
        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(new OnItemClickListener());
//		discoverDevicesBtn = (Button) findViewById(R.id.discover_devices_btn);
//		discoverDevicesBtn.setOnClickListener(this);
//		if (BTHelper.isBluetoothEnabled()) {
//			showBondedDevices();
//		}
//		bluetoothHelpBtn = (Button) findViewById(R.id.bluetooth_help_btn);
//		bluetoothHelpBtn.setOnClickListener(this);
        bluetoothTb.setChecked(EasyBluetooth.getInstance().isBluetoothEnabled());
        bluetoothTb.setOnCheckedChangeListener(switchListener);
        deviceAdapter = new EasyBluetoothDeviceAdapter(this, deviceList);
        listView.setAdapter(deviceAdapter);
    }

    private CompoundButton.OnCheckedChangeListener switchListener = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            EasyBluetooth.getInstance().setBluetoothEnabled(isChecked);
            if (!isChecked) {
                listView.setVisibility(View.INVISIBLE);
            } else {
                listView.setVisibility(View.VISIBLE);
            }
        }
    };


    private class OnItemClickListener implements
            AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            Object object = parent.getAdapter().getItem(position);
            if (object instanceof BluetoothDevice) {
                BluetoothDevice device = (BluetoothDevice) object;
                if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                    connectDevice(device);
                } else {
                    bondDevice(device);
                }
            }
        }

    }


    private void showProgressDialog(int res) {
        if (progressDialog == null)
            progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(res));
        progressDialog.show();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null)
            progressDialog.dismiss();
    }

    private void bondDevice(BluetoothDevice device) {
        if (EasyBluetooth.getInstance().createBond(device)) {
            showProgressDialog(R.string.dialog_bonding_bluetooth);
        } else {
            Log.d(TAG, "绑定拉玩具失败");
            Toast.makeText(this, R.string.toast_toy_unbonded, Toast.LENGTH_SHORT).show();
        }
    }

    private void connectDevice(BluetoothDevice device) {
        EasyBluetooth.getInstance().connect(device);
        showProgressDialog(R.string.toast_connecting_toy);
    }

    private List<BluetoothDevice> filterDevices(BluetoothDevice device) {
        for (int i = 0; i < deviceList.size(); i++) {
            if (deviceList.get(i).getAddress().equals(device.getAddress())) {
                return deviceList;
            }
        }
        deviceList.add(device);
        return deviceList;
    }


    @Override
    public void onDeviceConnected() {
        dismissProgressDialog();
        Toast.makeText(this, R.string.toast_connect_toy_success, Toast.LENGTH_SHORT).show();
        deviceAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDeviceDisconnected() {
        dismissProgressDialog();
        Toast.makeText(this, R.string.toast_disconnect_toy, Toast.LENGTH_SHORT).show();
        deviceAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDeviceConnectFailed() {
        dismissProgressDialog();
        Toast.makeText(this, R.string.toast_connect_toy_failure, Toast.LENGTH_SHORT).show();
        deviceAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDiscoveryOne(BluetoothDevice device) {
        Log.d(TAG, "发现一个设备:" + device.getName());
        filterDevices(device);
        if (deviceList.size() > 0) {
            deviceAdapter.notifyDataSetChanged();
        } else {

        }
    }

    @Override
    public void onDiscoveryFinished() {
        Log.d(TAG, "搜索蓝牙设备完毕！");
        Toast.makeText(this, R.string.toast_searching_toy_over, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, getResources().getString(R.string.toast_searching_toy_over), Toast.LENGTH_SHORT).show();
        deviceAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBluetoothTuringOn() {
        Log.i(TAG, "正在打开蓝牙");
        bluetoothTb.setClickable(false);
        Toast.makeText(this, R.string.toast_open_bluetooth_wait, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBluetoothTuringOff() {
        Log.i(TAG, "正在关闭蓝牙");
        bluetoothTb.setClickable(false);
        Toast.makeText(this, R.string.toast_close_bluetooth_wait, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBluetoothOn() {
        Log.i(TAG, "蓝牙已打开");
        bluetoothTb.setClickable(true);
        Toast.makeText(this, R.string.toast_bluetooth_open, Toast.LENGTH_SHORT).show();
//        showBondedDevices();
        EasyBluetooth.getInstance().startDiscovery();
    }

    @Override
    public void onBluetoothOff() {
        Log.i(TAG, "蓝牙已关闭");
        bluetoothTb.setClickable(true);
        Toast.makeText(this, R.string.toast_bluetooth_closed, Toast.LENGTH_SHORT).show();
        deviceList.clear();
    }

    @Override
    public void onBluetoothBonded() {
        Log.i(TAG, "玩具配对成功");
        progressDialog.dismiss();
        Toast.makeText(this, R.string.toast_bond_toy_success, Toast.LENGTH_SHORT).show();
        deviceAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBluetoothBondNone() {
        Log.d(TAG, "onReceive:bluetooth bond state changed -> " + "BOND_NONE");
        deviceAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBluetoothBonding() {
        Log.d(TAG, "正在绑定玩具");
    }

}
