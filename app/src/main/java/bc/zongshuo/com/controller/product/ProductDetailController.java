package bc.zongshuo.com.controller.product;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;

import bc.zongshuo.com.R;
import bc.zongshuo.com.common.BaseFragment;
import bc.zongshuo.com.cons.Constance;
import bc.zongshuo.com.cons.NetWorkConst;
import bc.zongshuo.com.controller.BaseController;
import bc.zongshuo.com.listener.INetworkCallBack;
import bc.zongshuo.com.listener.IParamentChooseListener;
import bc.zongshuo.com.ui.activity.IssueApplication;
import bc.zongshuo.com.ui.activity.buy.ShoppingCartActivity;
import bc.zongshuo.com.ui.activity.product.ProDetailActivity;
import bc.zongshuo.com.ui.activity.product.ShareProductActivity;
import bc.zongshuo.com.ui.activity.programme.DiyActivity;
import bc.zongshuo.com.ui.activity.programme.camera.TestCameraActivity;
import bc.zongshuo.com.ui.fragment.DetailGoodsFragmemt;
import bc.zongshuo.com.ui.fragment.IntroduceGoodsFragment;
import bc.zongshuo.com.ui.fragment.ParameterFragment;
import bc.zongshuo.com.ui.fragment.SunImageFragment;
import bc.zongshuo.com.ui.view.popwindow.SelectParamentPopWindow;
import bc.zongshuo.com.utils.CartAnimator;
import bc.zongshuo.com.utils.UIUtils;
import bocang.json.JSONObject;
import bocang.utils.AppDialog;
import bocang.utils.AppUtils;
import bocang.utils.IntentUtil;
import bocang.utils.MyToast;

/**
 * @author: Jun
 * @date : 2017/2/13 17:58
 * @description :
 */
public class ProductDetailController extends BaseController implements INetworkCallBack, ViewPager.OnPageChangeListener {
    private ProDetailActivity mView;
    private View product_view, detail_view, parament_view, sun_image_view;
    private TextView product_tv, detail_tv, parament_tv, sun_image_tv;
    private ProductContainerAdapter mContainerAdapter;
    private ViewPager container_vp;
    private Intent mIntent;
    private LinearLayout title_ll, product_ll, detail_ll, parament_ll, main_ll, sun_image_ll;
    private ImageView collectIv;
    private RelativeLayout main_rl;
    private TextView unMessageReadTv;
    private IntroduceGoodsFragment mIntroduceGoodsFragment;


    public ProductDetailController(ProDetailActivity v) {
        mView = v;
        initView();
        initViewData();
    }


    public void initViewData() {
        sendProductDetail();
        sendCustom();
    }

    private void initView() {
        product_view = mView.findViewById(R.id.product_view);
        detail_view = mView.findViewById(R.id.detail_view);
        parament_view = mView.findViewById(R.id.parament_view);
        sun_image_view = mView.findViewById(R.id.sun_image_view);
        product_tv = (TextView) mView.findViewById(R.id.product_tv);
        detail_tv = (TextView) mView.findViewById(R.id.detail_tv);
        parament_tv = (TextView) mView.findViewById(R.id.parament_tv);
        sun_image_tv = (TextView) mView.findViewById(R.id.sun_image_tv);
        unMessageReadTv = (TextView) mView.findViewById(R.id.unMessageReadTv);
        container_vp = (ViewPager) mView.findViewById(R.id.container_vp);
        mContainerAdapter = new ProductContainerAdapter(mView.getSupportFragmentManager());
        container_vp.setAdapter(mContainerAdapter);
        container_vp.setOnPageChangeListener(this);
        container_vp.setCurrentItem(0);
        title_ll = (LinearLayout) mView.findViewById(R.id.title_ll);
        product_ll = (LinearLayout) mView.findViewById(R.id.product_ll);
        main_ll = (LinearLayout) mView.findViewById(R.id.main_ll);
        detail_ll = (LinearLayout) mView.findViewById(R.id.detail_ll);
        parament_ll = (LinearLayout) mView.findViewById(R.id.parament_ll);
        sun_image_ll = (LinearLayout) mView.findViewById(R.id.sun_image_ll);
        main_rl = (RelativeLayout) mView.findViewById(R.id.main_rl);
        collectIv = (ImageView) mView.findViewById(R.id.collectIv);

        container_vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 1) {
                    mFragments.get(position).onStart();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }

    /**
     * 购物车数量显示
     */
    public void getCartMun() {
        if (IssueApplication.mCartCount == 0) {
            unMessageReadTv.setVisibility(View.GONE);
        } else {
            unMessageReadTv.setVisibility(View.VISIBLE);
            unMessageReadTv.setText(IssueApplication.mCartCount + "");
        }
    }

    /**
     * 产品详情不同选择
     *
     * @param type
     */
    public void selectProductType(int type) {
        product_view.setVisibility(View.INVISIBLE);
        detail_view.setVisibility(View.INVISIBLE);
        parament_view.setVisibility(View.INVISIBLE);
        sun_image_view.setVisibility(View.INVISIBLE);
        product_tv.setTextColor(mView.getResources().getColor(R.color.txt_black));
        detail_tv.setTextColor(mView.getResources().getColor(R.color.txt_black));
        parament_tv.setTextColor(mView.getResources().getColor(R.color.txt_black));
        sun_image_tv.setTextColor(mView.getResources().getColor(R.color.txt_black));
        switch (type) {
            case R.id.product_ll:
                product_view.setVisibility(View.VISIBLE);
                product_tv.setTextColor(mView.getResources().getColor(R.color.colorPrimaryRed));
                container_vp.setCurrentItem(0, true);
                break;
            case R.id.detail_ll:
                detail_view.setVisibility(View.VISIBLE);
                detail_tv.setTextColor(mView.getResources().getColor(R.color.colorPrimaryRed));
                container_vp.setCurrentItem(1, true);
                break;
            case R.id.parament_ll:
                parament_view.setVisibility(View.GONE);
                parament_tv.setTextColor(mView.getResources().getColor(R.color.colorPrimaryRed));
                container_vp.setCurrentItem(2, true);
                break;
            case R.id.sun_image_ll:
                sun_image_view.setVisibility(View.GONE);
                sun_image_tv.setTextColor(mView.getResources().getColor(R.color.colorPrimaryRed));
                container_vp.setCurrentItem(3, true);
                break;
        }

    }

    @Override
    protected void handleMessage(int action, Object[] values) {

    }

    @Override
    protected void myHandleMessage(Message msg) {

    }

    /**
     * 产品详情
     */
    public void sendProductDetail() {
        mNetWork.sendProductDetail(mView.mProductId, this);
    }

    @Override
    public void onSuccessListener(String requestCode, JSONObject ans) {
        try{
            mView.hideLoading();
            switch (requestCode) {
                case NetWorkConst.PRODUCTDETAIL:
                    mView.goodses = ans.getJSONObject(Constance.product);
                    try {
                        int level = IssueApplication.mUserObject.getInt(Constance.level);
                        if (level > 0) {
                            mView.mOrderId = mView.goodses.getInt(Constance.order_id);
                        } else {
                            mView.mOrderId = 1;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                case NetWorkConst.CUSTOM:
                    NetWorkConst.QQ = ans.getString(Constance.custom);
                    break;
                case NetWorkConst.ADDCART:
                    //                MyToast.show(mView, UIUtils.getString(R.string.go_cart_ok));
                    sendShoppingCart();

                    break;
                case NetWorkConst.GETCART:
                    goCartAnimator(ans);
                    EventBus.getDefault().post(Constance.CARTCOUNT);
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * 加入购物车动画效果
     */
    public void goCartAnimator(JSONObject ans) {
        CartAnimator cartAnimator = new CartAnimator(mView);
        ImageView iv = new ImageView(mView);
        String path = mView.goodses.getJSONObject(Constance.app_img).getString(Constance.img);
        ImageLoader.getInstance().displayImage(path, iv);
        cartAnimator.setParentView(main_rl);
        cartAnimator.setCartView(collectIv);
        cartAnimator.startCartAnimator(iv, container_vp);


        if (ans.getJSONArray(Constance.goods_groups).length() > 0) {
            IssueApplication.mCartCount = ans.getJSONArray(Constance.goods_groups)
                    .getJSONObject(0).getJSONArray(Constance.goods).length();
            unMessageReadTv.setVisibility(View.VISIBLE);
            unMessageReadTv.setText(IssueApplication.mCartCount + "");
        } else {
            IssueApplication.mCartCount = 0;
            unMessageReadTv.setVisibility(View.GONE);
        }

        Toast.makeText(mView, "加入购物车成功!", Toast.LENGTH_SHORT).show();

    }


    public void sendShoppingCart() {
        mNetWork.sendShoppingCart(this);
    }

    @Override
    public void onFailureListener(String requestCode, JSONObject ans) {
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

    private void sendCustom() {
        mNetWork.sendCustom(this);
    }

//    /**
//     * 联系客服
//     */
//    public void sendCall(String msg) {
//        try {
//            int level = IssueApplication.mUserObject.getInt(Constance.level);
//            if (level == 0) {
//                if (!mView.isToken()) {
//                    IntentUtil.startActivity(mView, ChartListActivity.class, false);
//                }
//                return;
//            }
//
//            String parent_name = IssueApplication.mUserObject.getString("parent_name");
//            String parent_id = IssueApplication.mUserObject.getString("parent_id");
//            String userIcon = NetWorkConst.SCENE_HOST + IssueApplication.mUserObject.getString("parent_avatar");
//            EaseUser user = new EaseUser(parent_id);
//            user.setNickname(parent_name);
//            user.setNick(parent_name);
//            user.setAvatar(userIcon);
//            DemoHelper.getInstance().saveContact(user);
//
//            if (!EMClient.getInstance().isLoggedInBefore()) {
//                ShowDialog mDialog = new ShowDialog();
//                mDialog.show(mView, "提示", msg, new ShowDialog.OnBottomClickListener() {
//                    @Override
//                    public void positive() {
//                        loginHX();
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
//
//
//        } catch (HyphenateException e) {
//            e.printStackTrace();
//        }
//    }
//
//    //登录环信
//    private void loginHX() {
//        final Toast toast = Toast.makeText(mView, "服务器连接中...!", Toast.LENGTH_SHORT);
//        toast.show();
//        if (NetUtils.hasNetwork(mView)) {
//            final String uid = MyShare.get(mView).getString(Constance.USERNAME);
//            if (AppUtils.isEmpty(uid)) {
//                return;
//            }
//            if (!TextUtils.isEmpty(uid)) {
//                getSuccessLogin(uid, toast);
//
//            }
//        }
//    }
//
//    private void getSuccessLogin(final String uid, final Toast toast) {
//        EMClient.getInstance().login(uid, uid, new EMCallBack() {
//            @Override
//            public void onSuccess() {
//                EMClient.getInstance().groupManager().loadAllGroups();
//                EMClient.getInstance().chatManager().loadAllConversations();
//                MyLog.e("登录环信成功!");
//                toast.cancel();
//                String parent_id = IssueApplication.mUserObject.getString("parent_id");
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
//                                        getSuccessLogin(uid, toast);
//                                    }
//                                });
//
//                            } catch (final HyphenateException e) {
//                                mView.runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        MyLog.e("注册失败!");
//                                        getSuccessLogin(uid, toast);
//                                    }
//                                });
//
//                            }
//                        }
//                    }).start();
//                } else {
//                    Toast.makeText(mView, "连接失败,请重试!", Toast.LENGTH_SHORT).show();
//                    MyLog.e("登录环信失败!");
//                    sendCall("连接聊天失败,请重试?");
//                }
//            }
//
//            @Override
//            public void onProgress(int i, String s) {
//            }
//        });
//    }


    /**
     * 购物车
     */
    public void getShoopingCart() {
        IntentUtil.startActivity(mView, ShoppingCartActivity.class, false);
    }

    /**
     * 马上配配看
     */
    public void GoDiyProduct() {
//        if (AppUtils.isEmpty(mView.goodses)) {
//            MyToast.show(mView, "还没加载完毕，请稍后再试");
//            return;
//        }
//        IssueApplication.mSelectProParamemt=new HashMap<>();
//        IssueApplication.mSelectProParamemt.put(mView.mProductId+"",mView.mAttrId);
//        mIntent = new Intent(mView, DiyActivity.class);
//        mIntent.putExtra(Constance.product, mView.goodses);
//        mIntent.putExtra(Constance.property, mView.mProperty);
//        IssueApplication.mSelectProducts.add(mView.goodses);
//        mView.startActivity(mIntent);
        if (AppUtils.isEmpty(mView.mProductObject))
            return;
        if (mView.mProductObject.getJSONArray(Constance.properties).size() == 0) {
            IssueApplication.mSelectProParamemt=new HashMap<>();
        IssueApplication.mSelectProParamemt.put(mView.mProductId+"",mView.mAttrId);
        mIntent = new Intent(mView, DiyActivity.class);
        mIntent.putExtra(Constance.product, mView.goodses);
        mIntent.putExtra(Constance.property, mView.mProperty);
        IssueApplication.mSelectProducts.add(mView.goodses);
        mView.startActivity(mIntent);
        } else {
            selectParament();
        }
    }

    /**
     * 加入购物车
     */
    public void GoShoppingCart() {

        if (AppUtils.isEmpty(mView.mProductObject))
            return;
        if (mView.mProductObject.getJSONArray(Constance.properties).size() == 0) {
            mView.setShowDialog(true);
            mView.setShowDialog("正在加入购物车中...");
            mView.showLoading();
            sendGoShoppingCart(mView.mProductId + "", "", 1);
        } else {
            selectParament();
        }
    }

    private SelectParamentPopWindow mPopWindow;

    /*
         * 选择参数
         */
    public void selectParament() {
        if (AppUtils.isEmpty(mView.mProductObject))
            return;
        mPopWindow = new SelectParamentPopWindow(mView, mView.mProductObject);
        mPopWindow.onShow(main_ll);
        mPopWindow.setListener(new IParamentChooseListener() {
            @Override
            public void onParamentChanged(String text, Boolean isGoCart, String property, String propertyId,String inventoryNum, int mount, double price,int goType,String url) {
                if(goType==1){
                    if(mView.isToken()){
                        return;
                    }
                    IssueApplication.mSelectProParamemt=new HashMap<>();
                    IssueApplication.mSelectProParamemt.put(mView.mProductId+"",mView.mAttrId);
                    mIntent = new Intent(mView, DiyActivity.class);
                    mView.goodses.add(Constance.cproperty,mView.mProperty);
                    mView.goodses.add(Constance.cproperty_id,propertyId);
                    mView.goodses.add(Constance.curl,url);
                    mIntent.putExtra(Constance.product, mView.goodses);
                    mIntent.putExtra(Constance.property, mView.mProperty);
                    mIntent.putExtra(Constance.url,url);
                    IssueApplication.mSelectProducts.add(mView.goodses);
                    mView.startActivity(mIntent);
                    return;
                }
                if (!AppUtils.isEmpty(text)) {
                    mView.mProperty = property;
                    mView.mPrice = price;
                    mView.mPropertyValue = text;
                }
                if (isGoCart == true) {
                    if(mView.isToken()){
                        return;
                    }
                    mView.setShowDialog(true);
                    mView.setShowDialog("正在加入购物车中...");
                    mView.showLoading();
                    sendGoShoppingCart(mView.mProductId + "", property, mount);
                    if (!AppUtils.isEmpty(mIntroduceGoodsFragment)) {
                        if (!AppUtils.isEmpty(mIntroduceGoodsFragment.mController)) {
                            mIntroduceGoodsFragment.mController.getInventoryNum(inventoryNum);
                        }
                    }
                }
                mView.mAttrId=propertyId;
                EventBus.getDefault().post(Constance.PROPERTY);
            }
        });

    }

    private void sendGoShoppingCart(String product, String property, int mount) {
        mNetWork.sendShoppingCart(product, property, mount, this);
    }


    /**
     *
     */
    public void setShare() {

        if (AppUtils.isEmpty(mView.goodses)) {
            MyToast.show(mView, "还没加载完毕，请稍后再试");
            return;
        }
        mIntent = new Intent(mView, ShareProductActivity.class);
        mIntent.putExtra(Constance.product, mView.goodses);
        mIntent.putExtra(Constance.property, mView.mProperty);
        mView.startActivity(mIntent);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case 0:
                selectProductType(R.id.product_ll);
                break;
            case 1:
                selectProductType(R.id.detail_ll);
                break;
            case 2:
                selectProductType(R.id.parament_ll);
                break;
            case 3:
                selectProductType(R.id.sun_image_ll);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private ArrayList<BaseFragment> mFragments;

    /**
     * 随心配
     */
    public void goPhoto() {
        if (AppUtils.isEmpty(mView.goodses)) {
            MyToast.show(mView, "还没加载完毕，请稍后再试");
            return;
        }
        mIntent = new Intent(mView, TestCameraActivity.class);
        mIntent.putExtra(Constance.product, mView.goodses);
        mIntent.putExtra(Constance.property, mView.mProperty);
        mView.startActivity(mIntent);
    }

    public class ProductContainerAdapter extends FragmentPagerAdapter {


        public ProductContainerAdapter(FragmentManager fm) {
            super(fm);
            initFragment();
        }

        private void initFragment() {
            mFragments = new ArrayList<>();
            Bundle bundle = new Bundle();
            bundle.putString(Constance.product, mView.mProductId + "");
            mIntroduceGoodsFragment = new IntroduceGoodsFragment();
            mIntroduceGoodsFragment.setArguments(bundle);
            mIntroduceGoodsFragment.setmListener(new IntroduceGoodsFragment.ScrollListener() {
                @Override
                public void onScrollToBottom(int currPosition) {
                    if (currPosition == 0) {
                        title_ll.setVisibility(View.GONE);
                        sun_image_ll.setVisibility(View.GONE);
                        product_ll.setVisibility(View.VISIBLE);
                        detail_ll.setVisibility(View.VISIBLE);
                        parament_ll.setVisibility(View.GONE);
                    } else {
                        title_ll.setVisibility(View.VISIBLE);
                        product_ll.setVisibility(View.GONE);
                        sun_image_ll.setVisibility(View.GONE);
                        detail_ll.setVisibility(View.GONE);
                        parament_ll.setVisibility(View.GONE);
                    }
                }
            });


            DetailGoodsFragmemt detailFragment = new DetailGoodsFragmemt();
            detailFragment.setArguments(bundle);

            ParameterFragment parameterFragment = new ParameterFragment();
            parameterFragment.setArguments(bundle);

            SunImageFragment sunImageFragment = new SunImageFragment();
            sunImageFragment.setArguments(bundle);


            mFragments.add(mIntroduceGoodsFragment);
            mFragments.add(detailFragment);
            mFragments.add(parameterFragment);
            mFragments.add(sunImageFragment);

        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

    }
}
