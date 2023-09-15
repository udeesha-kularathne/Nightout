package com.nightout.models;

public class ModelPost {
    //use same name as we given while uploading post
    String pId, pTitle, pDescr, pImage, pTime, uid, uEMail, uDp, uName, pGroup, verified, gCategory;

    public ModelPost() {

    }

    public ModelPost(String pId, String pTitle, String pDescr, String pImage, String pTime,
                     String uid, String uEMail, String uDp, String uName, String pGroup,
                     String verified, String gCategory) {
        this.pId = pId;
        this.pTitle = pTitle;
        this.pDescr = pDescr;
        this.pImage = pImage;
        this.pTime = pTime;
        this.uid = uid;
        this.uEMail = uEMail;
        this.uDp = uDp;
        this.uName = uName;
        this.pGroup = pGroup;
        this.verified = verified;
        this.gCategory = gCategory;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getpTitle() {
        return pTitle;
    }

    public void setpTitle(String pTitle) {
        this.pTitle = pTitle;
    }

    public String getpDescr() {
        return pDescr;
    }

    public void setpDescr(String pDescr) {
        this.pDescr = pDescr;
    }

    public String getpImage() {
        return pImage;
    }

    public void setpImage(String pImage) {
        this.pImage = pImage;
    }

    public String getpTime() {
        return pTime;
    }

    public void setpTime(String pTime) {
        this.pTime = pTime;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getuEMail() {
        return uEMail;
    }

    public void setuEMail(String uEMail) {
        this.uEMail = uEMail;
    }

    public String getuDp() {
        return uDp;
    }

    public void setuDp(String uDp) {
        this.uDp = uDp;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }

    public String getpGroup() {
        return pGroup;
    }

    public void setpGroup(String pGroup) {
        this.pGroup = pGroup;
    }

    public String getVerified() {
        return verified;
    }

    public void setVerified(String verified) {
        this.verified = verified;
    }

    public String getgCategory() {
        return gCategory;
    }

    public void setgCategory(String gCategory) {
        this.gCategory = gCategory;
    }
}
