package bc.zongshuo.com.ui.view.popwindow;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import bc.zongshuo.com.R;
import bc.zongshuo.com.ui.activity.user.LoginActivity;
import bc.zongshuo.com.ui.activity.user.Regiest01Activity;
import bocang.utils.IntentUtil;


/**
 * @author: Jun
 * @date : 2017/2/16 15:12
 * @description :
 */
public class OutLoginPopWindow extends BasePopwindown implements View.OnClickListener {
    private LinearLayout close_ll;
    private Button regiest_bt,login_bt;
    public FragmentActivity mActivity;

    public OutLoginPopWindow(Context context) {
        super(context);
    }

    @Override
    protected void initView(Context context) {
        View contentView = View.inflate(mContext, R.layout.pop_out_login, null);
        initUI(contentView);

    }


    private void initUI(View contentView) {
        close_ll = (LinearLayout) contentView.findViewById(R.id.close_ll);
        close_ll.setOnClickListener(this);
        regiest_bt = (Button) contentView.findViewById(R.id.regiest_bt);
        regiest_bt.setOnClickListener(this);
        login_bt = (Button) contentView.findViewById(R.id.login_bt);
        login_bt.setOnClickListener(this);

        mPopupWindow = new PopupWindow(contentView, -1, -1);
        // 1.让mPopupWindow内部的控件获取焦点
        mPopupWindow.setFocusable(true);
        // 2.mPopupWindow内部获取焦点后 外部的所有控件就失去了焦点
        mPopupWindow.setOutsideTouchable(true);
        //只有加载背景图还有效果
        // 3.如果不马上显示PopupWindow 一般建议刷新界面
        mPopupWindow.update();
        // 设置弹出窗体显示时的动画，从底部向上弹出
        mPopupWindow.setAnimationStyle(R.style.AnimBottom);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.close_ll:
                onDismiss();
            break;
            case R.id.regiest_bt:
                IntentUtil.startActivity(mActivity, Regiest01Activity.class, true);
                break;
            case R.id.login_bt:
                IntentUtil.startActivity(mActivity, LoginActivity.class, true);
                break;
        }
    }
}
