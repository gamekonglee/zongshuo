package bc.zongshuo.com.ui.fragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.lib.common.hxp.view.PullToRefreshLayout;
import com.sevenonechat.sdk.compts.InfoSubmitActivity;
import com.sevenonechat.sdk.sdkinfo.SdkRunningClient;

import bc.zongshuo.com.R;
import bc.zongshuo.com.common.BaseFragment;
import bc.zongshuo.com.cons.Constance;
import bc.zongshuo.com.controller.HomeController;
import bc.zongshuo.com.ui.activity.IssueApplication;
import bc.zongshuo.com.ui.activity.product.SelectGoodsActivity;
import bc.zongshuo.com.ui.activity.programme.DiyActivity;
import bc.zongshuo.com.ui.activity.user.MessageActivity;
import bc.zongshuo.com.ui.activity.user.SimpleScannerActivity;
import bocang.utils.AppUtils;
import bocang.utils.IntentUtil;


/**
 * @author Jun
 * @time 2017/1/5  12:00
 * @desc 首页面
 */
public class HomeFragment extends BaseFragment implements View.OnClickListener {
    private HomeController mController;
    private ImageView lineIv;
    private ConvenientBanner convenientBanner;
    private EditText et_search;
    private ImageView topLeftBtn,go_diy_iv;
    private ImageView topRightBtn;
    private PullToRefreshLayout refresh_view;
    private TextView typeTv,styleTv,spaceTv,xinpin_tv,tejia_tv;
    public TextView unMessageTv,message_more_tv;
    private float mCurrentCheckedRadioLeft;//当前被选中的Button距离左侧的距离
    private TextView textView2;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fm_home, null);
    }

    @Override
    protected void initController() {
        mController = new HomeController(this);
    }

    @Override
    protected void initViewData() {
        initImageView();
        typeTv.performClick();
    }

    /**
     * 动态初始化界面图片
     */
    private void initImageView() {
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void initView() {
        unMessageTv = (TextView) getActivity().findViewById(R.id.unMessageTv);
        lineIv = (ImageView) getActivity().findViewById(R.id.lineIv);
        et_search = (EditText) getActivity().findViewById(R.id.et_search);
        topLeftBtn = (ImageView) getActivity().findViewById(R.id.topLeftBtn);
        topRightBtn = (ImageView) getActivity().findViewById(R.id.topRightBtn);
        go_diy_iv = (ImageView) getActivity().findViewById(R.id.go_diy_iv);
        message_more_tv = (TextView) getActivity().findViewById(R.id.message_more_tv);
        xinpin_tv = (TextView) getActivity().findViewById(R.id.xinpin_tv);
        et_search.setOnClickListener(this);
        topLeftBtn.setOnClickListener(this);
        topRightBtn.setOnClickListener(this);
        message_more_tv.setOnClickListener(this);
        xinpin_tv.setOnClickListener(this);
        typeTv = (TextView) getActivity().findViewById(R.id.typeTv);
        styleTv = (TextView) getActivity().findViewById(R.id.styleTv);
        spaceTv = (TextView) getActivity().findViewById(R.id.spaceTv);
        lineIv = (ImageView) getActivity().findViewById(R.id.lineIv);
        tejia_tv = (TextView) getActivity().findViewById(R.id.tejia_tv);
        textView2 = (TextView) getActivity().findViewById(R.id.textView2);
        typeTv.setOnClickListener(this);
        tejia_tv.setOnClickListener(this);
        styleTv.setOnClickListener(this);
        spaceTv.setOnClickListener(this);
        xinpin_tv.setOnClickListener(this);
        go_diy_iv.setOnClickListener(this);
        textView2.setOnClickListener(this);
        convenientBanner = (ConvenientBanner) getActivity().findViewById(R.id.convenientBanner);
        refresh_view = (PullToRefreshLayout) getActivity().findViewById(R.id.refresh_view);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onResume() {
        super.onResume();
        mController.setResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mController.setPause();
    }

    @Override
    public void onClick(View v) {
        AnimationSet animationSet = new AnimationSet(true);
        TranslateAnimation translateAnimation = null;
        mController.reSetTextColor();
        switch (v.getId()) {
            case R.id.topLeftBtn://扫描二维码

                IntentUtil.startActivity(this.getActivity(), SimpleScannerActivity.class, false);
                break;
            case R.id.topRightBtn://消息
                if (!isToken()) {
//                    IntentUtil.startActivity(this.getActivity(), ChartListActivity.class, false);
                    String id = IssueApplication.mUserObject.getString(Constance.id);
                    SdkRunningClient.getInstance().setUserUid(id);
                    Intent it = new Intent(v.getContext(), InfoSubmitActivity.class);
                    v.getContext().startActivity(it);
                }
                break;
            case R.id.et_search://搜索产品
                IntentUtil.startActivity(this.getActivity(), SelectGoodsActivity.class, false);
                break;
            case R.id.message_more_tv://更多消息
                IntentUtil.startActivity(getActivity(), MessageActivity.class, false);
                break;
            case R.id.xinpin_tv://新品更多
                mController.getMore02Goods(5);
                break;
            case R.id.tejia_tv://特价更多
                mController.getMoreGoods("214");
                break;
            case R.id.go_diy_iv://私人定制
                IntentUtil.startActivity(this.getActivity(), DiyActivity.class, false);
                break;
            case R.id.typeTv:
                mController.getType();
                translateAnimation = new TranslateAnimation(mCurrentCheckedRadioLeft, 0f, -20f, -20f);
                break;
            case R.id.styleTv:
                mController.getStyle();
                translateAnimation = new TranslateAnimation(mCurrentCheckedRadioLeft, mController.mScreenWidth / 3f, -20f, -20f);
                break;
            case R.id.spaceTv:
                mController.getSpace();
                translateAnimation = new TranslateAnimation(mCurrentCheckedRadioLeft, mController.mScreenWidth * 2f / 3f, -20f, -20f);
                break;
            case R.id.textView2:
                if(Build.VERSION.SDK_INT>=23){
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.CALL_PHONE)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission_group.PHONE}, 1);
                    }else{
                        //
                        diallPhone();
                    }
                }else {
                diallPhone();
                }
                break;

        }

        if (AppUtils.isEmpty(translateAnimation))
            return;
        animationSet.addAnimation(translateAnimation);
        animationSet.setFillBefore(false);
        animationSet.setFillAfter(true);
        animationSet.setDuration(100);
        lineIv.startAnimation(animationSet);
                mCurrentCheckedRadioLeft = mController.getCurrentCheckedRadioLeft(v);//更新当前横条距离左边的距离
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        diallPhone();
    }

    @Override
    public void onStart() {
        super.onStart();
//        String token = MyShare.get(getActivity()).getString(Constance.TOKEN);
//        if (AppUtils.checkNetwork() && !AppUtils.isEmpty(token)) {
//            getSuccessLogin();
//        }
    }
    public void diallPhone() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:" + "4000131668");
        intent.setData(data);
        startActivity(intent);
    }
//    /**
//     * 登录成功处理事件
//     */
//    private void getSuccessLogin() {
//        final String uid = MyShare.get(getActivity()).getString(Constance.USERID);
//        if (AppUtils.isEmpty(uid)) {
//            return;
//        }
//
//        if (!EMClient.getInstance().isLoggedInBefore() || !EMClient.getInstance().isConnected()) {
//            EMClient.getInstance().login(uid, uid, new EMCallBack() {
//                @Override
//                public void onSuccess() {
//                    Log.e("520it", "H登录成功");
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
//                    Log.e("520it", "H登录失败:" + message);
//                    if (message.equals("User dosn't exist")) {
//                        sendRegiestSuccess();
//                    }
//                }
//            });
//        }
//    }
//
//    /**
//     * 环信注册
//     */
//    private void sendRegiestSuccess() {
//        final String uid = MyShare.get(getActivity()).getString(Constance.USERID);
//        if (AppUtils.isEmpty(uid)) {
//            return;
//        }
//        new Thread(new Runnable() {
//            public void run() {
//                try {
//                    EMClient.getInstance().createAccount(uid, uid);//同步方法
//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Log.e("520it", "S注册成功!");
//                            getSuccessLogin();
//
//                        }
//                    });
//
//                } catch (final HyphenateException e) {
//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Log.e("520it", "S注册失败!：" + e.getMessage());
//                            //                            getSuccessLogin();
//                        }
//                    });
//
//                }
//            }
//        }).start();
//    }


}
