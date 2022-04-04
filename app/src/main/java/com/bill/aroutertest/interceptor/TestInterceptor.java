package com.bill.aroutertest.interceptor;

import android.content.Context;
import android.util.Log;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.annotation.Interceptor;
import com.alibaba.android.arouter.facade.callback.InterceptorCallback;
import com.alibaba.android.arouter.facade.template.IInterceptor;

/**
 * Created by Bill on 2022/4/4.
 */

@Interceptor(priority = 2)
public class TestInterceptor implements IInterceptor {

    @Override
    public void process(Postcard postcard, InterceptorCallback callback) {
        Log.d("Bill", "TestInterceptor process :" + postcard);
        callback.onContinue(postcard);
    }

    @Override
    public void init(Context context) {
        Log.e("Bill", "TestInterceptor init");
    }
}
