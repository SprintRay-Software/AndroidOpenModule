package com.sprintray.net.http.source;



import com.sprintray.net.http.model.RefreshTokenResponse;

import java.util.TreeMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 *
 */

public interface TokenApiService {

    @POST("/login/refresh")
    Call<RefreshTokenResponse> refreshToken(@Body TreeMap<String, Object> request);

}
