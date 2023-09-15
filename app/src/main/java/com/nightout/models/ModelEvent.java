package com.nightout.models;

public class ModelEvent {
    //use same name as we given while uploading post
    String eId, eTitle, eDescr, eImage, eTime, eInOrOut, date, time,
    eDayorNight, location, uid, uEMail, uDp, uName, eGroup, verified, interested, going, participated;

    public ModelEvent() {

    }


    public ModelEvent(String eId, String eTitle, String eDescr, String eImage, String eTime,
                      String uid, String uEMail, String uDp, String uName, String eGroup,
                      String verified, String interested, String going, String participated,
                      String eInOrOut, String eDayorNight, String date, String time, String location) {
        this.eId = eId;
        this.eTitle = eTitle;
        this.eDescr = eDescr;
        this.eImage = eImage;
        this.eTime = eTime;
        this.uid = uid;
        this.uEMail = uEMail;
        this.uDp = uDp;
        this.uName = uName;
        this.eGroup = eGroup;
        this.verified = verified;
        this.interested = interested;
        this.going = going;
        this.participated = participated;
        this.eDayorNight = eDayorNight;
        this.eInOrOut = eInOrOut;
        this.location = location;
        this.date = date;
        this.time = time;


    }

    public String geteId() {
        return eId;
    }

    public void seteId(String eId) {
        this.eId = eId;
    }

    public String geteTitle() {
        return eTitle;
    }

    public void seteTitle(String eTitle) {
        this.eTitle = eTitle;
    }

    public String geteDescr() {
        return eDescr;
    }

    public void seteDescr(String eDescr) {
        this.eDescr = eDescr;
    }

    public String geteImage() {
        return eImage;
    }

    public void seteImage(String eImage) {
        this.eImage = eImage;
    }

    public String geteTime() {
        return eTime;
    }

    public void seteTime(String eTime) {
        this.eTime = eTime;
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

    public String geteGroup() {
        return eGroup;
    }

    public void seteGroup(String eGroup) {
        this.eGroup = eGroup;
    }

    public String getVerified() {
        return verified;
    }

    public void setVerified(String verified) {
        this.verified = verified;
    }

    public String getInterested() {
        return interested;
    }

    public void setInterested(String interested) {
        this.interested = interested;
    }

    public String getGoing() {
        return going;
    }

    public void setGoing(String going) {
        this.going = going;
    }

    public String getParticipated() {
        return participated;
    }

    public void setParticipated(String participated) {
        this.participated = participated;
    }

    public String geteInOrOut() {
        return eInOrOut;
    }

    public void seteInOrOut(String eInOrOut) {
        this.eInOrOut = eInOrOut;
    }

    public String geteDayorNight() {
        return eDayorNight;
    }

    public void seteDayorNight(String eDayorNight) {
        this.eDayorNight = eDayorNight;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}