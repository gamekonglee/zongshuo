package bc.zongshuo.com.ui.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sevenonechat.sdk.sdkCallBack.ChatMsgUnRead;
import com.sevenonechat.sdk.sdkinfo.SdkRunningClient;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import bc.zongshuo.com.Manifest;
import bc.zongshuo.com.R;
import bc.zongshuo.com.common.BaseActivity;
import bc.zongshuo.com.cons.Constance;
import bc.zongshuo.com.controller.MainController;
import bc.zongshuo.com.ui.activity.user.LoginActivity;
import bc.zongshuo.com.ui.fragment.CartFragment;
import bc.zongshuo.com.ui.fragment.ClassifyFragment;
import bc.zongshuo.com.ui.fragment.HomeFragment;
import bc.zongshuo.com.ui.fragment.MineFragment;
import bc.zongshuo.com.ui.fragment.ProgrammeFragment;
import bc.zongshuo.com.ui.view.BottomBar;
import bc.zongshuo.com.ui.view.WarnDialog;
import bc.zongshuo.com.ui.view.popwindow.OutLoginPopWindow;
import bc.zongshuo.com.utils.MyShare;
import bc.zongshuo.com.utils.NotificationsUtils;
import bc.zongshuo.com.utils.UIUtils;
import bocang.json.JSONArray;
import bocang.utils.AppUtils;
import bocang.utils.IntentUtil;
import bocang.utils.LogUtils;
import bocang.utils.MyToast;
import cn.jiguang.analytics.android.api.JAnalyticsInterface;
import cn.jpush.android.api.JPushInterface;
import me.leolin.shortcutbadger.ShortcutBadger;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    public HomeFragment mHomeFragment;
    private ClassifyFragment mProductFragment;
    private CartFragment mCartFragment;
    private ProgrammeFragment mMatchFragment;
    private MineFragment mMineFragment;
    private Fragment currentFragmen;
    private int pager = 2;
    private long exitTime;
    public static JSONArray mCategories;
    private MainController mController;
    public String download = DOWNLOAD_SERVICE;
    public int unreadMsgCount = 0;
    private TextView frag_top_tv;
    private TextView frag_product_tv;
    private TextView frag_match_tv;
    private TextView frag_cart_tv;
    private TextView frag_mine_tv;
    private ImageView frag_top_iv;
    private ImageView frag_product_iv;
    private ImageView frag_match_iv;
    private ImageView frag_cart_iv;
    private ImageView frag_mine_iv;

    public static boolean isForeground = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        EventBus.getDefault().register(this);
//        ActivityCompat.requestPermissions(this,
//                new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CALL_PHONE},
//                1);
        JAnalyticsInterface.onPageStart(this, this.getClass().getCanonicalName());
        SdkRunningClient.getInstance().addMsgUnReadListener(this, new ChatMsgUnRead() {
            @Override
            public void onChatMsgUnRead(long l) {
                IssueApplication.unreadMsgCount = unreadMsgCount;
                if (unreadMsgCount == 0) {
//                    ShortcutBadger.applyCount(this, badgeCount); //for 1.1.4+
                    mHomeFragment.unMessageTv.setVisibility(View.GONE);
                    ShortcutBadger.applyCount(MainActivity.this,0);
                } else {
                    ShortcutBadger.applyCount(MainActivity.this, MainActivity.this.unreadMsgCount); //for 1.1.4+
                    MainActivity.this.mHomeFragment.unMessageTv.setVisibility(View.VISIBLE);
                    MainActivity.this.mHomeFragment.unMessageTv.setText(MainActivity.this.unreadMsgCount + "");
                }
            }
        });
        int sdkint= Build.VERSION.SDK_INT;
        if(sdkint>22){
         if(ContextCompat.checkSelfPermission(this, android.Manifest.permission_group.PHONE)!= PackageManager.PERMISSION_GRANTED){
             ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission_group.PHONE, android.Manifest.permission_group.STORAGE},1);
         }
        }


        boolean isFirst=MyShare.get(this).getBoolean(Constance.ISFIRSTISTART);
        if(!isFirst){
            MyShare.get(this).putBoolean(Constance.ISFIRSTISTART,true);
            if(!NotificationsUtils.isNotificationEnabled(this)){
                UIUtils.showSingleWordDialog(this, "您的通知开关尚未设置，请前往设置开启", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        requestPermission(200);
                    }
                });

            }
        }
    }
    protected void requestPermission(int requestCode) {
        // TODO Auto-generated method stub
        // 6.0以上系统才可以判断权限

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH&&Build.VERSION.SDK_INT<23) {
            // 进入设置系统应用权限界面
            Intent intent = new Intent(Settings.ACTION_SETTINGS);
            startActivity(intent);
            return;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {// 运行系统在6.0环境使用
            // 进入设置系统应用权限界面
            Intent intent = new Intent();
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", getPackageName(), null));
            startActivity(intent);
            return;
        }
        return;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // 初始化 JPush。如果已经初始化，但没有登录成功，则执行重新登录。
    private void init() {
        //        JPushInterface.init(getApplicationContext());
        JPushInterface.setDebugMode(true);//如果时正式版就改成false
        JPushInterface.init(this);
        Log.e("520it", JPushInterface.getRegistrationID(this));
    }

    @Override
    protected void InitDataView() {
        selectItem(R.id.frag_top_ll);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (IssueApplication.isGoProgramme) {
            IssueApplication.isGoProgramme = false;
            selectItem(R.id.frag_match_ll);
            clickTab3Layout();
        }

    }

    @Override
    protected void initController() {
        mController = new MainController(this);
    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_main);

        //沉浸式状态栏
         setColor(this, getResources().getColor(R.color.colorPrimaryDark));
        frag_top_tv = (TextView) findViewById(R.id.frag_top_tv);
        frag_product_tv = (TextView) findViewById(R.id.frag_product_tv);
        frag_match_tv = (TextView) findViewById(R.id.frag_match_tv);
        frag_cart_tv = (TextView) findViewById(R.id.frag_cart_tv);
        frag_mine_tv = (TextView) findViewById(R.id.frag_mine_tv);
        frag_top_iv = (ImageView) findViewById(R.id.frag_top_iv);
        frag_product_iv = (ImageView) findViewById(R.id.frag_product_iv);
        frag_match_iv = (ImageView) findViewById(R.id.frag_match_iv);
        frag_cart_iv = (ImageView) findViewById(R.id.frag_cart_iv);
        frag_mine_iv = (ImageView) findViewById(R.id.frag_mine_iv);

        findViewById(R.id.frag_top_tv).setOnClickListener(this);
        findViewById(R.id.frag_product_tv).setOnClickListener(this);
        findViewById(R.id.frag_match_tv).setOnClickListener(this);
        findViewById(R.id.frag_cart_tv).setOnClickListener(this);
        findViewById(R.id.frag_mine_tv).setOnClickListener(this);
        findViewById(R.id.frag_top_ll).setOnClickListener(this);
        findViewById(R.id.frag_product_ll).setOnClickListener(this);
        findViewById(R.id.frag_match_ll).setOnClickListener(this);
        findViewById(R.id.frag_cart_ll).setOnClickListener(this);
        findViewById(R.id.frag_mine_ll).setOnClickListener(this);

        initTab();
//        registerMessageListener();

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
//
//            local LayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
//            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
//                //将侧边栏顶部延伸至status bar
//                mDrawerLayout.setFitsSystemWindows(true);
//                //将主页面顶部延伸至status bar;虽默认为false,但经测试,DrawerLayout需显示设置
//                mDrawerLayout.setClipToPadding(false);
//            }
//        }
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//        }



//        if (Build.VERSION.SDK_INT >= 21) {
//            View decorView = getWindow().getDecorView();
//            int option = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
//            decorView.setSystemUiVisibility(option);
//            getWindow().setNavigationBarColor(Color.TRANSPARENT);
//            getWindow().setStatusBarColor(Color.TRANSPARENT);
//        }
//        Log.e("520it", "initView: " + getHasVirtualKey() + ":" + getNoHasVirtualKey() );

    }

    // 通过反射机制获取手机状态栏高度
    public int getStatusBarHeight() {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return statusBarHeight;
    }


    /**
     * 获取屏幕尺寸，但是不包括虚拟功能高度
     *
     * @return
     */
    public int getNoHasVirtualKey() {
        int height = getWindowManager().getDefaultDisplay().getHeight();
        return height;
    }

    /**
     * 通过反射，获取包含虚拟键的整体屏幕高度
     *
     * @return
     */
    private int getHasVirtualKey() {
        int dpi = 0;
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        @SuppressWarnings("rawtypes")
        Class c;
        try {
            c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, dm);
            dpi = dm.heightPixels;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dpi;
    }



    @Override
    protected void initData() {
//        Intent intent = getIntent();
//        if (intent.getBooleanExtra(Constance.ACCOUNT_CONFLICT, false)) {
//            showConflictDialog();
//        }

    }

//    /**
//     * show the dialog when user logged into another device
//     */
//    public void showConflictDialog() {
//        EMClient.getInstance().logout(true, new EMCallBack() {
//            @Override
//            public void onSuccess() {
//                // TODO Auto-generated method stub
//                Log.e("520it", "Main注销登录");
//            }
//
//            @Override
//            public void onProgress(int progress, String status) {
//                // TODO Auto-generated method stub
//
//            }
//
//            @Override
//            public void onError(int code, String message) {
//                // TODO Auto-generated method stub
//                Log.e("520it", "Main注销失败:" + message);
//            }
//        });
//
//        MyShare.get(this).putString(Constance.TOKEN, "");
//        MyShare.get(MainActivity.this).putString(Constance.TOKEN, "");
//        MyShare.get(MainActivity.this).putString(Constance.USERNAME, "");
//        MyShare.get(MainActivity.this).putString(Constance.USERCODE, "");
//        MyShare.get(MainActivity.this).putString(Constance.USERCODEID, "");
//
//        ShowDialog mDialog = new ShowDialog();
//        mDialog.show(this, "提示", UIUtils.getString(R.string.connect_conflict), new ShowDialog.OnBottomClickListener() {
//            @Override
//            public void positive() {
//                EMClient.getInstance().logout(true, new EMCallBack() {
//
//                    @Override
//                    public void onSuccess() {
//                        // TODO Auto-generated method stub
//                        Log.e("520it", "Main注销登录");
//                    }
//
//                    @Override
//                    public void onProgress(int progress, String status) {
//                        // TODO Auto-generated method stub
//
//                    }
//
//                    @Override
//                    public void onError(int code, String message) {
//                        // TODO Auto-generated method stub
//                        Log.e("520it", "Main注销失败:" + message);
//                    }
//                });
//                Intent logoutIntent = new Intent(MainActivity.this, LoginActivity.class);
//                logoutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(logoutIntent);
//                finish();
//
//
//            }
//
//            @Override
//            public void negtive() {
//                EMClient.getInstance().logout(true, new EMCallBack() {
//
//                    @Override
//                    public void onSuccess() {
//                        // TODO Auto-generated method stub
//                        Log.e("520it", "Main注销登录");
//                    }
//
//                    @Override
//                    public void onProgress(int progress, String status) {
//                        // TODO Auto-generated method stub
//
//                    }
//
//                    @Override
//                    public void onError(int code, String message) {
//                        // TODO Auto-generated method stub
//                        Log.e("520it", "Main注销失败:" + message);
//                    }
//                });
//                Intent logoutIntent = new Intent(MainActivity.this, LoginActivity.class);
//                logoutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(logoutIntent);
//                finish();
//            }
//        });
//
//
//    }


//    /**
//     * EMEventListener
//     */
//    protected EMMessageListener messageListener = null;
//
//    protected void registerMessageListener() {
//        messageListener = new EMMessageListener() {
//            private BroadcastReceiver broadCastReceiver = null;
//
//            @Override
//            public void onMessageReceived(List<EMMessage> messages) {
//                for (EMMessage message : messages) {
//                    //                    EMLog.d(TAG, "onMessageReceived id : " + message.getMsgId());
//                    EaseUI.getInstance().getNotifier().onNewMsg(message);
//
//                    String userNice = message.getStringAttribute(Constance.USER_NICE, "");
//                    String userPic = message.getStringAttribute(Constance.USER_ICON, "");
//                    String userId = message.getStringAttribute(Constance.User_ID, "");
//                    EaseUser user1 = new EaseUser(userId);
//                    user1.setNickname(userId);
//                    user1.setNick(userNice);
//                    user1.setAvatar(userPic);
//                    DemoHelper.getInstance().saveContact(user1);
//
//                }
////                mController.refreshUIWithMessage();
//            }
//
//            @Override
//            public void onCmdMessageReceived(List<EMMessage> messages) {
//                for (EMMessage message : messages) {
//                    //                    EMLog.d(TAG, "receive command message");
//                    //get message body
//                    EMCmdMessageBody cmdMsgBody = (EMCmdMessageBody) message.getBody();
//                    final String action = cmdMsgBody.action();//获取自定义action
//
//                    if (action.equals("__Call_ReqP2P_ConferencePattern")) {
//                        String title = message.getStringAttribute("em_apns_ext", "conference call");
//                        //                        Toast.makeText(appContext, title, Toast.LENGTH_LONG).show();
//                    }
//                    //end of red packet code
//                    //获取扩展属性 此处省略
//                    //maybe you need get extension of your message
//                    //message.getStringAttribute("");
//                    //                    EMLog.d(TAG, String.format("Command：action:%s,message:%s", action, message.toString()));
//                }
//            }
//
//            @Override
//            public void onMessageReadAckReceived(List<EMMessage> messages) {
//            }
//
//            @Override
//            public void onMessageDeliveryAckReceived(List<EMMessage> message) {
//            }
//
//            @Override
//            public void onMessageChanged(EMMessage message, Object change) {
//
//            }
//        };
//
//        EMClient.getInstance().chatManager().addMessageListener(messageListener);
//    }


//    private BottomBar.IBottomBarItemClickListener mBottomBarClickListener = new BottomBar.IBottomBarItemClickListener() {
//        @Override
//        public void OnItemClickListener(int resId) {
//            switch (resId) {
//                case R.id.frag_top_ll:
//                    //                    full(true);
//                    //                    MainActivity.this.textView.setBackgroundColor(Color.parseColor("#FF0000"));
//                    //                    rootView.setBackgroundColor(Color.parseColor("#FF0000"));
//
//                    clickTab1Layout();
//                    break;
//                case R.id.frag_product_ll:
//                    //                    MainActivity.this.textView.setBackgroundColor(Color.parseColor("#00000000"));
//                    //                    rootView.setBackgroundColor(Color.parseColor("#00000000"));
//                    clickTab2Layout();
//
//                    break;
//                case R.id.frag_match_ll:
//                    clickTab3Layout();
//                    break;
//                case R.id.frag_cart_ll:
//                    clickTab4Layout();
//                    break;
//                case R.id.frag_mine_ll:
//                    String token= MyShare.get(MainActivity.this).getString(Constance.TOKEN);
//                    String userCode= MyShare.get(MainActivity.this).getString(Constance.USERCODE);
////                    if(AppUtils.isEmpty(token) && AppUtils.isEmpty(userCode)){
////                        IntentUtil.startActivity(MainActivity.this, LoginActivity.class, true);
////                    }else{
////                    }
//                    clickTab5Layout();
//
//                    break;
//                case R.id.frag_top_tv:
//                    //                    full(true);
//                    clickTab1Layout();
//                    break;
//            }
//        }
//    };

    /**
     * 初始化底部标签
     */
    private void initTab() {
        if (mHomeFragment == null) {
            mHomeFragment = new HomeFragment();
        }
        if (!mHomeFragment.isAdded()) {
            // 提交事务
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.top_bar, mHomeFragment).commit();

            // 记录当前Fragment
            currentFragmen = mHomeFragment;
        }
    }


    /**
     * 点击第1个tab
     */
    public void clickTab1Layout() {
        if (mHomeFragment == null) {
            mHomeFragment = new HomeFragment();
        }
        addOrShowFragment(getSupportFragmentManager().beginTransaction(), mHomeFragment);

    }

    /**
     * 点击第2个tab
     */
    public void clickTab2Layout() {
        if (mProductFragment == null) {
            mProductFragment = new ClassifyFragment();
        }
        addOrShowFragment(getSupportFragmentManager().beginTransaction(), mProductFragment);

    }

    /**
     * 点击第3个tab
     */
    public void clickTab3Layout() {
        if (mMatchFragment == null) {
            mMatchFragment = new ProgrammeFragment();
        }
        addOrShowFragment(getSupportFragmentManager().beginTransaction(), mMatchFragment);

    }

    /**
     * 点击第4个tab
     */
    private void clickTab4Layout() {
        if (mCartFragment == null) {
            mCartFragment = new CartFragment();
        }
        addOrShowFragment(getSupportFragmentManager().beginTransaction(), mCartFragment);

    }

    /**
     * 点击第5个tab
     */
    public void clickTab5Layout() {
        if (mMineFragment == null) {
            mMineFragment = new MineFragment();
        }
        addOrShowFragment(getSupportFragmentManager().beginTransaction(), mMineFragment);

    }

    /**
     * 添加或者显示碎片
     *
     * @param transaction
     * @param fragment
     */
    private void addOrShowFragment(FragmentTransaction transaction,
                                   Fragment fragment) {
        if (currentFragmen == fragment)
            return;

        if (!fragment.isAdded()) { // 如果当前fragment未被添加，则添加到Fragment管理器中
            transaction.hide(currentFragmen)
                    .add(R.id.top_bar, fragment).commit();
        } else {
            transaction.hide(currentFragmen).show(fragment).commit();
        }

        currentFragmen = fragment;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (pager == 2) {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
                if ((System.currentTimeMillis() - exitTime) > 2000) {
                    exitTime = System.currentTimeMillis();

                    MyToast.show(this, R.string.back_desktop);
                } else {
                    finish();
                }
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * show 激活 dialog
     */
    public void showActivateDialog() {
        WarnDialog activateDialog = new WarnDialog(this, "请激活该设备", "确定", true, false, false);
        activateDialog.setListener(new bc.zongshuo.com.ui.view.BaseDialog.IConfirmListener() {
            @Override
            public void onDlgConfirm(bc.zongshuo.com.ui.view.BaseDialog dlg, int flag) {
                if (flag == 0) {
                    MyToast.show(MainActivity.this, "激活成功!!");
                }
            }
        });
        activateDialog.show();
    }


    @Override
    protected void onResume() {
        isForeground = true;
        super.onResume();
    }


    @Override
    protected void onPause() {
        isForeground = false;
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    //在主线程执行
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserEvent(Integer action) {
        if (action == Constance.CARTCOUNT) {
            mController.setIsShowCartCount();
        }
        if (action == Constance.MESSAGE) {
            unreadMsgCount = IssueApplication.unreadMsgCount;
            if (unreadMsgCount == 0) {
                mHomeFragment.unMessageTv.setVisibility(View.GONE);
                ShortcutBadger.applyCount(this, 0);
            } else {
                ShortcutBadger.applyCount(this, this.unreadMsgCount); //for 1.1.4+
                mHomeFragment.unMessageTv.setVisibility(View.VISIBLE);
                mHomeFragment.unMessageTv.setText(unreadMsgCount + "");
            }
        }
    }

    @Override
    public void onClick(View v) {
        //	设置 如果点击的是当前的的按钮 再次点击无效
        if (mCurrenTabId != 0 && mCurrenTabId == v.getId()) {
            return;
        }

        selectItem(v.getId());
    }

    /**
     * 默认全部不被选中
     */
    private void defaultTabStyle() {
        frag_top_tv.setSelected(false);
        frag_top_iv.setSelected(false);
        frag_product_tv.setSelected(false);
        frag_product_iv.setSelected(false);
        frag_match_tv.setSelected(false);
        frag_match_iv.setSelected(false);
        frag_cart_tv.setSelected(false);
        frag_cart_iv.setSelected(false);
        frag_mine_tv.setSelected(false);
        frag_mine_iv.setSelected(false);
    }

    private int mCurrenTabId;

    /**
     * 选择指定的item
     *
     * @param currenTabId
     */
    public void selectItem(int currenTabId) {
        //	设置 如果电机的是当前的的按钮 再次点击无效
        if (mCurrenTabId != 0 && mCurrenTabId == currenTabId) {
            return;
        }
        //点击前先默认全部不被选中
        defaultTabStyle();

        mCurrenTabId = currenTabId;
        String token= MyShare.get(MainActivity.this).getString(Constance.TOKEN);
        String userCode= MyShare.get(MainActivity.this).getString(Constance.USERCODE);
        switch (currenTabId) {
            case R.id.frag_top_ll:
                frag_top_tv.setSelected(true);
                frag_top_iv.setSelected(true);
                clickTab1Layout();
                break;
            case R.id.frag_product_ll:
                frag_product_tv.setSelected(true);
                frag_product_iv.setSelected(true);
                clickTab2Layout();
                break;
            case R.id.frag_match_ll:
                if(isToken()){
                    selectItem(R.id.frag_top_ll);
                    return;
                }
                frag_match_tv.setSelected(true);
                frag_match_iv.setSelected(true);
                clickTab3Layout();
                break;
            case R.id.frag_cart_ll:
                if(isToken()){
                    selectItem(R.id.frag_top_ll);
                    return;
                }
                frag_cart_tv.setSelected(true);
                frag_cart_iv.setSelected(true);
                clickTab4Layout();
                break;
            case R.id.frag_mine_ll:
                if(isToken()){
                    selectItem(R.id.frag_top_ll);
                    return;
                }
                frag_mine_tv.setSelected(true);
                frag_mine_iv.setSelected(true);

//                if(AppUtils.isEmpty(token) && AppUtils.isEmpty(userCode)){
//                    IntentUtil.startActivity(MainActivity.this, LoginActivity.class, true);
//                }else{
//                }
                clickTab5Layout();
                break;
            case R.id.frag_top_tv:
                frag_top_tv.setSelected(true);
                frag_top_iv.setSelected(true);
                clickTab1Layout();
                break;
            case R.id.frag_product_tv:
                frag_product_tv.setSelected(true);
                frag_product_iv.setSelected(true);
                clickTab2Layout();
                break;
            case R.id.frag_match_tv:
                if(isToken()){
                    selectItem(R.id.frag_top_ll);
                    return;
                }
                frag_match_tv.setSelected(true);
                frag_match_iv.setSelected(true);
                clickTab3Layout();
                break;
            case R.id.frag_cart_tv:
                if(isToken()){
                    selectItem(R.id.frag_top_ll);
                    return;
                }
                frag_cart_tv.setSelected(true);
                frag_cart_iv.setSelected(true);
                clickTab4Layout();
                break;
            case R.id.frag_mine_tv:
                if(isToken()){
                    selectItem(R.id.frag_top_ll);
                    return;
                }
                frag_mine_tv.setSelected(true);
                frag_mine_iv.setSelected(true);
//                if(isToken()){
//                }else{
//                }
                clickTab5Layout();
                break;
        }
    }

    /**
     * 判断是否有toKen
     */
    public Boolean isToken() {
        String token = MyShare.get(this).getString(Constance.TOKEN);
        if(AppUtils.isEmpty(token)){
//            Intent logoutIntent = new Intent(getActivity(), LoginActivity.class);
//            logoutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(logoutIntent);
            View popupView = getLayoutInflater().inflate(R.layout.popuplayout, null);
            OutLoginPopWindow popWindow=new OutLoginPopWindow(this);
            popWindow.mActivity=this;
            popWindow.onShow(popupView);
            return true;
        }
        return  false;
    }

}
