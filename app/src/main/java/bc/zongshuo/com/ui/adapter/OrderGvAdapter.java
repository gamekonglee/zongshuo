package bc.zongshuo.com.ui.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import bc.zongshuo.com.R;
import bc.zongshuo.com.cons.Constance;
import bc.zongshuo.com.listener.IUpdateProductPriceListener;
import bc.zongshuo.com.ui.activity.IssueApplication;
import bocang.utils.AppUtils;

import static bc.zongshuo.com.cons.Constance.product_price;

/**
 * @author: Jun
 * @date : 2017/1/21 13:46
 * @description : 选择方案类型
 */
public class OrderGvAdapter extends BaseAdapter {
    private JSONArray mOrderes;
    private Activity mContext;
    private Intent mIntent;
    private List<Boolean> mIsClick;
    int  mOrderLevel;
    int state;
    private IUpdateProductPriceListener mListener;

    public void setUpdateProductPriceListener(IUpdateProductPriceListener mListener) {
        this.mListener = mListener;
    }

    public OrderGvAdapter(Activity context, JSONArray orderes, int  orderLevel,int state) {
        mContext = context;
        mOrderes = orderes;
        mOrderLevel = orderLevel;
       this.state=state;
        mIsClick = new ArrayList<>();
        for (int i = 0; i < mOrderes.size(); i++) {
            mIsClick.add(false);
        }

    }


    @Override
    public int getCount() {
        if (null == mOrderes)
            return 0;
        return mOrderes.size();
    }

    @Override
    public JSONObject getItem(int position) {
        if (null == mOrderes)
            return null;
        return mOrderes.getJSONObject(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_order, null);

            holder = new ViewHolder();

            holder.name_tv = (TextView) convertView.findViewById(R.id.name_tv);
            holder.goods_summoney_tv = (TextView) convertView.findViewById(R.id.goods_summoney_tv);
            holder.goods_sum_tv = (TextView) convertView.findViewById(R.id.goods_sum_tv);
            holder.property_tv = (TextView) convertView.findViewById(R.id.property_tv);
            holder.old_priceTv = (TextView) convertView.findViewById(R.id.old_priceTv);
            holder.update_product_money_tv = (TextView) convertView.findViewById(R.id.update_product_money_tv);
            holder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final JSONObject object = mOrderes.getJSONObject(position);
        String num = object.getString(Constance.total_amount);
        String totalMoney = object.getString(Constance.total_price);
        holder.goods_sum_tv.setText("X" + num + "件");


        final String productPrice = object.getString(product_price);
        final String originalPrice = object.getString(Constance.original_price);
        holder.goods_summoney_tv.setText("优惠价:" + productPrice+ "元");
        Float old_price = Float.parseFloat(object.getString(product_price));
        DecimalFormat decimalFormat = new DecimalFormat(".0");//构造方法的字符格式这里如果小数不足2位,会以0补足.
        String p = decimalFormat.format(old_price * 1.6);//format 返回的是字符串
        holder.old_priceTv.setText("零售价:" + p + "元");
        holder.name_tv.setText(object.getJSONObject(Constance.product).getString(Constance.name));
        String property = object.getString(Constance.property);
        if (!AppUtils.isEmpty(property)) {
            holder.property_tv.setText(property);
            holder.property_tv.setVisibility(View.VISIBLE);
        } else {
            holder.property_tv.setVisibility(View.GONE);
        }
        try {
            ImageLoader.getInstance().displayImage(object.getJSONObject(Constance.product).
                    getJSONArray(Constance.photos).getJSONObject(0).getString(Constance.thumb), holder.imageView);
        } catch (Exception e) {

        }
        int mLevel = IssueApplication.mUserObject.getInt(Constance.level);
        if (mLevel == 0) {

            if (mLevel != mOrderLevel&& state == 0) {
                holder.update_product_money_tv.setVisibility(View.GONE);

            } else {
                holder.update_product_money_tv.setVisibility(View.GONE);
            }



        } else {
            holder.update_product_money_tv.setVisibility(View.GONE);
        }
        holder.update_product_money_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onUpdateProductPriceListener(position, object);
            }
        });


        return convertView;
    }

    class ViewHolder {
        ImageView imageView;
        TextView name_tv;
        TextView goods_summoney_tv;
        TextView old_priceTv;
        TextView goods_sum_tv;
        TextView property_tv;
        TextView update_product_money_tv;
    }


}
