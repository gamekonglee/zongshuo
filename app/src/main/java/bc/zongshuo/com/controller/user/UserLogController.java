package bc.zongshuo.com.controller.user;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lib.common.hxp.view.ListViewForScrollView;
import com.lib.common.hxp.view.PullToRefreshLayout;

import java.util.ArrayList;

import bc.zongshuo.com.R;
import bc.zongshuo.com.bean.Logistics;
import bc.zongshuo.com.cons.Constance;
import bc.zongshuo.com.controller.BaseController;
import bc.zongshuo.com.data.LogisticDao;
import bc.zongshuo.com.listener.INetworkCallBack;
import bc.zongshuo.com.ui.activity.user.UserLogActivity;
import bc.zongshuo.com.ui.activity.user.UserLogAddActivity;
import bc.zongshuo.com.utils.ColorUtil;
import bocang.json.JSONObject;

/**
 * @author: Jun
 * @date : 2017/1/19 17:11
 * @description :
 */
public class UserLogController extends BaseController implements PullToRefreshLayout.OnRefreshListener, INetworkCallBack {
    private UserLogActivity mView;
    private ProAdapter mProAdapter;
    private ListViewForScrollView order_sv;
    private int page = 1;
    private View mNullView;
    private View mNullNet;
    private Button mRefeshBtn;
    private TextView mNullNetTv;
    private TextView mNullViewTv;
    private ArrayList<Logistics> mLogisticList;
    private LogisticDao mLogisticDao;
    private Intent mIntent;
    private ImageView iv;
    private TextView tip_tv;


    public UserLogController(UserLogActivity v) {
        mView = v;
        initView();
        initViewData();
    }

    private void initViewData() {
        mLogisticList = (ArrayList<Logistics>) mLogisticDao.getAll();
        if (mLogisticList.size() == 0) {
            mNullView.setVisibility(View.VISIBLE);
            iv.setImageResource(R.drawable.icon_no_address);
        } else {
            mNullView.setVisibility(View.GONE);
        }
        mProAdapter.notifyDataSetChanged();
        tip_tv.setText(ColorUtil.getSpecialTextColor("重要消息：卖家会尽可能满足您指定物流公司的需求，因工厂所在区域各不相同，请下单后工厂客服确认物流公司。",
                "重要消息", mView.getResources().getColor(R.color.red)));
    }

    private void initView() {
        order_sv = (ListViewForScrollView) mView.findViewById(R.id.order_sv);
        order_sv.setDivider(null);//去除listview的下划线
        mProAdapter = new ProAdapter();
        order_sv.setAdapter(mProAdapter);
        order_sv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });

        mNullView = mView.findViewById(R.id.null_view);
        tip_tv = (TextView) mView.findViewById(R.id.tip_tv);
        mNullNet = mView.findViewById(R.id.null_net);
        mRefeshBtn = (Button) mNullNet.findViewById(R.id.refesh_btn);
        mNullNetTv = (TextView) mNullNet.findViewById(R.id.tv);
        mNullViewTv = (TextView) mNullView.findViewById(R.id.tv);
        iv = (ImageView) mNullView.findViewById(R.id.iv);
        mLogisticDao = new LogisticDao(mView);
    }


    @Override
    protected void handleMessage(int action, Object[] values) {

    }

    @Override
    protected void myHandleMessage(Message msg) {

    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
    }


    @Override
    public void onSuccessListener(String requestCode, JSONObject ans) {
    }

    @Override
    public void onFailureListener(String requestCode, JSONObject ans) {
        getOutLogin(mView, ans);
    }

    private class ProAdapter extends BaseAdapter {
        public ProAdapter() {
        }

        @Override
        public int getCount() {
            if (null == mLogisticList)
                return 0;
            return mLogisticList.size();
        }

        @Override
        public Logistics getItem(int position) {
            if (null == mLogisticList)
                return null;
            return mLogisticList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(mView, R.layout.item_user_logistic, null);

                holder = new ViewHolder();
                holder.consignee_tv = (TextView) convertView.findViewById(R.id.consignee_tv);
                holder.address_tv = (TextView) convertView.findViewById(R.id.address_tv);
                holder.phone_tv = (TextView) convertView.findViewById(R.id.phone_tv);
                holder.default_addr_tv = (TextView) convertView.findViewById(R.id.default_addr_tv);
                holder.ll = (LinearLayout) convertView.findViewById(R.id.ll);
                holder.delete_iv = (ImageView) convertView.findViewById(R.id.delete_iv);
                holder.edit_iv = (ImageView) convertView.findViewById(R.id.edit_iv);
                holder.select_ll = (LinearLayout) convertView.findViewById(R.id.select_ll);
                holder.edit_rl = (RelativeLayout) convertView.findViewById(R.id.edit_rl);
                holder.select_cb = (CheckBox) convertView.findViewById(R.id.select_cb);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }


            Logistics logistics = mLogisticList.get(position);
            final int id = logistics.getId();
            holder.consignee_tv.setText(logistics.getName());
            holder.address_tv.setText(logistics.getAddress());
            holder.phone_tv.setText(logistics.getTel());
            holder.edit_rl.setVisibility(mView.isSelectLogistics == true ? View.GONE : View.VISIBLE);
            holder.delete_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(mView).setTitle(null).setMessage("是否删除该物流地址")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mLogisticDao.deleteOne(mLogisticList.get(position).getId());
                                    initViewData();
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    return;
                                }
                            }).show();
                }
            });

            holder.select_cb.setChecked(logistics.getIsDefault() == 1 ? true : false);

            holder.edit_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mIntent = new Intent(mView, UserLogAddActivity.class);
                    mIntent.putExtra(Constance.logistics, mLogisticList.get(position));
                    mView.startActivityForResult(mIntent, Constance.FROMLOG);
                }
            });
            holder.ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mView.isSelectLogistics) {
                        mIntent = new Intent();
                        mIntent.putExtra(Constance.logistics, mLogisticList.get(position));
                        mView.setResult(Constance.FROMLOG, mIntent);//告诉原来的Activity 将数据传递给它
                        mView.finish();//一定要调用该方法 关闭新的AC 此时 老是AC才能获取到Itent里面的值
                    } else {
                        mIntent = new Intent(mView, UserLogAddActivity.class);
                        mIntent.putExtra(Constance.logistics, mLogisticList.get(position));
                        mView.startActivityForResult(mIntent, Constance.FROMLOG);
                    }

                }
            });
            holder.select_ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mLogisticDao.updateIsdefault();
                    mLogisticDao.updateIsdefault(id);
                    mLogisticList = (ArrayList<Logistics>) mLogisticDao.getAll();
                    notifyDataSetChanged();
                }
            });
            return convertView;
        }

        class ViewHolder {
            TextView consignee_tv;
            TextView address_tv;
            TextView phone_tv;
            TextView default_addr_tv;
            LinearLayout ll;
            RelativeLayout edit_rl;
            ImageView delete_iv, edit_iv;
            LinearLayout select_ll;
            CheckBox select_cb;
        }
    }

    public void ActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constance.FROMLOG) {
            initViewData();
        }
    }
}
