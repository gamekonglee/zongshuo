package bc.zongshuo.com.bean;

import java.io.Serializable;

/**
 * @author: Jun
 * @date : 2017/3/2 16:46
 * @description :
 */
public class Logistics implements Serializable {
    private int id;
    private String name;
    private String address;
    private String tel;
    private String pId;
    private int isDefault;

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public int getIsDefault() {
        return isDefault;
    }

    public void setIsisDefault(int isisDefault) {
        this.isDefault = isisDefault;
    }

    @Override
    public String toString() {
        return "Logistics{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", tel='" + tel + '\'' +
                ", pId='" + pId + '\'' +
                ", isDefault=" + isDefault +
                '}';
    }
}
