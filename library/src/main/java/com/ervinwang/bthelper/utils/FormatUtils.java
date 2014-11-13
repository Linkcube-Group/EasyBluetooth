package com.ervinwang.bthelper.utils;

public class FormatUtils {

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

	private FormatUtils() {

	}

}
