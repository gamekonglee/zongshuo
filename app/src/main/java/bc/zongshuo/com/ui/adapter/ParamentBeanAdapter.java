package bc.zongshuo.com.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import bc.zongshuo.com.R;
import bc.zongshuo.com.bean.Attachments;
import bc.zongshuo.com.cons.Constance;

/**
 * @author: Jun
 * @date : 2017/2/14 15:07
 * @description :
 */
public class ParamentBeanAdapter extends BaseAdapter {
    private List<Attachments> mParamentLists;
    private Context mContext;

    public ParamentBeanAdapter(List<Attachments> paramentLists, Context context) {
        mParamentLists = paramentLists;
        mContext = context;
    }

    @Override
    public int getCount() {
        if (null == mParamentLists)
            return 0;
        return mParamentLists.size();
    }

    @Override
    public Object getItem(int position) {
        if (null == mParamentLists)
            return null;
        return mParamentLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_parameter_product, null);

            holder = new ViewHolder();
            holder.name_tv = (TextView) convertView.findViewById(R.id.name_tv);
            holder.value_tv = (TextView) convertView.findViewById(R.id.value_tv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.name_tv.setText(mParamentLists.get(position).getName()+":");
        if (mParamentLists.get(position).getAttrs().size()>0) {
            String attrsName="";
            for(int i=0;i<mParamentLists.get(position).getAttrs().size();i++){
                attrsName+=mParamentLists.get(position).getAttrs().get(i).getAttr_name()+"+";
            }
            attrsName=attrsName.substring(0,attrsName.length()-1);
            holder.value_tv.setText(attrsName);
        }
        return convertView;
    }

    class ViewHolder {
        TextView name_tv;
        TextView value_tv;

    }
}
