package bc.zongshuo.com.controller.product;

import android.os.Message;
import android.widget.EditText;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;

import bc.zongshuo.com.R;
import bc.zongshuo.com.cons.Constance;
import bc.zongshuo.com.cons.NetWorkConst;
import bc.zongshuo.com.controller.BaseController;
import bc.zongshuo.com.ui.activity.product.PostedImageActivity;
import bc.zongshuo.com.utils.NetWorkUtils;
import bocang.utils.AppUtils;
import bocang.utils.MyToast;

/**
 * @author Jun
 * @time 2017/12/10  13:31
 * @desc ${TODD}
 */

public class PostedImageController extends BaseController {
    private PostedImageActivity mView;
    private EditText value_et;


    public PostedImageController(PostedImageActivity v) {
        mView = v;
        initView();
        initViewData();
    }

    private void initViewData() {

    }

    private void initView() {
        value_et = (EditText) mView.findViewById(R.id.value_et);
    }


    @Override
    protected void handleMessage(int action, Object[] values) {

    }

    @Override
    protected void myHandleMessage(Message msg) {

    }

    public void reviceOrder() {
        mView.setShowDialog(true);
        mView.setShowDialog("正在发布中..");
        mView.showLoading();

        String review = value_et.getText().toString();
        if (AppUtils.isEmpty(review)) {
            MyToast.show(mView, "请输入评论内容！");
            return;
        }

        if (mView.lists.size() == 0) {
            MyToast.show(mView, "请选择要上传的图片！");
            return;
        }

        String cotentJson = "[{\"goods\":" + mView.mProductId + ",\"grade\":3,\"content\":\"" + review + "\"}]";

        final Map<String, String> params = new HashMap<String, String>();
        params.put("order", mView.mOrderId + "");
        params.put("review", cotentJson);
        params.put("is_anonymous", 0 + "");

        new Thread(new Runnable() {
            @Override
            public void run() {
                final String resultJson = NetWorkUtils.uploadMoreFile(mView.lists, NetWorkConst.REVICE_ORDER_URL, params, "shaitu");
                //                            //分享的操作
                mView.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mView.hideLoading();
                        JSONObject dataObject = JSONObject.parseObject(resultJson);
                        int code = dataObject.getInteger(Constance.error_code);
                        if (code == 0) {
                            mView.finish();
                            MyToast.show(mView, "发布成功，请等待后台审核!");
                        } else {
                            String errorDesc = dataObject.getString(Constance.error_desc);
                            MyToast.show(mView, errorDesc);
                        }
                    }
                });
            }
        }).start();
    }

}
