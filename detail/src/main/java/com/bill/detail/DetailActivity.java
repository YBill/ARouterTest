package com.bill.detail;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bill.baselib.bean.ListBean;
import com.bill.baselib.path.ARouterPath;

import androidx.appcompat.app.AppCompatActivity;

@Route(path = ARouterPath.PATH_DETAIL)
public class DetailActivity extends AppCompatActivity {

    private FrameLayout mWebViewGroup;
    private ProgressBar mProgressBar;
    private WebView mWebView;

    @Autowired(name = "bean", required = true)
    public ListBean mListBean;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ARouter.getInstance().inject(this);
        mWebViewGroup = findViewById(R.id.fragment_container);
        mProgressBar = findViewById(R.id.pb_detail);
        findViewById(R.id.view_say).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance()
                        .build(ARouterPath.PATH_COMMENT)
                        .withInt("type", 1)
                        .withString("contentId", mListBean == null ? "" : mListBean.id)
                        .navigation();
            }
        });

        init();

        if (mListBean == null)
            mWebView.loadUrl("https://www.baidu.com/");
        else
            mWebView.loadUrl("https://www.baidu.com/s?wd=" + mListBean.title);
    }

    private void init() {
        mWebView = new WebView(getApplicationContext());
        mWebViewGroup.addView(mWebView, 0);

        WebSettings mWebSettings = mWebView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setDomStorageEnabled(true);


        //设置不用系统浏览器打开,直接显示在当前WebView
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        //设置WebChromeClient类
        mWebView.setWebChromeClient(new WebChromeClient() {


            //获取网站标题
            @Override
            public void onReceivedTitle(WebView view, String title) {

            }


            //获取加载进度
            @Override
            public void onProgressChanged(WebView view, int newProgress) {

            }
        });

        //设置WebViewClient类
        mWebView.setWebViewClient(new WebViewClient() {

            //设置加载前的函数
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                mProgressBar.setVisibility(View.VISIBLE);
            }

            //设置结束加载函数
            @Override
            public void onPageFinished(WebView view, String url) {
                mProgressBar.setVisibility(View.GONE);
            }
        });

    }

    //点击返回上一页面而不是退出浏览器
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    //销毁WebView
    @Override
    protected void onDestroy() {
        if (mWebView != null) {
            mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mWebView.clearHistory();

            ((ViewGroup) mWebView.getParent()).removeView(mWebView);
            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroy();
    }
}