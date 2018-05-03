package bc.zongshuo.com.controller.user;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alipay.sdk.app.PayTask;
import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.lib.common.hxp.view.ListViewForScrollView;
import com.sevenonechat.sdk.compts.InfoSubmitActivity;
import com.sevenonechat.sdk.sdkinfo.SdkRunningClient;

import java.text.DecimalFormat;

import bc.zongshuo.com.R;
import bc.zongshuo.com.bean.OrderInfo;
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
import bc.zongshuo.com.ui.activity.user.ConsignmentOrderActivity;
import bc.zongshuo.com.ui.activity.user.OrderDetailActivity;
import bc.zongshuo.com.ui.adapter.OrderGvAdapter;
import bc.zongshuo.com.ui.fragment.Order02Fragment;
import bc.zongshuo.com.ui.view.ShowDialog;
import bc.zongshuo.com.utils.DateUtils;
import bc.zongshuo.com.utils.UIUtils;
import bc.zongshuo.com.utils.WXpayUtils;
import bocang.json.JSONObject;
import bocang.utils.AppDialog;
import bocang.utils.AppUtils;
import bocang.utils.MyToast;

import static bc.zongshuo.com.cons.Constance.id;

/**
 * @author: Jun
 * @date : 2017/2/6 15:13
 * @description :
 */
public class Order02Controller extends BaseController implements INetworkCallBack, OnItemClickListener {
    private Order02Fragment mView;
    public JSONArray goodses;
    private ProAdapter mProAdapter;
    private ListViewForScrollView order_sv;
    private ProgressBar pd;
    private int mPosition;
    int mLevel = -1;
    int mOrderLevel = -1;
    private Boolean mIsUpdate = false;
    private int mProductPosition = 0;
    private JSONArray mProductArray;
    private String mUpdateorderSn = "";
    public double mTotalProductMoney = 0;
    private float mDiscount = 1;
    private float mProductDiscount = 0;//返回产品打折范围
    private AlertView mApliayView;
    private String mOrderId;

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
                        //                        sendOrderList(mView.mOrdersn);
                        Intent intent = new Intent(mView.getActivity(), OrderDetailActivity.class);
                        intent.putExtra(Constance.order, goodses.getJSONObject(mPosition).toJSONString());
                        intent.putExtra(Constance.state, 1);
                        mView.getActivity().startActivity(intent);
                    } else {
                        // 判断resultStatus 为非"9000"则代表可能支付失败
                        // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            AppDialog.messageBox("支付结果确认中");

                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            AppDialog.messageBox("支付失败");


                        }
                    }
                    break;
                }
                default:
                    break;
            }
        }
    };


    public Order02Controller(Order02Fragment v) {
        mView = v;
        initView();
        initViewData();
    }

    private void initViewData() {
        pd.setVisibility(View.VISIBLE);
        sendOrderList(mView.mOrdersn);
    }

    private void sendOrderList(String orderCode) {

        mNetWork.semdOrderSearch(orderCode, new INetworkCallBack02() {
            @Override
            public void onSuccessListener(String requestCode, com.alibaba.fastjson.JSONObject ans) {
                try{
                    mView.hideLoading();
                    pd.setVisibility(View.GONE);
                    switch (requestCode) {
                        case NetWorkConst.ORDERLIST:
                            if (null == mView.getActivity() || mView.getActivity().isFinishing())
                                return;
                            break;
                    }

                    mView.mSearchOrder = ans.getJSONObject(Constance.orders);
                    if (AppUtils.isEmpty(mView.mSearchOrder)) {
                        dismissRefesh();
                        return;
                    }
                    goodses = new JSONArray();
                    goodses.add(mView.mSearchOrder);
                    mProAdapter.notifyDataSetChanged();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailureListener(String requestCode, com.alibaba.fastjson.JSONObject ans) {
                mView.hideLoading();
                pd.setVisibility(View.GONE);
                MyToast.show(mView.getActivity(), "网络异常，请重新加载!");
                getOutLogin02(mView.getActivity(), ans);
            }
        });
    }

    private void initView() {
        //        mPullToRefreshLayout = ((PullToRefreshLayout) mView.getView().findViewById(R.id.refresh_view));
        //        mPullToRefreshLayout.setOnRefreshListener(this);
        order_sv = (ListViewForScrollView) mView.getView().findViewById(R.id.order_sv);
        order_sv.setDivider(null);//去除listview的下划线
        mProAdapter = new ProAdapter();
        order_sv.setAdapter(mProAdapter);
        pd = (ProgressBar) mView.getView().findViewById(R.id.pd);


        imm = (InputMethodManager) mView.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        //拓展窗口
        mAlertViewExt = new AlertView("提示", "请输入价格折扣(不能低于8折)！", "取消", null, new String[]{"完成"}, mView.getActivity(), AlertView.Style.Alert, this);
        ViewGroup extView = (ViewGroup) LayoutInflater.from(mView.getActivity()).inflate(R.layout.alertext_form, null);
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

        imm02 = (InputMethodManager) mView.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        //拓展窗口
        mAlertViewExt02 = new AlertView("提示", "请输入价格折扣！", "取消", null, new String[]{"完成"}, mView.getActivity(), AlertView.Style.Alert, this);
        ViewGroup extView02 = (ViewGroup) LayoutInflater.from(mView.getActivity()).inflate(R.layout.alertext_form, null);
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

        mApliayView = new AlertView(null, null, "取消", null,
                Constance.APLIAYTYPE,
                mView.getActivity(), AlertView.Style.ActionSheet, new OnItemClickListener() {
            @Override
            public void onItemClick(Object o, int position) {
                mView.setShowDialog(true);
                mView.setShowDialog("正在付款中!");
                mView.showLoading();
                if (mApliayView.toString().equals(o.toString())) {
                    if (position == 0) {
                        sendPayment(mOrderId, "alipay.app");
                    } else if (position == 1) {
                        sendPayment(mOrderId, "wxpay.app");
                    }
                }
            }
        });

    }

    @Override
    protected void handleMessage(int action, Object[] values) {

    }

    @Override
    protected void myHandleMessage(Message msg) {

    }


    @Override
    public void onSuccessListener(String requestCode, JSONObject ans) {
    }

    private void dismissRefesh() {
    }

    public void onRefresh() {
        sendOrderList(mView.mOrdersn);
    }


    @Override
    public void onFailureListener(String requestCode, JSONObject ans) {
        mView.hideLoading();
        if (null == mView.getActivity() || mView.getActivity().isFinishing())
            return;
        getOutLogin(mView.getActivity(), ans);
    }

    PrepayIdInfo bean;

    /**
     * 支付订单
     *
     * @param order
     * @param code
     */
    private void sendPayment(String order, final String code) {
        mNetWork.sendPayment(order, code, new INetworkCallBack02() {
            @Override
            public void onSuccessListener(String requestCode, com.alibaba.fastjson.JSONObject ans) {
                try{
                    mView.hideLoading();
                    if (code.equals("alipay.app")) {
                        String notify_url = ans.getString(Constance.alipay);
                        if (AppUtils.isEmpty(notify_url))
                            return;
                        SubmitAliPay(notify_url);
                    } else {
                        com.alibaba.fastjson.JSONObject wxpayObject = ans.getJSONObject(Constance.wxpay);
                        String appid = wxpayObject.getString("appid");
                        String mch_id = wxpayObject.getString("mch_id");
                        String nonce_str = wxpayObject.getString("nonce_str");
                        String prepay_id = wxpayObject.getString("prepay_id");
                        String sign = wxpayObject.getString("sign");
                        String timestamp = wxpayObject.getString("timestamp");
                        bean = new PrepayIdInfo();
                        bean.setAppid(appid);
                        bean.setMch_id(mch_id);
                        bean.setNonce_str(nonce_str);
                        bean.setPrepay_id(prepay_id);
                        bean.setSign(sign);
                        bean.setTimestamp(timestamp);
                        WXpayUtils.mContext = mView.getContext();
                        WXpayUtils.Pay(bean, bean.getPrepay_id());
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailureListener(String requestCode, com.alibaba.fastjson.JSONObject ans) {
                mView.hideLoading();
                if (null == mView)
                    return;
                if (AppUtils.isEmpty(ans)) {
                    AppDialog.messageBox(UIUtils.getString(R.string.server_error));
                    return;
                }
                AppDialog.messageBox(ans.getString(Constance.error_desc));
                getOutLogin02(mView.getActivity(), ans);
            }
        });

    }

    //标记是支付
    private static final int SDK_PAY_FLAG = 1;
    private static final String TAG = "PayActivity";

    /**
     * 支付宝支付
     */
    private void SubmitAliPay(String notifyUrl) {
        //开始支付
        aliPay(notifyUrl);
    }

    /**
     * create the order info. 创建订单信息
     */
    private String createOrderInfo(OrderInfo order) {

        // 签约合作者身份ID
        String orderInfo = "partner=" + "\"" + order.getPartner() + "\"";

        // 签约卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + order.getSeller_id() + "\"";

        // 商户网站唯一订单号
        orderInfo += "&out_trade_no=" + "\"" + order.getOut_trade_no() + "\"";

        // 商品名称
        orderInfo += "&subject=" + "\"" + order.getSubject() + "\"";

        // 商品详情
        orderInfo += "&body=" + "\"" + order.getBody() + "\"";

        // 商品金额
        orderInfo += "&total_fee=" + "\"" + order.getTotal_fee() + "\"";

        // 服务器异步通知页面路径
        orderInfo += "&notify_url=" + "\"" + order.getNotify_url() + "\"";

        // 服务接口名称， 固定值
        orderInfo += "&service=\"mobile.securitypay.pay\"";

        // 支付类型， 固定值
        orderInfo += "&payment_type=\"1\"";

        // 参数编码， 固定值
        orderInfo += "&_input_charset=\"utf-8\"";

        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        orderInfo += "&it_b_pay=\"30m\"";

        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
        orderInfo += "&return_url=\"m.alipay.com\"";

        // 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
        // orderInfo += "&paymethod=\"expressGateway\"";

        return orderInfo;
    }

    /**
     * 开始-支付宝支付
     */
    private void aliPay(final String ss) {
        //        try {
        //            /**
        //             * 仅需对sign 做URL编码
        //             */
        //            sign = URLEncoder.encode(sign, "UTF-8");
        //        } catch (UnsupportedEncodingException e) {
        //            e.printStackTrace();
        //        }
        //
        //        /**
        //         * 完整的符合支付宝参数规范的订单信息
        //         */
        //        final String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + getSignType();

        Runnable payRunnable = new Runnable() {
            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(mView.getActivity());
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

    /**
     * get the sign type we use. 获取签名方式
     */
    private String getSignType() {
        return "sign_type=\"RSA\"";
    }

    String mBody = "";


    private class ProAdapter extends BaseAdapter implements INetworkCallBack {
        public ProAdapter() {
        }

        @Override
        public int getCount() {
            if (null == goodses)
                return 0;
            return goodses.size();
        }

        @Override
        public com.alibaba.fastjson.JSONObject getItem(int position) {
            if (null == goodses)
                return null;
            return goodses.getJSONObject(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(mView.getActivity(), R.layout.item_order_one, null);

                holder = new ViewHolder();
                holder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
                holder.state_tv = (TextView) convertView.findViewById(R.id.state_tv);
                holder.time_tv = (TextView) convertView.findViewById(R.id.time_tv);
                holder.do_tv = (TextView) convertView.findViewById(R.id.do_tv);
                holder.do02_tv = (TextView) convertView.findViewById(R.id.do02_tv);
                holder.do03_tv = (TextView) convertView.findViewById(R.id.do03_tv);
                holder.consigment_tv = (TextView) convertView.findViewById(R.id.consigment_tv);
                holder.chat_buy_tv = (TextView) convertView.findViewById(R.id.chat_buy_tv);
                holder.code_tv = (TextView) convertView.findViewById(R.id.code_tv);
                holder.total_tv = (TextView) convertView.findViewById(R.id.total_tv);
                holder.remark_tv = (TextView) convertView.findViewById(R.id.remark_tv);
                holder.lv = (ListViewForScrollView) convertView.findViewById(R.id.lv);
                holder.order_lv = (LinearLayout) convertView.findViewById(R.id.order_lv);
                holder.update_money_tv = (TextView) convertView.findViewById(R.id.update_money_tv);
                holder.old_money = (TextView) convertView.findViewById(R.id.old_money);
                holder.new_money = (TextView) convertView.findViewById(R.id.new_money);
                holder.pdf_tv = (TextView) convertView.findViewById(R.id.pdf_tv);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final com.alibaba.fastjson.JSONObject orderobject = goodses.getJSONObject(position);
            final int state = orderobject.getInteger(Constance.status);
            int tatalNum = 0;
            final String total = orderobject.getString(Constance.total);
            final String orderId = orderobject.getString(id);

            getState(state, holder.state_tv, holder.do_tv, holder.do02_tv, holder.do03_tv);
            final String sn = orderobject.getString(Constance.sn);
            final String id = orderobject.getString(Constance.id);
            holder.code_tv.setText("订单号:" + orderobject.getString(Constance.sn));
            final JSONArray array = orderobject.getJSONArray(Constance.goods);
            holder.time_tv.setText(DateUtils.getStrTime(orderobject.getString(Constance.created_at)));
            final String score = orderobject.getJSONObject("score").getString("score");
            final String discount = orderobject.getString(Constance.discount);
            String str1 = "市场价:￥" + score;
            int fstart1 = str1.indexOf(score);
            int fend1 = fstart1 + score.length();
            SpannableStringBuilder style1 = new SpannableStringBuilder(str1);
            style1.setSpan(new ForegroundColorSpan(Color.RED), fstart1, fend1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            holder.old_money.setText(style1);
            Double avg = ((Double.parseDouble(total) / Double.parseDouble(score)) * 100);
            if (avg == 100) {
                String str = "优惠价:￥" + total;
                int fstart = str.indexOf(total);
                int fend = fstart + total.length();
                SpannableStringBuilder style = new SpannableStringBuilder(str);
                style.setSpan(new ForegroundColorSpan(Color.RED), fstart, fend, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                holder.new_money.setText(style);
            } else {
                DecimalFormat decimalFormat = new DecimalFormat(".0");//构造方法的字符格式这里如果小数不足2位,会以0补足.
                String p = decimalFormat.format(avg * 0.1);//format 返回的是字符串
                String val = total;
                String str = "优惠价:￥" + val;
                int fstart = str.indexOf(val);
                int fend = fstart + val.length();
                SpannableStringBuilder style = new SpannableStringBuilder(str);
                style.setSpan(new ForegroundColorSpan(Color.RED), fstart, fend, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                holder.new_money.setText(style);
            }

            for (int i = 0; i < array.size(); i++) {
                tatalNum += array.getJSONObject(i).getInteger(Constance.total_amount);
            }

            String str = "共计 " + tatalNum + " 件商品 合计￥" + total + "";
            int fstart = str.indexOf(total);
            int fend = fstart + total.length();
            SpannableStringBuilder style = new SpannableStringBuilder(str);
            style.setSpan(new ForegroundColorSpan(Color.RED), fstart, fend, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            holder.total_tv.setText(style);

            OrderGvAdapter maGvAdapter = new OrderGvAdapter(mView.getActivity(), array, mOrderLevel, state);
            holder.lv.setAdapter(maGvAdapter);
            maGvAdapter.setUpdateProductPriceListener(new IUpdateProductPriceListener() {
                @Override
                public void onUpdateProductPriceListener(int position, com.alibaba.fastjson.JSONObject object) {
                    String itemDiscount = array.getJSONObject(position).getString(Constance.discount);
                    if (AppUtils.isEmpty(itemDiscount) || "0".equals(itemDiscount)) {
                        mProductDiscount = (float) 0.8;
                    } else {
                        mProductDiscount = Float.parseFloat(itemDiscount);
                    }
                    mProductArray = array;
                    mUpdateorderId = id;
                    mUpdateorderSn = sn;
                    mDiscount = Float.parseFloat(discount);
                    mProductPosition = position;
                    mAlertViewExt02.show();
                }
            });
            mLevel = IssueApplication.mUserObject.getInt(Constance.level_id);

            String user_name = orderobject.getString(Constance.user_name);
            mOrderLevel = orderobject.getInteger(Constance.level);
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

            if (mLevel == 100) {
                levelValue = levelValue + "(" + user_name + ")";
                holder.remark_tv.setText(levelValue);
                if (mLevel != mOrderLevel && state == 0) {
                    holder.remark_tv.setVisibility(View.VISIBLE);
                    mIsUpdate = true;
                    holder.update_money_tv.setVisibility(View.GONE);
                    if (state == 0) {
                        holder.do_tv.setVisibility(View.GONE);
                        holder.do02_tv.setVisibility(View.GONE);
                        holder.do03_tv.setVisibility(View.GONE);
                        holder.chat_buy_tv.setVisibility(View.VISIBLE);
                    }

                } else {
                    mIsUpdate = false;
                    holder.update_money_tv.setVisibility(View.GONE);
                }
                if (state == 1) {
                    holder.consigment_tv.setVisibility(View.VISIBLE);
                } else {
                    holder.consigment_tv.setVisibility(View.GONE);
                }

            } else {
                holder.remark_tv.setVisibility(View.GONE);
                mIsUpdate = false;
                holder.update_money_tv.setVisibility(View.GONE);
                holder.consigment_tv.setVisibility(View.GONE);
            }

            //修改价格
            holder.update_money_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mProductArray = array;
                    mUpdateOrderMoney = Float.parseFloat(score);
                    mUpdateorderSn = sn;
                    mDiscount = Float.parseFloat(discount);
                    mAlertViewExt.show();
                }
            });


            holder.do03_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String id = IssueApplication.mUserObject.getString(Constance.id);
                    SdkRunningClient.getInstance().setUserUid(id);
                    Intent it = new Intent(v.getContext(), InfoSubmitActivity.class);
                    v.getContext().startActivity(it);
                }
            });
            holder.do_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO
                    if (state == 1) {
                        String id = IssueApplication.mUserObject.getString(Constance.id);
                        SdkRunningClient.getInstance().setUserUid(id);
                        Intent it = new Intent(v.getContext(), InfoSubmitActivity.class);
                        v.getContext().startActivity(it);
                    } else if (state == 0) {

                        for (int i = 0; i < array.size(); i++) {
                            mBody += array.getJSONObject(i).getJSONObject(Constance.product).getString(Constance.name) + "  ";
                        }
                        mPosition = position;
                        mView.setShowDialog(true);
                        mView.setShowDialog("正在付款中!");
                        mView.showLoading();
                        sendPayment(orderId, "alipay.app");
                    } else if (state == 2) {
                        sendConfirmReceipt();
                    } else if (state == 5) {
                        String id = IssueApplication.mUserObject.getString(Constance.id);
                        SdkRunningClient.getInstance().setUserUid(id);
                        Intent it = new Intent(v.getContext(), InfoSubmitActivity.class);
                        v.getContext().startActivity(it);
                    } else if (state == 4 || state == 3) {
                        String id = IssueApplication.mUserObject.getString(Constance.id);
                        SdkRunningClient.getInstance().setUserUid(id);
                        Intent it = new Intent(v.getContext(), InfoSubmitActivity.class);
                        v.getContext().startActivity(it);
                    }
                }
            });
            holder.do02_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (state == 0) {
                        mView.setShowDialog(true);
                        mView.setShowDialog("正在取消中!");
                        mView.showLoading();
                        sendOrderCancel(orderId, "1");
                    }

                }
            });

            /**
             * 联系买家
             */
            holder.chat_buy_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String id = IssueApplication.mUserObject.getString(Constance.id);
                    SdkRunningClient.getInstance().setUserUid(id);
                    Intent it = new Intent(v.getContext(), InfoSubmitActivity.class);
                    v.getContext().startActivity(it);
                }
            });

            holder.order_lv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mView.getActivity(), OrderDetailActivity.class);
                    intent.putExtra(Constance.order, orderobject.toJSONString());
                    mView.getActivity().startActivity(intent);
                }
            });

            holder.lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(mView.getActivity(), OrderDetailActivity.class);
                    intent.putExtra(Constance.order, orderobject.toJSONString());
                    mView.getActivity().startActivity(intent);
                }
            });

            holder.pdf_tv.setOnClickListener(new View.OnClickListener() {//生成PDF
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mView.getActivity(), ExInventoryActivity.class);
                    intent.putExtra(Constance.goods, orderobject.toJSONString());
                    intent.putExtra(Constance.pdfType, 1);
                    mView.getActivity().startActivity(intent);
                }
            });


            //确认发货
            holder.consigment_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mView.getActivity(), ConsignmentOrderActivity.class);
                    intent.putExtra(Constance.order, orderobject.toJSONString());
                    intent.putExtra(Constance.type, true);
                    mView.getActivity().startActivity(intent);
                }
            });

            return convertView;
        }


        /**
         * 确认收货
         */
        private void sendConfirmReceipt() {
            ShowDialog mDialog = new ShowDialog();
            mDialog.show(mView.getActivity(), "提示", "是否确认收货?", new ShowDialog.OnBottomClickListener() {
                @Override
                public void positive() {
                    mView.setShowDialog(true);
                    mView.setShowDialog("确认收货中!");
                    mView.showLoading();
                    mNetWork.sendConfirmReceipt(mView.mOrdersn, new INetworkCallBack02() {
                        @Override
                        public void onSuccessListener(String requestCode, com.alibaba.fastjson.JSONObject ans) {
                            try {
                                mView.hideLoading();
                                MyToast.show(mView.getActivity(), "确认收货成功!");
                                sendOrderList(mView.mOrdersn);
                                Intent intent = new Intent(mView.getActivity(), OrderDetailActivity.class);
                                intent.putExtra(Constance.order, goodses.getJSONObject(mPosition).toJSONString());
                                intent.putExtra(Constance.state, 3);
                                mView.getActivity().startActivity(intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onFailureListener(String requestCode, com.alibaba.fastjson.JSONObject ans) {
                            mView.hideLoading();
                            MyToast.show(mView.getActivity(), "确认收货失败!");
                            getOutLogin02(mView.getActivity(), ans);
                        }
                    });
                }

                @Override
                public void negtive() {

                }
            });

        }


        private void sendOrderCancel(String order, String reason) {
            mNetWork.sendOrderCancel(order, reason, this);
        }

        /**
         * 订单状态
         *
         * @param type
         * @param state_tv
         * @param do_tv
         * @param do02_tv
         */
        private void getState(int type, TextView state_tv, TextView do_tv, TextView do02_tv, TextView do03_tv) {
            do_tv.setVisibility(View.GONE);
            do02_tv.setVisibility(View.GONE);
            do03_tv.setVisibility(View.GONE);
            String stateValue = "";
            switch (type) {
                case 0:
                    do03_tv.setVisibility(View.VISIBLE);
                    stateValue = "【待付款】";
                    do_tv.setVisibility(View.VISIBLE);
                    do02_tv.setVisibility(View.VISIBLE);
                    do_tv.setText("付款");
                    do02_tv.setText("取消订单");

                    break;
                case 1:
                    //                    do03_tv.setVisibility(View.VISIBLE);
                    stateValue = "【待发货】";
                    do_tv.setVisibility(View.VISIBLE);
                    do_tv.setText("联系商家");
                    break;
                case 2:
                    do03_tv.setVisibility(View.VISIBLE);
                    stateValue = "【待收货】";
                    do_tv.setText("确认收货");
                    if (mLevel == mOrderLevel) {
                        do_tv.setVisibility(View.VISIBLE);
                    }
                    break;
                case 4:
                    do_tv.setVisibility(View.VISIBLE);
                    do_tv.setText("联系商家");
                    stateValue = "【已完成】";
                    break;
                case 3:
                    do_tv.setVisibility(View.VISIBLE);
                    do_tv.setText("联系商家");
                    stateValue = "【已完成】";
                    break;
                case 5:
                    do_tv.setVisibility(View.VISIBLE);
                    do_tv.setText("联系商家");
                    stateValue = "【已取消】";
                    break;
            }
            state_tv.setText(stateValue);
        }

        @Override
        public void onSuccessListener(String requestCode, JSONObject ans) {
            switch (requestCode) {
                case NetWorkConst.ORDERCANCEL:
                    if (ans.getInt(Constance.error_code) == 0) {
                        sendOrderList(mView.mOrdersn);
                    } else {
                        mView.hideLoading();
                        MyToast.show(mView.getActivity(), "订单取消失败!");
                    }

                    break;
                case NetWorkConst.PAYMENT:
                    break;


            }
        }

        @Override
        public void onFailureListener(String requestCode, JSONObject ans) {
            MyToast.show(mView.getActivity(), "2");
            mView.hideLoading();
            MyToast.show(mView.getActivity(), "支付成功!");
            getOutLogin(mView.getActivity(), ans);
        }

        class ViewHolder {
            ImageView imageView;
            TextView state_tv;
            TextView time_tv;
            TextView do_tv;
            TextView do02_tv;
            TextView do03_tv;
            TextView consigment_tv;
            TextView code_tv;
            TextView pdf_tv;
            TextView chat_buy_tv;
            TextView total_tv;
            ListViewForScrollView lv;
            LinearLayout order_lv;
            TextView remark_tv;
            TextView update_money_tv;
            TextView old_money;
            TextView new_money;

        }
    }

    private AlertView mAlertViewExt;//窗口拓展例子
    private EditText etName;//拓展View内容
    private InputMethodManager imm;
    private AlertView mAlertViewExt02;//窗口拓展例子
    private EditText etName02;//拓展View内容
    private InputMethodManager imm02;

    private void closeKeyboard() {
        //关闭软键盘
        imm.hideSoftInputFromWindow(etName.getWindowToken(), 0);
        //恢复位置
        mAlertViewExt.setMarginBottom(0);
    }

    private void closeKeyboard02() {
        //关闭软键盘
        imm02.hideSoftInputFromWindow(etName02.getWindowToken(), 0);
        //恢复位置
        mAlertViewExt02.setMarginBottom(0);
    }

    private float mUpdateOrderMoney = 0;
    private String mUpdateorderId = "";

    @Override
    public void onItemClick(Object o, int position) {
        closeKeyboard();
        closeKeyboard02();
        //判断是否是拓展窗口View，而且点击的是非取消按钮
        if (o == mAlertViewExt && position != AlertView.CANCELPOSITION) {
            String name = etName.getText().toString();
            if (name.isEmpty()) {
                //                        MyToast.show(mView.getActivity(),"请输入需要查询的订单号!");
            } else {
                Float ratio = Float.parseFloat(name);
                if (ratio < 8) {
                    MyToast.show(mView.getActivity(), "输入的价格折扣不能小于8折");
                    return;
                } else if (ratio > 10) {
                    MyToast.show(mView.getActivity(), "输入的价格折扣不能大于10折");
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
        } else if (o == mAlertViewExt02 && position != AlertView.CANCELPOSITION) {
            String name = etName02.getText().toString();
            if (name.isEmpty()) {
                //                        MyToast.show(mView.getActivity(),"请输入需要查询的订单号!");
            } else {
                Float ratio = Float.parseFloat(name);
                if (ratio < mProductDiscount * 10) {
                    int num = (int) (mProductDiscount * 10);
                    MyToast.show(mView.getActivity(), "输入的价格折扣不能小于" + num + "折");
                    return;
                } else if (ratio > 10) {
                    MyToast.show(mView.getActivity(), "输入的价格折扣不能大于10折");
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
                MyToast.show(mView.getActivity(), "修改价格失败!");
                getOutLogin02(mView.getActivity(), ans);
            }
        });
    }


    /**
     * 修改订单价格
     *
     * @param orderId 订单号
     * @param money   价格
     */
    private void updatePrice(String orderId, double money, String discount) {
        mNetWork.updatePrice(orderId, money, discount, new INetworkCallBack02() {
            @Override
            public void onSuccessListener(String requestCode, com.alibaba.fastjson.JSONObject ans) {
                mView.hideLoading();
                MyToast.show(mView.getActivity(), "修改成功!");
                initViewData();
            }

            @Override
            public void onFailureListener(String requestCode, com.alibaba.fastjson.JSONObject ans) {
                mView.hideLoading();
                MyToast.show(mView.getActivity(), "2222");
                getOutLogin02(mView.getActivity(), ans);
            }
        });
    }
//
//    /**
//     * 联系客服
//     */
//    public void sendCall(String msg, final String parent_id, final String parent_name, final String userIcon) {
//        try {
//            //            if (AppUtils.isEmpty(IssueApplication.mUserObject.getString("parent_name"))) {
//            //                MyToast.show(mView.getActivity(), "不能和自己聊天!");
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
//                mDialog.show(mView.getActivity(), "提示", msg, new ShowDialog.OnBottomClickListener() {
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
//                mView.startActivity(new Intent(mView.getActivity(), ChatActivity.class).putExtra(EaseConstant.EXTRA_USER_ID, parent_id));
//            }
//        } catch (HyphenateException e) {
//            e.printStackTrace();
//        }
//    }
//
//    //登录环信
//    private void loginHX(final String parent_id, final String parent_name, final String userIcon) {
//        final Toast toast = Toast.makeText(mView.getActivity(), "服务器连接中...!", Toast.LENGTH_SHORT);
//        toast.show();
//        if (NetUtils.hasNetwork(mView.getActivity())) {
//            final String uid = MyShare.get(mView.getActivity()).getString(Constance.USERID);
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
//                    mView.startActivity(new Intent(mView.getActivity(), ChatActivity.class).putExtra(EaseConstant.EXTRA_USER_ID, parent_id));
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
//                                mView.getActivity().runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        MyLog.e("注册成功!");
//                                        getSuccessLogin(uid, toast, parent_id, parent_name, userIcon);
//                                    }
//                                });
//
//                            } catch (final HyphenateException e) {
//                                mView.getActivity().runOnUiThread(new Runnable() {
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
//                    Toast.makeText(mView.getActivity(), "连接失败,请重试!", Toast.LENGTH_SHORT).show();
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

}
