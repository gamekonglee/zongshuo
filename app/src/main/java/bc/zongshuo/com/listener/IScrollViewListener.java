package bc.zongshuo.com.listener;

import bc.zongshuo.com.ui.view.ObservableScrollView;

/**
 * @author: Jun
 * @date : 2017/5/24 9:55
 * @description :
 */
public interface IScrollViewListener {
    void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy);
}
