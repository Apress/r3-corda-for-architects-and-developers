package com.landRegistry.bean;

public class PropertyDetailsBean {
    private int propertyId;
    private String propertyAddress;
    private int propertyPrice;
    private int buyerId;
    private int sellerId;
    private String updatedBy;
    private String updatedDateTime;

    private String ownerString;

    public String getOwnerString() {
        return ownerString;
    }

    public int getPropertyId() {
        return propertyId;
    }

    public String getPropertyAddress() {
        return propertyAddress;
    }

    public int getPropertyPrice() {
        return propertyPrice;
    }

    public int getBuyerId() {
        return buyerId;
    }

    public int getSellerId() {
        return sellerId;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public String getUpdatedDateTime() {
        return updatedDateTime;
    }

    public void setOwnerString(String ownerString) {
        this.ownerString = ownerString;
    }

    public void setPropertyId(int propertyId) {
        this.propertyId = propertyId;
    }

    public void setPropertyAddress(String propertyAddress) {
        this.propertyAddress = propertyAddress;
    }

    public void setPropertyPrice(int propertyPrice) {
        this.propertyPrice = propertyPrice;
    }

    public void setBuyerId(int buyerId) {
        this.buyerId = buyerId;
    }

    public void setSellerId(int sellerId) {
        this.sellerId = sellerId;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public void setUpdatedDateTime(String updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }
}
