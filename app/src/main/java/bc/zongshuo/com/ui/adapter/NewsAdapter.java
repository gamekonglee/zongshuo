package bc.zongshuo.com.ui.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import bc.zongshuo.com.R;
import bc.zongshuo.com.cons.Constance;
import bc.zongshuo.com.ui.activity.IssueApplication;
import bc.zongshuo.com.utils.ConvertUtil;
import bc.zongshuo.com.utils.MyShare;
import bc.zongshuo.com.utils.UIUtils;
import bocang.json.JSONArray;
import bocang.json.JSONObject;
import bocang.utils.AppUtils;

import static bc.zongshuo.com.R.id.attrs_tv;

/**
 * Description :
 * Author : lauren
 * Email  : lauren.liuling@gmail.com
 * Blog   : http://www.liuling123.com
 * Date   : 15/12/19
 */
public class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private JSONArray mData;
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;

    public NewsAdapter(Context context) {
        this.mContext = context;
    }

    public void setmDate(JSONArray data) {
        this.mData = data;
        this.notifyDataSetChanged();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_gridview_fm_product03, parent, false);
        ItemViewHolder vh = new ItemViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {

            JSONObject itemObject = mData.getJSONObject(position);
            if (itemObject == null) {
                return;
            }
            try {
                String imagePath = mData.getJSONObject(position).getJSONObject(Constance.default_photo).getString(Constance.thumb);
                ImageLoader.getInstance().displayImage(imagePath, ((ItemViewHolder) holder).imageView);
                String name = mData.getJSONObject(position).getString(Constance.name);
                ((ItemViewHolder) holder).name_tv.setText(name);

                String current_price = mData.getJSONObject(position).getString(Constance.current_price);
                String token=MyShare.get(mContext).getString(Constance.TOKEN);
                JSONArray properties=mData.getJSONObject(position).getJSONArray(Constance.properties);
                int currentP=0;
                for(int i=0;i<properties.length();i++){
                    if(properties.getJSONObject(i).getString(Constance.name).equals("规格")){
                        currentP=i;
                        break;
                    }
                }
                if(!TextUtils.isEmpty(token)&&IssueApplication.mUserObject.getInt(Constance.level_id)!=104){
                    int levelId = IssueApplication.mUserObject.getInt(Constance.level_id);
                    current_price = properties.getJSONObject(currentP).getJSONArray(Constance.attrs).getJSONObject(0).getInt(("attr_price_" + (levelId + 1)).replace("10",""))+"";
                    ((ItemViewHolder) holder).price_tv.setText("代理价：￥" + current_price);
                    ((ItemViewHolder)holder).old_price_tv.setText("原价：¥"+mData.getJSONObject(position).getString(Constance.price));
                    ((ItemViewHolder)holder).old_price_tv.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                    ((ItemViewHolder)holder).old_price_tv.setVisibility(View.VISIBLE);
                }else {
                    ((ItemViewHolder)holder).old_price_tv.setVisibility(View.GONE);
                    ((ItemViewHolder) holder).price_tv.setText("￥" + current_price);
                }
                if (properties.length() > 0) {
                    JSONArray attrsArray = properties.getJSONObject(currentP).getJSONArray(Constance.attrs);
                    if (!AppUtils.isEmpty(attrsArray)) {
                        int price = 0;
                        int parantLevel = MyShare.get(this.mContext).getInt(Constance.parant_level);
                        price = attrsArray.getJSONObject(currentP).getInt("attr_price_5");

                        String parament = attrsArray.getJSONObject(currentP).getString(Constance.attr_name);
                        double currentPrice = Double.parseDouble(current_price) + price;
//                        ((ItemViewHolder) holder).price_tv.setText("￥" + currentPrice);
                        ((ItemViewHolder) holder).attrs_tv.setText(parament);
                    }
                    ((ItemViewHolder) holder).attrs_tv.setVisibility(View.GONE);
                } else {
                    ((ItemViewHolder) holder).attrs_tv.setVisibility(View.GONE);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mData == null) {
            return 0;
        }
        return mData.length();
    }

    public JSONObject getItem(int position) {
        return mData == null ? null : mData.getJSONObject(position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView imageView;
        public TextView name_tv;
        public TextView price_tv;
        public TextView attrs_tv;
        public TextView old_price_tv;

        public ItemViewHolder(View v) {
            super(v);
            imageView = (ImageView) v.findViewById(R.id.imageView);
            name_tv = (TextView) v.findViewById(R.id.name_tv);
            price_tv = (TextView) v.findViewById(R.id.price_tv);
            attrs_tv = (TextView) v.findViewById(R.id.attrs_tv);
            old_price_tv=v.findViewById(R.id.old_price_tv);
//            RelativeLayout.LayoutParams lLp = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
//            float h = (mScreenWidth - ConvertUtil.dp2px(mView.getActivity(), 45.8f)) / 2;
//            lLp.height = (int) h;
//            holder.imageView.setLayoutParams(lLp);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(view, this.getPosition());
            }
        }
    }

}
