package com.meng.duo.clip.doll.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.meng.duo.clip.doll.R;


/**
 * Created by Devin on 2017/12/5 17:12
 * E-mail:971060378@qq.com
 */

public class ClipDollResultPopupWindow extends PopupWindow implements View.OnClickListener {

    private ClipYesPopupNumListener listener;
    protected Context context;

    public interface ClipYesPopupNumListener {

        void onCancelClicked();

        void onGoToInviteClicked();

        void onTryAgingClicked();
    }

    public ClipDollResultPopupWindow(Context context, ClipYesPopupNumListener listener) {
        super(context);
        setContentView(LayoutInflater.from(context).inflate(R.layout.clip_doll_result_popup_window, null));
        this.listener = listener;
        this.context = context;
        // initView(morePopup, type);
        // setWindowLayoutMode(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
        setFocusable(true);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        //设置弹出窗体需要软键盘，
        this.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        //再设置模式，和Activity的一样，覆盖，调整大小。
        this.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        // 设置SelectPicPopupWindow弹出窗体可点击
        // this.setFocusable(true);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        // 实例化一个ColorDrawable颜色为半透明
        // ColorDrawable dw = new ColorDrawable(0xb0000000);
        // 设置SelectPicPopupWindow弹出窗体的背景
        setAnimationStyle(R.style.PopupWindowAnimation);
        this.setBackgroundDrawable(null);
        // mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        getContentView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int height = getMoreMenuView().getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });

        getContentView().setFocusableInTouchMode(true); // 设置view能够接听事件 标注2
        getContentView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
                if (arg1 == KeyEvent.KEYCODE_BACK) {
                    if (this != null) {
                        dismiss();
                    }
                }
                return false;
            }
        });
    }


    public void initView() {
        getContentView().findViewById(R.id.iv_cancel).setOnClickListener(this);
        getContentView().findViewById(R.id.btn_go_to_invite).setOnClickListener(this);
        getContentView().findViewById(R.id.btn_try_aging).setOnClickListener(this);
    }


    public View getViewG() {
        return getContentView().findViewById(R.id.view_bg);
    }


    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
        viewShowLocation();
    }

    protected void viewShowLocation() {
        ObjectAnimator fadeAnim = ObjectAnimator.ofFloat(getViewG(), "alpha", 0.5f).setDuration(250);
        fadeAnim.setStartDelay(250);
        fadeAnim.start();
    }

    @Override
    public void update() {
        super.update();
    }

    private RelativeLayout getMoreMenuView() {
        return (RelativeLayout) getContentView().findViewById(R.id.pop_layout);
    }


    @Override
    public void dismiss() {
        ObjectAnimator fadeAnim = ObjectAnimator.ofFloat(getViewG(), "alpha", 0f).setDuration(250);
        fadeAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }

            public void onAnimationEnd(Animator animation) {
                ClipDollResultPopupWindow.super.dismiss();
            }
        });
        fadeAnim.start();
    }

    @Override
    public void onClick(View v) {
        dismiss();
        switch (v.getId()) {
            case R.id.iv_cancel:
                if (listener != null) {
                    listener.onCancelClicked();
                }
                break;
            case R.id.btn_go_to_invite:
                if (listener != null) {
                    listener.onGoToInviteClicked();
                }
                break;
            case R.id.btn_try_aging:
                if (listener != null) {
                    listener.onTryAgingClicked();
                }
                break;
            default:
                break;
        }
    }
}
