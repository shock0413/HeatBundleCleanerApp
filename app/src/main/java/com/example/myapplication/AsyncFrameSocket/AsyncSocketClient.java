package com.example.myapplication.AsyncFrameSocket;

import static android.content.ContentValues.TAG;

import android.util.Log;
import com.example.myapplication.BitConverter;
import com.example.myapplication.LogManager;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.ArrayList;

public class AsyncSocketClient extends AsyncSocketClass {
    private AsynchronousSocketChannel conn = null;
    private final int BUF_SIZE = 8192;
    // private final int BUF_SIZE = 327680;
    // private final int BUF_SIZE = 65535;
    private ByteBuffer buffer = null;
    private ArrayList<Byte> receiveData = new ArrayList<Byte>();
    private boolean isConnected = false;

    public AsyncSocketClient(int id) {
        this.id = id;
    }

    public AsyncSocketClient(int id, AsynchronousSocketChannel conn) {
        this.id = id;
        this.conn = conn;
    }

    public AsynchronousSocketChannel Connection = this.conn;

    public void setConnection(AsynchronousSocketChannel value) {
        this.conn = value;
    }

    public AsynchronousSocketChannel getConnection() {
        return this.conn;
    }

    public boolean isConnected() {
        return this.isConnected;
    }

    public void connect(String hostAddress, int port) {
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    SocketAddress socketAddress = new InetSocketAddress(hostAddress, port);

                    try {
                        /*
                        AsynchronousChannelGroup channelGroup = AsynchronousChannelGroup.withFixedThreadPool(
                                Runtime.getRuntime().availableProcessors(),
                                Executors.defaultThreadFactory()
                        );
                        */

                        conn = AsynchronousSocketChannel.open();
                        conn.setOption(StandardSocketOptions.TCP_NODELAY, true);
                        conn.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
                        // conn.setOption(StandardSocketOptions.SO_LINGER, 5);
                        conn.connect(socketAddress, null, onConnectCallBack);
                    } catch (Exception e) {
                        isConnected = false;
                    }
                }
            }).start();
        } catch (Exception e) {
            LogManager.e("[AsyncSocketClient] [connect] " + e.getMessage());

            AsyncSocketErrorEventArgs eev = new AsyncSocketErrorEventArgs(id, e);

            errorOccured(eev);
        }
    }

    private CompletionHandler<Void, Object> onConnectCallBack = new CompletionHandler<Void, Object>() {
        @Override
        public void completed(Void result, Object attachment) {
            try {
                isConnected = true;

                Log.i(TAG, "비동기 연결 성공");

                receive();

                AsyncSocketConnectionEventArgs cev = new AsyncSocketConnectionEventArgs(id);
                connected(cev);
            } catch (Exception e) {
                LogManager.e("[AsyncSocketClient] [onConnectCallBack] " + e.getMessage());

                AsyncSocketErrorEventArgs eev = new AsyncSocketErrorEventArgs(id, e);

                errorOccured(eev);

                isConnected = false;
            }
        }

        @Override
        public void failed(Throwable exc, Object attachment) {
            Log.i(TAG, "비동기 연결 실패");
            isConnected = false;

            try
            {
                conn.close();
                conn = null;
            }
            catch (Exception e) {

            }
        }
    };

    private void receive() {
        try {
            this.buffer = ByteBuffer.allocate(BUF_SIZE);
            conn.read(buffer, (Object)conn, onReceiveCallBack);
        } catch (Exception e) {
            LogManager.e("[AsyncSocketClient] [receive] " + e.getMessage());

            AsyncSocketErrorEventArgs eev = new AsyncSocketErrorEventArgs(id, e);

            errorOccured(eev);
        }
    }

    private CompletionHandler<Integer, Object> onReceiveCallBack = new CompletionHandler<Integer, Object>() {
        @Override
        public void completed(Integer result, Object attachment) {
            try {
                if (result < 0) {
                    throw new Exception();
                } else {
                    byte[] bytes = buffer.array();

                    for (int i = 0; i < result; i++) {
                        receiveData.add(bytes[i]);
                    }

                    receiveData(result);

                    receive();
                }
            } catch (Exception e) {
                LogManager.e("[AsyncSocketClient] [onReceiveCallBack] " + e.getMessage());

                AsyncSocketErrorEventArgs eev = new AsyncSocketErrorEventArgs(id, e);

                errorOccured(eev);

                isConnected = false;
            }
        }

        @Override
        public void failed(Throwable exc, Object attachment) {
            Log.e(TAG, "데이터 수신 실패");

            isConnected = false;
        }
    };

    private void receiveData(int bytesRead) {
        try {
            ArrayList<Byte> currentData = new ArrayList<>();

            int length = 0;

            if (receiveData.size() > 4) {
                if (receiveData.get(0) == 0x01) {
                    length = BitConverter.toInt32(new byte[]{ receiveData.get(4).byteValue(), receiveData.get(3).byteValue(), receiveData.get(2).byteValue(), receiveData.get(1).byteValue() }, 0);

                    if (receiveData.size() >= 6 + length) {
                        for (int i = 6; i < 6 + length; i++) {
                            currentData.add(receiveData.get(i).byteValue());
                        }

                        ArrayList<Byte> temp = new ArrayList<>();

                        for (int i = 7 + length; i < receiveData.size(); i++) {
                            temp.add(receiveData.get(i));
                        }

                        receiveData = temp;

                        byte[] b = new byte[currentData.size()];
                        for (int i = 0; i < currentData.size(); i++) {
                            b[i] = (byte)(currentData.get(i).byteValue() & 0xff);
                        }

                        AsyncSocketReceiveEventArgs rev = new AsyncSocketReceiveEventArgs(this.id, b.length, b);
                        this.received(rev);
                    }
                }
            }
        } catch (Exception e) {
            LogManager.e("[AsyncSocketClient] [receiveData] " + e.getMessage());

            AsyncSocketErrorEventArgs eev = new AsyncSocketErrorEventArgs(id, e);

            errorOccured(eev);
        }
    }

    public boolean send(byte[] buffer) {
        try {
            ArrayList<Byte> temp = new ArrayList<Byte>();
            temp.add(Byte.valueOf((byte)0x01));
            byte[] arr = BitConverter.getBytes(buffer.length);

            for (int i = 0; i < arr.length; i++) {
                temp.add(Byte.valueOf(arr[i]));
            }

            temp.add(Byte.valueOf((byte)0x02));

            for (int i = 0; i < buffer.length; i++) {
                temp.add(Byte.valueOf(buffer[i]));
            }

            temp.add(Byte.valueOf((byte)0x03));

            byte[] data = new byte[temp.size()];
            for (int i = 0; i < temp.size(); i++) {
                data[i] = temp.get(i).byteValue();
            }

            ByteBuffer byteBuffer = ByteBuffer.wrap(data, 0, data.length);
            conn.write(byteBuffer, null, onSendCallBack);
        } catch (Exception e) {
            LogManager.e("[AsyncSocketClient] [send] " + e.getMessage());

            AsyncSocketErrorEventArgs eev = new AsyncSocketErrorEventArgs(id, e);

            errorOccured(eev);

            return false;
        }

        return true;
    }

    public void send(byte[] buffer, int size) {
        try {
            ByteBuffer byteBuffer = ByteBuffer.wrap(buffer, 0, size);
            conn.write(byteBuffer, null, onSendCallBack);
        } catch (Exception e) {

        }
    }

    private CompletionHandler<Integer, Object> onSendCallBack = new CompletionHandler<Integer, Object>() {
        @Override
        public void completed(Integer result, Object attachment) {
            try {
                int bytesWritten = result;

                AsyncSocketSendEventArgs sev = new AsyncSocketSendEventArgs(id, bytesWritten);

                send(sev);
            } catch (Exception e) {
                LogManager.e("[AsyncSocketClient] [onSendCallBack] " + e.getMessage());

                AsyncSocketErrorEventArgs eev = new AsyncSocketErrorEventArgs(id, e);

                errorOccured(eev);
            }

            // Log.w(TAG, "데이터 송신 성공");
        }

        @Override
        public void failed(Throwable exc, Object attachment) {
            // Log.e(TAG, "데이터 송신 실패");
        }
    };

    public boolean isAliveSocket() {
        try {

        } catch (Exception e) {

        }

        return true;
    }

    public void close() {
        try {
            if (conn != null) {
                conn.close();
            }

            isConnected = false;
        } catch (Exception e) {

        }
    }
}
