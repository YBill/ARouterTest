package com.bill.aroutertest.service;

import android.content.Context;
import android.util.Log;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.facade.service.DegradeService;
import com.bill.baselib.path.ARouterPath;

/**
 * Created by Bill on 2022/4/4.
 */

@Route(path = ARouterPath.PATH_DEGRADE)
public class DegradeServiceImpl implements DegradeService {

    @Override
    public void onLost(Context context, Postcard postcard) {
        Log.d("Bill", "DegradeServiceImpl onLost :" + postcard);
    }

    @Override
    public void init(Context context) {
        Log.e("Bill", "DegradeServiceImpl init");
    }
}
