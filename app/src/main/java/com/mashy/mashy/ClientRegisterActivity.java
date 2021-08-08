package com.mashy.mashy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.snackbar.Snackbar;
import com.mashy.mashy.api.MySingleton;
import com.mashy.mashy.api.URL;
import com.mashy.mashy.util.Utility;

import org.json.JSONException;
import org.json.JSONObject;

public class ClientRegisterActivity extends AppCompatActivity {

    private String TAG = "ClientRegisterActivityTAG";
    AppCompatEditText clientName, clientPhone, clientPassword;
    Button clientSingUpBtn;
    TextView loginClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_register);
        init();
        loginClient.setOnClickListener(v ->{
            Intent myIntent = new Intent(ClientRegisterActivity.this,
                    LoginActivity.class);
            ClientRegisterActivity.this.startActivity(myIntent);
        });
        Log.i(TAG,"url" + URL.CLIENT_SING_UP_URL);
        //signUpClient(URL.CLIENT_SING_UP_URL);
        clientSingUpBtn.setOnClickListener(v -> {
            if (Utility.isOnline(this)){
                checkData();
            }else
                Toast.makeText(this, "تأكد من اتصال الانترنت", Toast.LENGTH_SHORT).show();
        });

    }

    private void init(){
        clientName = findViewById(R.id.clientName);
        clientPhone = findViewById(R.id.clientPhone);
        clientPassword = findViewById(R.id.clientPassword);
        clientSingUpBtn = findViewById(R.id.clientSingUpBtn);
        loginClient = findViewById(R.id.loginClient);
    }

    private void checkData(){
        String name = clientName.getText().toString();
        String phone = clientPhone.getText().toString();
        String password = clientPassword.getText().toString();

        if (name.isEmpty()){
            clientName.setError("بالرجاء ادخال الاسم");
        } else if (phone.isEmpty()){
            clientPhone.setError("بالرجاء ادخال رقم الهاتف");
        }
//        else if (phone.length() > 9){
//            clientPhone.setError("بالرجاء تعديل رقم الهاتف");
//        }
        else if (password.isEmpty()){
            clientPassword.setError("بالرجاء ادخال كلمة المرور");
        }
//        else if (password.length() > 6 ){
//            clientPassword.setError("بالرجاء ادخال 6 خانات او اكثر");
//        }
        else {
            signUpClient(URL.CLIENT_SING_UP_URL, name, phone,password);
        }
    }

    private void signUpClient(String url, String name, String phone, String password){
        JSONObject postData = new JSONObject();
        try {
            postData.put("name", name);
            postData.put("phone", phone);
            postData.put("password", password);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("جاري انشاء حساب للعميل...");
        progressDialog.show();
        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.POST, url, postData,
                response ->{
                    try {
                        if(response.getBoolean("success")){
                            Log.i(TAG, "Response: " + response.toString());
                            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            String token = "Bearer "+ response.getString("token");
                            editor.putString("token", token);
                            editor.apply();
                            Intent myIntent = new Intent(ClientRegisterActivity.this,
                                    MainActivity.class);
                            ClientRegisterActivity.this.startActivity(myIntent);
                            finishAffinity();
                            progressDialog.dismiss();
                        }
                    } catch (JSONException e) {
                        progressDialog.dismiss();
                        e.printStackTrace();
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "فشل انشاء الحساب" +
                            " \n تأكد من البيانات المدخلة", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "Error: " + error.toString());
                    // TODO: Handle error
                }
        );

        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

}