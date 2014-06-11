package me.linkcube.library.core.bluetooth;

public class BTConst {

	protected final static String[] DEVICE_DEFAULT_NAMES = { "Mars", "Venus",
			"linkcube" };

	protected enum TOY_TYPE {
		MARS, VENUS, LINKCUBE
	}

	public static class BT_STATE {

		public static int ON = 0;

		public static int OFF = 1;

		public static int TURNING_ON = 2;

		public static int TURNING_OFF = 3;

		public static int DISCOVERING = 4;

		public static int DISCOVER_ONE = 5;

		public static int DISCOVER_FINISHED = 6;

	}

	public static class TOY_STATE {

		public static int BONDED = 0;

		public static int BONDING = 1;

		public static int BOND_NONE = 2;

		public static int CONNECTED = 3;

		public static int CONNECTING = 4;

		public static int CONNECT_FAIL = 5;

		public static int INTERRUPTED = 6;
	}

}
