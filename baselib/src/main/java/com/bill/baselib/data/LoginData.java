package com.bill.baselib.data;

/**
 * Created by Bill on 2022/4/3.
 */

public class LoginData {

    private static boolean isLogin = false;

    public static boolean isLogin() {
        return isLogin;
    }

    public static void setLogin(boolean isLogin) {
        LoginData.isLogin = isLogin;
    }

}
