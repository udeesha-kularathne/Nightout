package com.nightout.notification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers({
            "Content-Type: application/json",
            "Authorization:key=AAAA_-tjQUE:APA91bGURA0A1tnR83e1KMSxjQL6-O2uU30tPb_wzDqMAWfKFWONQwAqaG2QXZrWZA671oAbASZ0ItP4eOWkqWELnAaA7Jk_Fu7uySI-6AsA5VJ6MZM3Msplr6Fe3JAs4T2BLIPG6DuS"
    })



    @POST("fcm/send")
    Call<Response> sendNotification(@Body Sender body);
}
