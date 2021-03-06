package com.meng.duo.clip.doll.wxapi;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.meng.duo.clip.doll.R;
import com.meng.duo.clip.doll.activity.BaseActivity;
import com.meng.duo.clip.doll.util.Constants;
import com.meng.duo.clip.doll.util.DataManager;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.analytics.game.UMGameAgent;

/**
 * Created by jackie on 2017/8/15 22:52.
 * QQ : 971060378
 * Used as : 微信支付回调
 */

public class WXPayEntryActivity extends BaseActivity implements IWXAPIEventHandler {

    // IWXAPI 是第三方app和微信通信的openapi接口
    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //        setContentView(R.layout.activity_result_wx_pay);
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        api = WXAPIFactory.createWXAPI(this, Constants.APP_ID, false);
        // 将该app注册到微信
        api.registerApp(Constants.APP_ID);

        //注意：
        //第三方开发者如果使用透明界面来实现WXEntryActivity，需要判断handleIntent的返回值，如果返回值为false，则说明入参不合法未被SDK处理，应finish当前透明界面，避免外部通过传递非法参数的Intent导致停留在透明界面，引起用户的疑惑
        try {
            api.handleIntent(getIntent(), this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    // 微信发送请求到第三方应用时，会回调到该方法
    @Override
    public void onReq(BaseReq req) {
        switch (req.getType()) {
            case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
                LogUtils.e("command_get_message_from_wx");
                break;
            case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
                LogUtils.e("command_show_message_from_wx");
                break;
            default:
                break;
        }
    }

    // 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
    @Override
    public void onResp(BaseResp resp) {
        int result = 0;
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                //                DataManager.getInstance().setData1("RECHARGE_SUCCESS");
                int pay_money = (int) DataManager.getInstance().getData1();
                int get_wawa_coin = (int) DataManager.getInstance().getData2();
                DataManager.getInstance().setData1(null);
                DataManager.getInstance().setData2(null);
                UMGameAgent.pay(pay_money, get_wawa_coin, 21);//比如通过微信用 10元钱 购买了 1000 个金币，可以这样调用：
                LogUtils.e("支付金额：" + pay_money + "\n得到的娃娃币：" + get_wawa_coin);
                result = R.string.errcode_success;
                goBack();
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = R.string.errcode_cancel;
                goBack();
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = R.string.errcode_deny;
                goBack();
                break;
            case BaseResp.ErrCode.ERR_UNSUPPORT:
                result = R.string.errcode_unsupported;
                goBack();
                break;
            default:
                result = R.string.errcode_unknown;
                goBack();
                break;
        }
        Toast.makeText(this, result, Toast.LENGTH_LONG).show();
    }
}