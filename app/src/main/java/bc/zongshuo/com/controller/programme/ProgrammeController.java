package bc.zongshuo.com.controller.programme;

import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.lib.common.hxp.view.PullToRefreshLayout;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bc.zongshuo.com.R;
import bc.zongshuo.com.bean.Programme;
import bc.zongshuo.com.cons.Constance;
import bc.zongshuo.com.cons.NetWorkConst;
import bc.zongshuo.com.controller.BaseController;
import bc.zongshuo.com.listener.INetworkCallBack;
import bc.zongshuo.com.ui.activity.product.ProDetailActivity;
import bc.zongshuo.com.ui.activity.programme.DiyActivity;
import bc.zongshuo.com.ui.activity.user.MessageDetailActivity;
import bc.zongshuo.com.ui.adapter.MatchItemAdapter;
import bc.zongshuo.com.ui.fragment.ProgrammeFragment;
import bc.zongshuo.com.ui.view.HorizontalListView;
import bc.zongshuo.com.utils.ShareUtil;
import bc.zongshuo.com.utils.UIUtils;
import bocang.json.JSONArray;
import bocang.json.JSONObject;
import bocang.utils.AppDialog;
import bocang.utils.AppUtils;
import bocang.utils.IntentUtil;
import bocang.utils.MyToast;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @author: Jun
 * @date : 2017/3/10 17:49
 * @description :
 */
public class ProgrammeController extends BaseController implements INetworkCallBack, PullToRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {
    private ProgrammeFragment mView;
    private ProAdapter mProAdapter;
    public int page = 1;
    private int per_page = 20;
    public JSONArray mSchemes;
    private ProgressBar pd;
    public String mStyle;
    public String mSpace;
    private int mDeleteIndex = 0;
    private View mNullView;
    private View mNullNet;
    private Button mRefeshBtn;
    private Button go_btn;
    private TextView mNullNetTv;
    private TextView mNullViewTv;
    private ImageView iv;

    public ProgrammeController(ProgrammeFragment v) {
        mView = v;
        initView();
        initViewData();
    }


    public void initViewData() {
        page = 1;
//        mView.setShowDialog(true);
//        mView.showLoading();
        setropownMenuData();

    }


    private void setropownMenuData() {
        mView.mProgrammes = new ArrayList<>();
        String[] styleArrs = UIUtils.getStringArr(R.array.style);
        String[] spaceArrs = UIUtils.getStringArr(R.array.space);
        Programme programme = new Programme();
        programme.setAttr_name(UIUtils.getString(R.string.style_name));
        programme.setAttrVal(Arrays.asList(styleArrs));
        mView.mProgrammes.add(programme);
        Programme programme2 = new Programme();
        programme2.setAttr_name(UIUtils.getString(R.string.splace_name));
        programme2.setAttrVal(Arrays.asList(spaceArrs));
        mView.mProgrammes.add(programme2);
    }

    private void initView() {
        mView.mPullToRefreshLayout = ((PullToRefreshLayout) mView.getActivity().findViewById(R.id.mFilterContentView));
        mView.mPullToRefreshLayout.setOnRefreshListener(this);

        mProAdapter = new ProAdapter();
        mView.order_sv.setAdapter(mProAdapter);
        mView.order_sv.setOnItemClickListener(this);
        pd = (ProgressBar) mView.getActivity().findViewById(R.id.pd2);
        mNullView = mView.getView().findViewById(R.id.null_programe_view);
        mNullNet = mView.getView().findViewById(R.id.null_net);
        mRefeshBtn = (Button) mNullNet.findViewById(R.id.refesh_btn);
        mNullNetTv = (TextView) mNullNet.findViewById(R.id.tv);
        mNullViewTv = (TextView) mNullView.findViewById(R.id.tv);
        iv = (ImageView) mNullView.findViewById(R.id.iv);
        iv.setImageResource(R.drawable.design_none);
        go_btn = (Button) mNullView.findViewById(R.id.go_btn);
    }

    private List<Integer> itemPosList = new ArrayList<>();//有选中值的itemPos列表，长度为3


    @Override
    protected void handleMessage(int action, Object[] values) {

    }

    @Override
    protected void myHandleMessage(Message msg) {

    }

    public void getRefershData() {
        mView.setShowDialog(true);
        mView.showLoading();
        page = 1;
        sendFangAnList();
    }


    public void sendFangAnList() {
        mView.mPullToRefreshLayout.isMove = false;
        mNetWork.sendFangAnList(page, per_page, mStyle, mSpace, mView.mProgrammeType, this);
    }

    @Override
    public void onSuccessListener(String requestCode, JSONObject ans) {
        try{
            mView.hideLoading();
            go_btn.setVisibility(View.GONE);
            switch (requestCode) {
                case NetWorkConst.FANGANLIST:
                    if (null == mView || mView.getActivity().isFinishing())
                        return;

                    if (null != mView.mPullToRefreshLayout) {
                        dismissRefesh();
                    }
                    JSONArray goodsList = ans.getJSONArray(Constance.fangan);
                    if (AppUtils.isEmpty(goodsList) || goodsList.length() == 0) {
                        if (page == 1) {
                            mNullView.setVisibility(View.VISIBLE);
                            mView.mPullToRefreshLayout.isMove = true;
                            go_btn.setVisibility(View.VISIBLE);
                            go_btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    IntentUtil.startActivity(mView.getActivity(), DiyActivity.class, false);
                                }
                            });

                            mSchemes = new JSONArray();
                            dismissRefesh();
                            pd.setVisibility(View.GONE);
                            return;
                        } else {
                            MyToast.show(mView.getActivity(), "数据已经到底!");
                        }
                    }
                    getDataSuccess(goodsList);
                    pd.setVisibility(View.GONE);
                    break;
                case NetWorkConst.FANGANALLLIST:
                    if (null == mView || mView.getActivity().isFinishing())
                        return;

                    if (null != mView.mPullToRefreshLayout) {
                        dismissRefesh();
                    }
                    JSONArray goodsList1 = ans.getJSONArray(Constance.fangan);
                    if (AppUtils.isEmpty(goodsList1) || goodsList1.length() == 0) {
                        if (page == 1) {
                            mNullView.setVisibility(View.VISIBLE);
                            mView.mPullToRefreshLayout.isMove = true;
                            go_btn.setVisibility(View.VISIBLE);
                            go_btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    IntentUtil.startActivity(mView.getActivity(), DiyActivity.class, false);
                                }
                            });

                            mSchemes = new JSONArray();
                            dismissRefesh();
                            pd.setVisibility(View.GONE);
                            return;
                        } else {
                            MyToast.show(mView.getActivity(), "数据已经到底!");
                        }
                    }
                    getDataSuccess(goodsList1);
                    pd.setVisibility(View.GONE);
                    break;
                case NetWorkConst.FANGANDELETE:
                    mView.showContentView();
                    mSchemes.delete(mDeleteIndex);
                    mProAdapter.notifyDataSetChanged();
                    //                sendFangAnList();
                    break;
                case NetWorkConst.FANGAN_PRIVATE_URL:
                    MyToast.show(mView.getActivity(), "设置成功!");
                    page = 1;
                    sendFangAnList();
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onFailureListener(String requestCode, JSONObject ans) {
        mView.hideLoading();
        if (null == mView ||mView.getActivity()==null&& mView.getActivity().isFinishing())
            return;
        if (AppUtils.isEmpty(ans)) {
            AppDialog.messageBox(UIUtils.getString(R.string.server_error));
            mNullNet.setVisibility(View.VISIBLE);
            mView.mPullToRefreshLayout.isMove = true;
            mRefeshBtn.setOnClickListener(mRefeshBtnListener);
            return;
        }
        go_btn.setVisibility(View.GONE);
        this.page--;
        if (null != mView.mPullToRefreshLayout) {
            dismissRefesh();
        }
        if (requestCode == NetWorkConst.FANGAN_PRIVATE_URL) {
            MyToast.show(mView.getActivity(), "修改失败!");
        }
        getOutLogin(mView.getActivity(), ans);
    }

    private void dismissRefesh() {
        mView.mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
        mView.mPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
    }

    private View.OnClickListener mRefeshBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onRefresh();
        }
    };

    public void onRefresh() {
        pd.setVisibility(View.VISIBLE);
        page = 1;
        sendFangAnList();
    }

    private void getDataSuccess(JSONArray array) {
        if (1 == page)
            mSchemes = array;
        else if (null != mSchemes) {
            for (int i = 0; i < array.length(); i++) {
                mSchemes.add(array.getJSONObject(i));
            }

            if (AppUtils.isEmpty(array))
                MyToast.show(mView.getActivity(), "没有更多内容了");
        }
        mNullView.setVisibility(View.GONE);
        mNullNet.setVisibility(View.GONE);
        mProAdapter.notifyDataSetChanged();
    }

    public void ActivityResult(int requestCode, int resultCode, Intent data) {

    }


    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        pd.setVisibility(View.VISIBLE);
        page = 1;
        sendFangAnList();
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
        pd.setVisibility(View.VISIBLE);
        page = page + 1;

        sendFangAnList();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }


    public void sendDeleteFangan(int id) {
        mView.setShowDialog(true);
        mView.setShowDialog("删除中...");
        mView.showLoading();
        mNetWork.sendDeleteFangan(id, this);
    }


    private class ProAdapter extends BaseAdapter {
        public ProAdapter() {
        }

        @Override
        public int getCount() {
            if (null == mSchemes)
                return 0;
            return mSchemes.length();
        }

        @Override
        public JSONObject getItem(int position) {
            if (null == mSchemes)
                return null;
            return mSchemes.getJSONObject(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(mView.getActivity(), R.layout.item_match, null);

                holder = new ViewHolder();
                holder.close_iv = (ImageView) convertView.findViewById(R.id.close_iv);
                holder.share_iv = (ImageView) convertView.findViewById(R.id.share_iv);
                holder.match_iv = (ImageView) convertView.findViewById(R.id.match_iv);
                holder.match_name_tv = (TextView) convertView.findViewById(R.id.match_name_tv);
                holder.operation_ll = (LinearLayout) convertView.findViewById(R.id.operation_ll);
                holder.public_ll = (LinearLayout) convertView.findViewById(R.id.public_ll);
                holder.private_ll = (LinearLayout) convertView.findViewById(R.id.private_ll);
                holder.share_ll = (LinearLayout) convertView.findViewById(R.id.share_ll);
                holder.delete_ll = (LinearLayout) convertView.findViewById(R.id.delete_ll);
                holder.author_name_tv = (TextView) convertView.findViewById(R.id.author_name_tv);
                holder.time_tv = (TextView) convertView.findViewById(R.id.time_tv);
                holder.match_style_tv = (TextView) convertView.findViewById(R.id.match_style_tv);
                holder.head_iv = (CircleImageView) convertView.findViewById(R.id.head_iv);
                holder.public_state_iv = (ImageView) convertView.findViewById(R.id.public_state_iv);
                holder.horizon_listview = (HorizontalListView) convertView.findViewById(R.id.horizon_listview);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final JSONObject jsonObject = mSchemes.getJSONObject(position);
            String style = jsonObject.getString(Constance.style);
            String space = jsonObject.getString(Constance.space);
            final String id = jsonObject.getString(Constance.id);
            final String path = NetWorkConst.SHAREFANAN + jsonObject.getString(Constance.id);
            final String imgPath = NetWorkConst.SCENE_HOST + jsonObject.getString(Constance.path);
            holder.match_name_tv.setText(URLDecoder.decode(jsonObject.getString(Constance.name)));
//            holder.match_name02_tv.setText(style + "/" + space);
            ImageLoader.getInstance().displayImage(imgPath
                    , holder.match_iv);
            final JSONArray jsonArray = jsonObject.getJSONArray(Constance.goodsInfo);

            holder.head_iv.setImageResource(R.drawable.touxian);
            if (!AppUtils.isEmpty(jsonObject.getString(Constance.avatar))) {
                final String headImgPath = NetWorkConst.SCENE_HOST + jsonObject.getString(Constance.avatar);
                ImageLoader.getInstance().displayImage(headImgPath
                        , holder.head_iv);
            }
            String author_name = "作者:" + jsonObject.getString(Constance.nickname) + "(" + jsonObject.getString(Constance.parent_name) + ")";
            holder.author_name_tv.setText(author_name);
            String time = jsonObject.getString(Constance.date);
            holder.time_tv.setText(time);

            final String fanganId = jsonObject.getString(Constance.id);

            if (mView.mProgrammeType == 0) {
                holder.close_iv.setVisibility(View.GONE);
                holder.share_iv.setVisibility(View.GONE);
            } else {
                holder.close_iv.setVisibility(View.GONE);
                holder.share_iv.setVisibility(View.GONE);
            }
            int type = jsonObject.getInt("private");
            if (type == 1 && mView.mProgrammeType == 1) {
                holder.public_state_iv.setImageResource(R.drawable.zs_icon_gk);
            } else {
                holder.public_state_iv.setImageResource(R.drawable.zs_icon_sy);
            }
            holder.public_state_iv.setVisibility(mView.mProgrammeType == 1 ? View.VISIBLE : View.GONE);

            holder.close_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDeleteIndex = position;
                    //
                    new AlertDialog.Builder(mView.getActivity())
                            .setTitle("方案操作")
                            .setItems(R.array.arrcontent,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            switch (which) {
                                                case 0://私有
                                                    setPrivateProgramme(fanganId, 0);
                                                    break;
                                                case 1://公有
                                                    setPrivateProgramme(fanganId, 1);
                                                    break;
                                                case 2://删除
                                                    sendDeleteFangan(jsonObject.getInt(Constance.id));
                                                    break;
                                                case 3://分享
                                                    String title = "来自 " + jsonObject.getString(Constance.name) + " 方案的分享";
                                                    ShareUtil.shareWx(mView.getActivity(), title, path, imgPath);
                                                    break;
                                            }
                                        }
                                    })
                            .setNegativeButton("取消",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            // TODO Auto-generated method stub
                                        }
                                    }).show();
                }
            });

            //分享
            holder.share_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertView(null, null, "取消", null,
                            new String[]{"复制链接", "分享链接"},
                            mView.getActivity(), AlertView.Style.ActionSheet, new OnItemClickListener() {
                        @Override
                        public void onItemClick(Object o, int position) {
                            switch (position) {
                                case 0:
                                    if (!mView.isToken()) {
                                        ClipboardManager cm = (ClipboardManager) mView.getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                                        cm.setText(path);
                                    }
                                    break;
                                case 1:
                                    String title = "来自 " + jsonObject.getString(Constance.name) + " 方案的分享";
                                    ShareUtil.shareWx(mView.getActivity(), title, path, imgPath);
                                    break;
                            }
                        }
                    }).show();


                }
            });

            holder.match_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mView.getActivity(), MessageDetailActivity.class);
                    String SceenId = mSchemes.getJSONObject(position).getString(Constance.id);
                    intent.putExtra(Constance.url, NetWorkConst.SHAREFANAN + SceenId);
                    intent.putExtra(Constance.FROMTYPE, 1);
                    mView.startActivity(intent);
                }
            });

            MatchItemAdapter matchItemAdapter = new MatchItemAdapter(mView.getActivity(), jsonArray);
            holder.horizon_listview.setAdapter(matchItemAdapter);
            holder.horizon_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(mView.getActivity(), ProDetailActivity.class);
                    intent.putExtra(Constance.product, jsonArray.getJSONObject(position).getInt(Constance.id));
                    mView.getActivity().startActivity(intent);
                }
            });
            String spacevalue = jsonObject.getString(Constance.space);
            String stylevalue = jsonObject.getString(Constance.style);
            holder.match_style_tv.setText(stylevalue + "/" + spacevalue);
            holder.operation_ll.setVisibility(mView.mProgrammeType != 0 ? View.VISIBLE : View.GONE);
            holder.public_ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setPrivateProgramme(fanganId, 1);
                }
            });
            holder.private_ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setPrivateProgramme(fanganId, 0);
                }
            });
            holder.share_ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String title = "来自 " + jsonObject.getString(Constance.name) + " 方案的分享";
//                    ShareUtil.showShare(mView.getActivity(), title, path, imgPath);
                    ShareUtil.shareWx(mView.getActivity(), title, path, imgPath);
                }
            });
            holder.delete_ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendDeleteFangan(jsonObject.getInt(Constance.id));
                }
            });
            return convertView;
        }

        class ViewHolder {
            ImageView close_iv, match_iv, share_iv, public_state_iv;
            HorizontalListView horizon_listview;
            TextView match_name_tv, author_name_tv, time_tv, match_style_tv;
            CircleImageView head_iv;
            LinearLayout operation_ll, public_ll, private_ll, share_ll, delete_ll;
        }
    }


    private void setPrivateProgramme(String id, int type) {
        mView.setShowDialog(true);
        mView.setShowDialog("正在设置中...");
        mView.showLoading();
        mNetWork.setPrivateProgramme(id, type, this);
    }


}
