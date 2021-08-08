package com.mashy.mashy;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.snackbar.Snackbar;
import com.mashy.mashy.api.MySingleton;
import com.mashy.mashy.api.URL;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

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
            Log.i(TAG, "CLICK");
            Log.i(TAG, "start");
            signUpCaptain(URL.CAPTAIN_SING_UP_URL, "hamza", "0595381955"
                    ,"password");

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

    private void checkData() throws IOException {
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
            signUpCaptain(URL.CAPTAIN_SING_UP_URL, name, phone,password);
        }
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
                    if (null != selectedLicenseUri) {
                        // update the preview image in the layout
                        imageLicense.setImageURI(selectedLicenseUri);
                    }
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

        FileOutputStream outStream = new FileOutputStream(imageFile);
        image.compress(Bitmap.CompressFormat.PNG, 100, outStream);
        outStream.flush();
        outStream.close();

        return BitmapFactory.decodeFile(imageFile.getAbsolutePath());
    }

    private Bitmap convertToBitmap(Uri imageUri) throws IOException {
        return MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
    }

}