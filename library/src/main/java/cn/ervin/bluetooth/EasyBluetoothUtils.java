package cn.ervin.bluetooth;

import android.bluetooth.BluetoothDevice;

import static cn.ervin.bluetooth.EasyBluetoothConfig.DEFAULT_DEVICE_NAMES;

/**
 * Created by Ervin on 14/11/28.
 */
public class EasyBluetoothUtils {

    public static boolean isTargetDevice(BluetoothDevice device) {

        for (int i = 0; i < DEFAULT_DEVICE_NAMES.length; i++) {
            if (device.getName() == null) {
                return false;
            }
            if (device.getName().equalsIgnoreCase(DEFAULT_DEVICE_NAMES[i])) {
                return true;
            }
        }

        return false;
    }

    public static String bytesToHexString(byte[] buffer) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (buffer == null || buffer.length <= 0) {
            return null;
        }
        for (int i = 0; i < buffer.length; i++) {
            int v = buffer[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

}
