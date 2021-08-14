package com.mashy.mashy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
//import org.junit.Assert;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.snackbar.Snackbar;
import com.mashy.mashy.api.MySingleton;
import com.mashy.mashy.util.Utility;

//import org.apache.http.entity.mime.content.FileBody;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TardDetailsActivity extends AppCompatActivity {

    private String TAG = "TardDetailsActivityTAG";
    int SELECT_PICTURE = 200;
    Uri selectedImageUri;
    AppCompatEditText tardDetails;
    ImageButton tardImage;
    String sessionId;
    Button sendRequest;
    Bitmap bitmap;
    File file;
    View contextView ;
    String token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tard_details);
        init();




        Log.i(TAG, sessionId);
        tardImage.setOnClickListener(v->{
            imageChooser();
        });

        sendRequest.setOnClickListener(v->{
            if (Utility.isOnline(this)){
               checkData();
            }else {
                Toast.makeText(this, "تأكد من اتصال الانترنت", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkData(){
        String details = Objects.requireNonNull(tardDetails.getText()).toString();
        if (details.isEmpty()){
            Snackbar.make(contextView, "بالرجاء ادخال تفاصيل الطرد", Snackbar.LENGTH_SHORT)
                    .show();
        }else if (Uri.EMPTY.equals(selectedImageUri)){
            Snackbar.make(contextView, "بالرجاء اختيار صورة للطرد", Snackbar.LENGTH_SHORT)
                    .show();
        }else {
            addTard(details, sessionId, token);
        }
    }
    private void addTard(String details, String type, String token){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("جاري ارسال الطرد...");


        ApiConfig apiConfig=AppConfig.getRetrofit().create(ApiConfig.class);
        MediaType mediaType =MediaType.parse("multipart/from-data");
        RequestBody requestBodyDetails= RequestBody.create(mediaType,details);
        RequestBody requestBodyDistance= RequestBody.create(mediaType,"2");
        RequestBody requestBodyType= RequestBody.create(mediaType,type);
        //convirt file to MultipartBody.Part
        RequestBody requestBodyFile= RequestBody.create(mediaType,file);
        MultipartBody.Part filePart =
                MultipartBody.Part.createFormData("image",file.getName(),requestBodyFile);
        progressDialog.show();
        apiConfig.uploadFile( filePart,requestBodyDetails,requestBodyDistance,requestBodyType, token)
                .enqueue(new Callback<com.mashy.mashy.Request>() {
                    @Override
                    public void onResponse(Call<com.mashy.mashy.Request> call,
                                           Response<com.mashy.mashy.Request> response) {
                        Log.d(TAG, "onResponse: "+ response.body().toString());
                        if (response.body().success){
                            progressDialog.dismiss();
                            Toast.makeText(TardDetailsActivity.this, "تم ارسال الطرد بنجاح" , Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onResponse: "+ response.body().toString());
                            tardDetails.setText("");
                            tardImage.setImageDrawable(null);
                        }else {
                            progressDialog.dismiss();
                            Toast.makeText(TardDetailsActivity.this, "فشل ارسال الطرد1" , Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onResponse: "+ response.body().toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<com.mashy.mashy.Request> call, Throwable t) {
                        Log.d(TAG, "onResponse: "+t.getMessage());
                        Toast.makeText(TardDetailsActivity.this, "فشل ارسال الطرد2" , Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
    }

    private void init(){
        sessionId = getIntent().getStringExtra("type");
        tardDetails = findViewById(R.id.tardDetails);
        tardImage = findViewById(R.id.tardImage);
        sendRequest = findViewById(R.id.sendRequest);
        contextView = findViewById(android.R.id.content);
        SharedPreferences preferences = getSharedPreferences("myprefs", MODE_PRIVATE);
        token = preferences.getString("access_token", "");
    }

    private void shipmentAdd(String url, String details){
        JSONObject postData = new JSONObject();

        try {
            postData.put("details", details);
            postData.put("distance", 2);
//
//            postData.put("image", new VolleyMultipartRequest.DataPart( "index.png", getFileDataFromDrawable(bitmap))
//            );
            //postData.put("image", new DataPart("file_avatar.jpg", AppHelper.getFileDataFromDrawable(getBaseContext(), mAvatarImage.getDrawable()), "image/jpeg"));
            postData.accumulate("image", file);
            postData.put("type", sessionId);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.POST, url, postData,
                        response ->{
                            try {
                                if(response.getBoolean("success")){
                                    Log.i(TAG, "Response: " + response.toString());
                                }else {
                                    Log.i(TAG, "Response: " + response.toString());
                                    Log.i(TAG, postData.toString());
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        },
                        error -> {
                            Log.i(TAG, "Error: " + error.toString());
                            // TODO: Handle error
                        }
                ){
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String>  params = new HashMap<String, String>();
                        SharedPreferences preferences = getSharedPreferences("myprefs", MODE_PRIVATE);
                        String token = preferences.getString("access_token", "");
                        Log.i(TAG, "token is : "+token);
                        params.put("Authorization", token);
                        return params;
                    }
                };

        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    void imageChooser() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                // Get the url of the image from data
                selectedImageUri = data.getData();
                file = copyFileToInternalStorage(selectedImageUri,"image");
                Log.i(TAG, file.toString());
                if (null != selectedImageUri) {
                    // update the preview image in the layout
                    tardImage.setImageURI(selectedImageUri);
                }
            }

        }
    }

    private File copyFileToInternalStorage(Uri uri,String newDirName) {
        Uri returnUri = uri;

        Cursor returnCursor = this.getContentResolver().query(returnUri, new String[]{
                OpenableColumns.DISPLAY_NAME,OpenableColumns.SIZE
        }, null, null, null);


        /*
         * Get the column indexes of the data in the Cursor,
         *     * move to the first row in the Cursor, get the data,
         *     * and display it.
         * */
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();
        String name = (returnCursor.getString(nameIndex));
        String size = (Long.toString(returnCursor.getLong(sizeIndex)));

        File output;
        if(!newDirName.equals("")) {
            File dir = new File(this.getFilesDir() + "/" + newDirName);
            if (!dir.exists()) {
                dir.mkdir();
            }
            output = new File(this.getFilesDir() + "/" + newDirName + "/" + name);
        }
        else{
            output = new File(this.getFilesDir() + "/" + name);
        }
        try {
            InputStream inputStream = this.getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(output);
            int read = 0;
            int bufferSize = 1024;
            final byte[] buffers = new byte[bufferSize];
            while ((read = inputStream.read(buffers)) != -1) {
                outputStream.write(buffers, 0, read);
            }

            inputStream.close();
            outputStream.close();

        }
        catch (Exception e) {

            Log.e("Exception", e.getMessage());
        }

        return new File(output.getPath());
    }




}