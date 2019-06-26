package com.eHospital.bean;

public class DataBean {
    private String id;
    private String ownerString;
    private String issuerString;
    private String nameOfCompany;
    private String currency;
    private Integer nominalValue;
    private Integer faceValue;

    private String shareId;
    private String delegate;
    private String type;
    private Integer quantity;
    private Integer price;
    private String timeStamp;

    private String counterParty;

    public String getId() {
        return id;
    }

    public String getOwnerString() {
        return ownerString;
    }

    public String getIssuerString() {
        return issuerString;
    }

    public String getNameOfCompany() {
        return nameOfCompany;
    }

    public String getCurrency() {
        return currency;
    }

    public Integer getNominalValue() {
        return nominalValue;
    }

    public Integer getFaceValue() {
        return faceValue;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setOwnerString(String ownerString) {
        this.ownerString = ownerString;
    }

    public void setIssuerString(String issuerString) {
        this.issuerString = issuerString;
    }

    public void setNameOfCompany(String nameOfCompany) {
        this.nameOfCompany = nameOfCompany;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setNominalValue(Integer nominalValue) {
        this.nominalValue = nominalValue;
    }

    public void setFaceValue(Integer faceValue) {
        this.faceValue = faceValue;
    }

    public String getShareId() {
        return shareId;
    }

    public String getDelegate() {
        return delegate;
    }

    public String getType() {
        return type;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Integer getPrice() {
        return price;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setShareId(String shareId) {
        this.shareId = shareId;
    }

    public void setDelegate(String delegate) {
        this.delegate = delegate;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getCounterParty() {
        return counterParty;
    }

    public void setCounterParty(String counterParty) {
        this.counterParty = counterParty;
    }
}
