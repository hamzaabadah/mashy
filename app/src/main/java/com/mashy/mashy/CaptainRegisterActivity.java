package com.mashy.mashy;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.snackbar.Snackbar;
import com.mashy.mashy.api.DataPart;
import com.mashy.mashy.api.MySingleton;
import com.mashy.mashy.api.URL;
import com.mashy.mashy.api.VolleyMultipartRequest;
import com.mashy.mashy.util.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CaptainRegisterActivity extends AppCompatActivity {

    private String TAG = "CaptainRegisterActivityTAG";
    AppCompatEditText captainName,captainPhone, captainPassword;
    ImageButton idImage, imageLicense;
    CheckBox checkbox;
    Button captainRegister;
    TextView loginCaptain;
    int SELECT_PICTURE = 200;
    int pickID = 0;

    Uri selectedIdUri, selectedLicenseUri;
    File idFile , licenceFile;
    View contextView ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_captain_register);
        init();
        loginCaptain.setOnClickListener(v ->{
            Intent myIntent = new Intent(CaptainRegisterActivity.this,
                    LoginActivity.class);
            CaptainRegisterActivity.this.startActivity(myIntent);
        });

        idImage.setOnClickListener(v->{
            pickID = 1;
            imageChooser();
        });
        imageLicense.setOnClickListener(v->{
            pickID = 2;
            imageChooser();
        });

        captainRegister.setOnClickListener(v->{
            if (Utility.isOnline(this)){
                checkData();

            }else {
                Snackbar.make(contextView, "بالرجاء التأكد من اتصال الانترنت", Snackbar.LENGTH_SHORT)
                        .show();
            }
            //singCaptain("hamza", "7845963214", "password");

        });
    }

    private void init(){
        contextView = findViewById(android.R.id.content);
        captainName = findViewById(R.id.captainName);
        captainPhone = findViewById(R.id.captainPhone);
        captainPassword = findViewById(R.id.captainPassword);
        idImage = findViewById(R.id.idImage);
        imageLicense = findViewById(R.id.imageLicense);
        checkbox = findViewById(R.id.checkbox);
        captainRegister = findViewById(R.id.captainRegister);
        loginCaptain = findViewById(R.id.loginCaptain);
    }

    private void checkData() {
        String name = Objects.requireNonNull(captainName.getText()).toString();
        String phone = Objects.requireNonNull(captainPhone.getText()).toString();
        String password = Objects.requireNonNull(captainPassword.getText()).toString();

        if (name.isEmpty()){
            captainName.setError("بالرجاء ادخال الاسم");
        } else if (phone.isEmpty()){
            captainPhone.setError("بالرجاء ادخال رقم الهاتف");
        }
        else if (password.isEmpty()){
            captainPassword.setError("بالرجاء ادخال كلمة المرور");
        }else if (Uri.EMPTY.equals(selectedIdUri)){
            Snackbar.make(contextView, "بالرجاء تحديد صورة الهوية", Snackbar.LENGTH_SHORT)
                    .show();
        }else if (Uri.EMPTY.equals(selectedLicenseUri)){
            Snackbar.make(contextView, "بالرجاء تحديد صورة الرخصة", Snackbar.LENGTH_SHORT)
                    .show();
        }
        else {
            singCaptain(name,phone,password);
        }
    }

    private void singCaptain(String name, String phone, String password){
        Log.i(TAG, "ID IMAGE " + idFile);
        Log.i(TAG, "LICENCE IMAGE : "+ licenceFile);
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("جاري ارسال الطرد...");

        ApiConfig apiConfig=AppConfig.getRetrofit().create(ApiConfig.class);
        MediaType mediaType =MediaType.parse("multipart/from-data");
        RequestBody requestBodyName= RequestBody.create(mediaType,name);
        RequestBody requestBodyPhone= RequestBody.create(mediaType,phone);
        RequestBody requestBodyPassword= RequestBody.create(mediaType,password);

        RequestBody requestBodyFile1 = RequestBody.create(mediaType,idFile);
        MultipartBody.Part filePart1 =
                MultipartBody.Part.createFormData("nId",idFile.getName(),requestBodyFile1);

        RequestBody requestBodyFile2 = RequestBody.create(mediaType,idFile);
        MultipartBody.Part filePart2 =
                MultipartBody.Part.createFormData("drLicense",licenceFile.getName(),requestBodyFile2);
        progressDialog.show();
        Log.i(TAG,"file 1 : "+ filePart1);
        Log.i(TAG,"file 2 : "+ filePart2);
        apiConfig.singUpCaptain( filePart1,filePart2,requestBodyName,requestBodyPhone,requestBodyPassword)
                .enqueue(new Callback<CaptainRequest>() {
                    @Override
                    public void onResponse(Call<CaptainRequest> call,
                                           Response<CaptainRequest> response) {
                        Log.d(TAG, "onResponse: "+ 200);
                        Log.d(TAG, "onResponse: "+ response.toString());
                        Log.d(TAG, "onResponse: "+ response.message());
                        Log.d(TAG, "onResponse: "+ response.body().success);
                        progressDialog.dismiss();
                        if (response.body().success){
                            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            String token = "Bearer "+ response.body().token;
                            editor.putString("token", token);
                            Log.i(TAG, "TOKEN IS : "+ token);
                            editor.apply();
                            Intent myIntent = new Intent(CaptainRegisterActivity.this,
                                    MainActivity.class);
                            CaptainRegisterActivity.this.startActivity(myIntent);
                            finishAffinity();
                            Snackbar.make(contextView, "تم انشاء الحساب بنجاح", Snackbar.LENGTH_SHORT)
                                    .show();
                            progressDialog.dismiss();
                            Log.d(TAG, "onResponse: "+ response.body().toString());

                        }else {
                            progressDialog.dismiss();
                            Snackbar.make(contextView, "فشل انشاء الحساب", Snackbar.LENGTH_SHORT)
                                    .show();
                            Log.d(TAG, "onResponse: "+ response.body().toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<CaptainRequest> call, Throwable t) {
                        Log.d(TAG, "onFailure: "+t.getMessage());
                        Snackbar.make(contextView, "فشل انشاء الحساب", Snackbar.LENGTH_SHORT)
                                .show();
                        progressDialog.dismiss();
                    }
                });
    }

    private void signUpCaptain(String url, String name, String phone, String password) {
        JSONObject postData = new JSONObject();
        Log.i(TAG, "1");
        try {
            Log.i(TAG, "2");
            postData.put("name", name);
            postData.put("phone", phone);
            postData.put("password", password);
            postData.put("nId", new File(selectedIdUri.getPath()));
            postData.put("drLicense", new File(selectedLicenseUri.getPath()));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.POST, url, postData,
                        response ->{
                            Log.i(TAG, "Response: " + response.toString());
                            try {
                                Log.i(TAG, "3");
                                if(response.getBoolean("success")){
                                    Log.i(TAG, "Response: " + response.toString());
                                    SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPref.edit();
                                    String token = "Bearer "+ response.getString("token");
                                    editor.putString("token", token);
                                    editor.apply();
                                    Intent myIntent = new Intent(CaptainRegisterActivity.this,
                                            MainActivity.class);
                                    CaptainRegisterActivity.this.startActivity(myIntent);
                                    finish();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        },
                        error -> {
                            Log.i(TAG, "4");
                            Log.i(TAG, "Error: " + error.toString());
                            Log.i(TAG, url);
                            // TODO: Handle error
                        }
                );

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
            if (pickID == 1){
                if (requestCode == SELECT_PICTURE) {
                    // Get the url of the image from data
                    selectedIdUri = data.getData();
                    idFile = copyFileToInternalStorage(selectedIdUri, "image");
                    Log.i(TAG, "ID IMAGE "+ idFile);
                    if (null != selectedIdUri) {
                        // update the preview image in the layout
                        idImage.setImageURI(selectedIdUri);
                    }
                }
            }
            if (pickID == 2){
                if (requestCode == SELECT_PICTURE) {
                    // Get the url of the image from data
                    selectedLicenseUri = data.getData();
                    licenceFile = copyFileToInternalStorage(selectedLicenseUri, "image");
                    Log.i(TAG, "LICENCE IMAGE "+ licenceFile);
                    if (null != selectedLicenseUri) {
                        // update the preview image in the layout
                        imageLicense.setImageURI(selectedLicenseUri);
                    }
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


    private void addCaptain(String name, String phone, String password){
        HashMap<String, String> postData = new HashMap<String, String>();

        postData.put("name", name);
        postData.put("phone", phone);
        postData.put("password", password);
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("جاري انشاء الحساب...");
        progressDialog.show();
        VolleyMultipartRequest volleyMultipartRequest =
                new VolleyMultipartRequest(Request.Method.POST, URL.CAPTAIN_SING_UP_URL,
                response -> {
                    try {
                        Log.i(TAG, response.toString());
                        JSONObject obj = new JSONObject(new String(response.data));
                        Toast.makeText(getApplicationContext(), obj.getString("message"),
                                Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                    }
                },


                error -> {
                    progressDialog.dismiss();
                    Log.i(TAG, "error "+error.toString());
                    Log.i("GotError", "DDDD" + error.toString());
                    //Log.e("GotError", "DDDD" + Arrays.toString(error.networkResponse.data));


                    runOnUiThread(() -> {
                        Log.i(TAG, "error On thried"+error.toString());
                        error.printStackTrace();
                        Log.e("GotError", "" + error.getMessage());
                        progressDialog.dismiss();
//                                Toast.makeText(TardDetailsActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();

                    });
                }) {


            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                long imagename1 = System.currentTimeMillis();
                params.put("nId", new DataPart(imagename + ".jpg", getBytesOfFile(idFile)));
                params.put("drLicense", new DataPart(imagename1 + ".jpg", getBytesOfFile(licenceFile)));
                Log.i(TAG, "getByteData: "+ params);
                return params;
            }

            @Nullable
//                @org.jetbrains.annotations.Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Log.i(TAG, "getParams: "+ postData);
                return postData;
            }

        };

        Log.i(TAG, "REQUEST"+ volleyMultipartRequest.toString());
        MySingleton.getInstance(this).addToRequestQueue(volleyMultipartRequest);
    }

    private byte[] getBytesOfFile(File file) {
        int size = (int) file.length();
        byte[] bytes = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bytes;
    }
}