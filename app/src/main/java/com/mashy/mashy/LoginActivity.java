package com.mashy.mashy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.mashy.mashy.api.MySingleton;
import com.mashy.mashy.api.URL;
import com.mashy.mashy.util.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private String TAG= "LoginActivityTAG";
    AppCompatEditText phoneLogin, passwordLogin;
    TextView singUp;
    Button loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
        singUp.setOnClickListener(v ->{
            Intent myIntent = new Intent(LoginActivity.this,
                    SplashActivity.class);
            LoginActivity.this.startActivity(myIntent);
            finish();
        });
        loginBtn.setOnClickListener(v->{
            if (Utility.isOnline(this)){
                checkData();
            }else
                Toast.makeText(this, "تأكد من اتصال الانترنت", Toast.LENGTH_SHORT).show();
        });
    }

    private void init(){
        singUp = findViewById(R.id.singUp);
        phoneLogin = findViewById(R.id.phoneLogin);
        passwordLogin = findViewById(R.id.passwordLogin);
        loginBtn = findViewById(R.id.loginBtn);

    }

    private void checkData(){
        String phone = phoneLogin.getText().toString();
        String password = passwordLogin.getText().toString();

        if (phone.isEmpty()){
            phoneLogin.setError("بالرجاء ادخال رقم الهاتف");
        }
//        else if (phone.length() > 9){
//            phoneLogin.setError("رقم الهاتف اقل من 9 ارقام");
//        }
        else if (password.isEmpty()){
            passwordLogin.setError("بالرجاء ادخال كلمة المرور");
        }
//        else if (password.length()>6){
//            passwordLogin.setError("كلمة المرور أقل من 6 أحرف");
//        }
        else {
            login(URL.LOGIN_URL, phone,password);
        }
    }

    private void login(String url, String phone, String password){

        String completeUrl = url+ "?phone=" + phone + "&password=" + password;
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("جاري تسجيل الدخول...");
        progressDialog.show();
        JsonObjectRequest loginRequest = new JsonObjectRequest(Request.Method.GET, completeUrl, null,
                response -> {
                    try {
                        Log.i(TAG, "Response: " + response.toString());

                        String token = "Bearer "+ response.getString("token");
                        SharedPreferences preferences = getSharedPreferences("myprefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();

                        editor.putBoolean("logged_in", true);
                        editor.putString("access_token", token);
                        editor.apply();

                        Log.i(TAG, token);
                        Intent myIntent = new Intent(LoginActivity.this,
                                MainActivity.class);
                        myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        LoginActivity.this.startActivity(myIntent);
                        finishAffinity();
                        progressDialog.dismiss();
                    } catch (JSONException e) {
                        progressDialog.dismiss();
                        e.printStackTrace();
                    }

                },
                error -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "فشل تسجيل الدخول" +
                            " \n تأكد من البيانات المدخلة", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "error: " + error.toString());
                })
        {

        };
        MySingleton.getInstance(this).addToRequestQueue(loginRequest);

    }


}