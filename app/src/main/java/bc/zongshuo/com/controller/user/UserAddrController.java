package bc.zongshuo.com.controller.user;

import android.content.Intent;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lib.common.hxp.view.ListViewForScrollView;
import com.lib.common.hxp.view.PullToRefreshLayout;

import bc.zongshuo.com.R;
import bc.zongshuo.com.cons.Constance;
import bc.zongshuo.com.cons.NetWorkConst;
import bc.zongshuo.com.controller.BaseController;
import bc.zongshuo.com.listener.INetworkCallBack;
import bc.zongshuo.com.ui.activity.user.UserAddrActivity;
import bc.zongshuo.com.ui.activity.user.UserAddrAddActivity;
import bocang.json.JSONArray;
import bocang.json.JSONObject;
import bocang.utils.AppUtils;
import bocang.utils.MyToast;

import static bc.zongshuo.com.R.id.delete_iv;

/**
 * @author: Jun
 * @date : 2017/2/22 14:13
 * @description :收货地址
 */
public class UserAddrController extends BaseController implements INetworkCallBack, PullToRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {
    private UserAddrActivity mView;

    private JSONArray addresses;
    private PullToRefreshLayout mPullToRefreshLayout;
    private ProAdapter mProAdapter;
    private ListViewForScrollView order_sv;
    private int page = 1;

    private View mNullView;
    private View mNullNet;
    private Button mRefeshBtn;
    private TextView mNullNetTv;
    private TextView mNullViewTv;
    private ProgressBar pd;
    private ImageView iv;

    public UserAddrController(UserAddrActivity v) {
        mView = v;
        initView();
        initViewData();
    }

    private void initViewData() {
        mView.showLoadingPage("", R.drawable.ic_loading);
    }

    private void initView() {
        mPullToRefreshLayout = ((PullToRefreshLayout) mView.findViewById(R.id.contentView));
        mPullToRefreshLayout.setOnRefreshListener(this);

        order_sv = (ListViewForScrollView) mView.findViewById(R.id.order_sv);
        order_sv.setDivider(null);//去除listview的下划线
        order_sv.setOnItemClickListener(this);
        mProAdapter = new ProAdapter();
        order_sv.setAdapter(mProAdapter);

        mNullView = mView.findViewById(R.id.null_view);
        mNullNet = mView.findViewById(R.id.null_net);
        mRefeshBtn = (Button) mNullNet.findViewById(R.id.refesh_btn);
        mNullNetTv = (TextView) mNullNet.findViewById(R.id.tv);
        mNullViewTv = (TextView) mNullView.findViewById(R.id.tv);
        iv = (ImageView) mNullView.findViewById(R.id.iv);
        pd = (ProgressBar) mView.findViewById(R.id.pd);


    }

    public void sendAddressList() {
        mNetWork.sendAddressList(this);
    }

    @Override
    protected void handleMessage(int action, Object[] values) {

    }

    @Override
    protected void myHandleMessage(Message msg) {

    }

    @Override
    public void onSuccessListener(String requestCode, JSONObject ans) {
        mView.hideLoading();
        mView.showContentView();
        switch (requestCode) {
            case NetWorkConst.CONSIGNEELIST:
                if (null == mView || mView.isFinishing())
                    return;
                if (null != mPullToRefreshLayout) {
                    dismissRefesh();
                }
                JSONArray consigneeList = ans.getJSONArray(Constance.consignees);
                if (AppUtils.isEmpty(consigneeList)) {
                    if (page == 1) {
                        mNullView.setVisibility(View.VISIBLE);
                        iv.setImageResource(R.drawable.icon_no_address);
                    }

                    dismissRefesh();
                    return;
                }

                mNullView.setVisibility(View.GONE);
                mNullNet.setVisibility(View.GONE);
                getDataSuccess(consigneeList);
                break;
            case NetWorkConst.CONSIGNEEDELETE:
                mView.hideLoading();
                MyToast.show(mView, "删除成功!");
                sendAddressList();
                break;
            case NetWorkConst.CONSIGNEEDEFAULT:
                mView.hideLoading();
                MyToast.show(mView, "保存成功!");
                sendAddressList();
                break;
        }
    }


    @Override
    public void onFailureListener(String requestCode, JSONObject ans) {
        mView.hideLoading();
        if (AppUtils.isEmpty(ans)) {
            mNullNet.setVisibility(View.VISIBLE);
            mRefeshBtn.setOnClickListener(mRefeshBtnListener);
            return;
        }

        if (null != mPullToRefreshLayout) {
            dismissRefesh();
        }
        getOutLogin(mView, ans);
    }

    private void getDataSuccess(JSONArray array) {
        if (1 == page)
            addresses = array;
        else if (null != addresses) {
            for (int i = 0; i < array.length(); i++) {
                addresses.add(array.getJSONObject(i));
            }

            if (AppUtils.isEmpty(array))
                MyToast.show(mView, "没有更多内容了");
        }
        mProAdapter.notifyDataSetChanged();
    }

    private View.OnClickListener mRefeshBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onRefresh();
        }
    };

    public void onRefresh() {
        sendAddressList();
    }

    private void dismissRefesh() {
        mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
        mPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
        pd.setVisibility(View.INVISIBLE);
    }


    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        sendAddressList();
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
        sendAddressList();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mView.isSelectAddress) {
            Intent intent = new Intent();
            intent.putExtra(Constance.address, addresses.getJSONObject(position));
            mView.setResult(Constance.FROMADDRESS, intent);//告诉原来的Activity 将数据传递给它
            mView.finish();//一定要调用该方法 关闭新的AC 此时 老是AC才能获取到Itent里面的值
        } else {
            Intent intent = new Intent(mView, UserAddrAddActivity.class);
            intent.putExtra(Constance.address, addresses.getJSONObject(position));
            intent.putExtra(Constance.UpdateModele, true);
            mView.startActivity(intent);
        }


    }

    /**
     * 删除地址
     */
    public void deleteAddress(String id) {
        mView.setShowDialog(true);
        mView.setShowDialog("正在删除中..");
        mView.showLoading();
        mNetWork.sendDeleteAddress(id, this);
    }

    private void sendDefaultAddress(String id){
        mView.setShowDialog(true);
        mView.setShowDialog("正在删除中..");
        mView.showLoading();
        mNetWork.sendDefaultAddress(id,this);
    }

    private class ProAdapter extends BaseAdapter {
        public ProAdapter() {
        }

        @Override
        public int getCount() {
            if (null == addresses)
                return 0;
            return addresses.length();
        }

        @Override
        public JSONObject getItem(int position) {
            if (null == addresses)
                return null;
            return addresses.getJSONObject(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(mView, R.layout.item_user_address, null);

                holder = new ViewHolder();
                holder.consignee_tv = (TextView) convertView.findViewById(R.id.consignee_tv);
                holder.address_tv = (TextView) convertView.findViewById(R.id.address_tv);
                holder.phone_tv = (TextView) convertView.findViewById(R.id.phone_tv);
                holder.default_addr_tv = (TextView) convertView.findViewById(R.id.default_addr_tv);
                holder.delete_iv = (ImageView) convertView.findViewById(delete_iv);
                holder.edit_iv = (ImageView) convertView.findViewById(R.id.edit_iv);
                holder.select_cb = (CheckBox) convertView.findViewById(R.id.select_cb);
                holder.select_ll = (LinearLayout) convertView.findViewById(R.id.select_ll);
                holder.main_ll = (LinearLayout) convertView.findViewById(R.id.main_ll);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            String name = addresses.getJSONObject(position).getString(Constance.name);
            final String id = addresses.getJSONObject(position).getString(Constance.id);
            holder.consignee_tv.setText(name);
            holder.address_tv.setText(addresses.getJSONObject(position).getString(Constance.address));
            holder.phone_tv.setText(addresses.getJSONObject(position).getString(Constance.mobile));
            boolean isdefault = addresses.getJSONObject(position).getBoolean(Constance.is_default);
            holder.select_cb.setChecked(isdefault ? true : false);
            holder.select_ll .setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendDefaultAddress(id);
                }
            });
            holder.delete_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteAddress(id);
                }
            });
            holder.edit_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mView, UserAddrAddActivity.class);
                    intent.putExtra(Constance.address, addresses.getJSONObject(position));
                    intent.putExtra(Constance.UpdateModele, true);
                    mView.startActivity(intent);
                }
            });
            holder.main_ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mView.isSelectAddress) {
                        Intent intent = new Intent();
                        intent.putExtra(Constance.address, addresses.getJSONObject(position));
                        mView.setResult(Constance.FROMADDRESS, intent);//告诉原来的Activity 将数据传递给它
                        mView.finish();//一定要调用该方法 关闭新的AC 此时 老是AC才能获取到Itent里面的值
                    }
                }
            });
            if (mView.isSelectAddress) {
                holder.delete_iv.setVisibility(View.GONE);
                holder.edit_iv.setVisibility(View.GONE);
            }

            return convertView;
        }

        class ViewHolder {
            TextView consignee_tv;
            TextView address_tv;
            TextView phone_tv;
            TextView default_addr_tv;
            ImageView delete_iv, edit_iv;
            CheckBox select_cb;
            LinearLayout select_ll,main_ll;

        }
    }
}
