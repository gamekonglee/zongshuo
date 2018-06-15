package bc.zongshuo.com.controller.product;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Message;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.bigkoo.convenientbanner.CBPageAdapter;
import com.bigkoo.convenientbanner.CBViewHolderCreator;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.lib.common.hxp.view.ListViewForScrollView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.qysn.social.mqtt.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bc.zongshuo.com.R;
import bc.zongshuo.com.bean.Attrs;
import bc.zongshuo.com.bean.Goods;
import bc.zongshuo.com.bean.GoodsDetail;
import bc.zongshuo.com.bean.Photos;
import bc.zongshuo.com.bean.Properties;
import bc.zongshuo.com.cons.Constance;
import bc.zongshuo.com.cons.NetWorkConst;
import bc.zongshuo.com.controller.BaseController;
import bc.zongshuo.com.listener.INetworkCallBack;
import bc.zongshuo.com.listener.INetworkCallBack02;
import bc.zongshuo.com.listener.IParamentChooseListener;
import bc.zongshuo.com.ui.activity.IssueApplication;
import bc.zongshuo.com.ui.activity.product.ProDetailActivity;
import bc.zongshuo.com.ui.activity.programme.DiyActivity;
import bc.zongshuo.com.ui.adapter.ParamentAdapter;
import bc.zongshuo.com.ui.adapter.ParamentBeanAdapter;
import bc.zongshuo.com.ui.fragment.IntroduceGoodsFragment;
import bc.zongshuo.com.ui.view.countdownview.CountdownView;
import bc.zongshuo.com.ui.view.popwindow.SelectParamentPopWindow;
import bc.zongshuo.com.utils.MyShare;
import bc.zongshuo.com.utils.UIUtils;
import bocang.json.JSONObject;
import bocang.utils.AppDialog;
import bocang.utils.AppUtils;
import bocang.utils.MyToast;
import cn.qqtheme.framework.AppConfig;
import cn.qqtheme.framework.util.LogUtils;

/**
 * @author: Jun
 * @date : 2017/2/14 17:59
 * @description :
 */
public class IntroduceGoodsController extends BaseController implements INetworkCallBack, CountdownView.OnCountdownEndListener {
    private IntroduceGoodsFragment mView;
    private ConvenientBanner mConvenientBanner;
    private List<String> paths = new ArrayList<>();
    private WebView mWebView;
    private TextView unPriceTv, proNameTv, proPriceTv, numTv;
    private TextView mParamentTv, remaining_num_tv;
    private ImageView collect_iv;
    private int mIsLike = 0;
    private RelativeLayout rl_rl;
    private String mProperty;
    private ListView parameter_lv;
    private BaseAdapter mAdapter;
    private com.alibaba.fastjson.JSONObject mProductObject;
    private TextView tv_sale;

    public IntroduceGoodsController(IntroduceGoodsFragment v) {
        mView = v;
        initView();
        initViewData();


    }

    private void initViewData() {

        if (AppUtils.isEmpty(((ProDetailActivity) mView.getActivity()).mProductObject)) {
            sendProductDetail();
        } else {
            getProductDetail(((ProDetailActivity) mView.getActivity()).mProductObject);
        }

    }

    /**
     * 产品参数
     */
    public void sendProductParament() {
        mProductObject = ((ProDetailActivity) mView.getActivity()).mProductObject;
        if (AppUtils.isEmpty(mProductObject))
            return;
        try {
            GoodsDetail goodsDetail=new Gson().fromJson(mProductObject.toJSONString(),GoodsDetail.class);
            mAdapter=new ParamentBeanAdapter(goodsDetail.getAttachments(),mView.getActivity());
        }catch (Exception e){
            com.alibaba.fastjson.JSONArray attachArray = mProductObject.getJSONArray(Constance.attachments);
            mAdapter = new ParamentAdapter(attachArray, mView.getActivity());
        }
            parameter_lv.setAdapter(mAdapter);


    }

    private void initView() {
        int mScreenWidth = mView.getActivity().getResources().getDisplayMetrics().widthPixels;
        mConvenientBanner = (ConvenientBanner) mView.getActivity().findViewById(R.id.convenientBanner);
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) mConvenientBanner.getLayoutParams();
        rlp.width = mScreenWidth;
        rlp.height = mScreenWidth - 20;
        mConvenientBanner.setLayoutParams(rlp);

        mWebView = (WebView) mView.getActivity().findViewById(R.id.webView);
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setDefaultTextEncodingName("utf-8");

        unPriceTv = (TextView) mView.getActivity().findViewById(R.id.unPriceTv);
        remaining_num_tv = (TextView) mView.getActivity().findViewById(R.id.remaining_num_tv);
        proNameTv = (TextView) mView.getActivity().findViewById(R.id.proNameTv);
        numTv = (TextView) mView.getActivity().findViewById(R.id.numTv);
        proPriceTv = (TextView) mView.getActivity().findViewById(R.id.proPriceTv);
        mParamentTv = (TextView) mView.getActivity().findViewById(R.id.type_tv);
        collect_iv = (ImageView) mView.getActivity().findViewById(R.id.collect_iv);
        rl_rl = (RelativeLayout) mView.getActivity().findViewById(R.id.rl_rl);
        parameter_lv =  mView.getActivity().findViewById(R.id.parameter_lv);
        tv_sale = mView.getActivity().findViewById(R.id.tv_sale);

    }

    /**
     * 产品详情
     */
    public void sendProductDetail() {
        mView.setShowDialog(true);
        mView.setShowDialog("载入中...");
        mView.showLoading();

        String id = mView.productId;
        if (AppUtils.isEmpty(id))
            return;


        mNetWork.sendProductDetail02(id, new INetworkCallBack02() {
            @Override
            public void onSuccessListener(String requestCode, com.alibaba.fastjson.JSONObject ans) {
                try {
                    mView.hideLoading();
                    ((ProDetailActivity) mView.getActivity()).mProductObject = ans.getJSONObject(Constance.product);
                    getProductDetail(((ProDetailActivity) mView.getActivity()).mProductObject);
                    sendProductParament();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailureListener(String requestCode, com.alibaba.fastjson.JSONObject ans) {
                mView.hideLoading();
                if(mView!=null&&mView.getActivity()!=null&&!mView.getActivity().isFinishing()){
                    MyToast.show(mView.getActivity(), "网络异常，请重新加载!");
                    getOutLogin02(mView.getActivity(), ans);
                }
            }
        });

    }

    public String mCurrentPrice = "";
    public String mOldPrice = "";

    /**
     * 获取产品详情信息
     */
    private void getProductDetail(com.alibaba.fastjson.JSONObject productObject) {
//        bocang.utils.LogUtils.logE("productionObject",productObject.toJSONString());
        try {
            GoodsDetail goodsDetail = new Gson().fromJson(productObject.toJSONString(), GoodsDetail.class);
            final String value = goodsDetail.getGoods_desc();
            mIsLike = goodsDetail.getIs_liked();
            final String productName = goodsDetail.getName();
            mCurrentPrice = goodsDetail.getCurrent_price();
            mOldPrice = goodsDetail.getPrice();
            tv_sale.setText("销量："+goodsDetail.getSales_count());
            List<Photos> photos = goodsDetail.getPhotos();
            if (!AppUtils.isEmpty(photos)) {
                for (int i = 0; i < photos.size(); i++) {
                    paths.add(photos.get(i).getLarge());
                }
            }

            getWebView(value);
            mConvenientBanner.setPages(
                    new CBViewHolderCreator<NetworkImageHolderView>() {
                        @Override
                        public NetworkImageHolderView createHolder() {
                            return new NetworkImageHolderView();
                        }
                    }, paths);
            mConvenientBanner.setPageIndicator(new int[]{R.drawable.dot_unfocuse, R.drawable.dot_focuse});
            proNameTv.setText(productName);
            selectCollect();
            List<Properties> properties = goodsDetail.getProperties();
            if (properties.size() > 0) {
                for (int i = 0; i < properties.size(); i++) {
                    String name = properties.get(i).getName();
                    if ("规格".equals(name)) {
                        List<Attrs> attrsArray = properties.get(i).getAttrs();
                        int miniPosition=UIUtils.getMiniPricePostion(attrsArray);
                        if (!AppUtils.isEmpty(attrsArray) && attrsArray.size() > 0) {
                            String token = MyShare.get(mView.getContext()).getString(Constance.TOKEN);
                            String price = "0";
//                            int parantLevel = MyShare.get(mView.getContext()).getInt(Constance.parant_level);
                            String num = attrsArray.get(miniPosition).getNumber() + "";
                            if (AppUtils.isEmpty(token)) {
                                price = attrsArray.get(miniPosition).getAttr_price_5() + "";
                            } else {
                                price = AppUtils.getLevelPrice(properties, i, miniPosition);
                            }
                            mCurrentPrice = price + "";
                            String parament = attrsArray.get(miniPosition).getAttr_name();
                            ((ProDetailActivity) mView.getActivity()).mAttrId = attrsArray.get(miniPosition).getId() + "";
                            if (!AppUtils.isEmpty(num)) {
                                num = num.replace(".00", "");
                                num = num.replace(".0", "");
                                numTv.setText("库存：" + num);
                            }

                            String currentPrice = price;
                            unPriceTv.setText("￥" + mOldPrice);
                            unPriceTv.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                            proPriceTv.setText("￥" + currentPrice);
                            mParamentTv.setText("已选 " + name + ":" + parament);
                            return;
                        }
                    }
                }
            }
            proPriceTv.setText("￥" + mCurrentPrice);
            unPriceTv.setText("￥" + mOldPrice);
            unPriceTv.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            remaining_num_tv.setVisibility(View.GONE);
        } catch (Exception e) {


            final String value = productObject.getString(Constance.goods_desc);
            mIsLike = productObject.getInteger(Constance.is_liked);
            final String productName = productObject.getString(Constance.name);
            mCurrentPrice = productObject.getString(Constance.current_price);
            mOldPrice = productObject.getString(Constance.price);


            com.alibaba.fastjson.JSONArray array = productObject.getJSONArray(Constance.photos);
            if (!AppUtils.isEmpty(array)) {
                for (int i = 0; i < array.size(); i++) {
                    paths.add(array.getJSONObject(i).getString(Constance.large));
                }
            }

            getWebView(value);
            mConvenientBanner.setPages(
                    new CBViewHolderCreator<NetworkImageHolderView>() {
                        @Override
                        public NetworkImageHolderView createHolder() {
                            return new NetworkImageHolderView();
                        }
                    }, paths);
            mConvenientBanner.setPageIndicator(new int[]{R.drawable.dot_unfocuse, R.drawable.dot_focuse});
            proNameTv.setText(productName);
            selectCollect();
            JSONArray propertieArray = productObject.getJSONArray(Constance.properties);
            if (propertieArray.size() > 0) {
                for (int i = 0; i < propertieArray.size(); i++) {
                    String name = propertieArray.getJSONObject(i).getString(Constance.name);
                    if ("规格".equals(name)) {
                        JSONArray attrsArray = propertieArray.getJSONObject(i).getJSONArray(Constance.attrs);
                        if (!AppUtils.isEmpty(attrsArray)) {
                            String token = MyShare.get(mView.getContext()).getString(Constance.TOKEN);

                            double price = 0;
                            int parantLevel = MyShare.get(mView.getContext()).getInt(Constance.parant_level);
                            int miniPosition=UIUtils.getMiniPricePostion(attrsArray);
                            String num = attrsArray.getJSONObject(miniPosition).getString(Constance.number);
                            if (AppUtils.isEmpty(token)) {
                                price = attrsArray.getJSONObject(miniPosition).getDouble(("attr_price_5"));
                            } else {
                                int levelId = IssueApplication.mUserObject.getInt(Constance.level_id);
                                price = attrsArray.getJSONObject(miniPosition).getDouble(("attr_price_" + (levelId + 1)).replace("10", ""));
                            }
                            mCurrentPrice = price + "";
                            String parament = attrsArray.getJSONObject(miniPosition).getString(Constance.attr_name);
                            ((ProDetailActivity) mView.getActivity()).mAttrId = attrsArray.getJSONObject(miniPosition).getString(Constance.id);
                            if (!AppUtils.isEmpty(num)) {
                                num = num.replace(".00", "");
                                num = num.replace(".0", "");
                                numTv.setText("库存：" + num);
                            }
                            double currentPrice = price;
                            unPriceTv.setText("￥" + mOldPrice);
                            unPriceTv.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                            proPriceTv.setText("￥" + currentPrice);
                            mParamentTv.setText("已选 " + name + ":" + parament);
                            return;
                        }
                    }
                }
            }
            proPriceTv.setText("￥" + mCurrentPrice);
            unPriceTv.setText("￥" + mOldPrice);
            unPriceTv.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            remaining_num_tv.setVisibility(View.GONE);
        }
    }


    /**
     * 收藏图标状态
     */
    private void selectCollect() {
        if (mIsLike == 0) {
            collect_iv.setImageResource(R.drawable.ic_collect_normal);
        } else {
            collect_iv.setImageResource(R.drawable.ic_collect_press);
        }
    }

    /**
     * 加载网页
     *
     * @param htmlValue
     */
    private void getWebView(String htmlValue) {
        String html = htmlValue;
        html = html.replace("src=\"", "src=\"" + NetWorkConst.SCENE_HOST);
        html = html.replace("</p>", "");
        html = html.replace("<p>", "");

        //        html = html.replace("<p><img src=\"", "<img src=\"" + NetWorkConst.SCENE_HOST);
        //        html = html.replace("</p>", "");


        html = "<meta name=\"viewport\" content=\"width=device-width\">" + html;
        mWebView.loadData(html, "text/html; charset=UTF-8", null);//这种写法可以正确解析中文
    }

    /**
     * 加入收藏
     */
    public void sendCollectGoods() {
        mView.setShowDialog(true);
        mView.setShowDialog("正在收藏中!");
        mView.showLoading();
        String id = mView.productId;
        if (mIsLike == 0) {
            mNetWork.sendAddLikeCollect(id, this);
            mIsLike = 1;
        } else {
            mNetWork.sendUnLikeCollect(id, this);
            mIsLike = 0;
        }
    }

    private SelectParamentPopWindow mPopWindow;

    /*
     * 选择参数
     */
    public void selectParament() {
        if (AppUtils.isEmpty(((ProDetailActivity) mView.getActivity()).mProductObject))
            return;
        mPopWindow = new SelectParamentPopWindow(mView.getActivity(), ((ProDetailActivity) mView.getActivity()).mProductObject);
        mPopWindow.onShow(rl_rl);
        mPopWindow.setListener(new IParamentChooseListener() {

            @Override
            public void onParamentChanged(String text, Boolean isGoCart, String property, String propertyId, String inventoryNum, int mount, double price, int goType,String url) {
                if (goType == 1) {
                    if (mView.isToken()){
                        return;
                    }
                    Intent intent = new Intent(mView.getActivity(), DiyActivity.class);
                    intent.putExtra(Constance.product, ((ProDetailActivity) mView.getActivity()).goodses);
                    intent.putExtra(Constance.property, ((ProDetailActivity) mView.getActivity()).mProperty);
                    IssueApplication.mSelectProducts.add(((ProDetailActivity) mView.getActivity()).goodses);
                    IssueApplication.mSelectProParamemt = new HashMap<>();
                    IssueApplication.mSelectProParamemt.put(mView.productId, propertyId);
                    mView.startActivity(intent);
                    return;
                }
                if (!AppUtils.isEmpty(text)) {
                    mParamentTv.setText("已选 " + text);
                    ((ProDetailActivity) mView.getActivity()).mProperty = property;
                    ((ProDetailActivity) mView.getActivity()).mPrice = price;
                    proPriceTv.setText("￥" + (price));
                    unPriceTv.setText("￥" + ( price));
                    if (!AppUtils.isEmpty(inventoryNum)) {
                        inventoryNum = inventoryNum.replace(".00", "");
                        numTv.setText("库存：" + inventoryNum);
                    }
                }
                if (isGoCart == true) {
                    if (mView.isToken()){
                        return;
                    }
                    mView.setShowDialog(true);
                    mView.setShowDialog("正在加入购物车中...");
                    mView.showLoading();
                    mProperty = property;
                    sendGoShoppingCart(mView.productId, property, mount);
                }
                ((ProDetailActivity) mView.getActivity()).mAttrId = propertyId;
            }
        });

    }


    private void sendGoShoppingCart(String product, String property, int mount) {

        mNetWork.sendShoppingCart(product, property, mount, this);
    }

    public void getInventoryNum(String inventoryNum) {
        if (!AppUtils.isEmpty(inventoryNum)) {
            inventoryNum = inventoryNum.replace(".00", "");
            numTv.setText("库存：" + inventoryNum);
        }
    }


    @Override
    public void onSuccessListener(String requestCode, JSONObject ans) {
        try {
            mView.hideLoading();
            switch (requestCode) {
                case NetWorkConst.ADDLIKEDPRODUCT:
                    selectCollect();
                    break;
                case NetWorkConst.ULIKEDPRODUCT:
                    selectCollect();
                    break;
                case NetWorkConst.ADDCART:
                    MyToast.show(mView.getActivity(), UIUtils.getString(R.string.go_cart_ok));
                    sendShoppingCart();
                    break;
                case NetWorkConst.GETCART:
                    if (ans.getJSONArray(Constance.goods_groups).length() > 0) {
                        IssueApplication.mCartCount = ans.getJSONArray(Constance.goods_groups)
                                .getJSONObject(0).getInt(Constance.total_amount);
                    } else {
                        IssueApplication.mCartCount = 0;
                    }
                    EventBus.getDefault().post(Constance.CARTCOUNT);

                    ((ProDetailActivity) mView.getActivity()).mController.goCartAnimator(ans);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void sendShoppingCart() {
        mNetWork.sendShoppingCart(this);
    }

    @Override
    public void onFailureListener(String requestCode, JSONObject ans) {
        mView.hideLoading();
        if (null == mView || mView.getActivity().isFinishing())
            return;
        if (AppUtils.isEmpty(ans)) {
            AppDialog.messageBox(UIUtils.getString(R.string.server_error));
            return;
        }
        AppDialog.messageBox(ans.getString(Constance.error_desc));
        getOutLogin(mView.getActivity(), ans);
    }

    @Override
    public void onEnd(CountdownView cv) {
        //时间到刷新界面
        try {
            ((ProDetailActivity) mView.getActivity()).mController.initViewData();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    class NetworkImageHolderView implements CBPageAdapter.Holder<String> {
        private ImageView imageView;

        @Override
        public View createView(Context context) {
            // 你可以通过layout文件来创建，也可以像我一样用代码创建，不一定是Image，任何控件都可以进行翻页
            imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            return imageView;
        }

        @Override
        public void UpdateUI(Context context, final int position, String data) {
            imageView.setImageResource(R.drawable.bg_default);
            ImageLoader.getInstance().displayImage(data, imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(view.getContext(), "点击了第" + position + "个", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    protected void handleMessage(int action, Object[] values) {

    }

    @Override
    protected void myHandleMessage(Message msg) {

    }
}
