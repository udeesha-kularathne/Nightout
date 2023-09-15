package com.nightout.notification;

public class Token {
    /*An FCM Token, or much commonly known as a registration Token
    * AN ID issued by the GCM connection servers to the client app that allows it to receive messages*/

    String Token;

    public Token(String token) {
        this.Token = token;
    }

    public Token() {
    }

    public String getToken() {
        return Token;
    }

    public void setToken(String token) {
        this.Token = token;
    }
}
