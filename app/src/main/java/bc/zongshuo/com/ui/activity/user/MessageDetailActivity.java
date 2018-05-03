package bc.zongshuo.com.ui.activity.user;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import bc.zongshuo.com.R;
import bc.zongshuo.com.cons.Constance;
import bc.zongshuo.com.ui.view.MyWebView;
import bocang.utils.AppUtils;
import bocang.view.BaseActivity;

/**
 * @author: Jun
 * @date : 2017/3/10 16:20
 * @description :消息内容
 */
public class MessageDetailActivity extends BaseActivity {
    String mUrl;
    MyWebView mWebView;
    int mFromType=0;
    TextView title_tv;

    @Override
    protected void InitDataView() {
        if(AppUtils.isEmpty(mUrl)) return;
        mWebView.loadUrl(mUrl);
    }

    @Override
    protected void initController() {

    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_sys_message_detail);
        mWebView = (MyWebView)findViewById(R.id.webview);
        mWebView.setActivity(this);
        title_tv = (TextView)findViewById(R.id.title_tv);
        if(mFromType==1){
            title_tv.setText("场景详情");
        }
//        mWebView.setWebChromeClient(new WebChromeClient());
//        mWebView.setWebViewClient(new WebViewClient());
//        mWebView.getSettings().setJavaScriptEnabled(true);
//        mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
//        mWebView.getSettings().setSupportZoom(true);
//        mWebView.getSettings().setUseWideViewPort(true);
//        mWebView.getSettings().setLoadWithOverviewMode(true);
//        mWebView.getSettings().setDefaultTextEncodingName("utf-8");
        mWebView.setWebViewClient(new WebViewClient (){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("tel:")){
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(url));
                    startActivity(intent);
                } else if(url.startsWith("http:") || url.startsWith("https:")) {
                    view.loadUrl(url);
                }
                return true;
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                view.loadUrl("javascript:(function() { " +
                        "document.body.innerHTML = "+
                        "document.body.innerHTML.replace(/api/,'www');"+
                        "})()");
            }
        });
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        mUrl=intent.getStringExtra(Constance.url);
        mFromType=intent.getIntExtra(Constance.FROMTYPE,0);
    }

    @Override
    protected void onViewClick(View v) {

    }
}
