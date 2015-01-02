
package cn.com.nd.momo.api.types;

import cn.com.nd.momo.api.util.Utils;

public class Address {

    private long identifier; // 用于排序

    private String label = ""; // 标签，家庭or工作

    private String street = ""; // 街道

    private String city = ""; // 城市

    private String state = ""; // 省份

    private String postalCode = ""; // 邮政编码

    private String country = ""; // 国家

    public Address() {
        this.identifier = 0;
        this.street = "";
        this.city = "";
        this.state = "";
        this.postalCode = "";
        this.country = "";
    }

    public Address(long identifier, String label, String street, String city,
            String state, String postalCode, String country) {
        super();
        this.identifier = identifier;
        this.label = label;
        this.street = street;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.country = country;
    }

    public long getIdentifier() {
        return identifier;
    }

    public void setIdentifier(long identifier) {
        this.identifier = identifier;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Utils.setStringToBlankIfNull(label))
                .append(Utils.setStringToBlankIfNull(street))
                .append(Utils.setStringToBlankIfNull(city))
                .append(Utils.setStringToBlankIfNull(state))
                .append(Utils.setStringToBlankIfNull(postalCode))
                .append(Utils.setStringToBlankIfNull(country));
        return sb.toString();
    }

}
