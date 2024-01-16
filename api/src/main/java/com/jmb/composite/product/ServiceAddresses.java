package com.jmb.composite.product;

public class ServiceAddresses {
    private String cmp;
    private String pro;
    private String rev;
    private String rec;

    public ServiceAddresses() {}

    public ServiceAddresses(String compositeAddress, String productAddress, String reviewAddress, String recommendationAddress) {
        this.cmp = compositeAddress;
        this.pro = productAddress;
        this.rev = reviewAddress;
        this.rec = recommendationAddress;
    }

    public String getCmp() {
        return cmp;
    }

    public String getPro() {
        return pro;
    }

    public String getRev() {
        return rev;
    }

    public String getRec() {
        return rec;
    }
}
