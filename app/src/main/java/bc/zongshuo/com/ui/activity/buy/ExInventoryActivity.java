package bc.zongshuo.com.ui.activity.buy;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bc.zongshuo.com.R;
import bc.zongshuo.com.cons.Constance;
import bc.zongshuo.com.cons.NetWorkConst;
import bc.zongshuo.com.ui.activity.IssueApplication;
import bc.zongshuo.com.ui.view.ScannerUtils;
import bc.zongshuo.com.utils.FileUtil;
import bc.zongshuo.com.utils.ImageUtil;
import bc.zongshuo.com.utils.ShareUtil;
import bocang.json.JSONArray;
import bocang.utils.AppUtils;
import bocang.view.BaseActivity;

/**
 * @author: Jun
 * @date : 2017/8/7 9:27
 * @description :
 */
public class ExInventoryActivity extends BaseActivity {
    private RelativeLayout save_rl;
    private LinearLayout cotent_ll;
    public JSONArray goodsList;
    public JSONObject orderObject;
    private LinearLayout wechat_ll, wechatmoments_ll, share_qq_ll, save_ll;
    private ScrollView sv;
    private int mShareType = 0;
    private Bitmap bitmap;

    @Override
    protected void InitDataView() {

    }

    @Override
    protected void initController() {

    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_cart_inventory);
        save_rl = getViewAndClick(R.id.save_rl);
        wechat_ll = getViewAndClick(R.id.wechat_ll);
        wechatmoments_ll = getViewAndClick(R.id.wechatmoments_ll);
        share_qq_ll = getViewAndClick(R.id.share_qq_ll);
        save_ll = getViewAndClick(R.id.save_ll);
        sv = (ScrollView) findViewById(R.id.sv);
        cotent_ll = (LinearLayout) findViewById(R.id.cotent_ll);
        for (int i = 0; i < mUids.size(); i++) {
            WebView mView = new WebView(this);
            //            WebSettings settings = mView.getSettings();
            //            settings.setJavaScriptEnabled(true);
            //            settings.setAppCacheEnabled(true);
            //            settings.setDatabaseEnabled(true);
            //            settings.setDomStorageEnabled(true);
            //            settings.setCacheMode(WebSettings.LOAD_DEFAULT);
            //            settings.setAllowFileAccess(true);
            WebSettings webSettings = mView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setJavaScriptEnabled(true);
            webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
            webSettings.setUseWideViewPort(true);//关键点

            webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

            webSettings.setDisplayZoomControls(false);
            webSettings.setJavaScriptEnabled(true); // 设置支持javascript脚本
            webSettings.setAllowFileAccess(true); // 允许访问文件
            webSettings.setBuiltInZoomControls(false); // 设置显示缩放按钮
            webSettings.setSupportZoom(false); // 支持缩放

            webSettings.setLoadWithOverviewMode(true);

            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int mDensity = metrics.densityDpi;
            if (mDensity == 240) {
                webSettings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
            } else if (mDensity == 160) {
                webSettings.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
            } else if (mDensity == 120) {
                webSettings.setDefaultZoom(WebSettings.ZoomDensity.CLOSE);
            } else if (mDensity == DisplayMetrics.DENSITY_XHIGH) {
                webSettings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
            } else if (mDensity == DisplayMetrics.DENSITY_TV) {
                webSettings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
            } else {
                webSettings.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
            }
            webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
            //        //设置WebView可触摸放大缩小
            //            mView.setInitialScale(100);
            mView.setDrawingCacheEnabled(true);
            mView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
            cotent_ll.addView(mView);
        }

        if (mShareType == 0) {
            String id = IssueApplication.mUserObject.getString(Constance.id);
            String uids="";
            String numbers="";
            String attrs="";
            for(int x=0;x<mUids.size();x++){
                uids+=mUids.get(x)+",";
                numbers+=mNumbers.get(x)+",";
                attrs+=mAttrs.get(x)+",";
            }
            uids=uids.substring(0,uids.length()-1);
            numbers=numbers.substring(0,numbers.length()-1);
            attrs=attrs.substring(0,attrs.length()-1);
            for (int i = 0; i < cotent_ll.getChildCount(); i++) {

                String path = NetWorkConst.SCENE_HOST + "order_show.php?uid=" + id + "&goods=" + uids + "&number=" + numbers+"&attr="+attrs+ "&page=" + (i+1);
                if(i==cotent_ll.getChildCount()-1){
                    path+="&end";
                }
                ((WebView) cotent_ll.getChildAt(i)).loadUrl(path);
            }
        } else {
            String uids="";
            String numbers="";
            String attrs="";
            for(int x=0;x<mUids.size();x++){
                uids+=mUids.get(x)+",";
                numbers+=mNumbers.get(x)+",";
                attrs+=mAttrs.get(x)+",";
            }
            uids=uids.substring(0,uids.length()-1);
            numbers=numbers.substring(0,numbers.length()-1);
            attrs=attrs.substring(0,attrs.length()-1);

            for (int i = 0; i < cotent_ll.getChildCount(); i++) {
                String path = NetWorkConst.SCENE_HOST + "order_show.php?goods=" + uids+ "&number=" + numbers+"&attr="+attrs+ "&page=" + (i+1) + morderUserUrl;
                if(i==cotent_ll.getChildCount()-1){
                    path+="&end";
                }
                ((WebView) cotent_ll.getChildAt(i)).loadUrl(path);
            }
        }

    }


    private int mPage = 1;
    private List<String> mUids = new ArrayList<>();
    private List<String> mNumbers = new ArrayList<>();
    private List<String> mAttrs=new ArrayList<>();
    @Override
    protected void initData() {
        Intent intent = getIntent();
        mShareType = intent.getIntExtra(Constance.pdfType, 0);
        if (mShareType == 1) {
            orderObject = JSON.parseObject(intent.getStringExtra(Constance.goods));
        } else {
            goodsList = (JSONArray) intent.getSerializableExtra(Constance.goods);
        }


        getInventoryData();
    }

    String morderUserUrl = "";


    private void getInventoryData() {
        if (mShareType == 0) {
            if (goodsList.length() <= 8) {
                String uid = "";
                String number = "";
                String attr="";
                mPage = 1;
                for (int i = 0; i < goodsList.length(); i++) {
                    uid = uid + goodsList.getJSONObject(i).getJSONObject(Constance.product).getString(Constance.id) + ",";
                    number = number + goodsList.getJSONObject(i).getString(Constance.amount) + ",";
                    attr=attr+goodsList.getJSONObject(i).getString(Constance.attrs)+",";
                }
                uid = uid.substring(0, uid.length() - 1);
                number = number.substring(0, number.length() - 1);
                attr=attr.substring(0,attr.length()-1);
                mUids.add(uid);
                mNumbers.add(number);
                mAttrs.add(attr);
            } else {
                mPage = (goodsList.length() % 8 == 0) ? goodsList.length() / 8 : (goodsList.length() / 8 + 1);
                for (int i = 0; i < mPage; i++) {
                    String uid = "";
                    String number = "";
                    String attr="";
                    for (int j = 8 * i; j < 8 * (i + 1); j++) {
                        if (j > goodsList.length() - 1)
                            break;
                        uid = uid + goodsList.getJSONObject(j).getJSONObject(Constance.product).getString(Constance.id) + ",";
                        number = number + goodsList.getJSONObject(j).getString(Constance.amount) + ",";
                        attr=attr+goodsList.getJSONObject(j).getString(Constance.attrs)+",";
                    }
                    uid = uid.substring(0, uid.length() - 1);
                    number = number.substring(0, number.length() - 1);
                    attr=attr.substring(0,attr.length()-1);
                    mUids.add(uid);
                    mNumbers.add(number);
                    mAttrs.add(attr);
                }
            }
        } else {
            com.alibaba.fastjson.JSONArray goods = orderObject.getJSONArray(Constance.goods);
            if (goods.size() <= 8) {
                String uid = "";
                String number = "";
                String attr="";
                mPage = 1;
                for (int i = 0; i < goods.size(); i++) {
                    uid = uid + goods.getJSONObject(i).getString(Constance.id) + ",";
                    number = number + goods.getJSONObject(i).getString(Constance.total_amount) + ",";
                    attr=attr+goods.getJSONObject(i).getString(Constance.goods_attr_id)+",";
                }
                uid = uid.substring(0, uid.length() - 1);
                number = number.substring(0, number.length() - 1);
                attr=attr.substring(0,attr.length()-1);
                mUids.add(uid);
                mNumbers.add(number);
                mAttrs.add(attr);
            } else {
                mPage = (goods.size() % 8 == 0) ? goods.size() / 8 : (goods.size() / 8 + 1);

                for (int i = 0; i < mPage; i++) {
                    String uid = "";
                    String number = "";
                    String attr="";
                    for (int j = 8 * i; j < 8 * (i + 1); j++) {
                        if (j > goods.size() - 1)
                            break;
                        uid = uid + goods.getJSONObject(j).getString(Constance.id) + ",";
                        number = number + goods.getJSONObject(j).getString(Constance.total_amount) + ",";
                        attr=attr+goods.getJSONObject(j).getString(Constance.goods_attr_id)+",";

                    }
                    uid = uid.substring(0, uid.length() - 1);
                    number = number.substring(0, number.length() - 1);
                    attr=attr.substring(0,attr.length()-1);
                    mUids.add(uid);
                    mNumbers.add(number);
                    mAttrs.add(attr);
                }
            }

            JSONObject consigneeObject = orderObject.getJSONObject(Constance.consignee);
            String phone = consigneeObject.getString(Constance.mobile);
            String address = consigneeObject.getString(Constance.address);
            String username = consigneeObject.getString(Constance.name);
            morderUserUrl = "&phone=" + phone + "&address=" + address + "&username=" + username;
        }


    }

    @Override
    protected void onViewClick(View v) {
        switch (v.getId()) {
            case R.id.save_rl:
                savePDF();
                break;
            case R.id.wechat_ll://微信分享
                wechat_ll.setEnabled(false);
                Toast.makeText(this, "正在保存pdf文件..", Toast.LENGTH_LONG).show();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(500);
                            ExInventoryActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    savePDF();
                                    wechat_ll.setEnabled(true);
                                    Toast.makeText(ExInventoryActivity.this, "正在分享..", Toast.LENGTH_SHORT).show();
                                    typeShare = SendMessageToWX.Req.WXSceneSession;
                                    final Bitmap bitmap = ImageUtil.getBitmapByView(sv);
//                                    FileUtil.saveBitmap(bitmap, (String) mFileName);
                                    if(TextUtils.isEmpty(mFilePath)){
                                        savePDF();
                                    }
                                    ShareUtil.shareWxFile(ExInventoryActivity.this, (String) mFileName, mFilePath,true);
                                }
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

                break;
            case R.id.wechatmoments_ll://微信朋友圈
                Toast.makeText(this, "正在分享..", Toast.LENGTH_SHORT).show();
                typeShare = SendMessageToWX.Req.WXSceneTimeline;
                if (!AppUtils.isEmpty(bitmap)) {
                    ShareUtil.shareWxPic(ExInventoryActivity.this, (String) mFileName, bitmap,false);
                    return;
                }
                bitmap = ImageUtil.getBitmapByView(sv);
//                final Bitmap bitmap = ImageUtil.getBitmapByView(sv);
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        mShareImagePath = ScannerUtils.saveImageToGallery02(ExInventoryActivity.this, bitmap, ScannerUtils.ScannerType.RECEIVER);
                        ExInventoryActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ShareUtil.shareWxPic(ExInventoryActivity.this, (String) mFileName, bitmap,false);
                            }
                        });
                    }
                }).start();

                break;
            case R.id.share_qq_ll://qq分享
                Toast.makeText(this, "正在分享..", Toast.LENGTH_SHORT).show();
                typeShare = QQShare.SHARE_TO_QQ_TYPE_IMAGE;
                if (!AppUtils.isEmpty(mShareImagePath)) {
                    Toast.makeText(this, "图片保存为" + mShareImagePath, Toast.LENGTH_SHORT).show();
                    ShareUtil.shareQQLocalpic(ExInventoryActivity.this, mShareImagePath,mFileName+"");
                    //分享
                    return;
                }
                if(bitmap==null)bitmap=ImageUtil.getBitmapByView(sv);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
//                        mShareImagePath = ScannerUtils.saveImageToGallery02(ExInventoryActivity.this, bitmap, ScannerUtils.ScannerType.RECEIVER);
                        ExInventoryActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                final Bitmap bitmap = ImageUtil.getBitmapByView(sv);
                                mShareImagePath=ScannerUtils.saveImageToGallery(ExInventoryActivity.this, bitmap, ScannerUtils.ScannerType.RECEIVER);
                                ExInventoryActivity.this.hideLoading();
                                bitmap.recycle();
                                ShareUtil.shareQQLocalpic(ExInventoryActivity.this, mShareImagePath,mFileName+"");
                            }
                        });
                    }
                }).start();
                break;
            case R.id.save_ll://保存
                getSaveImage();
                break;

        }

    }

    private int  typeShare=0;
    private String mShareImagePath = "";


    /**
     * 保存图片
     */
    public void getSaveImage() {
        if (!AppUtils.isEmpty(mShareImagePath)) {
            Toast.makeText(this, "图片保存为" + mShareImagePath, Toast.LENGTH_SHORT).show();
            return;
        }

        ActivityCompat.requestPermissions(this,
                new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"},
                1);
        PackageManager packageManager = this.getPackageManager();
        int permission = packageManager.checkPermission("android.permission.WRITE_EXTERNAL_STORAGE", "bc.zongshuo.com");
        if (PackageManager.PERMISSION_GRANTED != permission) {
            return;
        } else {
            this.setShowDialog(true);
            //            mActivity.setShowDialog("正在保存中..");
            this.showLoading();
            new Thread(new Runnable() {
                @Override
                public void run() {


                    ExInventoryActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final Bitmap bitmap = ImageUtil.getBitmapByView(sv);
                            ScannerUtils.saveImageToGallery(ExInventoryActivity.this, bitmap, ScannerUtils.ScannerType.RECEIVER);
                            ExInventoryActivity.this.hideLoading();
                            bitmap.recycle();
                        }
                    });

                }
            }).start();
        }
    }

    private String mFilePath = "";
    private Object mFileName = "";

    /**
     * 保存PDF
     */
    private void savePDF() {
        Document document = new Document();
        try {
            File localFile;
            Object localObject1 = FileUtil.getFile(this);
            Object localObject2;
            Object localObject3;
            if (localObject1 != null) {
                localObject2 = new SimpleDateFormat("yyyyMMddHHmmss");
                localObject3 = new Date();
                if (mShareType == 0) {
                    localObject2 = "购物清单-" + ((SimpleDateFormat) localObject2).format((Date) localObject3) + ".pdf";
                    mFileName = "您的购物清单已出炉.pdf";
                } else {
                    localObject2 = "订单清单-" + ((SimpleDateFormat) localObject2).format((Date) localObject3) + ".pdf";
                    mFileName = "您的订单清单已出炉.pdf";
                }

                localFile = new File((File) localObject1, (String) localObject2);
                mFilePath = localFile.getAbsolutePath();
                PdfWriter.getInstance(document, new FileOutputStream(localFile));
                document.setMargins(0, 0, 0, 0);
                document.open();


                for (int i = 0; i < cotent_ll.getChildCount(); i++) {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    Bitmap bitmap = FileUtil.captureWebView(((WebView) cotent_ll.getChildAt(i)));
                    //获取webview缩放率
                    bitmap.compress(Bitmap.CompressFormat.PNG /* FileType */, 100 /* Ratio */, stream);
                    Image jpg = Image.getInstance(stream.toByteArray());
                    jpg.setAlignment(Image.MIDDLE);
                    float heigth = jpg.getHeight();
                    float width = jpg.getWidth();
                    int percent = getPercent2(heigth, width);
                    jpg.scalePercent(percent);
                    document.add(jpg);
                    bitmap.recycle();
                }
                document.setPageCount(cotent_ll.getChildCount());
            }
        } catch (DocumentException de) {
            System.err.println(de.getMessage());
        } catch (Exception ioe) {
            System.err.println(ioe.getMessage());
        }

        document.close();
        this.hideLoading();
    }

    public int getPercent(float h, float w) {
        int p = 0;
        float p2 = 0.0f;
        if (h > w) {
            p2 = 297 / h * 100;
        } else {
            p2 = 210 / w * 100;
        }
        p = Math.round(p2);
        return p;
    }

    public int getPercent2(float h, float w) {
        int p = 0;
        float p2 = 0.0f;
        p2 = 530 / w * 100;
        p = Math.round(p2);
        return p;
    }


}
