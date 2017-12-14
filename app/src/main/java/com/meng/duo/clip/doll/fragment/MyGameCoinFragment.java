package com.meng.duo.clip.doll.fragment;

import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.EmptyUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;
import com.meng.duo.clip.doll.R;
import com.meng.duo.clip.doll.bean.WeChatPayParamsBean;
import com.meng.duo.clip.doll.util.Constants;
import com.meng.duo.clip.doll.util.DataManager;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;

/**
 * Created by Devin on 2017/11/18 20:49
 * E-mail:971060378@qq.com
 */

public class MyGameCoinFragment extends BaseFragment {

    private RelativeLayout rl_id_1;
    private RelativeLayout rl_id_2;
    private RelativeLayout rl_id_3;
    private RelativeLayout rl_id_4;
    private RelativeLayout rl_id_5;
    private RelativeLayout rl_id_6;

    private TextView tv_id_1;
    private TextView tv_content_1;
    private TextView tv_id_2;
    private TextView tv_content_2;
    private TextView tv_id_3;
    private TextView tv_content_3;
    private TextView tv_id_4;
    private TextView tv_content_4;
    private TextView tv_id_5;
    private TextView tv_content_5;
    private TextView tv_id_6;
    private TextView tv_content_6;

    private TextView tv_recharge_num;

    private int priceId;
    private TextView tv_remain_coin;

    private IWXAPI api;
    private int isFirstRecharge;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_my_game_coin;
    }

    @Override
    protected void initView(View view) {
        view.findViewById(R.id.ll_close).setOnClickListener(this);
        ((TextView) view.findViewById(R.id.tv_bar_title)).setText("我的游戏币");
        view.findViewById(R.id.iv_share).setVisibility(View.GONE);
        TextView tv_cost_record = (TextView) view.findViewById(R.id.tv_cost_record);
        tv_cost_record.setVisibility(View.VISIBLE);
        tv_cost_record.setOnClickListener(this);

        tv_remain_coin = (TextView) view.findViewById(R.id.tv_remain_coin);

        view.findViewById(R.id.ll_coin_recharge).setOnClickListener(this);
        tv_recharge_num = (TextView) view.findViewById(R.id.tv_recharge_num);

        initContentView(view);

        rl_id_1 = (RelativeLayout) view.findViewById(R.id.rl_id_1);
        rl_id_1.setOnClickListener(this);
        rl_id_2 = (RelativeLayout) view.findViewById(R.id.rl_id_2);
        rl_id_2.setOnClickListener(this);
        rl_id_3 = (RelativeLayout) view.findViewById(R.id.rl_id_3);
        rl_id_3.setOnClickListener(this);
        rl_id_4 = (RelativeLayout) view.findViewById(R.id.rl_id_4);
        rl_id_4.setOnClickListener(this);
        rl_id_5 = (RelativeLayout) view.findViewById(R.id.rl_id_5);
        rl_id_5.setOnClickListener(this);
        rl_id_6 = (RelativeLayout) view.findViewById(R.id.rl_id_6);
        rl_id_6.setOnClickListener(this);

        //默认选中
        changeBackgroundColor(rl_id_1, R.color.main_color, tv_id_1, tv_content_1, R.color.pure_white_color);
        changeBackgroundColor(rl_id_2, R.color.pure_white_color, tv_id_2, tv_content_2, R.color.main_text_color);
        changeBackgroundColor(rl_id_3, R.color.pure_white_color, tv_id_3, tv_content_3, R.color.main_text_color);
        changeBackgroundColor(rl_id_4, R.color.pure_white_color, tv_id_4, tv_content_4, R.color.main_text_color);
        changeBackgroundColor(rl_id_5, R.color.pure_white_color, tv_id_5, tv_content_5, R.color.main_text_color);
        changeBackgroundColor(rl_id_6, R.color.pure_white_color, tv_id_6, tv_content_6, R.color.main_text_color);
        tv_recharge_num.setText(String.valueOf(10));
        priceId = 1;
        //注册微信支付
        regToWx();
    }

    private void initContentView(View view) {
        tv_id_1 = (TextView) view.findViewById(R.id.tv_id_1);
        tv_id_2 = (TextView) view.findViewById(R.id.tv_id_2);
        tv_id_3 = (TextView) view.findViewById(R.id.tv_id_3);
        tv_id_4 = (TextView) view.findViewById(R.id.tv_id_4);
        tv_id_5 = (TextView) view.findViewById(R.id.tv_id_5);
        tv_id_6 = (TextView) view.findViewById(R.id.tv_id_6);
        tv_content_1 = (TextView) view.findViewById(R.id.tv_content_1);
        tv_content_2 = (TextView) view.findViewById(R.id.tv_content_2);
        tv_content_3 = (TextView) view.findViewById(R.id.tv_content_3);
        tv_content_4 = (TextView) view.findViewById(R.id.tv_content_4);
        tv_content_5 = (TextView) view.findViewById(R.id.tv_content_5);
        tv_content_6 = (TextView) view.findViewById(R.id.tv_content_6);
    }

    private void changeBackgroundColor(View view, int color, TextView textView1, TextView textView2, int color1) {
        GradientDrawable drawable = (GradientDrawable) view.getBackground();
        drawable.setColor(getResources().getColor(color));
        textView1.setTextColor(getResources().getColor(color1));
        textView2.setTextColor(getResources().getColor(color1));
    }

    @Override
    protected void initData() {
        super.initData();
        //是否首充
        isFirstRecharge();
        //获取剩余币
        getBalance();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_close:
                goBack();
                break;
            case R.id.tv_cost_record:
                gotoPager(SpendCoinRecordFragment.class, null);
                break;
            case R.id.ll_coin_recharge:
                recharge();//充值
                break;
            case R.id.rl_id_1:
                changeBackgroundColor(rl_id_1, R.color.main_color, tv_id_1, tv_content_1, R.color.pure_white_color);
                changeBackgroundColor(rl_id_2, R.color.pure_white_color, tv_id_2, tv_content_2, R.color.main_text_color);
                changeBackgroundColor(rl_id_3, R.color.pure_white_color, tv_id_3, tv_content_3, R.color.main_text_color);
                changeBackgroundColor(rl_id_4, R.color.pure_white_color, tv_id_4, tv_content_4, R.color.main_text_color);
                changeBackgroundColor(rl_id_5, R.color.pure_white_color, tv_id_5, tv_content_5, R.color.main_text_color);
                changeBackgroundColor(rl_id_6, R.color.pure_white_color, tv_id_6, tv_content_6, R.color.main_text_color);
                tv_recharge_num.setText(String.valueOf(10));
                priceId = 1;
                break;
            case R.id.rl_id_2:
                changeBackgroundColor(rl_id_1, R.color.pure_white_color, tv_id_1, tv_content_1, R.color.main_text_color);
                changeBackgroundColor(rl_id_2, R.color.main_color, tv_id_2, tv_content_2, R.color.pure_white_color);
                changeBackgroundColor(rl_id_3, R.color.pure_white_color, tv_id_3, tv_content_3, R.color.main_text_color);
                changeBackgroundColor(rl_id_4, R.color.pure_white_color, tv_id_4, tv_content_4, R.color.main_text_color);
                changeBackgroundColor(rl_id_5, R.color.pure_white_color, tv_id_5, tv_content_5, R.color.main_text_color);
                changeBackgroundColor(rl_id_6, R.color.pure_white_color, tv_id_6, tv_content_6, R.color.main_text_color);
                tv_recharge_num.setText(String.valueOf(20));
                priceId = 2;
                break;
            case R.id.rl_id_3:
                changeBackgroundColor(rl_id_1, R.color.pure_white_color, tv_id_1, tv_content_1, R.color.main_text_color);
                changeBackgroundColor(rl_id_2, R.color.pure_white_color, tv_id_2, tv_content_2, R.color.main_text_color);
                changeBackgroundColor(rl_id_3, R.color.main_color, tv_id_3, tv_content_3, R.color.pure_white_color);
                changeBackgroundColor(rl_id_4, R.color.pure_white_color, tv_id_4, tv_content_4, R.color.main_text_color);
                changeBackgroundColor(rl_id_5, R.color.pure_white_color, tv_id_5, tv_content_5, R.color.main_text_color);
                changeBackgroundColor(rl_id_6, R.color.pure_white_color, tv_id_6, tv_content_6, R.color.main_text_color);
                tv_recharge_num.setText(String.valueOf(50));
                priceId = 3;
                break;
            case R.id.rl_id_4:
                changeBackgroundColor(rl_id_1, R.color.pure_white_color, tv_id_1, tv_content_1, R.color.main_text_color);
                changeBackgroundColor(rl_id_2, R.color.pure_white_color, tv_id_2, tv_content_2, R.color.main_text_color);
                changeBackgroundColor(rl_id_3, R.color.pure_white_color, tv_id_3, tv_content_3, R.color.main_text_color);
                changeBackgroundColor(rl_id_4, R.color.main_color, tv_id_4, tv_content_4, R.color.pure_white_color);
                changeBackgroundColor(rl_id_5, R.color.pure_white_color, tv_id_5, tv_content_5, R.color.main_text_color);
                changeBackgroundColor(rl_id_6, R.color.pure_white_color, tv_id_6, tv_content_6, R.color.main_text_color);
                tv_recharge_num.setText(String.valueOf(100));
                priceId = 4;
                break;
            case R.id.rl_id_5:
                changeBackgroundColor(rl_id_1, R.color.pure_white_color, tv_id_1, tv_content_1, R.color.main_text_color);
                changeBackgroundColor(rl_id_2, R.color.pure_white_color, tv_id_2, tv_content_2, R.color.main_text_color);
                changeBackgroundColor(rl_id_3, R.color.pure_white_color, tv_id_3, tv_content_3, R.color.main_text_color);
                changeBackgroundColor(rl_id_4, R.color.pure_white_color, tv_id_4, tv_content_4, R.color.main_text_color);
                changeBackgroundColor(rl_id_5, R.color.main_color, tv_id_5, tv_content_5, R.color.pure_white_color);
                changeBackgroundColor(rl_id_6, R.color.pure_white_color, tv_id_6, tv_content_6, R.color.main_text_color);
                tv_recharge_num.setText(String.valueOf(300));
                priceId = 5;
                break;
            case R.id.rl_id_6:
                changeBackgroundColor(rl_id_1, R.color.pure_white_color, tv_id_1, tv_content_1, R.color.main_text_color);
                changeBackgroundColor(rl_id_2, R.color.pure_white_color, tv_id_2, tv_content_2, R.color.main_text_color);
                changeBackgroundColor(rl_id_3, R.color.pure_white_color, tv_id_3, tv_content_3, R.color.main_text_color);
                changeBackgroundColor(rl_id_4, R.color.pure_white_color, tv_id_4, tv_content_4, R.color.main_text_color);
                changeBackgroundColor(rl_id_5, R.color.pure_white_color, tv_id_5, tv_content_5, R.color.main_text_color);
                changeBackgroundColor(rl_id_6, R.color.main_color, tv_id_6, tv_content_6, R.color.pure_white_color);
                tv_recharge_num.setText(String.valueOf(500));
                priceId = 6;
                break;
            default:
                break;
        }
    }

    /**
     * 充值
     */
    private void recharge() {
        OkHttpUtils.post()
                .url(Constants.getWeChatPayUrl())
                .addParams(Constants.SESSION, SPUtils.getInstance().getString(Constants.SESSION))
                .addParams("priceId", String.valueOf(priceId))
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
                                handlerWeChatPayData(jsonObjectResBody);
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

    private void handlerWeChatPayData(JSONObject jsonObjectResBody) {
        if (EmptyUtils.isNotEmpty(jsonObjectResBody)) {
            JSONObject jsonObject = jsonObjectResBody.optJSONObject("wxSdkParams");
            if (EmptyUtils.isNotEmpty(jsonObject)) {
                Gson gson = new Gson();
                WeChatPayParamsBean weChatPayParamsBean = gson.fromJson(jsonObject.toString(), WeChatPayParamsBean.class);
                if (EmptyUtils.isNotEmpty(weChatPayParamsBean)) {
                    PayReq request = new PayReq();
                    request.appId = weChatPayParamsBean.getAppid();
                    request.partnerId = weChatPayParamsBean.getPartnerid();
                    request.prepayId = weChatPayParamsBean.getPrepayid();
                    request.packageValue = weChatPayParamsBean.getPackageX();
                    request.nonceStr = weChatPayParamsBean.getNoncestr();
                    request.timeStamp = String.valueOf(weChatPayParamsBean.getTimestamp());
                    request.sign = weChatPayParamsBean.getSign();
                    //发起微信支付
                    api.sendReq(request);
                }
            }
        }
    }

    private void isFirstRecharge() {
        OkHttpUtils.get()
                .url(Constants.getIsFirstRechargeUrl())
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
                                int success = jsonObjectResBody.optInt("success");
                                if (success == 1) {
                                    isFirstRecharge = jsonObjectResBody.optInt("isFirstRecharge");
                                    //0：不是；1：是
                                    resetRechargePriceListData();
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

    private void resetRechargePriceListData() {
        switch (isFirstRecharge) {
            case 0://不是
                tv_content_1.setText("充10元送10币");
                tv_content_2.setText("充20元送20币");
                tv_content_3.setText("充50元送50币");
                tv_content_4.setText("充100元送100币");
                tv_content_5.setText("充300元送300币");
                tv_content_6.setText("充500元送500币");
                break;
            case 1:
                tv_content_1.setText("首充10元送60币");
                tv_content_2.setText("首充20元送150币");
                tv_content_3.setText("首充50元送450币");
                tv_content_4.setText("首充100元送950币");
                tv_content_5.setText("首充300元送2950币");
                tv_content_6.setText("首充500元送4950币");
                break;
            default:
                break;
        }
    }

    private void getBalance() {
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
                                tv_remain_coin.setText(String.valueOf(balance));
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

    private void regToWx() {
        api = WXAPIFactory.createWXAPI(getActivity(), Constants.APP_ID, false);//false：表示checkSignature
        api.registerApp(Constants.APP_ID);
    }

    @Override
    public void onResume() {
        super.onResume();
        String recharge_success = (String) DataManager.getInstance().getData1();
        if (EmptyUtils.isNotEmpty(recharge_success) && recharge_success.equals("RECHARGE_SUCCESS")) {
            //充值成功
            String remain_coin_string = tv_remain_coin.getText().toString();
            int remain_coin = Integer.valueOf(remain_coin_string);
            int recharge_coin = 0;
            if (isFirstRecharge == 1) {//是
                switch (priceId) {
                    case 1:
                        recharge_coin = 160;
                        break;
                    case 2:
                        recharge_coin = 350;
                        break;
                    case 3:
                        recharge_coin = 950;
                        break;
                    case 4:
                        recharge_coin = 1950;
                        break;
                    case 5:
                        recharge_coin = 5950;
                        break;
                    case 6:
                        recharge_coin = 9950;
                        break;
                    default:
                        break;
                }
            } else if (isFirstRecharge == 0) {//不是
                switch (priceId) {
                    case 1:
                        recharge_coin = 110;
                        break;
                    case 2:
                        recharge_coin = 220;
                        break;
                    case 3:
                        recharge_coin = 550;
                        break;
                    case 4:
                        recharge_coin = 1100;
                        break;
                    case 5:
                        recharge_coin = 3300;
                        break;
                    case 6:
                        recharge_coin = 5500;
                        break;
                    default:
                        break;
                }
            }
            int remain_coin_total = remain_coin + recharge_coin;
            tv_remain_coin.setText(String.valueOf(remain_coin_total));

            DataManager.getInstance().setData1(null);
        }
    }
}
