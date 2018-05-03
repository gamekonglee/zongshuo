package bc.zongshuo.com.wxapi;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;

import bc.zongshuo.com.cons.Constance;


public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";

    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(this, Constance.APP_ID);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onResp(BaseResp resp) {
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            Log.d("520it", "onPayFinish,errCode=" + resp.errCode);
            if (resp.errCode == 0) {
                Toast.makeText(this, "付款成功!", Toast.LENGTH_SHORT).show();
                EventBus.getDefault().post(Constance.WEISUCCESS);
                finish();
            } else if (resp.errCode == -2) {
                Toast.makeText(this, "您已取消付款!", Toast.LENGTH_SHORT).show();
                EventBus.getDefault().post(Constance.WEIFAIL);
                finish();
            } else {
                Toast.makeText(this, "参数错误", Toast.LENGTH_SHORT).show();
                EventBus.getDefault().post(Constance.WEIFAIL);
                finish();
            }
        } else {
            finish();
        }
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }
}