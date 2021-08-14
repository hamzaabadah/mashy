package com.mashy.mashy;


import java.util.ArrayList;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

interface ApiConfig {
    @Multipart
    @POST("api/shipment/add")
    Call<Request> uploadFile(@Part MultipartBody.Part file,
                             @Part("details") RequestBody details,
                             @Part("distance") RequestBody distance,
                             @Part("type") RequestBody type,
                             @Header("Authorization") String token);

    @Multipart
    @POST("api/captin/signup")
    Call<CaptainRequest> singUpCaptain(@Part MultipartBody.Part nId,
                                       @Part MultipartBody.Part drLicense,
                                       @Part("name") RequestBody name,
                                       @Part("phone") RequestBody phone,
                                       @Part("password") RequestBody password);
}
//
//    @Part MultipartBody.Part file1,
//    @Part MultipartBody.Part file2,