package com.bill.aroutertest.service;

import android.content.Context;
import android.util.Log;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.facade.service.PretreatmentService;
import com.bill.baselib.path.ARouterPath;

/**
 * Created by Bill on 2022/4/4.
 * 预处理服务，拦截器也会走这里
 */

@Route(path = ARouterPath.PATH_PRETREATMENT)
public class PretreatmentServiceImpl implements PretreatmentService {

    @Override
    public boolean onPretreatment(Context context, Postcard postcard) {
        Log.d("Bill", "PretreatmentServiceImpl onPretreatment :" + postcard);
        return true; // 返回false就拦截了
    }

    @Override
    public void init(Context context) {
        Log.e("Bill", "PretreatmentServiceImpl init");
    }

}
