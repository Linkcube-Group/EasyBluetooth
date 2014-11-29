package cn.ervin.bluetooth.demo;

import android.bluetooth.BluetoothDevice;

/**
 * 列表中设备被点击后的回调函数
 *
 * @author orange
 */
public interface OnDeviceItemClickListener {

    void onBondDeviceClick(BluetoothDevice device, int position);

    void onConnectDeviceClick(BluetoothDevice device, int position);

}
