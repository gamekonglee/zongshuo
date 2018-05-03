package bc.zongshuo.com.common;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import bc.zongshuo.com.ui.activity.IssueApplication;
import bc.zongshuo.com.utils.StatusBarUtil;

/**
 * @author Jun
 * @time 2017/1/5  10:31
 * @desc ${TODD}
 */
public abstract class BaseActivity  extends FragmentActivity {
    private IssueApplication app;
    public ViewGroup rootView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            StatusBarUtil.StatusBarLightMode(this);
        }
        initData();
        initView();
        initController();
        InitDataView();
        app = (IssueApplication) getApplication();

    }



    protected abstract void InitDataView();

    protected abstract void initController();

    protected abstract void initView();

    protected abstract void initData();

    public void tip(String msg){
        Toast.makeText(this,msg, Toast.LENGTH_SHORT).show();
    }

    public IssueApplication getApp() {
        return app;
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v("520it","停止");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v("520it","销毁");
    }


    /** * 设置状态栏颜色 * * @param activity 需要设置的activity * @param color 状态栏颜色值 */
    public static void setColor(Activity activity, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 设置状态栏透明
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 生成一个状态栏大小的矩形
            View statusView = createStatusView(activity, color);
            // 添加 statusView 到布局中
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            decorView.addView(statusView);
            // 设置根布局的参数
            ViewGroup rootView = (ViewGroup) ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
            rootView.setFitsSystemWindows(true);
            rootView.setClipToPadding(true);
        }
    }

    /** * 生成一个和状态栏大小相同的矩形条 * * @param activity 需要设置的activity * @param color 状态栏颜色值 * @return 状态栏矩形条 */
    private static View createStatusView(Activity activity, int color) {
        // 获得状态栏高度
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        int statusBarHeight = activity.getResources().getDimensionPixelSize(resourceId);

        // 绘制一个和状态栏一样高的矩形
        View statusView = new View(activity);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                statusBarHeight);
        statusView.setLayoutParams(params);
        statusView.setBackgroundColor(color);

        return statusView;
    }

    public void goBack(View v) {
        finish();
    }

}
