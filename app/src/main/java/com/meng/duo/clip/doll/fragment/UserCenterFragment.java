package com.meng.duo.clip.doll.fragment;

import android.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.EmptyUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.meng.duo.clip.doll.R;
import com.meng.duo.clip.doll.activity.LoginActivity;
import com.meng.duo.clip.doll.util.Constants;
import com.meng.duo.clip.doll.util.DataManager;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;

/**
 * Created by Devin on 2017/11/16 19:20
 * E-mail:971060378@qq.com
 */

public class UserCenterFragment extends BaseFragment {

    private ImageView music_btn_toggle_on;
    private ImageView music_btn_toggle_off;
    private ImageView sound_btn_toggle_on;
    private ImageView sound_btn_toggle_off;

    private ImageView iv_user_photo;
    private TextView tv_user_name;
    private TextView tv_id_num;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_user_center;
    }

    @Override
    protected void initView(View view) {
        view.findViewById(R.id.ll_close).setOnClickListener(this);
        ((TextView) view.findViewById(R.id.tv_bar_title)).setText("");
        ImageView iv_share = (ImageView) view.findViewById(R.id.iv_share);
        iv_share.setImageResource(R.drawable.news_white);
        iv_share.setOnClickListener(this);

        iv_user_photo = (ImageView) view.findViewById(R.id.iv_user_photo);
        tv_user_name = (TextView) view.findViewById(R.id.tv_user_name);
        tv_id_num = (TextView) view.findViewById(R.id.tv_id_num);

        view.findViewById(R.id.rl_game_coin).setOnClickListener(this);
        view.findViewById(R.id.rl_clip_doll_record).setOnClickListener(this);
        view.findViewById(R.id.rl_order).setOnClickListener(this);
        view.findViewById(R.id.rl_prize).setOnClickListener(this);
        view.findViewById(R.id.rl_invite_num).setOnClickListener(this);
        view.findViewById(R.id.rl_about_us).setOnClickListener(this);
        view.findViewById(R.id.rl_feed_back).setOnClickListener(this);
        view.findViewById(R.id.rl_score_prize).setOnClickListener(this);
        view.findViewById(R.id.rl_check_update).setOnClickListener(this);

        music_btn_toggle_on = (ImageView) view.findViewById(R.id.music_btn_toggle_on);
        music_btn_toggle_on.setOnClickListener(this);
        music_btn_toggle_off = (ImageView) view.findViewById(R.id.music_btn_toggle_off);
        music_btn_toggle_off.setOnClickListener(this);
        sound_btn_toggle_on = (ImageView) view.findViewById(R.id.sound_btn_toggle_on);
        sound_btn_toggle_on.setOnClickListener(this);
        sound_btn_toggle_off = (ImageView) view.findViewById(R.id.sound_btn_toggle_off);
        sound_btn_toggle_off.setOnClickListener(this);

        //背景音乐
        if (!SPUtils.getInstance().getBoolean(Constants.IS_PLAY_BACKGROUND_MUSIC)) {
            showImageView(music_btn_toggle_on, music_btn_toggle_off);
        } else {
            showImageView(music_btn_toggle_off, music_btn_toggle_on);
        }
        //背景音效
        if (!SPUtils.getInstance().getBoolean(Constants.IS_PLAY_BACKGROUND_SOUND)) {
            showImageView(sound_btn_toggle_on, sound_btn_toggle_off);
        } else {
            showImageView(sound_btn_toggle_off, sound_btn_toggle_on);
        }

        view.findViewById(R.id.btn_exit_login).setOnClickListener(this);
    }

    private void showImageView(ImageView imageView1, ImageView imageView2) {
        imageView1.setVisibility(View.VISIBLE);
        imageView2.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_close:
                goBack();
                break;
            case R.id.iv_share:
                gotoPager(NotificationCenterFragment.class, null);
                break;
            case R.id.rl_game_coin:
                gotoPager(MyGameCoinFragment.class, null);
                break;
            case R.id.rl_clip_doll_record:
                gotoPager(ClipDollRecordFragment.class, null);
                break;
            case R.id.rl_order:
                gotoPager(MyOrderFragment.class, null);
                break;
            case R.id.rl_prize:
                gotoPager(InvitePrizeFragment.class, null);
                break;
            case R.id.rl_invite_num:
                gotoPager(InviteNumExchangeFragment.class, null);
                break;
            case R.id.rl_about_us:
                gotoPager(AboutUsFragment.class, null);
                break;
            case R.id.rl_feed_back:
                gotoPager(FeedBackFragment.class, null);
                break;
            case R.id.rl_score_prize:
                ToastUtils.showShort("评分有奖");
                break;
            case R.id.rl_check_update:
                checkUpdate();
                break;
            case R.id.btn_exit_login:
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AlertDialog_Logout);
                View view = View.inflate(mContext, R.layout.dialog_logout_view, null);
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
                        OkHttpUtils.post()
                                .url(Constants.getLogoutUrl())
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
                                                //清空ticket和userId
                                                SPUtils.getInstance().put(Constants.SESSION, "");
                                                //清空sig
                                                SPUtils.getInstance().put(Constants.TLSSIGN, "");
                                                //清空第三方微信登录返回的用户信息
                                                SPUtils.getInstance().put(Constants.HEADIMG, "");
                                                SPUtils.getInstance().put(Constants.INVITECODE, "");
                                                SPUtils.getInstance().put(Constants.NICKNAME, "");
                                                SPUtils.getInstance().put(Constants.USERID, 0);
                                                DataManager.getInstance().setUserInfo(null);
                                                //清空登录状态
                                                SPUtils.getInstance().put(Constants.IS_USER_LOGIN, false);

                                                ActivityUtils.finishAllActivities();
                                                gotoPager(LoginActivity.class, null);
                                                ToastUtils.showShort("退出登录成功！");
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
                break;
            case R.id.music_btn_toggle_on:
                showImageView(music_btn_toggle_off, music_btn_toggle_on);
                SPUtils.getInstance().put(Constants.IS_PLAY_BACKGROUND_MUSIC, true);
                break;
            case R.id.music_btn_toggle_off:
                showImageView(music_btn_toggle_on, music_btn_toggle_off);
                SPUtils.getInstance().put(Constants.IS_PLAY_BACKGROUND_MUSIC, false);
                break;
            case R.id.sound_btn_toggle_on:
                showImageView(sound_btn_toggle_off, sound_btn_toggle_on);
                SPUtils.getInstance().put(Constants.IS_PLAY_BACKGROUND_SOUND, true);
                break;
            case R.id.sound_btn_toggle_off:
                showImageView(sound_btn_toggle_on, sound_btn_toggle_off);
                SPUtils.getInstance().put(Constants.IS_PLAY_BACKGROUND_SOUND, false);
                break;
            default:
                break;
        }
    }

    private void checkUpdate() {
        OkHttpUtils.get()
                .url(Constants.getCheckVersionUrl())
                .addParams(Constants.SESSION, SPUtils.getInstance().getString(Constants.SESSION))
                .addParams("platform", String.valueOf(0))
                .addParams("version", AppUtils.getAppVersionName())
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
                                    ToastUtils.showShort("已经是最新版本");
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
    protected void initData() {
        super.initData();
        initHeadView();
    }

    private void initHeadView() {
        String string_head_img = SPUtils.getInstance().getString(Constants.HEADIMG);
        if (EmptyUtils.isNotEmpty(string_head_img)) {
            Glide.with(mContext)
                    .load(string_head_img)
                    .placeholder(R.drawable.avatar)
                    .error(R.drawable.avatar)
                    .into(iv_user_photo);
        }
        tv_user_name.setText(SPUtils.getInstance().getString(Constants.NICKNAME));
        tv_id_num.setText("ID:" + SPUtils.getInstance().getInt(Constants.USERID));
    }
}
