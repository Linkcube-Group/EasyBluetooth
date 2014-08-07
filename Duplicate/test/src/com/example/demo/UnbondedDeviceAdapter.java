package com.example.demo;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class UnbondedDeviceAdapter extends BaseAdapter {

	private List<String> devices=new ArrayList<String>();
	private Context context;

	public UnbondedDeviceAdapter(Context _context, List<String> _devices) {
		this.devices = _devices;
		this.context = _context;
	}

	@Override
	public int getCount() {
		return devices.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView=LayoutInflater.from(context).inflate(R.layout.listview_item, null);
		TextView deviceNameTv=(TextView)convertView.findViewById(R.id.device_name_tv);
		deviceNameTv.setText(devices.get(position));
		return convertView;
	}
}
