package bc.zongshuo.com.controller.user;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.lib.common.hxp.view.PullToRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import bc.zongshuo.com.R;
import bc.zongshuo.com.bean.DistriButorBean;
import bc.zongshuo.com.cons.Constance;
import bc.zongshuo.com.cons.NetWorkConst;
import bc.zongshuo.com.controller.BaseController;
import bc.zongshuo.com.listener.INetworkCallBack;
import bc.zongshuo.com.listener.INetworkCallBack02;
import bc.zongshuo.com.ui.activity.IssueApplication;
import bc.zongshuo.com.ui.activity.user.MyDistributorActivity;
import bc.zongshuo.com.ui.view.ShowDialog;
import bc.zongshuo.com.utils.DateUtils;
import bc.zongshuo.com.utils.UIUtils;
import bocang.json.JSONArray;
import bocang.json.JSONObject;
import bocang.utils.AppUtils;
import bocang.utils.CommonUtil;
import bocang.utils.MyToast;

/**
 * @author: Jun
 * @date : 2017/9/19 9:12
 * @description :
 */
public class MyDistributorController extends BaseController implements PullToRefreshLayout.OnRefreshListener, INetworkCallBack, View.OnClickListener {
    private MyDistributorActivity mView;
    private TableLayout table_tl;
    private TableLayout table_head;
    private String[] mlistHead = {"会员名称", "级别", "注册日期", "操作"};
    private List<DistriButorBean> mDistriButorBeans = new ArrayList<>();
    private View mNullView;
    private View mNullNet;
    private Button mRefeshBtn;
    private TextView mNullNetTv;
    private TextView mNullViewTv;
//    private AlertView mLevelView;
    private String[] mLevels;
    private int mUserLevel;
    private String mUserId;
    private int currentState;

    public MyDistributorController(MyDistributorActivity v) {
        mView = v;
        initView();
        initViewData();
    }

    private void initViewData() {
        getAgentAll();
    }

    private void initView() {
        table_tl = (TableLayout) mView.findViewById(R.id.table_tl);
        table_head = (TableLayout) mView.findViewById(R.id.table_head);
        mNullView = mView.findViewById(R.id.null_view);
        mNullNet = mView.findViewById(R.id.null_net);
        mRefeshBtn = (Button) mNullNet.findViewById(R.id.refesh_btn);
        mRefeshBtn.setOnClickListener(this);
        mNullNetTv = (TextView) mNullNet.findViewById(R.id.tv);
        mNullViewTv = (TextView) mNullView.findViewById(R.id.tv);
        if(IssueApplication.mUserObject==null){
            mView.finish();
            return;
        }
        mUserLevel = IssueApplication.mUserObject.getInt(Constance.level);
        getLevel();
//        mLevelView = new AlertView(null, null, "取消", null,
//                mLevels,
//                mView, AlertView.Style.ActionSheet, this);
    }


    private void getLevel() {

        switch (mUserLevel) {
            case 0:
                mLevels = new String[]{"二级", "三级", "消费者"};
                break;
            case 1:
                mLevels = new String[]{"三级", "消费者"};
                break;
        }
    }

    private void getAgentAll() {
        mView.setShowDialog(true);
        mView.setShowDialog("载入中..");
        mView.showLoading();
        mNetWork.getAgentAll(this);
//        mNetWork.getAgentAll(new INetworkCallBack02() {
//            @Override
//            public void onSuccessListener(String requestCode, com.alibaba.fastjson.JSONObject ans) {
//                mView.hideLoading();
//                mDistriButorBeans = new ArrayList<>();
//                com.alibaba.fastjson.JSONArray jsonArray = ans.getJSONArray(Constance.data);
//                //01
//                for (int i = 0; i < jsonArray.size(); i++) {
//                    com.alibaba.fastjson.JSONObject object = jsonArray.getJSONObject(i);
//                    int id = object.getInteger(Constance.id);
//                    String nickname = object.getString(Constance.nickname);
//                    String username = object.getString(Constance.username);
//                    String mobile = object.getString(Constance.mobile);
//                    int level = object.getInteger(Constance.level);
//                    String joined_at = object.getString(Constance.joined_at);
//                    String sign = "├";
//                    if (AppUtils.isEmpty(nickname)) {
//                        nickname = username;
//                    }
//                    mDistriButorBeans.add(new DistriButorBean(id, nickname, level, joined_at, sign, mobile));
//                    //下级02
//                    com.alibaba.fastjson.JSONArray child02Array = object.getJSONArray(Constance.parent);
//                    for (int j = 0; j < child02Array.size(); j++) {
//                        com.alibaba.fastjson.JSONObject object02 = child02Array.getJSONObject(j);
//                        int id02 = object02.getInteger(Constance.id);
//                        String username02 = object02.getString(Constance.username);
//                        String nickname02 = object02.getString(Constance.nickname);
//                        String mobile02 = object.getString(Constance.mobile);
//                        int level02 = object02.getInteger(Constance.level);
//                        String joined_at02 = object02.getString(Constance.joined_at);
//                        String sign02 = "    ├";
//                        if (AppUtils.isEmpty(nickname02)) {
//                            nickname02 = username02;
//                        }
//                        mDistriButorBeans.add(new DistriButorBean(id02, nickname02, level02, joined_at02, sign02, mobile02));
//                        //                //下级03
//                        com.alibaba.fastjson.JSONArray child03Array = object02.getJSONArray(Constance.parent);
//                        for (int k = 0; k < child03Array.size(); k++) {
//                            com.alibaba.fastjson.JSONObject object03 = child03Array.getJSONObject(k);
//                            int id03 = object03.getInteger(Constance.id);
//                            String nickname03 = object03.getString(Constance.nickname);
//                            String username03 = object03.getString(Constance.username);
//                            String mobile03 = object.getString(Constance.mobile);
//                            int level03 = object03.getInteger(Constance.level);
//                            String joined_at03 = object03.getString(Constance.joined_at);
//                            String sign03 = "        ├";
//                            if (AppUtils.isEmpty(nickname03)) {
//                                nickname03 = username03;
//                            }
//                            mDistriButorBeans.add(new DistriButorBean(id03, nickname03, level03, joined_at03, sign03, mobile03));
//                        }
//                    }
//
//                }
//
//                if (mDistriButorBeans.size() == 0) {
//                    mNullView.setVisibility(View.VISIBLE);
//                    mNullNet.setVisibility(View.GONE);
//                    return;
//                }
//
//                mNullView.setVisibility(View.GONE);
//                mNullNet.setVisibility(View.GONE);
//                intTableData();
//            }
//
//            @Override
//            public void onFailureListener(String requestCode, com.alibaba.fastjson.JSONObject ans) {
//
//            }
//        });

    }

    @Override
    protected void handleMessage(int action, Object[] values) {

    }

    @Override
    protected void myHandleMessage(Message msg) {

    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        getAgentAll();
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
        getAgentAll();
    }


    @Override
    public void onSuccessListener(String requestCode, bocang.json.JSONObject ans) {
        try{
            switch (requestCode) {
                case NetWorkConst.AGENT_ALL_URL://获取我的分销商
                    mView.hideLoading();
                    mDistriButorBeans = new ArrayList<>();
                    JSONArray jsonArray = ans.getJSONArray(Constance.data);
                    //01
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        int id = object.getInt(Constance.id);
                        String nickname = object.getString(Constance.nickname);
                        String username = object.getString(Constance.username);
                        String mobile = object.getString(Constance.mobile);
                        if(TextUtils.isEmpty(mobile)){
                            mobile=username;
                        }
                        int level = object.getInt(Constance.level);
                        String joined_at = object.getString(Constance.joined_at);
                        int state=object.getInt(Constance.state);
                        String remark=object.getString(Constance.remark);
                        Log.e("remark",""+remark);
                        String sign = "├";
                        if (AppUtils.isEmpty(nickname)) {
                            nickname = username;
                        }
                        mDistriButorBeans.add(new DistriButorBean(id, nickname, level, joined_at, sign, mobile,state,remark));
                        //下级02
                        JSONArray child02Array = object.getJSONArray(Constance.parent);
                        if(child02Array!=null&&child02Array.length()>0)
                        {
                            for (int j = 0; j < child02Array.length(); j++) {
                                JSONObject object02 = child02Array.getJSONObject(j);
                                int id02 = object02.getInt(Constance.id);
                                String username02 = object02.getString(Constance.username);
                                String nickname02 = object02.getString(Constance.nickname);
                                String mobile02 = object.getString(Constance.mobile);
                                if(TextUtils.isEmpty(mobile02)){
                                    mobile02=username02;
                                }
                                int level02 = object02.getInt(Constance.level);
                                String joined_at02 = object02.getString(Constance.joined_at);
                                int state2=object02.getInt(Constance.state);
                                String remark2=object02.getString(Constance.remark);
                                Log.e("remark2",""+remark2);
                                String sign02 = "    ├";
                                if (AppUtils.isEmpty(nickname02)) {
                                    nickname02 = username02;
                                }
                                mDistriButorBeans.add(new DistriButorBean(id02, nickname02, level02, joined_at02, sign02, mobile02,state2,remark2));
                                //                //下级03
                                JSONArray child03Array = object02.getJSONArray(Constance.parent);
                                for (int k = 0; k < child03Array.length(); k++) {
                                    JSONObject object03 = child03Array.getJSONObject(k);
                                    int id03 = object03.getInt(Constance.id);
                                    String nickname03 = object03.getString(Constance.nickname);
                                    String username03 = object03.getString(Constance.username);
                                    String mobile03 = object.getString(Constance.mobile);
                                    if(TextUtils.isEmpty(mobile03)){
                                        mobile03=username03;
                                    }
                                    int level03 = object03.getInt(Constance.level);
                                    String joined_at03 = object03.getString(Constance.joined_at);
                                    int state03=object03.getInt(Constance.state);
                                    String remark03=object03.getString(Constance.remark);
                                    Log.e("remark3",""+remark03);
                                    String sign03 = "        ├";
                                    if (AppUtils.isEmpty(nickname03)) {
                                        nickname03 = username03;
                                    }
                                    mDistriButorBeans.add(new DistriButorBean(id03, nickname03, level03, joined_at03, sign03, mobile03,state03,remark03));
                                }
                            }
                        }
                    }

                    if (mDistriButorBeans.size() == 0) {
                        mNullView.setVisibility(View.VISIBLE);
                        mNullNet.setVisibility(View.GONE);
                        return;
                    }

                    mNullView.setVisibility(View.GONE);
                    mNullNet.setVisibility(View.GONE);
                    intTableData();
                    break;
                case NetWorkConst.LEVEL_EDIT_URL://修改级别
                    getAgentAll();
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setPhone(final String phoneNumber) {
        ActivityCompat.requestPermissions(mView,
                new String[]{"android.permission.CALL_PHONE"},
                1);
        ShowDialog mDialog = new ShowDialog();
        mDialog.show(mView, "提示", "是否打电话给" + phoneNumber + "?", new ShowDialog.OnBottomClickListener() {
            @Override
            public void positive() {
                PackageManager packageManager = mView.getPackageManager();
                int permission = packageManager.checkPermission("android.permission.CALL_PHONE", "bc.zongshuo.com");
                if (PackageManager.PERMISSION_GRANTED != permission) {
                    return;
                } else {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + phoneNumber));
                    mView.startActivity(intent);
                }
            }

            @Override
            public void negtive() {

            }
        });

    }


    @Override
    public void onFailureListener(String requestCode, bocang.json.JSONObject ans) {
        mView.hideLoading();
        mNullNet.setVisibility(View.VISIBLE);
        getOutLogin(mView, ans);
    }

    private void setTableHead() {

        table_head.setStretchAllColumns(true);

        TableRow tableRow = new TableRow(mView);

        for (int i = 0; i < mlistHead.length; i++) {
            TextView tv = new TextView(mView);
            tv.setText(mlistHead[i]);
            tv.setBackgroundResource(R.drawable.table_row);
            tv.setTextColor(mView.getResources().getColor(R.color.txt_black));
            tv.setTextSize(17);
            tv.setGravity(Gravity.CENTER);
            tv.setSingleLine();
            tv.setHeight(80);
            tv.getPaint().setFakeBoldText(true);
            tableRow.addView(tv);
        }

        table_tl.addView(tableRow, new TableLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.FILL_PARENT));

    }

    private void intTableData() {
        table_tl.removeAllViews();
        setTableHead();
        table_tl.setStretchAllColumns(true);
        foreachTableData();
    }

    private void foreachTableData() {
        for (int i = 0; i < mDistriButorBeans.size(); i++) {
            TableRow tableRow = new TableRow(mView);
            final String userName = mDistriButorBeans.get(i).getUsername();
            final String tel = mDistriButorBeans.get(i).getTel();
            final String uid = mDistriButorBeans.get(i).getId() + "";
            for (int j = 0; j < mlistHead.length; j++) {
                TextView tv = new TextView(mView);
                tv.setBackgroundResource(R.drawable.table_row);
                tv.setTextColor(mView.getResources().getColor(R.color.txt_black));
                tv.setTextSize(12);
                TableRow.LayoutParams layoutParams=new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 200);
//                layoutParams.setMargins(10,0,10,0);
                tv.setLayoutParams(layoutParams);
                tv.setGravity(Gravity.CENTER);
                tv.setHeight(200);
                switch (j) {
                    case 0:
                        tv.setGravity(Gravity.CENTER|Gravity.LEFT);
                        tv.setText(Html.fromHtml(mDistriButorBeans.get(i).getSign() + userName+"<br><font color='#888888'>备注："+mDistriButorBeans.get(i).getRemark()+"</font>"));
                        tv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!AppUtils.isEmpty(tel) && CommonUtil.isMobileNO(tel)) {
                                    setPhone(tel);
                                } else {
                                    MyToast.show(mView, "该用户没有电话");
                                }
                            }
                        });
                        break;
                    case 1:
                        tv.setText(getLevel(mDistriButorBeans.get(i).getLevel())+"\n");
                        break;
                    case 2:
                        String joinedAt = mDistriButorBeans.get(i).getJoined_at();
                        tv.setText(DateUtils.getStrTime02000(joinedAt)+"\n");
                        break;
                    case 3:
                        if (mUserLevel < 1) {
                            final String remark=mDistriButorBeans.get(i).getRemark();
                            final int state=mDistriButorBeans.get(i).getState();
//                            Log.e("state",state+"");
                            String joinedAt1 = "修改";
                            tv.setTextColor(mView.getResources().getColor(R.color.green));
                            tv.setText(joinedAt1+"\n");
                            tv.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mUserId = uid;
//                                    mLevelView.show();

                                    final Dialog dialog=UIUtils.showBottomInDialog(mView,R.layout.dialog_distr,UIUtils.dip2PX(300));
                                    final TextView tv_open=dialog.findViewById(R.id.tv_turnon);
                                    final TextView tv_close=dialog.findViewById(R.id.tv_close);
                                    final EditText et_remark=dialog.findViewById(R.id.et_remark);
                                    Button btn_save=dialog.findViewById(R.id.btn_save);
                                    currentState = state;
                                    if(state==1){
                                        tv_open.setBackgroundResource(R.drawable.bg_corner_theme_empty_10);
                                        tv_open.setTextColor(mView.getResources().getColor(R.color.green));
                                        tv_close.setBackgroundResource(R.drawable.bg_corner_grey_empty_10);
                                        tv_close.setTextColor(mView.getResources().getColor(R.color.grey_text_color_normal));
                                    }else {
                                        tv_close.setBackgroundResource(R.drawable.bg_corner_theme_empty_10);
                                        tv_close.setTextColor(mView.getResources().getColor(R.color.green));
                                        tv_open.setBackgroundResource(R.drawable.bg_corner_grey_empty_10);
                                        tv_open.setTextColor(mView.getResources().getColor(R.color.grey_text_color_normal));
                                    }
                                    tv_open.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if(currentState ==1){
                                                return;
                                            }
                                            currentState =1;
                                            tv_open.setBackgroundResource(R.drawable.bg_corner_theme_empty_10);
                                            tv_open.setTextColor(mView.getResources().getColor(R.color.green));
                                            tv_close.setBackgroundResource(R.drawable.bg_corner_grey_empty_10);
                                            tv_close.setTextColor(mView.getResources().getColor(R.color.grey_text_color_normal));
                                        }
                                    });
                                    tv_close.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if(currentState==0){
                                                return;
                                            }
                                            currentState=0;
                                            tv_close.setBackgroundResource(R.drawable.bg_corner_theme_empty_10);
                                            tv_close.setTextColor(mView.getResources().getColor(R.color.green));
                                            tv_open.setBackgroundResource(R.drawable.bg_corner_grey_empty_10);
                                            tv_open.setTextColor(mView.getResources().getColor(R.color.grey_text_color_normal));
                                        }
                                    });
                                    Log.e("remark",""+remark);
                                    et_remark.setText(""+remark);
                                    btn_save.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if(et_remark.getText()==null){
                                                et_remark.setText("");
                                            }
                                            mNetWork.sendInfoEdit(mUserId,currentState,et_remark.getText().toString(), new INetworkCallBack() {
                                                @Override
                                                public void onSuccessListener(String requestCode, JSONObject ans) {
                                                    Log.e("edit",ans.toString());
                                                    MyToast.show(mView,"修改成功！");
                                                    getAgentAll();
                                                    dialog.dismiss();
                                                }

                                                @Override
                                                public void onFailureListener(String requestCode, JSONObject ans) {
                                                    Log.e("edit_failed",ans.toString());
                                                    MyToast.show(mView,"修改失败！");
                                                    dialog.dismiss();
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        }
                        //                        if ("├".equals(mDistriButorBeans.get(i).getSign()) && mUserLevel < 2) {
                        //                            String joinedAt1 = "修改";
                        //                            tv.setTextColor(mView.getResources().getColor(R.color.green));
                        //                            tv.setText(joinedAt1);
                        //                            tv.setOnClickListener(new View.OnClickListener() {
                        //                                @Override
                        //                                public void onClick(View v) {
                        //                                    mUserId = uid;
                        //                                    mLevelView.show();
                        //                                }
                        //                            });
                        //                        }

                        break;
                }

                tableRow.addView(tv);

            }
            table_tl.addView(tableRow, new TableLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,
                    ActionBar.LayoutParams.FILL_PARENT));


        }
    }


    private String getLevel(int level) {
        String levelValue = "";
        if (level == 100) {
            levelValue = "平台客户";
        } else if (level == 101) {
            levelValue = "代理商";
        } else if (level == 102) {
            levelValue = "加盟商";
        } else if(level==103){
            levelValue = "经销商";
        }else {
            levelValue = "消费者";
        }
        return levelValue;
    }

    @Override
    public void onClick(View v) {
        getAgentAll();
    }
//
//    @Override
//    public void onItemClick(Object o, int position) {
//        if (position == -1)
//            return;
//        String levelValue = mLevels[position];
//        int level = -1;
//        switch (levelValue) {
//            case "二级":
//                level = 1;
//                break;
//            case "三级":
//                level = 2;
//                break;
//            case "消费者":
//                level = 3;
//                break;
//        }
//        mView.setShowDialog(true);
//        mView.setShowDialog("正在设置中..");
//        mView.showLoading();
//        mNetWork.editLevel(level, mUserId, this);
//
//    }
}
