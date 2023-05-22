package com.example.myapplication;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RotateDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.example.myapplication.AsyncFrameSocket.AsyncSocketAcceptEventArgs;
import com.example.myapplication.AsyncFrameSocket.AsyncSocketClient;
import com.example.myapplication.AsyncFrameSocket.AsyncSocketConnectionEventArgs;
import com.example.myapplication.AsyncFrameSocket.AsyncSocketErrorEventArgs;
import com.example.myapplication.AsyncFrameSocket.AsyncSocketReceiveEventArgs;
import com.example.myapplication.AsyncFrameSocket.AsyncSocketSendEventArgs;
import com.example.myapplication.AsyncFrameSocket.IEventHandler;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.util.VLCVideoLayout;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import com.example.myapplication.LogManager;

public class MainActivity extends AppCompatActivity {
    private int broadcast_index = 0;
    private boolean isCompleted = false;
    private FinishCleaningData m_FinishCleaningData = null;
    private boolean isPause = false;
    private boolean isSettingButtonTouched = false;
    private int settingButtonTouchMillisSeconds = 0;
    private int settingButtonTouchCount = 0;
    private int settingButtonClickMilliSeconds = 0;
    private int settingButtonClickCount = 0;
    private ProgressBar socketCheckProgressBar = null;
    private int cleaningMaxCount = 0;
    private int cleaningCount = 0;
    private boolean serviceSocketChecked = false;
    private boolean isTestMode = false;
    private boolean isRelayMode = false;
    private boolean isRelay = false;

    private ArrayList<StructErrorData> errorList = new ArrayList<>();

    // 브로드캐스트
    BroadcastReceiver m_CustomBroadCastReceiver = null;
    private ArrayList<HoleData> m_HoleDataList = new ArrayList<>();

    // 작업 시작 체크
    private boolean m_StartChecked = false;

    // 통신 응답 체크
    private boolean m_ReplyStartChecked = false;
    private boolean m_ReplyOriginLeftTopChecked = false;
    private boolean m_ReplyStartScanChecked = false;
    private boolean m_ReplyScanHolesChecked = false;
    private boolean m_ReplyStartCleaningChecked = false;
    private boolean m_ReplyCleaningDataChecked = false;
    private boolean m_ReplyRemoveHoleChecked = false;
    private boolean m_ReplyAddHoleChecked = false;
    private boolean m_ReplyPauseChecked = false;
    private boolean m_ReplyCancelChecked = false;
    private boolean m_ReplyMoveHoleChecked = false;
    private boolean m_ReplyPumpOnChecked = false;
    private boolean m_ReplyPumpOffChecked = false;
    private boolean m_ReplySkipOneHoleChecked = false;
    private boolean m_ReplyRetryStartChecked = false;
    private boolean m_ReplyEmergencyStopChecked = false;
    private boolean m_ReplyErrorMsgChecked = false;
    private boolean m_ReplyScanImageChecked = false;
    private boolean m_ReplyErrorMsgClearChecked = false;
    private boolean m_ReplyResumeChecked = false;
    private boolean m_ReplyAccelerateModeOnChecked = false;
    private boolean m_ReplyAccelerateModeOffChecked = false;

    private boolean isEditHoleRunning = false;
    private boolean m_ScanImageDownloaded = false;

    // 각 레이아웃
    private LinearLayout m_FirstLayout;
    private LinearLayout m_SecondLayout;
    private LinearLayout m_ThirdLayout;
    private LinearLayout m_FourthLayout;
    private LinearLayout m_FifthLayout;
    private GridLayout m_SixthLayout;
    // public ScrollView m_ManualLayout;
    public LinearLayout m_ManualLayout;
    private LinearLayout m_EditHoleLayout;

    // 첫번째 레이아웃 버튼
    private Button m_CenteringButton;
    private Button m_TestButton;
    private Button m_StartButton;
    private Button m_RelayButton;
    private ImageView m_SettingButton;
    private Button m_ManualButton;

    // 두번째 레이아웃 버튼
    private Button m_SecondNextButton;
    private Button m_SecondPreButton;
    public EditText m_WorkNumber;
    public EditText m_BundleLength;
    private Button m_NumericUpButton;
    boolean m_NumericUpButtonTouchDown = false;
    private Button m_NumericDownButton;
    boolean m_NumericDownButtonTouchDown = false;
    public int m_Control = 0;

    // 세번째 레이아웃 버튼
    private Button m_ThirdPreButton;
    private Button m_ThirdNextButton;
    private Button m_PauseButton;
    private Button m_SkipNextHoleButton;
    private Button m_PressurizeOrDumpButton;
    private Button m_SixthConfirmButton;
    private TextView m_MotorPositionSettingText;
    private int m_MotorPositionSettingNum;
    private Dialog m_SettingDialog;
    private Thread m_MainSocketClientCheckThread = null;
    private Thread m_SocketCheckThread = null;
    private AsyncSocketClient m_MainSocketClient = null;
    private boolean m_IsRunning = true;
    private int m_LayoutIndex = 0;
    private ImageView m_OriginBundleDraw;
    private Button m_CenteringJogUpButton;
    private Button m_CenteringJogLeftButton;
    private Button m_CenteringJogRightButton;
    private Button m_CenteringJogDownButton;

    // 다섯번째 레이아웃 버튼
    private Button m_StopButton;
    private Button m_PositionChangeButton;
    ProgressBar m_CleaningProgressBar;
    private boolean m_PositionChangeNumericDownButtonTouchDown = false;
    private boolean m_PositionChangeNumericUpButtonTouchDown = false;
    private int m_HoleMaxNum = -99999;
    private int m_HoleMinNum = 99999;

    // 메인 세팅 화면 구성
    private LinearLayout m_SettingListView;

    // 홀 추가 제거 이미지뷰
    private ImageView m_EditHoleImageView;

    // 세척 화면 이미지뷰
    private ImageView m_CleaningView;

    // 결과 화면 이미지뷰
    private ImageView m_ResultView;

    // 터치 관련
    private Matrix m_EditHoleImageViewMatrix;
    private Matrix m_EditHoleImageViewSavedMatrix;
    private PointF m_EditHoleImageViewStartPoint;
    private PointF m_EditHoleImageViewMidPoint;
    private float m_EditHoleImageViewOldDistance;
    private Matrix m_Matrix;
    private Matrix m_SavedMatrix;
    private PointF m_StartPoint;
    private PointF m_MidPoint;
    private float m_OldDistance;
    private Matrix m_ResultViewMatrix;
    private Matrix m_ResultViewSavedMatrix;
    private PointF m_ResultViewStartPoint;
    private PointF m_ResultViewMidPoint;
    private float m_ResultViewOldDistance;

    private int m_ScanHoleCount = 0;

    // 노즐 사용 유무
    private boolean m_IsUseNozel = false;

    enum TOUCH_MODE {
        NONE,   // 터치 안했을 때
        SINGLE, // 한 손가락 터치
        MULTI   // 두 손가락 터치
    }

    private TOUCH_MODE m_TouchMode;

    private Intent m_ServiceIntent = null;

    // private ArrayList<StructErrorData> errorList = new ArrayList<>();

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

    private boolean isErrored = false;

    private IEventHandler<AsyncSocketErrorEventArgs> onError = new IEventHandler<AsyncSocketErrorEventArgs>() {
        @Override
        public void eventReceived(Object sender, AsyncSocketErrorEventArgs e) {
            LogManager.e("[MainActivity] [onError] 에러 발생 : " + e.getAsyncSocketException().getMessage());
            isErrored = true;
        }
    };

    private IEventHandler<AsyncSocketReceiveEventArgs> onReceive = new IEventHandler<AsyncSocketReceiveEventArgs>() {
        @Override
        public void eventReceived(Object sender, AsyncSocketReceiveEventArgs e) {
            byte[] bytes = e.getReceiveData();

            commandSocketCheckMilliSeconds = 0;

            try {
                String recvStr = new String(bytes, StandardCharsets.US_ASCII);

                // Log.i(TAG, recvStr);

                String[] recvSplit = recvStr.split(",", -1);

                String command = recvSplit[0];

                Log.i(TAG, recvSplit.length + "");

                Log.i(TAG, command);

                if (command.equals("RPY_START")) {
                    if (bytes[command.getBytes().length + 1] == 0x00) {
                        m_ReplyStartChecked = true;
                        LogManager.i("[MainActivity] [onReceive] RPY_START 성공");
                    } else if ((bytes[command.getBytes().length + 1] & 0xff) == 0xff) {
                        m_ReplyStartChecked = false;
                        LogManager.i("[MainActivity] [onReceive] RPY_START 실패");
                    }
                } else if (command.equals("RPY_RETRY_START")) {
                    if (bytes[command.getBytes().length + 1] == 0x00) {
                        m_ReplyRetryStartChecked = true;
                        LogManager.i("[MainActivity] [onReceive] RPY_RETRY_START 성공");
                    } else if ((bytes[command.getBytes().length + 1] & 0xff) == 0xff) {
                        m_ReplyRetryStartChecked = false;
                        LogManager.i("[MainActivity] [onReceive] RPY_RETRY_START 실패");
                    }
                } else if (command.equals("RPY_PUMP_ON")) {
                    if (bytes[command.getBytes().length + 1] == 0x00) {
                        m_ReplyPumpOnChecked = true;
                        // Log.i(TAG, "RPY_PUMP_ON 성공");
                        LogManager.i("[MainActivity] [onReceive] RPY_PUMP_ON 성공");
                    } else if ((bytes[command.getBytes().length + 1] & 0xff) == 0xff) {
                        m_ReplyPumpOnChecked = true;
                        // Log.i(TAG, "RPY_PUMP_ON 실패");
                        LogManager.i("[MainActivity] [onReceive] RPY_PUMP_ON 실패");
                    }
                } else if (command.equals("RPY_SKIP_ONE_HOLE")) {
                    if (bytes[command.getBytes().length + 1] == 0x00) {
                        m_ReplySkipOneHoleChecked = true;
                        Log.i(TAG, "RPY_SKIP_ONE_HOLE 성공");
                    } else if ((bytes[command.getBytes().length + 1] & 0xff) == 0xff) {
                        m_ReplySkipOneHoleChecked = true;
                        Log.i(TAG, "RPY_SKIP_ONE_HOLE 실패");
                    }
                } else if (command.equals("RPY_PUMP_OFF")) {
                    if (bytes[command.getBytes().length + 1] == 0x00) {
                        m_ReplyPumpOffChecked = true;
                        Log.i(TAG, "RPY_PUMP_OFF 성공");
                    } else if ((bytes[command.getBytes().length + 1] & 0xff) == 0xff) {
                        m_ReplyPumpOffChecked = true;
                        Log.i(TAG, "RPY_PUMP_OFF 실패");
                    }
                } else if (command.equals("RPY_ORIGIN_LEFT_TOP")) {
                    if (bytes[command.getBytes().length + 1] == 0x00) {
                        m_ReplyOriginLeftTopChecked = true;
                        LogManager.i("[MainActivity] [onReceive] RPY_ORIGIN_LEFT_TOP 성공");
                    } else if ((bytes[command.getBytes().length + 1] & 0xff) == 0xff) {
                        m_ReplyOriginLeftTopChecked = true;
                        LogManager.i("[MainActivity] [onReceive] RPY_ORIGIN_LEFT_TOP 실패");
                    }
                } else if (command.equals("RPY_START_SCAN")) {
                    if (bytes[command.getBytes().length + 1] == 0x00) {
                        m_ReplyStartScanChecked = true;
                        // Log.i(TAG, "RPY_START_SCAN 성공");
                        LogManager.i("[MainActivity] [onReceive] RPY_START_SCAN 성공");
                    } else if ((bytes[command.getBytes().length + 1] & 0xff) == 0xff) {
                        m_ReplyStartScanChecked = false;
                        // Log.i(TAG, "RPY_START_SCAN 실패");
                        LogManager.i("[MainActivity] [onReceive] RPY_START_SCAN 실패");
                    }
                } else if (command.equals("RPY_MOVE_HOLE")) {
                    if (bytes[command.getBytes().length + 1] == 0x00) {
                        m_ReplyMoveHoleChecked = true;
                        Log.i(TAG, "RPY_MOVE_HOLE 성공");
                    } else if ((bytes[command.getBytes().length + 1] & 0xff) == 0xff) {
                        m_ReplyMoveHoleChecked = false;
                        Log.i(TAG, "RPY_MOVE_HOLE 실패");
                    }
                } else if (command.equals("RPY_PAUSE_CLEANING")) {
                    if (bytes[command.getBytes().length + 1] == 0x00) {
                        m_ReplyPauseChecked = true;

                        Log.i(TAG, "RPY_PAUSE_CLEANING 성공");
                    } else if ((bytes[command.getBytes().length + 1] & 0xff) == 0xff) {
                        m_ReplyPauseChecked = false;
                        Log.i(TAG, "RPY_PAUSE_CLEANING 실패");
                    }
                } else if (command.equals("RPY_RESUME_CLEANING")) {
                    if (bytes[command.getBytes().length + 1] == 0x00) {
                        m_ReplyResumeChecked = true;

                        Log.i(TAG, "RPY_RESUME_CLEANING 성공");
                    } else if ((bytes[command.getBytes().length + 1] & 0xff) == 0xff) {
                        m_ReplyResumeChecked = false;
                        Log.i(TAG, "RPY_RESUME_CLEANING 실패");
                    }
                } else if (command.equals("RPY_CANCEL_CLEANING")) {
                    if (bytes[command.getBytes().length + 1] == 0x00) {
                        m_ReplyCancelChecked = true;
                        // Log.i(TAG, "RPY_CANCEL_CLEANING 성공");
                        LogManager.i("[MainActivity] [onReceive] RPY_CANCEL_CLEANING 성공");
                    } else if ((bytes[command.getBytes().length + 1] & 0xff) == 0xff) {
                        m_ReplyCancelChecked = false;
                        // Log.i(TAG, "RPY_CANCEL_CLEANING 실패");
                        LogManager.i("[MainActivity] [onReceive] RPY_CANCEL_CLEANING 실패");
                    }
                } else if (command.equals("RPY_SCAN_IMAGE")) {
                    try {
                        // Log.i(TAG, "스캔 이미지 수신");

                        if (bytes[15] == 0x00) {
                            /*
                            String sendStr = "SCAN_IMAGE,";
                            byte[] sendBytes = sendStr.getBytes(StandardCharsets.US_ASCII);

                            try {
                                m_MainSocketClient.send(sendBytes);

                                Thread.sleep(100);
                            } catch (Exception exc) {
                                LogManager.e("[MainActivity] [onReceive] SCAN_IMAGE 추가 송신 에러 : " + exc.getMessage());
                                // Log.i(TAG, "[MainActivity] [onReceive] SCAN_IMAGE 추가 송신 에러 : " + exc.getMessage());
                            }
                            */

                        } else if ((bytes[15] & 0xff) == 0xff) {
                            m_MotorPositionSettingNum = 0;
                            setThirdLayout();
                            LogManager.i("[MainActivity] [onReceive] 스캔 이미지 수신 실패");
                            // Log.i(TAG, "[MainActivity] [onReceive] 스캔 이미지 수신 실패");
                        } else if (bytes[15] == 0x01) {
                            if (!m_ScanImageDownloaded && !m_ReplyScanImageChecked) {
                                LogManager.i("[MainActivity] [onReceive] 스캔 이미지 수신 성공");
                                // Log.i(TAG, "[MainActivity] [onReceive] 스캔 이미지 수신 성공");

                                m_MotorPositionSettingNum = 0;

                                int width = BitConverter.toInt32(new byte[] { bytes[19], bytes[18], bytes[17], bytes[16] }, 0);
                                int height = BitConverter.toInt32(new byte[] { bytes[23], bytes[22], bytes[21], bytes[20] }, 0);
                                int channel = BitConverter.toInt32(new byte[] { bytes[27], bytes[26], bytes[25], bytes[24] }, 0);

                                int len = 28;

                                if (!m_ScanImageDownloaded) {
                                    byte[] data = new byte[bytes.length - len];

                                    for (int i = 0; i < data.length; i++) {
                                        data[i] = bytes[len + i];
                                    }

                                    if (m_Bitmap != null) {
                                        // m_Bitmap.recycle();
                                        m_Bitmap = null;
                                    }

                                    m_Bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

                                    setEditHoleLayout();

                                    m_ScanImageDownloaded = true;
                                    m_ReplyScanImageChecked = true;
                                }
                            }
                        }
                    } catch (Exception exc) {
                        m_MotorPositionSettingNum = 0;
                        setThirdLayout();
                        LogManager.e("[MainActivity] [onReceive] 스캔 이미지 수신 실패");
                        // Log.e(TAG, "[MainActivity] [onReceive] 스캔 이미지 수신 실패");
                    }
                } else if (command.equals("RPY_SCAN_HOLES")) {
                    m_ReplyScanHolesChecked = false;
                    String scanHoleCountStr = recvSplit[1];
                    m_ScanHoleCount = Integer.parseInt(scanHoleCountStr);

                    // Log.i(TAG, "스캔홀 응답 메시지 : " + recvStr);
                    // LogManager.i("스캔홀 응답 메시지 : " + recvStr);

                    if (m_ScanHoleCount > 0) {
                        m_HoleDataList.clear();

                        for (int i = 2; i < recvSplit.length; i++) {
                            if (i >= recvSplit.length) {
                                continue;
                            }

                            String idxStr = recvSplit[i++];

                            if (i >= recvSplit.length) {
                                continue;
                            }

                            String nameStr = recvSplit[i++];

                            if (i >= recvSplit.length) {
                                continue;
                            }

                            String xStr = recvSplit[i++];

                            if (i >= recvSplit.length) {
                                continue;
                            }

                            String yStr = recvSplit[i++];

                            if (i >= recvSplit.length) {
                                continue;
                            }

                            String colorStr = recvSplit[i];

                            HoleData holeData = new HoleData();
                            holeData.Index = Integer.parseInt(idxStr);
                            holeData.Name = nameStr;
                            holeData.X = Integer.parseInt(xStr);
                            holeData.Y = Integer.parseInt(yStr);
                            holeData.Color = Integer.parseInt(colorStr);

                            boolean m_IsChecked = false;

                            for (int j = 0; j < m_HoleDataList.size(); j++) {
                                if (m_HoleDataList.get(j).Index == holeData.Index) {
                                    m_IsChecked = true;
                                    // Log.i(TAG, "존재하는 스캔 홀이므로 추가하지 않음");
                                    // LogManager.i("존재하는 스캔 홀이므로 추가하지 않음");

                                    break;
                                }
                            }

                            if (!m_IsChecked) {
                                // Log.i(TAG, "존재하지 않는 스캔 홀이므로 추가");
                                // LogManager.i("존재하지 않는 스캔 홀이므로 추가");
                                m_HoleDataList.add(holeData);
                            }
                        }

                        if (m_HoleDataList.size() == m_ScanHoleCount) {
                            // Log.i(TAG, "홀 개수 일치 모두 들어옴");
                            // LogManager.i("홀 개수 일치 모두 들어옴");
                            m_ReplyScanHolesChecked = true;
                        } else {
                            m_ReplyScanHolesChecked = false;
                        }
                    }
                } else if (command.equals("RPY_CLEANING_DATA")) {
                    m_ReplyCleaningDataChecked = true;
                    String cleaningMaxCountStr = recvSplit[1];
                    String cleaningCountStr = recvSplit[2];

                    cleaningMaxCount = Integer.valueOf(cleaningMaxCountStr);
                    cleaningCount = Integer.valueOf(cleaningCountStr);
                } else if (command.equals("RPY_START_CLEANING")) {
                    if (bytes[command.getBytes().length + 1] == 0x00) {
                        m_ReplyStartCleaningChecked = true;
                        isPause = false;
                        // Log.i(TAG, "RPY_START_CLEANING 성공");
                    } else if ((bytes[command.getBytes().length + 1] & 0xff) == 0xff) {
                        m_ReplyStartCleaningChecked = false;
                        // Log.i(TAG, "RPY_START_CLEANING 실패");
                    }
                } else if (command.equals("FINISH_CLEANING")) {
                    String totalHoleCount = recvSplit[1];
                    String cleaningHoleCount = recvSplit[2];
                    String noCleaningHoleCount = recvSplit[3];
                    String startDateTime = recvSplit[4];
                    String endDateTime = recvSplit[5];
                    String runTime = recvSplit[6];

                    m_FinishCleaningData = new FinishCleaningData();
                    m_FinishCleaningData.TotalHoleCount = totalHoleCount;
                    m_FinishCleaningData.CleaningHoleCount = cleaningHoleCount;
                    m_FinishCleaningData.NoCleaningHoleCount = noCleaningHoleCount;
                    m_FinishCleaningData.StartDateTime = startDateTime;
                    m_FinishCleaningData.EndDateTime = endDateTime;
                    m_FinishCleaningData.RunTime = runTime;

                    isCompleted = true;
                } else if (command.equals("RPY_TEST_START")) {
                    if (bytes[command.getBytes().length + 1] == 0x00) {
                        m_ReplyStartChecked = true;
                        Log.i(TAG, "RPY_TEST_START 성공");
                    } else if ((bytes[command.getBytes().length + 1] & 0xff) == 0xff) {
                        m_ReplyStartChecked = false;
                        Log.i(TAG, "RPY_TEST_START 실패");
                    }
                } else if (command.equals("RPY_RELAY_START")) {
                    if (bytes[command.getBytes().length + 1] == 0x00) {
                        m_ReplyStartChecked = true;
                        Log.i(TAG, "RPY_RELAY_START 성공");
                    } else if ((bytes[command.getBytes().length + 1] & 0xff) == 0xff) {
                        m_ReplyStartChecked = false;
                        Log.i(TAG, "RPY_RELAY_START 실패");
                    }
                } else if (command.equals("RPY_ADD_HOLE")) {
                    if (bytes[command.getBytes().length + 1] == 0x00) {
                        m_ReplyAddHoleChecked = true;
                        LogManager.i("[MainActivity] [onReceive] RPY_ADD_HOLE 성공");
                    } else if ((bytes[command.getBytes().length + 1] & 0xff) == 0xff) {
                        m_ReplyAddHoleChecked = false;
                        LogManager.i("[MainActivity] [onReceive] RPY_ADD_HOLE 실패");
                    }
                } else if (command.equals("RPY_REMOVE_HOLE")) {
                    if (bytes[command.getBytes().length + 1] == 0x00) {
                        m_ReplyRemoveHoleChecked = true;
                        LogManager.i("[MainActivity] [onReceive] RPY_REMOVE_HOLE 성공");
                    } else if ((bytes[command.getBytes().length + 1] & 0xff) == 0xff) {
                        m_ReplyRemoveHoleChecked = false;
                        LogManager.i("[MainActivity] [onReceive] RPY_REMOVE_HOLE 실패");
                    }
                } else if (command.equals("RPY_EMERGENCY_STOP")) {
                    if (bytes[command.getBytes().length + 1] == 0x00) {
                        m_ReplyEmergencyStopChecked = true;
                        Log.i(TAG, "RPY_EMERGENCY_STOP 성공");
                    } else if ((bytes[command.getBytes().length + 1] & 0xff) == 0xff) {
                        m_ReplyEmergencyStopChecked = false;
                        Log.i(TAG, "RPY_EMERGENCY_STOP 실패");
                    }
                } else if (command.equals("RPY_ERROR_MSG")) {
                    // Log.i(TAG, "RPY_ERROR_MSG 수신 완료");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 1; i < recvSplit.length; i++) {
                                StructErrorData errorData = new StructErrorData();

                                /*
                                if (recvSplit.length > 5) {
                                    errorData.id = recvSplit[1];
                                    errorData.command = recvSplit[2];
                                    errorData.dateTime = recvSplit[3];
                                    errorData.isCleared = recvSplit[4];
                                    errorData.clearDateTime = recvSplit[5];
                                } else if (recvSplit.length <= 5) {
                                    errorData.id = recvSplit[1];
                                    errorData.command = recvSplit[2];
                                    errorData.dateTime = recvSplit[3];
                                    errorData.isCleared = recvSplit[4];
                                }
                                */

                                if (i >= recvSplit.length) {
                                    continue;
                                }

                                errorData.id = recvSplit[i++];

                                if (i >= recvSplit.length) {
                                    continue;
                                }

                                errorData.priority = recvSplit[i++];

                                if (i >= recvSplit.length) {
                                    continue;
                                }

                                errorData.command = recvSplit[i++];

                                if (i >= recvSplit.length) {
                                    continue;
                                }

                                errorData.dateTime = recvSplit[i++];

                                if (i >= recvSplit.length) {
                                    continue;
                                }

                                errorData.isCleared = recvSplit[i++];

                                if (i >= recvSplit.length) {
                                    continue;
                                }

                                errorData.clearDateTime = recvSplit[i];

                                switch (errorData.command) {
                                    case "EMERGENCY":
                                        errorData.title = "비상 정지";
                                        errorData.content = "비상 정지 스위치가 눌린 상태입니다.";
                                        errorData.actionContent = "- 비상 정지 스위치를 오른쪽으로 돌려 복귀하세요.";
                                        break;
                                    case "SERVO_X":
                                        errorData.title = "X축 서보 알람 발생";
                                        errorData.content = "X축 서보 모터 알람 발생 했습니다.";
                                        errorData.actionContent = "- 에러 내역 번호 확인 후 문의하세요.";
                                        break;
                                    case "SERVO_Z":
                                        errorData.title = "Z축 서보 알람 발생";
                                        errorData.content = "Z축 서보 모터 알람 발생 했습니다.";
                                        errorData.actionContent = "- 에러 내역 번호 확인 후 문의하세요.";
                                        break;
                                    case "SERVO_MOVE":
                                        errorData.title = "서보 위치 이동 시간 경과 발생";
                                        errorData.content = "서보 운전 시간이 설정된 시간보다 경과한 상태입니다.";
                                        errorData.actionContent = "- 서보 이동에 장애 요소가 있는지 확인하세요.\n- 운전 시간, 속도 설정 값 확인하세요.";
                                        break;
                                    case "NOZZLE":
                                        errorData.title = "노즐 이상";
                                        errorData.content = "노즐 전진 및 후진 시간이 설정된 시간보다 경과한 상태입니다.";
                                        errorData.actionContent = "- 노즐 상태 확인하세요.\n- 감지 센서 확인하세요.\n- 노즐 전진, 후진 시 이송에 장애 요소가 있는지 확인하세요.";
                                        break;
                                    case "WIFI":
                                        errorData.title = "통신 연결 이상 발생";
                                        errorData.content = "태블릿 WiFi 연결이 끊긴 상태입니다.";
                                        errorData.actionContent = "- WiFi 연결 상태를 확인하세요.";
                                        break;
                                    default:
                                        break;
                                }

                                boolean isChecked = false;

                                for (int j = 0; j < errorList.size(); j++) {
                                    if (errorList.get(j).id.equals(errorData.id)) {
                                        isChecked = true;

                                        if (!errorList.get(j).isCleared.equals(errorData.isCleared)) {
                                            errorList.get(j).isCleared = errorData.isCleared;
                                            errorList.get(j).clearDateTime = errorData.clearDateTime;
                                        }

                                        break;
                                    }
                                }

                                if (!isChecked) {
                                    // errorList.add(errorData);
                                    errorList.add(0, errorData);
                                }
                            }

                            boolean isChecked = false;
                            int idx = 0;

                            for (int i = 0; i < errorList.size(); i++) {
                                if (errorList.get(i).isCleared.toUpperCase().equals("FALSE")) {
                                    isChecked = true;
                                    idx = i;

                                    if (errorList.get(i).priority.toUpperCase().equals("HIGH")) {
                                        break;
                                    }
                                }
                            }

                            if (isChecked) {
                                if (m_ErrorTextView != null) {
                                    switch (errorList.get(idx).command) {
                                        case "EMERGENCY":
                                            m_ErrorTextView.setText("비상 정지 스위치가 눌린 상태입니다.");
                                            break;
                                        case "SERVO_X":
                                            m_ErrorTextView.setText("X축 서보 모터 알람 발생 했습니다.");
                                            break;
                                        case "SERVO_Z":
                                            m_ErrorTextView.setText("Z축 서보 모터 알람 발생 했습니다.");
                                            break;
                                        case "SERVO_MOVE":
                                            m_ErrorTextView.setText("서보 운전 시간이 설정된 시간보다 경과한 상태입니다.");
                                            break;
                                        case "NOZZLE":
                                            m_ErrorTextView.setText("노즐 전진 및 후진 시간이 설정된 시간보다 경과한 상태입니다.");
                                            break;
                                        default:
                                            break;
                                    }
                                }
                            } else {
                                m_ErrorTextView.setText("이상 내역이 없습니다.");
                            }

                            isNotClearCount = 0;

                            for (int i = 0; i < errorList.size(); i++) {
                                if (errorList.get(i).isCleared.toUpperCase().equals("FALSE")) {
                                    isNotClearCount++;
                                }
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    m_ErrorMessageListButton.setText("" + isNotClearCount);
                                }
                            });
                        }
                    });

                    m_ReplyErrorMsgChecked = true;
                } else if (command.equals("RPY_ERROR_MSG_CLR")) {
                    if (bytes[command.getBytes().length + 1] == 0x00) {
                        m_ReplyErrorMsgClearChecked = true;

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                errorList.clear();
                            }
                        });

                        LogManager.i("[MainActivity] [onReceive] RPY_ERROR_MSG_CLR 성공");
                    } else if ((bytes[command.getBytes().length + 1] & 0xff) == 0xff) {
                        m_ReplyErrorMsgClearChecked = false;
                        LogManager.i("[MainActivity] [onReceive] RPY_ERROR_MSG_CLR 실패");
                    }
                } else if (command.equals("ERROR_MSG_CLR")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            errorList.clear();
                        }
                    });
                } else if (command.equals("RPY_ACCELERATE_MODE_ON")) {
                    if (bytes[command.getBytes().length + 1] == 0x00) {
                        m_ReplyAccelerateModeOnChecked = true;
                        Log.i(TAG, "RPY_ACCELERATE_MODE_ON 성공");
                    } else if ((bytes[command.getBytes().length + 1] & 0xff) == 0xff) {
                        m_ReplyAccelerateModeOnChecked = false;
                        Log.i(TAG, "RPY_ACCELERATE_MODE_ON 실패");
                    }
                } else if (command.equals("RPY_ACCELERATE_MODE_OFF")) {
                    if (bytes[command.getBytes().length + 1] == 0x00) {
                        m_ReplyAccelerateModeOffChecked = true;
                        Log.i(TAG, "RPY_ACCELERATE_MODE_OFF 성공");
                    } else if ((bytes[command.getBytes().length + 1] & 0xff) == 0xff) {
                        m_ReplyAccelerateModeOffChecked = false;
                        Log.i(TAG, "RPY_ACCELERATE_MODE_OFF 실패");
                    }
                }
            } catch (Exception exc) {
                LogManager.e("[MainActivity] [onReceive] " + exc.getMessage());
            }
        }
    };

    private int isNotClearCount = 0;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermission();

        socketCheckProgressBar = (ProgressBar)findViewById(R.id.socketCheckProgressBar);
        RotateDrawable rotateDrawable = (RotateDrawable)socketCheckProgressBar.getIndeterminateDrawable();
        rotateDrawable.setToDegrees(0);

        m_CustomBroadCastReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("com.example.myapplication.ControlOn")) {
                    m_Control = 1;
                } else if (intent.getAction().equals("com.example.myapplication.ControlOff")) {
                    m_Control = 0;
                }

                if (intent.getAction().equals("com.example.myapplication.FirstNozelForwardSensorOn"))
                {
                    if (m_LayoutIndex == 2) {
                        if (m_ThirdLayout != null) {
                            TextView centeringFirstForward = m_ThirdLayout.findViewById(R.id.centeringFirstForwardSensor);
                            centeringFirstForward.setBackgroundColor(Color.GREEN);
                        }
                    } else if (m_LayoutIndex == 4) {
                        if (m_FifthLayout != null) {
                            TextView cleaningFirstForward = m_FifthLayout.findViewById(R.id.cleaningFirstForwardSensor);
                            cleaningFirstForward.setBackgroundColor(Color.GREEN);
                        }
                    } else if (m_LayoutIndex == 6) {
                        if (m_ManualLayout != null) {
                            // Log.i(TAG, "액티비티 : 노즐 #1 센서 ON");
                            LogManager.i("[MainActivity] [onCreate] 액티비티 : 노즐 #1 센서 ON");
                            TextView manualFirstForward = m_ManualLayout.findViewById(R.id.manualFirstForwardSensor);
                            manualFirstForward.setBackgroundColor(Color.GREEN);
                        }
                    }

                    if (isCenteringShowed) {
                        TextView tubesheetFirstForward = m_Centering.findViewById(R.id.tubesheetFirstForwardSensor);
                        tubesheetFirstForward.setBackgroundColor(Color.GREEN);
                    }
                } else if (intent.getAction().equals("com.example.myapplication.FirstNozelForwardSensorOff")) {
                    if (m_LayoutIndex == 2) {
                        if (m_ThirdLayout != null) {
                            TextView centeringFirstForward = m_ThirdLayout.findViewById(R.id.centeringFirstForwardSensor);
                            centeringFirstForward.setBackgroundColor(Color.RED);
                        }
                    } else if (m_LayoutIndex == 4) {
                        if (m_FifthLayout != null) {
                            TextView cleaningFirstForward = m_FifthLayout.findViewById(R.id.cleaningFirstForwardSensor);
                            cleaningFirstForward.setBackgroundColor(Color.RED);
                        }
                    } else if (m_LayoutIndex == 6) {
                        if (m_ManualLayout != null) {
                            // Log.i(TAG, "액티비티 : 노즐 #1 센서 OFF");
                            LogManager.i("[MainActivity] [onCreate] 액티비티 : 노즐 #1 센서 OFF");
                            TextView manualFirstForward = m_ManualLayout.findViewById(R.id.manualFirstForwardSensor);
                            manualFirstForward.setBackgroundColor(Color.RED);
                        }
                    }

                    if (isCenteringShowed) {
                        TextView tubesheetFirstForward = m_Centering.findViewById(R.id.tubesheetFirstForwardSensor);
                        tubesheetFirstForward.setBackgroundColor(Color.RED);
                    }
                }

                if (intent.getAction().equals("com.example.myapplication.SecondNozelForwardSensorOn"))
                {
                    if (m_LayoutIndex == 2) {
                        if (m_ThirdLayout != null) {
                            TextView centeringSecondForward = m_ThirdLayout.findViewById(R.id.ceteringSecondForwardSensor);
                            centeringSecondForward.setBackgroundColor(Color.GREEN);
                        }
                    } else if (m_LayoutIndex == 4) {
                        if (m_FifthLayout != null) {
                            TextView cleaningSecondForward = m_FifthLayout.findViewById(R.id.cleaningSecondForwardSensor);
                            cleaningSecondForward.setBackgroundColor(Color.GREEN);
                        }
                    } else if (m_LayoutIndex == 6) {
                        if (m_ManualLayout != null) {
                            TextView manualFirstforward = m_ManualLayout.findViewById(R.id.manualSecondForwardSensor);
                            manualFirstforward.setBackgroundColor(Color.GREEN);
                        }
                    }

                    if (isCenteringShowed) {
                        TextView tubesheetFirstForward = m_Centering.findViewById(R.id.tubesheetSecondForwardSensor);
                        tubesheetFirstForward.setBackgroundColor(Color.GREEN);
                    }
                } else if (intent.getAction().equals("com.example.myapplication.SecondNozelForwardSensorOff")) {
                    if (m_LayoutIndex == 2) {
                        if (m_ThirdLayout != null) {
                            TextView centeringSecondForward = m_ThirdLayout.findViewById(R.id.ceteringSecondForwardSensor);
                            centeringSecondForward.setBackgroundColor(Color.RED);
                        }
                    } else if (m_LayoutIndex == 4) {
                        if (m_FifthLayout != null) {
                            TextView cleaningSecondForward = m_FifthLayout.findViewById(R.id.cleaningSecondForwardSensor);
                            cleaningSecondForward.setBackgroundColor(Color.RED);
                        }
                    } else if (m_LayoutIndex == 6) {
                        if (m_ManualLayout != null) {
                            TextView manualFirstforward = m_ManualLayout.findViewById(R.id.manualSecondForwardSensor);
                            manualFirstforward.setBackgroundColor(Color.RED);
                        }
                    }

                    if (isCenteringShowed) {
                        TextView tubesheetFirstForward = m_Centering.findViewById(R.id.tubesheetSecondForwardSensor);
                        tubesheetFirstForward.setBackgroundColor(Color.RED);
                    }
                }

                if (intent.getAction().equals("com.example.myapplication.ThirdNozelForwardSensorOn")) {
                    if (m_LayoutIndex == 2) {
                        if (m_ThirdLayout != null) {
                            TextView centeringThirdForward = m_ThirdLayout.findViewById(R.id.centeringThirdForwardSensor);
                            centeringThirdForward.setBackgroundColor(Color.GREEN);
                        }
                    } else if (m_LayoutIndex == 4) {
                        if (m_FifthLayout != null) {
                            TextView cleaningThirdForward = m_FifthLayout.findViewById(R.id.cleaningThirdForwardSensor);
                            cleaningThirdForward.setBackgroundColor(Color.GREEN);
                        }
                    } else if (m_LayoutIndex == 6) {
                        if (m_ManualLayout != null) {
                            TextView manualThirdforward = m_ManualLayout.findViewById(R.id.manualThirdForwardSensor);
                            manualThirdforward.setBackgroundColor(Color.GREEN);
                        }
                    }

                    if (isCenteringShowed) {
                        TextView tubesheetFirstForward = m_Centering.findViewById(R.id.tubesheetThirdForwardSensor);
                        tubesheetFirstForward.setBackgroundColor(Color.GREEN);
                    }
                } else if (intent.getAction().equals("com.example.myapplication.ThirdNozelForwardSensorOff")) {
                    if (m_LayoutIndex == 2) {
                        if (m_ThirdLayout != null) {
                            TextView centeringThirdForward = m_ThirdLayout.findViewById(R.id.centeringThirdForwardSensor);
                            centeringThirdForward.setBackgroundColor(Color.RED);
                        }
                    } else if (m_LayoutIndex == 4) {
                        if (m_FifthLayout != null) {
                            TextView cleaningThirdForward = m_FifthLayout.findViewById(R.id.cleaningThirdForwardSensor);
                            cleaningThirdForward.setBackgroundColor(Color.RED);
                        }
                    } else if (m_LayoutIndex == 6) {
                        if (m_ManualLayout != null) {
                            TextView manualThirdforward = m_ManualLayout.findViewById(R.id.manualThirdForwardSensor);
                            manualThirdforward.setBackgroundColor(Color.RED);
                        }
                    }

                    if (isCenteringShowed) {
                        TextView tubesheetFirstForward = m_Centering.findViewById(R.id.tubesheetThirdForwardSensor);
                        tubesheetFirstForward.setBackgroundColor(Color.RED);
                    }
                }

                if (intent.getAction().equals("com.example.myapplication.FirstNozelBackwardSensorOn")) {
                    if (m_LayoutIndex == 2) {
                        if (m_ThirdLayout != null) {
                            TextView centeringFirstBackward = m_ThirdLayout.findViewById(R.id.centeringFirstBackwardSensor);
                            centeringFirstBackward.setBackgroundColor(Color.GREEN);
                        }
                    } else if (m_LayoutIndex == 4) {
                        if (m_FifthLayout != null) {
                            TextView cleaningFirstBackward = m_FifthLayout.findViewById(R.id.cleaningFirstBackwardSensor);
                            cleaningFirstBackward.setBackgroundColor(Color.GREEN);
                        }
                    } else if (m_LayoutIndex == 6) {
                        if (m_ManualLayout != null) {
                            TextView manualFirstBackward = m_ManualLayout.findViewById(R.id.manualFirstBackwardSensor);
                            manualFirstBackward.setBackgroundColor(Color.GREEN);
                        }
                    }

                    if (isCenteringShowed) {
                        TextView tubesheetFirstForward = m_Centering.findViewById(R.id.tubesheetFirstBackwardSensor);
                        tubesheetFirstForward.setBackgroundColor(Color.GREEN);
                    }
                } else if (intent.getAction().equals("com.example.myapplication.FirstNozelBackwardSensorOff")) {
                    if (m_LayoutIndex == 2) {
                        if (m_ThirdLayout != null) {
                            TextView centeringFirstBackward = m_ThirdLayout.findViewById(R.id.centeringFirstBackwardSensor);
                            centeringFirstBackward.setBackgroundColor(Color.RED);
                        }
                    } else if (m_LayoutIndex == 4) {
                        if (m_FifthLayout != null) {
                            TextView cleaningFirstBackward = m_FifthLayout.findViewById(R.id.cleaningFirstBackwardSensor);
                            cleaningFirstBackward.setBackgroundColor(Color.RED);
                        }
                    } else if (m_LayoutIndex == 6) {
                        if (m_ManualLayout != null) {
                            TextView manualFirstBackward = m_ManualLayout.findViewById(R.id.manualFirstBackwardSensor);
                            manualFirstBackward.setBackgroundColor(Color.RED);
                        }
                    }

                    if (isCenteringShowed) {
                        TextView tubesheetFirstForward = m_Centering.findViewById(R.id.tubesheetFirstBackwardSensor);
                        tubesheetFirstForward.setBackgroundColor(Color.RED);
                    }
                }

                if (intent.getAction().equals("com.example.myapplication.SecondNozelBackwardSensorOn")) {
                    if (m_LayoutIndex == 2) {
                        if (m_ThirdLayout != null) {
                            TextView centeringSecondBackward = m_ThirdLayout.findViewById(R.id.centeringSecondBackwardSensor);
                            centeringSecondBackward.setBackgroundColor(Color.GREEN);
                        }
                    } else if (m_LayoutIndex == 4) {
                        if (m_FifthLayout != null) {
                            TextView cleaningSecondBackward = m_FifthLayout.findViewById(R.id.cleaningSecondBackwardSensor);
                            cleaningSecondBackward.setBackgroundColor(Color.GREEN);
                        }
                    } else if (m_LayoutIndex == 6) {
                        if (m_ManualLayout != null) {
                            TextView manualSecondBackward = m_ManualLayout.findViewById(R.id.manualSecondBackwardSensor);
                            manualSecondBackward.setBackgroundColor(Color.GREEN);
                        }
                    }

                    if (isCenteringShowed) {
                        TextView tubesheetFirstForward = m_Centering.findViewById(R.id.tubesheetSecondBackwardSensor);
                        tubesheetFirstForward.setBackgroundColor(Color.GREEN);
                    }
                } else if (intent.getAction().equals("com.example.myapplication.SecondNozelBackwardSensorOff")) {
                    if (m_LayoutIndex == 2) {
                        if (m_ThirdLayout != null) {
                            TextView centeringSecondBackward = m_ThirdLayout.findViewById(R.id.centeringSecondBackwardSensor);
                            centeringSecondBackward.setBackgroundColor(Color.RED);
                        }
                    } else if (m_LayoutIndex == 4) {
                        if (m_FifthLayout != null) {
                            TextView cleaningSecondBackward = m_FifthLayout.findViewById(R.id.cleaningSecondBackwardSensor);
                            cleaningSecondBackward.setBackgroundColor(Color.RED);
                        }
                    } else if (m_LayoutIndex == 6) {
                        if (m_ManualLayout != null) {
                            TextView manualSecondBackward = m_ManualLayout.findViewById(R.id.manualSecondBackwardSensor);
                            manualSecondBackward.setBackgroundColor(Color.RED);
                        }
                    }

                    if (isCenteringShowed) {
                        TextView tubesheetFirstForward = m_Centering.findViewById(R.id.tubesheetSecondBackwardSensor);
                        tubesheetFirstForward.setBackgroundColor(Color.RED);
                    }
                }

                if (intent.getAction().equals("com.example.myapplication.ThirdNozelBackwardSensorOn")) {
                    if (m_LayoutIndex == 2) {
                        if (m_ThirdLayout != null) {
                            TextView centeringThirdBackward = m_ThirdLayout.findViewById(R.id.centeringThirdBackwardSensor);
                            centeringThirdBackward.setBackgroundColor(Color.GREEN);
                        }
                    } else if (m_LayoutIndex == 4) {
                        if (m_FifthLayout != null) {
                            TextView cleaningThirdBackward = m_FifthLayout.findViewById(R.id.cleaningThirdBackwardSensor);
                            cleaningThirdBackward.setBackgroundColor(Color.GREEN);
                        }
                    } else if (m_LayoutIndex == 6) {
                        if (m_ManualLayout != null) {
                            TextView manualThirdBackward = m_ManualLayout.findViewById(R.id.manualThirdBackwardSensor);
                            manualThirdBackward.setBackgroundColor(Color.GREEN);
                        }
                    }

                    if (isCenteringShowed) {
                        TextView tubesheetFirstForward = m_Centering.findViewById(R.id.tubesheetThirdBackwardSensor);
                        tubesheetFirstForward.setBackgroundColor(Color.GREEN);
                    }
                } else if (intent.getAction().equals("com.example.myapplication.ThirdNozelBackwardSensorOff")) {
                    if (m_LayoutIndex == 2) {
                        if (m_ThirdLayout != null) {
                            TextView centeringThirdBackward = m_ThirdLayout.findViewById(R.id.centeringThirdBackwardSensor);
                            centeringThirdBackward.setBackgroundColor(Color.RED);
                        }
                    } else if (m_LayoutIndex == 4) {
                        if (m_FifthLayout != null) {
                            TextView cleaningThirdBackward = m_FifthLayout.findViewById(R.id.cleaningThirdBackwardSensor);
                            cleaningThirdBackward.setBackgroundColor(Color.RED);
                        }
                    } else if (m_LayoutIndex == 6) {
                        if (m_ManualLayout != null) {
                            TextView manualThirdBackward = m_ManualLayout.findViewById(R.id.manualThirdBackwardSensor);
                            manualThirdBackward.setBackgroundColor(Color.RED);
                        }
                    }

                    if (isCenteringShowed) {
                        TextView tubesheetFirstForward = m_Centering.findViewById(R.id.tubesheetThirdBackwardSensor);
                        tubesheetFirstForward.setBackgroundColor(Color.RED);
                    }
                } else if (intent.getAction().equals("com.example.myapplication.SOCKET_CHECK")) {
                    serviceSocketChecked = true;
                } else if (intent.getAction().equals("com.example.myapplication.SOCKET_NOT_CHECK")) {
                    serviceSocketChecked = false;
                }
            }
        };

        IntentFilter m_Filter = new IntentFilter();
        m_Filter.addAction("com.example.myapplication.SOCKET_CHECK");
        m_Filter.addAction("com.example.myapplication.SOCKET_NOT_CHECK");
        m_Filter.addAction("com.example.myapplication.NONE");
        m_Filter.addAction("com.example.myapplication.AUTO");
        m_Filter.addAction("com.example.myapplication.MANUAL");
        m_Filter.addAction("com.example.myapplication.EMERGENCY");
        m_Filter.addAction("com.example.myapplication.BUNDLE_SETTING");
        m_Filter.addAction("com.example.myapplication.ORIGIN_SETTING");
        m_Filter.addAction("com.example.myapplication.SCANNING");
        m_Filter.addAction("com.example.myapplication.CLEANING");
        m_Filter.addAction("com.example.myapplication.FINISH");
        m_Filter.addAction("com.example.myapplication.ORIGIN_STEP_LEFT_TOP");
        m_Filter.addAction("com.example.myapplication.ORIGIN_STEP_RIGHT_BOTTOM");
        m_Filter.addAction("com.example.myapplication.ControlOn");
        m_Filter.addAction("com.example.myapplication.ControlOff");
        m_Filter.addAction("com.example.myapplication.FirstNozelForwardSensorOn");
        m_Filter.addAction("com.example.myapplication.FirstNozelForwardSensorOff");
        m_Filter.addAction("com.example.myapplication.SecondNozelForwardSensorOn");
        m_Filter.addAction("com.example.myapplication.SecondNozelForwardSensorOff");
        m_Filter.addAction("com.example.myapplication.ThirdNozelForwardSensorOn");
        m_Filter.addAction("com.example.myapplication.ThirdNozelForwardSensorOff");
        m_Filter.addAction("com.example.myapplication.FirstNozelBackwardSensorOn");
        m_Filter.addAction("com.example.myapplication.FirstNozelBackwardSensorOff");
        m_Filter.addAction("com.example.myapplication.SecondNozelBackwardSensorOn");
        m_Filter.addAction("com.example.myapplication.SecondNozelBackwardSensorOff");
        m_Filter.addAction("com.example.myapplication.ThirdNozelBackwardSensorOn");
        m_Filter.addAction("com.example.myapplication.ThirdNozelBackwardSensorOff");
        registerReceiver(this.m_CustomBroadCastReceiver, m_Filter);
        boolean isServiceRunning = isServiceRunning(getApplicationContext());

        if (isServiceRunning) {
            // Log.i("ContentValues", "실행 중인 서비스 존재");
        } else {
            // Log.i("ContentValues", "실행 중인 서비스 미존재");
        }

        if (!isServiceRunning) {
            if (Build.VERSION.SDK_INT >= 26) {
                Log.d("ContentValues", "안드로이드 버젼 오레오 이상");
                Intent intent = new Intent(getApplicationContext(), CustomService.class);
                this.m_ServiceIntent = intent;
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startForegroundService(this.m_ServiceIntent);
            } else {
                Log.d("ContentValues", "안드로이드 버젼 오레오 이하");
                Intent intent2 = new Intent(getApplicationContext(), CustomService.class);
                this.m_ServiceIntent = intent2;
                intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startService(this.m_ServiceIntent);
            }
        }

        m_FirstLayout = (LinearLayout)findViewById(R.id.firstLayout);
        m_SecondLayout = (LinearLayout)findViewById(R.id.secondLayout);
        m_ThirdLayout = (LinearLayout)findViewById(R.id.thirdLayout);
        m_FourthLayout = (LinearLayout)findViewById(R.id.fourthLayout);
        m_FifthLayout = (LinearLayout)findViewById(R.id.fifthLayout);
        m_SixthLayout = (GridLayout)findViewById(R.id.sixthLayout);
        m_EditHoleLayout = (LinearLayout)findViewById(R.id.editHoleLayout);
        m_ManualLayout = (LinearLayout) findViewById(R.id.manualLayout);

        setFirstLayout();

        m_MainSocketClient = new AsyncSocketClient(0);
        m_MainSocketClientCheckThread = new Thread(mainSocketClientCheckThreadDo);
        m_MainSocketClientCheckThread.start();

        m_SocketCheckThread = new Thread(socketCheckThreadDo);
        m_SocketCheckThread.start();

        LogManager.i("[MainActivity] [onCreate] 앱 실행");
    }

    private Runnable mainSocketClientCheckThreadDo = new Runnable() {
        @Override
        public void run() {
            while (m_IsRunning) {
                if (!m_MainSocketClient.isConnected()) {
                    m_MainSocketClient.close();
                    m_MainSocketClient = null;

                    SharedPreferences pref = getApplicationContext().getSharedPreferences("rebuild_preference", MODE_PRIVATE);
                    String m_IPAddress = pref.getString("ipAddress", "192.168.1.9");
                    int m_Port = pref.getInt("port", 9601);
                    // Log.i("ContentValues", "접속할 IPAddress : " + m_IPAddress);
                    // Log.i("ContentValues", "포트번호 : " + m_Port);

                    m_MainSocketClient = new AsyncSocketClient(0);
                    m_MainSocketClient.onAccept.addEventHandler(onAccept);
                    m_MainSocketClient.onConnect.addEventHandler(onConnect);
                    m_MainSocketClient.onError.addEventHandler(onError);
                    m_MainSocketClient.onReceive.addEventHandler(onReceive);
                    m_MainSocketClient.onSend.addEventHandler(onSend);
                    m_MainSocketClient.onClose.addEventHandler(onClose);
                    m_MainSocketClient.connect(m_IPAddress, m_Port);
                }

                try {
                    Thread.sleep(5000);
                } catch (Exception e) {
                    // LogManager.e("[MainActivity] [mainSocketClientCheckThreadDo] " + e.getMessage());
                }
            }
        }
    };

    private int commandSocketCheckMilliSeconds = 0;
    private String preTime = "";

    private Runnable socketCheckThreadDo = new Runnable() {
        @Override
        public void run() {
            try {
                while (m_IsRunning) {
                    if (m_MainSocketClient == null || !m_MainSocketClient.isConnected() || !serviceSocketChecked) {
                        socketCheckProgressBar.post(new Runnable() {
                            @Override
                            public void run() {
                                RotateDrawable rotateDrawable = (RotateDrawable)socketCheckProgressBar.getIndeterminateDrawable();
                                rotateDrawable.setToDegrees(0);
                                m_Control = 1;
                            }
                        });
                    } else {
                        socketCheckProgressBar.post(new Runnable() {
                            @Override
                            public void run() {
                                RotateDrawable rotateDrawable = (RotateDrawable)socketCheckProgressBar.getIndeterminateDrawable();
                                rotateDrawable.setToDegrees(360);
                                m_Control = 0;
                            }
                        });
                    }

                    Thread.sleep(1000);

                    commandSocketCheckMilliSeconds = commandSocketCheckMilliSeconds + 1000;

                    long now = System.currentTimeMillis();
                    Date date = new Date(now);
                    SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
                    String time = format.format(date);

                    if (!preTime.equals(time)) {
                        checkPermission();
                        LogManager.update();
                        preTime = time;
                    }
                }
            } catch (Exception e) {
                LogManager.e("[MainActivity] [socketCheckThreadDo] " + e.getMessage());
            }
        }
    };

    // 0이면 대기, 255이면 실패, 2이면 성공
    private Bitmap m_Bitmap;
    private Bitmap m_CopyBitmap;

    private final int PERMISSIONS_REQUEST_CODE = 1;

    public void checkPermission() {
        boolean shouldProviceRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE);//사용자가 이전에 거절한적이 있어도 true 반환

        boolean shouldProviceRationale2 =
                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (shouldProviceRationale && shouldProviceRationale2) {
            //앱에 필요한 권한이 없어서 권한 요청
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);

            //권한있을때.
            //오레오부터 꼭 권한체크내에서 파일 만들어줘야함
            makeDir();
            LogManager.update();
        }

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        String time = format.format(date);
        preTime = time;

        if (Build.VERSION.SDK_INT < 23) {
            startService(new Intent(this, CustomService.class));
        } else if (!Settings.canDrawOverlays(this)) {
            startActivityForResult(new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION", Uri.parse("package:" + getPackageName())), 1);
        } else {
            startService(new Intent(this, CustomService.class));
        }
    }

    public void makeDir() {
        String root = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download"; //내장에 만든다
        String directoryName = "Portable Auto Cleaner";
        final File myDir = new File(root + "/" + directoryName);

        if (!myDir.exists()) {
            Log.i(TAG, myDir.getAbsolutePath() + " 폴더 생성 존재하지 않음.");

            boolean result = false;

            while (!result) {
                result = myDir.mkdirs();

                if (result) {
                    Log.i(TAG, myDir.getAbsolutePath() + " 폴더 생성 완료");
                } else {
                    Log.i(TAG, myDir.getAbsolutePath() + " 폴더 생성 실패");
                }
            }
        } else {
            Log.i(TAG, myDir.getAbsolutePath() + " 폴더 생성 이미 존재");
        }

        File dir = new File(myDir.getAbsolutePath() + "/logs");

        if (!dir.exists()) {
            Log.i(TAG, dir.getAbsolutePath() + " 폴더 생성 존재하지 않음.");

            boolean result = false;

            while (!result) {
                result = dir.mkdirs();

                if (result) {
                    Log.i(TAG, dir.getAbsolutePath() + " 폴더 생성 완료");
                } else {
                    Log.i(TAG, dir.getAbsolutePath() + " 폴더 생성 실패");
                }
            }
        } else {
            Log.i(TAG, dir.getAbsolutePath() + " 폴더 생성 이미 존재");
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && Settings.canDrawOverlays(this)) {
            startService(new Intent(this, CustomService.class));
        }
    }

    private boolean isServiceRunning(Context context) {
        ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo rsi : am.getRunningServices(Integer.MAX_VALUE)) {
            if (CustomService.class.getName().equals(rsi.service.getClassName())) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(m_CustomBroadCastReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();

        // disconnectSocket();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // disconnectSocket();
    }

    private void setFirstLayout() {
        m_FirstLayout.post(new Runnable() {
            @Override
            public void run() {
                m_FirstLayout.setVisibility(View.VISIBLE);
            }
        });

        m_SecondLayout.post(new Runnable() {
            @Override
            public void run() {
                m_SecondLayout.setVisibility(View.GONE);
            }
        });

        m_ThirdLayout.post(new Runnable() {
            @Override
            public void run() {
                m_ThirdLayout.setVisibility(View.GONE);
            }
        });

        m_FourthLayout.post(new Runnable() {
            @Override
            public void run() {
                m_FourthLayout.setVisibility(View.GONE);
            }
        });

        m_FifthLayout.post(new Runnable() {
            @Override
            public void run() {
                m_FifthLayout.setVisibility(View.GONE);
            }
        });

        m_SixthLayout.post(new Runnable() {
            @Override
            public void run() {
                m_SixthLayout.setVisibility(View.GONE);
            }
        });

        m_EditHoleLayout.post(new Runnable() {
            @Override
            public void run() {
                m_EditHoleLayout.setVisibility(View.GONE);
            }
        });

        if (m_ManualLayout != null)
        {
            m_ManualLayout.post(new Runnable() {
                @Override
                public void run() {
                    m_ManualLayout.setVisibility(View.GONE);
                }
            });
        }

        setFirstButton();
        this.m_LayoutIndex = 0;
    }

    private boolean isCenteringShowed = false;
    private Dialog m_Centering;

    private boolean centeringAccelerateMode = true;

    private void setFirstButton() {
        m_CenteringButton = (Button) findViewById(R.id.centeringButton);
        m_SettingButton = (ImageView)findViewById(R.id.settingButton);
        m_StartButton = (Button) findViewById(R.id.startButton);
        m_RelayButton = (Button) findViewById(R.id.relayButton);
        m_ManualButton = (Button) findViewById(R.id.manualButton);
        m_TestButton = (Button) findViewById(R.id.testButton);
        Button m_CloseButton = (Button)findViewById(R.id.closeButton);

        m_CenteringButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Log.w(TAG, "센터링 버튼 클릭");

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String sendStr = "CENTERING,1,";
                            byte[] sendBytes = sendStr.getBytes(StandardCharsets.US_ASCII);
                            m_MainSocketClient.send(sendBytes);
                        } catch (Exception e) {
                            LogManager.e("[MainActivity] [m_CenteringButton.setOnClickListener] " + e.getMessage());
                        }
                    }
                }).start();

                m_Centering = new Dialog(MainActivity.this);
                m_Centering.requestWindowFeature(1);
                m_Centering.setContentView(R.layout.centering);
                ((ImageView) m_Centering.findViewById(R.id.centeringView)).setImageBitmap(((BitmapDrawable)MainActivity.this.getResources().getDrawable(R.drawable.tubesheet_centering_setting)).getBitmap());

                ((Button)m_Centering.findViewById(R.id.centeringConfirmButton)).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    String sendStr = "CENTERING,0,";
                                    byte[] sendBytes = sendStr.getBytes(StandardCharsets.US_ASCII);
                                    m_MainSocketClient.send(sendBytes);
                                } catch (Exception e) {
                                    LogManager.e("[MainActivity] [m_Centering.findViewById(R.id.centeringConfirmButton)).setOnClickListener] " + e.getMessage());
                                }
                            }
                        }).start();

                        m_Centering.dismiss();
                        isCenteringShowed = false;
                    }
                });

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String sendStr = "ACCELERATE_MODE_ON,";
                            byte[] sendBytes = sendStr.getBytes(StandardCharsets.US_ASCII);
                            m_MainSocketClient.send(sendBytes);

                            int cnt = 0;

                            while (!m_ReplyAccelerateModeOnChecked) {
                                // Log.e(TAG, "가속모드 온 체크 여부" + m_ReplyAccelerateModeOnChecked);
                                Thread.sleep(10);
                                cnt++;

                                if (cnt > 100) {
                                    break;
                                }

                                errorCheck("ACCELERATE_MODE_ON,");
                            }

                            m_ReplyAccelerateModeOnChecked = false;
                        } catch (Exception e) {

                        }
                    }
                }).start();

                centeringAccelerateMode = true;

                ToggleButton accelerateButton = (ToggleButton)m_Centering.findViewById(R.id.tubesheetAccelerateButton);
                accelerateButton.setTextOn("가속 모드");
                accelerateButton.setTextOff("가속 모드");
                accelerateButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.toggle_button_on, null));
                accelerateButton.setChecked(true);
                accelerateButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!centeringAccelerateMode) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        String sendStr = "ACCELERATE_MODE_ON,";
                                        byte[] sendBytes = sendStr.getBytes(StandardCharsets.US_ASCII);
                                        m_MainSocketClient.send(sendBytes);

                                        int cnt = 0;

                                        while (!m_ReplyAccelerateModeOnChecked) {
                                            // Log.e(TAG, "가속모드 온 체크 여부" + m_ReplyAccelerateModeOnChecked);
                                            Thread.sleep(10);
                                            cnt++;

                                            if (cnt > 100) {
                                                break;
                                            }

                                            errorCheck("ACCELERATE_MODE_ON,");
                                        }

                                        if (m_ReplyAccelerateModeOnChecked) {
                                            // Log.i(TAG, "가속 모드 온 체크");
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    accelerateButton.setChecked(true);
                                                    accelerateButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.toggle_button_on, null));
                                                    centeringAccelerateMode = true;
                                                }
                                            });
                                        }

                                        m_ReplyAccelerateModeOnChecked = false;
                                    } catch (Exception e) {
                                        LogManager.e("[MainActivity] [setManualButton] 가속 모드 켜기 커맨드 전송 에러 : " + e.getMessage());
                                        Log.e(TAG, "[MainActivity] [setManualButton] 가속 모드 켜기 커맨드 전송 에러 : " + e.getMessage());
                                    }
                                }
                            }).start();
                        } else {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        String sendStr = "ACCELERATE_MODE_OFF,";
                                        byte[] sendBytes = sendStr.getBytes(StandardCharsets.US_ASCII);
                                        m_MainSocketClient.send(sendBytes);

                                        int cnt = 0;

                                        while (!m_ReplyAccelerateModeOffChecked) {
                                            // Log.e(TAG, "가속모드 오프 체크 여부" + m_ReplyAccelerateModeOffChecked);
                                            Thread.sleep(10);
                                            cnt++;

                                            if (cnt > 100) {
                                                break;
                                            }

                                            errorCheck("ACCELERATE_MODE_OFF,");
                                        }

                                        if (m_ReplyAccelerateModeOffChecked) {
                                            // Log.i(TAG, "가속 모드 오프 체크");
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    accelerateButton.setChecked(false);
                                                    accelerateButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.toggle_button_off, null));
                                                    centeringAccelerateMode = false;
                                                }
                                            });
                                        }

                                        m_ReplyAccelerateModeOffChecked = false;
                                    } catch (Exception e) {
                                        LogManager.e("[MainActivity] [setManualButton] 가속 모드 끄기 커맨드 전송 에러 : " + e.getMessage());
                                        Log.e(TAG, "[MainActivity] [setManualButton] 가속 모드 끄기 커맨드 전송 에러 : " + e.getMessage());
                                    }
                                }
                            }).start();
                        }
                    }
                });

                Button m_TubesheetLaserOnButton = (Button)m_Centering.findViewById(R.id.tubesheetLaserOnButton);
                m_TubesheetLaserOnButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    String sendStr = "CENTERING,1,";
                                    byte[] sendBytes = sendStr.getBytes(StandardCharsets.US_ASCII);
                                    m_MainSocketClient.send(sendBytes);
                                } catch (Exception e) {
                                    LogManager.e("[MainActivity] [setThirdLayout.centeringLaserOnButton.setOnClickListener] " + e.getMessage());
                                }
                            }
                        }).start();
                    }
                });

                Button m_TubesheetLaserOffButton = (Button)m_Centering.findViewById(R.id.tubesheetLaserOffButton);
                m_TubesheetLaserOffButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    String sendStr = "CENTERING,0,";
                                    byte[] sendBytes = sendStr.getBytes(StandardCharsets.US_ASCII);
                                    m_MainSocketClient.send(sendBytes);
                                } catch (Exception e) {
                                    LogManager.e("[MainActivity] [setThirdLayout.centeringLaserOnButton.setOnClickListener] " + e.getMessage());
                                }
                            }
                        }).start();
                    }
                });

                Button m_TubesheetJogUpButton = (Button)m_Centering.findViewById(R.id.tubesheetJogUpButton);
                m_TubesheetJogUpButton.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getActionMasked()) {
                            case 0:
                                if (m_Control == 0) {
                                    sendBroadcast(new Intent("com.example.myapplication.JogUpButtonDown"));
                                }

                                return false;
                            case 1:
                                if (m_Control == 0) {
                                    sendBroadcast(new Intent("com.example.myapplication.JogUpButtonUp"));
                                }
                                return false;
                            default:
                                return false;
                        }
                    }
                });

                Button m_TubesheetJogLeftButton = (Button)m_Centering.findViewById(R.id.tubesheetJogLeftButton);
                m_TubesheetJogLeftButton.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getActionMasked()) {
                            case 0:
                                if (m_Control == 0) {
                                    sendBroadcast(new Intent("com.example.myapplication.JogLeftButtonDown"));
                                }

                                return false;
                            case 1:
                                if (m_Control == 0) {
                                    sendBroadcast(new Intent("com.example.myapplication.JogLeftButtonUp"));
                                }
                                return false;
                            default:
                                return false;
                        }
                    }
                });

                Button m_TubesheetJogRightButton = (Button)m_Centering.findViewById(R.id.tubesheetJogRightButton);
                m_TubesheetJogRightButton.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getActionMasked()) {
                            case 0:
                                if (m_Control == 0) {
                                    sendBroadcast(new Intent("com.example.myapplication.JogRightButtonDown"));
                                }

                                return false;
                            case 1:
                                if (m_Control == 0) {
                                    sendBroadcast(new Intent("com.example.myapplication.JogRightButtonUp"));
                                }
                                return false;
                            default:
                                return false;
                        }
                    }
                });

                Button m_TubesheetJogDownButton = (Button)m_Centering.findViewById(R.id.tubesheetJogDownButton);
                m_TubesheetJogDownButton.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getActionMasked()) {
                            case 0:
                                if (m_Control == 0) {
                                    sendBroadcast(new Intent("com.example.myapplication.JogDownButtonDown"));
                                }

                                return false;
                            case 1:
                                if (m_Control == 0) {
                                    sendBroadcast(new Intent("com.example.myapplication.JogDownButtonUp"));
                                }
                                return false;
                            default:
                                return false;
                        }
                    }
                });

                Button m_TubesheetNozelForwardButton = (Button)m_Centering.findViewById(R.id.tubesheetNozelForwardButton);
                m_TubesheetNozelForwardButton.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getActionMasked()) {
                            case 0:
                                if (m_Control == 0) {
                                    sendBroadcast(new Intent("com.example.myapplication.allNozelForwardButtonDown"));
                                }

                                return false;
                            case 1:
                                if (m_Control == 0) {
                                    sendBroadcast(new Intent("com.example.myapplication.allNozelForwardButtonUp"));
                                }

                                return false;
                            default:
                                return false;
                        }
                    }
                });

                Button m_TubesheetNozelBackwardButton = (Button)m_Centering.findViewById(R.id.tubesheetNozelBackwardButton);
                m_TubesheetNozelBackwardButton.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getActionMasked()) {
                            case 0:
                                if (m_Control == 0) {
                                    sendBroadcast(new Intent("com.example.myapplication.allNozelBackwardButtonDown"));
                                }

                                return false;
                            case 1:
                                if (m_Control == 0) {
                                    sendBroadcast(new Intent("com.example.myapplication.allNozelBackwardButtonUp"));
                                }

                                return false;
                            default:
                                return false;
                        }
                    }
                });

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(m_Centering.getWindow().getAttributes());
                lp.width = -1;
                lp.height = -1;
                m_Centering.getWindow().setAttributes(lp);
                m_Centering.show();
                isCenteringShowed = true;
            }
        });

        m_StartButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (m_Control == 0) {
                    isTestMode = false;
                    isRelayMode = false;
                    isRelay = false;
                    isCompleted = false;
                    m_ReplyStartChecked = false;
                    m_ReplyCleaningDataChecked = false;
                    m_ReplyStartCleaningChecked = false;
                    m_ReplyStartScanChecked = false;
                    m_ReplyOriginLeftTopChecked = false;
                    m_ReplyScanHolesChecked = false;
                    m_ReplyCancelChecked = false;
                    m_ReplyPauseChecked = false;
                    setSecondLayout();
                }
            }
        });

        m_TestButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (m_Control == 0) {
                    isTestMode = true;
                    isRelayMode = false;
                    isRelay = false;
                    isCompleted = false;
                    m_StartChecked = true;
                    m_ReplyStartChecked = false;
                    m_ReplyCleaningDataChecked = false;
                    m_ReplyStartCleaningChecked = false;
                    m_ReplyStartScanChecked = false;
                    m_ReplyOriginLeftTopChecked = false;
                    m_ReplyScanHolesChecked = false;
                    m_ReplyCancelChecked = false;
                    m_ReplyPauseChecked = false;
                    isPause = false;
                    m_HoleMaxNum = -9999;
                    m_HoleMinNum = 9999;
                    m_MotorPositionSettingNum = 0;
                    m_ScanImageDownloaded = false;

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String sendStr = "";

                                sendStr = "TEST_START,TEST,0,";

                                byte[] sendBytes = sendStr.getBytes(StandardCharsets.US_ASCII);
                                m_MainSocketClient.send(sendBytes);

                                int cnt = 0;

                                while (!m_ReplyStartChecked) {
                                    Thread.sleep(10);
                                    cnt++;

                                    if (cnt > 100) {
                                        break;
                                    }
                                }

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        setThirdLayout();
                                    }
                                });
                            } catch (Exception e) {
                                LogManager.e("[MainActivity] [m_TestButton.setOnClickListener]" + e.getMessage());
                            }
                        }
                    }).start();
                }
            }
        });

        m_CloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBroadcast(new Intent("com.example.myapplication.close"));
                Intent intent = new Intent(getApplicationContext(), CustomService.class);
                stopService(intent);
                finish();
                Toast.makeText(MainActivity.this, "종료되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        m_RelayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (m_Control == 0) {
                    isTestMode = false;
                    isRelayMode = true;
                    isRelay = true;
                    isCompleted = false;
                    // setSecondLayout();
                    m_ReplyStartChecked = false;
                    m_ReplyStartCleaningChecked = false;
                    m_ReplyStartScanChecked = true;
                    m_ReplyOriginLeftTopChecked = false;
                    m_ReplyScanHolesChecked = false;
                    m_ReplyCancelChecked = false;
                    m_ReplyPauseChecked = false;
                    m_ScanImageDownloaded = false;

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String sendStr = "RELAY_START,";
                                byte[] sendBytes = sendStr.getBytes(StandardCharsets.US_ASCII);
                                m_MainSocketClient.send(sendBytes);

                                int cnt = 0;

                                while (!m_ReplyStartChecked) {
                                    Thread.sleep(10);
                                    cnt++;

                                    if (cnt > 100) {
                                        break;
                                    }

                                    errorCheck("RELAY_START,");
                                }

                                m_ReplyStartChecked = false;

                                sendStr = "SCAN_IMAGE,";
                                sendBytes = sendStr.getBytes(StandardCharsets.US_ASCII);
                                // send(sendBytes);
                                m_MainSocketClient.send(sendBytes);

                                m_ReplyScanImageChecked = false;

                                cnt = 0;

                                while (!m_ReplyScanImageChecked) {
                                    Thread.sleep(10);
                                    cnt++;

                                    if (cnt > 100) {
                                        break;
                                    }

                                    errorCheck("SCAN_IMAGE,");
                                }

                                m_ReplyScanImageChecked = false;
                            } catch (Exception e) {
                                LogManager.e("[MainActivity] [m_RelayButton.setOnClickListener] " + e.getMessage());
                            }
                        }
                    }).start();
                }
            }
        });

        m_SettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingButtonClickCount++;

                if (settingButtonClickCount == 2 && settingButtonClickMilliSeconds < 1000) {
                    m_SettingDialog = new Dialog(MainActivity.this);
                    m_SettingDialog.requestWindowFeature(1);
                    m_SettingDialog.setContentView(R.layout.setting_layout);
                    m_SettingListView = (LinearLayout)m_SettingDialog.findViewById(R.id.settingListView);
                    SharedPreferences pref = MainActivity.this.getApplicationContext().getSharedPreferences("rebuild_preference", MODE_PRIVATE);
                    final SharedPreferences.Editor editor = pref.edit();
                    String mainIpAddress = pref.getString("mainIpAddress", "192.168.1.9");
                    int mainPort = pref.getInt("mainPort", 9600);
                    final EditText mainIpAddressText = (EditText) m_SettingListView.findViewById(R.id.mainIpAddressEditText);
                    final EditText mainPortText = (EditText) m_SettingListView.findViewById(R.id.mainPortEditText);
                    mainIpAddressText.setText(mainIpAddress);
                    mainPortText.setText(mainPort + "");
                    // Log.i(TAG, "MainIPAddress : " + mainIpAddress);
                    // Log.i(TAG, "MainPort : " + mainPort);

                    EditText ipAddressText = m_SettingListView.findViewById(R.id.ipAddressEditText);
                    EditText portText = m_SettingListView.findViewById(R.id.portEditText);
                    String ipAddress = pref.getString("ipAddress", "192.168.1.9");
                    int port = pref.getInt("port", 9601);
                    ipAddressText.setText(ipAddress);
                    portText.setText(port + "");

                    EditText holeSizeText = (EditText)m_SettingListView.findViewById(R.id.holeSizeText);
                    int holeSize = pref.getInt("holeSize", 15);
                    holeSizeText.setText(holeSize + "");

                    Button numericUpButton = (Button)m_SettingListView.findViewById(R.id.numericUpButtonChild);
                    numericUpButton.setOnTouchListener(new View.OnTouchListener() {
                        public boolean onTouch(View v, MotionEvent event) {
                            switch (event.getActionMasked()) {
                                case 0:
                                    // Log.i("ContentValues", "ACTION_DOWN");
                                    m_NumericUpButtonTouchDown = true;
                                    new Thread(new Runnable() {
                                        public void run() {
                                            int cnt = 0;
                                            while (m_NumericUpButtonTouchDown) {
                                                try {
                                                    holeSizeText.post(new Runnable() {
                                                        public void run() {
                                                            holeSizeText.setText((Integer.parseInt(holeSizeText.getText().toString()) + 1) + "");
                                                            holeSizeText.setSelection(holeSizeText.length());
                                                        }
                                                    });

                                                    cnt++;

                                                    if (cnt < 5) {
                                                        Thread.sleep(500);
                                                    } else {
                                                        Thread.sleep(50);
                                                    }
                                                } catch (Exception e) {
                                                    LogManager.e("[MainActivity] [numericUpButton.setOnTouchListener] " + e.getMessage());
                                                    return;
                                                }
                                            }
                                        }
                                    }).start();
                                    break;
                                case 1:
                                    m_NumericUpButtonTouchDown = false;
                                    break;
                            }
                            return false;
                        }
                    });

                    Button numericDownButton = (Button)m_SettingListView.findViewById(R.id.numericDownButtonChild);
                    numericDownButton.setOnTouchListener(new View.OnTouchListener() {
                        public boolean onTouch(View v, MotionEvent event) {
                            switch (event.getActionMasked()) {
                                case 0:
                                    // Log.i("ContentValues", "ACTION_DOWN");
                                    MainActivity.this.m_NumericDownButtonTouchDown = true;
                                    new Thread(new Runnable() {
                                        public void run() {
                                            int cnt = 0;
                                            while (m_NumericDownButtonTouchDown) {
                                                try {
                                                    holeSizeText.post(new Runnable() {
                                                        public void run() {
                                                            int parseInt = Integer.parseInt(holeSizeText.getText().toString()) - 1;
                                                            if (parseInt > -1) {
                                                                holeSizeText.setText(parseInt + "");
                                                                holeSizeText.setSelection(holeSizeText.length());
                                                            }
                                                        }
                                                    });
                                                    cnt++;
                                                    if (cnt < 5) {
                                                        Thread.sleep(500);
                                                    } else {
                                                        Thread.sleep(50);
                                                    }
                                                } catch (Exception e) {
                                                    return;
                                                }
                                            }
                                        }
                                    }).start();
                                    break;
                                case 1:
                                    m_NumericDownButtonTouchDown = false;
                                    break;
                            }
                            return false;
                        }
                    });

                    EditText holeThicknessText = (EditText)m_SettingListView.findViewById(R.id.holeThicknessText);
                    int holeThickness = pref.getInt("holeThickness", 5);
                    holeThicknessText.setText(holeThickness + "");

                    Button holeThicknessUpButton = (Button)m_SettingListView.findViewById(R.id.holeThicknessUpButtonChild);
                    holeThicknessUpButton.setOnTouchListener(new View.OnTouchListener() {
                        public boolean onTouch(View v, MotionEvent event) {
                            switch (event.getActionMasked()) {
                                case 0:
                                    // Log.i(TAG, "ACTION_DOWN");
                                    m_NumericUpButtonTouchDown = true;
                                    new Thread(new Runnable() {
                                        public void run() {
                                            int cnt = 0;
                                            while (m_NumericUpButtonTouchDown) {
                                                try {
                                                    holeThicknessText.post(new Runnable() {
                                                        public void run() {
                                                            holeThicknessText.setText((Integer.parseInt(holeThicknessText.getText().toString()) + 1) + "");
                                                            holeThicknessText.setSelection(holeThicknessText.length());
                                                        }
                                                    });

                                                    cnt++;

                                                    if (cnt < 5) {
                                                        Thread.sleep(500);
                                                    } else {
                                                        Thread.sleep(50);
                                                    }
                                                } catch (Exception e) {
                                                    return;
                                                }
                                            }
                                        }
                                    }).start();
                                    break;
                                case 1:
                                    m_NumericUpButtonTouchDown = false;
                                    break;
                            }
                            return false;
                        }
                    });

                    Button holeThicknessDownButton = (Button)m_SettingListView.findViewById(R.id.holeThicknessDownButtonChild);
                    holeThicknessDownButton.setOnTouchListener(new View.OnTouchListener() {
                        public boolean onTouch(View v, MotionEvent event) {
                            switch (event.getActionMasked()) {
                                case 0:
                                    // Log.i("ContentValues", "ACTION_DOWN");
                                    m_NumericDownButtonTouchDown = true;
                                    new Thread(new Runnable() {
                                        public void run() {
                                            int cnt = 0;
                                            while (m_NumericDownButtonTouchDown) {
                                                try {
                                                    holeThicknessText.post(new Runnable() {
                                                        public void run() {
                                                            int parseInt = Integer.parseInt(holeThicknessText.getText().toString()) - 1;
                                                            if (parseInt > -1) {
                                                                holeThicknessText.setText(parseInt + "");
                                                                holeThicknessText.setSelection(holeThicknessText.length());
                                                            }
                                                        }
                                                    });
                                                    cnt++;
                                                    if (cnt < 5) {
                                                        Thread.sleep(500);
                                                    } else {
                                                        Thread.sleep(50);
                                                    }
                                                } catch (Exception e) {
                                                    return;
                                                }
                                            }
                                        }
                                    }).start();
                                    break;
                                case 1:
                                    m_NumericDownButtonTouchDown = false;
                                    break;
                            }
                            return false;
                        }
                    });

                    EditText holeFontSizeText = (EditText)m_SettingListView.findViewById(R.id.holeFontSizeText);
                    int holeFontSize = pref.getInt("holeFontSize", 12);
                    holeFontSizeText.setText(holeFontSize + "");

                    Button holeFontSizeUpButton = (Button)m_SettingListView.findViewById(R.id.holeFontSizeUpButtonChild);
                    holeFontSizeUpButton.setOnTouchListener(new View.OnTouchListener() {
                        public boolean onTouch(View v, MotionEvent event) {
                            switch (event.getActionMasked()) {
                                case 0:
                                    // Log.i(TAG, "ACTION_DOWN");
                                    m_NumericUpButtonTouchDown = true;
                                    new Thread(new Runnable() {
                                        public void run() {
                                            int cnt = 0;
                                            while (m_NumericUpButtonTouchDown) {
                                                try {
                                                    holeFontSizeText.post(new Runnable() {
                                                        public void run() {
                                                            holeFontSizeText.setText((Integer.parseInt(holeFontSizeText.getText().toString()) + 1) + "");
                                                            holeFontSizeText.setSelection(holeFontSizeText.length());
                                                        }
                                                    });

                                                    cnt++;

                                                    if (cnt < 5) {
                                                        Thread.sleep(500);
                                                    } else {
                                                        Thread.sleep(50);
                                                    }
                                                } catch (Exception e) {
                                                    return;
                                                }
                                            }
                                        }
                                    }).start();
                                    break;
                                case 1:
                                    m_NumericUpButtonTouchDown = false;
                                    break;
                            }
                            return false;
                        }
                    });

                    Button holeFontSizeDownButton = (Button)m_SettingListView.findViewById(R.id.holeFontSizeDownButtonChild);
                    holeFontSizeDownButton.setOnTouchListener(new View.OnTouchListener() {
                        public boolean onTouch(View v, MotionEvent event) {
                            switch (event.getActionMasked()) {
                                case 0:
                                    // Log.i("ContentValues", "ACTION_DOWN");
                                    m_NumericDownButtonTouchDown = true;
                                    new Thread(new Runnable() {
                                        public void run() {
                                            int cnt = 0;
                                            while (m_NumericDownButtonTouchDown) {
                                                try {
                                                    holeFontSizeText.post(new Runnable() {
                                                        public void run() {
                                                            int parseInt = Integer.parseInt(holeFontSizeText.getText().toString()) - 1;
                                                            if (parseInt > -1) {
                                                                holeFontSizeText.setText(parseInt + "");
                                                                holeFontSizeText.setSelection(holeFontSizeText.length());
                                                            }
                                                        }
                                                    });
                                                    cnt++;
                                                    if (cnt < 5) {
                                                        Thread.sleep(500);
                                                    } else {
                                                        Thread.sleep(50);
                                                    }
                                                } catch (Exception e) {
                                                    return;
                                                }
                                            }
                                        }
                                    }).start();
                                    break;
                                case 1:
                                    m_NumericDownButtonTouchDown = false;
                                    break;
                            }
                            return false;
                        }
                    });

                    EditText holeFontThicknessText = (EditText)m_SettingListView.findViewById(R.id.holeFontThicknessText);
                    int holeFontThickness = pref.getInt("holeFontThickness", 1);
                    holeFontThicknessText.setText(holeFontThickness + "");

                    Button holeFontThicknessUpButton = (Button)m_SettingListView.findViewById(R.id.holeFontThicknessUpButtonChild);
                    holeFontThicknessUpButton.setOnTouchListener(new View.OnTouchListener() {
                        public boolean onTouch(View v, MotionEvent event) {
                            switch (event.getActionMasked()) {
                                case 0:
                                    // Log.i(TAG, "ACTION_DOWN");
                                    m_NumericUpButtonTouchDown = true;
                                    new Thread(new Runnable() {
                                        public void run() {
                                            int cnt = 0;
                                            while (m_NumericUpButtonTouchDown) {
                                                try {
                                                    holeFontThicknessText.post(new Runnable() {
                                                        public void run() {
                                                            holeFontThicknessText.setText((Integer.parseInt(holeFontThicknessText.getText().toString()) + 1) + "");
                                                            holeFontThicknessText.setSelection(holeFontThicknessText.length());
                                                        }
                                                    });

                                                    cnt++;

                                                    if (cnt < 5) {
                                                        Thread.sleep(500);
                                                    } else {
                                                        Thread.sleep(50);
                                                    }
                                                } catch (Exception e) {
                                                    return;
                                                }
                                            }
                                        }
                                    }).start();
                                    break;
                                case 1:
                                    m_NumericUpButtonTouchDown = false;
                                    break;
                            }
                            return false;
                        }
                    });

                    Button holeFontThicknessDownButton = (Button)m_SettingListView.findViewById(R.id.holeFontThicknessDownButtonChild);
                    holeFontThicknessDownButton.setOnTouchListener(new View.OnTouchListener() {
                        public boolean onTouch(View v, MotionEvent event) {
                            switch (event.getActionMasked()) {
                                case 0:
                                    // Log.i("ContentValues", "ACTION_DOWN");
                                    m_NumericDownButtonTouchDown = true;
                                    new Thread(new Runnable() {
                                        public void run() {
                                            int cnt = 0;
                                            while (m_NumericDownButtonTouchDown) {
                                                try {
                                                    holeFontThicknessText.post(new Runnable() {
                                                        public void run() {
                                                            int parseInt = Integer.parseInt(holeFontThicknessText.getText().toString()) - 1;
                                                            if (parseInt > -1) {
                                                                holeFontThicknessText.setText(parseInt + "");
                                                                holeFontThicknessText.setSelection(holeFontThicknessText.length());
                                                            }
                                                        }
                                                    });
                                                    cnt++;
                                                    if (cnt < 5) {
                                                        Thread.sleep(500);
                                                    } else {
                                                        Thread.sleep(50);
                                                    }
                                                } catch (Exception e) {
                                                    return;
                                                }
                                            }
                                        }
                                    }).start();
                                    break;
                                case 1:
                                    m_NumericDownButtonTouchDown = false;
                                    break;
                            }
                            return false;
                        }
                    });

                    ((Button)m_SettingDialog.findViewById(R.id.settingConfirmButton)).setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            String mainIpAddress = mainIpAddressText.getText().toString();
                            String mainPort = mainPortText.getText().toString();
                            String ipAddress = ipAddressText.getText().toString();
                            String port = portText.getText().toString();
                            String holeSize = holeSizeText.getText().toString();
                            String holeThickness = holeThicknessText.getText().toString();
                            String holeFontSize = holeFontSizeText.getText().toString();
                            String holeFontThickness = holeFontThicknessText.getText().toString();
                            editor.putString("mainIpAddress", mainIpAddress);
                            editor.putInt("mainPort", Integer.valueOf(mainPort).intValue());
                            editor.putString("ipAddress", ipAddress);
                            editor.putInt("port", Integer.valueOf(port).intValue());
                            editor.putInt("holeSize", Integer.valueOf(holeSize).intValue());
                            editor.putInt("holeThickness", Integer.valueOf(holeThickness).intValue());
                            editor.putInt("holeFontSize", Integer.valueOf(holeFontSize).intValue());
                            editor.putInt("holeFontThickness", Integer.valueOf(holeFontThickness).intValue());
                            editor.commit();
                            m_SettingDialog.dismiss();

                            if (m_MainSocketClient != null) {
                                m_MainSocketClient.close();
                            }

                            sendBroadcast(new Intent("com.example.myapplication.communicationSettingChanged"));
                        }
                    });

                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    lp.copyFrom(m_SettingDialog.getWindow().getAttributes());
                    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                    lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                    m_SettingDialog.getWindow().setAttributes(lp);
                    m_SettingDialog.show();
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            while (true) {
                                Thread.sleep(10);

                                settingButtonClickMilliSeconds = settingButtonClickMilliSeconds + 10;

                                if (settingButtonClickMilliSeconds > 1000) {
                                    settingButtonClickCount = 0;
                                    settingButtonClickMilliSeconds = 0;
                                    break;
                                }
                            }
                        } catch (Exception e) {
                            LogManager.e("[MainActivity] [m_SettingButton.setOnClickListener] " + e.getMessage());
                        }
                    }
                }).start();
            }
        });

        m_SettingButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:

                    case MotionEvent.ACTION_UP:
                        isSettingButtonTouched = false;
                        settingButtonTouchMillisSeconds = 0;

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    while (!isSettingButtonTouched) {
                                        if (settingButtonTouchMillisSeconds > 1000) {
                                            settingButtonTouchCount = 0;
                                            settingButtonTouchMillisSeconds = 0;
                                            break;
                                        }

                                        Thread.sleep(10);

                                        settingButtonTouchMillisSeconds = settingButtonTouchMillisSeconds + 10;
                                    }
                                } catch (Exception e) {
                                    LogManager.e("[MainActivity] [m_SettingButton.setOnTouchListener] " + e.getMessage());
                                }
                            }
                        });

                        break;
                }
                return false;
            }
        });

        m_ManualButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (m_Control == 0) {
                    setManualLayout();
                }
            }
        });
    }

    private void setSecondLayout() {
        m_FirstLayout.post(new Runnable() {
            @Override
            public void run() {
                m_FirstLayout.setVisibility(View.GONE);
            }
        });

        m_SecondLayout.post(new Runnable() {
            @Override
            public void run() {
                m_SecondLayout.setVisibility(View.VISIBLE);
            }
        });

        m_ThirdLayout.post(new Runnable() {
            @Override
            public void run() {
                m_ThirdLayout.setVisibility(View.GONE);
            }
        });

        m_FourthLayout.post(new Runnable() {
            @Override
            public void run() {
                m_FourthLayout.setVisibility(View.GONE);
            }
        });

        m_FifthLayout.post(new Runnable() {
            @Override
            public void run() {
                m_FifthLayout.setVisibility(View.GONE);
            }
        });

        m_SixthLayout.post(new Runnable() {
            @Override
            public void run() {
                m_SixthLayout.setVisibility(View.GONE);
            }
        });

        m_EditHoleLayout.post(new Runnable() {
            @Override
            public void run() {
                m_EditHoleLayout.setVisibility(View.GONE);
            }
        });

        m_ManualLayout.post(new Runnable() {
            @Override
            public void run() {
                m_ManualLayout.setVisibility(View.GONE);
            }
        });

        m_StartChecked = false;
        m_ReplyStartChecked = false;
        m_ReplyOriginLeftTopChecked = false;
        m_ReplyStartScanChecked = false;
        m_ReplyScanHolesChecked = false;

        m_HoleDataList.clear();

        setSecondButton();

        m_MotorPositionSettingNum = 0;
        m_LayoutIndex = 1;
    }

    private String workNumber = "";

    private void setSecondButton() {
        TextView m_WorkNumberTextView = (TextView)m_SecondLayout.findViewById(R.id.workNumberTextView);

        m_WorkNumber = (EditText) this.m_SecondLayout.findViewById(R.id.workNumber);
        m_WorkNumber.setText("");

        if (isTestMode) {
            m_WorkNumberTextView.setVisibility(View.GONE);
            m_WorkNumber.setVisibility(View.GONE);
        } else {
            m_WorkNumberTextView.setVisibility(View.VISIBLE);
            m_WorkNumber.setVisibility(View.VISIBLE);
        }

        final InputMethodManager m_WorkNumberImm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        m_WorkNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    m_WorkNumberImm.showSoftInput(m_WorkNumber, 0);
                    return;
                }

                m_WorkNumberImm.hideSoftInputFromWindow(m_WorkNumber.getWindowToken(), 0);
            }
        });

        Button m_SecondPreButton = (Button)m_SecondLayout.findViewById(R.id.secondPreButton);
        m_SecondPreButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (m_Control == 0) {
                    setFirstLayout();
                }
            }
        });

        m_SecondNextButton = (Button)m_SecondLayout.findViewById(R.id.secondNextButton);
        m_SecondNextButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if ((m_WorkNumber.getText().toString().equals("") || m_WorkNumber.getText().toString().equals((Object) null)) && !isTestMode) {
                    if (m_Control == 0) {
                        final Dialog dialog = new Dialog(MainActivity.this);
                        dialog.requestWindowFeature(1);
                        dialog.setContentView(R.layout.worksetting_confirm_dialog);
                        ((Button)dialog.findViewById(R.id.workSettingConfirmButton)).setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                        lp.copyFrom(dialog.getWindow().getAttributes());
                        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                        dialog.getWindow().setAttributes(lp);
                        dialog.show();
                    }
                } else if (m_Control == 0) {
                    m_StartChecked = true;
                    isCompleted = false;
                    isPause = false;
                    m_HoleMaxNum = -9999;
                    m_HoleMinNum = 9999;
                    m_MotorPositionSettingNum = 0;
                    m_ScanImageDownloaded = false;

                    workNumber = m_WorkNumber.getText().toString();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String sendStr = "";

                                sendStr = "START," + m_WorkNumber.getText().toString() + "," + 0 + ",";

                                byte[] sendBytes = sendStr.getBytes(StandardCharsets.US_ASCII);
                                m_MainSocketClient.send(sendBytes);

                                int cnt = 0;

                                while (!m_ReplyStartChecked) {
                                    Thread.sleep(10);
                                    cnt++;

                                    if (cnt > 100) {
                                        break;
                                    }
                                }

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        setThirdLayout();
                                    }
                                });
                            } catch (Exception e) {
                                LogManager.e("[MainActivity] [m_SecondNextButton.setOnClickListener] " + e.getMessage());
                            }
                        }
                    }).start();
                }
            }
        });
    }

    private void setThirdLayout() {
        m_FirstLayout.post(new Runnable() {
            @Override
            public void run() {
                m_FirstLayout.setVisibility(View.GONE);
            }
        });

        m_SecondLayout.post(new Runnable() {
            @Override
            public void run() {
                m_SecondLayout.setVisibility(View.GONE);
            }
        });

        m_ThirdLayout.post(new Runnable() {
            @Override
            public void run() {
                m_ThirdLayout.setVisibility(View.VISIBLE);
            }
        });

        m_FourthLayout.post(new Runnable() {
            @Override
            public void run() {
                m_FourthLayout.setVisibility(View.GONE);
            }
        });

        m_FifthLayout.post(new Runnable() {
            @Override
            public void run() {
                m_FifthLayout.setVisibility(View.GONE);
            }
        });

        m_SixthLayout.post(new Runnable() {
            @Override
            public void run() {
                m_SixthLayout.setVisibility(View.GONE);
            }
        });

        m_EditHoleLayout.post(new Runnable() {
            @Override
            public void run() {
                m_EditHoleLayout.setVisibility(View.GONE);
            }
        });

        m_ManualLayout.post(new Runnable() {
            @Override
            public void run() {
                m_ManualLayout.setVisibility(View.GONE);
            }
        });

        setThirdButton();

        m_LayoutIndex = 2;
    }

    private boolean thirdAccelerateMode = true;

    private void setThirdButton() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String sendStr = "ACCELERATE_MODE_ON,";
                    byte[] sendBytes = sendStr.getBytes(StandardCharsets.US_ASCII);
                    m_MainSocketClient.send(sendBytes);

                    int cnt = 0;

                    while (!m_ReplyAccelerateModeOnChecked) {
                        // Log.e(TAG, "가속모드 온 체크 여부" + m_ReplyAccelerateModeOnChecked);
                        Thread.sleep(10);
                        cnt++;

                        if (cnt > 100) {
                            break;
                        }

                        errorCheck("ACCELERATE_MODE_ON,");
                    }

                    m_ReplyAccelerateModeOnChecked = false;
                } catch (Exception e) {

                }
            }
        }).start();

        thirdAccelerateMode = true;

        ToggleButton accelerateButton = (ToggleButton)m_ThirdLayout.findViewById(R.id.centeringAccelerateButton);
        accelerateButton.setTextOn("가속 모드");
        accelerateButton.setTextOff("가속 모드");
        accelerateButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.toggle_button_on, null));
        accelerateButton.setChecked(true);
        accelerateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!thirdAccelerateMode) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String sendStr = "ACCELERATE_MODE_ON,";
                                byte[] sendBytes = sendStr.getBytes(StandardCharsets.US_ASCII);
                                m_MainSocketClient.send(sendBytes);

                                int cnt = 0;

                                while (!m_ReplyAccelerateModeOnChecked) {
                                    // Log.e(TAG, "가속모드 온 체크 여부" + m_ReplyAccelerateModeOnChecked);
                                    Thread.sleep(10);
                                    cnt++;

                                    if (cnt > 100) {
                                        break;
                                    }

                                    errorCheck("ACCELERATE_MODE_ON,");
                                }

                                if (m_ReplyAccelerateModeOnChecked) {
                                    // Log.i(TAG, "가속 모드 온 체크");
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            accelerateButton.setChecked(true);
                                            accelerateButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.toggle_button_on, null));
                                            thirdAccelerateMode = true;
                                        }
                                    });
                                }

                                m_ReplyAccelerateModeOnChecked = false;
                            } catch (Exception e) {
                                LogManager.e("[MainActivity] [setManualButton] 가속 모드 켜기 커맨드 전송 에러 : " + e.getMessage());
                                Log.e(TAG, "[MainActivity] [setManualButton] 가속 모드 켜기 커맨드 전송 에러 : " + e.getMessage());
                            }
                        }
                    }).start();
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String sendStr = "ACCELERATE_MODE_OFF,";
                                byte[] sendBytes = sendStr.getBytes(StandardCharsets.US_ASCII);
                                m_MainSocketClient.send(sendBytes);

                                int cnt = 0;

                                while (!m_ReplyAccelerateModeOffChecked) {
                                    // Log.e(TAG, "가속모드 오프 체크 여부" + m_ReplyAccelerateModeOffChecked);
                                    Thread.sleep(10);
                                    cnt++;

                                    if (cnt > 100) {
                                        break;
                                    }

                                    errorCheck("ACCELERATE_MODE_OFF,");
                                }

                                if (m_ReplyAccelerateModeOffChecked) {
                                    // Log.i(TAG, "가속 모드 오프 체크");
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            accelerateButton.setChecked(false);
                                            accelerateButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.toggle_button_off, null));
                                            thirdAccelerateMode = false;
                                        }
                                    });
                                }

                                m_ReplyAccelerateModeOffChecked = false;
                            } catch (Exception e) {
                                LogManager.e("[MainActivity] [setManualButton] 가속 모드 끄기 커맨드 전송 에러 : " + e.getMessage());
                                Log.e(TAG, "[MainActivity] [setManualButton] 가속 모드 끄기 커맨드 전송 에러 : " + e.getMessage());
                            }
                        }
                    }).start();
                }
            }
        });

        m_OriginBundleDraw = (ImageView) m_ThirdLayout.findViewById(R.id.originBundleDraw);
        int w = m_OriginBundleDraw.getWidth();
        int h = m_OriginBundleDraw.getHeight();

        m_MotorPositionSettingText = (TextView) m_ThirdLayout.findViewById(R.id.motorPositionSettingText);
        // m_MotorPositionSettingNum = 0;
        m_ThirdPreButton = (Button) m_ThirdLayout.findViewById(R.id.thirdPreButton);
        m_ThirdNextButton = (Button) m_ThirdLayout.findViewById(R.id.thirdNextButton);

        switch (m_MotorPositionSettingNum) {
            case -1:
                if (isTestMode) {
                    setFirstLayout();
                } else {
                    setSecondLayout();
                }

                break;
            case 0:
                m_MotorPositionSettingText.setText("좌측 상단 원점 위치를 확인해주세요.");

                Drawable origin_lefttop_drawable = getResources().getDrawable(R.drawable.origin_lefttop);
                Bitmap origin_lefttop_bitmap = ((BitmapDrawable)origin_lefttop_drawable).getBitmap();

                m_OriginBundleDraw.setImageBitmap(origin_lefttop_bitmap);
                break;
            case 1:
                m_MotorPositionSettingText.setText("우측 하단 원점 위치를 확인해주세요.");

                Drawable origin_right_bottom_drawable = getResources().getDrawable(R.drawable.origin_rightbottom);
                Bitmap origin_right_bottom_bitmap = ((BitmapDrawable)origin_right_bottom_drawable).getBitmap();

                m_OriginBundleDraw.setImageBitmap(origin_right_bottom_bitmap);
                break;
            case 2:
                // setFifthLayout();
                break;
        }

        m_ThirdPreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (m_Control == 0) {
                    m_MotorPositionSettingNum--;

                    switch (m_MotorPositionSettingNum) {
                        case -1:
                            if (isTestMode) {
                                setFirstLayout();
                            } else {
                                setSecondLayout();
                            }

                            break;
                        case 0:
                            m_MotorPositionSettingText.setText("좌측 상단 원점 위치를 확인해주세요.");

                            Drawable origin_lefttop_drawable = getResources().getDrawable(R.drawable.origin_lefttop);
                            Bitmap origin_lefttop_bitmap = ((BitmapDrawable)origin_lefttop_drawable).getBitmap();

                            m_OriginBundleDraw.setImageBitmap(origin_lefttop_bitmap);
                            break;
                        case 1:
                            m_MotorPositionSettingText.setText("우측 하단 원점 위치를 확인해주세요.");

                            Drawable origin_rightbottom_drawable = getResources().getDrawable(R.drawable.origin_rightbottom);
                            Bitmap origin_rightbottom_bitmap = ((BitmapDrawable)origin_rightbottom_drawable).getBitmap();

                            m_OriginBundleDraw.setImageBitmap(origin_rightbottom_bitmap);
                            break;
                        case 2:
                            // setFifthLayout();
                            break;
                    }
                }
            }
        });

        m_ThirdNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (m_Control == 0) {
                    m_MotorPositionSettingNum++;

                    switch (m_MotorPositionSettingNum) {
                        case 0:
                            m_MotorPositionSettingText.setText("좌측 상단 원점 위치를 확인해주세요.");

                            Drawable origin_lefttop_drawable = getResources().getDrawable(R.drawable.origin_lefttop);
                            Bitmap origin_lefttop_bitmap = ((BitmapDrawable)origin_lefttop_drawable).getBitmap();

                            m_OriginBundleDraw.setImageBitmap(origin_lefttop_bitmap);
                            break;
                        case 1:
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        int cnt = 0;

                                        while (!m_ReplyStartChecked) {
                                            Thread.sleep(10);
                                            cnt++;

                                            if (cnt > 100) {
                                                break;
                                            }
                                        }

                                        m_ReplyStartChecked = false;

                                        if (m_StartChecked) {
                                            String sendStr = "ORIGIN_LEFT_TOP,";
                                            byte[] sendBytes = sendStr.getBytes(StandardCharsets.US_ASCII);
                                            m_MainSocketClient.send(sendBytes);
                                            // Log.i(TAG, "ORIGIN_LEFT_TOP 전송");
                                            LogManager.i("[MainActivity] [setThirdButton] ORIGIN_LEFT_TOP 전송");

                                            cnt = 0;

                                            while (!m_ReplyOriginLeftTopChecked) {
                                                Thread.sleep(10);
                                                cnt++;

                                                if (cnt > 100) {
                                                    break;
                                                }
                                            }

                                            m_ReplyOriginLeftTopChecked = false;

                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    final Dialog dialog = new Dialog(MainActivity.this);
                                                    dialog.requestWindowFeature(1);
                                                    dialog.setContentView(R.layout.origin_setting_dialog);

                                                    Button m_OriginSettingConfirmButton = (Button) dialog.findViewById(R.id.originSettingConfirmButton);
                                                    m_OriginSettingConfirmButton.setOnClickListener(new View.OnClickListener() {
                                                        public void onClick(View v) {
                                                            dialog.dismiss();

                                                            m_MotorPositionSettingText.setText("우측 하단 원점 위치를 확인해주세요.");

                                                            Drawable origin_rightbottom_drawable = getResources().getDrawable(R.drawable.origin_rightbottom);
                                                            Bitmap origin_rightbottom_bitmap = ((BitmapDrawable)origin_rightbottom_drawable).getBitmap();

                                                            m_OriginBundleDraw.setImageBitmap(origin_rightbottom_bitmap);
                                                        }
                                                    });

                                                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                                                    lp.copyFrom(dialog.getWindow().getAttributes());
                                                    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                                                    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                                                    dialog.getWindow().setAttributes(lp);
                                                    dialog.show();
                                                }
                                            });
                                        }
                                    } catch (Exception e) {
                                        LogManager.e("[MainAcitvity] [m_ThirdNextButton.setOnClickListener] " + e.getMessage());
                                    }
                                }
                            }).start();

                            break;
                        case 2:
                            setFourthLayout();
                            break;
                    }
                }
            }
        });

        m_CenteringJogUpButton = (Button)m_ThirdLayout.findViewById(R.id.centeringJogUpButton);
        m_CenteringJogUpButton.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case 0:
                        if (m_Control == 0) {
                            sendBroadcast(new Intent("com.example.myapplication.JogUpButtonDown"));
                        }

                        return false;
                    case 1:
                        if (m_Control == 0) {
                            sendBroadcast(new Intent("com.example.myapplication.JogUpButtonUp"));
                        }
                        return false;
                    default:
                        return false;
                }
            }
        });

        m_CenteringJogLeftButton = (Button)m_ThirdLayout.findViewById(R.id.centeringJogLeftButton);
        m_CenteringJogLeftButton.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case 0:
                        if (m_Control == 0) {
                            sendBroadcast(new Intent("com.example.myapplication.JogLeftButtonDown"));
                        }

                        return false;
                    case 1:
                        if (m_Control == 0) {
                            sendBroadcast(new Intent("com.example.myapplication.JogLeftButtonUp"));
                            // Toast.makeText(MainActivity.this, "JogLeftButtonUp", Toast.LENGTH_SHORT).show();
                        }

                        return false;
                    default:
                        return false;
                }
            }
        });

        m_CenteringJogRightButton = (Button)m_ThirdLayout.findViewById(R.id.centeringJogRightButton);
        m_CenteringJogRightButton.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case 0:
                        if (m_Control == 0) {
                            sendBroadcast(new Intent("com.example.myapplication.JogRightButtonDown"));
                        }

                        return false;
                    case 1:
                        if (m_Control == 0) {
                            sendBroadcast(new Intent("com.example.myapplication.JogRightButtonUp"));
                            // Toast.makeText(MainActivity.this, "JogRightButtonUp", Toast.LENGTH_SHORT).show();
                        }

                        return false;
                    default:
                        return false;
                }
            }
        });

        m_CenteringJogDownButton = (Button)m_ThirdLayout.findViewById(R.id.centeringJogDownButton);
        m_CenteringJogDownButton.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case 0:
                        if (m_Control == 0) {
                            sendBroadcast(new Intent("com.example.myapplication.JogDownButtonDown"));
                        }

                        return false;
                    case 1:
                        if (m_Control == 0) {
                            sendBroadcast(new Intent("com.example.myapplication.JogDownButtonUp"));
                        }

                        return false;
                    default:
                        return false;
                }
            }
        });

        Button m_CenteringNozelForwardButton = (Button)m_ThirdLayout.findViewById(R.id.centeringNozelForwardButton);
        m_CenteringNozelForwardButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case 0:
                        if (m_Control == 0) {
                            sendBroadcast(new Intent("com.example.myapplication.allNozelForwardButtonDown"));
                        }

                        return false;
                    case 1:
                        if (m_Control == 0) {
                            sendBroadcast(new Intent("com.example.myapplication.allNozelForwardButtonUp"));
                        }

                        return false;
                    default:
                        return false;
                }
            }
        });

        Button m_CenteringNozelBackwardButton = (Button)m_ThirdLayout.findViewById(R.id.centeringNozelBackwardButton);
        m_CenteringNozelBackwardButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case 0:
                        if (m_Control == 0) {
                            sendBroadcast(new Intent("com.example.myapplication.allNozelBackwardButtonDown"));
                        }

                        return false;
                    case 1:
                        if (m_Control == 0) {
                            sendBroadcast(new Intent("com.example.myapplication.allNozelBackwardButtonUp"));
                        }

                        return false;
                    default:
                        return false;
                }
            }
        });

        Button centeringLaserOnButton = (Button)m_ThirdLayout.findViewById(R.id.centeringLaserOnButton);
        centeringLaserOnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String sendStr = "CENTERING,1,";
                            byte[] sendBytes = sendStr.getBytes(StandardCharsets.US_ASCII);
                            m_MainSocketClient.send(sendBytes);
                        } catch (Exception e) {
                            LogManager.e("[MainActivity] [setThirdLayout.centeringLaserOnButton.setOnClickListener] " + e.getMessage());
                        }
                    }
                }).start();
            }
        });

        Button centeringLaserOffButton = (Button)m_ThirdLayout.findViewById(R.id.centeringLaserOffButton);
        centeringLaserOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String sendStr = "CENTERING,0,";
                            byte[] sendBytes = sendStr.getBytes(StandardCharsets.US_ASCII);
                            m_MainSocketClient.send(sendBytes);
                        } catch (Exception e) {
                            LogManager.e("[MainActivity] [setThirdLayout.centeringLaserOffButton.setOnClickListener] " + e.getMessage());
                        }
                    }
                }).start();
            }
        });
    }

    private void setFourthLayout() {
        m_FirstLayout.post(new Runnable() {
            @Override
            public void run() {
                m_FirstLayout.setVisibility(View.GONE);
            }
        });

        m_SecondLayout.post(new Runnable() {
            @Override
            public void run() {
                m_SecondLayout.setVisibility(View.GONE);
            }
        });

        m_ThirdLayout.post(new Runnable() {
            @Override
            public void run() {
                m_ThirdLayout.setVisibility(View.GONE);
            }
        });

        m_FourthLayout.post(new Runnable() {
            @Override
            public void run() {
                m_FourthLayout.setVisibility(View.VISIBLE);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (m_StartChecked) {
                                String sendStr = "START_SCAN,";
                                byte[] sendBytes = sendStr.getBytes(StandardCharsets.US_ASCII);
                                // send(sendBytes);

                                try {
                                    m_MainSocketClient.send(sendBytes);
                                } catch (Exception e) {
                                    LogManager.e("[MainActivity] [setFourthLayout] " + e.getMessage());
                                    Log.e(TAG, "[MainActivity] [setFourthLayout] " + e.getMessage());
                                    setThirdLayout();
                                }
                            }

                            LogManager.i("[MainActivity] [setFourthLayout] START_SCAN 전송");
                            // Log.i(TAG, "[MainActivity] [setFourthLayout] START_SCAN 전송");
                        } catch (Exception e) {
                            LogManager.e("[MainActivity] [setFourthLayout] " + e.getMessage());
                            // Log.e(TAG, "[MainActivity] [setFourthLayout] " + e.getMessage());
                        }
                    }
                }).start();
            }
        });

        m_FifthLayout.post(new Runnable() {
            @Override
            public void run() {
                m_FifthLayout.setVisibility(View.GONE);
            }
        });

        m_SixthLayout.post(new Runnable() {
            @Override
            public void run() {
                m_SixthLayout.setVisibility(View.GONE);
            }
        });

        m_EditHoleLayout.post(new Runnable() {
            @Override
            public void run() {
                m_EditHoleLayout.setVisibility(View.GONE);
            }
        });

        m_ManualLayout.post(new Runnable() {
            @Override
            public void run() {
                m_ManualLayout.setVisibility(View.GONE);
            }
        });

        setFourthButton();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int cnt = 0;

                    while (!m_ReplyStartScanChecked) {
                        Thread.sleep(10);
                        cnt++;

                        if (cnt > 100) {
                            break;
                        }

                        errorCheck("START_SCAN,");
                    }

                    m_ReplyStartScanChecked = false;

                    if (m_StartChecked) {
                        cnt = 0;

                        while (!m_ReplyScanImageChecked) {
                            String sendStr = "SCAN_IMAGE,";
                            byte[] sendBytes = sendStr.getBytes(StandardCharsets.US_ASCII);
                            // send(sendBytes);

                            try {
                                m_MainSocketClient.send(sendBytes);
                                // LogManager.i("[MainActivity] [setFourthLayout] SCAN_IMAGE 전송 완료");
                            } catch (Exception e) {
                                LogManager.e("[MainActivity] [setFourthLayout \"SCAN_IMAGE\"] " + e.getMessage());
                            }

                            Thread.sleep(1000);

                            errorCheck("SCAN_IMAGE,");
                        }

                        m_ReplyScanImageChecked = false;

                        cnt = 0;

                        while (!m_ScanImageDownloaded) {
                            String sendStr = "SCAN_IMAGE,";
                            byte[] sendBytes = sendStr.getBytes(StandardCharsets.US_ASCII);
                            // send(sendBytes);

                            try {
                                m_MainSocketClient.send(sendBytes);
                                // LogManager.i("[MainActivity] [setFourthLayout] SCAN_IMAGE 전송 완료");
                            } catch (Exception e) {
                                LogManager.e("[MainActivity] [setFourthLayout \"SCAN_IMAGE\"] " + e.getMessage());
                            }

                            Thread.sleep(1000);

                            errorCheck("SCAN_IMAGE,");
                        }

                        m_ScanImageDownloaded = false;
                    }
                } catch (Exception e) {
                    LogManager.e("[MainActivity] [setFourthLayout] " + e.getMessage());
                }

            }
        }).start();

        m_LayoutIndex = 3;
    }

    private void errorCheck(String sendStr) {
        try {
            if (isErrored) {
                LogManager.e("[MainActivity] [errorCheck] 소켓 에러 체크");
                
                int cnt = 0;

                while (!m_MainSocketClient.isConnected()) {
                    Thread.sleep(10);
                    cnt++;

                    if (cnt > 100) {
                        break;
                    }
                }

                if (m_MainSocketClient.isConnected()) {
                    LogManager.i("[MainActivity] [errorCheck] 소켓 연결 체크");
                    byte[] sendBytes = sendStr.getBytes(StandardCharsets.US_ASCII);
                    m_MainSocketClient.send(sendBytes);

                    isErrored = false;
                }
            }
        } catch (Exception e) {
            LogManager.e("[MainActivity] [errorCheck] " + e.getMessage());
        }
    }

    private void setFourthButton() {

    }

    private void setEditHoleLayout() {
        m_FirstLayout.post(new Runnable() {
            @Override
            public void run() {
                m_FirstLayout.setVisibility(View.GONE);
            }
        });

        m_SecondLayout.post(new Runnable() {
            @Override
            public void run() {
                m_SecondLayout.setVisibility(View.GONE);
            }
        });

        m_ThirdLayout.post(new Runnable() {
            @Override
            public void run() {
                m_ThirdLayout.setVisibility(View.GONE);
            }
        });

        m_FourthLayout.post(new Runnable() {
            @Override
            public void run() {
                m_FourthLayout.setVisibility(View.GONE);
            }
        });

        m_EditHoleLayout.post(new Runnable() {
            @Override
            public void run() {
                m_EditHoleLayout.setVisibility(View.VISIBLE);
            }
        });

        m_FifthLayout.post(new Runnable() {
            @Override
            public void run() {
                m_FifthLayout.setVisibility(View.GONE);
            }
        });

        m_SixthLayout.post(new Runnable() {
            @Override
            public void run() {
                m_SixthLayout.setVisibility(View.GONE);
            }
        });

        m_ManualLayout.post(new Runnable() {
            @Override
            public void run() {
                m_ManualLayout.setVisibility(View.GONE);
            }
        });

        setEditHoleButton();

        m_LayoutIndex = 7;
    }

    // 0이면 기본 모드, 1이면 추가 모드, 2이면 제거 모드
    private int m_EditHoleMode = 0;

    private void setEditHoleButton() {

        m_EditHoleImageView = (ImageView)m_EditHoleLayout.findViewById(R.id.editHoleImageView);

        m_EditHoleImageViewMatrix = new Matrix();
        m_EditHoleImageViewSavedMatrix = new Matrix();

        m_EditHoleImageView.post(new Runnable() {
            @Override
            public void run() {
                m_EditHoleImageView.setOnTouchListener(onEditHoleImageViewTouch);
                m_EditHoleImageView.setScaleType(ImageView.ScaleType.MATRIX);

                m_EditHoleImageView.setImageBitmap(m_Bitmap);
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                m_EditHoleImageView.post(new Runnable() {
                    @Override
                    public void run() {
                        resetEditHoleImageViewScale();
                    }
                });
            }
        }).start();

        ToggleButton m_AddEditHoleButton = (ToggleButton)m_EditHoleLayout.findViewById(R.id.addEditHoleButton);
        ToggleButton m_RemoveEditHoleButton = (ToggleButton)m_EditHoleLayout.findViewById(R.id.removeEditHoleButton);

        m_EditHoleMode = 1;
        m_AddEditHoleButton.setTextOn("추가 모드");
        m_AddEditHoleButton.setTextOff("추가 모드");
        m_RemoveEditHoleButton.setTextOn("제거 모드");
        m_RemoveEditHoleButton.setTextOff("제거 모드");
        m_AddEditHoleButton.setChecked(true);
        m_RemoveEditHoleButton.setChecked(false);

        m_AddEditHoleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_EditHoleMode = 1;
                m_AddEditHoleButton.setChecked(true);
                m_RemoveEditHoleButton.setChecked(false);
                m_AddEditHoleButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.toggle_button_on, null));
                m_RemoveEditHoleButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.toggle_button_off, null));
            }
        });

        m_RemoveEditHoleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_EditHoleMode = 2;
                m_AddEditHoleButton.setChecked(false);
                m_RemoveEditHoleButton.setChecked(true);
                m_AddEditHoleButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.toggle_button_off, null));
                m_RemoveEditHoleButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.toggle_button_on, null));
            }
        });

        Button m_EditHoleConfirmButton = (Button)m_EditHoleLayout.findViewById(R.id.editHoleConfirmButton);
        m_EditHoleConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFifthLayout();

                /*
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final Dialog dialog = new Dialog(MainActivity.this);
                        dialog.requestWindowFeature(1);
                        dialog.setContentView(R.layout.mode_select_dialog);

                        Button modeSelectConfirmButton = (Button)dialog.findViewById(R.id.modeSelectConfirmButton);
                        modeSelectConfirmButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                setFifthLayout();
                            }
                        });

                        RadioButton newCleaningRadioButton = (RadioButton)dialog.findViewById(R.id.newCleaningRadioButton);
                        RadioButton testCleaningRadioButton = (RadioButton)dialog.findViewById(R.id.testCleaningRadioButton);
                        RadioButton relayCleaningRadioButton = (RadioButton)dialog.findViewById(R.id.relayCleaningRadioButton);

                        newCleaningRadioButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                newCleaningRadioButton.setChecked(true);
                                testCleaningRadioButton.setChecked(false);
                                relayCleaningRadioButton.setChecked(false);
                            }
                        });

                        testCleaningRadioButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                newCleaningRadioButton.setChecked(false);
                                testCleaningRadioButton.setChecked(true);
                                relayCleaningRadioButton.setChecked(false);
                            }
                        });

                        relayCleaningRadioButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                newCleaningRadioButton.setChecked(false);
                                testCleaningRadioButton.setChecked(false);
                                relayCleaningRadioButton.setChecked(true);
                            }
                        });

                        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                        lp.copyFrom(dialog.getWindow().getAttributes());
                        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                        dialog.getWindow().setAttributes(lp);
                        dialog.show();
                    }
                });
                */
            }
        });

        Button m_EditHoleBackButton = (Button)m_EditHoleLayout.findViewById(R.id.editHoleBackButton);
        m_EditHoleBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRelay) {
                    setFirstLayout();
                } else {
                    setThirdLayout();
                }
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    m_ReplyScanHolesChecked = false;

                    while (true) {
                        try {
                            if (m_ReplyScanHolesChecked) {
                                // 홀 표시
                                Canvas canvas = new Canvas();
                                Paint paint = new Paint();

                                if (m_CopyBitmap != null) {
                                    // m_CopyBitmap.recycle();
                                    m_CopyBitmap = null;
                                }

                                m_CopyBitmap = m_Bitmap.copy(Bitmap.Config.ARGB_8888, true);
                                canvas.setBitmap(m_CopyBitmap);

                                SharedPreferences pref = getApplicationContext().getSharedPreferences("rebuild_preference", MODE_PRIVATE);
                                final SharedPreferences.Editor editor = pref.edit();
                                int holeSize = pref.getInt("holeSize", 15);
                                int holeThickness = pref.getInt("holeThickness", 5);
                                int holeFontSize = pref.getInt("holeFontSize", 12);
                                int holeFontThickness = pref.getInt("holeFontThickness", 1);

                                for (int i = 0; i < m_HoleDataList.size(); i++) {
                                    HoleData holeData = m_HoleDataList.get(i);

                                    switch (holeData.Color) {
                                        case 0:
                                            paint.setStyle(Paint.Style.STROKE);
                                            // paint.setStrokeWidth(10.0f);
                                            paint.setStrokeWidth((float)holeThickness);
                                            paint.setColor(Color.WHITE);
                                            break;
                                        case 1:
                                            paint.setStyle(Paint.Style.STROKE);
                                            // paint.setStrokeWidth(10.0f);
                                            paint.setStrokeWidth((float)holeThickness);
                                            paint.setColor(Color.YELLOW);
                                            break;
                                        case 2:
                                            paint.setStyle(Paint.Style.STROKE);
                                            // paint.setStrokeWidth(10.0f);
                                            paint.setStrokeWidth((float)holeThickness);
                                            paint.setColor(Color.BLUE);
                                            break;
                                        case 3:
                                            paint.setStyle(Paint.Style.STROKE);
                                            // paint.setStrokeWidth(10.0f);
                                            paint.setStrokeWidth((float)holeThickness);
                                            paint.setColor(Color.RED);
                                            break;
                                    }

                                    canvas.drawCircle((float)holeData.X, (float)holeData.Y, (float)holeSize, paint);

                                    if (holeData.Name != null || !holeData.Name.trim().equals("")) {
                                        paint.setColor(Color.WHITE);
                                        paint.setStrokeWidth((float)holeFontThickness);
                                        paint.setTextSize(holeFontSize);
                                        paint.setTextAlign(Paint.Align.CENTER);
                                        Rect textBounds = new Rect();
                                        paint.getTextBounds(holeData.Name, 0, holeData.Name.length(), textBounds);
                                        canvas.drawText(holeData.Name, (float)holeData.X, (float)(holeData.Y - textBounds.exactCenterY()), paint);

                                        if (!holeData.Name.trim().equals("")) {
                                            int holeNum = Integer.valueOf(holeData.Name);

                                            if (holeNum < m_HoleMinNum) {
                                                m_HoleMinNum = holeNum;
                                            }

                                            if (holeNum > m_HoleMaxNum) {
                                                m_HoleMaxNum = holeNum;
                                            }
                                        }
                                    }
                                }

                                m_ReplyScanHolesChecked = false;
                            }

                            String sendStr = "SCAN_HOLES,";
                            byte[] sendBytes = sendStr.getBytes(StandardCharsets.US_ASCII);
                            // send(sendBytes);
                            m_MainSocketClient.send(sendBytes);
                            // Log.i(TAG, "스캔홀 시작 전송");

                            int cnt = 0;

                            while (!m_ReplyScanHolesChecked) {
                                Thread.sleep(10);
                                cnt++;

                                if (cnt > 100) {
                                    break;
                                }

                                errorCheck("SCAN_HOLES,");
                            }

                            // 홀 표시
                            Canvas canvas = new Canvas();
                            Paint paint = new Paint();

                            if (m_CopyBitmap != null) {
                                // m_CopyBitmap.recycle();
                                m_CopyBitmap = null;
                            }

                            m_CopyBitmap = m_Bitmap.copy(Bitmap.Config.ARGB_8888, true);
                            canvas.setBitmap(m_CopyBitmap);

                            SharedPreferences pref = getApplicationContext().getSharedPreferences("rebuild_preference", MODE_PRIVATE);
                            final SharedPreferences.Editor editor = pref.edit();
                            int holeSize = pref.getInt("holeSize", 15);
                            int holeThickness = pref.getInt("holeThickness", 5);
                            int holeFontSize = pref.getInt("holeFontSize", 12);
                            int holeFontThickness = pref.getInt("holeFontThickness", 1);

                            cnt = 0;

                            for (int i = 0; i < m_HoleDataList.size(); i++) {
                                HoleData holeData = m_HoleDataList.get(i);

                                switch (holeData.Color) {
                                    case 0:
                                        paint.setStyle(Paint.Style.STROKE);
                                        // paint.setStrokeWidth(10.0f);
                                        paint.setStrokeWidth((float)holeThickness);
                                        paint.setColor(Color.WHITE);
                                        break;
                                    case 1:
                                        paint.setStyle(Paint.Style.STROKE);
                                        // paint.setStrokeWidth(10.0f);
                                        paint.setStrokeWidth((float)holeThickness);
                                        paint.setColor(Color.YELLOW);
                                        break;
                                    case 2:
                                        paint.setStyle(Paint.Style.STROKE);
                                        // paint.setStrokeWidth(10.0f);
                                        paint.setStrokeWidth((float)holeThickness);
                                        paint.setColor(Color.BLUE);
                                        break;
                                    case 3:
                                        paint.setStyle(Paint.Style.STROKE);
                                        // paint.setStrokeWidth(10.0f);
                                        paint.setStrokeWidth((float)holeThickness);
                                        paint.setColor(Color.RED);
                                        break;
                                }

                                canvas.drawCircle((float)holeData.X, (float)holeData.Y, (float)holeSize, paint);

                                if (holeData.Name != null || !holeData.Name.equals("")) {
                                    paint.setColor(Color.WHITE);
                                    paint.setStrokeWidth((float)holeFontThickness);
                                    paint.setTextSize(holeFontSize);
                                    paint.setTextAlign(Paint.Align.CENTER);
                                    Rect textBounds = new Rect();
                                    paint.getTextBounds(holeData.Name, 0, holeData.Name.length(), textBounds);
                                    canvas.drawText(holeData.Name, (float)holeData.X, (float)(holeData.Y - textBounds.exactCenterY()), paint);
                                }

                                cnt++;

                                Thread.sleep(1);
                            }

                            m_EditHoleImageView.post(new Runnable() {
                                @Override
                                public void run() {
                                    m_EditHoleImageView.setImageBitmap(m_CopyBitmap);
                                    resetEditHoleImageViewScale();
                                }
                            });

                            if (m_ScanHoleCount > 0 && m_ScanHoleCount == cnt) {
                                // Log.i(TAG, "스캔 홀 갯수 : " + m_ScanHoleCount);
                                LogManager.i("스캔 홀 갯수 : " + m_ScanHoleCount);
                                break;
                            }
                        } catch (Exception e) {
                            LogManager.e("[MainActivity] [setEditHoleButton while (true)] " + e.getMessage());
                        }

                        Thread.sleep(50);
                    }
                } catch (Exception e) {
                    LogManager.e("[MainActivity] [setEditHoleButton] " + e.getMessage());
                }
            }
        }).start();
    }

    private void setFifthLayout() {
        m_FirstLayout.post(new Runnable() {
            @Override
            public void run() {
                m_FirstLayout.setVisibility(View.GONE);
            }
        });

        m_SecondLayout.post(new Runnable() {
            @Override
            public void run() {
                m_SecondLayout.setVisibility(View.GONE);
            }
        });

        m_ThirdLayout.post(new Runnable() {
            @Override
            public void run() {
                m_ThirdLayout.setVisibility(View.GONE);
            }
        });

        m_FourthLayout.post(new Runnable() {
            @Override
            public void run() {
                m_FourthLayout.setVisibility(View.GONE);
            }
        });

        m_EditHoleLayout.post(new Runnable() {
            @Override
            public void run() {
                m_EditHoleLayout.setVisibility(View.GONE);
            }
        });

        m_FifthLayout.post(new Runnable() {
            @Override
            public void run() {
                m_FifthLayout.setVisibility(View.VISIBLE);
            }
        });

        m_SixthLayout.post(new Runnable() {
            @Override
            public void run() {
                m_SixthLayout.setVisibility(View.GONE);
            }
        });

        m_ManualLayout.post(new Runnable() {
            @Override
            public void run() {
                m_ManualLayout.setVisibility(View.GONE);
            }
        });

        setFifthButton();

        m_LayoutIndex = 4;
    }

    private TextView m_ErrorTextView = null;
    private boolean isStopButtonPressed = false;
    private boolean isAlarmMessageTextVisibled = true;
    private boolean isErrorTextViewVisibled = true;
    private boolean isCleaningDisplayVisibled = true;
    private boolean isCctvDisplayVisibled = true;
    private boolean isCleaningManualVisibled = true;

    private Button m_ErrorMessageListButton = null;

    private boolean fifthAccelerateMode = true;

    private void setFifthButton() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String sendStr = "ACCELERATE_MODE_ON,";
                    byte[] sendBytes = sendStr.getBytes(StandardCharsets.US_ASCII);
                    m_MainSocketClient.send(sendBytes);

                    int cnt = 0;

                    while (!m_ReplyAccelerateModeOnChecked) {
                        // Log.e(TAG, "가속모드 온 체크 여부" + m_ReplyAccelerateModeOnChecked);
                        Thread.sleep(10);
                        cnt++;

                        if (cnt > 100) {
                            break;
                        }

                        errorCheck("ACCELERATE_MODE_ON,");
                    }

                    m_ReplyAccelerateModeOnChecked = false;
                } catch (Exception e) {

                }
            }
        }).start();

        fifthAccelerateMode = true;

        ToggleButton accelerateButton = (ToggleButton)m_FifthLayout.findViewById(R.id.cleaningAccelerateButton);
        accelerateButton.setTextOn("가속 모드");
        accelerateButton.setTextOff("가속 모드");
        accelerateButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.toggle_button_on, null));
        accelerateButton.setChecked(true);
        accelerateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!fifthAccelerateMode) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String sendStr = "ACCELERATE_MODE_ON,";
                                byte[] sendBytes = sendStr.getBytes(StandardCharsets.US_ASCII);
                                m_MainSocketClient.send(sendBytes);

                                int cnt = 0;

                                while (!m_ReplyAccelerateModeOnChecked) {
                                    // Log.e(TAG, "가속모드 온 체크 여부" + m_ReplyAccelerateModeOnChecked);
                                    Thread.sleep(10);
                                    cnt++;

                                    if (cnt > 100) {
                                        break;
                                    }

                                    errorCheck("ACCELERATE_MODE_ON,");
                                }

                                if (m_ReplyAccelerateModeOnChecked) {
                                    // Log.i(TAG, "가속 모드 온 체크");
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            accelerateButton.setChecked(true);
                                            accelerateButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.toggle_button_on, null));
                                            fifthAccelerateMode = true;
                                        }
                                    });
                                }

                                m_ReplyAccelerateModeOnChecked = false;
                            } catch (Exception e) {
                                LogManager.e("[MainActivity] [setManualButton] 가속 모드 켜기 커맨드 전송 에러 : " + e.getMessage());
                                Log.e(TAG, "[MainActivity] [setManualButton] 가속 모드 켜기 커맨드 전송 에러 : " + e.getMessage());
                            }
                        }
                    }).start();
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String sendStr = "ACCELERATE_MODE_OFF,";
                                byte[] sendBytes = sendStr.getBytes(StandardCharsets.US_ASCII);
                                m_MainSocketClient.send(sendBytes);

                                int cnt = 0;

                                while (!m_ReplyAccelerateModeOffChecked) {
                                    // Log.e(TAG, "가속모드 오프 체크 여부" + m_ReplyAccelerateModeOffChecked);
                                    Thread.sleep(10);
                                    cnt++;

                                    if (cnt > 100) {
                                        break;
                                    }

                                    errorCheck("ACCELERATE_MODE_OFF,");
                                }

                                if (m_ReplyAccelerateModeOffChecked) {
                                    // Log.i(TAG, "가속 모드 오프 체크");
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            accelerateButton.setChecked(false);
                                            accelerateButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.toggle_button_off, null));
                                            fifthAccelerateMode = false;
                                        }
                                    });
                                }

                                m_ReplyAccelerateModeOffChecked = false;
                            } catch (Exception e) {
                                LogManager.e("[MainActivity] [setManualButton] 가속 모드 끄기 커맨드 전송 에러 : " + e.getMessage());
                                Log.e(TAG, "[MainActivity] [setManualButton] 가속 모드 끄기 커맨드 전송 에러 : " + e.getMessage());
                            }
                        }
                    }).start();
                }
            }
        });

        m_ErrorMessageListButton = (Button)m_FifthLayout.findViewById(R.id.errorMessageListButton);
        m_ErrorMessageListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.requestWindowFeature(1);
                dialog.setContentView(R.layout.error_message_list_dialog);

                TableLayout errorMessageTable = (TableLayout)dialog.findViewById(R.id.errorMessageListTable);

                for (int i = 0; i < errorList.size(); i++) {
                    StructErrorData errorData = errorList.get(i);

                    TableRow tr = new TableRow(getApplicationContext());
                    tr.setBackgroundColor(getResources().getColor(R.color.white, null));

                    for (int j = 0; j < 4; j++) {
                        TextView tv = new TextView(getApplicationContext());

                        TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
                        // params.setMargins(5, 5, 5, 5);
                        tv.setLayoutParams(params);

                        switch (j) {
                            case 0:
                                tv.setText(errorData.title);
                                break;
                            case 1:
                                tv.setText(errorData.content);
                                break;
                            case 2:
                                // tv.setText(errorData.actionContent);
                                tv.setText(errorData.dateTime);
                                break;
                            case 3:
                                // tv.setText(errorData.dateTime);
                                tv.setText(errorData.clearDateTime);
                                break;
                            case 4:
                                tv.setText(errorData.clearDateTime);
                                break;
                            default:
                                break;
                        }

                        // tv.setTextAppearance(R.style.TextAppearance_AppCompat_Small);
                        tv.setTextSize(9);
                        tv.setTextColor(getResources().getColor(R.color.black, null));
                        tv.setBackground(getResources().getDrawable(R.drawable.table_border, null));
                        tv.setSingleLine(false);
                        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                        tr.addView(tv);
                    }

                    errorMessageTable.addView(tr);
                }

                Button errorMessageListClearButton = (Button)dialog.findViewById(R.id.errorMessageListClearButton);
                errorMessageListClearButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String sendStr = "ERROR_MSG_CLR,";
                        byte[] sendBytes = sendStr.getBytes(StandardCharsets.US_ASCII);

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    m_MainSocketClient.send(sendBytes);

                                    LogManager.i("[MainActivity] [setFifthLayout] 에러 리스트 클리어 전송 완료");

                                    int cnt = 0;

                                    while (!m_ReplyErrorMsgClearChecked) {
                                        Thread.sleep(10);
                                        cnt++;

                                        if (cnt > 100) {
                                            break;
                                        }

                                        errorCheck(sendStr);
                                    }

                                    m_ReplyErrorMsgClearChecked = false;
                                } catch (Exception e) {
                                    LogManager.e("[MainActivity] [setFifthLayout] 에러 리스트 클리어 전송 실패 : " + e.getMessage());
                                }
                            }
                        }).start();
                    }
                });

                Button errorMessageListConfirmButton = (Button)dialog.findViewById(R.id.errorMessageListConfirmButton);
                errorMessageListConfirmButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                dialog.getWindow().setAttributes(lp);
                dialog.show();
            }
        });

        m_Matrix = new Matrix();
        m_SavedMatrix = new Matrix();

        m_CleaningView = (ImageView) m_FifthLayout.findViewById(R.id.cleaningView);
        m_CleaningView.setOnTouchListener(onTouch);
        m_CleaningView.setScaleType(ImageView.ScaleType.MATRIX);

        LinearLayout cleaningLayout = (LinearLayout)m_FifthLayout.findViewById(R.id.cleaningLayout);

        ImageButton cleaningDisplayButton = (ImageButton)m_FifthLayout.findViewById(R.id.cleaningDisplayButton);
        cleaningDisplayButton.setImageResource(R.drawable.ic_baseline_arrow_circle_up_24);
        cleaningDisplayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCleaningDisplayVisibled) {
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)cleaningLayout.getLayoutParams();
                    params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    params.weight = 0f;
                    cleaningLayout.setLayoutParams(params);
                    m_CleaningView.setVisibility(View.GONE);
                    isCleaningDisplayVisibled = false;
                    cleaningDisplayButton.setImageResource(R.drawable.ic_baseline_arrow_circle_down_24);
                } else {
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)cleaningLayout.getLayoutParams();
                    params.height = 0;
                    params.weight = 1f;
                    cleaningLayout.setLayoutParams(params);
                    m_CleaningView.setVisibility(View.VISIBLE);
                    isCleaningDisplayVisibled = true;
                    cleaningDisplayButton.setImageResource(R.drawable.ic_baseline_arrow_circle_up_24);
                }
            }
        });

        m_CleaningProgressBar = (ProgressBar)m_FifthLayout.findViewById(R.id.cleaningProgressBar);

        m_CleaningView.setImageBitmap(m_Bitmap);

        LinearLayout m_CleaningManualLayout = m_FifthLayout.findViewById(R.id.cleaningManualLayout);
        m_CleaningManualLayout.setVisibility(View.GONE);

        TextView alarmMessageText = (TextView)m_FifthLayout.findViewById(R.id.alarmMessage);
        alarmMessageText.setText("세척 중 입니다.");
        alarmMessageText.setVisibility(View.VISIBLE);

        isAlarmMessageTextVisibled = true;

        ImageButton alaramMessageButton = (ImageButton)m_FifthLayout.findViewById(R.id.alarmMessageButton);
        alaramMessageButton.setImageResource(R.drawable.ic_baseline_arrow_circle_up_24);
        alaramMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAlarmMessageTextVisibled) {
                    alarmMessageText.setVisibility(View.GONE);
                    isAlarmMessageTextVisibled = false;
                    alaramMessageButton.setImageResource(R.drawable.ic_baseline_arrow_circle_down_24);
                } else {
                    alarmMessageText.setVisibility(View.VISIBLE);
                    isAlarmMessageTextVisibled = true;
                    alaramMessageButton.setImageResource(R.drawable.ic_baseline_arrow_circle_up_24);
                }
            }
        });

        GridLayout cleaningJogLayout = (GridLayout)m_FifthLayout.findViewById(R.id.cleaning_jog_layout);
        GridLayout cleaningNozzleLayout = (GridLayout)m_FifthLayout.findViewById(R.id.cleaning_nozzle_layout);

        ImageButton cleaningManualButton = (ImageButton)m_FifthLayout.findViewById(R.id.cleaning_manual_button);
        cleaningManualButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCleaningManualVisibled) {
                    cleaningJogLayout.setVisibility(View.GONE);
                    cleaningNozzleLayout.setVisibility(View.GONE);
                    isCleaningManualVisibled = false;
                    cleaningManualButton.setImageResource(R.drawable.ic_baseline_arrow_circle_down_24);
                } else {
                    cleaningJogLayout.setVisibility(View.VISIBLE);
                    cleaningNozzleLayout.setVisibility(View.VISIBLE);
                    isCleaningManualVisibled = true;
                    cleaningManualButton.setImageResource(R.drawable.ic_baseline_arrow_circle_up_24);
                }
            }
        });

        m_ErrorTextView = (TextView)m_FifthLayout.findViewById(R.id.errorMessage);
        m_ErrorTextView.setText("이상 내역이 없습니다.");
        m_ErrorTextView.setVisibility(View.VISIBLE);
        isErrorTextViewVisibled = true;

        ImageButton errorMessageButton = (ImageButton)m_FifthLayout.findViewById(R.id.errorMessageButton);
        errorMessageButton.setImageResource(R.drawable.ic_baseline_arrow_circle_up_24);
        errorMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isErrorTextViewVisibled) {
                    m_ErrorTextView.setVisibility(View.GONE);
                    isErrorTextViewVisibled = false;
                    errorMessageButton.setImageResource(R.drawable.ic_baseline_arrow_circle_down_24);
                } else {
                    m_ErrorTextView.setVisibility(View.VISIBLE);
                    isErrorTextViewVisibled = true;
                    errorMessageButton.setImageResource(R.drawable.ic_baseline_arrow_circle_up_24);
                }
            }
        });

        m_SkipNextHoleButton = m_FifthLayout.findViewById(R.id.skipNextHoleButton);
        m_SkipNextHoleButton.setVisibility(View.GONE);
        m_SkipNextHoleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String sendStr = "SKIP_ONE_HOLE,";
                            byte[] sendBytes = sendStr.getBytes(StandardCharsets.US_ASCII);
                            m_MainSocketClient.send(sendBytes);
                        } catch (Exception e) {
                            LogManager.e("[MainActivity] [setFifthButton] " + e.getMessage());
                        }

                        m_CleaningManualLayout.post(new Runnable() {
                            @Override
                            public void run() {
                                m_CleaningManualLayout.setVisibility(View.GONE);
                            }
                        });

                        m_SkipNextHoleButton.post(new Runnable() {
                            @Override
                            public void run() {
                                m_SkipNextHoleButton.setVisibility(View.GONE);
                            }
                        });

                        m_PositionChangeButton.post(new Runnable() {
                            @Override
                            public void run() {
                                m_PositionChangeButton.setVisibility(View.GONE);
                            }
                        });

                        m_PressurizeOrDumpButton.post(new Runnable() {
                            @Override
                            public void run() {
                                m_PressurizeOrDumpButton.setVisibility(View.GONE);
                            }
                        });

                        m_PauseButton.post(new Runnable() {
                            @Override
                            public void run() {
                                m_PauseButton.setText("일시정지");
                            }
                        });

                        alarmMessageText.post(new Runnable() {
                            @Override
                            public void run() {
                                alarmMessageText.setText("세척 중 입니다.");
                            }
                        });

                        isPause = false;

                        try {
                            String sendStr = "RESUME_CLEANING,";
                            byte[] sendBytes = sendStr.getBytes(StandardCharsets.US_ASCII);
                            m_MainSocketClient.send(sendBytes);
                        } catch (Exception e) {
                            LogManager.e("[MainActivity] [setFifthLayout] 세척 재개 커맨드 전송 에러 : " + e.getMessage());
                        }
                    }
                }).start();
            }
        });

        m_StopButton = m_FifthLayout.findViewById(R.id.stopButton);
        m_StopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isStopButtonPressed) {
                    isStopButtonPressed = true;

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String sendStr = "CANCEL_CLEANING,";
                                byte[] sendBytes = sendStr.getBytes(StandardCharsets.US_ASCII);
                                m_MainSocketClient.send(sendBytes);

                                LogManager.i("[MainActivity] [setFifthLayout] CANCEL_CLEANING 전송 완료");

                                int cnt = 0;

                                while (!m_ReplyCancelChecked) {
                                    Thread.sleep(10);
                                    cnt++;

                                    if (cnt > 100) {
                                        break;
                                    }

                                    errorCheck("CANCEL_CLEANING,");
                                }

                                if (m_ReplyCancelChecked) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            alarmMessageText.setText("세척 종료 중 입니다.");
                                        }
                                    });
                                }

                                m_ReplyCancelChecked = false;

                                isStopButtonPressed = false;
                            } catch (Exception e) {
                                LogManager.e("[MainActivity] [setFifthLayout] 세척 종료 커맨드 전송 에러 : " + e.getMessage());
                            }
                        }
                    }).start();
                }
            }
        });

        m_PressurizeOrDumpButton = (Button)m_FifthLayout.findViewById(R.id.pressurizeOrDumpButton);
        m_PressurizeOrDumpButton.setVisibility(View.GONE);
        m_PressurizeOrDumpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (m_PressurizeOrDumpButton.getText().toString().contains("가압 선택")) {
                    m_PressurizeOrDumpButton.setText("덤프 선택");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String sendStr = "PUMP_ON,";
                                byte[] sendBytes = sendStr.getBytes(StandardCharsets.US_ASCII);
                                m_MainSocketClient.send(sendBytes);
                            } catch (Exception e) {
                                LogManager.e("[MainActivity] [setFifthButton] 덤프 선택 커맨드 전송 에러 : " + e.getMessage());
                            }
                        }
                    }).start();
                } else {
                    m_PressurizeOrDumpButton.setText("가압 선택");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String sendStr = "PUMP_OFF,";
                                byte[] sendBytes = sendStr.getBytes(StandardCharsets.US_ASCII);
                                m_MainSocketClient.send(sendBytes);
                            } catch (Exception e) {
                                LogManager.e("[MainActivity] [setFifthButton] 가압 선택 커맨드 전송 에러 : " + e.getMessage());
                            }
                        }
                    }).start();
                }
            }
        });

        m_PositionChangeButton = (Button)m_FifthLayout.findViewById(R.id.positionChangeButton);
        m_PositionChangeButton.setVisibility(View.GONE);
        m_PositionChangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.requestWindowFeature(1);
                dialog.setContentView(R.layout.position_change_dialog);
                EditText m_PositionChange = (EditText)dialog.findViewById(R.id.positionChange);

                m_PositionChange.addTextChangedListener(new TextWatcher() {
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    public void afterTextChanged(Editable s) {
                        if (s.toString().startsWith("0") && s.toString().length() > 1) {
                            m_PositionChange.setText(s.toString().substring(1));
                            EditText editText = m_PositionChange;
                            editText.setSelection(editText.length());
                        }

                        if (m_PositionChange.getText().toString().length() > 0) {
                            int position = Integer.valueOf(m_PositionChange.getText().toString());

                            if (position < m_HoleMinNum) {
                                m_PositionChange.setText(String.valueOf(m_HoleMinNum));
                                m_PositionChange.setSelection(m_PositionChange.length());
                            }

                            if (position > m_HoleMaxNum) {
                                m_PositionChange.setText(String.valueOf(m_HoleMaxNum));
                                m_PositionChange.setSelection(m_PositionChange.length());
                            }
                        }
                    }
                });
                ((Button)dialog.findViewById(R.id.positionChangeNumericUpButton)).setOnTouchListener(new View.OnTouchListener() {
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getActionMasked()) {
                            case 0:
                                m_PositionChangeNumericUpButtonTouchDown = true;

                                new Thread(new Runnable() {
                                    public void run() {
                                        int cnt = 0;
                                        while (m_PositionChangeNumericUpButtonTouchDown) {
                                            try {
                                                m_PositionChange.post(new Runnable() {
                                                    public void run() {
                                                        m_PositionChange.setText((Integer.parseInt(m_PositionChange.getText().toString()) + 1) + "");
                                                        m_PositionChange.setSelection(m_PositionChange.length());
                                                    }
                                                });
                                                cnt++;
                                                if (cnt < 5) {
                                                    Thread.sleep(500);
                                                } else {
                                                    Thread.sleep(50);
                                                }
                                            } catch (Exception e) {
                                                return;
                                            }
                                        }
                                    }
                                }).start();
                                break;
                            case 1:
                                m_PositionChangeNumericUpButtonTouchDown = false;
                                break;
                        }
                        return false;
                    }
                });
                ((Button)dialog.findViewById(R.id.positionChangeNumericDownButton)).setOnTouchListener(new View.OnTouchListener() {
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getActionMasked()) {
                            case 0:
                                m_PositionChangeNumericDownButtonTouchDown = true;

                                new Thread(new Runnable() {
                                    public void run() {
                                        int cnt = 0;
                                        while (m_PositionChangeNumericDownButtonTouchDown) {
                                            try {
                                                m_PositionChange.post(new Runnable() {
                                                    public void run() {
                                                        int parseInt = Integer.parseInt(m_PositionChange.getText().toString()) - 1;
                                                        if (parseInt > -1) {
                                                            m_PositionChange.setText(parseInt + "");
                                                            m_PositionChange.setSelection(m_PositionChange.length());
                                                        }
                                                    }
                                                });
                                                cnt++;
                                                if (cnt < 5) {
                                                    Thread.sleep(500);
                                                } else {
                                                    Thread.sleep(50);
                                                }
                                            } catch (Exception e) {
                                                return;
                                            }
                                        }
                                    }
                                }).start();
                                break;
                            case 1:
                                m_PositionChangeNumericDownButtonTouchDown = false;
                                break;
                        }
                        return false;
                    }
                });
                ((Button)dialog.findViewById(R.id.positionChangeConfirmButton)).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    String sendStr = "MOVE_HOLE," + m_PositionChange.getText().toString() + ",";
                                    byte[] sendBytes = sendStr.getBytes(StandardCharsets.US_ASCII);
                                    m_MainSocketClient.send(sendBytes);

                                    m_CleaningManualLayout.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            m_CleaningManualLayout.setVisibility(View.GONE);
                                        }
                                    });

                                    m_SkipNextHoleButton.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            m_SkipNextHoleButton.setVisibility(View.GONE);
                                        }
                                    });

                                    m_PositionChangeButton.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            m_PositionChangeButton.setVisibility(View.GONE);
                                        }
                                    });

                                    m_PressurizeOrDumpButton.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            m_PressurizeOrDumpButton.setVisibility(View.GONE);
                                        }
                                    });

                                    m_PauseButton.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            m_PauseButton.setText("일시정지");
                                        }
                                    });

                                    alarmMessageText.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            alarmMessageText.setText("세척 중 입니다.");
                                        }
                                    });

                                    isPause = false;

                                    try {
                                        sendStr = "RESUME_CLEANING,";
                                        sendBytes = sendStr.getBytes(StandardCharsets.US_ASCII);
                                        m_MainSocketClient.send(sendBytes);
                                    } catch (Exception e) {
                                        LogManager.e("[MainActivity] [setFifthButton] 세척 재개 커맨드 전송 에러 : " + e.getMessage());
                                    }
                                } catch (Exception e) {
                                    LogManager.e("[MainActivity] [setFifthButton dialog.findViewById(R.id.positionChangeConfirmButton)).setOnClickListener] " + e.getMessage());
                                }
                            }
                        }).start();

                        dialog.dismiss();
                    }
                });
                ((Button)dialog.findViewById(R.id.positionChangeCancelButton)).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                dialog.getWindow().setAttributes(lp);
                dialog.show();
            }
        });

        m_PauseButton = (Button)m_FifthLayout.findViewById(R.id.pauseButton);
        m_PauseButton.setText("일시정지");
        m_PauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = m_PauseButton.getText().toString();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (text.equals("일시정지")) {
                                String sendStr = "PAUSE_CLEANING,";
                                byte[] sendBytes = sendStr.getBytes(StandardCharsets.US_ASCII);
                                m_MainSocketClient.send(sendBytes);

                                int cnt = 0;

                                while (!m_ReplyPauseChecked) {
                                    Thread.sleep(10);
                                    cnt++;

                                    if (cnt > 100) {
                                        break;
                                    }

                                    errorCheck("PAUSE_CLEANING,");
                                }

                                m_ReplyPauseChecked = false;
                            } else if (text.equals("계속하기")) {
                                String sendStr = "RESUME_CLEANING,";
                                byte[] sendBytes = sendStr.getBytes(StandardCharsets.US_ASCII);
                                m_MainSocketClient.send(sendBytes);

                                int cnt = 0;

                                while (!m_ReplyResumeChecked) {
                                    Thread.sleep(10);
                                    cnt++;

                                    if (cnt > 100) {
                                        break;
                                    }

                                    errorCheck("RESUME_CLEANING,");
                                }

                                m_ReplyResumeChecked = false;
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (text.equals("계속하기")) {
                                        alarmMessageText.setText("세척 중 입니다.");

                                        isPause = false;

                                        m_PauseButton.setText("일시정지");

                                        m_CleaningManualLayout.setVisibility(View.GONE);

                                        m_SkipNextHoleButton.setVisibility(View.GONE);

                                        m_PositionChangeButton.setVisibility(View.GONE);

                                        m_PressurizeOrDumpButton.setVisibility(View.GONE);
                                    } else if (text.equals("일시정지")) {
                                        alarmMessageText.setText("일시정지 중 입니다.");
                                        isPause = true;
                                        m_PauseButton.setText("계속하기");

                                        m_CleaningManualLayout.setVisibility(View.VISIBLE);

                                        m_SkipNextHoleButton.setVisibility(View.VISIBLE);

                                        m_PositionChangeButton.setVisibility(View.VISIBLE);

                                        m_PressurizeOrDumpButton.setVisibility(View.VISIBLE);
                                    }

                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                Thread.sleep(100);

                                                resetScale();
                                            } catch (Exception e) {
                                                LogManager.e("[MainActivity] [setFifthButton] 일시정지 버튼 클릭 리스케일 에러 : " + e.getMessage());
                                            }
                                        }
                                    }).start();
                                }
                            });
                        } catch (Exception e) {
                            LogManager.e("[MainActivity] [setFifthButton m_PauseButton.setOnClickListener] " + e.getMessage());
                        }
                    }
                }).start();
            }
        });

        Button m_CleaningJogUpButton = (Button)m_FifthLayout.findViewById(R.id.cleaningJogUpButton);
        m_CleaningJogUpButton.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case 0:
                        sendBroadcast(new Intent("com.example.myapplication.JogUpButtonDown"));
                        return false;
                    case 1:
                        sendBroadcast(new Intent("com.example.myapplication.JogUpButtonUp"));
                        return false;
                    default:
                        return false;
                }
            }
        });

        Button m_CleaningJogLeftButton = (Button)m_FifthLayout.findViewById(R.id.cleaningJogLeftButton);
        m_CleaningJogLeftButton.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case 0:
                        sendBroadcast(new Intent("com.example.myapplication.JogLeftButtonDown"));
                        return false;
                    case 1:
                        sendBroadcast(new Intent("com.example.myapplication.JogLeftButtonUp"));
                        return false;
                    default:
                        return false;
                }
            }
        });

        Button m_CleaningJogRightButton = (Button)m_FifthLayout.findViewById(R.id.cleaningJogRightButton);
        m_CleaningJogRightButton.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case 0:
                        sendBroadcast(new Intent("com.example.myapplication.JogRightButtonDown"));
                        return false;
                    case 1:
                        sendBroadcast(new Intent("com.example.myapplication.JogRightButtonUp"));
                        return false;
                    default:
                        return false;
                }
            }
        });

        Button m_CleaningJogDownButton = (Button)m_FifthLayout.findViewById(R.id.cleaningJogDownButton);
        m_CleaningJogDownButton.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case 0:
                        sendBroadcast(new Intent("com.example.myapplication.JogDownButtonDown"));
                        return false;
                    case 1:
                        sendBroadcast(new Intent("com.example.myapplication.JogDownButtonUp"));
                        return false;
                    default:
                        return false;
                }
            }
        });

        Button m_CleaningNozelForwardButton = (Button)m_FifthLayout.findViewById(R.id.cleaningNozelForwardButton);
        m_CleaningNozelForwardButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case 0:
                        sendBroadcast(new Intent("com.example.myapplication.allNozelForwardButtonDown"));
                        return false;
                    case 1:
                        sendBroadcast(new Intent("com.example.myapplication.allNozelForwardButtonUp"));
                        return false;
                    default:
                        return false;
                }
            }
        });

        Button m_CleaningNozelBackwardButton = (Button)m_FifthLayout.findViewById(R.id.cleaningNozelBackwardButton);
        m_CleaningNozelBackwardButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case 0:
                        sendBroadcast(new Intent("com.example.myapplication.allNozelBackwardButtonDown"));
                        return false;
                    case 1:
                        sendBroadcast(new Intent("com.example.myapplication.allNozelBackwardButtonUp"));
                        return false;
                    default:
                        return false;
                }
            }
        });

        scanHolesCheck();

        try {
            LibVLC libVlc = new LibVLC(this);
            mediaPlayer = new org.videolan.libvlc.MediaPlayer(libVlc);
            VLCVideoLayout videoLayout = m_FifthLayout.findViewById(R.id.rtspView);
            LinearLayout cctvLayout = (LinearLayout)m_FifthLayout.findViewById(R.id.cctvLayout);

            ImageButton cctvDisplayButton = (ImageButton)m_FifthLayout.findViewById(R.id.cctvDisplayButton);
            cctvDisplayButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isCctvDisplayVisibled) {
                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)cctvLayout.getLayoutParams();
                        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                        params.weight = 0f;
                        cctvLayout.setLayoutParams(params);
                        videoLayout.setVisibility(View.GONE);
                        isCctvDisplayVisibled = false;
                        cctvDisplayButton.setImageResource(R.drawable.ic_baseline_arrow_circle_down_24);
                    } else {
                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)cctvLayout.getLayoutParams();
                        params.height = 0;
                        params.weight = 1f;
                        cctvLayout.setLayoutParams(params);
                        videoLayout.setVisibility(View.VISIBLE);
                        isCctvDisplayVisibled = true;
                        cctvDisplayButton.setImageResource(R.drawable.ic_baseline_arrow_circle_up_24);
                    }
                }
            });

            mediaPlayer.attachViews(videoLayout, null, false, false);

            String url = "rtsp://admin:hansero980325!@192.168.1.108:554/avstream/channel=1/stream=0.sdp";

            Media media = new Media(libVlc, Uri.parse(url));
            media.setHWDecoderEnabled(true, false);
            media.addOption(":network-caching=200");

            mediaPlayer.setMedia(media);
            media.release();

            mediaPlayer.play();

            mediaPlayer.setVideoScale(MediaPlayer.ScaleType.SURFACE_BEST_FIT);
        } catch (Exception e) {
            LogManager.e("[MainActivity] [setFifthButton] " + e.getMessage());
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(50);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            resetScale();
                        }
                    });
                } catch (Exception e) {
                    LogManager.e("[MainActivity] [setFifthButton] 리스케일 에러 : " + e.getMessage());
                }
            }
        }).start();
    }

    private void sendStartCleaning() {
        // 홀 표시 완료 후 세척 시작
        String sendStr = "START_CLEANING,";

        if (isTestMode) {
            sendStr += "0,";
        } else {
            if (m_IsUseNozel) {
                sendStr += "1,";
            } else {
                sendStr += "0,";
            }
        }

        byte[] sendBytes = sendStr.getBytes(StandardCharsets.US_ASCII);
        // send(sendBytes);
        try {
            m_MainSocketClient.send(sendBytes);
        } catch (Exception e) {
            LogManager.e("[MainActivity] [sendStartCleaning] " + e.getMessage());
        }
    }

    private void sendScanHoles() {
        String sendStr = "SCAN_HOLES,";
        byte[] sendBytes = sendStr.getBytes(StandardCharsets.US_ASCII);
        // send(sendBytes);

        try {
            m_MainSocketClient.send(sendBytes);
        } catch (Exception e) {
            LogManager.e("[MainActivity] [sendScanHoles] " + e.getMessage());
        }

        // Log.i(TAG, "스캔홀 시작 전송");
    }

    private void sendCleaningData() {
        String sendStr = "CLEANING_DATA,";
        byte[] sendBytes = sendStr.getBytes(StandardCharsets.US_ASCII);

        try {
            m_MainSocketClient.send(sendBytes);
        } catch (Exception e) {
            LogManager.e("[MainActivity] [sendCleaningData] " + e.getMessage());
        }
    }

    private void displayScanHoles() {
        // 홀 표시
        Canvas canvas = new Canvas();
        Paint paint = new Paint();

        if (m_CopyBitmap != null) {
            // m_CopyBitmap.recycle();
            m_CopyBitmap = null;
        }

        m_CopyBitmap = m_Bitmap.copy(Bitmap.Config.ARGB_8888, true);
        canvas.setBitmap(m_CopyBitmap);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("rebuild_preference", MODE_PRIVATE);
        final SharedPreferences.Editor editor = pref.edit();
        int holeSize = pref.getInt("holeSize", 15);
        int holeThickness = pref.getInt("holeThickness", 5);
        int holeFontSize = pref.getInt("holeFontSize", 12);
        int holeFontThickness = pref.getInt("holeFontThickness", 1);

        for (int i = 0; i < m_HoleDataList.size(); i++) {
            try {
                HoleData holeData = m_HoleDataList.get(i);

                switch (holeData.Color) {
                    case 0:
                        paint.setStyle(Paint.Style.STROKE);
                        // paint.setStrokeWidth(10.0f);
                        paint.setStrokeWidth((float)holeThickness);
                        paint.setColor(Color.WHITE);
                        break;
                    case 1:
                        paint.setStyle(Paint.Style.STROKE);
                        // paint.setStrokeWidth(10.0f);
                        paint.setStrokeWidth((float)holeThickness);
                        paint.setColor(Color.YELLOW);
                        break;
                    case 2:
                        paint.setStyle(Paint.Style.STROKE);
                        // paint.setStrokeWidth(10.0f);
                        paint.setStrokeWidth((float)holeThickness);
                        paint.setColor(Color.BLUE);
                        break;
                    case 3:
                        paint.setStyle(Paint.Style.STROKE);
                        // paint.setStrokeWidth(10.0f);
                        paint.setStrokeWidth((float)holeThickness);
                        paint.setColor(Color.RED);
                        break;
                }

                canvas.drawCircle((float)holeData.X, (float)holeData.Y, (float)holeSize, paint);

                if (holeData.Name != null || !holeData.Name.trim().equals("")) {
                    paint.setColor(Color.WHITE);
                    paint.setStrokeWidth((float)holeFontThickness);
                    paint.setTextSize(holeFontSize);
                    paint.setTextAlign(Paint.Align.CENTER);
                    Rect textBounds = new Rect();
                    paint.getTextBounds(holeData.Name, 0, holeData.Name.length(), textBounds);
                    canvas.drawText(holeData.Name, (float)holeData.X, (float)(holeData.Y - textBounds.exactCenterY()), paint);

                    if (!holeData.Name.trim().equals("")) {
                        int holeNum = Integer.valueOf(holeData.Name);

                        if (holeNum < m_HoleMinNum) {
                            m_HoleMinNum = holeNum;
                        }

                        if (holeNum > m_HoleMaxNum) {
                            m_HoleMaxNum = holeNum;
                        }
                    }
                }
            } catch (Exception e) {
                // Log.i(TAG, "displayScanHoles 에러 : " + e.getMessage());
                LogManager.e("[MainActivity] [displayScanHoles] " + e.getMessage());
            }
        }
    }

    private int displayScanHoles(int id) {
        // 홀 표시
        Canvas canvas = new Canvas();
        Paint paint = new Paint();

        if (m_CopyBitmap != null) {
            // m_CopyBitmap.recycle();
            m_CopyBitmap = null;
        }

        m_CopyBitmap = m_Bitmap.copy(Bitmap.Config.ARGB_8888, true);
        canvas.setBitmap(m_CopyBitmap);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("rebuild_preference", MODE_PRIVATE);
        final SharedPreferences.Editor editor = pref.edit();
        int holeSize = pref.getInt("holeSize", 15);
        int holeThickness = pref.getInt("holeThickness", 5);
        int holeFontSize = pref.getInt("holeFontSize", 12);
        int holeFontThickness = pref.getInt("holeFontThickness", 1);

        int cnt = 0;

        for (int i = 0; i < m_HoleDataList.size(); i++) {
            HoleData holeData = m_HoleDataList.get(i);

            switch (holeData.Color) {
                case 0:
                    paint.setStyle(Paint.Style.STROKE);
                    // paint.setStrokeWidth(10.0f);
                    paint.setStrokeWidth((float)holeThickness);
                    paint.setColor(Color.WHITE);
                    break;
                case 1:
                    paint.setStyle(Paint.Style.STROKE);
                    // paint.setStrokeWidth(10.0f);
                    paint.setStrokeWidth((float)holeThickness);
                    paint.setColor(Color.YELLOW);
                    break;
                case 2:
                    paint.setStyle(Paint.Style.STROKE);
                    // paint.setStrokeWidth(10.0f);
                    paint.setStrokeWidth((float)holeThickness);
                    paint.setColor(Color.BLUE);
                    break;
                case 3:
                    paint.setStyle(Paint.Style.STROKE);
                    // paint.setStrokeWidth(10.0f);
                    paint.setStrokeWidth((float)holeThickness);
                    paint.setColor(Color.RED);
                    break;
            }

            canvas.drawCircle((float)holeData.X, (float)holeData.Y, (float)holeSize, paint);

            if (holeData.Name != null || !holeData.Name.trim().equals("")) {
                paint.setColor(Color.WHITE);
                paint.setStrokeWidth((float)holeFontThickness);
                paint.setTextSize(holeFontSize);
                paint.setTextAlign(Paint.Align.CENTER);
                Rect textBounds = new Rect();
                paint.getTextBounds(holeData.Name, 0, holeData.Name.length(), textBounds);
                canvas.drawText(holeData.Name, (float)holeData.X, (float)(holeData.Y - textBounds.exactCenterY()), paint);

                if (!holeData.Name.trim().equals("")) {
                    int holeNum = Integer.valueOf(holeData.Name);

                    if (holeNum < m_HoleMinNum) {
                        m_HoleMinNum = holeNum;
                    }

                    if (holeNum > m_HoleMaxNum) {
                        m_HoleMaxNum = holeNum;
                    }
                }
            }

            cnt++;
        }

        // 그린 홀 개수 반환
        return cnt;
    }

    private int errorMsgCount = 0;

    private void scanHolesCheck() {
        LogManager.i("[MainActivity] [scanHolesCheck] scanHolesCheck 실행");

        errorMsgCount = 0;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int cnt = 0;

                    // 스캔 시작 응답 체크
                    while (!m_ReplyStartScanChecked) {
                        Thread.sleep(10);
                        cnt++;

                        if (cnt > 100) {
                            break;
                        }

                        errorCheck("START_SCAN,");
                    }

                    m_ReplyStartScanChecked = false;

                    sendStartCleaning();

                    cnt = 0;

                    while (!m_ReplyStartCleaningChecked) {
                        Thread.sleep(10);
                        cnt++;

                        if (cnt > 100) {
                            break;
                        }

                        errorCheck("START_CLEANING,");
                    }

                    m_ReplyStartCleaningChecked = false;

                    m_ReplyScanHolesChecked = false;

                    while (true) {
                        try {
                            if (m_ReplyScanHolesChecked) {
                                displayScanHoles();

                                m_CleaningView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        m_CleaningView.setImageBitmap(m_CopyBitmap);
                                        // resetScale();
                                    }
                                });
                            }

                            if (!isCompleted) {
                                m_ReplyScanHolesChecked = false;

                                sendScanHoles();

                                cnt = 0;

                                while (!m_ReplyScanHolesChecked) {
                                    Thread.sleep(10);
                                    cnt++;

                                    if (cnt > 100) {
                                        break;
                                    }

                                    errorCheck("SCAN_HOLES,");
                                }

                                m_ReplyCleaningDataChecked = false;

                                sendCleaningData();

                                cnt = 0;

                                while (!m_ReplyCleaningDataChecked) {
                                    Thread.sleep(10);
                                    cnt++;

                                    if (cnt > 100) {
                                        break;
                                    }

                                    errorCheck("CLEANING_DATA,");
                                }

                                m_ReplyCleaningDataChecked = false;

                                m_CleaningProgressBar.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        // m_CleaningProgressBar.setMin(0);
                                        m_CleaningProgressBar.setMax(cleaningMaxCount);
                                        m_CleaningProgressBar.setProgress(cleaningCount);
                                    }
                                });

                                if (errorMsgCount % 4 == 0) {
                                    // 에러 메시지 요청
                                    String sendStr = "ERROR_MSG,";
                                    byte[] sendBytes = sendStr.getBytes(StandardCharsets.US_ASCII);

                                    try {
                                        m_MainSocketClient.send(sendBytes);
                                    } catch (Exception e) {
                                        LogManager.e("[MainActivity] [scanHolesCheck] 에러 메시지 요청 커맨드 전송 에러 : " + e.getMessage());
                                    }

                                    cnt = 0;

                                    while (!m_ReplyErrorMsgChecked) {
                                        Thread.sleep(10);
                                        cnt++;

                                        if (cnt > 100) {
                                            break;
                                        }

                                        errorCheck("ERROR_MSG,");
                                    }

                                    m_ReplyErrorMsgChecked = false;
                                }

                                errorMsgCount++;
                            } else {
                                m_ReplyScanHolesChecked = false;

                                sendScanHoles();

                                cnt = 0;

                                while (!m_ReplyScanHolesChecked) {
                                    Thread.sleep(10);
                                    cnt++;

                                    if (cnt > 100) {
                                        break;
                                    }

                                    errorCheck("SCAN_HOLES,");
                                }

                                displayScanHoles();

                                m_CleaningView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        m_CleaningView.setImageBitmap(m_CopyBitmap);
                                        // resetScale();
                                    }
                                });

                                m_ReplyCleaningDataChecked = false;

                                sendCleaningData();

                                cnt = 0;

                                while (!m_ReplyCleaningDataChecked) {
                                    Thread.sleep(10);
                                    cnt++;

                                    if (cnt > 100) {
                                        break;
                                    }
                                }

                                m_ReplyCleaningDataChecked = false;

                                m_CleaningProgressBar.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        // m_CleaningProgressBar.setMin(0);
                                        m_CleaningProgressBar.setMax(cleaningMaxCount);
                                        m_CleaningProgressBar.setProgress(cleaningCount);
                                    }
                                });

                                break;
                            }
                        } catch (Exception e) {
                            // Log.e(TAG, "scanHolesCheck 내부 에러 : " + e.getMessage());
                            LogManager.e("[MainActivity] [scanHolesCheck] 스캔 홀 그리기 에러 : " + e.getMessage());
                        }

                        Thread.sleep(100);
                    }
                } catch (Exception e) {
                    LogManager.e("[MainActivity] [scanHolesCheck] " + e.getMessage());
                }

                setSixthLayout();
            }
        }).start();
    }

    private org.videolan.libvlc.MediaPlayer mediaPlayer = null;

    private void setSixthLayout() {
        m_FirstLayout.post(new Runnable() {
            @Override
            public void run() {
                m_FirstLayout.setVisibility(View.GONE);
            }
        });

        m_SecondLayout.post(new Runnable() {
            @Override
            public void run() {
                m_SecondLayout.setVisibility(View.GONE);
            }
        });

        m_ThirdLayout.post(new Runnable() {
            @Override
            public void run() {
                m_ThirdLayout.setVisibility(View.GONE);
            }
        });

        m_FourthLayout.post(new Runnable() {
            @Override
            public void run() {
                m_FourthLayout.setVisibility(View.GONE);
            }
        });

        m_FifthLayout.post(new Runnable() {
            @Override
            public void run() {
                m_FifthLayout.setVisibility(View.GONE);
            }
        });

        m_SixthLayout.post(new Runnable() {
            @Override
            public void run() {
                m_SixthLayout.setVisibility(View.VISIBLE);
            }
        });

        setSixthButton();

        m_LayoutIndex = 5;
    }

    private void setSixthButton() {
        TextView m_RunTimeText = (TextView)m_SixthLayout.findViewById(R.id.runTime);
        TextView m_ResultWorkNumber = (TextView)m_SixthLayout.findViewById(R.id.resultWorkNumber);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mediaPlayer.stop();

                if (isTestMode) {
                    m_ResultWorkNumber.setText("TEST");
                } else {
                    m_ResultWorkNumber.setText(workNumber);
                }
            }
        });

        m_RunTimeText.post(new Runnable() {
            @Override
            public void run() {
                m_RunTimeText.setText(m_FinishCleaningData.RunTime);
            }
        });

        TextView m_TotalHoleCountText = (TextView)m_SixthLayout.findViewById(R.id.totalHoleCount);
        m_TotalHoleCountText.post(new Runnable() {
            @Override
            public void run() {
                m_TotalHoleCountText.setText(m_FinishCleaningData.TotalHoleCount);
            }
        });

        TextView m_CleaningHoleCountText = (TextView)m_SixthLayout.findViewById(R.id.cleaningHoleCount);
        m_CleaningHoleCountText.post(new Runnable() {
            @Override
            public void run() {
                m_CleaningHoleCountText.setText(m_FinishCleaningData.CleaningHoleCount);
            }
        });

        TextView m_NoCleaningHoleCountText = (TextView)m_SixthLayout.findViewById(R.id.noCleaningHoleCount);
        m_NoCleaningHoleCountText.post(new Runnable() {
            @Override
            public void run() {
                m_NoCleaningHoleCountText.setText(m_FinishCleaningData.NoCleaningHoleCount);
            }
        });

        TextView m_StartDateTimeText = (TextView)m_SixthLayout.findViewById(R.id.startDateTime);
        m_StartDateTimeText.post(new Runnable() {
            @Override
            public void run() {
                m_StartDateTimeText.setText(m_FinishCleaningData.StartDateTime);
            }
        });

        TextView m_EndDateTimeText = (TextView)m_SixthLayout.findViewById(R.id.endDateTime);
        m_EndDateTimeText.post(new Runnable() {
            @Override
            public void run() {
                m_EndDateTimeText.setText(m_FinishCleaningData.EndDateTime);
            }
        });

        m_ResultViewMatrix = new Matrix();
        m_ResultViewSavedMatrix = new Matrix();

        m_ResultView = (ImageView)m_SixthLayout.findViewById(R.id.resultView);

        m_ReplyScanHolesChecked = false;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String sendStr = "SCAN_HOLES,";
                    byte[] sendBytes = sendStr.getBytes(StandardCharsets.US_ASCII);

                    try {
                        m_MainSocketClient.send(sendBytes);
                    } catch (Exception e) {
                        LogManager.e("[MainActivity] [setSixthLayout] 스캔 홀 커맨드 전송 에러 : " + e.getMessage());
                    }

                    // Log.i(TAG, "스캔홀 시작 전송");

                    int cnt = 0;

                    while (!m_ReplyScanHolesChecked) {
                        Thread.sleep(10);
                        cnt++;

                        if (cnt > 100) {
                            break;
                        }
                    }

                    // 홀 표시
                    Canvas canvas = new Canvas();
                    Paint paint = new Paint();

                    if (m_CopyBitmap != null) {
                        // m_CopyBitmap.recycle();
                        m_CopyBitmap = null;
                    }

                    m_CopyBitmap = m_Bitmap.copy(Bitmap.Config.ARGB_8888, true);
                    canvas.setBitmap(m_CopyBitmap);

                    SharedPreferences pref = getApplicationContext().getSharedPreferences("rebuild_preference", MODE_PRIVATE);
                    final SharedPreferences.Editor editor = pref.edit();
                    int holeSize = pref.getInt("holeSize", 15);
                    int holeThickness = pref.getInt("holeThickness", 5);
                    int holeFontSize = pref.getInt("holeFontSize", 12);
                    int holeFontThickness = pref.getInt("holeFontThickness", 1);

                    for (int i = 0; i < m_HoleDataList.size(); i++) {
                        try {
                            HoleData holeData = m_HoleDataList.get(i);

                            switch (holeData.Color) {
                                case 0:
                                    paint.setStyle(Paint.Style.STROKE);
                                    // paint.setStrokeWidth(10.0f);
                                    paint.setStrokeWidth((float)holeThickness);
                                    paint.setColor(Color.WHITE);
                                    break;
                                case 1:
                                    paint.setStyle(Paint.Style.STROKE);
                                    // paint.setStrokeWidth(10.0f);
                                    paint.setStrokeWidth((float)holeThickness);
                                    paint.setColor(Color.YELLOW);
                                    break;
                                case 2:
                                    paint.setStyle(Paint.Style.STROKE);
                                    // paint.setStrokeWidth(10.0f);
                                    paint.setStrokeWidth((float)holeThickness);
                                    paint.setColor(Color.BLUE);
                                    break;
                                case 3:
                                    paint.setStyle(Paint.Style.STROKE);
                                    // paint.setStrokeWidth(10.0f);
                                    paint.setStrokeWidth((float)holeThickness);
                                    paint.setColor(Color.RED);
                                    break;
                            }

                            canvas.drawCircle((float)holeData.X, (float)holeData.Y, (float)holeSize, paint);

                            if (holeData.Name != null || !holeData.Name.equals("")) {
                                paint.setColor(Color.WHITE);
                                paint.setStrokeWidth((float)holeFontThickness);
                                paint.setTextSize(holeFontSize);
                                paint.setTextAlign(Paint.Align.CENTER);
                                Rect textBounds = new Rect();
                                paint.getTextBounds(holeData.Name, 0, holeData.Name.length(), textBounds);
                                canvas.drawText(holeData.Name, (float)holeData.X, (float)(holeData.Y - textBounds.exactCenterY()), paint);
                            }
                        } catch (Exception e) {
                            // Log.e(TAG, "setSixthLayout 내부 에러 : " + e.getMessage());
                            LogManager.e("[MainActivity] [setSixthLayout] 내부 에러 : " + e.getMessage());
                        }

                        Thread.sleep(1);
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            m_ResultView.setOnTouchListener(onResultViewTouch);
                            m_ResultView.setScaleType(ImageView.ScaleType.MATRIX);
                            m_ResultView.setImageBitmap(m_CopyBitmap);
                            resetResultViewScale();
                        }
                    });
                } catch (Exception e) {
                    // Log.e(TAG, "setSixthLayout 에러 : " + e.getMessage());
                    LogManager.e("[MainActivity] [setSixthLayout] 에러 : " + e.getMessage());
                }
            }
        }).start();

        // 미세척홀 재시도 버튼
        Button m_RetryButton = (Button)m_SixthLayout.findViewById(R.id.retryButton);
        m_RetryButton.post(new Runnable() {
            @Override
            public void run() {
                m_RetryButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    String sendStr = "RETRY_START,";
                                    byte[] sendBytes = sendStr.getBytes(StandardCharsets.US_ASCII);
                                    m_MainSocketClient.send(sendBytes);

                                    int cnt = 0;

                                    while (!m_ReplyRetryStartChecked) {
                                        Thread.sleep(10);
                                        cnt++;

                                        if (cnt > 100) {
                                            break;
                                        }
                                    }

                                    isCompleted = false;
                                    isPause = false;

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            setEditHoleLayout();
                                        }
                                    });
                                } catch (Exception e) {
                                    LogManager.e("[MainActivity] [setSixthLayout] 세척 재시도 커맨드 전송 에러 : " + e.getMessage());
                                }
                            }
                        }).start();
                    }
                });
            }
        });

        m_SixthConfirmButton = m_SixthLayout.findViewById(R.id.sixthConfirmButton);
        m_SixthConfirmButton.post(new Runnable() {
            @Override
            public void run() {
                m_SixthConfirmButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setFirstLayout();
                    }
                });
            }
        });

        m_StartChecked = false;
    }

    private long delay = 0;
    private boolean editHoleImageViewMoved = false;
    private int editHoleImageViewMoveCount = 0;
    private long longTouchDelay = 0;
    private boolean isLongTouched = false;
    private boolean isActionUp = false;

    private View.OnTouchListener onEditHoleImageViewTouch = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (view.equals(m_EditHoleImageView)) {
                int action = motionEvent.getActionMasked();

                switch (action & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        m_TouchMode = TOUCH_MODE.SINGLE;
                        downEditHoleImageViewSingleEvent(motionEvent);

                        longTouchDelay = 0;
                        isActionUp = false;
                        isLongTouched = false;

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                while (true) {
                                    try {
                                        Thread.sleep(10);

                                        if (editHoleImageViewMoved) {
                                            break;
                                        }

                                        longTouchDelay += 10;

                                        if (longTouchDelay >= 1000) {
                                            resetEditHoleImageViewScale();
                                            isLongTouched = true;
                                            break;
                                        }

                                        if (isActionUp) {
                                            break;
                                        }
                                    } catch (Exception e) {
                                        LogManager.e("[MainActivity] [onEditHoleImageViewTouch] ACTION_DOWN 에러" + e.getMessage());
                                    }
                                }
                            }
                        }).start();

                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        if (motionEvent.getPointerCount() == 2) {
                            m_TouchMode = TOUCH_MODE.MULTI;
                            downEditHoleImageViewMultiEvent(motionEvent);
                        }

                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (!isLongTouched) {
                            if (m_TouchMode == TOUCH_MODE.SINGLE) {
                                moveEditHoleImageViewSingleEvent(motionEvent);
                                Log.e(TAG, "ACTION_MOVE");
                                editHoleImageViewMoveCount++;

                                if (editHoleImageViewMoveCount > 5) {
                                    editHoleImageViewMoved = true;
                                }
                            } else if (m_TouchMode == TOUCH_MODE.MULTI) {
                                try {
                                    moveEditHoleImageViewMultiEvent(motionEvent);
                                    editHoleImageViewMoved = true;
                                } catch (Exception e) {
                                    LogManager.e("[MainActivity] [onEditHoleImageViewTouch] ACTION_MOVE 에러 : " + e.getMessage());
                                }
                            }
                        }

                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        m_TouchMode = TOUCH_MODE.NONE;

                        Log.e(TAG, "ACTION_POINTER_UP");

                        break;
                    case MotionEvent.ACTION_UP:
                        Log.e(TAG, "ACTION_UP");

                        /*
                        if (System.currentTimeMillis() > delay) {
                            delay = System.currentTimeMillis() + 200;
                        }

                        if (System.currentTimeMillis() <= delay) {
                            // Log.i(TAG, "두번 터치");

                            resetEditHoleImageViewScale();
                            break;
                        }
                        */

                        isActionUp = true;

                        if (editHoleImageViewMoved) {
                            editHoleImageViewMoved = false;
                            editHoleImageViewMoveCount = 0;
                        } else if (isLongTouched) {
                            break;
                        } else {
                            if (!isEditHoleRunning) {
                                Log.i(TAG, "편집 중");

                                if (m_EditHoleMode == 1) {
                                    isEditHoleRunning = true;
                                    Matrix inverse = new Matrix();
                                    m_EditHoleImageView.getImageMatrix().invert(inverse);
                                    float[] touchPoint = new float[] { motionEvent.getX(), motionEvent.getY() };
                                    inverse.mapPoints(touchPoint);

                                    String sendStr = "ADD_HOLE," + touchPoint[0] + "," + touchPoint[1];
                                    byte[] sendBytes = sendStr.getBytes(StandardCharsets.US_ASCII);

                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                m_MainSocketClient.send(sendBytes);

                                                Log.e(TAG, "ADD_HOLE 전송");

                                                int cnt = 0;

                                                while (!m_ReplyAddHoleChecked) {
                                                    Thread.sleep(10);
                                                    cnt++;

                                                    if (cnt > 100) {
                                                        break;
                                                    }
                                                }

                                                m_ReplyAddHoleChecked = false;

                                                m_HoleDataList.clear();

                                                m_ReplyScanHolesChecked = false;

                                                while (true) {
                                                    try {
                                                        sendScanHoles();

                                                        cnt = 0;

                                                        while (!m_ReplyScanHolesChecked) {
                                                            Thread.sleep(10);
                                                            cnt++;

                                                            if (cnt > 100) {
                                                                break;
                                                            }
                                                        }

                                                        if (m_ReplyScanHolesChecked) {
                                                            cnt = displayScanHoles(0);
                                                        }

                                                        m_ReplyScanHolesChecked = false;

                                                        m_EditHoleImageView.post(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                m_EditHoleImageView.setImageBitmap(m_CopyBitmap);
                                                                // resetEditHoleImageViewScale();
                                                            }
                                                        });

                                                        if (m_ScanHoleCount > 0 && m_ScanHoleCount == cnt) {
                                                            Log.i(TAG, "스캔 홀 갯수 : " + m_ScanHoleCount);
                                                            Log.i(TAG, "REMOVE_HOLE 빠져나옴");
                                                            isEditHoleRunning = false;
                                                            break;
                                                        }

                                                        Thread.sleep(100);
                                                    } catch (Exception e) {
                                                        LogManager.e("[MainActivity] [onEditHoleImageViewTouch] 홀 추가 그리기 에러 : " + e.getMessage());
                                                    }
                                                }
                                            } catch (Exception e) {
                                                LogManager.e("[MainActivity] [onEditHoleImageViewTouch] 홀 추가 에러 : " + e.getMessage());
                                            }
                                        }
                                    }).start();
                                } else if (m_EditHoleMode == 2) {
                                    isEditHoleRunning = true;
                                    Matrix inverse = new Matrix();
                                    m_EditHoleImageView.getImageMatrix().invert(inverse);
                                    float[] touchPoint = new float[] { motionEvent.getX(), motionEvent.getY() };
                                    inverse.mapPoints(touchPoint);

                                    String sendStr = "REMOVE_HOLE," + touchPoint[0] + "," + touchPoint[1];
                                    byte[] sendBytes = sendStr.getBytes(StandardCharsets.US_ASCII);

                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                m_MainSocketClient.send(sendBytes);

                                                Log.e(TAG, "REMOVE_HOLE 전송");

                                                int cnt = 0;

                                                while (!m_ReplyRemoveHoleChecked) {
                                                    Thread.sleep(10);
                                                    cnt++;

                                                    if (cnt > 100) {
                                                        break;
                                                    }
                                                }

                                                m_ReplyRemoveHoleChecked = false;

                                                m_HoleDataList.clear();

                                                while (true) {
                                                    try {
                                                        sendScanHoles();

                                                        cnt = 0;

                                                        while (!m_ReplyScanHolesChecked) {
                                                            Thread.sleep(10);
                                                            cnt++;

                                                            if (cnt > 100) {
                                                                break;
                                                            }
                                                        }

                                                        if (m_ReplyScanHolesChecked) {
                                                            cnt = displayScanHoles(0);
                                                        }

                                                        m_ReplyScanHolesChecked = false;

                                                        m_EditHoleImageView.post(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                m_EditHoleImageView.setImageBitmap(m_CopyBitmap);
                                                                // resetEditHoleImageViewScale();
                                                            }
                                                        });

                                                        if (m_ScanHoleCount > 0 && m_ScanHoleCount == cnt) {
                                                            Log.i(TAG, "스캔 홀 갯수 : " + m_ScanHoleCount);
                                                            Log.i(TAG, "REMOVE_HOLE 빠져나옴");
                                                            isEditHoleRunning = false;
                                                            break;
                                                        }

                                                        Thread.sleep(100);
                                                    } catch (Exception e) {
                                                        LogManager.e("[MainActivity] [onEditHoleImageViewTouch] 홀 제거 그리기 에러 : " + e.getMessage());
                                                    }
                                                }
                                            } catch (Exception e) {
                                                LogManager.e("[MainActivity] [onEditHoleImageViewTouch] 홀 추가 에러 : " + e.getMessage());
                                            }
                                        }
                                    }).start();
                                }

                                editHoleImageViewMoved = false;
                                editHoleImageViewMoveCount = 0;
                            }
                        }

                        break;
                }
            }

            return true;
        }
    };

    private View.OnTouchListener onTouch = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (view.equals(m_CleaningView)) {
                int action = motionEvent.getActionMasked();

                switch (action & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        m_TouchMode = TOUCH_MODE.SINGLE;
                        downSingleEvent(motionEvent);

                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        if (motionEvent.getPointerCount() == 2) {
                            m_TouchMode = TOUCH_MODE.MULTI;
                            downMultiEvent(motionEvent);
                        }

                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (m_TouchMode == TOUCH_MODE.SINGLE) {
                            moveSingleEvent(motionEvent);
                        } else if (m_TouchMode == TOUCH_MODE.MULTI) {
                            moveMultiEvent(motionEvent);
                        }

                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        m_TouchMode = TOUCH_MODE.NONE;

                        break;
                    case MotionEvent.ACTION_UP:
                        if (System.currentTimeMillis() > delay) {
                            delay = System.currentTimeMillis() + 200;
                            break;
                        }

                        if (System.currentTimeMillis() <= delay) {
                            // Log.i(TAG, "두번 터치");

                            resetScale();
                        }

                        break;
                }
            }

            return true;
        }
    };

    private View.OnTouchListener onResultViewTouch = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (view.equals(m_ResultView)) {
                int action = motionEvent.getActionMasked();

                switch (action & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        m_TouchMode = TOUCH_MODE.SINGLE;
                        downResultViewSingleEvent(motionEvent);

                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        if (motionEvent.getPointerCount() == 2) {
                            m_TouchMode = TOUCH_MODE.MULTI;
                            downResultViewMultiEvent(motionEvent);
                        }

                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (m_TouchMode == TOUCH_MODE.SINGLE) {
                            moveResultViewSingleEvent(motionEvent);
                        } else if (m_TouchMode == TOUCH_MODE.MULTI) {
                            moveResultViewMultiEvent(motionEvent);
                        }

                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        m_TouchMode = TOUCH_MODE.NONE;

                        break;
                    case MotionEvent.ACTION_UP:
                        if (System.currentTimeMillis() > delay) {
                            delay = System.currentTimeMillis() + 200;
                            break;
                        }

                        if (System.currentTimeMillis() <= delay) {
                            // Log.i(TAG, "두번 터치");

                            resetResultViewScale();
                        }

                        break;
                }
            }

            return true;
        }
    };

    private void downEditHoleImageViewSingleEvent(MotionEvent event) {
        m_EditHoleImageViewSavedMatrix.set(m_EditHoleImageViewMatrix);
        m_EditHoleImageViewStartPoint = new PointF(event.getX(), event.getY());
    }

    private void downSingleEvent(MotionEvent event) {
        m_SavedMatrix.set(m_Matrix);
        m_StartPoint = new PointF(event.getX(), event.getY());
    }

    private void downResultViewSingleEvent(MotionEvent event) {
        m_ResultViewSavedMatrix.set(m_ResultViewMatrix);
        m_ResultViewStartPoint = new PointF(event.getX(), event.getY());
    }

    private void downEditHoleImageViewMultiEvent(MotionEvent event) {
        m_EditHoleImageViewOldDistance = getDistance(event);

        if (m_EditHoleImageViewOldDistance > 5f) {
            m_EditHoleImageViewSavedMatrix.set(m_EditHoleImageViewMatrix);
            m_EditHoleImageViewMidPoint = getMidPoint(event);
        }
    }

    private void downMultiEvent(MotionEvent event) {
        m_OldDistance = getDistance(event);

        if (m_OldDistance > 5f) {
            m_SavedMatrix.set(m_Matrix);
            m_MidPoint = getMidPoint(event);
        }
    }

    private void downResultViewMultiEvent(MotionEvent event) {
        m_ResultViewOldDistance = getDistance(event);

        if (m_ResultViewOldDistance > 5f) {
            m_ResultViewSavedMatrix.set(m_ResultViewMatrix);
            m_ResultViewMidPoint = getMidPoint(event);
        }
    }

    private PointF getMidPoint(MotionEvent e) {
        float x = (e.getX(0) + e.getX(1)) / 2;
        float y = (e.getY(0) + e.getY(1)) / 2;
        return new PointF(x, y);
    }

    private float getDistance(MotionEvent e) {
        float x = e.getX(0) - e.getX(1);
        float y = e.getY(0) - e.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private void moveEditHoleImageViewSingleEvent(MotionEvent event) {
        m_EditHoleImageViewMatrix.set(m_EditHoleImageViewSavedMatrix);
        m_EditHoleImageViewMatrix.postTranslate(event.getX() - m_EditHoleImageViewStartPoint.x, event.getY() - m_EditHoleImageViewStartPoint.y);
        m_EditHoleImageView.setImageMatrix(m_EditHoleImageViewMatrix);
    }

    private void moveSingleEvent(MotionEvent event) {
        m_Matrix.set(m_SavedMatrix);
        m_Matrix.postTranslate(event.getX() - m_StartPoint.x, event.getY() - m_StartPoint.y);
        m_CleaningView.setImageMatrix(m_Matrix);
    }

    private void moveResultViewSingleEvent(MotionEvent event) {
        m_ResultViewMatrix.set(m_ResultViewSavedMatrix);
        m_ResultViewMatrix.postTranslate(event.getX() - m_ResultViewStartPoint.x, event.getY() - m_ResultViewStartPoint.y);
        m_ResultView.setImageMatrix(m_ResultViewMatrix);
    }

    private void moveEditHoleImageViewMultiEvent(MotionEvent event) {
        float newDistance = getDistance(event);

        if (newDistance > 5f) {
            m_EditHoleImageViewMatrix.set(m_EditHoleImageViewSavedMatrix);
            float scale = newDistance / m_EditHoleImageViewOldDistance;
            m_EditHoleImageViewMatrix.postScale(scale, scale, m_EditHoleImageViewMidPoint.x, m_EditHoleImageViewMidPoint.y);
            m_EditHoleImageView.setImageMatrix(m_EditHoleImageViewMatrix);
        }
    }

    private void moveMultiEvent(MotionEvent event) {
        float newDistance = getDistance(event);

        if (newDistance > 5f) {
            m_Matrix.set(m_SavedMatrix);
            float scale = newDistance / m_OldDistance;
            m_Matrix.postScale(scale, scale, m_MidPoint.x, m_MidPoint.y);
            m_CleaningView.setImageMatrix(m_Matrix);
        }
    }

    private void moveResultViewMultiEvent(MotionEvent event) {
        float newDistance = getDistance(event);

        if (newDistance > 5f) {
            m_ResultViewMatrix.set(m_ResultViewSavedMatrix);
            float scale = newDistance / m_ResultViewOldDistance;
            m_ResultViewMatrix.postScale(scale, scale, m_ResultViewMidPoint.x, m_ResultViewMidPoint.y);
            m_ResultView.setImageMatrix(m_ResultViewMatrix);
        }
    }

    @Override
    public void onBackPressed() {
        Log.i(TAG, "현재 레이아웃 인덱스 : " + this.m_LayoutIndex);
        int i = this.m_LayoutIndex;
        switch (i) {
            case 1:
                this.m_LayoutIndex = i - 1;
                setFirstLayout();
                return;
            case 2:
                setSecondLayout();
                return;
            case 6:
                setFirstLayout();
                Log.i(TAG, "변경 레이아웃 인덱스 : " + this.m_LayoutIndex);
                return;
            case 7:
                this.m_LayoutIndex = 2;
                setThirdLayout();
                return;
            default:
                return;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 1015) {
            Log.d(TAG, "비상정지");

            String sendStr = "EMERGENCY_STOP,";
            byte[] sendBytes = sendStr.getBytes(StandardCharsets.US_ASCII);
            // send(sendBytes);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        m_MainSocketClient.send(sendBytes);
                    } catch (Exception e) {
                        LogManager.e("[MainActivity] [onKeyDown] 비상 정지 커맨드 전송 에러 : " + e.getMessage());
                    }
                }
            }).start();

            if (m_LayoutIndex == 3) {
                m_MotorPositionSettingNum = 0;
                setThirdLayout();
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    private void resetEditHoleImageViewScale() {
        m_EditHoleImageViewSavedMatrix = new Matrix();
        m_EditHoleImageViewOldDistance = 0;

        Drawable d = m_EditHoleImageView.getDrawable();

        if (d != null) {
            RectF imageRectF = new RectF(0, 0, (float)d.getIntrinsicWidth(), (float)d.getIntrinsicHeight());
            RectF viewRectF = new RectF(0, 0, (float)m_EditHoleImageView.getWidth(), (float)m_EditHoleImageView.getHeight());
            m_EditHoleImageViewMatrix.setRectToRect(imageRectF, viewRectF, Matrix.ScaleToFit.CENTER);

            m_EditHoleImageView.setImageMatrix(m_EditHoleImageViewMatrix);
        }
    }

    private void resetScale() {
        m_SavedMatrix = new Matrix();
        m_OldDistance = 0;

        Drawable d = m_CleaningView.getDrawable();
        RectF imageRectF = new RectF(0, 0, (float)d.getIntrinsicWidth(), (float)d.getIntrinsicHeight());
        RectF viewRectF = new RectF(0, 0, (float)m_CleaningView.getWidth(), (float)m_CleaningView.getHeight());
        m_Matrix.setRectToRect(imageRectF, viewRectF, Matrix.ScaleToFit.CENTER);

        m_CleaningView.setImageMatrix(m_Matrix);
    }

    private void resetResultViewScale() {
        m_ResultViewSavedMatrix = new Matrix();
        m_ResultViewOldDistance = 0;

        Drawable d = m_ResultView.getDrawable();
        RectF imageRectF = new RectF(0, 0, (float)d.getIntrinsicWidth(), (float)d.getIntrinsicHeight());
        RectF viewRectF = new RectF(0, 0, (float)m_ResultView.getWidth(), (float)m_ResultView.getHeight());
        m_ResultViewMatrix.setRectToRect(imageRectF, viewRectF, Matrix.ScaleToFit.CENTER);

        m_ResultView.setImageMatrix(m_ResultViewMatrix);
    }

    public void setManualLayout() {
        m_FirstLayout.post(new Runnable() {
            @Override
            public void run() {
                m_FirstLayout.setVisibility(View.GONE);
            }
        });

        m_SecondLayout.post(new Runnable() {
            @Override
            public void run() {
                m_SecondLayout.setVisibility(View.GONE);
            }
        });

        m_ThirdLayout.post(new Runnable() {
            @Override
            public void run() {
                m_ThirdLayout.setVisibility(View.GONE);
            }
        });

        m_FourthLayout.post(new Runnable() {
            @Override
            public void run() {
                m_FourthLayout.setVisibility(View.GONE);
            }
        });

        m_FifthLayout.post(new Runnable() {
            @Override
            public void run() {
                m_FifthLayout.setVisibility(View.GONE);
            }
        });

        m_SixthLayout.post(new Runnable() {
            @Override
            public void run() {
                m_SixthLayout.setVisibility(View.GONE);
            }
        });

        m_EditHoleLayout.post(new Runnable() {
            @Override
            public void run() {
                m_EditHoleLayout.setVisibility(View.GONE);
            }
        });

        m_ManualLayout.post(new Runnable() {
            @Override
            public void run() {
                m_ManualLayout.setVisibility(View.VISIBLE);
            }
        });

        this.m_LayoutIndex = 6;
        setManualButton();
    }

    private boolean manualAccelerateMode = true;

    private void setManualButton() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String sendStr = "ACCELERATE_MODE_ON,";
                    byte[] sendBytes = sendStr.getBytes(StandardCharsets.US_ASCII);
                    m_MainSocketClient.send(sendBytes);

                    int cnt = 0;

                    while (!m_ReplyAccelerateModeOnChecked) {
                        // Log.e(TAG, "가속모드 온 체크 여부" + m_ReplyAccelerateModeOnChecked);
                        Thread.sleep(10);
                        cnt++;

                        if (cnt > 100) {
                            break;
                        }

                        errorCheck("ACCELERATE_MODE_ON,");
                    }

                    m_ReplyAccelerateModeOnChecked = false;
                } catch (Exception e) {

                }
            }
        }).start();

        manualAccelerateMode = true;

        ToggleButton accelerateButton = (ToggleButton)m_ManualLayout.findViewById(R.id.accelerateButton);
        accelerateButton.setTextOn("가속 모드");
        accelerateButton.setTextOff("가속 모드");
        accelerateButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.toggle_button_on, null));
        accelerateButton.setChecked(true);
        accelerateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!manualAccelerateMode) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String sendStr = "ACCELERATE_MODE_ON,";
                                byte[] sendBytes = sendStr.getBytes(StandardCharsets.US_ASCII);
                                m_MainSocketClient.send(sendBytes);

                                int cnt = 0;

                                while (!m_ReplyAccelerateModeOnChecked) {
                                    // Log.e(TAG, "가속모드 온 체크 여부" + m_ReplyAccelerateModeOnChecked);
                                    Thread.sleep(10);
                                    cnt++;

                                    if (cnt > 100) {
                                        break;
                                    }

                                    errorCheck("ACCELERATE_MODE_ON,");
                                }

                                if (m_ReplyAccelerateModeOnChecked) {
                                    // Log.i(TAG, "가속 모드 온 체크");
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            accelerateButton.setChecked(true);
                                            accelerateButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.toggle_button_on, null));
                                            manualAccelerateMode = true;
                                        }
                                    });
                                }

                                m_ReplyAccelerateModeOnChecked = false;
                            } catch (Exception e) {
                                LogManager.e("[MainActivity] [setManualButton] 가속 모드 켜기 커맨드 전송 에러 : " + e.getMessage());
                                Log.e(TAG, "[MainActivity] [setManualButton] 가속 모드 켜기 커맨드 전송 에러 : " + e.getMessage());
                            }
                        }
                    }).start();
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String sendStr = "ACCELERATE_MODE_OFF,";
                                byte[] sendBytes = sendStr.getBytes(StandardCharsets.US_ASCII);
                                m_MainSocketClient.send(sendBytes);

                                int cnt = 0;

                                while (!m_ReplyAccelerateModeOffChecked) {
                                    // Log.e(TAG, "가속모드 오프 체크 여부" + m_ReplyAccelerateModeOffChecked);
                                    Thread.sleep(10);
                                    cnt++;

                                    if (cnt > 100) {
                                        break;
                                    }

                                    errorCheck("ACCELERATE_MODE_OFF,");
                                }

                                if (m_ReplyAccelerateModeOffChecked) {
                                    // Log.i(TAG, "가속 모드 오프 체크");
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            accelerateButton.setChecked(false);
                                            accelerateButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.toggle_button_off, null));
                                            manualAccelerateMode = false;
                                        }
                                    });
                                }

                                m_ReplyAccelerateModeOffChecked = false;
                            } catch (Exception e) {
                                LogManager.e("[MainActivity] [setManualButton] 가속 모드 끄기 커맨드 전송 에러 : " + e.getMessage());
                                Log.e(TAG, "[MainActivity] [setManualButton] 가속 모드 끄기 커맨드 전송 에러 : " + e.getMessage());
                            }
                        }
                    }).start();
                }
            }
        });

        Button homePositionSettingButton = (Button)m_ManualLayout.findViewById(R.id.homePositionSettingButton);
        homePositionSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w(TAG, "원점 위치 설정");

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String sendStr = "HOME_POSITION_SETTING,";
                            byte[] sendBytes = sendStr.getBytes(StandardCharsets.US_ASCII);
                            m_MainSocketClient.send(sendBytes);
                        } catch (Exception e) {
                            LogManager.e("[MainActivity] [setManualButton] 원점 위치 설정 커맨드 전송 에러 : " + e.getMessage());
                        }
                    }
                }).start();
            }
        });

        Button m_XHomeButton = (Button)m_ManualLayout.findViewById(R.id.xHomeButton);
        m_XHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    String sendStr = "X_HOME,";
                                    byte[] sendBytes = sendStr.getBytes(StandardCharsets.US_ASCII);
                                    m_MainSocketClient.send(sendBytes);
                                } catch (Exception e) {
                                    LogManager.e("[MainActivity] [setManualButton] X축 원점 커맨드 전송 에러 : " + e.getMessage());
                                }
                            }
                        }).start();
                    }
                }).start();
            }
        });

        Button m_YHomeButton = (Button)m_ManualLayout.findViewById(R.id.yHomeButton);
        m_YHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    String sendStr = "Y_HOME,";
                                    byte[] sendBytes = sendStr.getBytes(StandardCharsets.US_ASCII);
                                    m_MainSocketClient.send(sendBytes);
                                } catch (Exception e) {
                                    LogManager.e("[MainActivity] [setManualButton] Y축 원점 커맨드 전송 에러 : " + e.getMessage());
                                }
                            }
                        }).start();
                    }
                }).start();
            }
        });

        Button m_JogUpButton = (Button)m_ManualLayout.findViewById(R.id.jogUpButton);
        m_JogUpButton.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        sendBroadcast(new Intent("com.example.myapplication.JogUpButtonDown"));

                        // Log.e(TAG, "Activity, 인덱스 : " + broadcast_index + ", 명령 : JogUpButtonDown");
                        broadcast_index++;
                        return false;
                    case MotionEvent.ACTION_UP:
                        sendBroadcast(new Intent("com.example.myapplication.JogUpButtonUp"));
                        // Log.e(TAG, "Activity, 인덱스 : " + broadcast_index + ", 명령 : JogUpButtonUp");
                        broadcast_index++;
                        return false;
                    default:
                        return false;
                }
            }
        });

        Button m_JogLeftButton = (Button)m_ManualLayout.findViewById(R.id.jogLeftButton);
        m_JogLeftButton.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        sendBroadcast(new Intent("com.example.myapplication.JogLeftButtonDown"));
                        return false;
                    case MotionEvent.ACTION_UP:
                        sendBroadcast(new Intent("com.example.myapplication.JogLeftButtonUp"));
                        // Toast.makeText(MainActivity.this, "JogLeftButtonUp", Toast.LENGTH_SHORT).show();
                        return false;
                    default:
                        return false;
                }
            }
        });

        Button m_JogRightButton = (Button)m_ManualLayout.findViewById(R.id.jogRightButton);
        m_JogRightButton.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        sendBroadcast(new Intent("com.example.myapplication.JogRightButtonDown"));
                        return false;
                    case MotionEvent.ACTION_UP:
                        sendBroadcast(new Intent("com.example.myapplication.JogRightButtonUp"));
                        // Toast.makeText(MainActivity.this, "JogRightButtonUp", Toast.LENGTH_SHORT).show();
                        return false;
                    default:
                        return false;
                }
            }
        });

        Button m_JogDownButton = (Button)m_ManualLayout.findViewById(R.id.jogDownButton);
        m_JogDownButton.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case 0:
                        sendBroadcast(new Intent("com.example.myapplication.JogDownButtonDown"));
                        return false;
                    case 1:
                        sendBroadcast(new Intent("com.example.myapplication.JogDownButtonUp"));
                        return false;
                    default:
                        return false;
                }
            }
        });

        Button m_AllNozelForwardButton = (Button)m_ManualLayout.findViewById(R.id.allNozelForwardButton);

        m_AllNozelForwardButton.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case 0:
                        sendBroadcast(new Intent("com.example.myapplication.allNozelForwardButtonDown"));
                        return false;
                    case 1:
                        sendBroadcast(new Intent("com.example.myapplication.allNozelForwardButtonUp"));
                        return false;
                    default:
                        return false;
                }
            }
        });


        Button m_AllNozelBackwardButton = (Button)m_ManualLayout.findViewById(R.id.allNozelBackwardButton);
        m_AllNozelBackwardButton.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case 0:
                        sendBroadcast(new Intent("com.example.myapplication.allNozelBackwardButtonDown"));
                        return false;
                    case 1:
                        sendBroadcast(new Intent("com.example.myapplication.allNozelBackwardButtonUp"));
                        return false;
                    default:
                        return false;
                }
            }
        });

        Button m_LaserOnButton = (Button)m_ManualLayout.findViewById(R.id.laserOnButton);
        m_LaserOnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String sendStr = "CENTERING,1,";
                            byte[] sendBytes = sendStr.getBytes(StandardCharsets.US_ASCII);
                            m_MainSocketClient.send(sendBytes);
                        } catch (Exception e) {
                            LogManager.e("[MainActivity] [setManualLayout.m_LaserOnButton.setOnClickListener] " + e.getMessage());
                        }
                    }
                }).start();
            }
        });

        Button m_LaserOffButton = (Button)m_ManualLayout.findViewById(R.id.laserOffButton);
        m_LaserOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String sendStr = "CENTERING,0,";
                            byte[] sendBytes = sendStr.getBytes(StandardCharsets.US_ASCII);
                            m_MainSocketClient.send(sendBytes);
                        } catch (Exception e) {
                            LogManager.e("[MainActivity] [setManualLayout.m_LaserOffButton.setOnClickListener] " + e.getMessage());
                        }
                    }
                }).start();
            }
        });

        Button m_ManualConfirmButton = (Button)m_ManualLayout.findViewById(R.id.manualConfirmButton);
        m_ManualConfirmButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                m_ManualLayout.setVisibility(View.INVISIBLE);
                setFirstLayout();
            }
        });
    }
}
