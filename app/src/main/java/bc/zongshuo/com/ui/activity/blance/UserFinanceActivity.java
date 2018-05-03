package bc.zongshuo.com.ui.activity.blance;

import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import bc.zongshuo.com.R;
import bc.zongshuo.com.controller.blance.UserFinanceController;
import bocang.utils.IntentUtil;
import bocang.view.BaseActivity;

/**
 * @author: Jun
 * @date : 2017/7/6 15:15
 * @description :
 */
public class UserFinanceActivity extends BaseActivity {
    private Button user_fnc_btnWithdraw;
    private TextView topRighttv;
    private UserFinanceController mController;
    private RelativeLayout profit_record_rl;
    private RelativeLayout tixian_rl;
    private View sale_record_rl;

    @Override
    protected void InitDataView() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        mController.initViewData();
    }

    @Override
    protected void initController() {
        mController=new UserFinanceController(this);
    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_user_finance_new);
        //沉浸式状态栏
//        setColor(this, getResources().getColor(R.color.green));
        user_fnc_btnWithdraw = getViewAndClick(R.id.user_fnc_btnWithdraw);
        topRighttv = getViewAndClick(R.id.topRighttv);
        profit_record_rl = getViewAndClick(R.id.profit_record_rl);
        tixian_rl = getViewAndClick(R.id.tixian_rl);
        sale_record_rl = getViewAndClick(R.id.sale_record_rl);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onViewClick(View v) {
        switch (v.getId()) {
            case R.id.user_fnc_btnWithdraw:
                IntentUtil.startActivity(this, ExtractMoneyActivity.class, false);
                break;
            case R.id.tixian_rl:
            case R.id.topRighttv:
                IntentUtil.startActivity(this, ExtractDetailActivity.class, false);
                break;
            case R.id.profit_record_rl:
                IntentUtil.startActivity(this, ProfitRecordActivity.class, false);
                break;
            case R.id.sale_record_rl:
                IntentUtil.startActivity(this,SaleRecordActivity.class,false);
                break;
        }
    }
}
