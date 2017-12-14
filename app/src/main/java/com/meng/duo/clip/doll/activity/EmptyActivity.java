package com.meng.duo.clip.doll.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;

import com.blankj.utilcode.util.BarUtils;
import com.meng.duo.clip.doll.BaseApplication;
import com.meng.duo.clip.doll.R;
import com.meng.duo.clip.doll.fragment.BaseFragment;
import com.meng.duo.clip.doll.util.Constants;

/**
 * Created by Devin on 2017/12/11 10:24
 * E-mail:971060378@qq.com
 */

public class EmptyActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BarUtils.setStatusBarColor(EmptyActivity.this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.activity_empty);
        onNewIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        String fragmentName = intent.getStringExtra(Constants.FRAGMENT_NAME);
        BaseFragment fragment = (BaseFragment) Fragment.instantiate(this, fragmentName);
        Bundle bundle = intent.getExtras();
        fragment.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment currentFragment = getVisibleFragment();
        if (currentFragment != null) {
            ft.hide(currentFragment);
        }
        ft.add(R.id.container, fragment, fragmentName);
        ft.addToBackStack(null);
        ft.commitAllowingStateLoss();
        BaseApplication.setCurFragment(fragment);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (getVisibleFragment() != null) {
                getVisibleFragment().goBack();
            } else {
                finish();
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}