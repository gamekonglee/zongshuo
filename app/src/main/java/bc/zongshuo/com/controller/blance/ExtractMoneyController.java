package bc.zongshuo.com.controller.blance;

import android.content.Intent;
import android.os.Message;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;

import bc.zongshuo.com.R;
import bc.zongshuo.com.cons.Constance;
import bc.zongshuo.com.cons.NetWorkConst;
import bc.zongshuo.com.controller.BaseController;
import bc.zongshuo.com.listener.INetworkCallBack;
import bc.zongshuo.com.listener.INetworkCallBack02;
import bc.zongshuo.com.ui.activity.IssueApplication;
import bc.zongshuo.com.ui.activity.blance.ExtractMoneyActivity;
import bc.zongshuo.com.ui.activity.blance.WithDrawalDetailActivity;
import bc.zongshuo.com.utils.MyShare;
import bocang.utils.AppUtils;
import bocang.utils.MyToast;

/**
 * @author: Jun
 * @date : 2017/7/10 18:00
 * @description :
 */
public class ExtractMoneyController extends BaseController {
    private ExtractMoneyActivity mView;
    private TextView money_tv,exchange_num_et,alipay_tv;
    private String money;
    private String exchange_num;
    private String alipay;

    public ExtractMoneyController(ExtractMoneyActivity v){
        mView=v;
        initView();
        initViewData();
    }

    private void initViewData() {
        if(AppUtils.isEmpty(IssueApplication.mUserObject))return;
        money= IssueApplication.mUserObject.getString(Constance.money);
        money_tv.setText("￥" + money);

    }

    private void initView() {
        money_tv = (TextView) mView.findViewById(R.id.money_tv);
        exchange_num_et = (TextView) mView.findViewById(R.id.exchange_num_et);
        alipay_tv = (TextView) mView.findViewById(R.id.alipay_tv);
    }

    @Override
    protected void handleMessage(int action, Object[] values) {

    }

    @Override
    protected void myHandleMessage(Message msg) {

    }

    /**
     * 提现余额
     */
    public void WithdrawalsMoney() {
        alipay=alipay_tv.getText().toString().trim();
        exchange_num=exchange_num_et.getText().toString().trim();
        String name= MyShare.get(mView).getString(Constance.ALIPAYNAME);
        if(AppUtils.isEmpty(alipay)){
            MyToast.show(mView,"提现帐号不能为空!");
            return;
        }
        if(AppUtils.isEmpty(exchange_num)){
            MyToast.show(mView,"提现金额不能为空!");
            return;
        }

        if(Float.parseFloat(money)<Float.parseFloat(exchange_num)){
            MyToast.show(mView,"提现金额不能大于余额!");
            return;
        }
        mView.setShowDialog(true);
        mView.setShowDialog("正在提现中...");
        mView.showLoading();
        mNetWork.sendAlipayMoney(exchange_num, alipay, name,new INetworkCallBack02() {
            @Override
            public void onSuccessListener(String requestCode, JSONObject ans) {
                try{
                    mView.hideLoading();
                    String state=ans.getString(Constance.state);
                    if(state.equals("success")){
                        sendUser();
                    }else{
                        MyToast.show(mView,"提现失败!,请重试!");
                    }
                }catch (Exception e){

                }

            }

            @Override
            public void onFailureListener(String requestCode, JSONObject ans) {
                mView.hideLoading();
                MyToast.show(mView, "提现失败!,请重试!");
                getOutLogin02(mView, ans);
            }
        });
    }

    /**
     * 获取用户信息
     */
    public void sendUser() {
        mNetWork.sendUser(new INetworkCallBack() {
            @Override
            public void onSuccessListener(String requestCode, bocang.json.JSONObject ans) {
                try{
                    switch (requestCode) {
                        case NetWorkConst.PROFILE:
                            bocang.json.JSONObject mUserObject = ans.getJSONObject(Constance.user);
                            IssueApplication.mUserObject=mUserObject;
                            money_tv.setText("￥" + money);
                            Intent intent=new Intent(mView, WithDrawalDetailActivity.class);
                            intent.putExtra(Constance.alipay,alipay);
                            intent.putExtra(Constance.money,exchange_num);
                            mView.startActivity(intent);
                            break;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailureListener(String requestCode, bocang.json.JSONObject ans) {
                getOutLogin(mView, ans);
            }
        });
    }

}
