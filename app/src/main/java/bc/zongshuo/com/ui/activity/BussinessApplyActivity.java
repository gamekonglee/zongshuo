package bc.zongshuo.com.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import bc.zongshuo.com.R;
import bc.zongshuo.com.common.BaseActivity;
import bc.zongshuo.com.controller.BussinessApplyController;

/**
 * Created by gamekonglee on 2018/5/10.
 */

public class BussinessApplyActivity extends BaseActivity {

    private BussinessApplyController mController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    protected void InitDataView() {

    }

    @Override
    protected void initController() {
        mController = new BussinessApplyController(this);

    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_bussiness_apply);
        setColor(this, Color.WHITE);
        View rl_back=findViewById(R.id.rl_back);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BussinessApplyActivity.this, MainActivity.class));
                finish();
            }
        });

    }

    @Override
    protected void initData() {

    }
}
