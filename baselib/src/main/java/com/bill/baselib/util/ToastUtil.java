package com.bill.baselib.util;

import android.widget.Toast;

import com.bill.baselib.BaseApplication;

/**
 * Created by Bill on 2022/4/4.
 */

public class ToastUtil {

    private static Toast mToast;

    public static void toast(String msg) {
        if (mToast == null) {
            mToast = Toast.makeText(BaseApplication.getContext(), "", Toast.LENGTH_SHORT);
        }
        mToast.setText(msg);
        mToast.show();
    }


}
