package bc.zongshuo.com.controller.user;

import android.app.Dialog;
import android.content.Intent;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import bc.zongshuo.com.R;
import bc.zongshuo.com.cons.Constance;
import bc.zongshuo.com.cons.NetWorkConst;
import bc.zongshuo.com.controller.BaseController;
import bc.zongshuo.com.listener.INetworkCallBack;
import bc.zongshuo.com.ui.activity.IssueApplication;
import bc.zongshuo.com.ui.activity.SettingActivity;
import bc.zongshuo.com.ui.activity.user.CollectActivity;
import bc.zongshuo.com.ui.activity.user.InvitationCodeActivity;
import bc.zongshuo.com.ui.activity.user.LoginActivity;
import bc.zongshuo.com.ui.activity.user.MerchantInfoActivity;
import bc.zongshuo.com.ui.activity.user.MessageActivity;
import bc.zongshuo.com.ui.activity.user.MyDistributorActivity;
import bc.zongshuo.com.ui.activity.user.MyOrderActivity;
import bc.zongshuo.com.ui.activity.user.PerfectMydataActivity;
import bc.zongshuo.com.ui.activity.user.UserAddrActivity;
import bc.zongshuo.com.ui.activity.user.UserLogActivity;
import bc.zongshuo.com.ui.fragment.MineFragment;
import bc.zongshuo.com.ui.view.dialog.ShowSureDialog;
import bc.zongshuo.com.utils.ImageLoadProxy;
import bc.zongshuo.com.utils.ShareUtil;
import bc.zongshuo.com.utils.UIUtils;
import bocang.json.JSONArray;
import bocang.json.JSONObject;
import bocang.utils.AppDialog;
import bocang.utils.AppUtils;
import bocang.utils.IntentUtil;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @author: Jun
 * @date : 2017/1/21 15:08
 * @description :
 */
public class MineController extends BaseController implements INetworkCallBack {
    private MineFragment mView;
    private CircleImageView head_cv;
    private TextView nickname_tv;
    public JSONObject mUserObject;
    public Intent mIntent;
    private ScrollView main_sv;
    private TextView unMessageReadTv, unMessageRead02Tv, unMessageRead03Tv, level_tv;

    public MineController(MineFragment v) {
        mView = v;
        initView();
        initViewData();
    }

    private void initViewData() {
    }

    private void initView() {
        head_cv = (CircleImageView) mView.getView().findViewById(R.id.head_user_cv);
        nickname_tv = (TextView) mView.getView().findViewById(R.id.nickname_tv);
        unMessageReadTv = (TextView) mView.getView().findViewById(R.id.unMessageReadTv);
        unMessageRead02Tv = (TextView) mView.getView().findViewById(R.id.unMessageRead02Tv);
        unMessageRead03Tv = (TextView) mView.getView().findViewById(R.id.unMessageRead03Tv);
        level_tv = (TextView) mView.getView().findViewById(R.id.level_tv);
        main_sv = (ScrollView) mView.getView().findViewById(R.id.main_sv);
    }

    @Override
    protected void handleMessage(int action, Object[] values) {

    }

    @Override
    protected void myHandleMessage(Message msg) {

    }

    /**
     * 头像
     */
    public void setHead() {
        IntentUtil.startActivity(mView.getActivity(), PerfectMydataActivity.class, false);
    }

    /**
     * 设置
     */
    public void setSetting() {
        IntentUtil.startActivity(mView.getActivity(), SettingActivity.class, false);
    }

    /**
     * 我的订单
     */
    public void setOrder() {
        IntentUtil.startActivity(mView.getActivity(), MyOrderActivity.class, false);
    }

    /**
     * 我的收藏
     */
    public void setCollect() {
        IntentUtil.startActivity(mView.getActivity(), CollectActivity.class, false);

    }

    /**
     * 管理收货地址
     */
    public void setAddress() {
        IntentUtil.startActivity(mView.getActivity(), UserAddrActivity.class, false);
    }


    /**
     * 代收货
     */
    public void setReceiving() {
        mIntent = new Intent(mView.getActivity(), MyOrderActivity.class);
        mIntent.putExtra(Constance.order_type, 3);
        mView.startActivity(mIntent);
    }

    /**
     * 管理物流地址
     */
    public void setStream() {
        IntentUtil.startActivity(mView.getActivity(), UserLogActivity.class, false);
    }

    /**
     * 消息中心
     */
    public void SetMessage() {
        IntentUtil.startActivity(mView.getActivity(), MessageActivity.class, false);
    }

    /**
     * 待付款
     */
    public void setPayMen() {
        mIntent = new Intent(mView.getActivity(), MyOrderActivity.class);
        mIntent.putExtra(Constance.order_type, 1);
        mView.startActivity(mIntent);
    }

    /**
     * 待发货
     */
    public void setDelivery() {
        mIntent = new Intent(mView.getActivity(), MyOrderActivity.class);
        mIntent.putExtra(Constance.order_type, 2);
        mView.startActivity(mIntent);
    }

    /**
     * 获取用户信息
     */
    public void sendUser() {
        mNetWork.sendUser(this);
    }

//    /**
//     * 分享给好友
//     */
//    public void getShareApp() {
//        String title = "来自 " + UIUtils.getString(R.string.app_name) + " App的分享";
//        ShareUtil.showShare(mView.getActivity(), title, NetWorkConst.APK_URL, NetWorkConst.SHAREIMAGE);
//
//    }
    /**
     * 分享给好友
     */
    public void getShareApp() {
        final String title = "来自 " + UIUtils.getString(R.string.app_name) + " App的分享";
        final Dialog dialog=UIUtils.showBottomInDialog(mView.getActivity(),R.layout.share_dialog,UIUtils.dip2PX(150));
        TextView tv_cancel=dialog.findViewById(R.id.tv_cancel);
        LinearLayout ll_wx=dialog.findViewById(R.id.ll_wx);
        LinearLayout ll_pyq=dialog.findViewById(R.id.ll_pyq);
        LinearLayout ll_qq=dialog.findViewById(R.id.ll_qq);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        ll_wx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareUtil.shareWx(mView.getActivity(), title, NetWorkConst.APK_URL, NetWorkConst.SHAREIMAGE);
                dialog.dismiss();
            }
        });
        ll_pyq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareUtil.sharePyq(mView.getActivity(), title, NetWorkConst.APK_URL, NetWorkConst.SHAREIMAGE);
                dialog.dismiss();
            }
        });
        ll_qq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareUtil.shareQQApp(mView.getActivity(), title, NetWorkConst.APK_URL, NetWorkConst.SHAREIMAGE);
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onSuccessListener(String requestCode, JSONObject ans) {
        try{
            switch (requestCode) {
                case NetWorkConst.PROFILE:
                    mUserObject = ans.getJSONObject(Constance.user);
                    IssueApplication.mUserObject = mUserObject;
                    if (AppUtils.isEmpty(mUserObject))
                        return;
                    String avatar = NetWorkConst.SCENE_HOST + mUserObject.getString(Constance.avatar);
                    if (!AppUtils.isEmpty(avatar))
                        ImageLoadProxy.displayHeadIcon(avatar, head_cv);

                    String username = IssueApplication.mUserObject.getString(Constance.username);
                    String nickName = IssueApplication.mUserObject.getString(Constance.nickname);
                    int  level = IssueApplication.mUserObject.getInt(Constance.level_id);
                    String levelValue = "";
                    mView.user_money_ll.setVisibility(View.VISIBLE);
                    mView.two_code_ll.setVisibility(View.VISIBLE);
                    mView.distributor_ll.setVisibility(View.VISIBLE);
                    if (level == 100) {
                        levelValue = "平台客户";
                    } else if (level == 101) {
                        levelValue = "代理商";
                    } else if (level == 102) {
                        levelValue = "加盟商";
                    }else if (level == 103) {
                        levelValue = "经销商";
                    } else if(level==104){
                        mView.user_money_ll.setVisibility(View.INVISIBLE);
                        mView.two_code_ll.setVisibility(View.INVISIBLE);
                        mView.distributor_ll.setVisibility(View.INVISIBLE);
                        levelValue = "消费者";
                    }
                    level_tv.setText(levelValue);
                    Log.v("520it", IssueApplication.mUserObject.getString(Constance.money));
                    JSONArray countArray = ans.getJSONArray("count");
                    String count01 = countArray.get(0).toString();
                    String count02 = countArray.get(1).toString();
                    String count03 = countArray.get(2).toString();
                    unMessageReadTv.setText(countArray.get(0).toString());
                    unMessageRead02Tv.setText(countArray.get(1).toString());
                    unMessageRead03Tv.setText(countArray.get(2).toString());
                    unMessageReadTv.setVisibility(Integer.parseInt(count01) > 0 ? View.VISIBLE : View.GONE);
                    unMessageRead02Tv.setVisibility(Integer.parseInt(count02) > 0 ? View.VISIBLE : View.GONE);
                    unMessageRead03Tv.setVisibility(Integer.parseInt(count03) > 0 ? View.VISIBLE : View.GONE);
                    if (AppUtils.isEmpty(nickName)) {
                        nickname_tv.setText(username);
                        IntentUtil.startActivity(mView.getActivity(), PerfectMydataActivity.class, false);
                        return;
                    } else {
                        nickname_tv.setText(nickName);
                    }

                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onFailureListener(String requestCode, JSONObject ans) {
        try {
            if (AppUtils.isEmpty(ans)) {
                AppDialog.messageBox(UIUtils.getString(R.string.server_error));
                return;
            }
            getOutLogin(mView.getActivity(), ans);
        } catch (Exception e) {

        }
    }

    /**
     * 我的邀请码
     */
    public void getInvitationCode() {
        IntentUtil.startActivity(mView.getActivity(), InvitationCodeActivity.class, false);
    }

    /**
     * 我的分销商
     */
    public void getMyistributor() {
        IntentUtil.startActivity(mView.getActivity(), MyDistributorActivity.class, false);
    }

    /**
     * 服务
     */
    public void getService() {
        IntentUtil.startActivity(mView.getActivity(), MerchantInfoActivity.class, false);
    }
}
