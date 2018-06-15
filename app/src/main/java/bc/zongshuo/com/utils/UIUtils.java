package bc.zongshuo.com.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

import bc.zongshuo.com.R;
import bc.zongshuo.com.bean.Attrs;
import bc.zongshuo.com.bean.Properties;
import bc.zongshuo.com.cons.Constance;
import bc.zongshuo.com.ui.activity.IssueApplication;
import bocang.json.JSONArray;

/**
 * @author Jun
 * @time 2016/8/19  10:37
 * @desc ${TODD}
 */
public class UIUtils {

    /**
     * 得到上下文
     * @return
     */
    public static Context getContext(){
        return IssueApplication.getcontext();
    }


    public static String getDeviceId(){
        return ((TelephonyManager) getContext().getSystemService(getContext().TELEPHONY_SERVICE))
                .getDeviceId();
    }


    /**
     *mac
     * @param context
     * @return String
     */
    public static String getLocalMac(Context context){
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        String mac=info.getMacAddress();
        if(mac.equals("02:00:00:00:00:00")){
            return getInterfaceLocalmac();
        }else {
            return mac;
        }
    }



    public static String  getInterfaceLocalmac(){
        String mac="";
        Enumeration<NetworkInterface> interfaces = null;
        try {
            interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iF = interfaces.nextElement();
                byte[] addr = iF.getHardwareAddress();
                if (addr == null || addr.length == 0) {
                    continue;
                }
                StringBuilder buf = new StringBuilder();
                for (byte b : addr) {
                    buf.append(String.format("%02X:", b));
                }
                if (buf.length() > 0) {
                    buf.deleteCharAt(buf.length() - 1);
                }
                mac = buf.toString();
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return mac;
    }


    /**
     * 得到Resources对象
     * @return
     */
    public static Resources getResources(){
        return getContext().getResources();
    }

    /**
     * 得到包名
     * @return
     */
    public static String getpackageName(){
        return  getContext().getPackageName();
    }

    /**
     * 得到配置的String信息
     * @param resId
     * @return
     */
    public static String getString(int resId){
        return getResources().getString(resId);
    }

    /**
     * 得到配置的String信息
     * @param resId
     * @return
     */
    public static String getString(int resId,Object ...formatAgs){
        return getResources().getString(resId,formatAgs);
    }

    /**
     * 得到配置String数组
     * @param resId
     * @return
     */
    public static String[] getStringArr(int resId){
        return getResources().getStringArray(resId);
    }
    public static int dip2PX(int dip) {
        //拿到设备密度
        float density=getResources().getDisplayMetrics().density;
        int px= (int) (dip*density+.5f);
        return px;
    }

    public static Dialog showBottomInDialog(Activity activity,int layout_res,int height) {
        Dialog dialog = new Dialog(activity, R.style.customDialog);
        dialog.setContentView(layout_res);
        dialog.setCanceledOnTouchOutside(true);
        Window win = dialog.getWindow();
        win.setGravity(Gravity.BOTTOM|Gravity.LEFT );
        WindowManager.LayoutParams lp = win.getAttributes();
        WindowManager manager = activity.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        int width = outMetrics.widthPixels;
        lp.width = width;
        lp.height = height;
        lp.x=0;
        win.setWindowAnimations(R.style.dialogButtomInStyle);
        win.setAttributes(lp);
        dialog.show();
        return  dialog;
    }

    public static int getMiniPricePostion(com.alibaba.fastjson.JSONArray attrs) {
        String attrprice = Constance.attr_price_5;
//        switch (levelId) {
//            case 104:
//                attrprice = Constance.attr_price_5;
//                break;
//            case 103:
//                attrprice = Constance.attr_price_4;
//                break;
//            case 102:
//                attrprice = Constance.attr_price_3;
//                break;
//            case 101:
//                attrprice = Constance.attr_price_2;
//                break;
//            case 100:
//                attrprice = Constance.attr_price_1;
//                break;
//        }
        double temp = Double.parseDouble(attrs.getJSONObject(0).getString(attrprice));
        int position=0;
        try {
            for (int i = 1; i < attrs.size(); i++) {
                if (Double.parseDouble(attrs.getJSONObject(i).getString(attrprice)) < temp) {
                    temp = Double.parseDouble(attrs.getJSONObject(i).getString(attrprice));
                    position=i;
                }
            }
        }catch (Exception e){
            return 0;
        }
        return position;
    }
    public static int getMiniPricePostion(JSONArray attrs) {
        String attrprice = Constance.attr_price_5;
//        switch (levelId) {
//            case 104:
//                attrprice = Constance.attr_price_5;
//                break;
//            case 103:
//                attrprice = Constance.attr_price_4;
//                break;
//            case 102:
//                attrprice = Constance.attr_price_3;
//                break;
//            case 101:
//                attrprice = Constance.attr_price_2;
//                break;
//            case 100:
//                attrprice = Constance.attr_price_1;
//                break;
//        }
        double temp = Double.parseDouble(attrs.getJSONObject(0).getString(attrprice));
        int position=0;
        try {
            for (int i = 1; i < attrs.length(); i++) {
                if (Double.parseDouble(attrs.getJSONObject(i).getString(attrprice)) < temp) {
                    temp = Double.parseDouble(attrs.getJSONObject(i).getString(attrprice));
                    position=i;
                }
            }
        }catch (Exception e){
            return 0;
    }
        return position;
    }
    public static int getMiniPricePostion( List<Attrs> attrs) {
//        String attrprice = Constance.attr_price_5;
//        switch (levelId) {
//            case 104:
//                attrprice = Constance.attr_price_5;
//                break;
//            case 103:
//                attrprice = Constance.attr_price_4;
//                break;
//            case 102:
//                attrprice = Constance.attr_price_3;
//                break;
//            case 101:
//                attrprice = Constance.attr_price_2;
//                break;
//            case 100:
//                attrprice = Constance.attr_price_1;
//                break;
//        }
        double temp =attrs.get(0).getAttr_price_1();
        int position=0;
        try {
            for (int i = 1; i < attrs.size(); i++) {
                if(attrs.get(i).getAttr_price_1()<temp){
                    temp = attrs.get(i).getAttr_price_1();
                    position=i;
                }
            }
        }catch (Exception e){
            return 0;
        }
        return position;
    }
    public static String getMiniPrice(int levelId, List<Attrs> attrs) {
        String attrprice = Constance.attr_price_5;
        switch (levelId) {
            case 104:
                attrprice = Constance.attr_price_5;
                break;
            case 103:
                attrprice = Constance.attr_price_4;
                break;
            case 102:
                attrprice = Constance.attr_price_3;
                break;
            case 101:
                attrprice = Constance.attr_price_2;
                break;
            case 100:
                attrprice = Constance.attr_price_1;
                break;
        }
        double temp = attrs.get(0).getAttr_price_1();
        int position=0;
        try {
            for (int i = 1; i < attrs.size(); i++) {
                if(attrs.get(i).getAttr_price_1()<temp){
                    temp =attrs.get(i).getAttr_price_1();
                    position=i;
                }
            }
        }catch (Exception e){
            return "0";
        }
        return temp+"";
    }
    public static String getMiniPrice(int levelId, JSONArray attrs) {
        String attrprice = Constance.attr_price_5;
        switch (levelId) {
            case 104:
                attrprice = Constance.attr_price_5;
                break;
            case 103:
                attrprice = Constance.attr_price_4;
                break;
            case 102:
                attrprice = Constance.attr_price_3;
                break;
            case 101:
                attrprice = Constance.attr_price_2;
                break;
            case 100:
                attrprice = Constance.attr_price_1;
                break;
        }
        double temp = Double.parseDouble(attrs.getJSONObject(0).getString(attrprice));
        int position=0;
        try {
            for (int i = 1; i < attrs.length(); i++) {
                if (Double.parseDouble(attrs.getJSONObject(i).getString(attrprice)) < temp) {
                    temp = Double.parseDouble(attrs.getJSONObject(i).getString(attrprice));
                    position=i;
                }
            }
        }catch (Exception e){
            return "0";
        }
        return temp+"";
    }

    public static double getScCurrentPrice(String property, List<Properties > properties) {
        for(int x=0;x<properties.size();x++){
            if(properties.get(x).getName().equals("规格")){
                for(int y=0;y<properties.get(x).getAttrs().size();y++){
                    if((properties.get(x).getAttrs().get(y).getId()+"").equals(property)){
                        return getlevePrice(properties.get(x).getAttrs().get(y));
                    }
                }
            }
        }
        return 0;
    }
    public static double getlevePrice(Attrs attrs){
        int levelid=104;
        if(IssueApplication.mUserObject!=null){
            levelid=IssueApplication.mUserObject.getInt(Constance.level_id);
        }
        double attrprice = attrs.getAttr_price_5();
        switch (levelid) {
            case 104:
                attrprice =attrs.getAttr_price_5();
                break;
            case 103:
                attrprice = attrs.getAttr_price_4();
                break;
            case 102:
                attrprice =attrs.getAttr_price_3();
                break;
            case 101:
                attrprice = attrs.getAttr_price_2();
                break;
            case 100:
                attrprice =attrs.getAttr_price_1();
                break;
        }
        return attrprice;
    }
    public static String getScCurrentImg(String attrs, List<Properties> properties) {
        for(int x=0;x<properties.size();x++){
            if(properties.get(x).getName().equals("规格")){
                for(int y=0;y<properties.get(x).getAttrs().size();y++){
                    if((properties.get(x).getAttrs().get(y).getId()+"").equals(attrs)){
                        return properties.get(x).getAttrs().get(y).getImg();
                    }
                }
            }
        }
        return null;
    }
    public static void showSingleWordDialog(final Context activity, String tittle, final View.OnClickListener listener) {
        final Dialog dialog = new Dialog(activity, R.style.customDialog);
        dialog.setContentView(R.layout.dialog_layout);
        TextView tv_num= (TextView) dialog.findViewById(R.id.tv_num);
        tv_num.setText(tittle);

        TextView btn = (TextView) dialog.findViewById(R.id.tv_ensure);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(view);
                dialog.dismiss();
            }
        });
        TextView cancel= (TextView) dialog.findViewById(R.id.tv_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
           /*
         * 获取圣诞框的窗口对象及参数对象以修改对话框的布局设置, 可以直接调用getWindow(),表示获得这个Activity的Window
         * 对象,这样这可以以同样的方式改变这个Activity的属性.
         */
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
    }
    /**
     * 保存图片
     *
     */
    public static String saveImage( WebView webView)  {
        Picture picture = webView.capturePicture();
        Bitmap b = Bitmap.createBitmap(
                picture.getWidth(), picture.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        picture.draw(c);

        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/zspage/");
        if(file.exists()) {
            file.delete();
        }
            file.mkdirs();
        String local_file=file.getAbsolutePath()+"/zspage.jpg";
        FileOutputStream fos = null;
        File local=new File(local_file);
        try {
            local.createNewFile();
            fos = new FileOutputStream(local.getAbsoluteFile());
            if (fos != null) {
                b.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                fos.close();
            }
            return local.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
        return local.getAbsolutePath();
        }
    }

}
