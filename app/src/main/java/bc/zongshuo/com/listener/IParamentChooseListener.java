package bc.zongshuo.com.listener;

/**
 * @author: Jun
 * @date : 2017/2/21 11:18
 * @description :
 */
public interface IParamentChooseListener {
    public void onParamentChanged(String text,Boolean isGoCart,String property,String propertyId,String inventoryNum,int mount,int price,int goType,String url);
}
