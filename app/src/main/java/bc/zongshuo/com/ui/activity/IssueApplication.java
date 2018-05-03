package bc.zongshuo.com.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.multidex.MultiDex;

import com.baidu.mapapi.SDKInitializer;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.pgyersdk.crash.PgyCrashManager;
import com.sevenonechat.sdk.sdkCustomUi.UiCustomOptions;
import com.sevenonechat.sdk.sdkinfo.SdkRunningClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import bc.zongshuo.com.R;
import bc.zongshuo.com.utils.ImageLoadProxy;
import bocang.json.JSONArray;
import bocang.json.JSONObject;
import bocang.view.BaseApplication;
import cn.jiguang.analytics.android.api.JAnalyticsInterface;
import cn.jpush.android.api.JPushInterface;

/**
 * @author Jun
 * @time 2017/1/6  16:06
 * @desc 全局
 */
public class IssueApplication extends BaseApplication {
    protected static Context mContext = null;


    public static JSONObject UserInfo;
    public static int unreadMsgCount;

    @Override
    public void onCreate() {
        MultiDex.install(this);
//        Thread.currentThread().setUncaughtExceptionHandler(new MyExceptionHander());
        super.onCreate();
        mContext = getApplicationContext();
        super.mInstance = this;
        SDKInitializer.initialize(getApplicationContext());
        initImageLoader();
        ImageLoadProxy.initImageLoader(mContext);

        JPushInterface.setDebugMode(false);    // 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);            // 初始化 JPush
        PgyCrashManager.register(this);
        JAnalyticsInterface.init(mContext);
        JAnalyticsInterface.initCrashHandler(mContext);
        SdkRunningClient.getInstance().initAndLoginSdk(getApplicationContext(), "196417",
                "168e5ed4-d961-4230-a72c-696343615e17", initUiCustomOptions());
//        //SDK 初次注册成功后，开发者通过在自定义的 Receiver 里监听 Action - cn.jpush.android.intent.REGISTRATION 来获取对应的 RegistrationID。注册成功后，也可以通过此函数获取
//        public static String getRegistrationID(Context context)2500-17002571f432-8d48-466e-9954-a5980ac4bcbe

    }

    private UiCustomOptions initUiCustomOptions() {
        UiCustomOptions options = new UiCustomOptions();
        options.msgTitle_backgroudColor = R.color.green;
        return options;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


    private class MyExceptionHander implements Thread.UncaughtExceptionHandler {
        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            // Logger.i("MobileSafeApplication", "发生了异常,但是被哥捕获了..");
            //            LogUtils.d("MobileSafeApplication","发生了异常,但是被哥捕获了..");
            //并不能把异常消灭掉,只是在应用程序关掉前,来一个留遗嘱的事件
            //获取手机硬件信息
            try {
                Field[] fields = Build.class.getDeclaredFields();
                StringBuffer sb = new StringBuffer();
                for (Field field : fields) {
                    String value = field.get(null).toString();
                    String name = field.getName();
                    sb.append(name);
                    sb.append(":");
                    sb.append(value);
                    sb.append("\n");
                }
                File file = new File(getFilesDir(), "error.log");
                FileOutputStream out = new FileOutputStream(file);
                StringWriter wr = new StringWriter();
                PrintWriter err = new PrintWriter(wr);
                //获取错误信息
                ex.printStackTrace(err);
                String errorlog = wr.toString();
                sb.append(errorlog);
                out.write(sb.toString().getBytes());
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //杀死页面进程
            restartApp();
        }
    }

    public void restartApp() {
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());  //结束进程之前可以把你程序的注销或者退出代码放在这段代码之前
    }


    /**
     * 获得全局上下文
     *
     * @return
     */
    public static Context getcontext() {
        return mContext;
    }

    //初始化网络图片缓存库
    private void initImageLoader() {
        //网络图片例子,结合常用的图片缓存库UIL,你可以根据自己需求自己换其他网络图片库
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisk(true).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getApplicationContext()).defaultDisplayImageOptions(defaultOptions)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO).build();
        ImageLoader.getInstance().init(config);
    }


    /**
     * 返回桌面
     */
    public void toDesktop() {
        Intent home = new Intent(Intent.ACTION_MAIN);
        home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //        home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        home.addCategory(Intent.CATEGORY_HOME);
        startActivity(home);
    }

    public static String mCId = "";

    public static JSONObject mUserObject;

    public static String imagePath = "";

    public static File cameraPath;

    public static boolean isClassify = false;

    public static int mCartCount = 0;

    public static int mLightIndex = 0;//点出来的灯的序号

    public static boolean isGoProgramme = false;

    public static Map<String,String> mSelectProParamemt=new HashMap<>();
    public static JSONArray mSelectProducts = new JSONArray();

    public static JSONArray mSelectScreens = new JSONArray();

}
