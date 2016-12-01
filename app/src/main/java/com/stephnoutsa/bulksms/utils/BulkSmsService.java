package com.stephnoutsa.bulksms.utils;

import com.stephnoutsa.bulksms.models.Contact;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by stephnoutsa on 11/30/16.
 */

public interface BulkSmsService {
    // Start of methods for contacts
    @POST("contacts")
    Call<Contact[]> addContacts(@Body Contact[] contact);

    @GET("contacts")
    Call<Contact[]> getAllContacts();
    // End of methods for contacts
}
