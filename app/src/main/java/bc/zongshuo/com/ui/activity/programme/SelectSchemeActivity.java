package bc.zongshuo.com.ui.activity.programme;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import bc.zongshuo.com.R;
import bc.zongshuo.com.controller.programme.SelectSchemeController;
import bocang.view.BaseActivity;

/**
 * @author: Jun
 * @date : 2017/3/14 11:01
 * @description :选择方案类型
 */
public class SelectSchemeActivity extends BaseActivity {
    private SelectSchemeController mController;
    private TextView iv_save;

    @Override
    protected void InitDataView() {

    }

    @Override
    protected void initController() {
        mController=new SelectSchemeController(this);
    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_scheme_type);
        iv_save=getViewAndClick(R.id.iv_save);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onViewClick(View v) {
        switch (v.getId()){
            case R.id.iv_save:
            mController.saveScheme();
            break;
        }
    }
}
