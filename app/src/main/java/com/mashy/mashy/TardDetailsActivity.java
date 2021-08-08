package com.mashy.mashy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
//import org.junit.Assert;

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
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.mashy.mashy.api.MySingleton;
import com.mashy.mashy.api.URL;
import com.mashy.mashy.util.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class TardDetailsActivity extends AppCompatActivity {

    private String TAG = "TardDetailsActivityTAG";
    int SELECT_PICTURE = 200;
    Uri selectedImageUri;
    AppCompatEditText tardDetails;
    ImageButton tardImage;
    String sessionId;
    Button sendRequest;
    Bitmap bitmap;

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
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                convertToPNG(bitmap);
                Log.i(TAG, convertToPNG(bitmap).toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (Utility.isOnline(this)){
                shipmentAdd(URL.ADD_TARD, "DDDDD");
            }else {
                Toast.makeText(this, "تأكد من اتصال الانترنت", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void init(){
        sessionId = getIntent().getStringExtra("type");
        tardDetails = findViewById(R.id.tardDetails);
        tardImage = findViewById(R.id.tardImage);
        sendRequest = findViewById(R.id.sendRequest);
    }

    private void shipmentAdd(String url, String details){
        JSONObject postData = new JSONObject();

        try {
            postData.put("details", details);
            postData.put("distance", 2);
            try {
                postData.put("image", convertToPNG(bitmap));
            } catch (IOException e) {
                e.printStackTrace();
            }
            postData.put("type", sessionId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("جاري ارسال الطرد...");
        progressDialog.show();
        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.POST, url, postData,
                        response ->{
                            try {
                                if(response.getBoolean("success")){
                                    Log.i(TAG, "Response: " + response.toString());
                                    progressDialog.dismiss();
                                }else {
                                    progressDialog.dismiss();
                                    Toast.makeText(this, "فشل ارسال الطرد1" , Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                progressDialog.dismiss();
                                e.printStackTrace();
                            }
                        },
                        error -> {
                            progressDialog.dismiss();
                            Toast.makeText(this, "فشل ارسال الطرد2" , Toast.LENGTH_SHORT).show();
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
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                    convertToPNG(bitmap);
                    Log.i(TAG, convertToPNG(bitmap).toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (null != selectedImageUri) {
                    // update the preview image in the layout
                    tardImage.setImageURI(selectedImageUri);
                }
            }

        }
    }

    public static Bitmap convertToPNG(Bitmap image) throws IOException {

        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(
                "imageFileName",  /* prefix */
                ".png",         /* suffix */
                storageDir      /* directory */
        );

        FileOutputStream outStream = null;
        try {
            outStream = new FileOutputStream(imageFile);
            image.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return BitmapFactory.decodeFile(imageFile.getAbsolutePath());
    }


}