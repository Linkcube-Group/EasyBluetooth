package me.linkcube.library.core.bluetooth;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import me.linkcube.library.core.bluetooth.BTConst.TOY_TYPE;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import static me.linkcube.library.core.bluetooth.BTConst.DEVICE_DEFAULT_NAMES;

public class BTUtils {

	public static boolean isHaveBluetooth() {
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		if (adapter == null)
			return false;
		else
			return true;
	}

	public static boolean isBluetoothEnabled() {
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		return adapter.isEnabled();
	}

	public static void setBluetoothEnabled(boolean enabled) {
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		if (enabled) {
			adapter.enable();
		} else {
			adapter.disable();
		}
	}

	public static List<BluetoothDevice> getBondedDevices() {
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		Set<BluetoothDevice> pairedDevices = adapter.getBondedDevices();
		List<BluetoothDevice> devlist = new ArrayList<BluetoothDevice>();
		for (BluetoothDevice device : pairedDevices) {
			for (int nc = 0; nc < DEVICE_DEFAULT_NAMES.length; nc++) {
				if (device.getName().equalsIgnoreCase(DEVICE_DEFAULT_NAMES[nc])) {
					devlist.add(device);
					break;
				}
			}
		}
		return devlist;
	}

	public static boolean bondDevice(BluetoothDevice dev) {
		Method createBondMethod;
		try {
			createBondMethod = dev.getClass().getMethod("createBond");
			try {
				createBondMethod.invoke(dev);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				return false;
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				return false;
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				return false;
			}
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static boolean isLinkCubeDevice(BluetoothDevice device) {

		for (int nc = 0; nc < DEVICE_DEFAULT_NAMES.length; nc++) {
			if (device.getName() == null) {
				return false;
			}
			if (device.getName().equalsIgnoreCase(DEVICE_DEFAULT_NAMES[nc])) {
				return true;
			}
		}
		return false;
	}

	public static TOY_TYPE getDeviceType(String name) {
		if (name.equals(DEVICE_DEFAULT_NAMES[0])) {
			return TOY_TYPE.MARS;
		} else {
			return TOY_TYPE.VENUS;
		}
	}

	private BTUtils() {

	}

}
