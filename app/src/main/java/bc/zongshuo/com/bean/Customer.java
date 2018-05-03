package bc.zongshuo.com.bean;

/**
 * Created by XY on 2018/1/25.
 */

public class Customer {
    private String extUserId;
    private String name;
    private String level;
    private String telphone;
    private String emai;
    private String areaDetails;
    private String remark;
    private String qq;
    private String sex;
    private String age;
    private String startTime;
    private String endTime;
    private String pageSize;
    private String pageNo;

    public Customer(String extUserId,String name,String telphone,String startTime,String endTime,String pageSize,String pageNo){
        this.extUserId = extUserId;
        this.name = name;
        this.telphone = telphone;
        this.startTime = startTime;
        this.endTime = endTime;
        this.pageSize = pageSize;
        this.pageNo = pageNo;
    }
    public Customer(String extUserId,String name,String telphone){
        this.extUserId = extUserId;
        this.name = name;
        this.telphone = telphone;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getPageSize() {
        return pageSize;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

    public String getPageNo() {
        return pageNo;
    }

    public void setPageNo(String pageNo) {
        this.pageNo = pageNo;
    }

    public Customer(String extUserId, String name, String level, String telphone, String emai, String areaDetails, String remark, String qq, String sex, String age) {
        this.extUserId = extUserId;
        this.name = name;
        this.level = level;
        this.telphone = telphone;
        this.emai = emai;
        this.areaDetails = areaDetails;
        this.remark = remark;
        this.qq = qq;
        this.sex = sex;
        this.age = age;
    }

    public String getExtUserId() {
        return extUserId;
    }

    public void setExtUserId(String extUserId) {
        this.extUserId = extUserId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getTelphone() {
        return telphone;
    }

    public void setTelphone(String telphone) {
        this.telphone = telphone;
    }

    public String getEmai() {
        return emai;
    }

    public void setEmai(String emai) {
        this.emai = emai;
    }

    public String getAreaDetails() {
        return areaDetails;
    }

    public void setAreaDetails(String areaDetails) {
        this.areaDetails = areaDetails;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "extUserId='" + extUserId + '\'' +
                ", name='" + name + '\'' +
                ", level='" + level + '\'' +
                ", telphone='" + telphone + '\'' +
                ", emai='" + emai + '\'' +
                ", areaDetails='" + areaDetails + '\'' +
                ", remark='" + remark + '\'' +
                ", qq='" + qq + '\'' +
                ", sex='" + sex + '\'' +
                ", age='" + age + '\'' +
                '}';
    }
}
