ARouter

#### **一、为什么使用 ARouter**

##### **1、介绍**

在项目开发中，经常会拆分多个 module，各个 module 间应该相互独立，即使有引用也应该是单向依赖；那么多个 module 间不相互依赖，多个 module 间如何关联呢；尤其是在组件化项目中，在没有依赖的情况下是如何完成 Activity 的跳转的，ARouter 可以帮助我们实现。

##### **2、组件化例子**

[组件化](https://github.com/YBill/ARouterTest/blob/master/Component-README.md)

#### **二、介绍**

##### **0、文档**

[介绍](https://developer.aliyun.com/article/71687)

[README](https://github.com/alibaba/ARouter/blob/master/README_CN.md)

##### **1、功能介绍**

1. 支持直接解析标准URI进行跳转，并自动注入参数到目标页面中
2. 支持多模块工程使用
3. 支持添加多个拦截器，自定义拦截顺序
4. 支持依赖注入，可单独作为依赖注入框架使用
5. 支持InstantRun
6. 支持MultiDex(Google方案)
7. 映射关系按组分类、多级管理，按需初始化
8. 支持用户指定全局降级与局部降级策略
9. 页面、拦截器、服务等组件均自动注册到框架
10. 支持多种方式配置转场动画
11. 支持获取Fragment
12. 完全支持Kotlin以及混编(配置见文末 其他#5)
13. 支持第三方 App 加固(使用 arouter-register 实现自动注册)
14. 支持生成路由文档
15. 提供 IDE 插件便捷的关联路径和目标类
16. 支持增量编译(开启文档生成后无法增量编译)
17. 支持动态注册路由信息

#### **三、引入**

##### **1、添加依赖**

- 基础依赖

在项目需要使用 ARouter 的各个 module 的 build.gradle 下添加

```Java
 android {
     defaultConfig {
         ...
         javaCompileOptions {
             annotationProcessorOptions {
                 arguments = [AROUTER_MODULE_NAME: project.getName()]
             }
         }
     }
 }
 
 dependencies {
     ...
     implementation 'com.alibaba:arouter-api:x.x.x'
     annotationProcessor 'com.alibaba:arouter-compiler:x.x.x'
 }
```

- 使用 Gradle 插件实现路由表的自动加载 (可选)

> 通过 ARouter 提供的注册插件进行路由表的自动加载， 默认通过扫描 dex 的方式 进行加载通过 gradle 插件，进行自动注册可以缩短初始化时间解决应用加固导致无法直接访问 dex 文件，初始化失败的问题

在项目的 build.gradle 下添加

```Java
 buildscript {
     repositories {
         mavenCentral()
     }
 
     dependencies {
         classpath "com.alibaba:arouter-register:x.x.x"
     }
 }
```

并在主 module 下添加

```Java
 apply plugin: 'com.alibaba.arouter'
```

- 使用 IDE 插件导航到目标类 (可选)

> 在 Android Studio 中搜索插件

`ARouter Helper`, 或者去 Arouter 的 Github 中找提供的 zip 安装包手动安装，安装后可以在跳转代码的行首找到一个图标 ()点击该图标，即可跳转到标识了代码中路径的目标类

- 生成路由文档（可选）

```Java
 javaCompileOptions {
     annotationProcessorOptions {
           arguments = [
               AROUTER_MODULE_NAME: project.getName(), 
               AROUTER_GENERATE_DOC: "enable"
           ]
     }
 }
```

编译后会在

`com.alibaba.android.arouter.docs` 下生成 `arouter-map-of-app.json`  文件，内容大致如下：

```Java
 {
   "test":[
     {
       "group":"test",
       "path":"/test/activity",
       "className":"com.bill.myaroutertest.TestActivity",
       "type":"activity",
       "mark":-2147483648,
       "params":[
         {
           "key":"name",
           "type":"string",
           "required":false
         }
       ]
     }
   ],
   "provider":[
     {
       "group":"provider",
       "path":"/provider/test",
       "prototype":"null, com.alibaba.android.arouter.facade.template.IProvider",
       "className":"com.bill.myaroutertest.TestProvider",
       "type":"provider",
       "mark":-2147483648
     }
   ]
 }
```

##### **2、初始化**

尽可能早初始化，最好在 Application 中初始化

```Java
 if (BuildConfig.DEBUG) {
     // 这两行必须写在init之前，否则这些配置在init过程中将无效
     ARouter.openLog(); // 打印日志
     ARouter.openDebug(); // 开启调试模式(主要处理InstantRun模式下init情况)
 }
 ARouter.init(application);
```

##### **3、代码混淆**

```Java
 -keep public class com.alibaba.android.arouter.routes.**{*;}
 -keep public class com.alibaba.android.arouter.facade.**{*;}
 -keep class * implements com.alibaba.android.arouter.facade.template.ISyringe{*;}
 
 # 如果使用了 byType 的方式获取 Service，需添加下面规则，保护接口
 -keep interface * implements com.alibaba.android.arouter.facade.template.IProvider
 
 # 如果使用了 单类注入，即不定义接口实现 IProvider，需添加下面规则，保护实现
 -keep class * implements com.alibaba.android.arouter.facade.template.IProvider
```

#### **四、使用**

##### **0、注解类型**

**Route** 注解作用在类上，必须是 `public` 修饰的，且 path 必须是 `/` 开头，一般情况 path 至少需要两级，即 `/xx/xx`；目前只支持作用在下面三个类上：

- Activity
- Fragment
- IProvider
- Service

```Java
 @Route(path = "/test/xxx")
 public class TestProvider extends Activity/Fragment {}
 
 @Route(path = "/provider/test")
 public class TestProvider implements IProvider {
     @Override
     public void init(Context context) {}
 }
```

**Interceptor** 注解只能在 `IInterceptor` 类上使用，必须是 `public` 修饰的，必须指定优先级`priority` ，且如果多个不能相同；

```Java
 @Interceptor(priority = 100)
 public class TestInterceptor implements IInterceptor {
     @Override
     public void process(Postcard postcard, InterceptorCallback callback) {}
     @Override
     public void init(Context context) {}
 }
```

**Autowired** 注解在属性上使用，且所修饰属性不能是 `private` 的，使用前需要调用 `ARouter.getInstance().inject(this);` 注入；根据所修饰的属性类型不同，分下面两种情况：

- IProvider 类型：可以用在任何地方；如果指定 name，则通过 byName 的方式获取；不指定则通过 byType 的方式获取；
- 其他类型：必须在 Activity 或 Fragment 中使用；如果指定 name，则指定 name 为传值的 key，不指定则以属性的名字作为 key；支持 boolean、byte、short、int、long、chat、float、double、String、Serializable、Parcelable 以及 Object 类型；

**注意**：在 Fragment 中使用时不传值时，getArguments() 会报空指针；使用 Object 传值时需要实现 `SerializationService` 接口处理；

```Java
 @Route(path = "/test/xxx")
 public class TestProvider extends Activity/Fragment {
     @Autowired(name = "id")
     public String mId;
     
     @Autowired
     IService service1;
     
     @Autowired(name = "/service/login")
     IServiceImpl service2;
     
     public void onCreate() {
       ARouter.getInstance().inject(this);
     }
 }
```

##### **1、 路由操作**

添加注解

```Java
 // 这里的路径需要注意的是至少需要有两级，/xx/xx
 @Route(path = "/test/activity")
 public class TestActivity extend Activity {
     ...
 }
```

发起路由操作

```Java
 ARouter.getInstance().build("/test/activity").navigation(activity?);
```

发起路由并传值

```Java
 ARouter.getInstance().build("/test/activity")
        .withLong("key1", 666L)
        .withString("key3", "888")
        .withBundle("bundle", new Bundle())
        .withSerializable("ser", serializable)
        .withParcelable("pac", parcelable)
        .withObject("key4", new Test())
        .navigation(activity?);
```

添加注解并指定 group，不推荐指定 group

```Java
 // 添加注解，并指定 group
 @Route(path = "/activity", group = "test")
 public class TestActivity extend Activity {
     ...
 }
 
 // 发起路由操作
 // @Route 指定 group，则发起路由时需要指定 group
 ARouter.getInstance().build("/activity", "test").navigation();
```

##### **2、解析参数**

> 解析参数需要注意的是使用 withObject 传递 实现了 Serializable 或 Parcelable 接口的实现类的时候（如ArrayList），接收该对象的地方不能标注具体的实现类类型，应仅标注为没实现 Serializable 或 Parcelable 的接口（如 List），否则会优先使用 Serializable 或 Parcelable 传递值。

```Java
 @Route(path = "/test/activity")
 public class TestActivity extends Activity {
       @Autowired
       public String name;
       @Autowired
       int age;
       @Autowired(name = "girl") 
       boolean boy;
       @Autowired
       TestObj obj; // 处理 SerializationService
       @Autowired
       List<TestObj> list;
       @Autowired
       Map<String, List<TestObj>> map;
     
       @Override
       protected void onCreate(Bundle savedInstanceState) {
           super.onCreate(savedInstanceState);
           ARouter.getInstance().inject(this);
 
           // ARouter 会自动对字段进行赋值，无需主动获取
           Log.d("param", name + age + boy);
       }
 }
```

##### **3、使用** `**startActivityForResult**` **跳转**

```Java
 ARouter.getInstance().build("/test/activity")
        .navigation(activity, 100);
```

##### **4、添加 Flags 和 Action**

```Java
 ARouter.getInstance().build("/test/activity")
        .withFlags(flags) // setFlags
        .addFlags(flags) // add Flags
        .withAction(action) // set Action
        .navigation(activity?);
```

##### **5、设置转场动画**

```Java
 // 转场动画(常规方式:overridePendingTransition)
 ARouter.getInstance().build("/test/activity")
        .withTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom)
        .navigation(activity);
        
 // 转场动画(API16+:共享元素)
 ARouter.getInstance().build("/test/activity")
        .withOptionsCompat(compat)
        .navigation(activity);      
```

##### **6、使用绿色通道(跳过所有的拦截器)**

```Java
 ARouter.getInstance().build("/test/activity")
        .greenChannel()
        .navigation(activity?);
```

##### **7、处理路由结果**

`navigation` 时传入 callback 监听路由结果

```TypeScript
 // 使用两个参数的navigation方法，可以获取单次跳转的结果
 ARouter.getInstance().build("/test/activity").navigation(activity?, new NavigationCallback() {
     @Override
     public void onFound(Postcard postcard) {
         ...
     }
 
     @Override
     public void onLost(Postcard postcard) {
         ...
     }
 });
```

##### **8、通过** **Uri** **跳转**

新建一个 Activity 用于监听 Scheme 事件

```Java
 public class SchemeFilterActivity extends Activity {
     @Override
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
 
         Uri uri = getIntent().getData();
         ARouter.getInstance().build(uri).navigation();
         finish();
     }
 }
```

配置 Scheme

```Java
 <activity android:name=".activity.SchemeFilterActivity">
     <!-- Scheme -->
     <intent-filter>
         <data android:host="bill.com" android:scheme="arouter"/>
         
         <action android:name="android.intent.action.VIEW"/>
         
         <category android:name="android.intent.category.DEFAULT"/>
         <category android:name="android.intent.category.BROWSABLE"/>
     </intent-filter>
 </activity>
```

模拟端外通过 Scheme 跳转

```Java
 // 通过 Scheme 拉起 App 调转到 SchemeFilterActivity 中
 adb shell am start -W -a android.intent.action.VIEW -d arouter://bill.com/test/activity?name=Bill&age=18 com.bill.myaroutertest
```

普通跳转传 Uri

```Java
 // 通过 uri 解析出 path 后进行跳转，uri 中的参数可以被 ARouter 解析并传到 Activity 中
 Uri testUriMix = Uri.parse("arouter://bill.com/test/activity?name=Bill&age=18");
 ARouter.getInstance().build(testUriMix)
         .withString("key1", "value1")
         .navigation();
```

注意：通过 uri 链接中传值时，在目标页面一定要使用 Autowired 注解标注属性；如果需要自动注入，则调用 `ARouter.getInstance().inject(this)` ，不自动注入则可以自己通过 `getIntent()` 的方式获取；

##### **9、获取 Fragment**

```Java
 Fragment fragment = (Fragment) ARouter.getInstance()
                                       .build("/test/fragment")
                                       .navigation();
```

##### **10、服务管理（****单例****）**

- 直接实现 IProvider 接口

```Java
 @Route(path = "/service/login")
 public class LoginService implements IProvider {
 
     public boolean isLogin() {
         return false;
     }
 
     @Override
     public void init(Context context) {
 
     }
 }
```

获取 Service

```Java
 // 通过 byType 的形式获取
 LoginService service = ARouter.getInstance().navigation(LoginService.class);
 
 // 通过 byName 的形式获取
 LoginService service = (LoginService)ARouter.getInstance()
                                             .build("/service/login")
                                             .navigation();
```

- 暴露服务（实现控制反转）

```Java
 public interface ILoginService extends IProvider {
     boolean isLogin();
 }
```

接口实现 1

```Java
 @Route(path = "/service/login1")
 public class LoginServiceImpl1 implements ILoginService {
 
     @Override
     public void init(Context context) {
 
     }
 
     @Override
     public boolean isLogin() {
         return true;
     }
 }
```

接口实现 2

```Java
 @Route(path = "/service/login2")
 public class LoginServiceImpl2 implements ILoginService {
 
     @Override
     public void init(Context context) {
 
     }
 
     @Override
     public boolean isLogin() {
         return false;
     }
 }
```

获取 Service 1

```Java
 // 通过 byName 的形式获取
 ILoginService service = (ILoginService)ARouter.getInstance()
                                             .build("/service/login1")
                                             .navigation();
```

获取 Service 2

```Java
 // 通过 byType 的形式获取
 ILoginService service = ARouter.getInstance().navigation(ILoginService.class);
 
 // 通过 byName 的形式获取
 ILoginService service = (ILoginService)ARouter.getInstance()
                                             .build("/service/login2")
                                             .navigation();
```

##### **11、声明拦截器**

> 只有跳转 Activity 时，拦截器才会生效

定义拦截器

> 定义拦截器后，最好处理下结果

```Java
 @Interceptor(priority = 100)
 public class TestInterceptor implements IInterceptor {
     @Override
     public void process(Postcard postcard, InterceptorCallback callback) {
         ...
         
          // 不拦截路由
         callback.onContinue(postcard);
         
         // 觉得有问题，中断路由流程
         callback.onInterrupt(new RuntimeException("我觉得有点异常"));
     }
 
     @Override
     public void init(Context context) {
         // 拦截器的初始化，会在sdk初始化的时候调用该方法，仅会调用一次
     }
 }
```

发起路由跳转 Activity 时可监听回调

```Java
 ARouter.getInstance()
         .build("/test/activity")
         .navigation(context, new NavCallback() {
             @Override
             public void onArrival(Postcard postcard) {
                 // 跳转成功
             }
 
             @Override
             public void onInterrupt(Postcard postcard) {
                 // 被拦截
             }
         });
```

##### **12、预处理服务**

```Java
 // 实现 PretreatmentService 接口
 @Route(path = "/xxx/xxx")
 public class PretreatmentServiceImpl implements PretreatmentService {
     @Override
     public boolean onPretreatment(Context context, Postcard postcard) {
         // 跳转前预处理，如果需要自行处理跳转，该方法返回 false 即可
     }
 
     @Override
     public void init(Context context) {
 
     }
 }
```

##### **13、自定义全局降级策略**

> navigation 时不传 callback 时，则路由未完成会执行全局降级策略

```Java
 // 实现DegradeService接口
 @Route(path = "/xxx/xxx")
 public class DegradeServiceImpl implements DegradeService {
     @Override
     public void onLost(Context context, Postcard postcard) {
         // do something.
     }
 
     @Override
     public void init(Context context) {
 
     }
 }
```

##### **14、重写跳转 path**

```Java
 // 实现PathReplaceService接口
 @Route(path = "/xxx/xxx")
 public class PathReplaceServiceImpl implements PathReplaceService {
     @Override
     String forString(String path) {
         return path; // 按照一定的规则处理之后返回处理后的结果
     }
     
     @Override
     Uri forUri(Uri uri) {
         return url; // 按照一定的规则处理之后返回处理后的结果
     }
     
     @Override
     public void init(Context context) {
 
     }
 }
```

##### **15、处理传递自定义对象**

> 处理 withObject()

```Java
 @Route(path = "/xxx/xxx")
 public class JsonServiceImpl implements SerializationService {
     @Override
     public <T> T json2Object(String text, Class<T> clazz) {
         return JSON.parseObject(text, clazz);
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
 
     }
 }
```

##### **16、动态注册路由信息**

```Java
 ARouter.getInstance().addRouteGroup(new IRouteGroup() {
     @Override
     public void loadInto(Map<String, RouteMeta> atlas) {
         atlas.put("/dynamic/activity",      // path
             RouteMeta.build(
                 RouteType.ACTIVITY,         // 路由信息
                 TestDynamicActivity.class,  // 目标的 Class
                 "/dynamic/activity",        // Path
                 "dynamic",                  // Group, 尽量保持和 path 的第一段相同
                 0,                          // 优先级，暂未使用
                 0                           // Extra，用于给页面打标签
             )
         );
     }
 });
```

##### **17、其他设置**

```Java
 ARouter.openLog(); // 开启日志
 ARouter.openDebug(); // 使用InstantRun的时候，需要打开该开关，上线之后关闭，否则有安全风险
 ARouter.printStackTrace(); // 打印日志的时候打印线程堆栈
 
 ARouter.setLogger(); // 使用自己的日志工具打印日志
 
 ARouter.setExecutor(); // 使用自己提供的线程池
 
 // 我们经常需要在目标页面中配置一些属性，比方说"是否需要登陆"之类的可以通过 Route 注解中
 // 的 extras 属性进行扩展，这个属性是一个 int值，换句话说，单个int有4字节，也就是32位，
 // 可以配置32个开关剩下的可以自行发挥，通过字节操作可以标识32个开关，通过开关标记目标页面
 // 的一些属性，在拦截器中可以拿到这个标记进行业务逻辑判断
 @Route(path = "/test/activity", extras = Consts.XXXX)
 
 // 目的页面获取原始的 Uri
 String uriStr = getIntent().getStringExtra(ARouter.RAW_URI);
```

##### **18、ARouter 内部自定义服务**

- 处理解析参数的

```Java
 @Route(path = "/arouter/service/autowired")
 public class AutowiredServiceImpl implements AutowiredService {
     @Override
     public void init(Context context) {}
     @Override
     public void autowire(Object instance) {}
 }
```

- 处理拦截器

```Java
 @Route(path = "/arouter/service/interceptor")
 public class InterceptorServiceImpl implements InterceptorService {
     @Override
     public void init(Context context) {}
     @Override
     public void doInterceptions(final Postcard postcard, final InterceptorCallback callback) {}
 }
```

#### **五、****源码****解析**

##### 0、项目结构

```Kotlin
arouter-annotation：定义了注解类以及携带数据的bean
arouter-compiler：注解生成器
arouter-api：实现路由功能
arouter_gradle-plugin：通过ASM框架自动生成代码（api >1.3）
atouter-idea-plugin：Idea插件
```

##### **1、注解生成规则**

- **Route 生成规则**

添加注解

```Java
 @Route(path = "/test1/activity")
 public class TestActivity extends Activity {
     @Autowired
     String name;
 }
 
 @Route(path = "/test1/fragment")
 public class TestFragment extends Fragment {}
 
 @Route(path = "/test2/activity")
 public class TestActivity2 extends Activity {}
 
 public interface ILoginService extends IProvider {}
 
 @Route(path = "/service/login1")
 public class LoginServiceImpl1 implements ILoginService {}
 
 @Route(path = "/service/login2")
 public class LoginServiceImpl2 implements ILoginService {}
 
 @Route(path = "/service2/login")
 public class LoginServiceImpl implements IProvider {}
```

编译后生成类如下

```Java
 package com.alibaba.android.arouter.routes;
 
 public class ARouter$$Group$$test1 implements IRouteGroup {
   @Override
   public void loadInto(Map<String, RouteMeta> atlas) {
     atlas.put("/test1/activity", 
         RouteMeta.build(RouteType.ACTIVITY, TestActivity.class, 
                         "/test1/activity", "test1", 
                         new java.util.HashMap<String, Integer>(){{put("name", 8); }}, 
                         -1, -2147483648));
     atlas.put("/test1/fragment", 
         RouteMeta.build(RouteType.FRAGMENT, TestFragment.class, 
                         "/test1/fragment", "test1", 
                         null, 
                         -1, -2147483648));
   }
 }
 
 public class ARouter$$Group$$test2 implements IRouteGroup {
   @Override
   public void loadInto(Map<String, RouteMeta> atlas) {
     atlas.put("/test2/activity", 
         RouteMeta.build(RouteType.ACTIVITY, TestActivity2.class, 
                         "/test2/activity", "test2", 
                         null,                           
                         -1, -2147483648));
   }
 }
 
 public class ARouter$$Group$$service implements IRouteGroup {
   @Override
   public void loadInto(Map<String, RouteMeta> atlas) {
     atlas.put("/service/login1", 
         RouteMeta.build(RouteType.PROVIDER, LoginServiceImpl1.class,            
                         "/service/login1", "service", 
                         null, -1, -2147483648));
     atlas.put("/service/login2", 
         RouteMeta.build(RouteType.PROVIDER, LoginServiceImpl2.class, 
                         "/service/login2", "service", 
                         null, -1, -2147483648));
   }
 }
 
 public class ARouter$$Group$$service2 implements IRouteGroup {
   @Override
   public void loadInto(Map<String, RouteMeta> atlas) {
     atlas.put("/service2/login", 
         RouteMeta.build(RouteType.PROVIDER, LoginServiceImpl.class, 
                         "/service2/login", "service2", 
                         null, -1, -2147483648));
   }
 }
 
 public class ARouter$$Root$$app implements IRouteRoot {
   @Override
   public void loadInto(Map<String, Class<? extends IRouteGroup>> routes) {
     routes.put("service", ARouter$$Group$$service.class);
     routes.put("service2", ARouter$$Group$$service2.class);
     routes.put("test1", ARouter$$Group$$test1.class);
     routes.put("test2", ARouter$$Group$$test2.class);
   }
 }
 
 public class ARouter$$Providers$$app implements IProviderGroup {
   @Override
   public void loadInto(Map<String, RouteMeta> providers) {
     providers.put("com.bill.myaroutertest.service.ILoginService", 
         RouteMeta.build(RouteType.PROVIDER, LoginServiceImpl1.class, 
                         "/service/login1", "service", 
                         null, -1, -2147483648));
     providers.put("com.bill.myaroutertest.service.ILoginService", 
         RouteMeta.build(RouteType.PROVIDER, LoginServiceImpl2.class, 
                         "/service/login2", "service", 
                         null, -1, -2147483648));
     providers.put("com.bill.myaroutertest.service.LoginServiceImpl", 
         RouteMeta.build(RouteType.PROVIDER, LoginServiceImpl.class, 
                         "/service2/login", "service2", 
                         null, -1, -2147483648));
   }
 }
```

生成类包名：`com.alibaba.android.arouter.routes`

生成类类名：

`ARouter$$Group$$ + group` ，

`ARouter$$Root$$ + AROUTER_MODULE_NAME`，

```
ARouter$$Providers$$ + AROUTER_MODULE_NAME
```

所以，同一个 module 下会生成一个 Provider 类，且此类实现 IProviderGroup 接口，实现 loadInto 方法；会生成一个 Root 类，且此类实现了 IRouteRoot 接口，实现 loadInto 方法；会根据 group 不同生成多个 Group 类，且此类实现了 IRouteGroup 接口，实现 loadInto 方法；

总结：

1、Route 的 path 必须是"/"开头，然后跟字符串，如"/xxx"；如果 group 指定了，则 path 可以就一级(一个"/")，此时跳转时也必须制定 group；如果不指定 group，则 path 至少有两级(两个"/")，因为 ARouter 会根据 path 截取出第一个和第二个"/"之间的字符串作为 group；

2、不同的 module 的 Aroute 注解不能使用相同的组名

3、Activity/Fragment 的 Route 会生成 IRouteGroup，并添加到 IRouteRoot 中；IProvider 的 Route 会生成 IRouteGroup，并且会生 IProviderGroup，并添加到 IRouteRoot 中；

4-1、IProvider 的作用是为了通过 byType 的方式找到 path

4-2、IRouteRoot 的作用是为了先保存所对应的 IRouteGroup，为了后面按需加载找到对应 IRouteGroup

4-3、IRouteGroup 的作用是保存路由信息

5、通过 byType 的方式发现服务，是先在 IProviderGroup 里通过接口的包名+类名获取到 path，再在 IRouteGroup 里通过 path 获取到实现类；通过 byName 的方式发现服务，是直接在 IRouteGroup 里通过 path 获取到实现类；

![img](https://github.com/YBill/ARouterTest/blob/master/sc/arouter/auto.png)

- **Interceptor 生成规则**

添加注解

```Java
 @Interceptor(priority = 100)
 public class TestInterceptor1 implements IInterceptor {}
 
 @Interceptor(priority = 200)
 public class TestInterceptor2 implements IInterceptor {}
```

编译后生成类

```Java
 package com.alibaba.android.arouter.routes;
 public class ARouter$$Interceptors$$app implements IInterceptorGroup {
   @Override
   public void loadInto(Map<Integer, Class<? extends IInterceptor>> interceptors) {
     interceptors.put(100, TestInterceptor1.class);
     interceptors.put(200, TestInterceptor2.class);
   }
 }
```

生成类包名：`com.alibaba.android.arouter.routes`

生成类类名：`ARouter$$Interceptors$$ + AROUTER_MODULE_NAME`

所以，同一个 module 下只会生成一个类，并且此类实现 IInterceptorGroup 接口，实现 loadInto 方法；多个拦截器会按照 priority 顺序分别 put 到 TreeMap 中，并且会判重报错，所以同一个 module 中生成的迭代器是有序的；

- **Autowired 生成规则**

添加注解

```Java
 package com.bill.myaroutertest;
 public class TestActivity extends AppCompatActivity {
     @Autowired(name = "id")
     int mId;
     
     @Autowired(required = true)
     String name;
 
     @Autowired
     ILoginService service1;
 
     @Autowired(name = "/service/login1")
     ILoginService service2;
 
     @Autowired
     public Bean bean;
 }
```

编译生成类：

```Java
 package com.bill.myaroutertest;
 public class TestActivity$$ARouter$$Autowired implements ISyringe {
   private SerializationService serializationService;
 
   @Override
   public void inject(Object target) {
     serializationService = ARouter.getInstance().navigation(SerializationService.class); 
     TestActivity substitute = (TestActivity)target;
     
     substitute.mId = substitute.getIntent().getIntExtra("id", substitute.mId);
     
     substitute.name = substitute.getIntent().getExtras() == null 
         ? substitute.name 
         : substitute.getIntent().getExtras().getString("name", substitute.name);
     if (null == substitute.name) {
       Log.e("ARouter::", "The field 'name' is null, in class '" + 
           TestActivity.class.getName() + "!");
     }
     
     substitute.service1 = ARouter.getInstance().navigation(ILoginService.class);
     substitute.service2 = 
         (ILoginService)ARouter.getInstance().build("/service/login1").navigation();
         
     if (null != serializationService) {
       substitute.bean = 
         serializationService.parseObject(
             substitute.getIntent().getStringExtra("bean"), 
             new com.alibaba.android.arouter.facade.model.
               TypeWrapper<ListBean>(){}.getType());
     } else {
       Log.e("ARouter::", "You want automatic inject the field 'bean' 
         in class 'TestActivity' , then you should implement 'SerializationService' 
         to support object auto inject!");
     }
     
   }
 }
生成类包名：同当前所在类同包名
```

生成类类名：`当前所在类类名 + $$ARouter$$Autowired`

所以，每一个类都会生成一个对应类，并且此类实现 ISyringe 接口，实现 inject 方法；被 Autowired 标注的属性都会生成赋值代码；会验证属性不能是私有的，非  provider 类型属性所属类必须是 Activity 或 Fragment；

##### **2、ARouter 初始化**

```Java
 ARouter.init(application);
```

- 未使用 arouter-register 插件

```Java
 Set<String> routerMap;
 if (ARouter.debuggable() || PackageUtils.isNewVersion(context)) {
     routerMap = ClassUtils.getFileNameByPackageName(
       mContext, ROUTE_ROOT_PAKCAGE);
 } else {
     routerMap = getByCache();
 }
 
 for (String className : routerMap) {
     if (className.startsWith(ROUTE_ROOT_PAKCAGE + DOT + SDK_NAME 
         + SEPARATOR + SUFFIX_ROOT)) {
          // This one of root elements, load root.
          ((IRouteRoot) (Class.forName(className).getConstructor().newInstance()))
             .loadInto(Warehouse.groupsIndex);
     } else if (className.startsWith(ROUTE_ROOT_PAKCAGE + DOT
         + SDK_NAME + SEPARATOR + SUFFIX_INTERCEPTORS)) {
           // Load interceptorMeta
           ((IInterceptorGroup)(Class.forName(className).getConstructor()
             .newInstance())).loadInto(Warehouse.interceptorsIndex);
     } else if (className.startsWith(ROUTE_ROOT_PAKCAGE + DOT
         + SDK_NAME + SEPARATOR + SUFFIX_PROVIDERS)) {
           // Load providerIndex
           ((IProviderGroup) (Class.forName(className).getConstructor()
             .newInstance())).loadInto(Warehouse.providersIndex);
    }
 }
```

- 使用 arouter-register 插件

```Java
 private static void loadRouterMap() {
     registerByPlugin = false;
     // class to dex 前插入下面代码
     register("com.alibaba.android.arouter.routes.ARouter$$Root$$arouterapi");
     register("com.alibaba.android.arouter.routes.ARouter$$Root$$app");
     register("com.alibaba.android.arouter.routes.ARouter$$Interceptors$$app");
     register("com.alibaba.android.arouter.routes.ARouter$$Providers$$arouterapi");
     register("com.alibaba.android.arouter.routes.ARouter$$Providers$$app");
 }
 
 private static void register(String className) {
     if (!TextUtils.isEmpty(className)) {
         try {
             Class<?> clazz = Class.forName(className);
             Object obj = clazz.getConstructor().newInstance();
             if (obj instanceof IRouteRoot) {
                 registerByPlugin = true;
                 ((IRouteRoot) obj).loadInto(Warehouse.groupsIndex);
             } else if (obj instanceof IProviderGroup) {
                 registerByPlugin = true;
                 ((IProviderGroup) obj).loadInto(Warehouse.providersIndex);
             } else if (obj instanceof IInterceptorGroup) {
                 registerByPlugin = true;
                 ((IInterceptorGroup) obj).loadInto(Warehouse.interceptorsIndex);
             } else {
                 logger.info(TAG, "register failed, class name: " + 
                         className + " should implements one of 
                         IRouteRoot/IProviderGroup/IInterceptorGroup.");
             }
         } catch (Exception e) {
             logger.error(TAG,"register class error:" + className, e);
         }
     }
 }
```

##### **3、拦截器**

```Java
@Route(path = "/arouter/service/interceptor")
public class InterceptorServiceImpl implements InterceptorService {
    
    @Override
    public void doInterceptions(final Postcard postcard, 
        final InterceptorCallback callback) {
    
    }
    
    
    @Override
    public void init(final Context context) {
    
    }
}
```

##### **4、发起路由**

```Java
 ARouter.getInstance().build("/test/activity").navigation();
```

完成按需加载

```Java
 public synchronized static void addRouteGroupDynamic(
     String groupName, IRouteGroup group) 
     throws NoSuchMethodException, IllegalAccessException, 
            InvocationTargetException, InstantiationException {
     if (Warehouse.groupsIndex.containsKey(groupName)){
         Warehouse.groupsIndex.get(groupName).getConstructor()
           .newInstance().loadInto(Warehouse.routes);
         Warehouse.groupsIndex.remove(groupName);
     }
 
     // cover old group.
     if (null != group) {
         group.loadInto(Warehouse.routes);
     }
 }
```

##### **5、依赖注入**

```Java
 ARouter.getInstance().inject(this);
 static void inject(Object thiz) {
     AutowiredService autowiredService = ((AutowiredService) 
       ARouter.getInstance().build("/arouter/service/autowired").navigation());
     if (null != autowiredService) {
         autowiredService.autowire(thiz);
     }
 }
```