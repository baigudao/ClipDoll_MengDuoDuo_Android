package com.meng.duo.clip.doll.activity;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.EmptyUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meng.duo.clip.doll.R;
import com.meng.duo.clip.doll.adapter.BaseRecyclerViewAdapter;
import com.meng.duo.clip.doll.bean.HomeRoomBean;
import com.meng.duo.clip.doll.bean.LiveRoomLuckyUserBean;
import com.meng.duo.clip.doll.bean.LiveRoomUserBean;
import com.meng.duo.clip.doll.fragment.InvitePrizeFragment;
import com.meng.duo.clip.doll.fragment.MyGameCoinFragment;
import com.meng.duo.clip.doll.util.BackgroundMusicPlayerUtil;
import com.meng.duo.clip.doll.util.Constants;
import com.meng.duo.clip.doll.util.DataManager;
import com.meng.duo.clip.doll.util.SoundPoolUtil;
import com.meng.duo.clip.doll.view.CircleImageView;
import com.meng.duo.clip.doll.view.ClipNoPopupWindow;
import com.meng.duo.clip.doll.view.ClipYesPopupWindow;
import com.meng.duo.clip.doll.view.SharePlatformPopupWindow;
import com.tencent.av.sdk.AVRoomMulti;
import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.ILiveConstants;
import com.tencent.ilivesdk.core.ILiveRoomManager;
import com.tencent.ilivesdk.view.AVRootView;
import com.tencent.ilivesdk.view.AVVideoView;
import com.tencent.ilivesdk.view.VideoListener;
import com.tencent.livesdk.ILVLiveManager;
import com.tencent.livesdk.ILVLiveRoomOption;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;

import static com.meng.duo.clip.doll.R.drawable.wawa_loading;
import static com.tencent.av.sdk.AVView.VIDEO_SRC_TYPE_CAMERA;
import static com.tencent.ilivesdk.adapter.CommonConstants.Const_Auth_Host;
import static com.tencent.ilivesdk.adapter.CommonConstants.Const_Auth_Member;


public class ClipDollDetailActivity extends BaseActivity implements View.OnClickListener, View.OnTouchListener, AVRootView.onSubViewCreatedListener, BaseRecyclerViewAdapter.OnItemClickListener {

    private View view;
    private RelativeLayout rl_start_clip_and_recharge;
    private RelativeLayout rl_operation;
    private LinearLayout ll_start_clip_doll;

    private TextView tv_cost_coin_num;
    private TextView tv_coin_num;
    private TextView tv_start_clip_doll;

    private ImageButton action_btn_left;
    private ImageButton action_btn_bottom;
    private ImageButton action_btn_top;
    private ImageButton action_btn_right;
    private ImageButton action_start_clip;

    private LinearLayout ll_live_room_player;
    private ImageView iv_live_room_player;
    private RelativeLayout rl_live_room_user;
    private CircleImageView iv_user_1;
    private CircleImageView iv_user_2;
    private CircleImageView iv_user_3;
    private CircleImageView iv_user_4;
    private TextView tv_user_num;
    private TextView tv_live_room_player_name;
    private TextView tv_waiting_game_result;
    private TextView tv_room_id;
    private ImageView iv_live_room_camera;
    private ImageView iv_background_music;

    private AVRootView arv_root;
    private boolean isFront;

    private static final int CLIP_DOLL_RECORD_LIVE_DATA_TYPE = 3;
    private RecyclerView recyclerView_lucky;
    private static final int PRODUCT_INTRODUCE_DATA_TYPE = 7;
    private RecyclerView recyclerView_introduce;

    private TextView tv_timer;

    private Timer mTimer;
    private TimerTask mTask;
    private int mTotalTime;
    private int tryAgingTotalTime;
    private Timer tryAgingYesTimer;
    private Timer tryAgingNoTimer;
    private Timer roomStateTimer;
    private Timer playerNumTimer;
    private Timer gameResultTimer;
    private ClipNoPopupWindow clipNoPopupWindow;
    private ClipYesPopupWindow clipYesPopupWindow;

    private boolean isShowGoBackDialog;
    private boolean isCloseBackgroundMusicAndSound;

    private HomeRoomBean homeRoomBean;

    private int exceptionTime;
    private Timer exceptionTimer;
    private TimerTask exceptionTimerTask;

    private IWXAPI api;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 55) {
                beforeStartGame();
            }
        }
    };
    private TimerTask roomStateTimerTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BarUtils.setStatusBarColor(ClipDollDetailActivity.this, getResources().getColor(R.color.second_background_color));
        view = View.inflate(this, R.layout.activity_clip_doll_detail, null);
        setContentView(view);

        initView();
        LogUtils.e("onCreate");
    }

    private void initView() {
        //屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //一屏显示
        int height = ScreenUtils.getScreenHeight() - 50;
        RelativeLayout rl_fill_screen = (RelativeLayout) findViewById(R.id.rl_fill_screen);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) rl_fill_screen.getLayoutParams();
        layoutParams.height = height;
        rl_fill_screen.setLayoutParams(layoutParams);
        //设置直播视图7/10显示
        RelativeLayout rl_live_view = (RelativeLayout) findViewById(R.id.rl_live_view);
        RelativeLayout.LayoutParams layoutParams_live = (RelativeLayout.LayoutParams) rl_live_view.getLayoutParams();
        layoutParams_live.height = (height * 7) / 10;
        rl_live_view.setLayoutParams(layoutParams_live);
        //开始和充值按钮距离上面
        rl_start_clip_and_recharge = (RelativeLayout) findViewById(R.id.rl_start_clip_and_recharge);
        RelativeLayout.LayoutParams layoutParams_start_clip = (RelativeLayout.LayoutParams) rl_start_clip_and_recharge.getLayoutParams();
        layoutParams_start_clip.topMargin = (height * 3 / 10 - SizeUtils.dp2px(100)) / 2;
        rl_start_clip_and_recharge.setLayoutParams(layoutParams_start_clip);

        //头部视图
        findViewById(R.id.ll_close).setOnClickListener(this);
        findViewById(R.id.iv_share).setOnClickListener(this);

        //主要视图
        arv_root = (AVRootView) findViewById(R.id.arv_root);
        ILVLiveManager.getInstance().setAvVideoView(arv_root);
        arv_root.setBackground(R.drawable.wawa_loading);
        arv_root.setAutoOrientation(false);
        isFront = true;
        //有画面之后的回调
        arv_root.setSubCreatedListener(this);

        rl_operation = (RelativeLayout) findViewById(R.id.rl_operation);
        tv_cost_coin_num = (TextView) findViewById(R.id.tv_cost_coin_num);
        tv_coin_num = (TextView) findViewById(R.id.tv_coin_num);
        tv_start_clip_doll = (TextView) findViewById(R.id.tv_start_clip_doll);
        ll_start_clip_doll = (LinearLayout) findViewById(R.id.ll_start_clip_doll);
        ll_start_clip_doll.setOnClickListener(this);
        findViewById(R.id.ll_coin_recharge).setOnClickListener(this);
        tv_timer = (TextView) findViewById(R.id.tv_timer);
        tv_room_id = (TextView) findViewById(R.id.tv_room_id);
        iv_live_room_camera = (ImageView) findViewById(R.id.iv_live_room_camera);
        iv_live_room_camera.setOnClickListener(this);
        iv_background_music = (ImageView) findViewById(R.id.iv_background_music);
        iv_background_music.setOnClickListener(this);
        if (SPUtils.getInstance().getBoolean(Constants.IS_PLAY_BACKGROUND_MUSIC) && SPUtils.getInstance().getBoolean(Constants.IS_PLAY_BACKGROUND_SOUND)) {
            iv_background_music.setImageResource(R.drawable.background_music_close);
            isCloseBackgroundMusicAndSound = true;
        } else {
            iv_background_music.setImageResource(R.drawable.background_music_open);
            isCloseBackgroundMusicAndSound = false;
        }
        ll_live_room_player = (LinearLayout) findViewById(R.id.ll_live_room_player);
        iv_live_room_player = (ImageView) findViewById(R.id.iv_live_room_player);
        tv_live_room_player_name = (TextView) findViewById(R.id.tv_live_room_player_name);
        rl_live_room_user = (RelativeLayout) findViewById(R.id.rl_live_room_user);
        iv_user_1 = (CircleImageView) findViewById(R.id.iv_user_1);
        iv_user_2 = (CircleImageView) findViewById(R.id.iv_user_2);
        iv_user_3 = (CircleImageView) findViewById(R.id.iv_user_3);
        iv_user_4 = (CircleImageView) findViewById(R.id.iv_user_4);
        tv_user_num = (TextView) findViewById(R.id.tv_user_num);
        tv_waiting_game_result = (TextView) findViewById(R.id.tv_waiting_game_result);

        //上下左右下抓操作按钮视图
        action_btn_left = (ImageButton) findViewById(R.id.action_btn_left);
        action_btn_left.setOnTouchListener(this);
        action_btn_bottom = (ImageButton) findViewById(R.id.action_btn_bottom);
        action_btn_bottom.setOnTouchListener(this);
        action_btn_top = (ImageButton) findViewById(R.id.action_btn_top);
        action_btn_top.setOnTouchListener(this);
        action_btn_right = (ImageButton) findViewById(R.id.action_btn_right);
        action_btn_right.setOnTouchListener(this);
        action_start_clip = (ImageButton) findViewById(R.id.action_start_clip);
        action_start_clip.setOnClickListener(this);

        //底部视图的初始化
        recyclerView_lucky = (RecyclerView) findViewById(R.id.recyclerView_lucky);
        recyclerView_introduce = (RecyclerView) findViewById(R.id.recyclerView_introduce);

        //得到主页面传过来的数据
        homeRoomBean = (HomeRoomBean) DataManager.getInstance().getData1();
        DataManager.getInstance().setData1(null);

        //注册微信
        regToWx();

        //加入房间
        joinRoom();

        //默认显示
        beforeStartGame();

        //控制媒体音量
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }


    @Override
    protected void onStart() {
        super.onStart();
        LogUtils.e("onStart");

        //房间ID
        tv_room_id.setText("房间ID:" + homeRoomBean.getRoomId());

        //得到余额
        getBalanceCoin();
        //得到幸运儿
        getLuckyUsers();

        //渲染宝贝图片
        if (EmptyUtils.isNotEmpty(homeRoomBean)) {
            ArrayList<String> stringArrayList = (ArrayList<String>) homeRoomBean.getProduct().getDetailPics();
            if (stringArrayList.size() != 0) {
                BaseRecyclerViewAdapter baseRecyclerViewAdapter = new BaseRecyclerViewAdapter(ClipDollDetailActivity.this, stringArrayList, PRODUCT_INTRODUCE_DATA_TYPE);
                recyclerView_introduce.setAdapter(baseRecyclerViewAdapter);
                recyclerView_introduce.setLayoutManager(new LinearLayoutManager(ClipDollDetailActivity.this, LinearLayoutManager.VERTICAL, false));
            }
        }
    }

    private void addUserNumForLiveRoom() {
        OkHttpUtils.get()
                .url(Constants.getAddUserForLiveRoomUrl())
                .addParams(Constants.SESSION, SPUtils.getInstance().getString(Constants.SESSION))
                .addParams(Constants.GROUPID, homeRoomBean.getGroupId())
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtils.e(e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            JSONObject jsonObjectResHead = jsonObject.optJSONObject("resHead");
                            int code = jsonObjectResHead.optInt("code");
                            String msg = jsonObjectResHead.optString("msg");
                            String req = jsonObjectResHead.optString("req");
                            JSONObject jsonObjectResBody = jsonObject.optJSONObject("resBody");
                            if (code == 1) {
                                playerNumTimer = new Timer();
                                playerNumTimer.schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        //获取直播间观众数和玩家 计数
                                        getLiveRoomPlayerNum();
                                    }
                                }, 0, 3000);
                                int success = jsonObjectResBody.optInt("success");
                                if (success != 1) {
                                    ToastUtils.showShort("您的图像显示失败");
                                }
                            } else {
                                LogUtils.e("请求数据失败：" + msg + "-" + code + "-" + req);
                                ToastUtils.showShort("请求数据失败:" + msg);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void getBalanceCoin() {
        OkHttpUtils.post()
                .url(Constants.getUserBalance())
                .addParams(Constants.SESSION, SPUtils.getInstance().getString(Constants.SESSION))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtils.e(e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            JSONObject jsonObjectResHead = jsonObject.optJSONObject("resHead");
                            int code = jsonObjectResHead.optInt("code");
                            String msg = jsonObjectResHead.optString("msg");
                            String req = jsonObjectResHead.optString("req");
                            JSONObject jsonObjectResBody = jsonObject.optJSONObject("resBody");
                            if (code == 1) {
                                int balance = jsonObjectResBody.optInt("balance");
                                tv_coin_num.setText(String.valueOf(balance));
                            } else {
                                LogUtils.e("请求数据失败：" + msg + "-" + code + "-" + req);
                                ToastUtils.showShort("请求数据失败:" + msg);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void getLuckyUsers() {
        //获取抓取记录，幸运儿
        OkHttpUtils.post()
                .url(Constants.getClipDollRecordUrl())
                .addParams(Constants.GROUPID, homeRoomBean.getGroupId())
                .addParams(Constants.PAGENUM, "1")
                .addParams(Constants.PAGESIZE, "10")
                .addParams(Constants.RESULT, "1")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtils.e(e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            JSONObject jsonObjectResHead = jsonObject.optJSONObject("resHead");
                            int code = jsonObjectResHead.optInt("code");
                            String msg = jsonObjectResHead.optString("msg");
                            String req = jsonObjectResHead.optString("req");
                            JSONObject jsonObjectResBody = jsonObject.optJSONObject("resBody");
                            if (code == 1) {
                                handlerDataForLuckyUsers(jsonObjectResBody);
                            } else {
                                LogUtils.e("请求数据失败：" + msg + "-" + code + "-" + req);
                                ToastUtils.showShort("请求数据失败:" + msg);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void handlerDataForLuckyUsers(JSONObject jsonObjectResBody) {
        if (EmptyUtils.isNotEmpty(jsonObjectResBody)) {
            JSONArray jsonArrayForLuckyUsers = jsonObjectResBody.optJSONArray("pageData");
            if (EmptyUtils.isNotEmpty(jsonArrayForLuckyUsers)) {
                Gson gson = new Gson();
                ArrayList<LiveRoomLuckyUserBean> liveRoomLuckyUserBeanArrayList = gson.fromJson(jsonArrayForLuckyUsers.toString()
                        , new TypeToken<ArrayList<LiveRoomLuckyUserBean>>() {
                        }.getType());
                if (EmptyUtils.isNotEmpty(liveRoomLuckyUserBeanArrayList) && liveRoomLuckyUserBeanArrayList.size() != 0) {
                    BaseRecyclerViewAdapter baseRecyclerViewAdapter = new BaseRecyclerViewAdapter(ClipDollDetailActivity.this, liveRoomLuckyUserBeanArrayList, CLIP_DOLL_RECORD_LIVE_DATA_TYPE);
                    recyclerView_lucky.setAdapter(baseRecyclerViewAdapter);
                    recyclerView_lucky.setLayoutManager(new LinearLayoutManager(ClipDollDetailActivity.this, LinearLayoutManager.VERTICAL, false));
                    baseRecyclerViewAdapter.setOnItemClickListener(this);
                }
            }
        }
    }

    private void joinRoom() {
        //加入房间配置项
        ILVLiveRoomOption memberOption = new ILVLiveRoomOption("")
                .autoCamera(false) //是否自动打开摄像头
                .controlRole("Guest") //角色设置 LiveGuest
                .authBits(AVRoomMulti.AUTH_BITS_JOIN_ROOM | AVRoomMulti.AUTH_BITS_RECV_AUDIO |
                        AVRoomMulti.AUTH_BITS_RECV_CAMERA_VIDEO | AVRoomMulti.AUTH_BITS_RECV_SCREEN_VIDEO) //权限设置
                .videoRecvMode(AVRoomMulti.VIDEO_RECV_MODE_SEMI_AUTO_RECV_CAMERA_VIDEO) //是否开始半自动接收
                .autoMic(false);//是否自动打开mic

        //加入房间
        ILVLiveManager.getInstance().joinRoom(Integer.parseInt(homeRoomBean.getGroupId()), memberOption, new ILiveCallBack() {//10054
            @Override
            public void onSuccess(Object data) {
                //加入房间成功
                ILiveRoomManager.getInstance().enableSpeaker(false);
                ILiveRoomManager.getInstance().enableMic(false);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                //加入房间失败
                LogUtils.e("加入房间失败：" + "module=" + module + ",errMsg=" + errMsg + ",errCode=" + errCode);
                if (errCode == 1003) {

                } else {
                    //加入房间失败，弹出对话框
                    showJoinRoomFailDialog();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_close:
                if (isShowGoBackDialog) {
                    showGoBackDialog();
                } else {
                    goBack();
                }
                break;
            case R.id.iv_live_room_camera:
                //这里切换摄像头
                if (isFront) {
                    arv_root.swapVideoView(0, 1);// 与大屏交换
                    isFront = false;
                } else {
                    arv_root.swapVideoView(1, 0);// 与大屏交换
                    isFront = true;
                }
                break;
            case R.id.ll_start_clip_doll:
                //请求开始游戏接口
                requestBeginGame();
                break;
            case R.id.ll_coin_recharge:
                gotoPager(MyGameCoinFragment.class, null);
                break;
            case R.id.action_start_clip:
                //下抓
                startGameAfter();
                break;
            case R.id.iv_background_music:
                //如果全闭
                if (isCloseBackgroundMusicAndSound) {
                    iv_background_music.setImageResource(R.drawable.background_music_open);
                    isCloseBackgroundMusicAndSound = false;
                    SPUtils.getInstance().put(Constants.IS_PLAY_BACKGROUND_MUSIC, false);
                    SPUtils.getInstance().put(Constants.IS_PLAY_BACKGROUND_SOUND, false);
                    BackgroundMusicPlayerUtil.getInstance(getApplicationContext()).playMusic();
                } else {
                    iv_background_music.setImageResource(R.drawable.background_music_close);
                    isCloseBackgroundMusicAndSound = true;
                    SPUtils.getInstance().put(Constants.IS_PLAY_BACKGROUND_MUSIC, true);
                    SPUtils.getInstance().put(Constants.IS_PLAY_BACKGROUND_SOUND, true);
                    BackgroundMusicPlayerUtil.getInstance(getApplicationContext()).stopMusic();
                }
                break;
            case R.id.iv_share:
                showSharePlatformPopWindow();
                break;
            default:
                break;
        }
    }

    //==============================================固定============================================

    /**
     * 开始游戏前
     */
    private void beforeStartGame() {
        //隐藏等待游戏结果的提示文字
        tv_waiting_game_result.setVisibility(View.GONE);
        //显示开始抓取和充值的入口
        rl_start_clip_and_recharge.setVisibility(View.VISIBLE);
        rl_operation.setVisibility(View.GONE);
        ll_start_clip_doll.setEnabled(true);
        tv_start_clip_doll.setTextColor(getResources().getColor(R.color.seventh_text_color));
        tv_cost_coin_num.setTextColor(getResources().getColor(R.color.seventh_text_color));
        tv_cost_coin_num.setText(String.valueOf(homeRoomBean.getGamePrice()) + "币/次");
        //是否弹出退出房间的对话框
        isShowGoBackDialog = false;
        //开始轮询房间状态
        roomStateTimer = new Timer();
        roomStateTimerTask = new TimerTask() {
            @Override
            public void run() {
                OkHttpUtils.get()
                        .url(Constants.getRoomStateUrl())
                        .addParams(Constants.SESSION, SPUtils.getInstance().getString(Constants.SESSION))
                        .addParams(Constants.ROOMID, String.valueOf(homeRoomBean.getRoomId()))
                        .build()
                        .execute(new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                LogUtils.e(e.toString());
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                LogUtils.e(response);
                                JSONObject jsonObject = null;
                                try {
                                    jsonObject = new JSONObject(response);
                                    JSONObject jsonObjectResHead = jsonObject.optJSONObject("resHead");
                                    int code = jsonObjectResHead.optInt("code");
                                    String msg = jsonObjectResHead.optString("msg");
                                    String req = jsonObjectResHead.optString("req");
                                    JSONObject jsonObjectResBody = jsonObject.optJSONObject("resBody");
                                    if (code == 1) {
                                        int success = jsonObjectResBody.optInt("success");
                                        if (success == 1) {
                                            int roomState = jsonObjectResBody.optInt("roomState");
                                            switch (roomState) {
                                                case 0:
                                                    //空闲中：显示开始按钮并激活
                                                    ll_start_clip_doll.setEnabled(true);
                                                    tv_start_clip_doll.setTextColor(getResources().getColor(R.color.seventh_text_color));
                                                    tv_cost_coin_num.setTextColor(getResources().getColor(R.color.seventh_text_color));
                                                    break;
                                                case 1:
                                                    //游戏中
                                                    ll_start_clip_doll.setEnabled(false);
                                                    tv_start_clip_doll.setTextColor(getResources().getColor(R.color.pure_white_color));
                                                    tv_cost_coin_num.setTextColor(getResources().getColor(R.color.pure_white_color));
                                                    break;
                                                default:
                                                    break;
                                            }
                                        }
                                    } else {
                                        LogUtils.e("请求数据失败：" + msg + "-" + code + "-" + req);
                                        ToastUtils.showShort("请求数据失败:" + msg);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
            }
        };
        roomStateTimer.schedule(roomStateTimerTask, 0, 1500);
    }

    private void requestBeginGame() {
        tryAgingTotalTime = 10;
        OkHttpUtils.post()
                .url(Constants.getApplyBeginGameUrl())
                .addParams(Constants.SESSION, SPUtils.getInstance().getString(Constants.SESSION))
                .addParams(Constants.GROUPID, homeRoomBean.getGroupId())
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtils.e(e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            JSONObject jsonObjectResHead = jsonObject.optJSONObject("resHead");
                            int code = jsonObjectResHead.optInt("code");
                            String msg = jsonObjectResHead.optString("msg");
                            String req = jsonObjectResHead.optString("req");
                            JSONObject jsonObjectResBody = jsonObject.optJSONObject("resBody");
                            if (code == 1) {
                                int success = jsonObjectResBody.optInt("success");
                                switch (success) {
                                    case 0:
                                        String alertMsg = jsonObjectResBody.optString("alertMsg");
                                        ToastUtils.showShort(alertMsg);
                                        break;
                                    case 1:
                                        //扣币并实时显示
                                        tv_coin_num.setText(String.valueOf(Integer.valueOf(tv_coin_num.getText().toString()) - homeRoomBean.getGamePrice()));

                                        isShowGoBackDialog = true;
                                        //切换角色
                                        ILiveRoomManager.getInstance().changeAuthority(Const_Auth_Host, new ILiveCallBack() {
                                            @Override
                                            public void onSuccess(Object data) {
                                                LogUtils.e("切换角色成功");
                                            }

                                            @Override
                                            public void onError(String module, int errCode, String errMsg) {
                                                LogUtils.e("切换角色错误：" + "module=" + module + "，errCode=" + errCode + "，errMsg=" + errMsg);
                                            }
                                        });
                                        JSONObject jsonObjectResData = jsonObjectResBody.optJSONObject("resData");
                                        String gameId = jsonObjectResData.optString("gameId");
                                        SPUtils.getInstance().put("gameId", gameId);
                                        startGaming();
                                        break;
                                    default:
                                        break;
                                }
                            } else {
                                LogUtils.e("请求数据失败：" + msg + "-" + code + "-" + req);
                                ToastUtils.showShort("" + msg);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 游戏中
     */
    private void startGaming() {
        //播放ready_go音效
        if (!SPUtils.getInstance().getBoolean(Constants.IS_PLAY_BACKGROUND_SOUND)) {
            SoundPoolUtil.getInstance(getApplicationContext()).play(0);
        }
        //显示操作按钮，隐藏开始抓取和充值按钮
        rl_start_clip_and_recharge.setVisibility(View.GONE);
        rl_operation.setVisibility(View.VISIBLE);
        action_btn_left.setEnabled(true);
        action_btn_bottom.setEnabled(true);
        action_btn_top.setEnabled(true);
        action_btn_right.setEnabled(true);
        action_start_clip.setEnabled(true);

        //显示倒计时
        mTotalTime = 30;
        mTimer = new Timer();
        initTimerTask();
        mTimer.schedule(mTask, 0, 1000);

        tv_timer.setVisibility(View.VISIBLE);
    }

    /**
     * 游戏后
     */
    private void startGameAfter() {
        //开启异常轮询
        //异常轮询器初始化
        exceptionTime = 25;
        exceptionTimer = new Timer();
        exceptionTimerTask = new TimerTask() {
            @Override
            public void run() {
                --exceptionTime;
                if (exceptionTime == 0) {
                    Message message = new Message();
                    message.what = 55;
                    handler.sendMessage(message);
                    exceptionTimer.cancel();
                    exceptionTimerTask.cancel();
                }
            }
        };
        exceptionTimer.schedule(exceptionTimerTask, 0, 1000);
        //显示等待结果的文字
        tv_waiting_game_result.setVisibility(View.VISIBLE);
        //下抓的音效
        if (!SPUtils.getInstance().getBoolean(Constants.IS_PLAY_BACKGROUND_SOUND)) {
            SoundPoolUtil.getInstance(getApplicationContext()).play(3);
        }
        //调取服务器的下抓接口
        handlerWaWa("catch");
        //隐藏倒计时，取消倒计时
        mTimer.cancel();
        tv_timer.setVisibility(View.GONE);
        //不激活操作按钮
        action_start_clip.setEnabled(false);
        action_btn_left.setEnabled(false);
        action_btn_top.setEnabled(false);
        action_btn_right.setEnabled(false);
        action_btn_bottom.setEnabled(false);
        //定时器，开始轮询结果
        gameResultTimer = new Timer();
        gameResultTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                ClipDollDetailActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        OkHttpUtils.post()
                                .url(Constants.getGameResultUrl())
                                .addParams(Constants.SESSION, SPUtils.getInstance().getString(Constants.SESSION))
                                .addParams("gameId", SPUtils.getInstance().getString("gameId"))
                                .build()
                                .execute(new StringCallback() {
                                    @Override
                                    public void onError(Call call, Exception e, int id) {
                                        LogUtils.e(e.toString());
                                    }

                                    @Override
                                    public void onResponse(String response, int id) {
                                        JSONObject jsonObject = null;
                                        try {
                                            jsonObject = new JSONObject(response);
                                            JSONObject jsonObjectResHead = jsonObject.optJSONObject("resHead");
                                            int code = jsonObjectResHead.optInt("code");
                                            String msg = jsonObjectResHead.optString("msg");
                                            String req = jsonObjectResHead.optString("req");
                                            JSONObject jsonObjectResBody = jsonObject.optJSONObject("resBody");
                                            if (code == 1) {
                                                int success = jsonObjectResBody.optInt("success");
                                                switch (success) {
                                                    case 0:
                                                        String alertMsg = jsonObjectResBody.optString("alertMsg");
                                                        ToastUtils.showShort("查询结果失败");
                                                        break;
                                                    case 1:
                                                        JSONObject jsonObjectResData = jsonObjectResBody.optJSONObject("resData");
                                                        int result = jsonObjectResData.optInt("result");
                                                        switch (result) {
                                                            case -1:

                                                                break;
                                                            case 0:
                                                                //没抓中
                                                                gameResultTimer.cancel();
                                                                gameResultTimer = null;
                                                                if (!SPUtils.getInstance().getBoolean(Constants.IS_PLAY_BACKGROUND_SOUND)) {
                                                                    SoundPoolUtil.getInstance(getApplicationContext()).play(5);
                                                                }
                                                                showResultDialog(false);
                                                                beforeStartGame();
                                                                //隐藏等待结果的文字
                                                                tv_waiting_game_result.setVisibility(View.GONE);
                                                                break;
                                                            case 1:
                                                                //抓中
                                                                gameResultTimer.cancel();
                                                                gameResultTimer = null;
                                                                if (!SPUtils.getInstance().getBoolean(Constants.IS_PLAY_BACKGROUND_SOUND)) {
                                                                    SoundPoolUtil.getInstance(getApplicationContext()).play(4);
                                                                }
                                                                showResultDialog(true);
                                                                beforeStartGame();
                                                                //隐藏等待结果的文字
                                                                tv_waiting_game_result.setVisibility(View.GONE);
                                                                break;
                                                            default:
                                                                break;
                                                        }
                                                        break;
                                                    default:
                                                        break;
                                                }
                                            } else {
                                                LogUtils.e("请求数据失败：" + msg + "-" + code + "-" + req);
                                                ToastUtils.showShort("请求数据失败:" + msg);
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                    }
                });
            }
        }, 0, 1000);
    }

    private void showResultDialog(boolean isClip) {
        beforeStartGame();
        if (isClip) {
            clipYesPopupWindow = new ClipYesPopupWindow(this, new ClipYesPopupWindow.ClipYesPopupNumListener() {
                @Override
                public void onCancelClicked() {
                    tryAgingYesTimer.cancel();
                    gameOver();
                    //                    beforeStartGame();
                }

                @Override
                public void onGoToInviteClicked() {
                    tryAgingYesTimer.cancel();
                    gameOver();
                    //                    beforeStartGame();
                    gotoPager(InvitePrizeFragment.class, null);
                }

                @Override
                public void onTryAgingClicked() {
                    tryAgingYesTimer.cancel();
                    exceptionTimer.cancel();
                    exceptionTimerTask.cancel();
                    beforeStartGame();
                    requestBeginGame();
                }
            });
            clipYesPopupWindow.initView();
            clipYesPopupWindow.showAtLocation(view, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            clipYesPopupWindow.setOutsideTouchable(false);

            final Button btn_try_aging = (Button) clipYesPopupWindow.getContentView().findViewById(R.id.btn_try_aging);
            tryAgingYesTimer = new Timer();
            tryAgingYesTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    ClipDollDetailActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            --tryAgingTotalTime;
                            btn_try_aging.setText("再来一局（" + tryAgingTotalTime + "）");
                            if (tryAgingTotalTime == 0) {
                                clipYesPopupWindow.dismiss();
                                tryAgingYesTimer.cancel();
                                //                                beforeStartGame();
                                gameOver();
                            }
                            if (tryAgingTotalTime < 0) {
                                tryAgingYesTimer = null;
                                return;
                            }
                        }
                    });
                }
            }, 0, 1000);
        } else {
            clipNoPopupWindow = new ClipNoPopupWindow(this, new ClipNoPopupWindow.ClipNoListener() {
                @Override
                public void onCancelClicked() {
                    tryAgingNoTimer.cancel();
                    gameOver();
                    //                    beforeStartGame();
                }

                @Override
                public void onGoToInviteClicked() {
                    tryAgingNoTimer.cancel();
                    gameOver();
                    //                    beforeStartGame();
                    gotoPager(InvitePrizeFragment.class, null);
                }

                @Override
                public void onTryAgingClicked() {
                    tryAgingNoTimer.cancel();
                    exceptionTimer.cancel();
                    exceptionTimerTask.cancel();
                    beforeStartGame();
                    requestBeginGame();
                }
            });
            clipNoPopupWindow.initView();
            clipNoPopupWindow.showAtLocation(view, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            clipNoPopupWindow.setOutsideTouchable(false);

            final Button btn_try_aging = (Button) clipNoPopupWindow.getContentView().findViewById(R.id.btn_try_aging);
            tryAgingNoTimer = new Timer();
            tryAgingNoTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    ClipDollDetailActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            --tryAgingTotalTime;
                            btn_try_aging.setText("再来一局（" + tryAgingTotalTime + "）");
                            if (tryAgingTotalTime == 0) {
                                clipNoPopupWindow.dismiss();
                                tryAgingNoTimer.cancel();
                                //                                beforeStartGame();
                                gameOver();
                            }
                            if (tryAgingTotalTime < 0) {
                                tryAgingNoTimer = null;
                                return;
                            }
                        }
                    });
                }
            }, 0, 1000);
        }
    }

    @Override
    public void onSubViewCreated() {
        if (arv_root.getViewByIndex(0) != null) { //主摄像头画面
            arv_root.getViewByIndex(0).setBackground(wawa_loading);
            arv_root.getViewByIndex(0).setRotate(false);
            arv_root.getViewByIndex(0).setRotation(90);
        }
        if (arv_root.getViewByIndex(1) != null) { //副摄像头画面
            arv_root.getViewByIndex(1).setBackground(wawa_loading);
            arv_root.getViewByIndex(1).setPosHeight(0);
            arv_root.getViewByIndex(1).setPosWidth(0);
            arv_root.getViewByIndex(1).setRotate(false);
            arv_root.getViewByIndex(1).setPosTop(400);
            arv_root.getViewByIndex(1).setRotation(270);
        }

        //统计:主摄像头
        arv_root.getViewByIndex(0).setVideoListener(new VideoListener() {
            @Override
            public void onFirstFrameRecved(int width, int height, int angle, String identifier) {
                LogUtils.e("主摄像头：onFirstFrameRecved。" + " 详情： " + "width=" + width + ",height=" + height + ",angle=" + angle + ",identifier=" + identifier);
            }

            @Override
            public void onHasVideo(String identifier, int srcType) {
                LogUtils.e("主摄像头：onHasVideo。" + " 详情： " + "srcType=" + srcType + ",identifier=" + identifier);
                //定义front结尾的为主摄像头，side结尾的为副摄像头，如果发现不对就和大画面交换。
                boolean isHasFront = identifier.contains("front");
                if (isHasFront) {
                    isFront = true;
                    arv_root.bindIdAndView(0, VIDEO_SRC_TYPE_CAMERA, identifier);//绑定主摄像头
                } else {
                    isFront = false;
                    arv_root.bindIdAndView(1, VIDEO_SRC_TYPE_CAMERA, identifier);//绑定副摄像头
                }
            }

            @Override
            public void onNoVideo(String identifier, int srcType) {
                LogUtils.e("主摄像头：onNoVideo。" + " 详情： " + "srcType=" + srcType + ",identifier=" + identifier);
            }
        });

        //统计:副摄像头
        arv_root.getViewByIndex(1).setVideoListener(new VideoListener() {
            @Override
            public void onFirstFrameRecved(int width, int height, int angle, String identifier) {
                LogUtils.e("副摄像头：onFirstFrameRecved。" + " 详情： " + "width=" + width + ",height=" + height + ",angle=" + angle + ",identifier=" + identifier);
            }

            @Override
            public void onHasVideo(String identifier, int srcType) {
                LogUtils.e("副摄像头：onHasVideo。" + " 详情： " + "srcType=" + srcType + ",identifier=" + identifier);
                iv_live_room_camera.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNoVideo(String identifier, int srcType) {
                LogUtils.e("副摄像头：onNoVideo。" + " 详情： " + "srcType=" + srcType + ",identifier=" + identifier);
            }
        });

        for (int i = 2; i < ILiveConstants.MAX_AV_VIDEO_NUM; i++) {
            AVVideoView subview = arv_root.getViewByIndex(i);
            //不应该设置不可见，应该设置大小为零
            subview.setPosHeight(0);
            subview.setPosWidth(0);
        }
    }

    /**
     * 可见，可操作
     */
    @Override
    protected void onResume() {
        super.onResume();
        LogUtils.e("onResume");
        //用户加入直播间 计数
        addUserNumForLiveRoom();
        ILVLiveManager.getInstance().onResume();
        if (!SPUtils.getInstance().getBoolean(Constants.IS_PLAY_BACKGROUND_MUSIC)) {
            BackgroundMusicPlayerUtil.getInstance(getApplicationContext()).playMusic();
        }
    }

    private void getLiveRoomPlayerNum() {
        OkHttpUtils.get()
                .url(Constants.getLiveRoomUserUrl())
                .addParams(Constants.SESSION, SPUtils.getInstance().getString(Constants.SESSION))
                .addParams(Constants.GROUPID, homeRoomBean.getGroupId())
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtils.e(e.toString());
                    }

                    @Override
                    public void onResponse(final String response, int id) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!ClipDollDetailActivity.this.isFinishing()) {
                                    JSONObject jsonObject = null;
                                    try {
                                        jsonObject = new JSONObject(response);
                                        JSONObject jsonObjectResHead = jsonObject.optJSONObject("resHead");
                                        int code = jsonObjectResHead.optInt("code");
                                        String msg = jsonObjectResHead.optString("msg");
                                        String req = jsonObjectResHead.optString("req");
                                        JSONObject jsonObjectResBody = jsonObject.optJSONObject("resBody");
                                        if (code == 1) {
                                            int success = jsonObjectResBody.optInt("success");
                                            if (success == 1) {
                                                JSONObject jsonObjectReq = jsonObjectResBody.optJSONObject("req");
                                                if (EmptyUtils.isNotEmpty(jsonObjectReq)) {
                                                    JSONObject jsonObjectUser = jsonObjectReq.optJSONObject("user");
                                                    if (EmptyUtils.isNotEmpty(jsonObjectUser)) {
                                                        ll_live_room_player.setVisibility(View.VISIBLE);
                                                        String headImg = jsonObjectUser.optString("headImg");
                                                        Glide.with(ClipDollDetailActivity.this)
                                                                .load(headImg)
                                                                .placeholder(R.drawable.wawa_default_user)
                                                                .error(R.drawable.wawa_default_user)
                                                                .into(iv_live_room_player);
                                                        String nickName = jsonObjectUser.optString("nickName");
                                                        tv_live_room_player_name.setText(nickName);
                                                    } else {
                                                        ll_live_room_player.setVisibility(View.GONE);
                                                    }
                                                    JSONArray jsonArrayUserList = jsonObjectReq.optJSONArray("userList");
                                                    if (EmptyUtils.isNotEmpty(jsonArrayUserList)) {
                                                        Gson gson = new Gson();
                                                        ArrayList<LiveRoomUserBean> liveRoomUserBeanArrayList = gson.fromJson(jsonArrayUserList.toString(), new TypeToken<ArrayList<LiveRoomUserBean>>() {
                                                        }.getType());
                                                        if (liveRoomUserBeanArrayList.size() != 0) {
                                                            rl_live_room_user.setVisibility(View.VISIBLE);
                                                            int userNum = jsonObjectReq.optInt("userNum");
                                                            tv_user_num.setText(userNum + "人在线");
                                                            int user_size = liveRoomUserBeanArrayList.size();
                                                            switch (user_size) {
                                                                case 1:
                                                                    iv_user_1.setVisibility(View.VISIBLE);
                                                                    Glide.with(ClipDollDetailActivity.this)
                                                                            .load(liveRoomUserBeanArrayList.get(0).getHeadImg())
                                                                            .placeholder(R.drawable.avatar)
                                                                            .error(R.drawable.avatar)
                                                                            .into(iv_user_1);
                                                                    iv_user_2.setVisibility(View.GONE);
                                                                    iv_user_3.setVisibility(View.GONE);
                                                                    iv_user_4.setVisibility(View.GONE);
                                                                    break;
                                                                case 2:
                                                                    iv_user_1.setVisibility(View.VISIBLE);
                                                                    Glide.with(ClipDollDetailActivity.this)
                                                                            .load(liveRoomUserBeanArrayList.get(0).getHeadImg())
                                                                            .placeholder(R.drawable.avatar)
                                                                            .error(R.drawable.avatar)
                                                                            .into(iv_user_1);
                                                                    iv_user_2.setVisibility(View.VISIBLE);
                                                                    Glide.with(ClipDollDetailActivity.this)
                                                                            .load(liveRoomUserBeanArrayList.get(1).getHeadImg())
                                                                            .placeholder(R.drawable.avatar)
                                                                            .error(R.drawable.avatar)
                                                                            .into(iv_user_2);
                                                                    iv_user_3.setVisibility(View.GONE);
                                                                    iv_user_4.setVisibility(View.GONE);
                                                                    break;
                                                                case 3:
                                                                    iv_user_1.setVisibility(View.VISIBLE);
                                                                    Glide.with(ClipDollDetailActivity.this)
                                                                            .load(liveRoomUserBeanArrayList.get(0).getHeadImg())
                                                                            .placeholder(R.drawable.avatar)
                                                                            .error(R.drawable.avatar)
                                                                            .into(iv_user_1);
                                                                    iv_user_2.setVisibility(View.VISIBLE);
                                                                    Glide.with(ClipDollDetailActivity.this)
                                                                            .load(liveRoomUserBeanArrayList.get(1).getHeadImg())
                                                                            .placeholder(R.drawable.avatar)
                                                                            .error(R.drawable.avatar)
                                                                            .into(iv_user_2);
                                                                    iv_user_3.setVisibility(View.VISIBLE);
                                                                    Glide.with(ClipDollDetailActivity.this)
                                                                            .load(liveRoomUserBeanArrayList.get(2).getHeadImg())
                                                                            .placeholder(R.drawable.avatar)
                                                                            .error(R.drawable.avatar)
                                                                            .into(iv_user_3);
                                                                    iv_user_4.setVisibility(View.GONE);
                                                                    break;
                                                                case 4:
                                                                    iv_user_1.setVisibility(View.VISIBLE);
                                                                    Glide.with(ClipDollDetailActivity.this)
                                                                            .load(liveRoomUserBeanArrayList.get(0).getHeadImg())
                                                                            .placeholder(R.drawable.avatar)
                                                                            .error(R.drawable.avatar)
                                                                            .into(iv_user_1);
                                                                    iv_user_2.setVisibility(View.VISIBLE);
                                                                    Glide.with(ClipDollDetailActivity.this)
                                                                            .load(liveRoomUserBeanArrayList.get(1).getHeadImg())
                                                                            .placeholder(R.drawable.avatar)
                                                                            .error(R.drawable.avatar)
                                                                            .into(iv_user_2);
                                                                    iv_user_3.setVisibility(View.VISIBLE);
                                                                    Glide.with(ClipDollDetailActivity.this)
                                                                            .load(liveRoomUserBeanArrayList.get(2).getHeadImg())
                                                                            .placeholder(R.drawable.avatar)
                                                                            .error(R.drawable.avatar)
                                                                            .into(iv_user_3);
                                                                    iv_user_4.setVisibility(View.VISIBLE);
                                                                    Glide.with(ClipDollDetailActivity.this)
                                                                            .load(liveRoomUserBeanArrayList.get(3).getHeadImg())
                                                                            .placeholder(R.drawable.avatar)
                                                                            .error(R.drawable.avatar)
                                                                            .into(iv_user_4);
                                                                    break;
                                                                default:
                                                                    break;
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        } else {
                                            LogUtils.e("请求数据失败：" + msg + "-" + code + "-" + req);
                                            ToastUtils.showShort("请求数据失败:" + msg);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtils.e("onPause");
        ILVLiveManager.getInstance().onPause();
    }

    /**
     * 不可见，不可操作
     */
    @Override
    protected void onStop() {
        LogUtils.e("onStop");
        if (EmptyUtils.isNotEmpty(roomStateTimer)) {
            roomStateTimer.cancel();
            roomStateTimer = null;//取消房间的状态轮询
            roomStateTimerTask.cancel();
            roomStateTimerTask = null;
        }
        if (EmptyUtils.isNotEmpty(playerNumTimer)) {
            playerNumTimer.cancel();
        }
        super.onStop();
        //用户退出直播间 计数
        removeUserNumForLiveRoom();
        //停止播放背景音乐
        BackgroundMusicPlayerUtil.getInstance(getApplicationContext()).stopMusic();
    }

    private void removeUserNumForLiveRoom() {
        OkHttpUtils.get()
                .url(Constants.getRemoveUserForLiveRoomUrl())
                .addParams(Constants.SESSION, SPUtils.getInstance().getString(Constants.SESSION))
                .addParams(Constants.GROUPID, homeRoomBean.getGroupId())
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtils.e(e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            JSONObject jsonObjectResHead = jsonObject.optJSONObject("resHead");
                            int code = jsonObjectResHead.optInt("code");
                            String msg = jsonObjectResHead.optString("msg");
                            String req = jsonObjectResHead.optString("req");
                            JSONObject jsonObjectResBody = jsonObject.optJSONObject("resBody");
                            if (code == 1) {
                                int success = jsonObjectResBody.optInt("success");
                                if (success == 1) {
                                    LogUtils.e("用户退出计数成功");
                                }
                            } else {
                                LogUtils.e("请求数据失败：" + msg + "-" + code + "-" + req);
                                ToastUtils.showShort("请求数据失败:" + msg);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        LogUtils.e("onDestroy");
        ILVLiveManager.getInstance().onDestory();
        releaseResource();
        Glide.with(getApplicationContext()).pauseRequests();
        BackgroundMusicPlayerUtil.getInstance(getApplicationContext()).stopMusic();
        super.onDestroy();
    }

    @Override
    public void goBack() {
        super.goBack();
    }

    /**
     * 释放资源
     */
    private void releaseResource() {
        if (EmptyUtils.isNotEmpty(clipYesPopupWindow)) {
            clipYesPopupWindow.dismiss();
        }
        if (EmptyUtils.isNotEmpty(clipNoPopupWindow)) {
            clipNoPopupWindow.dismiss();
        }
        if (EmptyUtils.isNotEmpty(gameResultTimer)) {
            gameResultTimer.cancel();
        }
        if (EmptyUtils.isNotEmpty(roomStateTimer)) {
            roomStateTimer.cancel();
        }
        if (EmptyUtils.isNotEmpty(mTimer)) {
            mTimer.cancel();
        }
        if (EmptyUtils.isNotEmpty(tryAgingYesTimer)) {
            tryAgingYesTimer.cancel();
        }
        if (EmptyUtils.isNotEmpty(tryAgingNoTimer)) {
            tryAgingNoTimer.cancel();
        }
        if (EmptyUtils.isNotEmpty(playerNumTimer)) {
            playerNumTimer.cancel();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (isShowGoBackDialog) {
                showGoBackDialog();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void gameOver() {
        isShowGoBackDialog = false;
        exceptionTimer.cancel();
        exceptionTimerTask.cancel();
        ILiveRoomManager.getInstance().changeAuthority(Const_Auth_Member, new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                LogUtils.e("切换角色成功");
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                LogUtils.e("切换角色错误：" + "module=" + module + "，errCode=" + errCode + "，errMsg=" + errMsg);
            }
        });
        OkHttpUtils.post()
                .url(Constants.getGameOverUrl())
                .addParams(Constants.SESSION, SPUtils.getInstance().getString(Constants.SESSION))
                .addParams(Constants.GROUPID, homeRoomBean.getGroupId())
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtils.e(e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        LogUtils.e(response);
                    }
                });
    }

    private void initTimerTask() {
        mTask = new TimerTask() {
            @Override
            public void run() {
                ClipDollDetailActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        --mTotalTime;
                        tv_timer.setText(String.valueOf(mTotalTime) + "s");
                        tv_timer.setTextSize(30);
                        tv_timer.setTextColor(getResources().getColor(R.color.pure_white_color));
                        if (mTotalTime <= 9) {
                            tv_timer.setTextSize(40);
                            tv_timer.setTextColor(getResources().getColor(R.color.seventh_text_color));
                            //最后5s倒计时
                            if (!SPUtils.getInstance().getBoolean(Constants.IS_PLAY_BACKGROUND_SOUND)) {
                                if (mTotalTime == 5 || mTotalTime == 4 || mTotalTime == 3 || mTotalTime == 2 || mTotalTime == 1) {
                                    SoundPoolUtil.getInstance(getApplicationContext()).play(2);
                                }
                            }
                        }
                        if (mTotalTime <= 0) {
                            startGameAfter();
                        }
                    }
                });
            }
        };
    }

    private void handlerWaWa(String oper) {
        OkHttpUtils.post()
                .url(Constants.getPlayGameUrl())
                .addParams(Constants.SESSION, SPUtils.getInstance().getString(Constants.SESSION))
                .addParams("gameId", SPUtils.getInstance().getString("gameId"))
                .addParams(Constants.GROUPID, homeRoomBean.getGroupId())
                .addParams("oper", oper)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtils.e(e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        LogUtils.e(response);
                    }
                });
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (isFront) {
            //正面摄像头
            switch (v.getId()) {
                case R.id.action_btn_left:
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        handlerWaWa("left");
                        if (!SPUtils.getInstance().getBoolean(Constants.IS_PLAY_BACKGROUND_SOUND)) {
                            SoundPoolUtil.getInstance(getApplicationContext()).play(1);
                        }
                    }
                    break;
                case R.id.action_btn_right:
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        handlerWaWa("right");
                        if (!SPUtils.getInstance().getBoolean(Constants.IS_PLAY_BACKGROUND_SOUND)) {
                            SoundPoolUtil.getInstance(getApplicationContext()).play(1);
                        }
                    }
                    break;
                case R.id.action_btn_top:
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        handlerWaWa("down");
                        if (!SPUtils.getInstance().getBoolean(Constants.IS_PLAY_BACKGROUND_SOUND)) {
                            SoundPoolUtil.getInstance(getApplicationContext()).play(1);
                        }
                    }
                    break;
                case R.id.action_btn_bottom:
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        handlerWaWa("up");
                        if (!SPUtils.getInstance().getBoolean(Constants.IS_PLAY_BACKGROUND_SOUND)) {
                            SoundPoolUtil.getInstance(getApplicationContext()).play(1);
                        }
                    }
                    break;
                default:
                    break;
            }
        } else {
            //侧面摄像头
            switch (v.getId()) {
                case R.id.action_btn_left:
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        handlerWaWa("up");
                        if (!SPUtils.getInstance().getBoolean(Constants.IS_PLAY_BACKGROUND_SOUND)) {
                            SoundPoolUtil.getInstance(getApplicationContext()).play(1);
                        }
                    }
                    break;
                case R.id.action_btn_right:
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        handlerWaWa("down");
                        if (!SPUtils.getInstance().getBoolean(Constants.IS_PLAY_BACKGROUND_SOUND)) {
                            SoundPoolUtil.getInstance(getApplicationContext()).play(1);
                        }
                    }
                    break;
                case R.id.action_btn_top:
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        handlerWaWa("left");
                        if (!SPUtils.getInstance().getBoolean(Constants.IS_PLAY_BACKGROUND_SOUND)) {
                            SoundPoolUtil.getInstance(getApplicationContext()).play(1);
                        }
                    }
                    break;
                case R.id.action_btn_bottom:
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        handlerWaWa("right");
                        if (!SPUtils.getInstance().getBoolean(Constants.IS_PLAY_BACKGROUND_SOUND)) {
                            SoundPoolUtil.getInstance(getApplicationContext()).play(1);
                        }
                    }
                    break;
                default:
                    break;
            }
        }
        return false;
    }

    /**
     * 游戏中退出房间的对话框
     */
    private void showGoBackDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialog_Logout);
        View view = View.inflate(this, R.layout.dialog_out_room_view, null);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        //设置对话框的大小
        alertDialog.getWindow().setLayout(SizeUtils.dp2px(350), LinearLayout.LayoutParams.WRAP_CONTENT);
        //监听事件
        view.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        view.findViewById(R.id.btn_make_sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                releaseResource();
                goBack();
            }
        });
    }

    /**
     * 加入房间失败的对话框
     */
    private void showJoinRoomFailDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialog_Logout);
        View view = View.inflate(this, R.layout.dialog_join_room_fail_view, null);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        //设置对话框的大小
        alertDialog.getWindow().setLayout(SizeUtils.dp2px(340), LinearLayout.LayoutParams.WRAP_CONTENT);
        //监听事件
        view.findViewById(R.id.btn_out_room).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                goBack();
            }
        });
        view.findViewById(R.id.btn_report).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                releaseResource();
                goBack();
                ToastUtils.showShort("反馈成功！我们将尽快处理。");
                feedBackPostServer();
                alertDialog.dismiss();
            }
        });
        alertDialog.setCanceledOnTouchOutside(false);
    }

    private void feedBackPostServer() {
        OkHttpUtils.get()
                .url(Constants.getFeedBackPostServerUrl())
                .addParams(Constants.SESSION, SPUtils.getInstance().getString(Constants.SESSION))
                .addParams(Constants.GROUPID, homeRoomBean.getGroupId())
                .addParams(Constants.CONTENT, "加入房间失败")
                .addParams(Constants.TYPE, String.valueOf(0))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtils.e(e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        LogUtils.e(response);
                    }
                });
    }

    private void regToWx() {
        api = WXAPIFactory.createWXAPI(this, Constants.APP_ID, true);
        api.registerApp(Constants.APP_ID);
    }

    private void showSharePlatformPopWindow() {
        SharePlatformPopupWindow sharePlatformPopWindow = new SharePlatformPopupWindow(ClipDollDetailActivity.this, new SharePlatformPopupWindow.SharePlatformListener() {
            @Override
            public void onWeChatClicked() {
                weChatShare(0);
            }

            @Override
            public void onWechatMomentsClicked() {
                weChatShare(1);
            }

            @Override
            public void onCancelBtnClicked() {
            }
        });
        sharePlatformPopWindow.initView();
        sharePlatformPopWindow.showAtLocation(view, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    private void weChatShare(int flag) {
        //初始化一个wxwebpageobject对象
        WXWebpageObject webpageObject = new WXWebpageObject();
        webpageObject.webpageUrl = "http://wwh5.91tmedia.com/room/" + homeRoomBean.getGroupId();

        //用wxwebpageobject对象初始化一个wxmediaMessage对象
        WXMediaMessage msg = new WXMediaMessage(webpageObject);
        msg.title = "好友" + SPUtils.getInstance().getString(Constants.NICKNAME) + "邀您一起在线直播抓娃娃，共同High起抓娃娃世界。";
        msg.description = "手机直播抓娃娃，随时随地想抓就抓";
        Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        msg.setThumbImage(thumb);

        //构造一个Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = flag == 0 ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;

        //调用api接口发送数据到微信
        api.sendReq(req);
    }

    @Override
    public void onItemClick(Object data, int position) {
        if (data.getClass().getSimpleName().equals("LiveRoomLuckyUserBean")) {
            LiveRoomLuckyUserBean liveRoomLuckyUserBean = (LiveRoomLuckyUserBean) data;
            if (EmptyUtils.isNotEmpty(liveRoomLuckyUserBean)) {
                LiveRoomLuckyUserBean.UserBean userBean = liveRoomLuckyUserBean.getUser();
                if (EmptyUtils.isNotEmpty(userBean)) {
                    //                    DataManager.getInstance().setData1(liveRoomLuckyUserBean.getUser());
                    //                    gotoPager(GuestStateFragment.class, null);
                }
            }
        }
    }
}
