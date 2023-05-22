package com.example.myapplication;

import static android.content.ContentValues.TAG;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import androidx.core.app.NotificationCompat;

import com.example.myapplication.AsyncFrameSocket.AsyncSocketAcceptEventArgs;
import com.example.myapplication.AsyncFrameSocket.AsyncSocketClient;
import com.example.myapplication.AsyncFrameSocket.AsyncSocketConnectionEventArgs;
import com.example.myapplication.AsyncFrameSocket.AsyncSocketErrorEventArgs;
import com.example.myapplication.AsyncFrameSocket.AsyncSocketReceiveEventArgs;
import com.example.myapplication.AsyncFrameSocket.AsyncSocketSendEventArgs;
import com.example.myapplication.AsyncFrameSocket.IEventHandler;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.mindpipe.android.logging.log4j.LogConfigurator;

public class CustomService extends Service {
    private int broadcast_index = 0;
    // 브로드캐스트
    private BroadcastReceiver m_BroadcastReceiver = null;

    private int socketCheckMilliSeconds = 0;

    public static final int MSG_REGISTER_CLIENT = 1;
    //public static final int MSG_UNREGISTER_CLIENT = 2;
    public static final int MSG_SEND_TO_SERVICE = 3;
    public static final int MSG_SEND_TO_ACTIVITY = 4;

    private boolean m_IsRunning = true;

    private InputData m_InputData = new InputData();
    private OutputData m_OutputData = new OutputData();

    // 최상위뷰
    private View m_HighestView;
    private WindowManager m_WindowManager;

    IBinder m_Binder = new CustomBinder();

    Intent m_NotificationIntent;
    public boolean m_IsHighest = false;

    private boolean m_Closed = false;

    class CustomBinder extends Binder {
        CustomService getService() {
            return CustomService.this;
        }
    }

    private AsyncSocketClient m_MainSocketClient = null;
    private Thread m_MainSocketClientCheckThread = null;
    private Thread m_SocketCheckThread = null;

    private IEventHandler<AsyncSocketAcceptEventArgs> onAccept = new IEventHandler<AsyncSocketAcceptEventArgs>() {
        @Override
        public void eventReceived(Object sender, AsyncSocketAcceptEventArgs e) {

        }
    };

    private IEventHandler<AsyncSocketConnectionEventArgs> onConnect = new IEventHandler<AsyncSocketConnectionEventArgs>() {
        @Override
        public void eventReceived(Object sender, AsyncSocketConnectionEventArgs e) {

        }
    };

    private IEventHandler<AsyncSocketErrorEventArgs> onError = new IEventHandler<AsyncSocketErrorEventArgs>() {
        @Override
        public void eventReceived(Object sender, AsyncSocketErrorEventArgs e) {
            Log.e(TAG, "에러 발생");
        }
    };

    private IEventHandler<AsyncSocketReceiveEventArgs> onReceive = new IEventHandler<AsyncSocketReceiveEventArgs>() {
        @Override
        public void eventReceived(Object sender, AsyncSocketReceiveEventArgs e) {
            socketCheckMilliSeconds = 0;

            byte[] bytes = e.getReceiveData();

            if (bytes != null && bytes.length > 0) {
                try {
                    m_InputData.HeartBeat = bytes[0];
                    m_InputData.Control = bytes[1];
                    m_InputData.FirstNozelForwardSensor = bytes[2];
                    m_InputData.SecondNozelForwardSensor = bytes[3];
                    m_InputData.ThirdNozelForwardSensor = bytes[4];
                    m_InputData.FirstNozelBackwardSensor = bytes[5];
                    m_InputData.SecondNozelBackwardSensor = bytes[6];
                    m_InputData.ThirdNozelBackwardSensor = bytes[7];

                    /*
                    LogManager.i(m_InputData.HeartBeat + " / " + m_InputData.Control + " / " + m_InputData.FirstNozelForwardSensor + " / " + m_InputData.SecondNozelForwardSensor + " / " + m_InputData.ThirdNozelForwardSensor +
                            " / " + m_InputData.FirstNozelBackwardSensor + " / " + m_InputData.SecondNozelBackwardSensor + " / " + m_InputData.ThirdNozelBackwardSensor);
                    */

                    if (m_InputData.Control == 0) {
                        sendBroadcast(new Intent("com.example.myapplication.ControlOff"));

                        if (m_IsHighest) {
                            WindowManager windowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
                            windowManager.removeView(m_HighestView);
                            m_IsHighest = false;
                            m_OutputData.GetControl = 0;
                        }
                    } else if (m_InputData.Control == 1) {
                        sendBroadcast(new Intent("com.example.myapplication.ControlOn"));

                        if (!m_IsHighest) {
                            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                public void run() {
                                    m_HighestView = ((LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.get_control_layout, (ViewGroup) null);
                                    ((Button) m_HighestView.findViewById(R.id.getControlButton)).setOnClickListener(new View.OnClickListener() {
                                        public void onClick(View v) {
                                            m_OutputData.GetControl = 1;
                                        }
                                    });
                                    WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(-1, -2, Build.VERSION.SDK_INT >= 26 ? 2038 : 2003, 262184, -3);
                                    layoutParams.gravity = 49;
                                    WindowManager windowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
                                    windowManager.addView(m_HighestView, layoutParams);
                                }
                            }, 0);

                            m_IsHighest = true;
                        }
                    }

                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
                    Date currentTime = Calendar.getInstance().getTime();
                    String currentTimeStr = format.format(currentTime);

                    if (m_InputData.FirstNozelForwardSensor == 0) {
                        // Log.i(TAG, "[" +  currentTimeStr  + "] 서비스 : 노즐 #1 전진 센서 OFF");
                        sendBroadcast(new Intent("com.example.myapplication.FirstNozelForwardSensorOff"));
                    } else if (m_InputData.FirstNozelForwardSensor == 1) {
                        // Log.i(TAG, "[" +  currentTimeStr  + "] 서비스 : 노즐 #1 전진 센서 ON");
                        sendBroadcast(new Intent("com.example.myapplication.FirstNozelForwardSensorOn"));
                    }

                    if (m_InputData.SecondNozelForwardSensor == 0) {
                        // Log.i(TAG, "[" +  currentTimeStr  + "] 서비스 : 노즐 #2 전진 센서 OFF");
                        sendBroadcast(new Intent("com.example.myapplication.SecondNozelForwardSensorOff"));
                    } else if (m_InputData.SecondNozelForwardSensor == 1) {
                        // Log.i(TAG, "[" +  currentTimeStr  + "] 서비스 : 노즐 #2 전진 센서 ON");
                        sendBroadcast(new Intent("com.example.myapplication.SecondNozelForwardSensorOn"));
                    }

                    if (m_InputData.ThirdNozelForwardSensor == 0) {
                        // Log.i(TAG, "[" +  currentTimeStr  + "] 서비스 : 노즐 #3 전진 센서 OFF");
                        sendBroadcast(new Intent("com.example.myapplication.ThirdNozelForwardSensorOff"));
                    } else if (m_InputData.ThirdNozelForwardSensor == 1) {
                        // Log.i(TAG, "[" +  currentTimeStr  + "] 서비스 : 노즐 #3 전진 센서 ON");
                        sendBroadcast(new Intent("com.example.myapplication.ThirdNozelForwardSensorOn"));
                    }

                    if (m_InputData.FirstNozelBackwardSensor == 0) {
                        // Log.i(TAG, "[" +  currentTimeStr  + "] 서비스 : 노즐 #1 후진 센서 OFF");
                        sendBroadcast(new Intent("com.example.myapplication.FirstNozelBackwardSensorOff"));
                    } else if (m_InputData.FirstNozelBackwardSensor == 1) {
                        // Log.i(TAG, "[" +  currentTimeStr  + "] 서비스 : 노즐 #1 후진 센서 ON");
                        sendBroadcast(new Intent("com.example.myapplication.FirstNozelBackwardSensorOn"));
                    }

                    if (m_InputData.SecondNozelBackwardSensor == 0) {
                        // Log.i(TAG, "[" +  currentTimeStr  + "] 서비스 : 노즐 #2 후진 센서 OFF");
                        sendBroadcast(new Intent("com.example.myapplication.SecondNozelBackwardSensorOff"));
                    } else if (m_InputData.SecondNozelBackwardSensor == 1) {
                        // Log.i(TAG, "[" +  currentTimeStr  + "] 서비스 : 노즐 #2 후진 센서 ON");
                        sendBroadcast(new Intent("com.example.myapplication.SecondNozelBackwardSensorOn"));
                    }

                    if (m_InputData.ThirdNozelBackwardSensor == 0) {
                        // Log.i(TAG, "[" +  currentTimeStr  + "] 서비스 : 노즐 #3 후진 센서 OFF");
                        sendBroadcast(new Intent("com.example.myapplication.ThirdNozelBackwardSensorOff"));
                    } else if (m_InputData.ThirdNozelBackwardSensor == 1) {
                        // Log.i(TAG, "[" +  currentTimeStr  + "] 서비스 : 노즐 #3 후진 센서 ON");
                        sendBroadcast(new Intent("com.example.myapplication.ThirdNozelBackwardSensorOn"));
                    }
                } catch (Exception exc) {
                    LogManager.e("[CustomService] [onReceive] " + exc.getMessage());
                }
            }
        }
    };

    private IEventHandler<AsyncSocketSendEventArgs> onSend = new IEventHandler<AsyncSocketSendEventArgs>() {
        @Override
        public void eventReceived(Object sender, AsyncSocketSendEventArgs e) {

        }
    };

    private IEventHandler<AsyncSocketConnectionEventArgs> onClose = new IEventHandler<AsyncSocketConnectionEventArgs>() {
        @Override
        public void eventReceived(Object sender, AsyncSocketConnectionEventArgs e) {

        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        this.m_BroadcastReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("com.example.myapplication.JogUpButtonDown")) {
                    broadcast_index++;
                    m_OutputData.JogUp = 1;
                } else if (intent.getAction().equals("com.example.myapplication.JogUpButtonUp")) {
                    broadcast_index++;
                    m_OutputData.JogUp = 0;
                }

                if (intent.getAction().equals("com.example.myapplication.JogLeftButtonDown")) {
                    m_OutputData.JogLeft = 1;
                } else if (intent.getAction().equals("com.example.myapplication.JogLeftButtonUp")) {
                    m_OutputData.JogLeft = 0;
                }

                if (intent.getAction().equals("com.example.myapplication.JogRightButtonDown")) {
                    m_OutputData.JogRight = 1;
                } else if (intent.getAction().equals("com.example.myapplication.JogRightButtonUp")) {
                    m_OutputData.JogRight = 0;
                }

                if (intent.getAction().equals("com.example.myapplication.JogDownButtonDown")) {
                    m_OutputData.JogDown = 1;
                } else if (intent.getAction().equals("com.example.myapplication.JogDownButtonUp")) {
                    m_OutputData.JogDown = 0;
                }

                if (intent.getAction().equals("com.example.myapplication.allNozelForwardButtonDown")) {
                    m_OutputData.NozelForward = 1;
                } else if (intent.getAction().equals("com.example.myapplication.allNozelForwardButtonUp")) {
                    m_OutputData.NozelForward = 0;
                }

                if (intent.getAction().equals("com.example.myapplication.allNozelBackwardButtonDown")) {
                    m_OutputData.NozelBackward = 1;
                } else if (intent.getAction().equals("com.example.myapplication.allNozelBackwardButtonUp")) {
                    m_OutputData.NozelBackward = 0;
                }

                if (intent.getAction().equals("com.example.myapplication.communicationSettingChanged")) {
                    try {
                        if (m_MainSocketClient != null) {
                            m_MainSocketClient.close();
                        }

                        // Log.i(TAG, "서비스 소켓 정상 종료");
                        LogManager.i("[CustomService] [onCreate] 서비스 소켓 정상 종료");
                    } catch (Exception e) {
                        LogManager.e("[CustomService] [onCreate] " + e.getMessage());
                    }
                }

                if (intent.getAction().equals("com.example.myapplication.close")) {
                    stopSelf();
                    stopForeground(true);
                    m_Closed = true;
                }
            }
        };

        IntentFilter m_Filter = new IntentFilter();
        m_Filter.addAction("com.example.myapplication.JogUpButtonDown");
        m_Filter.addAction("com.example.myapplication.JogUpButtonUp");
        m_Filter.addAction("com.example.myapplication.JogLeftButtonDown");
        m_Filter.addAction("com.example.myapplication.JogLeftButtonUp");
        m_Filter.addAction("com.example.myapplication.JogRightButtonDown");
        m_Filter.addAction("com.example.myapplication.JogRightButtonUp");
        m_Filter.addAction("com.example.myapplication.JogDownButtonDown");
        m_Filter.addAction("com.example.myapplication.JogDownButtonUp");
        m_Filter.addAction("com.example.myapplication.allNozelForwardButtonDown");
        m_Filter.addAction("com.example.myapplication.allNozelForwardButtonUp");
        m_Filter.addAction("com.example.myapplication.allNozelBackwardButtonDown");
        m_Filter.addAction("com.example.myapplication.allNozelBackwardButtonUp");
        m_Filter.addAction("com.example.myapplication.communicationSettingChanged");
        m_Filter.addAction("com.example.myapplication.close");
        registerReceiver(m_BroadcastReceiver, m_Filter);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("rebuild_preference", MODE_PRIVATE);

        m_MainSocketClient = new AsyncSocketClient(0);
        m_MainSocketClientCheckThread = new Thread(mainSocketClientCheckThreadDo);
        m_MainSocketClientCheckThread.start();

        m_SocketCheckThread = new Thread(socketCheckThreadDo);
        m_SocketCheckThread.start();
    }

    private Runnable mainSocketClientCheckThreadDo = new Runnable() {
        @Override
        public void run() {
            while (m_IsRunning) {
                if (!m_MainSocketClient.isConnected()) {
                    m_MainSocketClient.close();
                    m_MainSocketClient = null;

                    SharedPreferences pref = getApplicationContext().getSharedPreferences("rebuild_preference", MODE_PRIVATE);
                    String m_IPAddress = pref.getString("mainIpAddress", "192.168.1.9");
                    int m_Port = pref.getInt("mainPort", 9600);

                    m_MainSocketClient = new AsyncSocketClient(0);
                    m_MainSocketClient.onAccept.addEventHandler(onAccept);
                    m_MainSocketClient.onConnect.addEventHandler(onConnect);
                    m_MainSocketClient.onError.addEventHandler(onError);
                    m_MainSocketClient.onReceive.addEventHandler(onReceive);
                    m_MainSocketClient.onSend.addEventHandler(onSend);
                    m_MainSocketClient.onClose.addEventHandler(onClose);
                    m_MainSocketClient.connect(m_IPAddress, m_Port);

                    try {
                        Thread.sleep(5000);
                    } catch (Exception e) {

                    }
                } else {
                    try {
                        byte[] bytes = new byte[] { m_OutputData.HeartBeat, m_OutputData.JogLeft, m_OutputData.JogRight, m_OutputData.JogUp, m_OutputData.JogDown, m_OutputData.NozelForward, m_OutputData.NozelBackward, m_OutputData.GetControl };
                        m_MainSocketClient.send(bytes, bytes.length);

                        if (m_OutputData.HeartBeat == 0) {
                            m_OutputData.HeartBeat = 1;
                        } else if (m_OutputData.HeartBeat == 1) {
                            m_OutputData.HeartBeat = 0;
                        }

                        Thread.sleep(100);
                    } catch (Exception e) {
                        // Log.e(TAG, "에러 내용 : " + e.getMessage());
                        LogManager.e("[CustomService] [mainSocketClientCheckThreadDo] 에러 내용 : " + e.getMessage());
                    }
                }
            }
        }
    };

    private Runnable socketCheckThreadDo = new Runnable() {
        @Override
        public void run() {
            try {
                while (m_IsRunning) {
                    if (m_MainSocketClient == null || !m_MainSocketClient.isConnected()) {
                        sendBroadcast(new Intent("com.example.myapplication.SOCKET_NOT_CHECK"));
                    } else {
                        sendBroadcast(new Intent("com.example.myapplication.SOCKET_CHECK"));
                    }

                    Thread.sleep(1000);

                    socketCheckMilliSeconds = socketCheckMilliSeconds + 1000;
                }
            } catch (Exception e) {
                LogManager.e("[CustomService] [socketCheckThreadDo] " + e.getMessage());
            }
        }
    };

    public CustomService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        stopSelf();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogManager.d("[CustomService] [onStartCommand] onStartCommand 콜");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default");
        builder.setContentTitle("포그라운드");
        builder.setContentText("포그라운드 실행 중");

        m_NotificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, m_NotificationIntent, 0);
        builder.setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(new NotificationChannel("default", "기본채널", NotificationManager.IMPORTANCE_DEFAULT));
        }
        startForeground(1, builder.build());

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        m_IsRunning = false;

        if (m_MainSocketClient != null) {
            m_MainSocketClient.close();
        }

        if (m_Closed) {
            stopSelf();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                LogManager.d("[CustomService] [onDestroy] onDestroy() 안드로이드 버젼 오레오 이상");

                Intent serviceIntent = new Intent(getApplicationContext(), CustomService.class);
                serviceIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                getApplicationContext().startForegroundService(serviceIntent);
            } else {
                LogManager.d("[CustomService] [onDestroy] 안드로이드 버젼 오레오 이하");

                Intent serviceIntent = new Intent(getApplicationContext(), CustomService.class);
                serviceIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                getApplicationContext().startService(serviceIntent);
            }
        }

        unregisterReceiver(m_BroadcastReceiver);
    }

    public void postToastMessage(final String message) {
        Handler handler = new Handler(Looper.getMainLooper());

        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public ComponentName startForegroundService(Intent service) {
        return super.startForegroundService(service);
    }
}
