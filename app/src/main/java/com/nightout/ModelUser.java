package com.nightout;

public class ModelUser {



    private String name;
    private String email;
    private boolean following;

    public ModelUser(String name, String email, boolean following) {
        this.name = name;
        this.email = email;
        this.following = following;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isFollowing() {
        return following;
    }

    public void setFollowing(boolean following) {
        this.following = following;
    }
}
