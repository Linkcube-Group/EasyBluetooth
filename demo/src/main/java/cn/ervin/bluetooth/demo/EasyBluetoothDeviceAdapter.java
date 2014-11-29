package cn.ervin.bluetooth.demo;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import cn.ervin.bluetooth.EasyBluetooth;

/**
 * 蓝牙搜索列表适配器
 *
 * @author orange
 */
public class EasyBluetoothDeviceAdapter extends BaseAdapter {

    private Context mContext;

    private List<BluetoothDevice> devices;

    public EasyBluetoothDeviceAdapter(Context context, List<BluetoothDevice> devices) {
        this.mContext = context;
        this.devices = devices;
    }

    @Override
    public int getCount() {
        return devices.size();
    }

    @Override
    public BluetoothDevice getItem(int position) {
        return devices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        EasyBluetoothDeviceCell cell;
        if (convertView == null) {
            cell = new EasyBluetoothDeviceCell(mContext);
        } else {
            cell = (EasyBluetoothDeviceCell) convertView;
        }
        BluetoothDevice device = getItem(position);
        String name = device.getName();
        cell.setDeviceName(name);

        BluetoothDevice connectedDevice = EasyBluetooth.getInstance().getConnectedDevice();
        if (connectedDevice != null && device.getAddress().equals(connectedDevice.getAddress())) {
            cell.setDeviceState(R.string.connected);
        } else {
            if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                cell.setDeviceState(R.string.bonded);
            } else if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                cell.setDeviceState(R.string.unbond);
            } else if (device.getBondState() == BluetoothDevice.BOND_BONDING) {
                cell.setDeviceState(R.string.bonding);
            }
        }

        return cell;
    }

}
