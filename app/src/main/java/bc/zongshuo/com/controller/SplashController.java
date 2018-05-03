package bc.zongshuo.com.controller;

import android.os.Message;

import bc.zongshuo.com.cons.Constance;
import bc.zongshuo.com.cons.NetWorkConst;
import bc.zongshuo.com.listener.INetworkCallBack;
import bc.zongshuo.com.ui.activity.IssueApplication;
import bc.zongshuo.com.ui.activity.SplashActivity;
import bc.zongshuo.com.utils.MyShare;
import bocang.json.JSONObject;
import bocang.utils.AppUtils;

/**
 * Created by XY on 2018/1/23.
 */

public class SplashController extends BaseController implements INetworkCallBack {
    private SplashActivity mView;

    public SplashController(SplashActivity view) {
        mView = view;
        initView();
        initViewData();
    }

    private void initViewData() {
        String token = MyShare.get(mView).getString(Constance.TOKEN);
        if (!AppUtils.isEmpty(token)) {
            sendUser();
        }
    }

    /**
     * 获取用户信息
     */

    public void sendUser() {
        mNetWork.sendUser(this);
    }

    private void initView() {

    }


    @Override
    protected void handleMessage(int action, Object[] values) {

    }

    @Override
    protected void myHandleMessage(Message msg) {

    }

    @Override
    public void onSuccessListener(String requestCode, JSONObject ans) {
        switch (requestCode) {
            case NetWorkConst.PROFILE:
                JSONObject mUserObject = ans.getJSONObject(Constance.user);
                IssueApplication.mUserObject = mUserObject;
                break;
        }

    }

    @Override
    public void onFailureListener(String requestCode, JSONObject ans) {
        getOutLogin(mView, ans);
    }
}
