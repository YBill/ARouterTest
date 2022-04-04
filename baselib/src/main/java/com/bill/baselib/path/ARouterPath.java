package com.bill.baselib.path;

/**
 * Created by Bill on 2022/4/3.
 */

public class ARouterPath {

    /**
     * 不同的module组名不能相同，否则运行不起来，报错：ARouter&&Group$$xxx is defined multiple times
     */

    public static final String PATH_PRETREATMENT ="/app/pretreatment";
    public static final String PATH_DEGRADE ="/app/degrade";

    public static final String PATH_LIST ="/list/news";

    public static final String PATH_DETAIL ="/detail/news";
    public static final String PATH_SERVICE_OBJ ="/detail/service/obj";

    public static final String PATH_COMMENT ="/comment/news";

    public static final String PATH_LOGIN ="/mine/login";
    public static final String PATH_SETTING ="/mine/setting";
    public static final String PATH_LOGIN_SERVICE_IMPL ="/mine/service_impl";
    public static final String PATH_LOGIN_SERVICE_IMPL_2 ="/mine/service_impl_2";

}
