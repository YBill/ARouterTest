package com.bill.mine.service;

import android.content.Context;
import android.util.Log;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.bill.baselib.data.LoginData;
import com.bill.baselib.path.ARouterPath;

/**
 * Created by Bill on 2022/4/4.
 */

@Route(path = ARouterPath.PATH_LOGIN_SERVICE_IMPL)
public class LoginServiceImpl implements ILoginService {

    @Override
    public void init(Context context) {
        Log.e("Bill", "LoginServiceImpl init");
    }

    @Override
    public boolean isLogin() {
        Log.i("Bill", "LoginServiceImpl isLogin = " + LoginData.isLogin());
        return LoginData.isLogin();
    }
}
