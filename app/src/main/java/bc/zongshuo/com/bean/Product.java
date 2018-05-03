package bc.zongshuo.com.bean;

import java.util.List;

/**
 * Created by gamekonglee on 2018/4/13.
 */

public class Product {
    public int warn_number;
    public String original_img;
    public App_img app_img;
    public int id;
    public int category;
    public Default_photo default_photo;
    public String name;
    public double price;
    public double current_price;
    public int score;
    public String good_stock;
    public List<Properties> properties;

    public int getWarn_number() {
        return warn_number;
    }

    public void setWarn_number(int warn_number) {
        this.warn_number = warn_number;
    }

    public String getOriginal_img() {
        return original_img;
    }

    public void setOriginal_img(String original_img) {
        this.original_img = original_img;
    }

    public App_img getApp_img() {
        return app_img;
    }

    public void setApp_img(App_img app_img) {
        this.app_img = app_img;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public Default_photo getDefault_photo() {
        return default_photo;
    }

    public void setDefault_photo(Default_photo default_photo) {
        this.default_photo = default_photo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getCurrent_price() {
        return current_price;
    }

    public void setCurrent_price(double current_price) {
        this.current_price = current_price;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getGood_stock() {
        return good_stock;
    }

    public void setGood_stock(String good_stock) {
        this.good_stock = good_stock;
    }

    public List<Properties> getProperties() {
        return properties;
    }

    public void setProperties(List<Properties > properties) {
        this.properties = properties;
    }
}
