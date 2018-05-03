package bc.zongshuo.com.controller;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.alibaba.fastjson.JSON;
import com.bigkoo.convenientbanner.CBPageAdapter;
import com.bigkoo.convenientbanner.CBViewHolderCreator;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.lib.common.hxp.view.PullToRefreshLayout;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import bc.zongshuo.com.R;
import bc.zongshuo.com.bean.ArticlesBean;
import bc.zongshuo.com.bean.Customer;
import bc.zongshuo.com.cons.Constance;
import bc.zongshuo.com.cons.NetWorkConst;
import bc.zongshuo.com.listener.INetworkCallBack;
import bc.zongshuo.com.ui.activity.IssueApplication;
import bc.zongshuo.com.ui.activity.MainActivity;
import bc.zongshuo.com.ui.activity.product.ProDetailActivity;
import bc.zongshuo.com.ui.activity.product.SelectGoodsActivity;
import bc.zongshuo.com.ui.activity.user.LoginActivity;
import bc.zongshuo.com.ui.activity.user.MessageDetailActivity;
import bc.zongshuo.com.ui.adapter.ItemAdapter;
import bc.zongshuo.com.ui.adapter.NewsAdapter;
import bc.zongshuo.com.ui.fragment.HomeFragment;
import bc.zongshuo.com.ui.view.HorizontalListView;
import bc.zongshuo.com.ui.view.countdownview.CountdownView;
import bc.zongshuo.com.utils.ConvertUtil;
import bc.zongshuo.com.utils.MyShare;
import bc.zongshuo.com.utils.UIUtils;
import bocang.json.JSONArray;
import bocang.json.JSONObject;
import bocang.utils.AppUtils;
import bocang.utils.MyToast;
import bocang.utils.UniversalUtil;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import static bc.zongshuo.com.R.id.attrs_tv;

/**
 * @author Jun
 * @time 2017/1/7  20:31
 * @desc ${TODD}
 */
public class HomeController extends BaseController implements INetworkCallBack, AdapterView.OnItemClickListener, PullToRefreshLayout.OnRefreshListener {
    private final HomeFragment mView;
    private TextSwitcher textSwitcher_title;
    private int curStr;
    private List<String> paths = new ArrayList<>();
    private List<String> ImageLinks = new ArrayList<>();
    private ConvenientBanner mConvenientBanner;
    public int mScreenWidth;
    private int page = 1;
    private ProAdapter mProAdapter;
//    private ProNewAdapter mProNewAdapter;
//    private ProTelAdapter mProTelAdapter;
    private RecommendProAdapter mReProAdapter;
    private JSONArray goodses;
    private JSONArray goodNewses;
    private JSONArray goodTelses;
    public JSONArray recommendGoodses;
    public JSONArray mPopularityGoodses;
    private PullToRefreshLayout mPullToRefreshLayout;
    private Intent mIntent;
    private JSONArray mArticlesArray;
    private ProgressBar pd;
    private GridView priductGv01;
    private List<ArticlesBean> mArticlesBeans = new ArrayList<>();
    private JSONArray mTimeBuyDatas = new JSONArray();
    private CountdownView cv_countdownView;
    private ArrayList<Long> mEndTimeArry = new ArrayList<>();
    private TextView typeTv, styleTv, spaceTv;
    private GridView itemGridView, mProGridView;
    private ImageView lineIv;
    private ItemAdapter mItemAdapter;
    private List<String> nameList;
    private List<String> iDList;
    private List<String> imageResList;
    //下级选项名称列表
    private List<String> typeList = new ArrayList<>();
    private List<String> spaceList = new ArrayList<>();
    private List<String> styleList = new ArrayList<>();
    //对应的按钮图片
    private List<String> typeResList = new ArrayList<>();
    private List<String> spaceResList = new ArrayList<>();
    private List<String> styleResList = new ArrayList<>();
    //对应分类ID
    private List<String> typeIdList = new ArrayList<>();
    private List<String> spaceIdList = new ArrayList<>();
    private List<String> styleIdList = new ArrayList<>();

//    private HorizontalListView horizon_listview;
    private RecyclerView horizon_listview;
    private RecyclerView tejia_hv;
    private NewsAdapter mAdapter;
    private NewsAdapter mTelAdapter;

    public HomeController(HomeFragment v) {
        super();
        mView = v;
        initView();
        initData();
        initViewData();
        mView.setShowDialog(true);
    }

    private void initData() {
        typeList.clear();
        spaceList.clear();
        styleList.clear();
        typeResList.clear();
        spaceResList.clear();
        styleResList.clear();

        //默认选中类型
        nameList = typeList;
        imageResList = typeResList;
        iDList = typeIdList;
        mItemAdapter.setDatas(nameList, imageResList);
    }

    private LinearLayoutManager mLayoutManager;
    private void initView() {
        textSwitcher_title = (TextSwitcher) mView.getActivity().findViewById(R.id.textSwitcher_title);
        mProGridView = (GridView) mView.getActivity().findViewById(R.id.priductGridView);
        mProGridView.setOnItemClickListener(this);
        cv_countdownView = (CountdownView) mView.getActivity().findViewById(R.id.cv_countdownView);
        mPullToRefreshLayout = ((PullToRefreshLayout) mView.getActivity().findViewById(R.id.refresh_view));
        mPullToRefreshLayout.setOnRefreshListener(this);

        mScreenWidth = mView.getActivity().getResources().getDisplayMetrics().widthPixels;
        mConvenientBanner = (ConvenientBanner) mView.getActivity().findViewById(R.id.convenientBanner);
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) mConvenientBanner.getLayoutParams();
        rlp.width = mScreenWidth;
        rlp.height = (int) (mScreenWidth * (360f / 800f));
        mConvenientBanner.setLayoutParams(rlp);
        pd = (ProgressBar) mView.getActivity().findViewById(R.id.pd);
        priductGv01 = (GridView) mView.getActivity().findViewById(R.id.priductGv01);
        priductGv01.setOnItemClickListener(this);
        mProAdapter = new ProAdapter();

        mProGridView.setAdapter(mProAdapter);

        typeTv = (TextView) mView.getActivity().findViewById(R.id.typeTv);
        styleTv = (TextView) mView.getActivity().findViewById(R.id.styleTv);
        spaceTv = (TextView) mView.getActivity().findViewById(R.id.spaceTv);
        lineIv = (ImageView) mView.getActivity().findViewById(R.id.lineIv);
        itemGridView = (GridView) mView.getActivity().findViewById(R.id.itemGridView);
        horizon_listview = (RecyclerView) mView.getActivity().findViewById(R.id.horizon_listview);
        horizon_listview.setLayoutManager(new LinearLayoutManager(mView.getActivity(),LinearLayoutManager.HORIZONTAL,false));
        mAdapter = new NewsAdapter(mView.getActivity().getApplicationContext());
        horizon_listview.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new NewsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                int productId = goodNewses.getJSONObject(position).getInt(Constance.id);
                getMoreActivity(productId);
            }
        });
        horizon_listview.setItemAnimator(new DefaultItemAnimator());
        mItemAdapter = new ItemAdapter(mView.getContext());
        itemGridView.setAdapter(mItemAdapter);
        itemGridView.setOnItemClickListener(this);
        tejia_hv = (RecyclerView) mView.getActivity().findViewById(R.id.tejia_hv);
        tejia_hv.setLayoutManager(new LinearLayoutManager(mView.getActivity(),LinearLayoutManager.HORIZONTAL,false));
        mTelAdapter = new NewsAdapter(mView.getActivity().getApplicationContext());
        tejia_hv.setAdapter(mTelAdapter);
        mTelAdapter.setOnItemClickListener(new NewsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                int productId = goodTelses.getJSONObject(position).getInt(Constance.id);
                getMoreActivity(productId);
            }
        });
//        mProTelAdapter = new ProTelAdapter();
//        tejia_hv.setAdapter(mProTelAdapter);

    }

    /**
     * 判断是否有toKen
     */
    public Boolean isToken() {
        String token = MyShare.get(mView.getActivity()).getString(Constance.TOKEN);
        if (AppUtils.isEmpty(token)) {
            Intent logoutIntent = new Intent(mView.getActivity(), LoginActivity.class);
            logoutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            mView.startActivity(logoutIntent);
            return true;
        }
        return false;
    }

    /**
     * 初始化加载数据
     */
    private void initViewData() {
        mView.setShowDialog(true);
        mView.showLoading();
        sendBanner();
        page = 1;
        sendRecommendGoodsList(1, 6, null, null, null, null, null, null);
        //        sendPopularityGoodsList(1, null, null, null, null, null, null);
        selectProduct(page, "20");
        selectProductNew();
        selectProductTel();
        sendGoodsType();
        if (AppUtils.isEmpty(mArticlesArray)) {
            sendArticle();
        }
        sendGoodsStyle();
        String token = MyShare.get(mView.getActivity()).getString(Constance.TOKEN);
        if (!AppUtils.isEmpty(token)) {
            sendUser();
        }
        refreshUIWithMessage();

        if (!AppUtils.isEmpty(IssueApplication.mUserObject)) {
            String id = IssueApplication.mUserObject.getString(Constance.id);
            String username = IssueApplication.mUserObject.getString(Constance.username);
            String mobile = IssueApplication.mUserObject.getString(Constance.mobile);
            String sex = "男";
            final String customerJson = JSON.toJSONString(new Customer(id, username, mobile, "", "", "", ""));
            final String customer02Json = JSON.toJSONString(new Customer(id, username, mobile));
            try {
                mNetWork.sendGetCustomer(customerJson, new INetworkCallBack() {
                    @Override
                    public void onSuccessListener(String requestCode, JSONObject ans) {
                        Log.e("520it", "onSuccessListener: " + ans.toString());
                        String errorCode = ans.getString("errorCode");
                        if ("30303".equals(errorCode)) {
                            try {
                                mNetWork.sendRegiestCustomer(customer02Json, new INetworkCallBack() {
                                    @Override
                                    public void onSuccessListener(String requestCode, JSONObject ans) {
                                        Log.e("520it", "onSuccessListener: " + ans.toString());
                                    }

                                    @Override
                                    public void onFailureListener(String requestCode, JSONObject ans) {
                                        Log.e("520it", "onSuccessListener: " + ans.toString());
                                        getOutLogin(mView.getActivity(), ans);
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {

                        }
                    }

                    @Override
                    public void onFailureListener(String requestCode, JSONObject ans) {
                        Log.e("520it", "onFailureListener: " + ans.toString());
                        getOutLogin(mView.getActivity(), ans);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private TextView mCheckedTv;

    /**
     * 类型
     */
    public void getType() {
        mCheckedTv = typeTv;
        typeTv.setTextColor(mView.getResources().getColor(R.color.colorPrimaryRed));

        nameList = typeList;
        iDList = typeIdList;
        imageResList = typeResList;
        mItemAdapter.setDatas(nameList, imageResList);

    }

    /**
     * 风格
     */
    public void getStyle() {
        mCheckedTv = styleTv;
        styleTv.setTextColor(mView.getResources().getColor(R.color.colorPrimaryRed));
        nameList = styleList;
        iDList = styleIdList;
        imageResList = styleResList;
        mItemAdapter.setDatas(nameList, imageResList);
    }

    /**
     * 空间
     */
    public void getSpace() {
        mCheckedTv = spaceTv;
        spaceTv.setTextColor(mView.getResources().getColor(R.color.colorPrimaryRed));
        nameList = spaceList;
        iDList = spaceIdList;
        imageResList = spaceResList;
        mItemAdapter.setDatas(nameList, imageResList);
    }

    public void reSetTextColor() {
        typeTv.setTextColor(Color.GRAY);
        spaceTv.setTextColor(Color.GRAY);
        styleTv.setTextColor(Color.GRAY);
        typeTv.setBackgroundColor(Color.WHITE);
        spaceTv.setBackgroundColor(Color.WHITE);
        styleTv.setBackgroundColor(Color.WHITE);
    }

    /**
     * 获得当前被选中的Button距离左侧的距离
     */
    public float getCurrentCheckedRadioLeft(View v) {
        if (v == typeTv) {
            return 0f;
        } else if (v == spaceTv) {
            return mScreenWidth / 3f;
        } else if (v == styleTv) {
            return mScreenWidth * 2f / 3f;
        }
        return 0f;
    }


    /**
     * 获取新品列表
     *
     * @param page
     * @param per_page
     * @param brand
     * @param category
     * @param shop
     * @param keyword
     * @param sort_key
     * @param sort_value
     */
    private void sendRecommendGoodsList(int page, int per_page, String brand, String category, String shop, String keyword, String sort_key, String sort_value) {
        mNetWork.sendSpecailGoodsList(page,per_page+"",2, new INetworkCallBack() {
            @Override
            public void onSuccessListener(String requestCode, JSONObject ans) {
                getRecommendGoodsList(ans);
            }

            @Override
            public void onFailureListener(String requestCode, JSONObject ans) {

            }
        });
//        mNetWork.sendRecommendGoodsList(page, per_page, brand, category, null, shop, keyword, sort_key, sort_value, this);
    }

    public void refreshTime(long leftTime) {
        if (leftTime > 0) {
            cv_countdownView.start(leftTime);
        } else {
            cv_countdownView.stop();
            cv_countdownView.allShowZero();
        }
    }

    /**
     * 获取人气产品列表
     *
     * @param page
     * @param brand
     * @param category
     * @param shop
     * @param keyword
     * @param sort_key
     * @param sort_value
     */
    private void sendPopularityGoodsList(int page, String brand, String category, String shop, String keyword, String sort_key, String sort_value) {
        mNetWork.sendGoodsList(page, 3 + "", brand, category, null, shop, keyword, "2", "1", new INetworkCallBack() {
                    @Override
                    public void onSuccessListener(String requestCode, JSONObject ans) {
                        try{
                            switch (requestCode) {
                                case NetWorkConst.PRODUCT:
                                    mPopularityGoodses = ans.getJSONArray(Constance.goodsList);
                                    break;

                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailureListener(String requestCode, JSONObject ans) {
                        getOutLogin(mView.getActivity(), ans);
                    }
                }

        );
    }

    /**
     * 获取产品列表
     *
     * @param page
     * @param per_page
     */
    public void selectProduct(final int page, String per_page) {
        Random random=new Random();

        String sortKey=""+(1+random.nextInt(6));
        String sortValue="2";
        mNetWork.sendGoodsList(page, per_page, null, null, null, null, null, sortKey, sortValue, new INetworkCallBack() {
            @Override
            public void onSuccessListener(String requestCode, JSONObject ans) {
                if (null == mView ||mView.getActivity()==null|| mView.getActivity().isFinishing())
                    return;
                if (null != mPullToRefreshLayout) {
                    dismissRefesh();
                }

                JSONArray goodsList = ans.getJSONArray(Constance.goodsList);
                if (AppUtils.isEmpty(goodsList) || goodsList.length() == 0) {
                    if (page == 1) {
                    } else {
                        MyToast.show(mView.getActivity(), "没有更多数据了!");
                    }

                    dismissRefesh();
                    return;
                }

                getDataSuccess(goodsList);
            }

            @Override
            public void onFailureListener(String requestCode, JSONObject ans) {

            }
        });
    }

    /**
     * 获取新品产品列表
     */
    public void selectProductNew() {
        mNetWork.sendSpecailGoodsList(1, "10",1, new INetworkCallBack() {
            @Override
            public void onSuccessListener(String requestCode, JSONObject ans) {
                try{
                    if (null == mView || mView.getActivity().isFinishing())
                        return;
                    if (null != mPullToRefreshLayout) {
                        dismissRefesh();
                    }

                    JSONArray goodsList = ans.getJSONArray(Constance.goodsList);
                    if (AppUtils.isEmpty(goodsList) || goodsList.length() == 0) {
                        if (page == 1) {
                        } else {
                            MyToast.show(mView.getActivity(), "没有更多数据了!");
                        }

                        dismissRefesh();
                        return;
                    }

                    if (1 == page)
                        goodNewses = goodsList;
                    else if (null != goodses) {
                        for (int i = 0; i < goodsList.length(); i++) {
                            goodNewses.add(goodsList.getJSONObject(i));
                        }

                        if (AppUtils.isEmpty(goodsList))
                            MyToast.show(mView.getActivity(), "没有更多内容了");
                    }
//                    mProNewAdapter.notifyDataSetChanged();
                    mAdapter.setmDate(goodNewses);
                }catch (Exception e){
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailureListener(String requestCode, JSONObject ans) {
                getOutLogin(mView.getActivity(), ans);
            }
        });
    }

    /**
     * 获取特价产品列表
     */
    public void selectProductTel() {
        mNetWork.sendGoodsList(1, "10", null, "214 ", null, null, null, null, null, new INetworkCallBack() {
            @Override
            public void onSuccessListener(String requestCode, JSONObject ans) {
                try{
                    if (null == mView || mView.getActivity().isFinishing())
                        return;
                    if (null != mPullToRefreshLayout) {
                        dismissRefesh();
                    }

                    JSONArray goodsList = ans.getJSONArray(Constance.goodsList);
                    if (AppUtils.isEmpty(goodsList) || goodsList.length() == 0) {
                        if (page == 1) {
                        } else {
                            MyToast.show(mView.getActivity(), "没有更多数据了!");
                        }

                        dismissRefesh();
                        return;
                    }

                    if (1 == page)
                        goodTelses = goodsList;
                    else if (null != goodses) {
                        for (int i = 0; i < goodsList.length(); i++) {
                            goodTelses.add(goodsList.getJSONObject(i));
                        }

                        if (AppUtils.isEmpty(goodsList))
                            MyToast.show(mView.getActivity(), "没有更多内容了");
                    }
                    mTelAdapter.setmDate(goodTelses);
                }catch (Exception e){
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailureListener(String requestCode, JSONObject ans) {
                getOutLogin(mView.getActivity(), ans);
            }
        });
    }

    /**
     * 获取用户信息
     */

    public void sendUser() {
        mNetWork.sendUser(this);
    }

    /**
     * 产品类别
     */
    private void sendGoodsType() {
        mNetWork.sendGoodsType(1, 20, null, null, this);
    }


    private void sendGoodsStyle() {
        mNetWork.sendGoodsStyle(this);
    }

    /**
     * 滚动新闻
     */
    private void getNews() {
        try {

            textSwitcher_title.setFactory(new ViewSwitcher.ViewFactory() {
                @Override
                public View makeView() {
                    final TextView tv = new TextView(mView.getActivity());
                    tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    tv.setGravity(Gravity.CENTER_VERTICAL);
                    tv.setSingleLine();
                    tv.setEllipsize(TextUtils.TruncateAt.END);
                    tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String url = mArticlesBeans.get(mNewsPoistion).getUrl();
                            Intent intent = new Intent(mView.getActivity(), MessageDetailActivity.class);
                            intent.putExtra(Constance.url, url);
                            mView.startActivity(intent);
                        }
                    });
                    return tv;
                }
            });

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mNewsPoistion = curStr++ % mArticlesBeans.size();
                    textSwitcher_title.setText(mArticlesBeans.get(mNewsPoistion).getTitle());
                    handler.postDelayed(this, 5000);
                }
            }, 1000);
        } catch (Exception e) {

        }
    }


    private int mNewsPoistion = 0;


    private void sendBanner() {
        mNetWork.sendBanner(this)
        ;
    }

    /**
     * 广告图
     */
    private void getAd() {
        mConvenientBanner.setPages(
                new CBViewHolderCreator<NetworkImageHolderView>() {
                    @Override
                    public NetworkImageHolderView createHolder() {
                        return new NetworkImageHolderView();
                    }
                }, paths);
        mConvenientBanner.setPageIndicator(new int[]{R.drawable.dot_unfocuse, R.drawable.dot_focuse});
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent == mProGridView) {
            int productId = goodses.getJSONObject(position).getInt(Constance.id);
            getMoreActivity(productId);
        } else if (parent == priductGv01) {
            int productId = recommendGoodses.getJSONObject(position).getInt(Constance.id);
            getMoreActivity(productId);
        } else if (parent == itemGridView) {
            if(position==7){
                ((MainActivity)mView.getActivity()).selectItem(R.id.frag_product_ll);
            }else {
            Intent intent = new Intent(mView.getActivity(), SelectGoodsActivity.class);
            String categoriesId = iDList.get(position);
            intent.putExtra(Constance.categories, categoriesId);
            mView.getActivity().startActivity(intent);
            }

        }

    }

    public void getMoreGoods(String id) {
        Intent intent = new Intent(mView.getActivity(), SelectGoodsActivity.class);
        intent.putExtra(Constance.categories, id);
        mView.getActivity().startActivity(intent);
    }

    public void getMore02Goods(int sort) {
        Intent intent = new Intent(mView.getActivity(), SelectGoodsActivity.class);
        intent.putExtra(Constance.sort, sort);
        mView.getActivity().startActivity(intent);
    }


    public void getMoreActivity(int id) {
        Intent intent = new Intent(mView.getActivity(), ProDetailActivity.class);
        int productId = id;
        intent.putExtra(Constance.product, productId);
        mView.getActivity().startActivity(intent);
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        sendGoodsStyle();
        page = 1;
        selectProduct(page, "20");
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
        sendGoodsStyle();
        page = page + 1;
        selectProduct(page, "20");
    }

    /**
     * 跳转到文章信息
     *
     * @param id
     */
    public void getArticleDetail(int id) {
        String url = NetWorkConst.ARTICLE_URL + id;
        Intent intent = new Intent(mView.getActivity(), MessageDetailActivity.class);
        intent.putExtra(Constance.url, url);
        mView.startActivity(intent);
    }




    class NetworkImageHolderView implements CBPageAdapter.Holder<String> {
        private ImageView imageView;

        @Override
        public View createView(Context context) {
            imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            return imageView;
        }

        @Override
        public void UpdateUI(Context context, final int position, String data) {
            imageView.setImageResource(R.drawable.bg_default);
            ImageLoader.getInstance().displayImage(data, imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //跳转到知道的网址
                    String link = ImageLinks.get(position);
                    if (!AppUtils.isEmpty(link)) {
                        mIntent = new Intent();
                        mIntent.setAction(Intent.ACTION_VIEW);
                        mIntent.setData(Uri.parse(ImageLinks.get(position)));
                        mView.startActivity(mIntent);
                    }

                }
            });
        }
    }

    public void setResume() {
        // 开始自动翻页
        mConvenientBanner.startTurning(UniversalUtil.randomA2B(3000, 5000));
    }

    public void setPause() {
        // 停止翻页
        mConvenientBanner.stopTurning();
    }


    /**
     * 获取产品分类列表
     *
     * @param ans
     */
    private void getGoodsType(JSONObject ans) {
        //重新获取，先清空列表
        typeList.clear();
        spaceList.clear();
        styleList.clear();
        typeResList.clear();
        spaceResList.clear();
        styleResList.clear();
        nameList.clear();
        imageResList.clear();

        JSONArray all_attr_list = ans.getJSONArray(Constance.categories);
        ((MainActivity) mView.getActivity()).mCategories = all_attr_list;

        if (AppUtils.isEmpty(all_attr_list))
            return;
        for (int i = 0; i < all_attr_list.length(); i++) {
            JSONObject goodsAllAttr = all_attr_list.getJSONObject(i);
            if (i == 0) {//类型
                String attrName = goodsAllAttr.getString(Constance.name);
                String attrId = goodsAllAttr.getString(Constance.id);
                typeTv.setText(attrName);
                JSONArray attr_list = goodsAllAttr.getJSONArray(Constance.categories);
                for (int j = 0; j < attr_list.length(); j++) {
                    if (j <= 6) {
                        typeList.add(attr_list.getJSONObject(j).getString(Constance.name));
                        typeIdList.add(attr_list.getJSONObject(j).getString(Constance.id));
                        typeResList.add(attr_list.getJSONObject(j).getString(Constance.thumbs));
                    }
                }
                if (attr_list.length() >= 7) {
                    typeList.add("更多");
                    typeIdList.add(attrId);
                    typeResList.add("");
                }

            } else if (i == 1) {//空间
                String attrName = goodsAllAttr.getString(Constance.name);
                String attrId = goodsAllAttr.getString(Constance.id);
                spaceTv.setText(attrName);
                JSONArray attr_list = goodsAllAttr.getJSONArray(Constance.categories);
                for (int j = 0; j < attr_list.length(); j++) {
                    if (j <= 6) {
                        spaceList.add(attr_list.getJSONObject(j).getString(Constance.name));
                        spaceIdList.add(attr_list.getJSONObject(j).getString(Constance.id));
                        spaceResList.add(attr_list.getJSONObject(j).getString(Constance.thumbs));
                    }
                }
                if (attr_list.length() >= 7) {
                    spaceList.add("更多");
                    spaceIdList.add(attrId);
                    spaceResList.add("");
                }

            } else if (i == 2) {//风格
                String attrName = goodsAllAttr.getString(Constance.name);
                String attrId = goodsAllAttr.getString(Constance.id);
                styleTv.setText(attrName);
                JSONArray attr_list = goodsAllAttr.getJSONArray(Constance.categories);
                for (int j = 0; j < attr_list.length(); j++) {
                    if (j <= 6) {
                        styleList.add(attr_list.getJSONObject(j).getString(Constance.name));
                        styleIdList.add(attr_list.getJSONObject(j).getString(Constance.id));
                        styleResList.add(attr_list.getJSONObject(j).getString(Constance.thumbs));
                    }
                }
                if (attr_list.length() >= 7) {
                    styleList.add("更多");
                    styleIdList.add(attrId);
                    styleResList.add("");
                }

            }
        }
        //
        //        if (AppUtils.isEmpty(moreTv.getText().toString())) {
        //            moreTv.setText("更多");
        //        }

        mItemAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSuccessListener(String requestCode, JSONObject ans) {
        try{
            mView.hideLoading();
            switch (requestCode) {
                case NetWorkConst.RECOMMENDPRODUCT:
//                    getRecommendGoodsList(ans);
                    break;
                case NetWorkConst.BANNER:
                    if (null == mView.getActivity() || mView.getActivity().isFinishing())
                        return;
                    JSONArray babbersArray = ans.getJSONArray(Constance.banners);
                    for (int i = 0; i < babbersArray.length(); i++) {
                        try {
                            String imageUri = babbersArray.getJSONObject(i).getString(Constance.url);
                            paths.add(imageUri);
                            ImageLinks.add(babbersArray.getJSONObject(i).getString(Constance.link));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    getAd();
                    break;
                case NetWorkConst.CATEGORY:
                    getGoodsType(ans);
                    pd.setVisibility(View.INVISIBLE);
                    break;
                case NetWorkConst.ARTICLELIST:
                    mArticlesArray = ans.getJSONArray(Constance.articles);
                    for (int i = 0; i < mArticlesArray.length(); i++) {
                        JSONObject jsonObject = mArticlesArray.getJSONObject(i);
                        if (jsonObject.getInt(Constance.article_type) == 1) {
                            int id = jsonObject.getInt(Constance.id);
                            String title = jsonObject.getString(Constance.title);
                            String url = jsonObject.getString(Constance.url);
                            mArticlesBeans.add(new ArticlesBean(id, title, url));
                        }
                    }
                    if (mArticlesBeans.size() == 0)
                        return;

                    getNews();
                    break;
                case NetWorkConst.PRODUCT:

                    break;
                case NetWorkConst.PROFILE:
                    mUserObject = ans.getJSONObject(Constance.user);
                    IssueApplication.mUserObject = mUserObject;
                    if (!AppUtils.isEmpty(mUserObject.getString("parent_id"))) {
                        if (mUserObject.getInt("parent_id") == 0) {
                            MyShare.get(mView.getActivity()).putInt(Constance.USERCODEID, mUserObject.getInt("id"));
                        } else {
                            MyShare.get(mView.getActivity()).putInt(Constance.USERCODEID, mUserObject.getInt("parent_id"));
                        }

                    }
                    if (!AppUtils.isEmpty(mUserObject.getString("parent_name"))) {
                        MyShare.get(mView.getActivity()).putString(Constance.USERCODE, mUserObject.getString("parent_name"));
                    } else {
                        MyShare.get(mView.getActivity()).putString(Constance.USERCODE, mUserObject.getString("nickname"));
                    }

                    String aliasId = IssueApplication.mUserObject.getString(Constance.id);
                    JPushInterface.setAlias(mView.getActivity(), aliasId, new TagAliasCallback() {
                        @Override
                        public void gotResult(int responseCode, String s, Set<String> set) {
                            Log.e("520it", "结果:" + s + ":" + responseCode);
                        }
                    });
                    int level = mUserObject.getInt(Constance.level);
                    String inviteCode = mUserObject.getString(Constance.invite_code);
                    MyShare.get(mView.getActivity()).putString(Constance.invite_code, inviteCode);
                    //                if (level < 3) {
                    //                    if (AppUtils.isEmpty(inviteCode)) {
                    //                        IntentUtil.startActivity(mView.getActivity(), SetInviteCodeActivity.class, false);
                    //                    }
                    //                }


                    break;

            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void getDataSuccess(JSONArray array) {
        if (1 == page)
            goodses = array;
        else if (null != goodses) {
            for (int i = 0; i < array.length(); i++) {
                goodses.add(array.getJSONObject(i));
            }

            if (AppUtils.isEmpty(array))
                MyToast.show(mView.getActivity(), "没有更多内容了");
        }
        mProAdapter.notifyDataSetChanged();
    }


    private void dismissRefesh() {
        mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
        mPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
    }

    public JSONObject mUserObject;

    /**
     * 获取文章列表
     */
    private void sendArticle() {
        int page = 1;
        int per_page = 20;
        mNetWork.sendArticle(page, per_page, this);
    }


    /**
     * 获取最新产品
     *
     * @param ans
     */
    private void getRecommendGoodsList(JSONObject ans) {

        JSONArray goodsList = ans.getJSONArray(Constance.goodsList);

        if (1 == page)
            recommendGoodses = goodsList;
        else if (null != recommendGoodses) {
            for (int i = 0; i < goodsList.length(); i++) {
                recommendGoodses.add(goodsList.getJSONObject(i));
            }

            if (AppUtils.isEmpty(goodsList))
                MyToast.show(mView.getActivity(), "没有更多内容了");
        }

        mReProAdapter = new RecommendProAdapter(recommendGoodses);
        mReProAdapter.notifyDataSetChanged();
        priductGv01.setAdapter(mReProAdapter);
        mReProAdapter.notifyDataSetChanged();
    }


    @Override
    public void onFailureListener(String requestCode, JSONObject ans) {
        if (null == mView.getActivity() || mView.getActivity().isFinishing())
            return;
        this.page--;
        if (null != mPullToRefreshLayout) {
            mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
            mPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
        }

        if (requestCode.equals(NetWorkConst.CATEGORY)) {
            pd.setVisibility(View.INVISIBLE);
            mView.hideLoading();
        }
        getOutLogin(mView.getActivity(), ans);
    }

    private class ProAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (null == goodses)
                return 0;
            return goodses.length();
        }

        @Override
        public JSONObject getItem(int position) {
            if (null == goodses)
                return null;
            return goodses.getJSONObject(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(mView.getActivity(), R.layout.item_gridview_fm_product, null);

                holder = new ViewHolder();
                holder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
                holder.name_tv = (TextView) convertView.findViewById(R.id.name_tv);
                holder.price_tv = (TextView) convertView.findViewById(R.id.price_tv);
                holder.attrs_tv = (TextView) convertView.findViewById(attrs_tv);
                holder.old_price_tv=convertView.findViewById(R.id.old_price_tv);
                RelativeLayout.LayoutParams lLp = (RelativeLayout.LayoutParams) holder.imageView.getLayoutParams();
                float h = (mScreenWidth - ConvertUtil.dp2px(mView.getActivity(), 45.8f)) / 2;
                lLp.height = (int) h;
                holder.imageView.setLayoutParams(lLp);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            try {
                String imagePath = goodses.getJSONObject(position).getJSONObject(Constance.default_photo).getString(Constance.thumb);
                ImageLoader.getInstance().displayImage(imagePath, holder.imageView);
                String name = goodses.getJSONObject(position).getString(Constance.name);
//                String current_price = goodses.getJSONObject(position).getString(Constance.current_price);
                holder.name_tv.setText(name);

                String current_price = goodses.getJSONObject(position).getString(Constance.current_price);
                String token=MyShare.get(mView.getContext()).getString(Constance.TOKEN);
                JSONArray properties=goodses.getJSONObject(position).getJSONArray(Constance.properties);
                int currentP=0;
                for(int i=0;i<properties.length();i++){
                    if(properties.getJSONObject(i).getString(Constance.name).equals("规格")){
                        currentP=i;
                        break;
                    }
                }
                if(!TextUtils.isEmpty(token)&&IssueApplication.mUserObject.getInt(Constance.level_id)!=104){
                    int levelId = IssueApplication.mUserObject.getInt(Constance.level_id);
//                    int miniPricePostion=UIUtils.getMiniPricePostion(levelId,properties.getJSONObject(currentP).getJSONArray(Constance.attrs));
//                    current_price = properties.getJSONObject(currentP).getJSONArray(Constance.attrs).getJSONObject(miniPricePostion).getInt(("attr_price_" + (levelId + 1)).replace("10",""))+"";
                    current_price = UIUtils.getMiniPrice(levelId,properties.getJSONObject(currentP).getJSONArray(Constance.attrs));
                    holder.price_tv.setText("代理价：￥" + current_price);
                    holder.old_price_tv.setText("原价：¥"+goodses.getJSONObject(position).getString(Constance.price));
                    holder.old_price_tv.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                    holder.old_price_tv.setVisibility(View.VISIBLE);
                }else {
                    holder.old_price_tv.setVisibility(View.GONE);
                    holder.price_tv.setText("￥" + current_price);
                }
//                holder.price_tv.setText("￥" + current_price);

//                JSONArray propertieArray = goodses.getJSONObject(position).getJSONArray(Constance.properties);
//                int currentProperty=0;
//                for(int i=0;i<propertieArray.length();i++){
//                    if(propertieArray.getJSONObject(i).getString(Constance.name).equals("规格")){
//                        currentProperty=i;
//                        break;
//                    }
//                }
                if (properties.length() > 0) {
                    JSONArray attrsArray = properties.getJSONObject(currentP).getJSONArray(Constance.attrs);
                    if (!AppUtils.isEmpty(attrsArray)) {
                        int price = 0;
                        int parantLevel = MyShare.get(mView.getContext()).getInt(Constance.parant_level);

                        price = attrsArray.getJSONObject(currentP).getInt("attr_price_5");

                        String parament = attrsArray.getJSONObject(currentP).getString(Constance.attr_name);
//                        double currentPrice =  price;
//                        holder.price_tv.setText("￥" + currentPrice);
                        holder.attrs_tv.setText(parament);
                    }
                    holder.attrs_tv.setVisibility(View.GONE);
                } else {
                    holder.attrs_tv.setVisibility(View.GONE);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }


            return convertView;
        }

        class ViewHolder {
            ImageView imageView;
            TextView name_tv;
            TextView price_tv;
            TextView attrs_tv;
            TextView old_price_tv;
        }
    }

    private class ProNewAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (null == goodNewses)
                return 0;
            return goodNewses.length();
        }

        @Override
        public JSONObject getItem(int position) {
            if (null == goodNewses)
                return null;
            return goodNewses.getJSONObject(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(mView.getActivity(), R.layout.item_gridview_fm_product02, null);

                holder = new ViewHolder();
                holder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
                holder.name_tv = (TextView) convertView.findViewById(R.id.name_tv);
                holder.price_tv = (TextView) convertView.findViewById(R.id.price_tv);
                holder.attrs_tv = (TextView) convertView.findViewById(attrs_tv);
                RelativeLayout.LayoutParams lLp = (RelativeLayout.LayoutParams) holder.imageView.getLayoutParams();
                float h = (mScreenWidth - ConvertUtil.dp2px(mView.getActivity(), 45.8f)) / 2;
                lLp.height = (int) h;
                holder.imageView.setLayoutParams(lLp);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            try {
                String imagePath = goodNewses.getJSONObject(position).getJSONObject(Constance.default_photo).getString(Constance.thumb);
                ImageLoader.getInstance().displayImage(imagePath, holder.imageView);
                String name = goodNewses.getJSONObject(position).getString(Constance.name);
                holder.name_tv.setText(name);
                String current_price = goodNewses.getJSONObject(position).getString(Constance.current_price);
                String token=MyShare.get(mView.getContext()).getString(Constance.TOKEN);
                JSONArray properties=goodNewses.getJSONObject(position).getJSONArray(Constance.properties);
                int currentP=0;
                for(int i=0;i<properties.length();i++){
                    if(properties.getJSONObject(i).getString(Constance.name).equals("规格")){
                        currentP=i;
                        break;
                    }
                }
                if(!TextUtils.isEmpty(token)){
                    int levelId = IssueApplication.mUserObject.getInt(Constance.level_id);
                    current_price = properties.getJSONObject(currentP).getJSONArray(Constance.attrs).getJSONObject(0).getInt(("attr_price_" + (levelId + 1)).replace("10",""))+"";
                    holder.price_tv.setText("代理价：￥" + current_price);
                    holder.old_price_tv.setText("原价：¥"+goodNewses.getJSONObject(position).getString(Constance.price));
                    holder.old_price_tv.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                    holder.old_price_tv.setVisibility(View.VISIBLE);
                }else {
                    holder.old_price_tv.setVisibility(View.GONE);
                    holder.price_tv.setText("￥" + current_price);
                }
                if (properties.length() > 0) {
                    JSONArray attrsArray = properties.getJSONObject(currentP).getJSONArray(Constance.attrs);
                    if (!AppUtils.isEmpty(attrsArray)) {
                        int price = 0;
                        int parantLevel = MyShare.get(mView.getContext()).getInt(Constance.parant_level);
                        price = attrsArray.getJSONObject(currentP).getInt("attr_price_5");

                        String parament = attrsArray.getJSONObject(currentP).getString(Constance.attr_name);
                        double currentPrice = price;
//                        holder.price_tv.setText("￥" + currentPrice);
                        holder.attrs_tv.setText(parament);
                    }
                    holder.attrs_tv.setVisibility(View.GONE);
                } else {
                    holder.attrs_tv.setVisibility(View.GONE);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }


            return convertView;
        }

        class ViewHolder {
            ImageView imageView;
            TextView name_tv;
            TextView price_tv;
            TextView attrs_tv;
            TextView old_price_tv;
        }
    }

    private class ProTelAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (null == goodTelses)
                return 0;
            return goodTelses.length();
        }

        @Override
        public JSONObject getItem(int position) {
            if (null == goodTelses)
                return null;
            return goodTelses.getJSONObject(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(mView.getActivity(), R.layout.item_gridview_fm_product02, null);

                holder = new ViewHolder();
                holder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
                holder.name_tv = (TextView) convertView.findViewById(R.id.name_tv);
                holder.price_tv = (TextView) convertView.findViewById(R.id.price_tv);
                holder.attrs_tv = (TextView) convertView.findViewById(attrs_tv);
                holder.old_price_tv=convertView.findViewById(R.id.old_price_tv);
                RelativeLayout.LayoutParams lLp = (RelativeLayout.LayoutParams) holder.imageView.getLayoutParams();
                float h = (mScreenWidth - ConvertUtil.dp2px(mView.getActivity(), 45.8f)) / 2;
                lLp.height = (int) h;
                holder.imageView.setLayoutParams(lLp);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            try {
                String imagePath = goodTelses.getJSONObject(position).getJSONObject(Constance.default_photo).getString(Constance.thumb);
                ImageLoader.getInstance().displayImage(imagePath, holder.imageView);
                String name = goodTelses.getJSONObject(position).getString(Constance.name);
                holder.name_tv.setText(name);
                String current_price = goodTelses.getJSONObject(position).getString(Constance.current_price);
                String token=MyShare.get(mView.getContext()).getString(Constance.TOKEN);
                JSONArray properties=goodTelses.getJSONObject(position).getJSONArray(Constance.properties);
                int currentP=0;
                for(int i=0;i<properties.length();i++){
                    if(properties.getJSONObject(i).getString(Constance.name).equals("规格")){
                        currentP=i;
                        break;
                    }
                }
                if(!TextUtils.isEmpty(token)){
                    int levelId = IssueApplication.mUserObject.getInt(Constance.level_id);
                    current_price = properties.getJSONObject(currentP).getJSONArray(Constance.attrs).getJSONObject(0).getInt(("attr_price_" + (levelId + 1)).replace("10",""))+"";
                    holder.price_tv.setText("代理价：￥" + current_price);
                    holder.old_price_tv.setText("原价：¥"+goodTelses.getJSONObject(position).getString(Constance.price));
                    holder.old_price_tv.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                    holder.old_price_tv.setVisibility(View.VISIBLE);
                }else {
                    holder.old_price_tv.setVisibility(View.GONE);
                    holder.price_tv.setText("￥" + current_price);
                }

                if (properties.length() > 0) {
                    JSONArray attrsArray = properties.getJSONObject(currentP).getJSONArray(Constance.attrs);
                    if (!AppUtils.isEmpty(attrsArray)) {
                        int price = 0;
                        int parantLevel = MyShare.get(mView.getContext()).getInt(Constance.parant_level);

//                        price = attrsArray.getJSONObject(currentProperty).getInt("attr_price_5");

                        String parament = attrsArray.getJSONObject(currentP).getString(Constance.attr_name);
//                        double currentPrice =price;
//                        holder.price_tv.setText("￥" + currentPrice);
                        holder.attrs_tv.setText(parament);
                    }
                    holder.attrs_tv.setVisibility(View.GONE);
                } else {
                    holder.attrs_tv.setVisibility(View.GONE);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }


            return convertView;
        }

        class ViewHolder {
            ImageView imageView;
            TextView name_tv;
            TextView price_tv;
            TextView attrs_tv;
            TextView old_price_tv;
        }
    }


    public int unreadMsgCount = 0;

    public void refreshUIWithMessage() {
        //        mView.getActivity().runOnUiThread(new Runnable() {
        //            public void run() {
        //                // refresh unread count
        //                for (EMConversation conversation : EMClient.getInstance().chatManager().getAllConversations().values()) {
        //                    unreadMsgCount = conversation.getUnreadMsgCount();
        //                }
        //                //获取此会话在本地的所有的消息数量
        //                //如果只是获取当前在内存的消息数量，调用
        //                IssueApplication.unreadMsgCount = unreadMsgCount;
        //                if (unreadMsgCount == 0) {
        //                    //                    ShortcutBadger.applyCount(this, badgeCount); //for 1.1.4+
        //                    mView.unMessageTv.setVisibility(View.GONE);
        //                    ShortcutBadger.applyCount(mView.getActivity(), 0);
        //                } else {
        //                    ShortcutBadger.applyCount(mView.getActivity(), unreadMsgCount); //for 1.1.4+
        //                    mView.unMessageTv.setVisibility(View.VISIBLE);
        //                    mView.unMessageTv.setText(unreadMsgCount + "");
        //                }
        //            }
        //        });
    }


    @Override
    protected void handleMessage(int action, Object[] values) {

    }

    @Override
    protected void myHandleMessage(Message msg) {

    }


    private class RecommendProAdapter extends BaseAdapter {
        private JSONArray mDatas;

        public RecommendProAdapter(JSONArray datas) {
            mDatas = datas;
        }

        @Override
        public int getCount() {
            if (null == mDatas)
                return 0;
            return mDatas.length();
        }

        @Override
        public JSONObject getItem(int position) {
            if (null == mDatas)
                return null;
            return mDatas.getJSONObject(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            RecommendProAdapter.ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(mView.getActivity(), R.layout.item_gridview_fm_product, null);

                holder = new RecommendProAdapter.ViewHolder();
                holder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
                holder.name_tv = (TextView) convertView.findViewById(R.id.name_tv);
                holder.price_tv = (TextView) convertView.findViewById(R.id.price_tv);
                holder.attrs_tv = (TextView) convertView.findViewById(attrs_tv);
                holder.old_price_tv=convertView.findViewById(R.id.old_price_tv);
                RelativeLayout.LayoutParams lLp = (RelativeLayout.LayoutParams) holder.imageView.getLayoutParams();
                float h = (mScreenWidth - ConvertUtil.dp2px(mView.getActivity(), 45.8f)) / 2;
                lLp.height = (int) h;
                holder.imageView.setLayoutParams(lLp);
                convertView.setTag(holder);
            } else {
                holder = (RecommendProAdapter.ViewHolder) convertView.getTag();
            }
            try {
                String imagePath = mDatas.getJSONObject(position).getJSONObject(Constance.default_photo).getString(Constance.thumb);
                ImageLoader.getInstance().displayImage(imagePath, holder.imageView);
                String name = mDatas.getJSONObject(position).getString(Constance.name);
                String current_price = mDatas.getJSONObject(position).getString(Constance.current_price);
                String token=MyShare.get(mView.getContext()).getString(Constance.TOKEN);
                JSONArray properties=mDatas.getJSONObject(position).getJSONArray(Constance.properties);
                int currentP=0;
                for(int i=0;i<properties.length();i++){
                    if(properties.getJSONObject(i).getString(Constance.name).equals("规格")){
                        currentP=i;
                        break;
                    }
                }
                if(!TextUtils.isEmpty(token)&&IssueApplication.mUserObject.getInt(Constance.level_id)!=104){
                    int levelId = IssueApplication.mUserObject.getInt(Constance.level_id);
//                    int miniPricePostion=UIUtils.getMiniPricePostion(levelId,properties.getJSONObject(currentP).getJSONArray(Constance.attrs));
//                    current_price = properties.getJSONObject(currentP).getJSONArray(Constance.attrs).getJSONObject(miniPricePostion).getInt(("attr_price_" + (levelId + 1)).replace("10",""))+"";
                    current_price = UIUtils.getMiniPrice(levelId,properties.getJSONObject(currentP).getJSONArray(Constance.attrs));
                    holder.price_tv.setText("代理价：￥" + current_price);
                    holder.old_price_tv.setText("原价：¥"+mDatas.getJSONObject(position).getString(Constance.price));
                    holder.old_price_tv.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                    holder.old_price_tv.setVisibility(View.VISIBLE);
                }else {
                    holder.old_price_tv.setVisibility(View.GONE);
                    holder.price_tv.setText("￥" + current_price);
                }
                holder.name_tv.setText(name);

                JSONArray propertieArray = mDatas.getJSONObject(position).getJSONArray(Constance.properties);
                int currentProperty=0;
                for(int i=0;i<propertieArray.length();i++){
                    if(propertieArray.getJSONObject(i).getString(Constance.name).equals("规格")){
                        currentProperty=i;
                        break;
                    }
                }
                if (propertieArray.length() > 0) {
                    JSONArray attrsArray = propertieArray.getJSONObject(currentProperty).getJSONArray(Constance.attrs);
                    if (!AppUtils.isEmpty(attrsArray)) {
                        int price = 0;
                        int parantLevel = MyShare.get(mView.getContext()).getInt(Constance.parant_level);

                        price = attrsArray.getJSONObject(currentProperty).getInt("attr_price_5");
                        String parament = attrsArray.getJSONObject(currentProperty).getString(Constance.attr_name);
                        double currentPrice =price;
//                        holder.price_tv.setText("￥" + currentPrice);
                        holder.attrs_tv.setText(parament);
                    }
                    holder.attrs_tv.setVisibility(View.GONE);
                } else {
                    holder.attrs_tv.setVisibility(View.GONE);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return convertView;
        }

        class ViewHolder {
            ImageView imageView;
            TextView name_tv;
            TextView price_tv;
            TextView attrs_tv;
            TextView old_price_tv;
        }
    }
}
