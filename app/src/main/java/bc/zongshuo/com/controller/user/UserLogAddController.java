package bc.zongshuo.com.controller.user;

import android.content.Intent;
import android.os.Message;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;

import bc.zongshuo.com.R;
import bc.zongshuo.com.bean.Logistics;
import bc.zongshuo.com.cons.Constance;
import bc.zongshuo.com.controller.BaseController;
import bc.zongshuo.com.data.LogisticDao;
import bc.zongshuo.com.ui.activity.user.UserLogAddActivity;
import bc.zongshuo.com.utils.ColorUtil;
import bc.zongshuo.com.utils.UIUtils;
import bocang.utils.AppDialog;
import bocang.utils.AppUtils;
import bocang.utils.CommonUtil;
import bocang.utils.MyToast;
import cn.qqtheme.framework.entity.City;
import cn.qqtheme.framework.entity.County;
import cn.qqtheme.framework.entity.Province;
import cn.qqtheme.framework.picker.AddressPicker;
import cn.qqtheme.framework.util.ConvertUtils;
import cn.qqtheme.framework.util.LogUtils;

/**
 * @author: Jun
 * @date : 2017/1/19 17:08
 * @description :
 */
public class UserLogAddController extends BaseController {
    private UserLogAddActivity mView;
    private EditText logistics_name_tv, logistics_phone_tv, logistics_address_tv;
    private Intent mIntent;
    private String mId = "";
    private TextView tip_tv, user_addr_txtCity;
    private String mRegion;

    public UserLogAddController(UserLogAddActivity v) {
        mView = v;
        initView();
        initViewData();
    }

    private void initViewData() {
        tip_tv.setText(ColorUtil.getSpecialTextColor("重要消息：卖家会尽可能满足您指定物流公司的需求，因工厂所在区域各不相同，请下单后工厂客服确认物流公司。",
                "重要消息", mView.getResources().getColor(R.color.red)));
        if (AppUtils.isEmpty(mView.mLogistics)) return;
        logistics_name_tv.setText(mView.mLogistics.getName());
        logistics_address_tv.setText(mView.mLogistics.getAddress());
        logistics_phone_tv.setText(mView.mLogistics.getTel());
        mId = mView.mLogistics.getId() + "";

    }

    private void initView() {
        logistics_name_tv = (EditText) mView.findViewById(R.id.logistics_name_tv);
        user_addr_txtCity = (TextView) mView.findViewById(R.id.user_addr_txtCity);
        logistics_phone_tv = (EditText) mView.findViewById(R.id.logistics_phone_tv);
        logistics_address_tv = (EditText) mView.findViewById(R.id.logistics_address_tv);
        tip_tv = (TextView) mView.findViewById(R.id.tip_tv);
    }

    @Override
    protected void handleMessage(int action, Object[] values) {

    }

    @Override
    protected void myHandleMessage(Message msg) {

    }

    /**
     * 选择城市
     */
    public void selectAddress() {
        try {
            ArrayList<Province> data = new ArrayList<>();
            String json = ConvertUtils.toString(mView.getAssets().open("city.json"));
            data.addAll(JSON.parseArray(json, Province.class));
            final AddressPicker picker = new AddressPicker(mView, data);
            picker.setHideProvince(false);
            picker.setSelectedItem("广东", "佛山", "禅城");
            picker.setOnAddressPickListener(new AddressPicker.OnAddressPickListener() {
                @Override
                public void onAddressPicked(Province province, City city, County county) {
                    String address = province.getAreaName() + " " + city.getAreaName() + " " + county.getAreaName();

                    if (AppUtils.isEmpty(county.getCityId())) {
                        mRegion = city.getAreaId();


                    } else {
                        mRegion = county.getAreaId();
                    }
                    user_addr_txtCity.setText(address);
                    picker.dismiss();
                }
            });
            picker.show();

        } catch (Exception e) {
            MyToast.show(mView, LogUtils.toStackTraceString(e));
        }
    }

    /**
     * 保存物流
     */
    public void sendAddLogistic() {
        String name = logistics_name_tv.getText().toString();
        String phone = logistics_phone_tv.getText().toString();
        String address = logistics_address_tv.getText().toString();
        if (AppUtils.isEmpty(name)) {
            MyToast.show(mView, "物流公司名称不能为空!");
            return;
        }
        if (AppUtils.isEmpty(phone)) {
            MyToast.show(mView, "电话不能为空!");
            return;
        }

        // 做个正则验证手机号
        if (!CommonUtil.isMobileNO(phone)) {
            AppDialog.messageBox(UIUtils.getString(R.string.mobile_assert));
            return;
        }
        if (AppUtils.isEmpty(address)) {
            MyToast.show(mView, "物流地址不能为空!");
            return;
        }

        LogisticDao dao = new LogisticDao(mView);
        Logistics logistics = new Logistics();
        logistics.setName(name);
        logistics.setTel(phone);
        logistics.setAddress(address);
        long isSave = dao.replaceOne(logistics);
        if (isSave == -1) {
            MyToast.show(mView, "保存失败");
        } else {
            MyToast.show(mView, "保存成功");
            if (!AppUtils.isEmpty(mId)) {
                dao.deleteOne(Integer.parseInt(mId));
            }
            mIntent = new Intent();
            mView.setResult(Constance.FROMLOG, mIntent);//告诉原来的Activity 将数据传递给它
            mView.finish();//一定要调用该方法 关闭新的AC 此时 老是AC才能获取到Itent里面的值
        }


    }

}
