package bc.zongshuo.com.controller.programme;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;

import java.io.IOException;

import bc.zongshuo.com.controller.BaseController;
import bc.zongshuo.com.ui.activity.IssueApplication;
import bc.zongshuo.com.ui.activity.MainActivity;
import bc.zongshuo.com.ui.activity.programme.ShareProgrammeActivity;
import bc.zongshuo.com.ui.view.ScannerUtils;
import bc.zongshuo.com.ui.view.ShowDialog;
import bc.zongshuo.com.utils.ImageUtil;
import bc.zongshuo.com.utils.ShareUtil;

/**
 * @author: Jun
 * @date : 2017/6/8 16:09
 * @description :
 */
public class ShareProgrammeController extends BaseController {
    private ShareProgrammeActivity mView;
    private Bitmap localBitmap;

    public ShareProgrammeController(ShareProgrammeActivity v) {
        mView = v;
        initView();
        initViewData();
    }

    private void initViewData() {

    }

    private void initView() {

    }

    @Override
    protected void handleMessage(int action, Object[] values) {

    }

    @Override
    protected void myHandleMessage(Message msg) {

    }

    /**
     * 查看方案
     */
    public void getFanganDetail() {
        Intent mainIntent = new Intent(mView, MainActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        IssueApplication.isGoProgramme = true;
        mView.startActivity(mainIntent);
    }

    /**
     * 保存图片
     */
    public void getSaveImage() {
        ActivityCompat.requestPermissions(mView,
                new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"},
                1);
        PackageManager packageManager = mView.getPackageManager();
        int permission = packageManager.checkPermission("android.permission.WRITE_EXTERNAL_STORAGE", "bc.zongshuo.com");
        if (PackageManager.PERMISSION_GRANTED != permission) {
            return;
        } else {

            ShowDialog mDialog = new ShowDialog();
            mDialog.show(mView, "提示", "是否保存该分享图片?", new ShowDialog.OnBottomClickListener() {
                @Override
                public void positive() {
                    PackageManager packageManager = mView.getPackageManager();
                    int permission = packageManager.checkPermission("android.permission.WRITE_EXTERNAL_STORAGE", "bc.zongshuo.com");
                    if (PackageManager.PERMISSION_GRANTED != permission) {
                        return;
                    } else {
                        mView.setShowDialog(true);
                        mView.setShowDialog("正在保存中..");
                        mView.showLoading();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                final Bitmap bitmap = ImageUtil.getbitmap(mView.mShareImgPath);
                                mView.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ScannerUtils.saveImageToGallery(mView, bitmap, ScannerUtils.ScannerType.RECEIVER);
                                        bitmap.recycle();
                                        mView.hideLoading();
                                    }
                                });
                            }
                        }).start();
                    }
                }

                @Override
                public void negtive() {

                }
            });
        }
    }

    /**
     * 分享数据
     */
    public void getShareData() {
        Toast.makeText(mView, "正在分享..", Toast.LENGTH_LONG).show();

        if(mView.mShareType==1){
            //pic
            try {
//                localBitmap = BitmapFactory.decodeFile(mView.mShareImgPath);
                ImageLoader.getInstance().loadImage(mView.mShareImgPath, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String s, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String s, View view, FailReason failReason) {

                    }

                    @Override
                    public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                        if(mView.typeShare== SendMessageToWX.Req.WXSceneSession){
                            ShareUtil.shareWxPic(mView,mView.mShareTitle,bitmap,true);
                        }else if(mView.typeShare==SendMessageToWX.Req.WXSceneTimeline){
                            ShareUtil.shareWxPic(mView,mView.mShareTitle,bitmap,false);
                        }else {
                            mView.mShareImgPath=ScannerUtils.saveImageToGallery(mView,bitmap, ScannerUtils.ScannerType.MEDIA);
                            ShareUtil.shareQQLocalpic(mView,mView.mShareImgPath,mView.mShareTitle);
                        }
                    }

                    @Override
                    public void onLoadingCancelled(String s, View view) {

                    }
                });
//                ImageLoader.getInstance().loadImage(mView.mSharePath);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }else {
            if(mView.typeShare== SendMessageToWX.Req.WXSceneSession){
                ShareUtil.shareWx(mView,mView.mShareTitle,mView.mSharePath,mView.mShareImgPath);
            }else if(mView.typeShare==SendMessageToWX.Req.WXSceneTimeline){
                ShareUtil.sharePyq(mView,mView.mShareTitle,mView.mSharePath,mView.mShareImgPath);
            }else {
                ShareUtil.shareQQ(mView,mView.mShareTitle,mView.mSharePath,mView.mShareImgPath);
            }
        }
    }
}
