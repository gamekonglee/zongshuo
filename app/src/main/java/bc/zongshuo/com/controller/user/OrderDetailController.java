package bc.zongshuo.com.controller.user;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alipay.sdk.app.PayTask;
import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.lib.common.hxp.view.ListViewForScrollView;
import com.sevenonechat.sdk.compts.InfoSubmitActivity;
import com.sevenonechat.sdk.sdkinfo.SdkRunningClient;

import java.text.DecimalFormat;

import bc.zongshuo.com.R;
import bc.zongshuo.com.bean.PayResult;
import bc.zongshuo.com.bean.PrepayIdInfo;
import bc.zongshuo.com.cons.Constance;
import bc.zongshuo.com.cons.NetWorkConst;
import bc.zongshuo.com.controller.BaseController;
import bc.zongshuo.com.listener.INetworkCallBack;
import bc.zongshuo.com.listener.INetworkCallBack02;
import bc.zongshuo.com.listener.IUpdateProductPriceListener;
import bc.zongshuo.com.ui.activity.IssueApplication;
import bc.zongshuo.com.ui.activity.buy.ExInventoryActivity;
import bc.zongshuo.com.ui.activity.product.ProDetailActivity;
import bc.zongshuo.com.ui.activity.user.OrderDetailActivity;
import bc.zongshuo.com.ui.adapter.OrderGvAdapter;
import bc.zongshuo.com.ui.view.ShowDialog;
import bc.zongshuo.com.utils.DateUtils;
import bc.zongshuo.com.utils.UIUtils;
import bc.zongshuo.com.utils.WXpayUtils;
import bocang.utils.AppDialog;
import bocang.utils.AppUtils;
import bocang.utils.MyToast;

import static bc.zongshuo.com.cons.Constance.sn;

/**
 * @author: Jun
 * @date : 2017/3/20 15:48
 * @description :
 */
public class OrderDetailController extends BaseController implements INetworkCallBack, OnItemClickListener {
    private OrderDetailActivity mView;
    private TextView title_tv, consignee_tv, phone_tv, address_tv, do_tv, do02_tv, do03_tv, order_code_tv, order_time_tv, chat_buy_tv,consigment_tv;
    private TextView go_state_tv, total_tv, old_money, new_money, remark_tv, update_money_tv,order_remark_tv,log_sv_tv,log_remark_tv,consignment_time_title_tv;
    private ImageView go_state_iv;
    private ListViewForScrollView order_detail_lv;
    private OrderGvAdapter mAGvAdapter;
    private int mOrderId = 0;
    private String order_code;
    private LinearLayout order_payment_ll,payment_ll;
    private int mLevel=-1;
    private int mOrderLevel=-1;
    private RelativeLayout log_rl;
    private Boolean mIsUpdate=false;

    private int mProductPosition = 0;
    private JSONArray mProductArray;
    private String mUpdateorderSn = "";
    public double mTotalProductMoney = 0;
    private float mDiscount = 1;

    private AlertView mAlertViewExt02;//窗口拓展例子
    private EditText etName02;//拓展View内容
    private InputMethodManager imm02;
    private float mUpdateOrderMoney = 0;
    private String mUpdateorderId = "";

    public AlertView mAlertViewExt;//窗口拓展例子
    private EditText etName;//拓展View内容
    private InputMethodManager imm;
    private float mProductDiscount=0;//返回产品打折范围

    private CheckBox appliay_cb;


    public OrderDetailController(OrderDetailActivity v) {
        mView = v;
        initView();
        if(AppUtils.isEmpty(mView.mOrderSn)){
            initViewData();
            sendOrderDetail(mView.mOrderObject.getString(sn));
        }else{
            sendOrderDetail(mView.mOrderSn);
        }

    }

    private void initView() {
        title_tv = (TextView) mView.findViewById(R.id.title_tv);
        consignee_tv = (TextView) mView.findViewById(R.id.consignee_tv);
        phone_tv = (TextView) mView.findViewById(R.id.phone_tv);
        address_tv = (TextView) mView.findViewById(R.id.address_tv);
        go_state_tv = (TextView) mView.findViewById(R.id.go_state_tv);
        do_tv = (TextView) mView.findViewById(R.id.do_tv);
        do02_tv = (TextView) mView.findViewById(R.id.do02_tv);
        do03_tv = (TextView) mView.findViewById(R.id.do03_tv);
        chat_buy_tv = (TextView) mView.findViewById(R.id.chat_buy_tv);
        order_code_tv = (TextView) mView.findViewById(R.id.order_code_tv);
        order_time_tv = (TextView) mView.findViewById(R.id.order_time_tv);
        update_money_tv = (TextView) mView.findViewById(R.id.update_money_tv);
        total_tv = (TextView) mView.findViewById(R.id.total_tv);
        old_money = (TextView) mView.findViewById(R.id.old_money);
        new_money = (TextView) mView.findViewById(R.id.new_money);
        appliay_cb = (CheckBox) mView.findViewById(R.id.appliay_cb);
        remark_tv = (TextView) mView.findViewById(R.id.remark_tv);
        order_remark_tv = (TextView) mView.findViewById(R.id.order_remark_tv);
        order_detail_lv = (ListViewForScrollView) mView.findViewById(R.id.order_detail_lv);
        go_state_iv = (ImageView) mView.findViewById(R.id.go_state_iv);
        order_payment_ll = (LinearLayout) mView.findViewById(R.id.order_payment_ll);
        payment_ll = (LinearLayout) mView.findViewById(R.id.payment_ll);
        consigment_tv = (TextView) mView.findViewById(R.id.consigment_tv);
        log_sv_tv = (TextView) mView.findViewById(R.id.log_sv_tv);
        log_remark_tv = (TextView) mView.findViewById(R.id.log_remark_tv);
        consignment_time_title_tv = (TextView) mView.findViewById(R.id.consignment_time_title_tv);
        log_rl = (RelativeLayout) mView.findViewById(R.id.log_rl);

        imm02 = (InputMethodManager) mView.getSystemService(Context.INPUT_METHOD_SERVICE);
        //拓展窗口
        mAlertViewExt02 = new AlertView("提示", "请输入价格折扣", "取消", null, new String[]{"完成"}, mView, AlertView.Style.Alert, this);
        ViewGroup extView02 = (ViewGroup) LayoutInflater.from(mView).inflate(R.layout.alertext_form, null);
        etName02 = (EditText) extView02.findViewById(R.id.etName);
        etName02.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean focus) {
                //输入框出来则往上移动
                boolean isOpen = imm02.isActive();
                mAlertViewExt02.setMarginBottom(isOpen && focus ? 120 : 0);
                System.out.println(isOpen);
            }
        });
        mAlertViewExt02.addExtView(extView02);

        imm = (InputMethodManager) mView.getSystemService(Context.INPUT_METHOD_SERVICE);
        //拓展窗口
        mAlertViewExt = new AlertView("提示", "请输入价格折扣(不能低于8折)！", "取消", null, new String[]{"完成"}, mView, AlertView.Style.Alert, this);
        ViewGroup extView = (ViewGroup) LayoutInflater.from(mView).inflate(R.layout.alertext_form, null);
        etName = (EditText) extView.findViewById(R.id.etName);
        etName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean focus) {
                //输入框出来则往上移动
                boolean isOpen = imm.isActive();
                mAlertViewExt.setMarginBottom(isOpen && focus ? 120 : 0);
                System.out.println(isOpen);
            }
        });
        mAlertViewExt.addExtView(extView);
    }

    private void initViewData() {
        if (AppUtils.isEmpty(mView.mOrderObject))
            return;
        if( mView.mStatus==-1){
            mView.mStatus = mView.mOrderObject.getInteger(Constance.status);
        }


        mLevel= IssueApplication.mUserObject.getInt(Constance.level_id);
        getState(mView.mStatus);
        JSONObject consigneeObject = mView.mOrderObject.getJSONObject(Constance.consignee);
        final int state = mView.mOrderObject.getInteger(Constance.status);
        consignee_tv.setText(consigneeObject.getString(Constance.name));
        phone_tv.setText(consigneeObject.getString(Constance.mobile));
        address_tv.setText(consigneeObject.getString(Constance.address));
        order_code = mView.mOrderObject.getString(Constance.sn);
        final String id = mView.mOrderObject.getString(Constance.id);
        final String discount =  mView.mOrderObject.getString(Constance.discount);
        order_code_tv.setText(order_code);
        order_time_tv.setText(DateUtils.getStrTime(mView.mOrderObject.getString(Constance.created_at)));
        mProductArray = mView.mOrderObject.getJSONArray(Constance.goods);
        if (AppUtils.isEmpty(mProductArray))
            return;
        mAGvAdapter = new OrderGvAdapter(mView, mProductArray,mOrderLevel, state);
        order_detail_lv.setAdapter(mAGvAdapter);

        mAGvAdapter.setUpdateProductPriceListener(new IUpdateProductPriceListener() {
            @Override
            public void onUpdateProductPriceListener(int position, com.alibaba.fastjson.JSONObject object) {
                JSONObject jsonObject = mProductArray.getJSONObject(position);
                String itemDiscount=jsonObject.getString(Constance.discount);
                if(AppUtils.isEmpty(itemDiscount) || "0".equals(itemDiscount)){
                    mProductDiscount= (float) 0.8;
                }else{
                    mProductDiscount=Float.parseFloat(itemDiscount);
                }
                mUpdateorderId = id;
                mUpdateorderSn = order_code;
                mDiscount = Float.parseFloat(discount);
                mProductPosition = position;
                mAlertViewExt02.show();
            }
        });
        order_detail_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mView, ProDetailActivity.class);
                int productId = mProductArray.getJSONObject(position).getInteger(Constance.id);
                intent.putExtra(Constance.product, productId);
                intent.putExtra(Constance.order_id, mOrderId+"");
                mView.startActivity(intent);
            }
        });
        mOrderId = mView.mOrderObject.getInteger(Constance.id);
        int tatalNum = 0;
        String total = mView.mOrderObject.getString(Constance.total);
        for (int i = 0; i < mProductArray.size(); i++) {
            tatalNum += mProductArray.getJSONObject(i).getInteger(Constance.total_amount);
        }
        total_tv.setText("共计 " + tatalNum + " 件商品 合计" + total + "元");
        final String score = mView.mOrderObject.getJSONObject("score").getString("score");
        old_money.setText("市场价:￥" + score);
        Double avg =  ((Double.parseDouble(total) / Double.parseDouble(score)) * 100);
        if (avg == 100) {
            String str = "优惠价:￥" + total;
            new_money.setText(str);
        } else {
            DecimalFormat decimalFormat = new DecimalFormat(".0");//构造方法的字符格式这里如果小数不足2位,会以0补足.
            String p = decimalFormat.format(avg * 0.1);//format 返回的是字符串
            String val = total ;
            String str = "优惠价:￥" + val;
            new_money.setText(str);
        }
        String user_name = mView.mOrderObject.getString(Constance.user_name);
        mOrderLevel= mView.mOrderObject.getInteger(Constance.level);
        String levelValue = "";

        if (mOrderLevel == 101) {
            levelValue = "平台客户";
        } else if (mOrderLevel == 102) {
            levelValue = "代理商";
        } else if (mOrderLevel == 103) {
            levelValue = "加盟商";
        } else if(mOrderLevel == 104){
            levelValue = "经销商";
        }else if(mOrderLevel == 105){
            levelValue = "消费者";
        }


//        if (mOrderLevel == 0) {
//            levelValue = "一级";
//        } else if (mOrderLevel == 1) {
//            levelValue = "二级";
//        } else if (mOrderLevel == 2) {
//            levelValue = "三级";
//        } else {
//            levelValue = "消费者";
//        }
        //        levelValue = levelValue + "(" + user_name + ")";
        //        remark_tv.setText(levelValue);

        if (mLevel == 100) {
            levelValue = levelValue + "(" + user_name + ")";
            remark_tv.setText(levelValue);
            if (mLevel != mOrderLevel && mView.mStatus == 0) {
                remark_tv.setVisibility(View.VISIBLE);
                mIsUpdate=true;
                update_money_tv.setVisibility(View.GONE);
                if (mView.mStatus == 0) {
                    do_tv.setVisibility(View.GONE);
                    do02_tv.setVisibility(View.GONE);
                    do03_tv.setVisibility(View.GONE);
                    chat_buy_tv.setVisibility(View.VISIBLE);
                }

            } else {
                mIsUpdate=false;
                update_money_tv.setVisibility(View.GONE);
            }

        } else {
            remark_tv.setVisibility(View.GONE);
            update_money_tv.setVisibility(View.GONE);
            mIsUpdate=false;
        }
        String postscript=mView.mOrderObject.getString(Constance.postscript);

        order_remark_tv.setText(postscript);

        JSONObject shippingObject = mView.mOrderObject.getJSONObject(Constance.shipping);
        if(!AppUtils.isEmpty(shippingObject)){
            log_sv_tv.setText(shippingObject.getString("invoice_no"));
            log_remark_tv.setText(shippingObject.getString("shipping_name"));
            String shoppingTime=mView.mOrderObject.getString("shipping_at");
            if(!AppUtils.isEmpty(shoppingTime))
            consignment_time_title_tv.setText("发货时间:  "+DateUtils.getStrTime(shoppingTime));
        }

    }

    /**
     * 获取订单详情
     * @param orderCode
     */
    private void sendOrderDetail(String orderCode) {
        mNetWork.semdOrderSearch(orderCode, new INetworkCallBack02() {
            @Override
            public void onSuccessListener(String requestCode, com.alibaba.fastjson.JSONObject ans) {
                try{
                    mView.hideLoading();
                    switch (requestCode) {
                        case NetWorkConst.ORDERLIST:
                            if (null == mView || mView.isFinishing())
                                return;
                            break;
                    }
                    mView.mOrderObject=ans.getJSONObject(Constance.orders);
                    initViewData();
                }catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailureListener(String requestCode, com.alibaba.fastjson.JSONObject ans) {
                mView.hideLoading();
                MyToast.show(mView, "网络异常，请重新加载!");
                getOutLogin02(mView, ans);
            }
        });
    }



    /**
     * 订单状态
     *
     * @param type
     */
    private void getState(int type) {
        do_tv.setVisibility(View.GONE);
        do02_tv.setVisibility(View.GONE);
        do03_tv.setVisibility(View.GONE);
        consigment_tv.setVisibility(View.GONE);
        log_rl.setVisibility(View.GONE);
        consignment_time_title_tv.setVisibility(View.GONE);
        String stateValue = "订单详情";
        switch (type) {
            case 0:
                do_tv.setVisibility(View.VISIBLE);
                do02_tv.setVisibility(View.VISIBLE);
                do03_tv.setVisibility(View.VISIBLE);
                do_tv.setText("付款");
                do02_tv.setText("取消订单");
                go_state_tv.setText("等待买家付款");
                go_state_iv.setImageResource(R.drawable.wait_pay);
                order_payment_ll.setVisibility(View.GONE);
                payment_ll.setVisibility(View.VISIBLE);
                break;
            case 1:
                do_tv.setVisibility(View.VISIBLE);
                do_tv.setText("联系商家");
                go_state_tv.setText("等待商家发货");
                go_state_iv.setImageResource(R.drawable.wait_send_goos);
                order_payment_ll.setVisibility(View.VISIBLE);
                payment_ll.setVisibility(View.GONE);
                if(mLevel==0)
                consigment_tv.setVisibility(View.VISIBLE);
                break;
            case 2:
                if(mOrderLevel==mLevel){
                    do_tv.setVisibility(View.VISIBLE);
                    do_tv.setText("确认收货");
                }
                do03_tv.setVisibility(View.VISIBLE);

                go_state_tv.setText("等待买家收货");
                go_state_iv.setImageResource(R.drawable.wait_receipt_goods);
                order_payment_ll.setVisibility(View.VISIBLE);
                payment_ll.setVisibility(View.GONE);
                log_rl.setVisibility(View.VISIBLE);
                consignment_time_title_tv.setVisibility(View.VISIBLE);
                break;
            case 4:
                do_tv.setVisibility(View.VISIBLE);
                do_tv.setText("联系商家");
                go_state_tv.setText("订单已完成");
                go_state_iv.setImageResource(R.drawable.al_complete);
                order_payment_ll.setVisibility(View.VISIBLE);
                payment_ll.setVisibility(View.GONE);
                log_rl.setVisibility(View.VISIBLE);
                consignment_time_title_tv.setVisibility(View.VISIBLE);
                break;
            case 3:
                do_tv.setVisibility(View.VISIBLE);
                do_tv.setText("联系商家");
                go_state_tv.setText("订单已完成");
                go_state_iv.setImageResource(R.drawable.al_complete);
                order_payment_ll.setVisibility(View.VISIBLE);
                payment_ll.setVisibility(View.GONE);
                log_rl.setVisibility(View.VISIBLE);
                consignment_time_title_tv.setVisibility(View.VISIBLE);
                break;
            case 5:
                do_tv.setVisibility(View.VISIBLE);
                do_tv.setText("联系商家");
                go_state_tv.setText("订单已取消");
                go_state_iv.setImageResource(R.drawable.al_cancel);
                order_payment_ll.setVisibility(View.VISIBLE);
                payment_ll.setVisibility(View.GONE);
                break;
        }
        title_tv.setText(stateValue);
    }


    @Override
    protected void handleMessage(int action, Object[] values) {

    }

    @Override
    protected void myHandleMessage(Message msg) {

    }

    private void sendOrderCancel(String order, String reason) {
        mNetWork.sendOrderCancel(order, reason, this);
    }

    public void doOrder() {
        if (mOrderId == 0)
            return;
        //TODO
        if (mView.mStatus == 1) {
            String id1= IssueApplication.mUserObject.getString(Constance.id);
            SdkRunningClient.getInstance().setUserUid(id1);
            Intent it = new Intent(mView, InfoSubmitActivity.class);
        } else if (mView.mStatus == 0) {
            mView.setShowDialog(true);
            mView.setShowDialog("正在付款中!");
            mView.showLoading();
            if(appliay_cb.isChecked()){
                sendPayment(mOrderId + "", "alipay.app");
            }else{
                sendPayment(mOrderId + "", "wxpay.app");
            }

        } else if (mView.mStatus == 2) {
            //TODO 确认收货
            sendConfirmReceipt(mOrderId+"");
        } else if (mView.mStatus == 5) {
            String id1= IssueApplication.mUserObject.getString(Constance.id);
            SdkRunningClient.getInstance().setUserUid(id1);
            Intent it = new Intent(mView, InfoSubmitActivity.class);
            mView.startActivity(it);
        } else if (mView.mStatus == 4) {
            String id1= IssueApplication.mUserObject.getString(Constance.id);
            SdkRunningClient.getInstance().setUserUid(id1);
            Intent it = new Intent(mView, InfoSubmitActivity.class);
            mView.startActivity(it);
        }
    }



    /**
     * 确认收货
     */
    private void sendConfirmReceipt(final String order){
        ShowDialog mDialog=new ShowDialog();
        mDialog.show(mView, "提示", "是否确认收货?", new ShowDialog.OnBottomClickListener() {
            @Override
            public void positive() {
                mView.setShowDialog(true);
                mView.setShowDialog("确认收货中!");
                mView.showLoading();
                mNetWork.sendConfirmReceipt(order, new INetworkCallBack02() {
                    @Override
                    public void onSuccessListener(String requestCode, com.alibaba.fastjson.JSONObject ans) {
                        try{
                            mView.hideLoading();
                            MyToast.show(mView, "确认收货成功!");
                            mView.mStatus=3;
                            sendOrderDetail(mView.mOrderObject.getString(sn));
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailureListener(String requestCode, com.alibaba.fastjson.JSONObject ans) {
                        mView.hideLoading();
                        MyToast.show(mView, "确认收货失败!");
                        getOutLogin02(mView, ans);
                    }
                });
            }

            @Override
            public void negtive() {

            }
        });

    }


    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);
                    /**
                     * 同步返回的结果必须上传到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
                     * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
                     * docType=1) 建议商户依赖异步通知
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息


                    String resultStatus = payResult.getResultStatus();
                    Log.d("TAG", "resultStatus=" + resultStatus);
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        MyToast.show(mView, "支付成功");
                        mView.finish();
                    } else {
                        // 判断resultStatus 为非"9000"则代表可能支付失败
                        // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            AppDialog.messageBox("支付结果确认中");

                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            AppDialog.messageBox("支付失败");
                            mView.hideLoading();

                        }
                    }
                    break;
                }
                default:
                    break;
            }
        }
    };

    PrepayIdInfo bean;

    /**
     * 支付订单
     *
     * @param order
     * @param code
     */
    private void sendPayment(String order, final String code) {
        mView.hideLoading();
        mNetWork.sendPayment(order, code, new INetworkCallBack02() {
            @Override
            public void onSuccessListener(String requestCode, com.alibaba.fastjson.JSONObject ans) {
                try{
                    if(code.equals("alipay.app")){
                        String notify_url = ans.getString(Constance.alipay);
                        if (AppUtils.isEmpty(notify_url))
                            return;
                        SubmitAliPay(notify_url);
                    }else{
                        com.alibaba.fastjson.JSONObject wxpayObject = ans.getJSONObject(Constance.wxpay);
                        String appid= wxpayObject.getString("appid");
                        String mch_id= wxpayObject.getString("mch_id");
                        String nonce_str= wxpayObject.getString("nonce_str");
                        String packages= wxpayObject.getString("packages");
                        String prepay_id= wxpayObject.getString("prepay_id");
                        String sign= wxpayObject.getString("sign");
                        String timestamp= wxpayObject.getString("timestamp");
                        bean=new PrepayIdInfo();
                        bean.setAppid(appid);
                        bean.setMch_id(mch_id);
                        bean.setNonce_str(nonce_str);
                        bean.setPrepay_id(prepay_id);
                        bean.setSign(sign);
                        bean.setTimestamp(timestamp);
                        WXpayUtils.mContext=mView;
                        WXpayUtils.Pay(bean,bean.getPrepay_id());
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailureListener(String requestCode, com.alibaba.fastjson.JSONObject ans) {
                mView.hideLoading();
                if (null == mView || mView.isFinishing())
                    return;
                if (AppUtils.isEmpty(ans)) {
                    AppDialog.messageBox(UIUtils.getString(R.string.server_error));
                    return;
                }
                AppDialog.messageBox(ans.getString(Constance.error_desc));
                getOutLogin02(mView, ans);
            }
        });

    }

    /**
     * 支付宝支付
     */
    private void SubmitAliPay(String notifyUrl) {
        //开始支付
        aliPay(notifyUrl);
    }

    //标记是支付
    private static final int SDK_PAY_FLAG = 1;
    private static final String TAG = "PayActivity";

    /**
     * 开始-支付宝支付
     */
    private void aliPay(final String ss) {
        Runnable payRunnable = new Runnable() {
            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(mView);
                // 调用支付接口，获取支付结果
                String result = alipay.pay(ss, true);
                //异步处理支付结果
                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }


    public void do02Order() {
        if (mOrderId == 0)
            return;
        if (mView.mStatus == 0) {
            mView.setShowDialog(true);
            mView.setShowDialog("正在取消中!");
            mView.showLoading();
            sendOrderCancel(mOrderId + "", "1");
        }
    }

    public void do03Order() {
        String id1= IssueApplication.mUserObject.getString(Constance.id);
        SdkRunningClient.getInstance().setUserUid(id1);
        Intent it = new Intent(mView, InfoSubmitActivity.class);
        mView.startActivity(it);
    }

    @Override
    public void onSuccessListener(String requestCode, bocang.json.JSONObject ans) {
        try{
            switch (requestCode) {
                case NetWorkConst.ORDERCANCEL:
                    if (ans.getInt(Constance.error_code) == 0) {
                        MyToast.show(mView, "取消成功!");
                        mView.finish();
                    } else {
                        MyToast.show(mView, "订单取消失败!");
                    }

                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onFailureListener(String requestCode, bocang.json.JSONObject ans) {
        mView.hideLoading();
        if (null == mView || mView.isFinishing())
            return;
        if (AppUtils.isEmpty(ans)) {
            AppDialog.messageBox(UIUtils.getString(R.string.server_error));
            return;
        }
        AppDialog.messageBox(ans.getString(Constance.error_desc));
        getOutLogin(mView, ans);
    }

    /**
     * 复制订单号
     */
    public void getCopyOrder() {
        ClipboardManager cm = (ClipboardManager) mView.getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setText(order_code);
        MyToast.show(mView, "复制成功!");
    }


    /**
     * 修改订单价格
     */
    public void setUpdateMoney(float avg) {
        final String score = mView.mOrderObject.getJSONObject("score").getString("score");
        float updateOrderMoney = Float.parseFloat(score);
        float money = (float) (updateOrderMoney * (avg * 0.1));
        String mUpdateorderId = mView.mOrderObject.getString(sn);
        mView.setShowDialog(true);
        mView.setShowDialog("正在修改订单价格!");
        mView.showLoading();
        updatePrice(mUpdateorderId, money,"");
    }


    /**
     * 修改订单价格
     *
     * @param orderId 订单号
     * @param money   价格
     */
    private void updatePrice(final String orderId, double money,String discount) {
        mNetWork.updatePrice(orderId, money, discount,new INetworkCallBack02() {
            @Override
            public void onSuccessListener(String requestCode, com.alibaba.fastjson.JSONObject ans) {
                try{
                    sendOrderList(orderId);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailureListener(String requestCode, com.alibaba.fastjson.JSONObject ans) {
                mView.hideLoading();
                MyToast.show(mView, "修改价格失败!");
                getOutLogin02(mView, ans);
            }
        });
    }

    private void sendOrderList(String orderCode) {
        mNetWork.semdOrderSearch(orderCode, new INetworkCallBack02() {
            @Override
            public void onSuccessListener(String requestCode, com.alibaba.fastjson.JSONObject ans) {
                try{
                    mView.hideLoading();
                    mView.mOrderObject = ans.getJSONObject(Constance.orders);
                    initViewData();
                    MyToast.show(mView, "修改成功!");
                }catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailureListener(String requestCode, com.alibaba.fastjson.JSONObject ans) {
                mView.hideLoading();
                MyToast.show(mView, "网络异常，请重新加载!");
                getOutLogin02(mView, ans);
            }
        });
    }

//    /**
//     * 联系客服
//     */
//    public void sendCall(String msg, final String parent_id, final String parent_name, final String userIcon) {
//        try {
//            //            if (AppUtils.isEmpty(IssueApplication.mUserObject.getString("parent_name"))) {
//            //                MyToast.show(mView, "不能和自己聊天!");
//            //                return;
//            //            }
//
//            EaseUser user = new EaseUser(parent_id);
//            user.setNickname(parent_name);
//            user.setNick(parent_name);
//            user.setAvatar(userIcon);
//            DemoHelper.getInstance().saveContact(user);
//
//
//            if (!EMClient.getInstance().isLoggedInBefore()) {
//                ShowDialog mDialog = new ShowDialog();
//                mDialog.show(mView, "提示", msg, new ShowDialog.OnBottomClickListener() {
//                    @Override
//                    public void positive() {
//                        loginHX(parent_id, parent_name, userIcon);
//                    }
//
//                    @Override
//                    public void negtive() {
//
//                    }
//                });
//            } else {
//                EMClient.getInstance().contactManager().acceptInvitation(parent_id);
//                mView.startActivity(new Intent(mView, ChatActivity.class).putExtra(EaseConstant.EXTRA_USER_ID, parent_id));
//            }
//        } catch (HyphenateException e) {
//            e.printStackTrace();
//        }
//    }
//
//    //登录环信
//    private void loginHX(final String parent_id, final String parent_name, final String userIcon) {
//        final Toast toast = Toast.makeText(mView, "服务器连接中...!", Toast.LENGTH_SHORT);
//        toast.show();
//        if (NetUtils.hasNetwork(mView)) {
//            final String uid = MyShare.get(mView).getString(Constance.USERID);
//            if (AppUtils.isEmpty(uid)) {
//                return;
//            }
//            if (!TextUtils.isEmpty(uid)) {
//                getSuccessLogin(uid, toast, parent_id, parent_name, userIcon);
//
//
//            }
//        }
//    }
//
//    private void getSuccessLogin(final String uid, final Toast toast, final String parent_id, final String parent_name, final String userIcon) {
//        EMClient.getInstance().login(uid, uid, new EMCallBack() {
//            @Override
//            public void onSuccess() {
//                EMClient.getInstance().groupManager().loadAllGroups();
//                EMClient.getInstance().chatManager().loadAllConversations();
//                MyLog.e("登录环信成功!");
//                toast.cancel();
//                String parent_name = IssueApplication.mUserObject.getString("parent_name");
//                try {
//                    EMClient.getInstance().contactManager().acceptInvitation(parent_id);
//                    mView.startActivity(new Intent(mView, ChatActivity.class).putExtra(EaseConstant.EXTRA_USER_ID, parent_id));
//                } catch (HyphenateException e) {
//                    e.printStackTrace();
//                }
//
//            }
//
//            @Override
//            public void onError(int i, String s) {
//                if (s.equals("User dosn't exist")) {
//                    new Thread(new Runnable() {
//                        public void run() {
//                            try {
//                                EMClient.getInstance().createAccount(uid, uid);//同步方法
//                                mView.runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        MyLog.e("注册成功!");
//                                        getSuccessLogin(uid, toast, parent_id, parent_name, userIcon);
//                                    }
//                                });
//
//                            } catch (final HyphenateException e) {
//                                mView.runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        MyLog.e("注册失败!");
//                                        getSuccessLogin(uid, toast, parent_id, parent_name, userIcon);
//                                    }
//                                });
//
//                            }
//                        }
//                    }).start();
//                } else {
//                    Toast.makeText(mView, "连接失败,请重试!", Toast.LENGTH_SHORT).show();
//                    MyLog.e("登录环信失败!");
//                    sendCall("连接聊天失败,请重试?", parent_id, parent_name, userIcon);
//                }
//
//
//            }
//
//            @Override
//            public void onProgress(int i, String s) {
//            }
//        });
//    }

//    /**
//     * 联系买家
//     */
//    public void chatBuy() {
//
//        String user_name = mView.mOrderObject.getString(Constance.user_name);
//        String user_id = mView.mOrderObject.getString(Constance.user_id);
//        String user_icon = NetWorkConst.SCENE_HOST + mView.mOrderObject.getJSONObject(Constance.avatar).getString(Constance.avatar);
//        sendCall("尝试连接聊天服务..请连接?", user_id, user_name, user_icon);
//    }

    /**
     * 导出PDF
     */
    public void getPDF() {
        Intent intent=new Intent(mView,ExInventoryActivity.class);
        intent.putExtra(Constance.goods, mView.mOrderObject.toJSONString());
        intent.putExtra(Constance.pdfType, 1);
        mView.startActivity(intent);
    }

    public void ActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constance.FROMCONSIGNMENT) {
            mView.mStatus=2;
            initViewData();
        }
    }

    private void closeKeyboard02() {
        //关闭软键盘
        imm02.hideSoftInputFromWindow(etName02.getWindowToken(), 0);
        //恢复位置
        mAlertViewExt02.setMarginBottom(0);
    }

    private void closeKeyboard() {
        //关闭软键盘
        imm.hideSoftInputFromWindow(etName.getWindowToken(), 0);
        //恢复位置
        mAlertViewExt.setMarginBottom(0);
    }

    @Override
    public void onItemClick(Object o, int position) {
        closeKeyboard02();
        closeKeyboard();
        if (o == mAlertViewExt02 && position != AlertView.CANCELPOSITION) {
            String name = etName02.getText().toString();
            if (name.isEmpty()) {
                //                        MyToast.show(mView.getActivity(),"请输入需要查询的订单号!");
            } else {
                Float ratio = Float.parseFloat(name);
                if (ratio < mProductDiscount*10) {
                    int num= (int) (mProductDiscount*10);
                    MyToast.show(mView, "输入的价格折扣不能小于"+num+"折");
                    return;
                } else if (ratio > 10) {
                    MyToast.show(mView, "输入的价格折扣不能大于10折");
                    return;
                }

                com.alibaba.fastjson.JSONObject jsonObject = mProductArray.getJSONObject(mProductPosition);
                String id = jsonObject.getString(Constance.id);
                String price = jsonObject.getString(Constance.original_price);
                int total_amount = jsonObject.getInteger(Constance.total_amount);

                float productPrice = (float) (Float.parseFloat(price) * (ratio * 0.1));

                mView.setShowDialog(true);
                mView.setShowDialog("正在修改订单产品价格!");
                mView.showLoading();
                mTotalProductMoney = 0;
                for (int i = 0; i < mProductArray.size(); i++) {
                    if (i == mProductPosition) {
                        mTotalProductMoney = mTotalProductMoney + productPrice * total_amount;
                    } else {
                        com.alibaba.fastjson.JSONObject itemObject = mProductArray.getJSONObject(i);
                        String total_price = itemObject.getString(Constance.total_price);
                        mTotalProductMoney = mTotalProductMoney + Float.parseFloat(total_price);
                    }
                }
                //判断是否有折扣
                if (mDiscount != 0) {
                    mTotalProductMoney = mTotalProductMoney * mDiscount;
                } else {
                    mDiscount = 1;
                }

                updateProductPrice(mUpdateorderId, id, productPrice + "");

            }
        }else if (o == mAlertViewExt && position != AlertView.CANCELPOSITION) {
            String name = etName.getText().toString();
            if (name.isEmpty()) {
                //                        MyToast.show(mView.getActivity(),"请输入需要查询的订单号!");
            } else {
                Float ratio = Float.parseFloat(name);
                if (ratio < 8) {
                    MyToast.show(mView, "输入的价格折扣不能小于8折");
                    return;
                } else if (ratio > 10) {
                    MyToast.show(mView, "输入的价格折扣不能大于10折");
                    return;
                }
                mDiscount = (float) (ratio * 0.1);
                mTotalProductMoney = 0;
                for (int i = 0; i < mProductArray.size(); i++) {
                    com.alibaba.fastjson.JSONObject itemObject = mProductArray.getJSONObject(i);
                    String total_price = itemObject.getString(Constance.total_price);
                    mTotalProductMoney = mTotalProductMoney + Float.parseFloat(total_price);
                }
                float money = (float) (mTotalProductMoney * mDiscount);

                mView.setShowDialog(true);
                mView.setShowDialog("正在修改订单价格!");
                mView.showLoading();
                updatePrice(mUpdateorderSn, money, mDiscount + "");
                //                        mController.SearchOrder(name);
            }

            return;
        }


    }

        /**
         * 修改订单产品价格
         *
         * @param orderId
         * @param goods_id
         * @param goods_amount
         */
        private void updateProductPrice(String orderId, String goods_id, String goods_amount) {
            mNetWork.updateProductPrice(orderId, goods_id, goods_amount, new INetworkCallBack02() {
                @Override
                public void onSuccessListener(String requestCode, com.alibaba.fastjson.JSONObject ans) {
                    try{
                        mView.hideLoading();
                        updatePrice(mUpdateorderSn, mTotalProductMoney, mDiscount + "");
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailureListener(String requestCode, com.alibaba.fastjson.JSONObject ans) {
                    mView.hideLoading();
                    MyToast.show(mView, "修改价格失败!");
                    getOutLogin02(mView, ans);
                }
            });
        }


    public void setUpdateMoney(){
        mUpdateorderSn = order_code;
        mAlertViewExt.show();
    }
}
