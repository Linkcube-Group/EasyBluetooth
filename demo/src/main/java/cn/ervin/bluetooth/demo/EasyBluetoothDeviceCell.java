package cn.ervin.bluetooth.demo;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 蓝牙列表中每一个单元视图
 *
 * @author orange
 */
public class EasyBluetoothDeviceCell extends LinearLayout {

    private TextView deviceNameTv;

    private TextView deviceStateTv;

    public EasyBluetoothDeviceCell(Context context) {
        super(context);
        init(context);
    }

    public EasyBluetoothDeviceCell(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = mInflater.inflate(R.layout.bt_device_listview_cell,
                this, true);
        deviceNameTv = (TextView) view.findViewById(R.id.device_name_tv);
        deviceStateTv = (TextView) view.findViewById(R.id.device_state_tv);
    }

    public void setDeviceName(int resId) {
        deviceNameTv.setText(resId);
    }

    public void setDeviceName(String name) {
        deviceNameTv.setText(name);
    }

    public void setDeviceState(int resId) {
        deviceStateTv.setText(resId);
    }

    public void setDeviceState(String state) {
        deviceStateTv.setText(state);
    }

}
