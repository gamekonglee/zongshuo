package bc.zongshuo.com.controller.user;

import android.os.Message;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;

import bc.zongshuo.com.R;
import bc.zongshuo.com.cons.Constance;
import bc.zongshuo.com.cons.NetWorkConst;
import bc.zongshuo.com.controller.BaseController;
import bc.zongshuo.com.listener.INetworkCallBack02;
import bc.zongshuo.com.ui.activity.user.UserDataActivity;
import bc.zongshuo.com.utils.ImageLoadProxy;
import bc.zongshuo.com.utils.UIUtils;
import bocang.utils.AppDialog;
import bocang.utils.AppUtils;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @author: Jun
 * @date : 2017/7/5 14:10
 * @description :
 */
public class UserDataController extends BaseController {
    private UserDataActivity mView;
    private CircleImageView head_iv;
    private TextView name_tv,sex_tv,birthday_tv;
    private int sexType = 0;

    public UserDataController(UserDataActivity v){
        mView=v;
        initView();
        initViewData();
    }

    private void initViewData() {
        mView.setShowDialog(true);
        mView.setShowDialog("正在获取数据..");
        mView.showLoading();
        getUser();
    }

    private void initView() {
        head_iv = (CircleImageView) mView.findViewById(R.id.head_iv);
        name_tv = (TextView) mView.findViewById(R.id.name_tv);
        sex_tv = (TextView) mView.findViewById(R.id.sex_tv);
        birthday_tv = (TextView) mView.findViewById(R.id.birthday_tv);
    }


    private void getUser(){
        mNetWork.sendUserInfo(mView.mUserId, new INetworkCallBack02() {
            @Override
            public void onSuccessListener(String requestCode, JSONObject ans) {
                try{
                    mView.hideLoading();
                    JSONObject mUserObject = ans.getJSONObject(Constance.user);
                    if(AppUtils.isEmpty(mUserObject)) return;
                    if(!AppUtils.isEmpty(mUserObject.getString(Constance.avatar))){
                        String avatar = NetWorkConst.SCENE_HOST + mUserObject.getString(Constance.avatar);
                        if (!AppUtils.isEmpty(avatar))
                            ImageLoadProxy.displayHeadIcon(avatar, head_iv);
                    }

                    String username = mUserObject.getString(Constance.username);
                    name_tv.setText(username);
                    int sex = mUserObject.getInteger(Constance.gender);
                    sexType = sex;
                    sex_tv.setText(sex == 0 ? "男" : "女");
                    birthday_tv.setText(mUserObject.getString(Constance.birthday));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailureListener(String requestCode, JSONObject ans) {
                mView.hideLoading();
                if (AppUtils.isEmpty(ans)) {
                    AppDialog.messageBox(UIUtils.getString(R.string.server_error));
                    return;
                }
                AppDialog.messageBox(ans.getString(Constance.error_desc));
                getOutLogin02(mView, ans);
            }
        });

    }

    @Override
    protected void handleMessage(int action, Object[] values) {

    }

    @Override
    protected void myHandleMessage(Message msg) {

    }
}
