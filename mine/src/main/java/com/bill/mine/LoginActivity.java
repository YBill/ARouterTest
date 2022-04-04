package com.bill.mine;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.bill.baselib.data.LoginData;
import com.bill.baselib.path.ARouterPath;
import com.bill.baselib.util.ThreadPool;

import androidx.appcompat.app.AppCompatActivity;

@Route(path = ARouterPath.PATH_LOGIN)
public class LoginActivity extends AppCompatActivity {

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        progressBar = findViewById(R.id.pb_login);
    }

    public void handleLogin(View view) {
        progressBar.setVisibility(View.VISIBLE);

        ThreadPool.getInstance().executeMain(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                LoginData.setLogin(true);
                close();
            }
        }, 2000);

    }

    private void close() {
        setResult(1);
        finish();
    }

    public void handleUnLogin(View view) {
        LoginData.setLogin(false);
        finish();
    }
}