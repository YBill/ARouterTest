package com.bill.aroutertest.service;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.facade.service.PathReplaceService;
import com.bill.baselib.path.ARouterPath;

/**
 * Created by Bill on 2022/4/10.
 */

@Route(path = ARouterPath.PATH_PATHREPLACE)
public class PathReplaceServiceImpl implements PathReplaceService {

    @Override
    public String forString(String path) {
        return path;
    }

    @Override
    public Uri forUri(Uri uri) {
        return uri;
    }

    @Override
    public void init(Context context) {
        Log.e("Bill", "PathReplaceServiceImpl init");
    }
}
