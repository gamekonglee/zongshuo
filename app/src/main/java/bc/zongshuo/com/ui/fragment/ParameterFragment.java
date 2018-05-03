package bc.zongshuo.com.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import bc.zongshuo.com.R;
import bc.zongshuo.com.bean.Programme;
import bc.zongshuo.com.common.BaseFragment;
import bc.zongshuo.com.cons.Constance;
import bc.zongshuo.com.controller.product.ParameterController;

/**
 * @author: Jun
 * @date : 2017/2/14 11:04
 * @description :
 */
public class ParameterFragment extends BaseFragment {
    private ParameterController mController;
    public String productId;
    public List<Programme> mProgrammes;

    @Nullable
     @Override
     public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fm_product_parameter, null);

    }

    @Override
    protected void initController() {
        mController=new ParameterController(this);
    }

    @Override
    protected void initViewData() {

    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        productId= (String) getArguments().get(Constance.product);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
