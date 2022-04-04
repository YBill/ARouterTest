package com.bill.mine.intercept;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.annotation.Interceptor;
import com.alibaba.android.arouter.facade.callback.InterceptorCallback;
import com.alibaba.android.arouter.facade.template.IInterceptor;
import com.bill.baselib.data.LoginData;
import com.bill.baselib.path.ARouterPath;

/**
 * Created by Bill on 2022/4/3.
 */

@Interceptor(priority = 5)
public class LoginInterceptor implements IInterceptor {

    @Override
    public void process(Postcard postcard, InterceptorCallback callback) {
        Log.d("Bill", "LoginInterceptor process :" + postcard);

        if (ARouterPath.PATH_COMMENT.equals(postcard.getPath())) {
            Bundle bundle = postcard.getExtras();
            if (bundle != null && bundle.getInt("type") == 2 && !LoginData.isLogin()) {
                callback.onInterrupt(null);
            } else
                callback.onContinue(postcard);
        } else {
            callback.onContinue(postcard);
        }
    }

    @Override
    public void init(Context context) {
        Log.e("Bill", "LoginInterceptor init");
    }
}
