package bc.zongshuo.com.controller.buy;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.qysn.social.mqtt.gson.Gson;
import com.sevenonechat.sdk.compts.InfoSubmitActivity;
import com.sevenonechat.sdk.sdkinfo.SdkRunningClient;
import com.yjn.swipelistview.swipelistviewlibrary.widget.SwipeMenu;
import com.yjn.swipelistview.swipelistviewlibrary.widget.SwipeMenuCreator;
import com.yjn.swipelistview.swipelistviewlibrary.widget.SwipeMenuItem;
import com.yjn.swipelistview.swipelistviewlibrary.widget.SwipeMenuListView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import bc.zongshuo.com.R;
import bc.zongshuo.com.bean.Default_photo;
import bc.zongshuo.com.bean.Product;
import bc.zongshuo.com.bean.Properties;
import bc.zongshuo.com.bean.ScGoods;
import bc.zongshuo.com.cons.Constance;
import bc.zongshuo.com.cons.NetWorkConst;
import bc.zongshuo.com.controller.BaseController;
import bc.zongshuo.com.listener.INetworkCallBack;
import bc.zongshuo.com.ui.activity.IssueApplication;
import bc.zongshuo.com.ui.activity.buy.ConfirmOrderActivity;
import bc.zongshuo.com.ui.activity.buy.ExInventoryActivity;
import bc.zongshuo.com.ui.fragment.CartFragment;
import bc.zongshuo.com.ui.view.NumberInputView;
import bc.zongshuo.com.utils.UIUtils;
import bocang.json.JSONArray;
import bocang.json.JSONObject;
import bocang.json.JSONParser;
import bocang.utils.AppDialog;
import bocang.utils.AppUtils;
import bocang.utils.MyToast;
import cn.qqtheme.framework.util.LogUtils;

/**
 * @author: Jun
 * @date : 2017/2/21 14:35
 * @description :
 */
public class CartController extends BaseController implements INetworkCallBack {
    private CartFragment mView;
    private SwipeMenuListView mListView;
//    private JSONArray goodses;
    private MyAdapter myAdapter;
    private CheckBox checkAll;
    private TextView money_tv, settle_tv, edit_tv, num_tv;
    private boolean isStart = false;
    private LinearLayout sum_ll;
    private Boolean isEdit = false;
    private List<ScGoods> goods;
    private JSONObject mAddressObject;
    private View null_view;
    private List<ScGoods> scGoods;

    public CartController(CartFragment v) {
        mView = v;
        initView();
        initViewData();
    }

    private void initViewData() {
        mView.setShowDialog(true);
        mView.showLoading();
        sendAddressList();
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                mView.getActivity().getResources().getDisplayMetrics());
    }


    private void initView() {
        mListView = (SwipeMenuListView) mView.getView().findViewById(R.id.cart_lv);
        mListView.setDivider(null);//去除listview的下划线
        myAdapter = new MyAdapter();
        mListView.setAdapter(myAdapter);

        final SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(mView.getActivity());

                deleteItem.setBackground(new ColorDrawable(Color.parseColor("#fe3c3a")));
                deleteItem.setWidth(dp2px(80));
                deleteItem.setTitle("删除");
                deleteItem.setTitleColor(Color.WHITE);
                deleteItem.setTitleSize(20);
                menu.addMenuItem(deleteItem);
            }
        };
        mListView.setMenuCreator(creator);

        mListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                if (index == 0) {
                    mView.setShowDialog(true);
                    mView.setShowDialog("正在删除");
                    mView.showLoading();
                    String id =scGoods.get(position).getId()+"";
                    //                    mDeleteIndex=position;
                    isLastDelete = false;
                    deleteShoppingCart(id);
                }
                return false;
            }
        });
        checkAll = (CheckBox) mView.getActivity().findViewById(R.id.checkAll);
        money_tv = (TextView) mView.getActivity().findViewById(R.id.money_tv);
        num_tv = (TextView) mView.getActivity().findViewById(R.id.num_tv);
        settle_tv = (TextView) mView.getActivity().findViewById(R.id.settle_tv);
        edit_tv = (TextView) mView.getActivity().findViewById(R.id.edit_tv);
        sum_ll = (LinearLayout) mView.getActivity().findViewById(R.id.sum_ll);
        null_view = mView.getActivity().findViewById(R.id.null_cart_view);

        mExtView = (ViewGroup) LayoutInflater.from(mView.getActivity()).inflate(R.layout.alertext_form, null);
        etNum = (EditText) mExtView.findViewById(R.id.etName);
        etNum.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        imm = (InputMethodManager) mView.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

    }

    int mDeleteIndex = -1;


    private void deleteShoppingCart(String goodId) {
        mNetWork.sendDeleteCart(goodId, this);
    }

    @Override
    protected void handleMessage(int action, Object[] values) {

    }

    @Override
    protected void myHandleMessage(Message msg) {

    }

    /**
     * 编辑
     */
    public void setEdit() {
        if (!isEdit) {
            sum_ll.setVisibility(View.GONE);
            settle_tv.setText("删除");
            edit_tv.setText("完成");
            isEdit = true;

        } else {
            sum_ll.setVisibility(View.VISIBLE);
            settle_tv.setText("结算");
            edit_tv.setText("编辑");
            isEdit = false;
        }
    }

    public void sendShoppingCart() {
        null_view.setVisibility(View.GONE);
        mNetWork.sendShoppingCart(this);
    }

    public void sendUpdateCart(String good, String amount) {
        mNetWork.sendUpdateCart(good, amount, this);
    }

    @Override
    public void onSuccessListener(String requestCode, JSONObject ans) {
//        pd.setVisibility(View.INVISIBLE);
        try {
            mView.showContentView();
            switch (requestCode) {
                case NetWorkConst.DeleteCART:
//                    if (isLastDelete == true) {
//                        isLastDelete = false;
////                    isCheckList.remove(mDeleteIndex);
//                        return;
//                    }
                        sendShoppingCart();
                    mView.hideLoading();
                    break;
                case NetWorkConst.UpdateCART:
                    sendShoppingCart();
                    break;
                case NetWorkConst.GETCART:
                    mView.hideLoading();
                    bocang.utils.LogUtils.logE("cart",ans.toString());
                    if (ans.getJSONArray(Constance.goods_groups).length() > 0) {
                        JSONArray goodses = ans.getJSONArray(Constance.goods_groups).getJSONObject(0).getJSONArray(Constance.goods);
                        scGoods = new ArrayList<>();
                        for(int i=0;i<goodses.length();i++){
                            try {
                            scGoods.add(new Gson().fromJson(goodses.getJSONObject(i).toString(),ScGoods.class));
                            }catch (Exception e){
                                ScGoods temp=new ScGoods();
                                temp.setId(goodses.getJSONObject(i).getInt(Constance.id));
                                temp.setAmount(goodses.getJSONObject(i).getInt(Constance.amount));
                                temp.setProperty(goodses.getJSONObject(i).getString(Constance.property));
                                temp.setAttrs(goodses.getJSONObject(i).getString(Constance.attrs));

                                Product product=new Product();
                                JSONObject productObject=goodses.getJSONObject(i).getJSONObject(Constance.product);
                                product.setName(productObject.getString(Constance.name));
                                product.setId(Integer.parseInt(productObject.getString(Constance.id)));
                                product.setDefault_photo(new Gson().fromJson(productObject.getJSONObject(Constance.default_photo).toString(), Default_photo.class));
                                product.setCurrent_price(Double.parseDouble(productObject.getString(Constance.current_price)));
                                product.setWarn_number(productObject.getInt(Constance.warn_number));
                                List<Properties> properties=new ArrayList<>();
                                JSONArray propertiesArray=productObject.getJSONArray(Constance.properties);
                                for(int y=0;y<propertiesArray.length();y++){
                                    properties.add(new Gson().fromJson(propertiesArray.getJSONObject(y).toString(),Properties.class));
                                }
                                product.setProperties(properties);
//                                double currentPrice=UIUtils.getScCurrentPrice(temp.getProperty(),product.getProperties());
//                                product.setCurrent_price(currentPrice);
                                String price=UIUtils.getScCurrentPrice(temp.getAttrs(),properties)+"";
                                String img= UIUtils.getScCurrentImg(temp.getAttrs(),properties);
                                if(TextUtils.isEmpty(img)||"null".equals(img)||img==null){
                                    img=product.getDefault_photo().getThumb();
                                }
                                temp.setImg(img);
                                if(TextUtils.isEmpty(price)){
                                    price=goodses.getJSONObject(i).getJSONObject(Constance.product).getString(Constance.price);
                                }
                                temp.setPrice(Double.parseDouble(price));
                                product.setCurrent_price(Double.parseDouble(price));
                                temp.setProduct(product);
                                scGoods.add(temp);
                            }
                        }
                        for(ScGoods temp:scGoods){
                            List<Properties> properties=temp.getProduct().getProperties();
                            String price=UIUtils.getScCurrentPrice(temp.getAttrs(),properties)+"";
                            String img= UIUtils.getScCurrentImg(temp.getAttrs(),properties);
                            if(TextUtils.isEmpty(img)||"null".equals(img)||img==null){
                                img=temp.getProduct().getDefault_photo().getThumb();
                            }
                            temp.setImg(img);
                            if(!TextUtils.isEmpty(price)){
                                temp.setPrice(Double.parseDouble(price));
                                temp.getProduct().setCurrent_price(Double.parseDouble(price));
                            }
                        }
                        myAdapter.addIsCheckAll(false);
                        myAdapter.notifyDataSetChanged();
                        myAdapter.getTotalMoney();
                        IssueApplication.mCartCount = goodses.length();
                    } else {
//                        goodses = null;
                        scGoods=new ArrayList<>();
                        myAdapter.notifyDataSetChanged();
                        myAdapter.getTotalMoney();
                        IssueApplication.mCartCount = 0;
                        null_view.setVisibility(View.VISIBLE);
                    }
                    EventBus.getDefault().post(Constance.CARTCOUNT);

                    isStart = true;
                    break;
                case NetWorkConst.CONSIGNEELIST:
                    JSONArray consigneeList = ans.getJSONArray(Constance.consignees);
                    if (consigneeList.length() == 0)
                        return;
                    mAddressObject = consigneeList.getJSONObject(0);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    @Override
    public void onFailureListener(String requestCode, JSONObject ans) {
        isLastDelete = false;
        mView.hideLoading();
        mView.showContentView();
        if (AppUtils.isEmpty(ans)) {
            AppDialog.messageBox(UIUtils.getString(R.string.server_error));
            return;
        }
        AppDialog.messageBox(ans.getString(Constance.error_desc));
        getOutLogin(mView.getActivity(), ans);
    }


//    private List<Integer> buyNum = new ArrayList<>();

    private ArrayList<Boolean> isCheckList = new ArrayList<>();

    public void setCkeckAll(Boolean isCheck) {
        myAdapter.setIsCheckAll(isCheck);
        myAdapter.getTotalMoney();
        myAdapter.notifyDataSetChanged();

    }


    /**
     * 结算/删除
     */
    public void sendSettle() {
        if (!isEdit) {
            myAdapter.getCartGoodsCheck();
            if (goods.size()== 0) {
                MyToast.show(mView.getActivity(), "请选择产品");
                return;
            }

            Intent intent = new Intent(mView.getActivity(), ConfirmOrderActivity.class);
            JSONArray goodsTemp=new JSONArray();
            for(int i=0;i<goods.size();i++){
                JSONObject jsonObject= new JSONObject(new Gson().toJson(goods.get(i),ScGoods.class));
                goodsTemp.add(jsonObject);
            }
            intent.putExtra(Constance.goods, goodsTemp);
            intent.putExtra(Constance.money, mMoney);
            intent.putExtra(Constance.address, mAddressObject);

            mView.getActivity().startActivity(intent);
        } else {
            sendDeleteCart();
        }
    }

    private Boolean isLastDelete = false;

    /**
     * 删除购物车数据
     */
    public void sendDeleteCart() {
        if (isCheckList.size() == 0) {
            MyToast.show(mView.getActivity(), "请选择产品");
            return;
        }
        mView.setShowDialog(true);
        mView.setShowDialog("正在删除");
        mView.showLoading();
        ArrayList<String> deleteList = new ArrayList<>();
        for (int i = 0; i < isCheckList.size(); i++) {
            if (isCheckList.get(i)) {
                String id =scGoods.get(i).getId()+"";
                deleteList.add(id);
            }
        }
        for (int j = 0; j < deleteList.size(); j++) {
            if (j == deleteList.size() - 1) {
                isLastDelete = true;
            }
//            mDeleteIndex=j;
            deleteShoppingCart(deleteList.get(j));
        }
    }

    float mMoney = 0;

    /**
     * 获取收货地址
     */
    public void sendAddressList() {
        mNetWork.sendAddressList(this);
    }

    /**
     * 导出清单
     */
    public void exportData() {
        myAdapter.getCartGoodsCheck();
        if (goods.size() == 0) {
            MyToast.show(mView.getActivity(), "请选择产品");
            return;
        }
        Intent intent = new Intent(mView.getActivity(), ExInventoryActivity.class);
        JSONArray goodsTemp=new JSONArray();
        for(int i=0;i<goods.size();i++){
            JSONObject jsonObject= new JSONObject(new Gson().toJson(goods.get(i),ScGoods.class));
            goodsTemp.add(jsonObject);
        }
        intent.putExtra(Constance.goods, goodsTemp);
        mView.getActivity().startActivity(intent);
    }


    private class MyAdapter extends BaseAdapter {
        private DisplayImageOptions options;
        private ImageLoader imageLoader;

        public MyAdapter() {
            options = new DisplayImageOptions.Builder()
                    // 设置图片下载期间显示的图片
                    .showImageOnLoading(R.drawable.bg_default)
                    // 设置图片Uri为空或是错误的时候显示的图片
                    .showImageForEmptyUri(R.drawable.bg_default)
                    // 设置图片加载或解码过程中发生错误显示的图片
                    // .showImageOnFail(R.drawable.ic_error)
                    // 设置下载的图片是否缓存在内存中
                    .cacheInMemory(true)
                    // 设置下载的图片是否缓存在SD卡中
                    .cacheOnDisk(true)
                    // .displayer(new RoundedBitmapDisplayer(20)) // 设置成圆角图片
                    // 是否考虑JPEG图像EXIF参数（旋转，翻转）
                    .considerExifParams(true)
                    .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)// 设置图片可以放大（要填满ImageView必须配置memoryCacheExtraOptions大于Imageview）
                    // .displayer(new FadeInBitmapDisplayer(100))//
                    // 图片加载好后渐入的动画时间
                    .build(); // 构建完成

            // 得到ImageLoader的实例(使用的单例模式)
            imageLoader = ImageLoader.getInstance();
        }

        public void setIsCheckAll(Boolean isCheck) {
            if (AppUtils.isEmpty(scGoods)||scGoods.size()==0) return;
            for (int i = 0; i < scGoods.size(); i++) {
                isCheckList.set(i, isCheck);
            }
        }

        public void addIsCheckAll(Boolean isCheck) {
            isCheckList = new ArrayList<>();
            for (int i = 0; i < scGoods.size(); i++) {
                isCheckList.add(isCheck);
            }

        }

        public void getCartGoodsCheck() {
            if (scGoods.size()!= isCheckList.size()) return;
            goods = new ArrayList<>();
            for (int i = 0; i < isCheckList.size(); i++) {
                if (isCheckList.get(i)) {
                    goods.add(scGoods.get(i));
                }
            }
        }


        public void setIsCheck(int poistion, Boolean isCheck) {
            if (isCheckList.size() <= poistion) return;
            isCheckList.set(poistion, isCheck);
            getTotalMoney();


        }

        /**
         * 获取到总金额
         */
        public void getTotalMoney() {
            float isSumMoney = 0;
            int count = 0;
            if (AppUtils.isEmpty(scGoods)||scGoods.size()==0) {
                checkAll.setChecked(false);
                money_tv.setText("￥" + 0 + "");
                num_tv.setText(0 + "件");
                return;
            }
            for (int i = 0; i < scGoods.size(); i++) {
                if (isCheckList.get(i) == true) {
                    double price = scGoods.get(i).getPrice();
                    int num = scGoods.get(i).getAmount();
                    isSumMoney += (num * price);
                    count += num;
                }
            }
            mMoney = isSumMoney;

            num_tv.setText(count + "件");
            money_tv.setText("￥" + isSumMoney + "");
        }

        @Override
        public int getCount() {
            if (null == scGoods)
                return 0;
            return scGoods.size();
        }


        @Override
        public ScGoods getItem(int position) {
            if (null == scGoods)
                return null;
            return scGoods.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(mView.getActivity(), R.layout.item_lv_cart, null);

                holder = new ViewHolder();
                holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkbox);
                holder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
                holder.leftTv = (ImageView) convertView.findViewById(R.id.leftTv);
                holder.rightTv = (ImageView) convertView.findViewById(R.id.rightTv);
                holder.nameTv = (TextView) convertView.findViewById(R.id.nameTv);
                holder.contact_service_tv = (TextView) convertView.findViewById(R.id.contact_service_tv);
                holder.SpecificationsTv = (TextView) convertView.findViewById(R.id.SpecificationsTv);
                holder.numTv = (EditText) convertView.findViewById(R.id.numTv);
                holder.priceTv = (TextView) convertView.findViewById(R.id.priceTv);
                holder.old_priceTv = (TextView) convertView.findViewById(R.id.old_priceTv);
                holder.number_input_et = (NumberInputView) convertView.findViewById(R.id.number_input_et);
//                //取得设置好的drawable对象
//                Drawable drawable = mView.getResources().getDrawable(R.drawable.selector_checkbox03);
//                //设置drawable对象的大小
//                drawable.setBounds(0, 0, 80, 80);
//                //设置CheckBox对象的位置，对应为左、上、右、下
//                holder.checkBox.setCompoundDrawables(drawable,null,null,null);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
//            final JSONObject goodsObject = goodses.getJSONObject(position);
            holder.nameTv.setText(scGoods.get(position).getProduct().getName());
            try {
                String img=scGoods.get(position).getImg();
                if(!img.contains("http"))img=NetWorkConst.SCENE_HOST+img;
                imageLoader.displayImage(img
                        , holder.imageView, options);
            } catch (Exception e) {
                e.printStackTrace();
            }


            String property = scGoods.get(position).getProperty();
            if (AppUtils.isEmpty(property)) {
                holder.SpecificationsTv.setVisibility(View.GONE);
            } else {
                holder.SpecificationsTv.setVisibility(View.VISIBLE);
            }
            holder.SpecificationsTv.setText(property);
            String price =scGoods.get(position).getPrice()+"'";
            holder.priceTv.setText("优惠价:" + price + "元");
//            String oldPrice = goodsObject.getJSONObject(Constance.product).getString(Constance.current_price);
            int warn_number=scGoods.get(position).getProduct().getWarn_number();
            if(warn_number==0)warn_number=1;
            final int finalWarn_number = warn_number;
//            holder.old_priceTv.setText("零售价:" + oldPrice + "元");
            holder.number_input_et.setMax(10000);//设置数量的最大值

            holder.numTv.setText(scGoods.get(position).getAmount() + "");
            holder.numTv.setFocusable(false);
            holder.numTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mAlertViewExt == null) {
                        mAlertViewExt = new AlertView("提示", "修改购买数量！", "取消", null, new String[]{"完成"},
                                mView.getActivity(), AlertView.Style.Alert, new OnItemClickListener() {
                            @Override
                            public void onItemClick(Object o, int position) {
                                if (position != AlertView.CANCELPOSITION) {
                                    String num = etNum.getText().toString();
                                    if (num.equals("0")) {
                                        MyToast.show(mView.getActivity(), "不能等于0");
                                        return;
                                    }
                                    if(Integer.parseInt(num)%finalWarn_number!=0){
                                        MyToast.show(mView.getActivity(),"购买数量必须为"+finalWarn_number+"的倍数");
                                        return;
                                    }
                                    mView.setShowDialog(true);
                                    mView.setShowDialog("正在处理中...");
                                    mView.showLoading();
                                    sendUpdateCart(scGoods.get(position).getId()+"", num);
                                }
                            }
                        });

                        etNum.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                //输入框出来则往上移动
                                boolean isOpen = imm.isActive();
                                mAlertViewExt.setMarginBottom(isOpen && hasFocus ? 120 : 0);
                                System.out.println(isOpen);
                            }
                        });
                        mAlertViewExt.addExtView(mExtView);
                    }
                    etNum.setText(scGoods.get(position).getAmount() + "");
                    mAlertViewExt.show();

                }
            });

            holder.checkBox.setChecked(isCheckList.get(position));


            holder.rightTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mView.setShowDialog(true);
                    mView.setShowDialog("正在处理中...");
                    mView.showLoading();
                    sendUpdateCart(scGoods.get(position).getId()+"", (scGoods.get(position).getAmount()+ finalWarn_number) + "");
                }
            });
            holder.leftTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (scGoods.get(position).getAmount()== finalWarn_number) {
                        MyToast.show(mView.getActivity(), "亲,已经到底啦!");
                        return;
                    }
                    mView.setShowDialog(true);
                    mView.setShowDialog("正在处理中...");
                    mView.showLoading();
                    sendUpdateCart(scGoods.get(position).getId()+"", (scGoods.get(position).getAmount()- finalWarn_number) + "");
                }
            });


            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    setIsCheck(position, isChecked);
                    getTotalMoney();

                }
            });
            holder.contact_service_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String id = IssueApplication.mUserObject.getString(Constance.id);
                    SdkRunningClient.getInstance().setUserUid(id);
                    Intent it = new Intent(v.getContext(), InfoSubmitActivity.class);
                    mView.startActivity(it);
                }
            });

            return convertView;
        }

        class ViewHolder {
            CheckBox checkBox;
            ImageView imageView;
            TextView nameTv;
            TextView priceTv;
            TextView SpecificationsTv;
            NumberInputView number_input_et;
            EditText numTv;
            ImageView leftTv, rightTv;
            TextView contact_service_tv;
            TextView old_priceTv;
        }
    }

    private AlertView mAlertViewExt;//窗口拓展例子
    private EditText etNum;//拓展View内容
    private InputMethodManager imm;
    private ViewGroup mExtView;

}
