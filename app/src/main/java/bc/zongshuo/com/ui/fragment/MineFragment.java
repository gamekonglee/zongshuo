package bc.zongshuo.com.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.sevenonechat.sdk.compts.InfoSubmitActivity;
import com.sevenonechat.sdk.sdkinfo.SdkRunningClient;

import bc.zongshuo.com.R;
import bc.zongshuo.com.common.BaseFragment;
import bc.zongshuo.com.cons.Constance;
import bc.zongshuo.com.controller.user.MineController;
import bc.zongshuo.com.ui.activity.IssueApplication;
import bc.zongshuo.com.ui.activity.MainActivity;
import bc.zongshuo.com.ui.activity.blance.UserFinanceActivity;
import bc.zongshuo.com.ui.activity.buy.ShoppingCartActivity;
import bc.zongshuo.com.ui.activity.user.SimpleScannerActivity;
import bc.zongshuo.com.ui.activity.user.SimpleScannerLoginActivity;
import bocang.utils.AppUtils;
import bocang.utils.IntentUtil;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @author Jun
 * @time 2017/1/5  12:00
 * @desc 我的页面
 */
public class MineFragment extends BaseFragment implements View.OnClickListener {
    private CircleImageView head_cv;
    private RelativeLayout order_rl, all_rl;
    public LinearLayout collect_ll, address_ll, stream_ll, message_ll, share_ll, user_money_ll, call_kefu_ll, cart_ll, two_code_ll, setting_ll, distributor_ll, service_ll, call_kefu02_ll, setting02_ll, mine03_lv, mine04_lv;
    private MineController mController;
    private RelativeLayout payment_rl, delivery_rl, Receiving_rl;
    private ImageView iv_sao;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fm_mine, null);
    }

    @Override
    protected void initController() {
        mController = new MineController(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        if(!isToken()){
            mController.sendUser();
        }else {
            ((MainActivity)getActivity()).selectItem(R.id.frag_top_ll);
        }
    }

    @Override
    protected void initViewData() {

        if (AppUtils.isEmpty(IssueApplication.mUserObject))
            return;
        int level = IssueApplication.mUserObject.getInt(Constance.level);
        if (level > 2) {
            mine03_lv.setVisibility(View.GONE);
            mine04_lv.setVisibility(View.VISIBLE);
        } else {
            mine03_lv.setVisibility(View.VISIBLE);
            mine04_lv.setVisibility(View.GONE);
        }

    }

    @Override
    protected void initView() {
        head_cv = (CircleImageView) getActivity().findViewById(R.id.head_user_cv);
        setting_ll = (LinearLayout) getActivity().findViewById(R.id.setting_ll);
        setting02_ll = (LinearLayout) getActivity().findViewById(R.id.setting02_ll);
        order_rl = (RelativeLayout) getActivity().findViewById(R.id.order_rl);
        collect_ll = (LinearLayout) getActivity().findViewById(R.id.collect_ll);
        address_ll = (LinearLayout) getActivity().findViewById(R.id.address_ll);
        stream_ll = (LinearLayout) getActivity().findViewById(R.id.stream_ll);
        message_ll = (LinearLayout) getActivity().findViewById(R.id.message_ll);
        share_ll = (LinearLayout) getActivity().findViewById(R.id.share_ll);
        user_money_ll = (LinearLayout) getActivity().findViewById(R.id.user_money_ll);
        payment_rl = (RelativeLayout) getActivity().findViewById(R.id.payment_rl);
        all_rl = (RelativeLayout) getActivity().findViewById(R.id.all_rl);
        delivery_rl = (RelativeLayout) getActivity().findViewById(R.id.delivery_rl);
        Receiving_rl = (RelativeLayout) getActivity().findViewById(R.id.Receiving_rl);
        call_kefu_ll = (LinearLayout) getActivity().findViewById(R.id.call_kefu_ll);
        call_kefu02_ll = (LinearLayout) getActivity().findViewById(R.id.call_kefu02_ll);
        two_code_ll = (LinearLayout) getActivity().findViewById(R.id.two_code_ll);
        distributor_ll = (LinearLayout) getActivity().findViewById(R.id.distributor_ll);
        service_ll = (LinearLayout) getActivity().findViewById(R.id.service_ll);
        cart_ll = (LinearLayout) getActivity().findViewById(R.id.cart_ll);
        mine03_lv = (LinearLayout) getActivity().findViewById(R.id.mine03_lv);
        mine04_lv = (LinearLayout) getActivity().findViewById(R.id.mine04_lv);
        iv_sao = getActivity().findViewById(R.id.sao_iv);
        head_cv.setOnClickListener(this);
        setting_ll.setOnClickListener(this);
        setting02_ll.setOnClickListener(this);
        order_rl.setOnClickListener(this);
        collect_ll.setOnClickListener(this);
        address_ll.setOnClickListener(this);
        stream_ll.setOnClickListener(this);
        message_ll.setOnClickListener(this);
        payment_rl.setOnClickListener(this);
        delivery_rl.setOnClickListener(this);
        Receiving_rl.setOnClickListener(this);
        share_ll.setOnClickListener(this);
        user_money_ll.setOnClickListener(this);
        call_kefu_ll.setOnClickListener(this);
        call_kefu02_ll.setOnClickListener(this);
        cart_ll.setOnClickListener(this);
        two_code_ll.setOnClickListener(this);
        distributor_ll.setOnClickListener(this);
        service_ll.setOnClickListener(this);
        all_rl.setOnClickListener(this);
        iv_sao.setOnClickListener(this);

    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_user_cv://头像
                mController.setHead();
                break;
            case R.id.setting_ll://设置
                mController.setSetting();
                break;
            case R.id.setting02_ll://设置
                mController.setSetting();
                break;
            case R.id.two_code_ll://邀请码
                mController.getInvitationCode();
                break;
            case R.id.distributor_ll://我的分销商
                mController.getMyistributor();
                break;
            case R.id.service_ll://服务
                mController.getService();
                break;
            case R.id.all_rl://我的订单
                mController.setOrder();
                break;
            case R.id.collect_ll://我的收藏
                mController.setCollect();
                break;
            case R.id.address_ll://管理收货地址
                mController.setAddress();
                break;
            case R.id.stream_ll://管理物流地址
                mController.setStream();
                break;
            case R.id.message_ll://消息中心
                mController.SetMessage();
                break;
            case R.id.payment_rl://待付款
                mController.setPayMen();
                break;
            case R.id.delivery_rl://待发货
                mController.setDelivery();
                break;
            case R.id.Receiving_rl://待收货
                mController.setReceiving();
                break;
            case R.id.share_ll://分享给好友
                mController.getShareApp();
                break;
            case R.id.user_money_ll://余额
                IntentUtil.startActivity(getActivity(), UserFinanceActivity.class, false);
                break;
            case R.id.call_kefu_ll://客服中心
                if(IssueApplication.mUserObject==null){
                return;
                }
                String id = IssueApplication.mUserObject.getString(Constance.id);
                SdkRunningClient.getInstance().setUserUid(id);
                Intent it = new Intent(v.getContext(), InfoSubmitActivity.class);
                v.getContext().startActivity(it);
                break;
            case R.id.call_kefu02_ll://客服中心
                SdkRunningClient.getInstance().upDataUserInfo(null,"1","1","1","222222");
                Intent it1 = new Intent(v.getContext(), InfoSubmitActivity.class);
                v.getContext().startActivity(it1);
                break;
            case R.id.cart_ll://购物车
                IntentUtil.startActivity(getActivity(), ShoppingCartActivity.class, false);
                break;
            case R.id.sao_iv:
                if(isToken()){
                }else {
                IntentUtil.startActivity(this.getActivity(), SimpleScannerLoginActivity.class, false);
                }
                break;
        }
    }
}
