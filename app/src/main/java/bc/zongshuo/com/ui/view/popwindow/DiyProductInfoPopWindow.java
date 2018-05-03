package bc.zongshuo.com.ui.view.popwindow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import bc.zongshuo.com.R;
import bc.zongshuo.com.cons.Constance;
import bc.zongshuo.com.cons.NetWorkConst;
import bc.zongshuo.com.listener.IDiyProductInfoListener;
import bc.zongshuo.com.listener.INetworkCallBack;
import bc.zongshuo.com.ui.activity.IssueApplication;
import bc.zongshuo.com.ui.activity.user.UserLogActivity;
import bc.zongshuo.com.ui.adapter.ParamentAdapter02;
import bc.zongshuo.com.utils.MyShare;
import bc.zongshuo.com.utils.UIUtils;
import bocang.json.JSONArray;
import bocang.json.JSONObject;
import bocang.utils.AppUtils;


/**
 * @author: Jun
 * @date : 2017/2/16 15:12
 * @description :
 */
public class DiyProductInfoPopWindow extends BasePopwindown implements View.OnClickListener, INetworkCallBack {
    private Activity mActivity;
    private IDiyProductInfoListener mListener;
    private ImageView product_iv, close_iv;
    private RelativeLayout two_bar_codes_rl, parameter_rl, logo_rl, card_rl;
    public JSONObject productObject;
    public String productId;
    private ListView attr_lv;
    private ParamentAdapter02 mParamentAdapter;
    private StringBuffer mParamMsg;

    public void setListener(IDiyProductInfoListener listener) {
        mListener = listener;
    }

    public DiyProductInfoPopWindow(Context context, Activity activity) {
        super(context);
        mActivity = activity;
    }

    @Override
    protected void initView(Context context) {
        View contentView = View.inflate(mContext, R.layout.pop_diy_product_info, null);
        initUI(contentView);
    }

    public void initViewData() {
//        String path = productObject.getJSONObject(Constance.app_img).getString(Constance.img);
        String path = productObject.getString(Constance.curl);
        if(TextUtils.isEmpty(path)){
            path= productObject.getJSONObject(Constance.app_img).getString(Constance.img);
        }
        if (!AppUtils.isEmpty(IssueApplication.mSelectProParamemt)) {
            String paramentId = IssueApplication.mSelectProParamemt.get(productId);
//            Log.e("520it", "paramentId:"+paramentId);
            JSONArray propertieArray = productObject.getJSONArray(Constance.properties);
            if (!AppUtils.isEmpty(propertieArray)) {

                JSONArray attrsArray = propertieArray.getJSONObject(0).getJSONArray(Constance.attrs);
                for (int i = 0; i < attrsArray.length(); i++) {
                    String attrId = attrsArray.getJSONObject(i).getString(Constance.id);
                    if (paramentId.equals(attrId)) {
//                        Log.e("520it", "paramentId:path:"+path);
//                        path = NetWorkConst.SCENE_HOST + attrsArray.getJSONObject(i).getString(Constance.img);
                        break;
                    }
                }
            }
        }
        ImageLoader.getInstance().displayImage(path, product_iv);
        getDetail();
    }


    private void initUI(View contentView) {

        product_iv = (ImageView) contentView.findViewById(R.id.product_iv);
        two_bar_codes_rl = (RelativeLayout) contentView.findViewById(R.id.two_bar_codes_rl);
        parameter_rl = (RelativeLayout) contentView.findViewById(R.id.parameter_rl);
        logo_rl = (RelativeLayout) contentView.findViewById(R.id.logo_rl);
        card_rl = (RelativeLayout) contentView.findViewById(R.id.card_rl);
        close_iv = (ImageView) contentView.findViewById(R.id.close_iv);
        attr_lv = (ListView) contentView.findViewById(R.id.attr_lv);
        product_iv.setOnClickListener(this);
        two_bar_codes_rl.setOnClickListener(this);
        parameter_rl.setOnClickListener(this);
        logo_rl.setOnClickListener(this);
        card_rl.setOnClickListener(this);
        close_iv.setOnClickListener(this);

        mPopupWindow = new PopupWindow(contentView, -1, -1);
        // 1.让mPopupWindow内部的控件获取焦点
        mPopupWindow.setFocusable(true);
        // 2.mPopupWindow内部获取焦点后 外部的所有控件就失去了焦点
        mPopupWindow.setOutsideTouchable(true);
        //只有加载背景图还有效果
        // 3.如果不马上显示PopupWindow 一般建议刷新界面
        mPopupWindow.update();
        // 设置弹出窗体显示时的动画，从底部向上弹出
        mPopupWindow.setAnimationStyle(R.style.AnimBottom);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.close_iv:
                onDismiss();
                break;
            case R.id.bg_ll:
                onDismiss();
                break;
            case R.id.btn_logistic:
                Intent intent = new Intent(mContext, UserLogActivity.class);
                intent.putExtra(Constance.isSelectLogistice, true);
                mActivity.startActivityForResult(intent, Constance.FROMLOG);
                onDismiss();
                break;
            case R.id.two_bar_codes_rl://二维码
                mListener.onDiyProductInfo(0, null);
                onDismiss();
                break;
            case R.id.parameter_rl://参数
                mListener.onDiyProductInfo(1, mParamMsg.toString());
                onDismiss();
                break;
            case R.id.logo_rl://LOGO
                mListener.onDiyProductInfo(2, null);
                onDismiss();
                break;
            case R.id.card_rl://产品卡
                mListener.onDiyProductInfo(3, null);
                onDismiss();
                break;
        }
    }

    /**
     * 产品详情
     */
    public void getDetail() {
        mParamMsg = new StringBuffer();
        JSONArray attachmentArray = productObject.getJSONArray(Constance.attachments);
        String propertyId=productObject.getString(Constance.cproperty_id);
        List<String> attachs = new ArrayList<>();
        String attr_name = "";
        String price = productObject.getString(Constance.current_price);
        int currentAttrPostion=0;
        if (!AppUtils.isEmpty(IssueApplication.mSelectProParamemt)) {
            String paramentId = IssueApplication.mSelectProParamemt.get(productId);
            JSONArray propertieArray = productObject.getJSONArray(Constance.properties);
            if (!AppUtils.isEmpty(propertieArray)) {
                for(int j=0;j<propertieArray.length();j++){
                   String name= propertieArray.getJSONObject(j).getString(Constance.name);
                    if("规格".equals(name)){
                        JSONArray attrsArray = propertieArray.getJSONObject(j).getJSONArray(Constance.attrs);
                        for (int i = 0; i < attrsArray.length(); i++) {
                            String attrId = attrsArray.getJSONObject(i).getString(Constance.id);
                            if (propertyId.equals(attrId)) {
                                currentAttrPostion=i;
                                int attrprice = 0;
//                                int parantLevel = MyShare.get(mContext).getInt(Constance.parant_level);
                                String token = MyShare.get(mContext).getString(Constance.TOKEN);
                                if (AppUtils.isEmpty(token)) {
                                    attrprice = attrsArray.getJSONObject(i).getInt(("attr_price_5" ));
                                } else {
                                    int levelId = IssueApplication.mUserObject.getInt(Constance.level_id);
                                    attrprice = attrsArray.getJSONObject(i).getInt(("attr_price_" + (levelId + 1)).replace("10",""));
                                }
                                price = (attrprice) + "";
                                attr_name=attrsArray.getJSONObject(i).getString(Constance.attr_name);
                                break;
                            }
                        }
                    }
                }

            }
        }
        attachs.add("价格:  ￥" + price );
        attachs.add("名称: " + productObject.getString(Constance.name));
        mParamMsg.append("名称: " + productObject.getString(Constance.name) + "\n");
        mParamMsg.append("价格: ￥" + price + "\n");
        if(!AppUtils.isEmpty(attr_name)){
            attachs.add("规格: " + attr_name);
            mParamMsg.append("规格: " + attr_name + "\n");
        }

        for (int i = 0; i < attachmentArray.length(); i++) {
            JSONObject jsonObject = attachmentArray.getJSONObject(i);
            JSONArray attrs = jsonObject.getJSONArray(Constance.attrs);
            attachs.add(jsonObject.getString(Constance.name) + ": " + attrs.getJSONObject(0).getString(Constance.attr_name));
            if (i < attachmentArray.length() - 1) {
                mParamMsg.append(jsonObject.getString(Constance.name) + ": " + attrs.getJSONObject(0).getString(Constance.attr_name) + "\n");
            } else {
                mParamMsg.append(jsonObject.getString(Constance.name) + ": " + attrs.getJSONObject(0).getString(Constance.attr_name));
            }

        }


        mParamentAdapter = new ParamentAdapter02(attachs, mContext, 1);
        attr_lv.setAdapter(mParamentAdapter);
        attr_lv.setDivider(null);//去除listview的下划线
    }


    @Override
    public void onSuccessListener(String requestCode, JSONObject ans) {
    }

    @Override
    public void onFailureListener(String requestCode, JSONObject ans) {
    }

}
