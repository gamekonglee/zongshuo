package bc.zongshuo.com.controller.user;

import android.os.Message;

import com.alibaba.fastjson.JSONObject;

import bc.zongshuo.com.controller.BaseController;
import bc.zongshuo.com.listener.INetworkCallBack02;
import bc.zongshuo.com.ui.activity.user.SimpleScannerLoginActivity;
import bocang.utils.MyToast;
import bocang.view.BaseActivity;

/**
 * Created by gamekonglee on 2018/3/12.
 */

public class SimpleScannerLoginController extends BaseController {

    private final BaseActivity mView;

    public SimpleScannerLoginController(BaseActivity simpleScannerLoginActivity,String android_id) {
        mView = simpleScannerLoginActivity;
        mNetWork.sendTokenAdd(android_id, new INetworkCallBack02() {
            @Override
            public void onSuccessListener(String requestCode, JSONObject ans) {
                mView.hideLoading();
                mView.finish();
//                MyToast.show(mView,ans.toJSONString());
            }

            @Override
            public void onFailureListener(String requestCode, JSONObject ans) {
            mView.hideLoading();
//            MyToast.show(mView,"failuter");
//            MyToast.show(mView,"failure"+ans.toJSONString());
            }
        });
    }

    @Override
    protected void handleMessage(int action, Object[] values) {

    }

    @Override
    protected void myHandleMessage(Message msg) {

    }



}
