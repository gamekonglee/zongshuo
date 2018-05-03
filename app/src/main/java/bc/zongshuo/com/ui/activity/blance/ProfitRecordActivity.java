package bc.zongshuo.com.ui.activity.blance;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import bc.zongshuo.com.R;
import bc.zongshuo.com.controller.blance.ProfitRecordController;
import bc.zongshuo.com.ui.view.CustomDatePicker;
import bc.zongshuo.com.utils.DateUtils;
import bocang.utils.LogUtils;
import bocang.view.BaseActivity;

/**
 * @author: Jun
 * @date : 2017/7/27 17:46
 * @description :
 */
public class ProfitRecordActivity extends BaseActivity {
    private ProfitRecordController mController;
    public String start_time="955533548000";
    public String end_time=System.currentTimeMillis()+"";
    private TextView tv_start_date;
    private TextView tv_end_date;


    @Override
    protected void InitDataView() {

    }

    @Override
    protected void initController() {
        mController=new ProfitRecordController(this);
    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_profit_record);
        tv_start_date = findViewById(R.id.tv_start_date);
        tv_end_date = findViewById(R.id.tv_end_date);

        tv_start_date.setOnClickListener(this);
        tv_end_date.setOnClickListener(this);
        start_time = "";
        end_time = "";
        //沉浸式状态栏
//        setColor(this, getResources().getColor(R.color.green));
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onViewClick(View v) {
        switch (v.getId()){
            case R.id.tv_start_date:
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
                String now = sdf.format(new Date());
                CustomDatePicker currentDate= new CustomDatePicker(this, new CustomDatePicker.ResultHandler() {
                    @Override
                    public void handle(String time) {
                        LogUtils.logE("time",time);
                        start_time=""+DateUtils.getTimeStamp(time.substring(0,10),"yyyy-MM-dd");
                        tv_start_date.setText(DateUtils.getStrTime02(start_time)+"");
                        tv_end_date.performClick();
                    }
                },"1970-01-01 00:00", now);
                    currentDate.showSpecificTime(false);
                    currentDate.setIsLoop(true);
//                    currentDate.setSelectedTime(DateUtils.getStrTime(System.currentTimeMillis()+""));
                    currentDate.show(now);
//                    if(!TextUtils.isEmpty(start_time)&&!TextUtils.isEmpty(end_time)){
//                        mController.sendProfitRecordList();
//                    }
                break;
            case R.id.tv_end_date:
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
                String now2 = sdf2.format(new Date());
                CustomDatePicker currentDate2= new CustomDatePicker(this, new CustomDatePicker.ResultHandler() {
                    @Override
                    public void handle(String time) {
                        LogUtils.logE("time",time);
                        end_time=""+DateUtils.getTimeStamp(time.substring(0,10),"yyyy-MM-dd");
                        tv_end_date.setText(DateUtils.getStrTime02(end_time)+"");
                        if(!TextUtils.isEmpty(start_time)&&!TextUtils.isEmpty(end_time)){
                            mController.sendProfitRecordList();
                        }
                    }
                },"1970-01-01 00:00", now2);
                currentDate2.showSpecificTime(false);
                currentDate2.setIsLoop(true);
//                    currentDate.setSelectedTime(DateUtils.getStrTime(System.currentTimeMillis()+""));
                currentDate2.show(now2);

                break;

        }
    }
}
