package bc.zongshuo.com.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import bc.zongshuo.com.R;
import bc.zongshuo.com.common.BaseActivity;
import bc.zongshuo.com.cons.Constance;
import bc.zongshuo.com.controller.SplashController;
import bc.zongshuo.com.ui.activity.user.LoginActivity;
import bc.zongshuo.com.utils.MyShare;
import bocang.utils.AppUtils;
import bocang.utils.CommonUtil;
import bocang.utils.IntentUtil;

/**
 * @author Jun
 * @time 2017/1/5  10:29
 * @desc 启动页
 */
public class SplashActivity extends BaseActivity {
    private ImageView mLogoIv;
    private AlphaAnimation mAnimation;
    private TextView version_tv;
    private SplashController mController;
    private boolean remember;

    @Override
    protected void InitDataView() {
        String localVersion = CommonUtil.localVersionName(this);
        version_tv.setText("V "+localVersion);
    }

    @Override
    protected void initController() {
        mController=new SplashController(this);
    }

    @Override
    protected void initView() {
        //去除title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //去掉Activity上面的状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);
        mLogoIv = (ImageView) findViewById(R.id.logo_iv);
        mLogoIv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        //布置透明度动画
        mAnimation.setDuration(2500);
        mAnimation.setFillAfter(true);
        mLogoIv.startAnimation(mAnimation);
        String token = MyShare.get(this).getString(Constance.TOKEN);
//        if (AppUtils.checkNetwork() && !AppUtils.isEmpty(token)){
//            getSuccessLogin();
//        }
        version_tv = (TextView)findViewById(R.id.version_tv);
        if((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0){
            finish();
            return;
        }
    }

//    /**
//     * 登录成功处理事件
//     */
//    private void getSuccessLogin() {
//        final String uid=MyShare.get(this).getString(Constance.USERID);
//        if(AppUtils.isEmpty(uid)){
//            return;
//        }
//
//        if(EMClient.getInstance().isLoggedInBefore()){
//            EMClient.getInstance().logout(true, new EMCallBack() {
//
//                @Override
//                public void onSuccess() {
//                    // TODO Auto-generated method stub
//                    Log.e("520it", "S注销成功");
//                    getSuccessLogin();
//                }
//
//                @Override
//                public void onProgress(int progress, String status) {
//                    // TODO Auto-generated method stub
//
//                }
//
//                @Override
//                public void onError(int code, String message) {
//                    // TODO Auto-generated method stub
//                    Log.e("520it", "S注销失败");
//
//                }
//            });
//
//        }else{
//            EMClient.getInstance().login(uid, uid, new EMCallBack() {
//                @Override
//                public void onSuccess() {
//                    Log.e("520it", "S登录成功");
//                    EMClient.getInstance().groupManager().loadAllGroups();
//                    EMClient.getInstance().chatManager().loadAllConversations();
//                }
//
//                @Override
//                public void onProgress(int progress, String status) {
//                }
//
//                @Override
//                public void onError(final int code, final String message) {
//                    Log.e("520it", "S登录失败:"+message);
//                    if(message.equals("User dosn't exist")){
//                        sendRegiestSuccess();
//                    }
//
//                }
//            });
//        }
//
//
//    }
//
//    /**
//     * 环信注册
//     */
//    private void sendRegiestSuccess() {
//        final String uid=MyShare.get(this).getString(Constance.USERID);
//        if(AppUtils.isEmpty(uid)){
//            return;
//        }
//        new Thread(new Runnable() {
//            public void run() {
//                try {
//                    EMClient.getInstance().createAccount(uid,uid);//同步方法
//                    SplashActivity.this.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            MyShare.get(SplashActivity.this).putBoolean(Constance.EMREGIEST, true);//保存TOKEN
//                            Log.e("520it", "S注册成功!");
//                            getSuccessLogin();
//
//                        }
//                    });
//
//                } catch (final HyphenateException e) {
//                    SplashActivity.this.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Log.e("520it", "S注册失败!：" + e.getMessage());
//                            getSuccessLogin();
//                        }
//                    });
//
//                }
//            }
//        }).start();
//    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            showDialog();
        }
    };
    @Override
    protected void initData() {
        mAnimation = new AlphaAnimation (0.2f, 1.0f);
        new Timer().schedule(new TimerSchedule(), 2600);
    }

    private class TimerSchedule extends TimerTask {
        @Override
        public void run() {
            boolean remember=MyShare.get(SplashActivity.this).getBoolean(Constance.apply_remember);
            if(!remember){
                handler.sendEmptyMessage(0);
            }else {
                startAct();
            }
        }
    }

    private void startAct() {
        Boolean isFinish= MyShare.get(SplashActivity.this).getBoolean(Constance.ISFIRSTISTART);
        if(isFinish){
            String token= MyShare.get(SplashActivity.this).getString(Constance.TOKEN);
            String userCode= MyShare.get(SplashActivity.this).getString(Constance.USERCODE);
            if(AppUtils.isEmpty(token) && AppUtils.isEmpty(userCode)){
                IntentUtil.startActivity(SplashActivity.this, MainActivity.class, true);
            }else{
                IntentUtil.startActivity(SplashActivity.this, MainActivity.class, true);
            }

        }else{
            IntentUtil.startActivity(SplashActivity.this, LeadPageActivity.class, true);
        }
    }
    public void showDialog(){
        final Dialog dialog=new Dialog(this,R.style.customDialog);
        dialog.setContentView(R.layout.dialog_apply);
        ImageView iv_dismiss=dialog.findViewById(R.id.iv_dismiss);
        TextView tv_dimiss=dialog.findViewById(R.id.tv_dismiss);
        ImageView iv_apply=dialog.findViewById(R.id.iv_apply);
        final TextView tv_remember=dialog.findViewById(R.id.tv_remember);
        remember = false;
        iv_dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyShare.get(SplashActivity.this).putBoolean(Constance.apply_remember, remember);
                dialog.dismiss();
                startAct();
            }
        });
        tv_dimiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyShare.get(SplashActivity.this).putBoolean(Constance.apply_remember, remember);
                dialog.dismiss();
                startAct();
            }
        });
        tv_remember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(remember){
                    remember =false;
                    Drawable drawable=getResources().getDrawable(R.mipmap.jm_icom_nor);
                    drawable.setBounds(0,0,drawable.getMinimumHeight(),drawable.getMinimumWidth());
                    tv_remember.setCompoundDrawables(drawable,null,null,null);
                }else {
                    remember =true;
                    Drawable drawable=getResources().getDrawable(R.mipmap.jm_icon_sel);
                    drawable.setBounds(0,0,drawable.getMinimumHeight(),drawable.getMinimumWidth());
                    tv_remember.setCompoundDrawables(drawable,null,null,null);
                }
            }
        });
        iv_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                startActivity(new Intent(SplashActivity.this,BussinessApplyActivity.class));
                finish();
            }
        });
        try {
            dialog.show();
        }catch (Exception e){
            startActivity(new Intent(this,MainActivity.class));
            finish();
        }
    }
}
