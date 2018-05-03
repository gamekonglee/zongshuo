package bc.zongshuo.com.ui.view.dialog;

import android.content.Context;

/**
 * Created by WZH on 2016/12/11.
 */

public class ShowSureDialog {
    private SureDialog selfDialog ;
    public ShowSureDialog() {
    }
    public void show(final Context ctx, String title, String message, final OnBottomClickListener onBottomClickListener){
        selfDialog = new SureDialog(ctx);
        selfDialog.setTitle(title);
        selfDialog.setMessage(message);
        selfDialog.setYesOnclickListener("确定", new SureDialog.onYesOnclickListener() {
            @Override
            public void onYesClick() {
                if (onBottomClickListener != null) {
                    onBottomClickListener.positive();
                }
                selfDialog.dismiss();
            }
        });
        selfDialog.show();
    }

    public interface OnBottomClickListener{
        void positive();
    }
}
