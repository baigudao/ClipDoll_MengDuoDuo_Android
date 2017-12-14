package com.meng.duo.clip.doll.fragment;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.EmptyUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meng.duo.clip.doll.R;
import com.meng.duo.clip.doll.adapter.BaseRecyclerViewAdapter;
import com.meng.duo.clip.doll.bean.GuestStateClipDollInfoBean;
import com.meng.duo.clip.doll.bean.LiveRoomLuckyUserBean;
import com.meng.duo.clip.doll.util.Constants;
import com.meng.duo.clip.doll.util.DataManager;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Call;


/**
 * Created by Devin on 2017/12/9 19:40
 * E-mail:971060378@qq.com
 */

public class GuestStateFragment extends BaseFragment {

    private static final int CLIP_DOLL_RECORD_USER_DATA_TYPE = 6;
    private RecyclerView recyclerView;

    private TextView tv_clip_doll_num;
    private LiveRoomLuckyUserBean.UserBean userBean;

    private ImageView iv_user_photo;
    private TextView tv_user_name;
    private TextView tv_id_num;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_guest_state;
    }

    @Override
    protected void initView(View view) {
        view.findViewById(R.id.ll_close).setOnClickListener(this);
        ((TextView) view.findViewById(R.id.tv_bar_title)).setText("");
        view.findViewById(R.id.iv_share).setVisibility(View.GONE);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        iv_user_photo = (ImageView) view.findViewById(R.id.iv_user_photo);
        tv_user_name = (TextView) view.findViewById(R.id.tv_user_name);
        tv_id_num = (TextView) view.findViewById(R.id.tv_id_num);

        tv_clip_doll_num = (TextView) view.findViewById(R.id.tv_clip_doll_num);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_close:
                goBack();
                break;
            default:
                break;
        }
    }

    @Override
    protected void initData() {
        super.initData();
        userBean = (LiveRoomLuckyUserBean.UserBean) DataManager.getInstance().getData1();
        DataManager.getInstance().setData1(null);
        if (EmptyUtils.isNotEmpty(userBean)) {
            String headImage = userBean.getHeadImg();
            if (EmptyUtils.isNotEmpty(headImage)) {
                Glide.with(mContext)
                        .load(headImage)
                        .placeholder(R.drawable.avatar)
                        .error(R.drawable.avatar)
                        .into(iv_user_photo);
            }
            tv_user_name.setText(userBean.getNickName());
            tv_id_num.setText("ID:" + userBean.getUserId());
            //请求列表数据
            getDataFromNet();
        }
    }

    private void getDataFromNet() {
        //获取抓取记录，幸运儿
        OkHttpUtils.post()
                .url(Constants.getClipDollRecordUrl())
                .addParams(Constants.PAGENUM, "1")
                .addParams(Constants.PAGESIZE, "10")
                .addParams(Constants.RESULT, "1")
                .addParams(Constants.USERID, String.valueOf(userBean.getUserId()))
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
                                handlerDataForGuestClipDollLucky(jsonObjectResBody);
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

    private void handlerDataForGuestClipDollLucky(JSONObject jsonObjectResBody) {
        if (EmptyUtils.isNotEmpty(jsonObjectResBody)) {
            int dataTotal = jsonObjectResBody.optInt("dataTotal");
            tv_clip_doll_num.setText(String.valueOf(dataTotal));
            JSONArray jsonArrayForLuckyUsers = jsonObjectResBody.optJSONArray("pageData");
            if (EmptyUtils.isNotEmpty(jsonArrayForLuckyUsers)) {
                Gson gson = new Gson();
                ArrayList<GuestStateClipDollInfoBean> liveRoomLuckyUserBeanArrayList = gson.fromJson(jsonArrayForLuckyUsers.toString()
                        , new TypeToken<ArrayList<GuestStateClipDollInfoBean>>() {
                        }.getType());
                if (EmptyUtils.isNotEmpty(liveRoomLuckyUserBeanArrayList) && liveRoomLuckyUserBeanArrayList.size() != 0) {
                    BaseRecyclerViewAdapter baseRecyclerViewAdapter = new BaseRecyclerViewAdapter(mContext, liveRoomLuckyUserBeanArrayList, CLIP_DOLL_RECORD_USER_DATA_TYPE);
                    recyclerView.setAdapter(baseRecyclerViewAdapter);
                    recyclerView.setLayoutManager(new GridLayoutManager(mContext, 2, LinearLayoutManager.VERTICAL, false));
                }
            }
        }
    }
}
