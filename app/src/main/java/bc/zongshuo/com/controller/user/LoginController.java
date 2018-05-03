package bc.zongshuo.com.controller.user;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashMap;

import bc.zongshuo.com.R;
import bc.zongshuo.com.cons.Constance;
import bc.zongshuo.com.cons.NetWorkConst;
import bc.zongshuo.com.controller.BaseController;
import bc.zongshuo.com.listener.INetworkCallBack;
import bc.zongshuo.com.ui.activity.IssueApplication;
import bc.zongshuo.com.ui.activity.MainActivity;
import bc.zongshuo.com.ui.activity.user.LoginActivity;
import bc.zongshuo.com.ui.activity.user.Regiest01Activity;
import bc.zongshuo.com.ui.activity.user.UpdatePasswordActivity;
import bc.zongshuo.com.utils.MyShare;
import bc.zongshuo.com.utils.UIUtils;
import bocang.json.JSONObject;
import bocang.utils.AppDialog;
import bocang.utils.AppUtils;
import bocang.utils.IntentUtil;
import bocang.utils.MyToast;

/**
 * @author: Jun
 * @date : 2017/2/7 14:07
 * @description :登录
 */
public class LoginController extends BaseController implements INetworkCallBack {
    private LoginActivity mView;
    private ImageView phone_iv, pwd_iv;
    private TextView regiest_tv, find_pwd_tv;
    private RelativeLayout pwd_rl;
    private TextView typeTv, type02Tv;
    private EditText phone_et, pwd_et;
    private int mType = 0;
    private Button login_bt;
    private String mCode;


    public LoginController(LoginActivity v) {
        mView = v;
        initView();
        InitViewData();
    }

    private void InitViewData() {
        if (mType == 0) {
            phone_et.setText(MyShare.get(mView).getString(Constance.USERNAME));
        }
        selectType(R.id.typeTv);
    }


    private void initView() {
        phone_iv = (ImageView) mView.findViewById(R.id.phone_iv);
        pwd_iv = (ImageView) mView.findViewById(R.id.pwd_iv);
        phone_et = (EditText) mView.findViewById(R.id.phone_et);
        pwd_et = (EditText) mView.findViewById(R.id.pwd_et);
        regiest_tv = (TextView) mView.findViewById(R.id.regiest_tv);
        find_pwd_tv = (TextView) mView.findViewById(R.id.find_pwd_tv);
        pwd_rl = (RelativeLayout) mView.findViewById(R.id.pwd_rl);
        type02Tv = (TextView) mView.findViewById(R.id.type02Tv);
        typeTv = (TextView) mView.findViewById(R.id.typeTv);
        login_bt = (Button) mView.findViewById(R.id.login_bt);
    }

    @Override
    protected void handleMessage(int action, Object[] values) {

    }

    @Override
    protected void myHandleMessage(Message msg) {

    }

    /**
     * 判断是通过什么方式登录
     *
     * @param type
     */
    public void selectType(int type) {
        typeTv.setTextColor(mView.getResources().getColor(R.color.fontColor2));
        type02Tv.setTextColor(mView.getResources().getColor(R.color.fontColor2));
        phone_et.setText("");
        switch (type) {
            case R.id.typeTv:
                typeTv.setTextColor(mView.getResources().getColor(R.color.green));
                pwd_rl.setVisibility(View.VISIBLE);
                phone_et.setHint(UIUtils.getString(R.string.him_phone));
                phone_iv.setImageResource(R.drawable.wode);
                phone_et.setText(MyShare.get(mView).getString(Constance.USERNAME));
                mType = 0;
                break;
            case R.id.type02Tv:
                type02Tv.setTextColor(mView.getResources().getColor(R.color.green));
                pwd_rl.setVisibility(View.GONE);
                phone_et.setHint(UIUtils.getString(R.string.him_invite_code));
                phone_iv.setImageResource(R.drawable.invite_code);
                mType = 1;
                break;
        }
    }

    /**
     * 登录
     */
    public void sendLogin() {
        mCode = phone_et.getText().toString();
        String pwd = pwd_et.getText().toString();
        if (AppUtils.isEmpty(mCode) && mType == 0) {
            AppDialog.messageBox(UIUtils.getString(R.string.isnull_accounts));
            return;
        }

        if (AppUtils.isEmpty(mCode) && mType == 1) {
            AppDialog.messageBox(UIUtils.getString(R.string.isnull_invitation_code));
            return;
        }

        //判断密码是否为空
        if (AppUtils.isEmpty(pwd) && mType == 0) {
            AppDialog.messageBox(UIUtils.getString(R.string.isnull_pwd));
            return;
        }
        String deviceId = UIUtils.getLocalMac(mView);
        mView.setShowDialog(true);
        mView.setShowDialog("正在登录中..");
        mView.showLoading();
        if (mType == 0) {//手机号码登录
            mNetWork.sendLogin(mCode, pwd,deviceId, this);
        } else {//邀请码登录
            //TODO

            sendUserCode(mCode);
        }
    }

    /**
     * 注册
     */
    public void sendRegiest() {
        IntentUtil.startActivity(mView, Regiest01Activity.class, false);
    }
    /**
     * 判断邀请码
     */
    public void sendUserCode(String code) {
       mNetWork.sendUserCode(code, this);
    }

    /**
     * 找回密码
     */
    public void sendFindPwd() {
        IntentUtil.startActivity(mView, UpdatePasswordActivity.class, false);
    }


    /**
     * 获取用户信息
     */

    public void sendUser() {
        mNetWork.sendUser(this);
    }

    @Override
    public void onSuccessListener(String requestCode, JSONObject ans) {
        try{
            switch (requestCode) {
                case NetWorkConst.LOGIN:
                    mView.hideLoading();
                    if (mType == 0) {
                        MyShare.get(mView).putString(Constance.USERNAME, mCode);
                        MyShare.get(mView).putString(Constance.USERID, ans.getJSONObject(Constance.user).getString(Constance.id));

                    }
                    String token = ans.getString(Constance.TOKEN);
                    MyShare.get(mView).putString(Constance.TOKEN, token);
                    sendUser();
                    break;
                case NetWorkConst.USERCODE:
                    mView.hideLoading();
                    if(AppUtils.isEmpty(ans.getJSONObject(Constance.user))){
                        MyToast.show(mView,"邀请码错误!");
                    }else{
                        MyShare.get(mView).putString(Constance.invite_code,mCode);
                        MyShare.get(mView).putInt(Constance.parant_level,ans.getJSONObject(Constance.user).getInt(Constance.level));
                        MyShare.get(mView).putString(Constance.USERCODE, ans.getJSONObject(Constance.user).getString(Constance.user_name));
                        MyShare.get(mView).putInt(Constance.USERCODEID, ans.getJSONObject(Constance.user).getInt(Constance.user_id));
                        IntentUtil.startActivity(mView, MainActivity.class, true);
                    }

                    break;
                case NetWorkConst.PROFILE:
                    JSONObject mUserObject = ans.getJSONObject(Constance.user);
                    IssueApplication.mUserObject = mUserObject;
                    IntentUtil.startActivity(mView, MainActivity.class, true);
//                getSuccessLogin();
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onFailureListener(String requestCode, JSONObject ans) {
        mView.hideLoading();
        if (null == mView || mView.isFinishing())
            return;
        if(AppUtils.isEmpty(ans)){
            AppDialog.messageBox(UIUtils.getString(R.string.server_error));
            return;
        }
        AppDialog.messageBox(ans.getString(Constance.error_desc));
    }




//    /**
//     * 分享操作
//     */
//    public void showShare(final String url, final String appName) {
//        ShareSDK.initSDK(mView);
//        HashMap<String, Object> wechat = new HashMap<String, Object>();
//        wechat.put("Id", "4");
//        wechat.put("SortId", "4");
//        wechat.put("AppId", "wx552a6247e06de962");
//        wechat.put("AppSecret", "fad84dbef5639004bd73c4bce0995c1d");
//        wechat.put("BypassApproval", "false");
//        wechat.put("Enable", "true");
//        ShareSDK.setPlatformDevInfo(Wechat.NAME, wechat);
//        ShareSDK.setPlatformDevInfo(WechatMoments.NAME, wechat);
//        OnekeyShare oks = new OnekeyShare();
//        //关闭sso授权
//        oks.disableSSOWhenAuthorize();
//
//        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
//        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
//        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
//        oks.setTitle(appName);
//        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
//        oks.setTitleUrl(url);
//        // text是分享文本，所有平台都需要这个字段
//        oks.setText("一款能约，能玩，还能赚钱的神器。");
//        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
//        // url仅在微信（包括好友和朋友圈）中使用
//        oks.setUrl(url);
//        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
//        oks.setComment(appName);
//        // site是分享此内容的网站名称，仅在QQ空间使用
//        oks.setSite(appName);
//        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
//        oks.setSiteUrl(url);
//
//        oks.setImageUrl("http://118.178.241.214/ic_launcher.png");
//        oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
//            @Override
//            public void onShare(Platform platform, cn.sharesdk.framework.Platform.ShareParams paramsToShare) {
//                if ("QZone".equals(platform.getName())) {
//                    paramsToShare.setTitle(appName);
//                    paramsToShare.setTitleUrl(url);
//                }
//                if ("SinaWeibo".equals(platform.getName())) {
//                    paramsToShare.setUrl(null);
//                    paramsToShare.setText("分享文本 ");
//                }
//                if ("Wechat".equals(platform.getName())) {
//                    Bitmap imageData = BitmapFactory.decodeResource(mView.getResources(), R.mipmap.ic_launcher);
//                    paramsToShare.setImageData(imageData);
//                }
//                if ("WechatMoments".equals(platform.getName())) {
//                    Bitmap imageData = BitmapFactory.decodeResource(mView.getResources(), R.mipmap.ic_launcher);
//                    paramsToShare.setImageData(imageData);
//                }
//
//            }
//        });
//
//        // 启动分享GUI
//        oks.show(mView);
//    }
}
