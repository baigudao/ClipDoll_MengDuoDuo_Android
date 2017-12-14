package com.meng.duo.clip.doll.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.EmptyUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.meng.duo.clip.doll.R;
import com.meng.duo.clip.doll.fragment.UserProtocolFragment;
import com.meng.duo.clip.doll.util.Constants;
import com.rey.material.widget.CheckBox;
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
                                wxLogin();
//                test();
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
        JSONObject jsonObjectTicketAndUserId = getJsonData("Ga+iVFHxtrcURHJE/dsZ1OIvWORg51u8a5CmfT9/388=", 1000014);
        //存储ticket和userId
        SPUtils.getInstance().put(Constants.SESSION, jsonObjectTicketAndUserId.toString());
        //存储sig
        SPUtils.getInstance().put(Constants.TLSSIGN, "eJxFkN1Og0AQRt*F2xrdX1JMvEBEQqAqwdbqzWbtLs3YSOmyJdim7*4WS8zcnZPJfN8cvde8vJZNA0pIK6hR3q2HvKsB674Bo4WsrDYOY845QWi0nTYtbGsnCMIcE4rQvwSlawsV-C2eBWYX1cLasVk8j9LHXhUHnzQLiabRLgJ1KIKyVA-Jl4Gkq7Nq8mHSPEzeFjKEOOzbdM7aeBlM3y3uNjIL-Or*xV-vPvc37DkvnqqJnSU4SzN2Nx5TGzG0O8dgLgZnlIxJLHzroRembhjlFy5Xq*2*tsL*NHp4x*kXLThWQA__");
        //存储第三方微信登录返回的用户信息
        SPUtils.getInstance().put(Constants.HEADIMG, "https://images.pexels.com/photos/247206/pexels-photo-247206.jpeg?h=350&auto=compress&cs=tinysrgb");
        SPUtils.getInstance().put(Constants.INVITECODE, "YXMGHD");
        SPUtils.getInstance().put(Constants.NICKNAME, "测试账号1");
        SPUtils.getInstance().put(Constants.USERID, 1000014);

        //存储登录状态
        if (EmptyUtils.isNotEmpty(SPUtils.getInstance().getString(Constants.SESSION))) {
            SPUtils.getInstance().put(Constants.IS_USER_LOGIN, true);
        }
        gotoPager(MainActivity.class, null);
        finish();
        ToastUtils.showShort(R.string.login_successful);
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
