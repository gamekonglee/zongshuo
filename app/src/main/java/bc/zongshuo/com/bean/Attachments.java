/**
 * Copyright 2018 bejson.com
 */
package bc.zongshuo.com.bean;
import java.util.List;

/**
 * Auto-generated: 2018-04-10 15:7:50
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class Attachments {

    private boolean is_multiselect;
    private List<Attrs> attrs;
    private int id;
    private String name;
    public void setIs_multiselect(boolean is_multiselect) {
        this.is_multiselect = is_multiselect;
    }
    public boolean getIs_multiselect() {
        return is_multiselect;
    }

    public void setAttrs(List<Attrs> attrs) {
        this.attrs = attrs;
    }
    public List<Attrs> getAttrs() {
        return attrs;
    }

    public void setId(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

}