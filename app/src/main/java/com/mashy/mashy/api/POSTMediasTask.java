package com.mashy.mashy.api;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.request.SimpleMultiPartRequest;
import com.android.volley.toolbox.Volley;

public class POSTMediasTask {
    public void uploadMedia(final Context context, String filePath) {

        SimpleMultiPartRequest multiPartRequestWithParams = new SimpleMultiPartRequest(Request.Method.POST,
                URL.ADD_TARD,
                response -> {
                    Log.d("Response", response);
                    // TODO: Do something on success
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO: Handle your error here
            }
        });

        multiPartRequestWithParams.addStringParam("details", "details");
        multiPartRequestWithParams.addStringParam("distance", "2");
        // Add the file here
        multiPartRequestWithParams.addFile("image", filePath);
        multiPartRequestWithParams.addStringParam("type", "carton");


        // Add the params here

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(multiPartRequestWithParams);
    }
}