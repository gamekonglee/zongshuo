package bc.zongshuo.com.ui.view.popwindow;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lib.common.hxp.view.GridViewForScrollView;
import com.lib.common.hxp.view.ListViewForScrollView;

import java.util.ArrayList;
import java.util.List;

import bc.zongshuo.com.R;
import bc.zongshuo.com.cons.Constance;
import bc.zongshuo.com.cons.NetWorkConst;
import bc.zongshuo.com.listener.INumberInputListener;
import bc.zongshuo.com.listener.IParamentChooseListener;
import bc.zongshuo.com.ui.activity.IssueApplication;
import bc.zongshuo.com.ui.view.NumberInputView;
import bc.zongshuo.com.utils.ImageLoadProxy;
import bc.zongshuo.com.utils.MyShare;
import bocang.json.JSONArray;
import bocang.json.JSONObject;
import bocang.utils.AppUtils;


/**
 * @author: Jun
 * @date : 2017/2/16 15:12
 * @description :
 */
public class SelectProductParamentPopWindow extends BasePopwindown implements View.OnClickListener {
    private ImageView close_iv, goods_iv;
    private LinearLayout bg_ll;
    private TextView goods_name_tv, proPriceTv, inventory_tv, parament_tv;
    private Button btn_goShoppingCart, toDiyBtn;
    private JSONObject mGoodObject;
    private ListViewForScrollView properties_lv;
//    private JSONArray propertiesList;
    private ProAdapter mAdapter;
    private NumberInputView mNumberInputView;
    private JSONObject itemObject;
    private int mAmount = 1;
    private String mParamentValue = "";
    private double mPrice = 0;
    private String mInventoryNum = "0";
    private String mParamentId = "";
    private String mProductId = "";
    private List<String> mPropertiesArr = new ArrayList<>();

    private IParamentChooseListener mListener;

    public void setListener(IParamentChooseListener listener) {
        mListener = listener;
    }

    public SelectProductParamentPopWindow(Context context, JSONObject goodObject) {
        super(context);
        mGoodObject = goodObject;
        initViewData();

    }

    @Override
    protected void initView(Context context) {
        View contentView = View.inflate(mContext, R.layout.pop_select_product_parament, null);
        initUI(contentView);

    }

    private void initViewData() {
        goods_name_tv.setText(mGoodObject.getString(Constance.name));

        if (!AppUtils.isEmpty(mGoodObject.getJSONObject(Constance.default_photo))) {
            String imageUrl = mGoodObject.getJSONObject(Constance.default_photo).getString(Constance.large);
            ImageLoadProxy.displayImage(imageUrl, goods_iv);
        }
        JSONArray itemArray = mGoodObject.getJSONArray(Constance.properties);
        for (int i = 0; i < itemArray.length(); i++) {
            String name = itemArray.getJSONObject(i).getString(Constance.name);
            if ("规格".equals(name)) {
                mPropertiesArr.add(itemArray.get(i).toString());
                break;
            }
        }

        mProductId = mGoodObject.getString(Constance.id);
        mAdapter = new ProAdapter();
        properties_lv.setAdapter(mAdapter);
        if (!AppUtils.isEmpty(mPropertiesArr) && mPropertiesArr.size() > 0) {
            JSONObject itemObject1=new  JSONObject(mPropertiesArr.get(0)) ;
            final JSONArray attrsList = itemObject1.getJSONArray(Constance.attrs);
            itemObject = attrsList.getJSONObject(0);
            int parantLevel = MyShare.get(mContext).getInt(Constance.parant_level);
            mInventoryNum = itemObject.getString(Constance.number);
            parament_tv.setText(itemObject.getString(Constance.attr_name));
            String img = NetWorkConst.SCENE_HOST + itemObject.getString(Constance.img);
            ImageLoadProxy.displayImage(img, goods_iv);
            String token = MyShare.get(mContext).getString(Constance.TOKEN);
            if (AppUtils.isEmpty(token)) {
                mPrice = Double.parseDouble(itemObject.getString(("attr_price_" + (parantLevel + 1)).replace("10", "")));
            } else {
                int levelId = IssueApplication.mUserObject.getInt(Constance.level_id);
                mPrice = Double.parseDouble(itemObject.getString(("attr_price_" + (levelId + 1)).replace("10", "")));
            }
            proPriceTv.setText("￥" + mPrice);
            if (!AppUtils.isEmpty(mInventoryNum)) {
                mInventoryNum = mInventoryNum.replace(".00", "");
                inventory_tv.setText("库存：" + mInventoryNum);
            }
            return;
        }
        proPriceTv.setText("￥" + mGoodObject.getString(Constance.current_price));

    }

    private void initUI(View contentView) {
        close_iv = (ImageView) contentView.findViewById(R.id.close_iv);
        bg_ll = (LinearLayout) contentView.findViewById(R.id.bg_ll);
        close_iv.setOnClickListener(this);
        bg_ll.setOnClickListener(this);
        goods_iv = (ImageView) contentView.findViewById(R.id.goods_iv);
        goods_name_tv = (TextView) contentView.findViewById(R.id.goods_name_tv);
        parament_tv = (TextView) contentView.findViewById(R.id.parament_tv);
        proPriceTv = (TextView) contentView.findViewById(R.id.proPriceTv);
        inventory_tv = (TextView) contentView.findViewById(R.id.inventory_tv);
        btn_goShoppingCart = (Button) contentView.findViewById(R.id.btn_goShoppingCart);
        btn_goShoppingCart.setOnClickListener(this);
        toDiyBtn = (Button) contentView.findViewById(R.id.toDiyBtn);
        toDiyBtn.setOnClickListener(this);
        properties_lv = (ListViewForScrollView) contentView.findViewById(R.id.properties_lv);
        properties_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });


        mNumberInputView = (NumberInputView) contentView.findViewById(R.id.number_input_et);
        mNumberInputView.setMax(10000);
        mNumberInputView.setListener(new INumberInputListener() {
            @Override
            public void onTextChange(int index) {
                mAmount = index;
            }
        });

//        mPopupWindow = new PopupWindow(contentView, -1, -1);
//        // 1.让mPopupWindow内部的控件获取焦点
////        mPopupWindow.setFocusable(true);
////        // 2.mPopupWindow内部获取焦点后 外部的所有控件就失去了焦点
////        mPopupWindow.setOutsideTouchable(true);
//        //只有加载背景图还有效果
//        // 3.如果不马上显示PopupWindow 一般建议刷新界面
//        mPopupWindow.update();
//        // 设置弹出窗体显示时的动画，从底部向上弹出
//        mPopupWindow.setAnimationStyle(R.style.AnimBottom);
        mPopupWindow = new PopupWindow(contentView, -1, -1);
        // 1.让mPopupWindow内部的控件获取焦点
        mPopupWindow.setFocusable(true);
        // 2.mPopupWindow内部获取焦点后 外部的所有控件就失去了焦点
        mPopupWindow.setOutsideTouchable(true);
        //只有加载背景图还有效果
        mPopupWindow.setBackgroundDrawable(new ColorDrawable());
        // 3.如果不马上显示PopupWindow 一般建议刷新界面
        mPopupWindow.update();
        mPopupWindow.setAnimationStyle(R.style.AnimBottom);
    }

    @Override
    public void onClick(View v) {
        String property = "";
        String url="";
        if (!AppUtils.isEmpty(itemObject)) {
            mParamentId = itemObject.getString(Constance.id);
            url=itemObject.getString(Constance.img);
            if(TextUtils.isEmpty(url)){
                url=mGoodObject.getJSONObject(Constance.default_photo).getString(Constance.thumb);
            }else {
                url=NetWorkConst.SCENE_HOST+url;
            }
            property = "{\"id\":" + mParamentId + "}";
        } else {
            onDismiss();
        }
        switch (v.getId()) {
            case R.id.close_iv:
                mListener.onParamentChanged(mParamentValue, false, property, mParamentId, mInventoryNum, mAmount, mPrice, 0,url);
                onDismiss();
                break;
            case R.id.bg_ll:
                mListener.onParamentChanged(mParamentValue, false, property, mParamentId, mInventoryNum, mAmount, mPrice, 0,url);
                onDismiss();
                break;
            case R.id.btn_goShoppingCart://加入购物车
                mListener.onParamentChanged(mParamentValue, true, property, mParamentId, mInventoryNum, mAmount, mPrice, 0,url);
                onDismiss();
                break;
            case R.id.toDiyBtn://加入场景
                mListener.onParamentChanged(mParamentValue, true, property, mParamentId, mInventoryNum, mAmount, mPrice, 1,url);
                onDismiss();
                break;
        }
    }

    private class ProAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            if (null == mPropertiesArr)
                return 0;
            return mPropertiesArr.size();
        }


        @Override
        public String getItem(int position) {
            if (null == mPropertiesArr)
                return null;
            return mPropertiesArr.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(mContext, R.layout.item_properties, null);
                //
                holder = new ViewHolder();
                holder.properties_name = (TextView) convertView.findViewById(R.id.properties_name);
                holder.itemGridView = (GridViewForScrollView) convertView.findViewById(R.id.itemGridView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            JSONObject itemObject1=new  JSONObject(mPropertiesArr.get(0)) ;
            final String name = itemObject1.getString(Constance.name);
            holder.properties_name.setText(name);

            if (holder.itemGridView != null) {
                final JSONArray attrsList = itemObject1.getJSONArray(Constance.attrs);
                final ItemProAdapter gridViewAdapter = new ItemProAdapter(attrsList);
                holder.itemGridView.setAdapter(gridViewAdapter);
                holder.itemGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        itemObject = attrsList.getJSONObject(position);
                        int parantLevel = MyShare.get(mContext).getInt(Constance.parant_level);
                        String token = MyShare.get(mContext).getString(Constance.TOKEN);
                        mInventoryNum = itemObject.getString(Constance.number);
                        if (AppUtils.isEmpty(token)) {
                            mPrice = itemObject.getInt(("attr_price_" + (parantLevel + 1)).replace("10", ""));
                        } else {
                            int levelId = IssueApplication.mUserObject.getInt(Constance.level_id);
                            mPrice = itemObject.getInt(("attr_price_" + (levelId + 1)).replace("10", ""));
                        }
                        if (!AppUtils.isEmpty(mInventoryNum)) {
                            mInventoryNum = mInventoryNum.replace(".00", "");
                            inventory_tv.setText("库存：" + mInventoryNum);
                        }
                        proPriceTv.setText("￥" + mPrice);
                        gridViewAdapter.mCurrentPoistion = position;
                        gridViewAdapter.notifyDataSetChanged();
                        parament_tv.setText(itemObject.getString(Constance.attr_name));
                        String img=itemObject.getString(Constance.img);
                        if(TextUtils.isEmpty(img)){
                            img=mGoodObject.getString(Constance.original_img);
                        }else {
                            img=NetWorkConst.SCENE_HOST+img;
                        }
                        ImageLoadProxy.displayImage(img, goods_iv);
                        mParamentValue = name + ":" + itemObject.getString(Constance.attr_name) + "";
                    }
                });
//                holder.itemGridView.performItemClick(null, 0, 0);
            }

            return convertView;
        }


        class ViewHolder {
            TextView properties_name;
            GridViewForScrollView itemGridView;
        }
    }

    private class ItemProAdapter extends BaseAdapter {
        public int mCurrentPoistion = 0;
        JSONArray mDatas;

        public ItemProAdapter(JSONArray datas) {
            this.mDatas = datas;
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
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(mContext, R.layout.item_properties02, null);
                //
                holder = new ViewHolder();
                holder.item_tv = (TextView) convertView.findViewById(R.id.item_tv);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.item_tv.setText(mDatas.getJSONObject(position).getString(Constance.attr_name));
            holder.item_tv.setSelected(mCurrentPoistion == position ? true : false);

            return convertView;
        }

        class ViewHolder {
            TextView item_tv;
        }
    }

}
