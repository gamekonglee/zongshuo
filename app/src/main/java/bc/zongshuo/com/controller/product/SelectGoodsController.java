package bc.zongshuo.com.controller.product;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lib.common.hxp.view.PullToRefreshLayout;
import com.nostra13.universalimageloader.core.ImageLoader;

import bc.zongshuo.com.R;
import bc.zongshuo.com.cons.Constance;
import bc.zongshuo.com.cons.NetWorkConst;
import bc.zongshuo.com.controller.BaseController;
import bc.zongshuo.com.listener.INetworkCallBack;
import bc.zongshuo.com.listener.IParamentChooseListener;
import bc.zongshuo.com.ui.activity.IssueApplication;
import bc.zongshuo.com.ui.activity.product.ClassifyGoodsActivity;
import bc.zongshuo.com.ui.activity.product.ProDetailActivity;
import bc.zongshuo.com.ui.activity.product.SelectGoodsActivity;
import bc.zongshuo.com.ui.view.EndOfGridView;
import bc.zongshuo.com.ui.view.EndOfListView;
import bc.zongshuo.com.ui.view.PMSwipeRefreshLayout;
import bc.zongshuo.com.ui.view.popwindow.SelectProductParamentPopWindow;
import bc.zongshuo.com.utils.ConvertUtil;
import bc.zongshuo.com.utils.MyShare;
import bc.zongshuo.com.utils.UIUtils;
import bocang.json.JSONArray;
import bocang.json.JSONObject;
import bocang.utils.AppUtils;
import bocang.utils.LogUtils;
import bocang.utils.MyToast;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * @author: Jun
 * @date : 2017/2/16 17:30
 * @description :产品列表
 */
public class SelectGoodsController extends BaseController implements INetworkCallBack, PullToRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener, EndOfListView.OnEndOfListListener {
    private SelectGoodsActivity mView;
    private EditText et_search;
    private JSONArray goodses;
//    private PullToRefreshLayout mPullToRefreshLayout;
    private ProAdapter mProAdapter;
    private EndOfGridView order_sv;
    private int currentPage = 1;
    private View mNullView;
    private View mNullNet;
    private Button mRefeshBtn;
    private TextView mNullNetTv;
    private TextView mNullViewTv;
    private String per_pag = "20";
    public int mScreenWidth;
    public String mSortKey;
    public String mSortValue;
    private TextView popularityTv, priceTv, newTv, saleTv;
    private ImageView price_iv, popularity_line_iv, new_line_iv, sale_line_iv, price_line_iv;
    private Intent mIntent;
    private ProgressBar pd;
    private LinearLayout main_ll;
    private PMSwipeRefreshLayout pullToRefresh;
    private boolean isbottom;


    public SelectGoodsController(SelectGoodsActivity v) {
        mView = v;
        initView();
        initViewData();
    }

    private void initViewData() {
        if (!AppUtils.isEmpty(mView.mSort)) {
            if (mView.mSort == 4) {
                selectSortType(R.id.saleTv);
            } else if (mView.mSort == 5) {
                selectSortType(R.id.newTv);
            } else if (mView.mSort == 2) {
                selectSortType(R.id.popularityTv);
            }
        }
        pullToRefresh.setRefreshing(true);
        if (mView.mIsYiJI) {
            selectYijiProduct(1, per_pag, null, null, null);
        } else {
            selectProduct(1, per_pag, null, null, null);
        }
    }

    private void initView() {
        et_search = (EditText) mView.findViewById(R.id.et_search);
//        mPullToRefreshLayout = ((PullToRefreshLayout) mView.findViewById(R.id.refresh_view));
//        mPullToRefreshLayout.setOnRefreshListener(this);
        pullToRefresh = mView.findViewById(R.id.pullToRefresh);
        pullToRefresh.setColorSchemeColors(Color.BLUE,Color.RED,
                mView.getResources().getColor(R.color.yellow),mView.getResources().getColor(R.color.qysn_green));
        pullToRefresh.setOnRefreshListener(this);
        order_sv =  mView.findViewById(R.id.priductGridView);
        order_sv.setOnEndOfListListener(this);
        mProAdapter = new ProAdapter();
        order_sv.setAdapter(mProAdapter);
        order_sv.setOnItemClickListener(this);

        mNullView = mView.findViewById(R.id.null_view);
        mNullNet = mView.findViewById(R.id.null_net);
        mRefeshBtn = (Button) mNullNet.findViewById(R.id.refesh_btn);
        mNullNetTv = (TextView) mNullNet.findViewById(R.id.tv);
        mNullViewTv = (TextView) mNullView.findViewById(R.id.tv);
        mScreenWidth = mView.getResources().getDisplayMetrics().widthPixels;
        popularityTv = (TextView) mView.findViewById(R.id.popularityTv);
        popularity_line_iv = (ImageView) mView.findViewById(R.id.popularity_line_iv);
        new_line_iv = (ImageView) mView.findViewById(R.id.new_line_iv);
        sale_line_iv = (ImageView) mView.findViewById(R.id.sale_line_iv);
        price_line_iv = (ImageView) mView.findViewById(R.id.price_line_iv);
        priceTv = (TextView) mView.findViewById(R.id.priceTv);
        newTv = (TextView) mView.findViewById(R.id.newTv);
        saleTv = (TextView) mView.findViewById(R.id.saleTv);
        main_ll = (LinearLayout) mView.findViewById(R.id.main_ll);
        price_iv = (ImageView) mView.findViewById(R.id.price_iv);
        pd = (ProgressBar) mView.findViewById(R.id.pd);
        et_search.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                // 修改回车键功能
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    // 先隐藏键盘
                    ((InputMethodManager) mView.getSystemService(INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(mView
                                            .getCurrentFocus().getWindowToken(),
                                    InputMethodManager.HIDE_NOT_ALWAYS);
                }
                mView.mFilterAttr="";
                mView.mCategoriesId="";
                if (mView.mIsYiJI) {
                    selectYijiProduct(1, per_pag, null, null, null);
                } else {
                    selectProduct(1, per_pag, null, null, null);
                }
                return false;
            }
        });
    }

    private void getLineShow(int type) {
        popularity_line_iv.setVisibility(View.GONE);
        new_line_iv.setVisibility(View.GONE);
        sale_line_iv.setVisibility(View.GONE);
        price_line_iv.setVisibility(View.GONE);
        switch (type) {
            case 0:
                popularity_line_iv.setVisibility(View.VISIBLE);
                break;
            case 1:
                new_line_iv.setVisibility(View.VISIBLE);
                break;
            case 2:
                sale_line_iv.setVisibility(View.VISIBLE);
                break;
            case 3:
                price_line_iv.setVisibility(View.VISIBLE);
                break;
        }
    }


    @Override
    protected void handleMessage(int action, Object[] values) {

    }

    @Override
    protected void myHandleMessage(Message msg) {

    }


    public void selectSortType(int type) {
        popularityTv.setTextColor(mView.getResources().getColor(R.color.fontColor60));
        priceTv.setTextColor(mView.getResources().getColor(R.color.fontColor60));
        newTv.setTextColor(mView.getResources().getColor(R.color.fontColor60));
        saleTv.setTextColor(mView.getResources().getColor(R.color.fontColor60));
        price_iv.setImageResource(R.drawable.arror);

        switch (type) {
            case R.id.popularityTv:
                popularityTv.setTextColor(mView.getResources().getColor(R.color.colorPrimaryRed));
                mSortKey = "";//人气
                mSortValue = "";//排序
                getLineShow(0);
                break;
            case R.id.stylell:
                priceTv.setTextColor(mView.getResources().getColor(R.color.colorPrimaryRed));
                price_iv.setImageResource(R.drawable.arror_top);
                mSortKey = "1";//价格
                mSortValue = "1";//排序
                getLineShow(3);
                break;
            case 2:
                priceTv.setTextColor(mView.getResources().getColor(R.color.colorPrimaryRed));
                price_iv.setImageResource(R.drawable.arror_button);
                mSortKey = "1";//价格
                mSortValue = "2";//排序
                getLineShow(3);
                break;
            case R.id.newTv:
                newTv.setTextColor(mView.getResources().getColor(R.color.colorPrimaryRed));
                mSortKey = "5";//新品
                mSortValue = "1";//排序
                getLineShow(1);
                break;
            case R.id.saleTv:
                saleTv.setTextColor(mView.getResources().getColor(R.color.colorPrimaryRed));
                mSortKey = "4";//销售
                mSortValue = "1";//排序
                getLineShow(2);
                break;
        }
        currentPage = 1;
        order_sv.smoothScrollToPositionFromTop(0,0);
        if (mView.mIsYiJI) {
            selectYijiProduct(1, per_pag, null, null, null);
        } else {
            selectProduct(1, per_pag, null, null, null);
        }

    }


    public void selectProduct(int page, String per_page, String brand, String category, String shop) {
        String keyword = et_search.getText().toString();
//        pd.setVisibility(View.VISIBLE);
        pullToRefresh.setRefreshing(true);
        isbottom=false;
        mNetWork.sendGoodsList(page, per_page, brand, mView.mCategoriesId, mView.mFilterAttr, shop, keyword, mSortKey, mSortValue, this);
    }

    public void selectYijiProduct(int page, String per_page, String brand, String category, String shop) {
        String keyword = et_search.getText().toString();
//        pd.setVisibility(View.VISIBLE);
        isbottom=false;
        pullToRefresh.setRefreshing(true);
        String invite_code = MyShare.get(mView).getString(Constance.invite_code);
        mNetWork.selectYijiProduct(page, per_page, brand, mView.mCategoriesId, mView.mFilterAttr, shop, keyword, mSortKey, mSortValue, invite_code, this);
    }

    @Override
    public void onSuccessListener(String requestCode, JSONObject ans) {
        try{
            mView.hideLoading();
            pd.setVisibility(View.INVISIBLE);
            switch (requestCode) {
                case NetWorkConst.PRODUCT:
                    if (null == mView || mView.isFinishing())
                        return;
                    if (null != pullToRefresh) {
                        dismissRefesh();
                    }

                    JSONArray goodsList = ans.getJSONArray(Constance.goodsList);
                    JSONObject paged=ans.getJSONObject(Constance.paged);
                    int page=paged.getInt(Constance.page);
                    if (AppUtils.isEmpty(goodsList) || goodsList.length() == 0) {
                        if (page == 1) {
                            mNullView.setVisibility(View.VISIBLE);
                        } else {
                            isbottom = true;
                            MyToast.show(mView, "没有更多数据了!");
                        }

                        dismissRefesh();
                        return;
                    }

                    mNullView.setVisibility(View.GONE);
                    mNullNet.setVisibility(View.GONE);
                    getDataSuccess(goodsList,page);
                    break;
                case NetWorkConst.PRODUCTYIJI:
                    if (null == mView || mView.isFinishing())
                        return;
                    if (null != pullToRefresh) {
                        dismissRefesh();
                    }

                    JSONArray goodsList1 = ans.getJSONArray(Constance.goodsList);
                    JSONObject paged2=ans.getJSONObject(Constance.paged);
                    int page2=paged2.getInt(Constance.page);
                    if (AppUtils.isEmpty(goodsList1) || goodsList1.length() == 0) {
                        if (page2 == 1) {
                            mNullView.setVisibility(View.VISIBLE);
                        } else {
                            isbottom = true;
                            MyToast.show(mView, "没有更多数据了!");
                        }

                        dismissRefesh();
                        return;
                    }

                    mNullView.setVisibility(View.GONE);
                    mNullNet.setVisibility(View.GONE);
                    getDataSuccess(goodsList1,page2);
                    return;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onFailureListener(String requestCode, JSONObject ans) {
        mView.hideLoading();
        pd.setVisibility(View.INVISIBLE);
        if (null == mView || mView.isFinishing())
            return;
        this.currentPage--;

        if (AppUtils.isEmpty(ans)) {
            mNullNet.setVisibility(View.VISIBLE);
            mRefeshBtn.setOnClickListener(mRefeshBtnListener);
            return;
        }

        if (null != pullToRefresh) {

            pullToRefresh.post(new Runnable() {
                @Override
                public void run() {
                    pullToRefresh.setRefreshing(false);
                }
            });
//            mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
//            mPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
        }
        getOutLogin(mView, ans);
    }

    private void dismissRefesh() {
        if (null != pullToRefresh) {

            pullToRefresh.post(new Runnable() {
                @Override
                public void run() {
                    pullToRefresh.setRefreshing(false);
                }
            });
//            mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
//            mPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
        }
    }

    private void getDataSuccess(JSONArray array,int page) {
        if (1 == page){
            goodses = array;
        }
        else if (null != goodses) {
            for (int i = 0; i < array.length(); i++) {
                goodses.add(array.getJSONObject(i));
            }

            if (AppUtils.isEmpty(array))
                MyToast.show(mView, "没有更多内容了");
        }
        mProAdapter.notifyDataSetChanged();
//        if(1==page){
//            order_sv.smoothScrollToPositionFromTop(0,0);
//        }
    }

    private View.OnClickListener mRefeshBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onRefresh();
        }
    };

    public void onRefresh() {
        currentPage = 1;
        if (mView.mIsYiJI) {
            selectYijiProduct(currentPage, per_pag, null, null, null);
        } else {
            selectProduct(currentPage, per_pag, null, null, null);
        }

    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        currentPage = 1;
        isbottom=false;
        if (mView.mIsYiJI) {
            selectYijiProduct(currentPage, per_pag, null, null, null);
        } else {
            selectProduct(currentPage, per_pag, null, null, null);
        }

    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
        currentPage = currentPage + 1;
        if (mView.mIsYiJI) {
            selectYijiProduct(currentPage, per_pag, null, null, null);
        } else {
            selectProduct(currentPage, per_pag, null, null, null);
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        if (mView.isSelectGoods == true) {
            final String[] mProperty=new String[1];
            for (int i = 0; i < IssueApplication.mSelectProducts.length(); i++) {
                String selectName = IssueApplication.mSelectProducts.getJSONObject(i).getString(Constance.name);
                String selectProperty=IssueApplication.mSelectProducts.getJSONObject(i).getString(Constance.cproperty);
                String currnetProperty=mProperty[0];
                String name = goodses.getJSONObject(position).getString(Constance.name);
                if (selectName.equals(name)&&currnetProperty!=null&&selectProperty.equals(currnetProperty)) {
                    IssueApplication.mSelectProducts.delete(i);
                    mProAdapter.notifyDataSetChanged();
                    mView.select_num_tv.setText(IssueApplication.mSelectProducts.length() + "");
                    String idd="";
                    idd = goodses.getJSONObject(position).getString(Constance.id);
                    IssueApplication.mSelectProParamemt.remove(idd);
                    return;
                }
            }

            SelectProductParamentPopWindow mPopWindow = new SelectProductParamentPopWindow(mView, goodses.getJSONObject(position));
            mPopWindow.onShow(main_ll);
            mPopWindow.setListener(new IParamentChooseListener() {
                @Override
                public void onParamentChanged(String text, Boolean isGoCart, String property, String propertyId, String inventoryNum, int mount, double price, int goType,String url) {
                    if (isGoCart) {
                        String id="";
                        for (int i = 0; i < IssueApplication.mSelectProducts.length(); i++) {
                            String selectName = IssueApplication.mSelectProducts.getJSONObject(i).getString(Constance.name);
                            String selectProperty=IssueApplication.mSelectProducts.getJSONObject(i).getString(Constance.curl);
                            String currentProperty=property;
                            String name = goodses.getJSONObject(position).getString(Constance.name);
                            if (selectName.equals(name)&&selectProperty!=null&&selectProperty.equals(currentProperty)) {
                                IssueApplication.mSelectProducts.delete(i);
                                break;
                            }
                        }
                        mProperty[0]=property;
                        id = goodses.getJSONObject(position).getString(Constance.id);
                        goodses.getJSONObject(position).add(Constance.cproperty,property);
                        goodses.getJSONObject(position).add(Constance.curl,url);
                        IssueApplication.mSelectProducts.add(goodses.getJSONObject(position));
                        IssueApplication.mSelectProParamemt.put(id, propertyId);
                        mProAdapter.notifyDataSetChanged();
                        mView.select_num_tv.setText(IssueApplication.mSelectProducts.length() + "");
                        return;
                    }
                }
            });

        } else {
            mIntent = new Intent(mView, ProDetailActivity.class);
            int productId = goodses.getJSONObject(position).getInt(Constance.id);
            mIntent.putExtra(Constance.product, productId);
            mView.startActivity(mIntent);
        }

    }

    /**
     * 筛选
     */
    public void openClassify() {
        mIntent = new Intent(mView, ClassifyGoodsActivity.class);
        mView.startActivityForResult(mIntent, 103);
    }

    @Override
    public void onEndOfList(Object lastItem) {
        currentPage = currentPage + 1;
        LogUtils.logE("onEnd", currentPage +"");
        if (mView.mIsYiJI) {
            selectYijiProduct(currentPage, per_pag, null, null, null);
        } else {
            selectProduct(currentPage, per_pag, null, null, null);
        }
    }


    private class ProAdapter extends BaseAdapter {
        public ProAdapter() {
        }

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
                convertView = View.inflate(mView, R.layout.item_gridview_fm_product, null);
                holder = new ViewHolder();
                holder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
                holder.check_iv = (ImageView) convertView.findViewById(R.id.check_iv);
                holder.textView = (TextView) convertView.findViewById(R.id.name_tv);
                holder.groupbuy_tv = (TextView) convertView.findViewById(R.id.groupbuy_tv);
                holder.attrs_tv = (TextView) convertView.findViewById(R.id.attrs_tv);
                holder.old_price_tv = (TextView) convertView.findViewById(R.id.old_price_tv);
                holder.price_tv = (TextView) convertView.findViewById(R.id.price_tv);
                RelativeLayout.LayoutParams lLp = (RelativeLayout.LayoutParams) holder.imageView.getLayoutParams();
                float h = (mScreenWidth - ConvertUtil.dp2px(mView, 45.8f)) / 2;
                lLp.height = (int) h;
                holder.imageView.setLayoutParams(lLp);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            try {
                String name = goodses.getJSONObject(position).getString(Constance.name);
                holder.textView.setText(name);
                ImageLoader.getInstance().displayImage(goodses.getJSONObject(position).getJSONObject(Constance.default_photo).getString(Constance.thumb)
                        , holder.imageView);

                JSONObject groupBuyObject = goodses.getJSONObject(position).getJSONObject(Constance.group_buy);
                holder.groupbuy_tv.setVisibility(!AppUtils.isEmpty(groupBuyObject) ? View.VISIBLE : View.GONE);
                JSONArray properties=goodses.getJSONObject(position).getJSONArray(Constance.properties);
                int currentP=0;
                for(int i=0;i<properties.length();i++){
                    if(properties.getJSONObject(i).getString(Constance.name).equals("规格")){
                        currentP=i;
                        break;
                    }
                }

                String current_price = goodses.getJSONObject(position).getString(Constance.current_price);
                String token=MyShare.get(mView).getString(Constance.TOKEN);
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
                    current_price = UIUtils.getMiniPrice(104,properties.getJSONObject(currentP).getJSONArray(Constance.attrs));
                    holder.old_price_tv.setVisibility(View.GONE);
                    holder.price_tv.setText("￥" + current_price);
                }
                JSONArray propertieArray = goodses.getJSONObject(position).getJSONArray(Constance.properties);
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
                        int parantLevel = MyShare.get(mView).getInt(Constance.parant_level);

                        price = attrsArray.getJSONObject(0).getInt("attr_price_5");
                        String parament = attrsArray.getJSONObject(0).getString(Constance.attr_name);
                        double currentPrice =  price;
//                        holder.price_tv.setText("￥" + currentPrice);
                        holder.attrs_tv.setText(parament);
                    }
//                    holder.attrs_tv.setVisibility(View.VISIBLE);
                } else {
//                    holder.attrs_tv.setVisibility(View.VISIBLE);
                }


                holder.check_iv.setVisibility(View.GONE);
                if (mView.isSelectGoods == true) {
                    for (int i = 0; i < IssueApplication.mSelectProducts.length(); i++) {
                        String goodName = IssueApplication.mSelectProducts.getJSONObject(i).getString(Constance.name);
                        if (name.equals(goodName)) {
                            holder.check_iv.setVisibility(View.VISIBLE);
                            break;
                        }

                    }
                    if (propertieArray.length() > 0) {
                        JSONArray attrsArray = propertieArray.getJSONObject(currentProperty).getJSONArray(Constance.attrs);
                        if (!AppUtils.isEmpty(attrsArray)) {
                            if (!AppUtils.isEmpty(IssueApplication.mSelectProParamemt)) {
                                String id = goodses.getJSONObject(position).getString(Constance.id);
                                String paramentId = IssueApplication.mSelectProParamemt.get(id);
                                for(int j=0;j<attrsArray.length();j++){
                                    String attrid=attrsArray.getJSONObject(j).getString(Constance.id);
                                    if(paramentId.equals(attrid)){
                                        int price = 0;
                                        int parantLevel = MyShare.get(mView).getInt(Constance.parant_level);

                                        if (AppUtils.isEmpty(token)) {
                                            price = attrsArray.getJSONObject(currentProperty).getInt(("attr_price_" + (parantLevel + 1)).replace("10",""));

                                        } else {
                                            int levelId = IssueApplication.mUserObject.getInt(Constance.level_id);
                                            price = attrsArray.getJSONObject(currentProperty).getInt(("attr_price_" + (levelId + 1)).replace("10",""));

                                        }
                                        String parament = attrsArray.getJSONObject(j).getString(Constance.attr_name);
                                        holder.attrs_tv.setText(parament);//3210696429
                                         token=MyShare.get(mView).getString(Constance.TOKEN);
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
                                            current_price = UIUtils.getMiniPrice(104,properties.getJSONObject(currentP).getJSONArray(Constance.attrs));
                                            holder.old_price_tv.setVisibility(View.GONE);
                                            holder.price_tv.setText("￥" + current_price);
                                        }

                                        break;
                                    }
                                }
                            }
                        }


                    }


                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return convertView;
        }

        class ViewHolder {
            ImageView imageView;
            ImageView check_iv;
            TextView textView;
            TextView groupbuy_tv;
            TextView old_price_tv;
            TextView price_tv;
            TextView attrs_tv;

        }
    }
}
