package bc.zongshuo.com.bean;

/**
 * Created by gamekonglee on 2018/4/13.
 */

public class ScGoods {
    public int id;
    public int amount;
    public String property;
    public String attr_stock;
    public String attrs;
    public Product product;
    public double price;
    public String img;

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double Price) {
        this.price = Price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getAttr_stock() {
        return attr_stock;
    }

    public void setAttr_stock(String attr_stock) {
        this.attr_stock = attr_stock;
    }

    public String getAttrs() {
        return attrs;
    }

    public void setAttrs(String attrs) {
        this.attrs = attrs;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
