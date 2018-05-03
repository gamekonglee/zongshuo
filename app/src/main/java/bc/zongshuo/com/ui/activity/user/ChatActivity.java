package bc.zongshuo.com.ui.activity.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;


import bc.zongshuo.com.R;
import bc.zongshuo.com.cons.Constance;
import bocang.utils.AppUtils;
import bocang.view.BaseActivity;

public class ChatActivity extends BaseActivity {
    public static ChatActivity activityInstance;
    String toChatUsername;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_chat);
    }





    @Override
    protected void InitDataView() {

    }

    @Override
    protected void initController() {

    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onViewClick(View v) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityInstance = null;
    }
    
    @Override
    protected void onNewIntent(Intent intent) {
        // 点击notification bar进入聊天页面，保证只有一个聊天页面
        String username = intent.getStringExtra("userId");
        if (toChatUsername.equals(username))
            super.onNewIntent(intent);
        else {
            finish();
            startActivity(intent);
        }

    }
    @Override
    public void onBackPressed() {
    }
    
    public String getToChatUsername(){
        return toChatUsername;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 101) {
            if(AppUtils.isEmpty(data))return;
           String value= data.getStringExtra(Constance.VALUE);
        }
    }
}
