package bc.zongshuo.com.ui.view;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import bc.zongshuo.com.R;
import bc.zongshuo.com.listener.INumberInputListener;


/**
 * @author Jun
 * @time 2016/9/6  15:03
 * @desc ${TODD}
 */
public class NumberInputView extends LinearLayout implements View.OnClickListener{

    private ImageView mIncreaseIv;
    private EditText mNumberTv;
    private ImageView mDecreaseIv;
    private int mMax;
    private int mCurrentNum;
    private INumberInputListener mListener;
    private int warn_number=1;

    public void setListener(INumberInputListener mListener) {
        this.mListener = mListener;
    }

    public NumberInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 能够输入的最大值
     */
    public void setMax(int mMax) {
        this.mMax = mMax;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mIncreaseIv=(ImageView) findViewById(R.id.rightTv);
        mDecreaseIv=(ImageView) findViewById(R.id.leftTv);
        mIncreaseIv.setOnClickListener(this);
        mDecreaseIv.setOnClickListener(this);
        mNumberTv=(EditText) findViewById(R.id.numTv);
        mNumberTv.addTextChangedListener(new TextChangedListener());
    }

    public boolean ifChanged=true;

    public void setWarnNumber(int warn_number) {
        mNumberTv.setText(""+warn_number);
        this.warn_number=warn_number;
    }

    public class TextChangedListener implements TextWatcher {


        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!ifChanged) {
                return;
            }
            checkNumber();
        }

    }

    private void checkNumber() {
        ifChanged=false;
        String number = mNumberTv.getText().toString();
        if (!TextUtils.isEmpty(number)) {
            mCurrentNum=Integer.parseInt(number);
            if (mCurrentNum<warn_number) {
                mCurrentNum=warn_number;
                mNumberTv.setText(mCurrentNum+"");
            }
            if (mCurrentNum>mMax) {
                mCurrentNum=mMax;
                mNumberTv.setText(mCurrentNum+"");
            }
        }else {
            mNumberTv.setText(""+warn_number);
        }
        if (mListener!=null) {
            mListener.onTextChange(Integer.parseInt(mNumberTv.getText().toString()));
        }
        ifChanged=true;
    }

    @Override
    public void onClick(View v) {
        String number = mNumberTv.getText().toString();
        if (TextUtils.isEmpty(number)) {
            mNumberTv.setText(""+warn_number);
            return;
        }
        mCurrentNum=Integer.parseInt(number);
        if (v.getId()== R.id.rightTv) {
            mCurrentNum+=warn_number;
        }else if (v.getId()==R.id.leftTv) {
            mCurrentNum-=warn_number;
        }
        mNumberTv.setText(mCurrentNum+"");
    }

}
