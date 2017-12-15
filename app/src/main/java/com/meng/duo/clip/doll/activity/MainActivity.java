package com.meng.duo.clip.doll.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.blankj.utilcode.util.EmptyUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meng.duo.clip.doll.R;
import com.meng.duo.clip.doll.adapter.BaseRecyclerViewAdapter;
import com.meng.duo.clip.doll.bean.BannerBean;
import com.meng.duo.clip.doll.bean.HomeRoomBean;
import com.meng.duo.clip.doll.fragment.BannerFragment;
import com.meng.duo.clip.doll.fragment.InvitePrizeFragment;
import com.meng.duo.clip.doll.fragment.NotificationCenterFragment;
import com.meng.duo.clip.doll.fragment.UserCenterFragment;
import com.meng.duo.clip.doll.util.Constants;
import com.meng.duo.clip.doll.util.DataManager;
import com.meng.duo.clip.doll.util.GlideImageLoader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.core.ILiveLoginManager;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Call;


public class MainActivity extends BaseActivity implements View.OnClickListener, OnRefreshListener, OnLoadmoreListener, BaseRecyclerViewAdapter.OnItemClickListener {

    private Banner banner;

    private static final int HOME_ROOM_LIST_DATA_TYPE = 1;

    private NestedScrollView nestedScrollView;

    private SmartRefreshLayout smartRefreshLayout;
    private RecyclerView recyclerView;
    private boolean bLogin;

    private ArrayList<BannerBean> bannerArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = View.inflate(this, R.layout.activity_main, null);
        setContentView(view);

        //设置状态栏文字颜色及图标为深色
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        initView();
        initData();
    }

    private void initView() {
        findViewById(R.id.ll_close).setVisibility(View.GONE);
        ImageView iv_user_photo = (ImageView) findViewById(R.id.iv_user_photo);
        iv_user_photo.setVisibility(View.VISIBLE);
        iv_user_photo.setOnClickListener(this);
        ImageView iv_share = (ImageView) findViewById(R.id.iv_share);
        iv_share.setImageResource(R.drawable.news);
        iv_share.setOnClickListener(this);

        findViewById(R.id.tv_exchange).setOnClickListener(this);
        findViewById(R.id.iv_exchange).setOnClickListener(this);

        nestedScrollView = (NestedScrollView) findViewById(R.id.nestedScrollView);

        smartRefreshLayout = (SmartRefreshLayout) findViewById(R.id.smartRefreshLayout);
        smartRefreshLayout.setEnableRefresh(true);
        smartRefreshLayout.setEnableLoadmore(true);
        smartRefreshLayout.setOnRefreshListener(this);
        smartRefreshLayout.setOnLoadmoreListener(this);

        bLogin = false;

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setNestedScrollingEnabled(false);

        banner = (Banner) findViewById(R.id.banner);
        //动态的设置banner的长宽比
        int width = ScreenUtils.getScreenWidth() - SizeUtils.dp2px(30);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) banner.getLayoutParams();
        layoutParams.height = (width * 3) / 7;
        banner.setLayoutParams(layoutParams);
        //设置banner样式
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        //设置图片加载器
        banner.setImageLoader(new GlideImageLoader());
        //设置banner动画效果
        banner.setBannerAnimation(Transformer.DepthPage);
        //设置自动轮播，默认为true
        banner.isAutoPlay(true);
        //设置轮播时间
        banner.setDelayTime(4000);
        //设置指示器位置（当banner模式中有指示器时）
        banner.setIndicatorGravity(BannerConfig.CENTER);
        //设置banner的点击事件
        banner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                if (bannerArrayList.size() != 0) {
                    BannerBean bannerBean = bannerArrayList.get(position);
                    if (EmptyUtils.isNotEmpty(bannerBean)) {
                        String url = bannerBean.getUrl();
                        if (EmptyUtils.isNotEmpty(url)) {
                            DataManager.getInstance().setData1(url);
                            gotoPager(BannerFragment.class, null);
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_user_photo:
                gotoPager(UserCenterFragment.class, null);
                break;
            case R.id.iv_exchange:
            case R.id.tv_exchange:
                getHomeRoomListData();
                break;
            case R.id.iv_share:
                gotoPager(NotificationCenterFragment.class, null);
                break;
            default:
                break;
        }
    }

    private void initData() {
        getHomeBannerData();
        getHomeRoomListData();
        if (!bLogin) {
            loginTXLive();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //是否首次登录
        int firstLogin = SPUtils.getInstance().getInt(Constants.FIRSTLOGIN);
        //0.是(跳注册奖励弹窗) 1.否
        if (firstLogin == 0) {
            SPUtils.getInstance().put(Constants.FIRSTLOGIN, 1);
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialog_Logout);
            View view_ = View.inflate(MainActivity.this, R.layout.new_user_login_dialog, null);
            builder.setView(view_);
            final AlertDialog alertDialog = builder.create();
            alertDialog.show();
            //设置对话框的大小
            alertDialog.getWindow().setLayout(SizeUtils.dp2px(300), LinearLayout.LayoutParams.WRAP_CONTENT);
            //监听事件
            view_.findViewById(R.id.iv_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });
            view_.findViewById(R.id.btn_go_to_invite).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                    gotoPager(InvitePrizeFragment.class, null);
                }
            });
            view_.findViewById(R.id.btn_start_game).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });
        }
    }

    private void getHomeBannerData() {
        OkHttpUtils.get()
                .url(Constants.getHomeBannerUrl())
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
                                handlerBannerData(jsonObjectResBody);
                                smartRefreshLayout.finishRefresh();
                                smartRefreshLayout.finishLoadmore();
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

    private void getHomeRoomListData() {
        OkHttpUtils.get()
                .url(Constants.getHomeRoomListUrl())
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
                            final String req = jsonObjectResHead.optString("req");
                            JSONObject jsonObjectResBody = jsonObject.optJSONObject("resBody");
                            if (code == 1) {
                                handlerHomeRoomListData(jsonObjectResBody);
                                smartRefreshLayout.finishRefresh();
                                smartRefreshLayout.finishLoadmore();
                            } else {
                                LogUtils.e("请求数据失败：" + msg + "-" + code + "-" + req);
                                ToastUtils.showShort("请求数据失败" + msg);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void handlerHomeRoomListData(JSONObject jsonObjectResBody) {
        if (EmptyUtils.isNotEmpty(jsonObjectResBody)) {
            JSONArray homeRoomList = jsonObjectResBody.optJSONArray("liveRoomList");
            if (EmptyUtils.isNotEmpty(homeRoomList)) {
                Gson gson = new Gson();
                ArrayList<HomeRoomBean> homeRoomBeanArrayList = gson.fromJson(homeRoomList.toString(), new TypeToken<ArrayList<HomeRoomBean>>() {
                }.getType());
                if (EmptyUtils.isNotEmpty(homeRoomBeanArrayList) && homeRoomBeanArrayList.size() != 0) {
                    BaseRecyclerViewAdapter baseRecyclerViewAdapter = new BaseRecyclerViewAdapter(MainActivity.this, homeRoomBeanArrayList, HOME_ROOM_LIST_DATA_TYPE);
                    recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 2, LinearLayoutManager.VERTICAL, false));
                    recyclerView.setAdapter(baseRecyclerViewAdapter);
                    baseRecyclerViewAdapter.setOnItemClickListener(this);
                }
            }
        }
    }

    private void handlerBannerData(JSONObject jsonObjectResBody) {
        if (EmptyUtils.isNotEmpty(jsonObjectResBody)) {
            JSONArray bannerList = jsonObjectResBody.optJSONArray("bannerList");
            if (EmptyUtils.isNotEmpty(bannerList)) {
                Gson gson = new Gson();
                bannerArrayList = gson.fromJson(bannerList.toString(), new TypeToken<ArrayList<BannerBean>>() {
                }.getType());

                if (EmptyUtils.isNotEmpty(bannerArrayList) && bannerArrayList.size() != 0) {
                    ArrayList<String> images = new ArrayList<>();
                    for (int i = 0; i < bannerArrayList.size(); i++) {
                        images.add(bannerArrayList.get(i).getPictures());
                    }
                    //设置图片集合
                    banner.setImages(images);
                    //banner设置方法全部调用完毕时最后调用
                    banner.start();
                }
            }
        }
    }

    @Override
    public void onItemClick(Object data, int position) {
        if (data.getClass().getSimpleName().equals("HomeRoomBean")) {
            HomeRoomBean homeRoomBean = (HomeRoomBean) data;
            if (EmptyUtils.isNotEmpty(homeRoomBean)) {
                DataManager.getInstance().setData1(homeRoomBean);
                gotoPager(ClipDollDetailActivity.class, null);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //开始轮播
        banner.startAutoPlay();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //结束轮播
        banner.stopAutoPlay();
    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        initData();
    }

    @Override
    public void onLoadmore(RefreshLayout refreshlayout) {
        //此时没有重新加载数据，只是做了返回nestedScrollView顶部的操作
        smartRefreshLayout.finishLoadmore();
        nestedScrollView.post(new Runnable() {
            @Override
            public void run() {
                nestedScrollView.fullScroll(View.FOCUS_UP);
            }
        });
    }

    private void loginTXLive() {
        String sig = SPUtils.getInstance().getString(Constants.TLSSIGN);
        int user_id = SPUtils.getInstance().getInt(Constants.USERID);
        if (EmptyUtils.isNotEmpty(sig) && EmptyUtils.isNotEmpty(user_id)) {
            ILiveLoginManager.getInstance().iLiveLogin(String.valueOf(user_id), sig, new ILiveCallBack() {
                @Override
                public void onSuccess(Object data) {
                    bLogin = true;
                    LogUtils.e("登录成功");
                }

                @Override
                public void onError(String module, int errCode, String errMsg) {
                    LogUtils.e("module：" + module + "，errCode：" + errCode + "，errMsg" + errMsg);
                }
            });
        }
    }

    private long startTime = 0;

    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();
        if ((currentTime - startTime) > 2000) {
            Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            startTime = currentTime;
        } else {
            finish();
        }
    }
}
