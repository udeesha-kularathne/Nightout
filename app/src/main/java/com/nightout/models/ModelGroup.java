package com.nightout.models;

public class ModelGroup {

    String gName, gDescrp, gCategory, image, uid;

    public ModelGroup() {

    }

    public ModelGroup(String gName, String gDescrp, String gCategory, String image, String uid) {
        this.gName = gName;
        this.gDescrp = gDescrp;
        this.gCategory = gCategory;
        this.image = image;
        this.uid = uid;
    }

    public String getgName() {
        return gName;
    }

    public void setgName(String gName) {
        this.gName = gName;
    }

    public String getgDescrp() {
        return gDescrp;
    }

    public void setgDescrp(String gDescrp) {
        this.gDescrp = gDescrp;
    }

    public String getgCategory() {
        return gCategory;
    }

    public void setgCategory(String gCategory) {
        this.gCategory = gCategory;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
