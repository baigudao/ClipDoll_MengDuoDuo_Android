package com.meng.duo.clip.doll.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.EmptyUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.meng.duo.clip.doll.R;
import com.meng.duo.clip.doll.fragment.UserProtocolFragment;
import com.meng.duo.clip.doll.util.Constants;
import com.rey.material.widget.CheckBox;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Devin on 2017/12/11 10:46
 * E-mail:971060378@qq.com
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private LinearLayout ll_weixin_login;
    private IWXAPI api;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BarUtils.setStatusBarColor(LoginActivity.this, getResources().getColor(R.color.main_color));
        BarUtils.hideNavBar(LoginActivity.this);
        setContentView(R.layout.activity_login);

        ll_weixin_login = (LinearLayout) findViewById(R.id.ll_weixin_login);
        ll_weixin_login.setOnClickListener(this);
        findViewById(R.id.tv_user_protocol).setOnClickListener(this);
        CheckBox material_check_box = (CheckBox) findViewById(R.id.material_check_box);
        material_check_box.setChecked(true);//默认选择
        ll_weixin_login.setEnabled(true);//默认激活
        material_check_box.setOnCheckedChangeListener(this);

        //注册微信
        regToWX();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_weixin_login:
                //                wxLogin();
                test();
                break;
            case R.id.tv_user_protocol:
                gotoPager(UserProtocolFragment.class, null);
                break;
            default:
                break;
        }
    }

    private void regToWX() {
        api = WXAPIFactory.createWXAPI(LoginActivity.this, Constants.APP_ID, true);
        api.registerApp(Constants.APP_ID);
    }

    public void wxLogin() {
        if (api != null && api.isWXAppInstalled()) {
            SendAuth.Req req = new SendAuth.Req();
            req.scope = "snsapi_userinfo";
            req.state = "clip_doll_meng_weixin_login";
            api.sendReq(req);
            finish();
        } else
            ToastUtils.showShort("用户未安装微信");
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            ll_weixin_login.setEnabled(true);
        } else {
            ll_weixin_login.setEnabled(false);
        }
    }


    private void test() {
        JSONObject jsonObjectTicketAndUserId = getJsonData("nvsQRv269/fexiLcCnr+cdXELOi8wWQKnKUVmjuB+4U=", 1000030);
        //存储ticket和userId
        SPUtils.getInstance().put(Constants.SESSION, jsonObjectTicketAndUserId.toString());
        //存储sig
        SPUtils.getInstance().put(Constants.TLSSIGN, "eJxFkF1vgjAYhf8L18tsKQVd4gV*xS0sAxzRcUMKbV3dhFpqgzH776tMstvnyZtzznt13qPNI5FS0ILoAinqPDnAeegx66RQrCBcM2UxxBi7AAzWMNWKprbCBRBDFwHwLwVltRZc-B3eBBpUK-aWvS6T*fNi7sfKj8u30RhvePn9wvPKpCYrV1o2XhS39ET2eRfAHQhFKFKWH9ddep5wmJnD8jMxWSTxx3gdjE7RdpasaHrZhYdtEE6nQxj9Kvp1txqerYE95Hp3qcWR9bugZT6aBHdOqqo517rQF8n6d-z8AmlSVv0_");
        //存储第三方微信登录返回的用户信息
        SPUtils.getInstance().put(Constants.HEADIMG, "http://m.qulishi.com/UploadFile/2015-3/2015310172554.jpg");
        SPUtils.getInstance().put(Constants.INVITECODE, "YXMGHD");
        SPUtils.getInstance().put(Constants.NICKNAME, "测试账号1");
        SPUtils.getInstance().put(Constants.USERID, 1000030);

        configXGPush();

        //存储登录状态
        if (EmptyUtils.isNotEmpty(SPUtils.getInstance().getString(Constants.SESSION))) {
            SPUtils.getInstance().put(Constants.IS_USER_LOGIN, true);
        }
        gotoPager(MainActivity.class, null);
        finish();
        ToastUtils.showShort(R.string.login_successful);
    }

    private void configXGPush() {
        XGPushConfig.enableDebug(this, true);
        XGPushManager.setTag(this, "XINGE");
        XGPushManager.registerPush(this, String.valueOf(SPUtils.getInstance().getInt(Constants.USERID)), new XGIOperateCallback() {
            @Override
            public void onSuccess(Object o, int i) {
                LogUtils.e("注册成功，设备token为：" + o);
                LogUtils.e("id: " + String.valueOf(SPUtils.getInstance().getInt(Constants.USERID)));
            }

            @Override
            public void onFail(Object o, int i, String s) {
                LogUtils.e("注册失败，错误码：" + i + ",错误信息：" + s);
            }
        });
    }

    public JSONObject getJsonData(String ticket, int userId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("ticket", ticket);
            jsonObject.put("userId", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
