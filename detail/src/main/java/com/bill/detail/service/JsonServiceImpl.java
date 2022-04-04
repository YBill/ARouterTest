package com.bill.detail.service;

import android.content.Context;
import android.util.Log;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.facade.service.SerializationService;
import com.alibaba.fastjson.JSON;
import com.bill.baselib.path.ARouterPath;

import java.lang.reflect.Type;

/**
 * Created by Bill on 2022/4/4.
 */

@Route(path = ARouterPath.PATH_SERVICE_OBJ)
public class JsonServiceImpl implements SerializationService {

    @Override
    public <T> T json2Object(String input, Class<T> clazz) {
        return JSON.parseObject(input, clazz);
    }

    @Override
    public String object2Json(Object instance) {
        return JSON.toJSONString(instance);
    }

    @Override
    public <T> T parseObject(String input, Type clazz) {
        return JSON.parseObject(input, clazz);
    }

    @Override
    public void init(Context context) {
        Log.e("Bill", "JsonServiceImpl init");
    }
}
