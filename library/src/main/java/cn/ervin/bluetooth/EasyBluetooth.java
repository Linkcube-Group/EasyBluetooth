package cn.ervin.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static android.bluetooth.BluetoothAdapter.ACTION_DISCOVERY_FINISHED;
import static android.bluetooth.BluetoothAdapter.ACTION_STATE_CHANGED;
import static android.bluetooth.BluetoothAdapter.EXTRA_STATE;
import static android.bluetooth.BluetoothAdapter.STATE_OFF;
import static android.bluetooth.BluetoothAdapter.STATE_ON;
import static android.bluetooth.BluetoothAdapter.STATE_TURNING_OFF;
import static android.bluetooth.BluetoothAdapter.STATE_TURNING_ON;
import static android.bluetooth.BluetoothDevice.ACTION_BOND_STATE_CHANGED;
import static android.bluetooth.BluetoothDevice.ACTION_FOUND;
import static android.bluetooth.BluetoothDevice.BOND_BONDED;
import static android.bluetooth.BluetoothDevice.BOND_BONDING;
import static android.bluetooth.BluetoothDevice.BOND_NONE;
import static cn.ervin.bluetooth.EasyBluetoothConst.ConnectMessage.MESSAGE_DEVICE_NAME;
import static cn.ervin.bluetooth.EasyBluetoothConst.ConnectMessage.MESSAGE_READ;
import static cn.ervin.bluetooth.EasyBluetoothConst.ConnectMessage.MESSAGE_STATE_CHANGE;
import static cn.ervin.bluetooth.EasyBluetoothConst.ConnectMessage.MESSAGE_TOAST;
import static cn.ervin.bluetooth.EasyBluetoothConst.ConnectMessage.MESSAGE_WRITE;
import static cn.ervin.bluetooth.EasyBluetoothConst.ConnectState.STATE_CONNECTED;
import static cn.ervin.bluetooth.EasyBluetoothConst.ConnectState.STATE_CONNECTING;
import static cn.ervin.bluetooth.EasyBluetoothConst.ConnectState.STATE_NONE;

public class EasyBluetooth {

    private final static String TAG = "EasyBluetooth";

    private static EasyBluetooth instance;

    private DeviceBroadcastReceiver receiver;

    private EasyBluetoothService service = null;

    private BluetoothAdapter mBluetoothAdapter;

    private boolean isAutoConnecting = false;

    private boolean isAutoConnectionEnabled = false;

    private boolean isConnected = false;

    private boolean isConnecting = false;

    private boolean isServiceRunning = false;

    private String keyword;

    /**
     * Count of devices which are to connect automatically
     */
    private int autoDevicesCount = 0;

    private OnBluetoothConnectionListener bcl;

    private OnBluetoothConnectionListener onBluetoothConnectionListener;

    private OnAutoConnectionListener onAutoConnectionListener;

    private OnDataReceivedListener onDataReceivedListener;

    private OnDeviceDiscoveryListener onDeviceDiscoveryListener;

    public static EasyBluetooth getInstance() {
        if (instance == null)
            instance = new EasyBluetooth();
        return instance;
    }

    private EasyBluetooth() {
        receiver = new DeviceBroadcastReceiver();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public void onStart(Activity activity) {
        registerReceiver(activity);

        if (service == null)
            service = new EasyBluetoothService(activity, mHandler);
    }

    public void onPause(Activity activity) {
        unregisterReceiver(activity);
    }

    public void startDiscovery() {
        mBluetoothAdapter.startDiscovery();
    }

    public boolean isDiscovery() {
        return mBluetoothAdapter.isDiscovering();
    }

    public void cancelDiscovery() {
        mBluetoothAdapter.cancelDiscovery();
    }

    public boolean isBluetoothEnabled() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        return adapter.isEnabled();
    }

    public void setBluetoothEnabled(boolean enabled) {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (enabled) {
            adapter.enable();
        } else {
            adapter.disable();
        }
    }

    public boolean isAutoConnecting() {
        return isAutoConnecting;
    }

    public String[] getPairedDeviceName() {
        int c = 0;
        Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
        String[] name_list = new String[devices.size()];
        for (BluetoothDevice device : devices) {
            name_list[c] = device.getName();
            c++;
        }
        return name_list;
    }

    public String[] getPairedDeviceAddress() {
        int c = 0;
        Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
        String[] address_list = new String[devices.size()];
        for (BluetoothDevice device : devices) {
            address_list[c] = device.getAddress();
            c++;
        }
        return address_list;
    }

    public List<BluetoothDevice> getPairedDevices() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = adapter.getBondedDevices();
        List<BluetoothDevice> devices = new ArrayList<BluetoothDevice>();
        if (!EasyBluetoothConfig.FILTER_DEVICES)
            for (BluetoothDevice device : pairedDevices) {
                if (EasyBluetoothUtils.isTargetDevice(device)) {
                    devices.add(device);
                    break;
                }
            }
        return devices;
    }

    public boolean createBond(BluetoothDevice btDevice) {
        Method createBondMethod = null;
        try {
            createBondMethod = btDevice.getClass().getMethod("createBond");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return false;
        }
        Boolean returnValue = null;
        try {
            returnValue = (Boolean) createBondMethod.invoke(btDevice);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return false;
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            return false;
        }
        return returnValue.booleanValue();
    }

    public boolean removeBond(BluetoothDevice btDevice) {
        Method removeBondMethod = null;
        try {
            removeBondMethod = btDevice.getClass().getMethod("removeBond");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return false;
        }
        Boolean returnValue = null;
        try {
            returnValue = (Boolean) removeBondMethod.invoke(btDevice);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return false;
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            return false;
        }
        return returnValue.booleanValue();
    }

    public boolean setPin(BluetoothDevice btDevice, String str) {
        Method removeBondMethod = null;
        try {
            removeBondMethod = btDevice.getClass().getDeclaredMethod("setPin",
                    new Class[]{byte[].class});
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return false;
        }
        Boolean returnValue = null;
        try {
            returnValue = (Boolean) removeBondMethod.invoke(btDevice, new Object[]{str.getBytes()});
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return false;
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            return false;
        }
        Log.e("returnValue", "" + returnValue);
        return returnValue.booleanValue();

    }

    // 取消用户输入
    public boolean cancelPairingUserInput(BluetoothDevice device) {
        Method createBondMethod = null;
        try {
            createBondMethod = device.getClass().getMethod("cancelPairingUserInput");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return false;
        }
        // cancelBondProcess()
        Boolean returnValue = null;
        try {
            returnValue = (Boolean) createBondMethod.invoke(device);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return false;
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            return false;
        }
        return returnValue.booleanValue();
    }

    // 取消配对
    boolean cancelBondProcess(BluetoothDevice device) {
        Method createBondMethod = null;
        try {
            createBondMethod = device.getClass().getMethod("cancelBondProcess");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return false;
        }
        Boolean returnValue = null;
        try {
            returnValue = (Boolean) createBondMethod.invoke(device);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return false;
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            return false;
        }
        return returnValue.booleanValue();
    }

    public void connect(BluetoothDevice device) {
        service.connect(device);
    }

    public void connect(String address) {
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        service.connect(device);
    }

    public void disconnect() {
        if (service != null) {
            isServiceRunning = false;
            service.stop();
            if (service.getState() == STATE_NONE) {
                isServiceRunning = true;
                service.start();
            }
        }
    }

    public void autoConnect(String keywordName) {
        if (!isAutoConnectionEnabled) {
            keyword = keywordName;
            isAutoConnectionEnabled = true;
            isAutoConnecting = true;
            if (onAutoConnectionListener != null)
                onAutoConnectionListener.onAutoConnectionStarted();
            final ArrayList<String> arr_filter_address = new ArrayList<String>();
            final ArrayList<String> arr_filter_name = new ArrayList<String>();
            String[] arr_name = getPairedDeviceName();
            String[] arr_address = getPairedDeviceAddress();
            for (int i = 0; i < arr_name.length; i++) {
                if (arr_name[i].contains(keywordName)) {
                    arr_filter_address.add(arr_address[i]);
                    arr_filter_name.add(arr_name[i]);
                }
            }

            bcl = new OnBluetoothConnectionListener() {
                public void onDeviceConnected(String name, String address) {
                    bcl = null;
                    isAutoConnecting = false;
                }

                public void onDeviceDisconnected() {
                }

                public void onDeviceConnectionFailed() {
                    Log.e("CHeck", "Failed");
                    if (isServiceRunning) {
                        if (isAutoConnectionEnabled) {
                            autoDevicesCount++;
                            if (autoDevicesCount >= arr_filter_address.size())
                                autoDevicesCount = 0;
                            connect(arr_filter_address.get(autoDevicesCount));
                            Log.e("CHeck", "Connect");
                            if (onAutoConnectionListener != null)
                                onAutoConnectionListener.onNewConnection(arr_filter_name.get(autoDevicesCount)
                                        , arr_filter_address.get(autoDevicesCount));
                        } else {
                            bcl = null;
                            isAutoConnecting = false;
                        }
                    }
                }
            };

            setOnBluetoothConnectionListener(bcl);
            autoDevicesCount = 0;
            if (onAutoConnectionListener != null)
                onAutoConnectionListener.onNewConnection(arr_name[autoDevicesCount], arr_address[autoDevicesCount]);
            if (arr_filter_address.size() > 0)
                connect(arr_filter_address.get(autoDevicesCount));
            else
                Log.d(TAG, "Device name doesn't match the given names");
        }
    }

    public void stopAutoConnect() {
        isAutoConnectionEnabled = false;
    }

    private void registerReceiver(Activity activity) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_FOUND);
        filter.addAction(ACTION_BOND_STATE_CHANGED);
        filter.addAction(ACTION_DISCOVERY_FINISHED);
        filter.addAction(ACTION_STATE_CHANGED);
        activity.registerReceiver(receiver, filter);
    }

    private void unregisterReceiver(Activity activity) {
        activity.unregisterReceiver(receiver);
    }

    private final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_WRITE:
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    String readMessage = new String(readBuf);
                    if (readBuf != null && readBuf.length > 0) {
                        if (onDataReceivedListener != null)
                            onDataReceivedListener.onDataReceived(readBuf, readMessage);
                    }
                    break;
                case MESSAGE_DEVICE_NAME:
                    String mDeviceName = msg.getData().getString(EasyBluetoothConst.DEVICE_NAME);
                    String mDeviceAddress = msg.getData().getString(EasyBluetoothConst.DEVICE_ADDRESS);
                    if (onBluetoothConnectionListener != null)
                        onBluetoothConnectionListener.onDeviceConnected(mDeviceName, mDeviceAddress);
                    isConnected = true;
                    break;
                case MESSAGE_TOAST:
                    break;
                case MESSAGE_STATE_CHANGE:
                    if (isConnected && msg.arg1 != STATE_CONNECTED) {
                        if (onBluetoothConnectionListener != null)
                            onBluetoothConnectionListener.onDeviceDisconnected();
                        if (isAutoConnectionEnabled) {
                            isAutoConnectionEnabled = false;
                            autoConnect(keyword);
                        }
                        isConnected = false;
                        mDeviceName = null;
                        mDeviceAddress = null;
                    }

                    if (!isConnecting && msg.arg1 == STATE_CONNECTING) {
                        isConnecting = true;
                    } else if (isConnecting) {
                        if (msg.arg1 != STATE_CONNECTED) {
                            if (onBluetoothConnectionListener != null)
                                onBluetoothConnectionListener.onDeviceConnectionFailed();
                        }
                        isConnecting = false;
                    }
                    break;
            }
        }
    };

    private class DeviceBroadcastReceiver extends BroadcastReceiver {

        private String TAG = "DeviceBroadcastReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //Bluetooth state changed
            if (ACTION_STATE_CHANGED.equals(action)) {
                int state = intent.getIntExtra(EXTRA_STATE, -1);
                Log.d(TAG, "ACTION_STATE_CHANGED : " + state);
                if (onDeviceDiscoveryListener != null) {
                    switch (state) {
                        case STATE_TURNING_ON:
                            onDeviceDiscoveryListener.onBluetoothTuringOn();
                            break;
                        case STATE_ON:
                            onDeviceDiscoveryListener.onBluetoothOn();
                            break;
                        case STATE_TURNING_OFF:
                            onDeviceDiscoveryListener.onBluetoothTuringOff();
                            break;
                        case STATE_OFF:
                            onDeviceDiscoveryListener.onBluetoothOff();
                            break;
                        default:
                            break;
                    }
                }

            }

            //扫描设备时候的变化
            if (ACTION_FOUND.equals(action)) {
                Log.d(TAG, "discover a device");
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (EasyBluetoothUtils.isTargetDevice(device)) {
                    if (BOND_BONDED != device.getBondState()) {
                        Log.d(TAG,
                                "discover a device that is not bonded");
                        if (onDeviceDiscoveryListener != null)
                            onDeviceDiscoveryListener.onDiscoveryOne(device);
                    }
                }

            }

            //扫描设备时候的变化
            if (ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.d(TAG, "discovery finished");
                if (onDeviceDiscoveryListener != null)
                    onDeviceDiscoveryListener.onDiscoveryFinished();
            }


            //绑定蓝牙设备是时候的状态变化
            if (ACTION_BOND_STATE_CHANGED.equals(action)) {
                Log.d(TAG, "device bond state changed");
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (EasyBluetoothUtils.isTargetDevice(device)) {
                    int bondState = device.getBondState();
                    if (onDeviceDiscoveryListener != null) {
                        switch (bondState) {
                            //Indicates the remote device is not bonded (paired).
                            case BOND_NONE:
                                onDeviceDiscoveryListener.onBluetoothBondNone();
                                break;
                            //Indicates bonding (pairing) is in progress with the remote device.
                            case BOND_BONDING:
                                onDeviceDiscoveryListener.onBluetoothBonding();
                                break;
                            //Indicates the remote device is bonded (paired).
                            case BOND_BONDED:
                                onDeviceDiscoveryListener.onBluetoothBonded();
                                break;
                            default:
                                break;
                        }
                    }
                }
            }


        }

    }


    public void setOnDeviceDiscoveryListener(OnDeviceDiscoveryListener listener) {
        this.onDeviceDiscoveryListener = listener;
    }

    public void setOnBluetoothConnectionListener(OnBluetoothConnectionListener listener) {
        this.onBluetoothConnectionListener = listener;
    }

    public void setOnAutoConnectionListener(OnAutoConnectionListener listener) {
        this.onAutoConnectionListener = listener;
    }

    public void setOnDataReceivedListener(OnDataReceivedListener listener) {
        this.onDataReceivedListener = listener;
    }

    public interface OnDataReceivedListener {
        public void onDataReceived(byte[] data, String message);
    }

    public interface OnBluetoothConnectionListener {

        public void onDeviceConnected(String name, String address);

        public void onDeviceDisconnected();

        public void onDeviceConnectionFailed();
    }

    public interface OnAutoConnectionListener {

        public void onAutoConnectionStarted();

        public void onNewConnection(String name, String address);
    }

    /**
     * 搜索蓝牙设备的回调接口
     *
     * @author Ervin
     */
    public interface OnDeviceDiscoveryListener {

        /**
         * 发现一个设备的回调接口
         */
        void onDiscoveryOne(BluetoothDevice device);

        /**
         * 查找蓝牙设备完毕的回调接口
         */
        void onDiscoveryFinished();

        /**
         * 蓝牙正在打开
         */
        void onBluetoothTuringOn();

        /**
         * 蓝牙正在关闭
         */
        void onBluetoothTuringOff();

        /**
         * 蓝牙已经打开
         */
        void onBluetoothOn();

        /**
         * 蓝牙已经关闭
         */
        void onBluetoothOff();

        /**
         * 绑定设备成功
         */
        void onBluetoothBonded();

        /**
         * 没有绑定设备
         */
        void onBluetoothBondNone();

        /**
         * 正在绑定蓝牙设备
         */
        void onBluetoothBonding();

    }

}
