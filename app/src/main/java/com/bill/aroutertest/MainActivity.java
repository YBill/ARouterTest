package com.bill.aroutertest;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.callback.NavCallback;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bill.baselib.path.ARouterPath;
import com.bill.baselib.util.ToastUtil;
import com.bill.mine.service.ILoginService;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String ATTENTION_TAG = "AttentionFragment";
    private static final String RECOMMEND_TAG = "RecommendFragment";

    private static final int REQUEST_CODE_ATTENTION_LIST = 100;
    private static final int REQUEST_CODE_MY_COMMENT_LIST = 101;

    private AppCompatTextView attentionTv;
    private AppCompatTextView recommendTv;

    private Fragment mCurrentFragment;

    @Autowired
    ILoginService loginService;
    @Autowired(name = ARouterPath.PATH_LOGIN_SERVICE_IMPL)
    ILoginService loginService2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ARouter.getInstance().inject(this);
        init();
        handleTopTab(RECOMMEND_TAG);
    }

    private void init() {
        attentionTv = findViewById(R.id.tv_attention);
        recommendTv = findViewById(R.id.tv_recommend);
        attentionTv.setOnClickListener(this);
        recommendTv.setOnClickListener(this);
    }

    private void handleTopTab(String tag) {
        if (ATTENTION_TAG.equals(tag) &&
                !isLogin()) {
            ARouter.getInstance()
                    .build(ARouterPath.PATH_LOGIN)
                    .navigation(this, REQUEST_CODE_ATTENTION_LIST);
            return;
        }

        if (ATTENTION_TAG.equals(tag)) {
            attentionTv.setTextColor(Color.RED);
            recommendTv.setTextColor(Color.BLACK);
        } else if (RECOMMEND_TAG.equals(tag)) {
            attentionTv.setTextColor(Color.BLACK);
            recommendTv.setTextColor(Color.RED);
        }

        switchContent(tag);
    }

    /**
     * 依赖注入测试
     */
    private boolean isLogin() {
        Log.v("Bill", "loginService = " + (loginService == null ? "null" : loginService.isLogin()));
        Log.v("Bill", "loginService2 = " + (loginService2 == null ? "null" : loginService2.isLogin()));

        // 通过byType的方式发现服务
        boolean isLogin1 = ARouter.getInstance().navigation(ILoginService.class).isLogin();
        // 通过byName的方式
        boolean isLogin2 = ((ILoginService) ARouter.getInstance().build(ARouterPath.PATH_LOGIN_SERVICE_IMPL).navigation()).isLogin();
        boolean isLogin3 = ((ILoginService) ARouter.getInstance().build(ARouterPath.PATH_LOGIN_SERVICE_IMPL_2).navigation()).isLogin();

        Log.v("Bill", "isLogin2 = " + isLogin1);
        Log.v("Bill", "isLogin3 = " + isLogin2);
        Log.v("Bill", "isLogin4 = " + isLogin3);

        return isLogin1;
    }

    private void switchContent(String tag) {
        Fragment toFragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (toFragment == null) {
            toFragment = getFragment(tag);
            if (toFragment == null) return;
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.fragment_container, toFragment, tag);
            if (mCurrentFragment != null) {
                fragmentTransaction.hide(mCurrentFragment);
            }
            fragmentTransaction.commitAllowingStateLoss();
            mCurrentFragment = toFragment;
        } else {
            if (mCurrentFragment != toFragment) {
                FragmentTransaction fmt = getSupportFragmentManager().beginTransaction();
                if (mCurrentFragment != null) {
                    fmt.hide(mCurrentFragment);
                }
                fmt.show(toFragment);
                fmt.commitAllowingStateLoss();
                mCurrentFragment = toFragment;
            }
        }
    }

    private Fragment getFragment(String tag) {
        if (ATTENTION_TAG.equals(tag)) {
            Fragment fragment = (Fragment) ARouter.getInstance().build(ARouterPath.PATH_LIST)
                    .withString("type", "attention")
                    .navigation();
            return fragment;
        } else if (RECOMMEND_TAG.equals(tag)) {
            Fragment fragment = (Fragment) ARouter.getInstance().build(ARouterPath.PATH_LIST)
                    .withString("type", "recommend")
                    .navigation();
            return fragment;
        }
        return null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_attention:
                handleTopTab(ATTENTION_TAG);
                break;
            case R.id.tv_recommend:
                handleTopTab(RECOMMEND_TAG);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ATTENTION_LIST) {
            if (resultCode == 1) {
                handleTopTab(ATTENTION_TAG);
            } else {
                ToastUtil.toast("未登录");
            }
        } else if (requestCode == REQUEST_CODE_MY_COMMENT_LIST) {
            if (resultCode == 1) {
                ARouter.getInstance()
                        .build(ARouterPath.PATH_COMMENT)
                        .withInt("type", 2)
                        .navigation();

            } else {
                ToastUtil.toast("未登录");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
            case R.id.setting:
                ARouter.getInstance()
                        .build(ARouterPath.PATH_SETTING)
                        .navigation();
                break;
            case R.id.my_comment:
                ARouter.getInstance()
                        .build(ARouterPath.PATH_COMMENT)
                        .withInt("type", 2)
                        .navigation(this, new NavCallback() {
                            @Override
                            public void onArrival(Postcard postcard) {
                                Log.d("Bill", "onArrival");
                            }

                            @Override
                            public void onInterrupt(Postcard postcard) {
                                Log.d("Bill", "onInterrupt");
                                ARouter.getInstance()
                                        .build(ARouterPath.PATH_LOGIN)
                                        .navigation(MainActivity.this, REQUEST_CODE_MY_COMMENT_LIST);
                            }
                        });
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}