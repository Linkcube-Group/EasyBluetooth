package cn.ervin.bluetooth;

public class EasyBluetoothConst {

    public final static String DEVICE_NAME = "DEVICE_NAME";

    public final static String DEVICE_ADDRESS = "DEVICE_ADDRESS";

    private final byte[] CHECK_DATA = {0x35, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x35};

    public static class ConnectState {
        // Constants that indicate the current connection state
        public static final int STATE_NONE = 0;        // we're doing nothing
        public static final int STATE_LISTEN = 1;        // now listening for incoming connections
        public static final int STATE_CONNECTING = 2;    // now initiating an outgoing connection
        public static final int STATE_CONNECTED = 3;    // now connected to a remote device
        public static final int STATE_NULL = -1;        // now service is null
    }

    public static class ConnectMessage {
        // Message types sent from the BluetoothChatService Handler
        public static final int MESSAGE_STATE_CHANGE = 1;
        public static final int MESSAGE_READ = 2;
        public static final int MESSAGE_WRITE = 3;
        public static final int MESSAGE_DEVICE_NAME = 4;
        public static final int MESSAGE_TOAST = 5;

    }

}
