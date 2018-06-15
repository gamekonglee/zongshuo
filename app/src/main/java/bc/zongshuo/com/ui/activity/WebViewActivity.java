package bc.zongshuo.com.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import bc.zongshuo.com.R;
import bc.zongshuo.com.cons.Constance;
import bc.zongshuo.com.cons.NetWorkConst;
import bc.zongshuo.com.ui.view.ScannerUtils;

public class WebViewActivity extends AppCompatActivity {

    private boolean hasSend;
    private WebView webView;
    private String mHtml;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new InJavaScriptLocalObj(),"local_obj");
        webView.setWebViewClient(new MyWebViewClient());
        webView.setInitialScale(60);

        hasSend = false;
        String url=getIntent().getStringExtra(Constance.url);
        webView.loadUrl(url);

    }

    final class MyWebViewClient extends WebViewClient {
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Log.d("WebView","onPageStarted");
            super.onPageStarted(view, url, favicon);
        }
        public void onPageFinished(WebView view, String url) {
            Log.d("WebView","onPageFinished ");

            view.loadUrl("javascript:window.local_obj.showSource('<head>'+"+
                    "document.getElementsByTagName('html')[0].innerHTML+'</head>');");
            super.onPageFinished(view, url);
        }
    }

    final class InJavaScriptLocalObj {
        @JavascriptInterface
        @SuppressWarnings("unused")
        public void showSource(String html) {
            Log.e("html",html);
            mHtml = html.replace("font-size:.32rem","font-size:.86rem").replace("<img src=\"./","<img src=\""+ NetWorkConst.SCENE_HOST).replace("font-size:.6rem","font-size:86rem").replace("style=\"font-size: 0.457143rem;\"","style=\"font-size: 1rem;\"");
            Log.d("HTML", mHtml);
//            mHtml = html.replace("<p>","<a style=\" color:#666; font-size:20px;\">").replace("</p>","</a>").replace("<img src=\"./","<img src=\"http://www.juhao.com/");
            if(!hasSend){
                handler.sendEmptyMessage(0);
            }
        }
    }
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            hasSend=true;
            webView.loadData(mHtml,  "text/html; charset=UTF-8", null);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    String path=ScannerUtils.saveImageToGallery(WebViewActivity.this,getBitmap(webView), ScannerUtils.ScannerType.RECEIVER);
                    Intent intent=getIntent().putExtra("path",path);
                    setResult(200,intent);
                    WebViewActivity.this.finish();
                }
            },500);
        }
    };
    public static Bitmap getBitmap(WebView webView){
        Picture picture = webView.capturePicture();
        Bitmap b = Bitmap.createBitmap(
                picture.getWidth(), picture.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        picture.draw(c);
        return b;
    }
}
