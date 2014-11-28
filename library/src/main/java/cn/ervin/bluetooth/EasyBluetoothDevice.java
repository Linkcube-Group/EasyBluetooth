package cn.ervin.bluetooth;

/**
 * Created by Ervin on 14/11/28.
 */
public class EasyBluetoothDevice {

    private String name;

    private String address;

    public EasyBluetoothDevice(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }
}
