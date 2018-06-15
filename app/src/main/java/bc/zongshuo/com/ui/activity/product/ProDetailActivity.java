package bc.zongshuo.com.ui.activity.product;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.sevenonechat.sdk.compts.InfoSubmitActivity;
import com.sevenonechat.sdk.sdkinfo.SdkRunningClient;

import bc.zongshuo.com.R;
import bc.zongshuo.com.cons.Constance;
import bc.zongshuo.com.controller.product.ProductDetailController;
import bc.zongshuo.com.ui.activity.IssueApplication;
import bc.zongshuo.com.ui.activity.MainActivity;
import bc.zongshuo.com.utils.MyShare;
import bocang.json.JSONObject;
import bocang.utils.PermissionUtils;
import bocang.view.BaseActivity;

/**
 * @author: Jun
 * @date : 2017/2/13 17:50
 * @description :
 */
public class ProDetailActivity extends BaseActivity {
    public int mProductId=0;
    public int mOrderId=1;
    public String mOrderid="";
    public ProductDetailController mController;
    private LinearLayout product_ll, detail_ll, parament_ll, callLl, go_photo_Ll,sun_image_ll;
    private Button toDiyBtn, toCartBtn;
    private ImageView share_iv,home_iv;
    public static JSONObject goodses;
    public String mProperty = "";
    public String mPropertyValue = "";
    public double mPrice = 0;
    private RelativeLayout shopping_cart_Ll;
    public String mAttrId;

    public com.alibaba.fastjson.JSONObject mProductObject;


    @Override
    protected void InitDataView() {
        //        mController.sendProductDetail();
    }

    @Override
    protected void initController() {
        mController = new ProductDetailController(this);
    }


    @Override
    protected void initView() {
        setContentView(R.layout.activity_product_detail);
        product_ll = getViewAndClick(R.id.product_ll);
        detail_ll = getViewAndClick(R.id.detail_ll);
        parament_ll = getViewAndClick(R.id.parament_ll);
        sun_image_ll = getViewAndClick(R.id.sun_image_ll);
        callLl = getViewAndClick(R.id.callLl);
        shopping_cart_Ll = getViewAndClick(R.id.shopping_cart_Ll);
        toDiyBtn = getViewAndClick(R.id.toDiyBtn);
        toCartBtn = getViewAndClick(R.id.toCartBtn);
        share_iv = getViewAndClick(R.id.share_iv);
        home_iv = getViewAndClick(R.id.home_iv);
        go_photo_Ll = getViewAndClick(R.id.go_photo_Ll);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mController.getCartMun();
    }


    @Override
    protected void initData() {
        Intent intent = getIntent();
        mProductId = intent.getIntExtra(Constance.product, 0);
        mOrderid = intent.getStringExtra(Constance.order_id);

    }

    @Override
    protected void onViewClick(View v) {
        switch (v.getId()) {
            case R.id.product_ll:
                mController.selectProductType(R.id.product_ll);
                break;
            case R.id.detail_ll:
                mController.selectProductType(R.id.detail_ll);
                break;
            case R.id.parament_ll:
                mController.selectProductType(R.id.parament_ll);
                break;
            case R.id.sun_image_ll:
                mController.selectProductType(R.id.sun_image_ll);
                break;
            case R.id.callLl:
                if(isToken()){
                    return;
                }
                String userid=MyShare.get(this).getString(Constance.USERID);
//                String username=IssueApplication.mUserObject.getString(Constance.username);
//                if(TextUtils.isEmpty(username)){
//                    username=IssueApplication.mUserObject.getString(Constance.nickname);
//                }
//                Log.e("userid",userid);
                SdkRunningClient.getInstance().setUserUid(userid);
                Intent it = new Intent(v.getContext(), InfoSubmitActivity.class);
                v.getContext().startActivity(it);
                break;
            case R.id.shopping_cart_Ll:
                if (!isToken())
                    mController.getShoopingCart();
                break;
            case R.id.toDiyBtn:
//                if (!isToken())
                mController.GoDiyProduct();
                break;
            case R.id.toCartBtn:
//                if (!isToken())
                    mController.GoShoppingCart();
                break;
            case R.id.share_iv:
                mController.setShare();
//                IntentUtil.startActivity(this,ShareProductActivity.class,false);
                break;
            case R.id.go_photo_Ll:
                PermissionUtils.requestPermission(ProDetailActivity.this, PermissionUtils.CODE_CAMERA, new PermissionUtils.PermissionGrant() {
                    @Override
                    public void onPermissionGranted(int requestCode) {
                        mController.goPhoto();
                    }
                });
                break;
            case R.id.home_iv:
                Intent intent = new Intent(this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                this.startActivity(intent);
                break;
        }
    }


}
