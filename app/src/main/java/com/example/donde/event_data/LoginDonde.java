//package com.example.donde.event_data;
//
//import android.app.ProgressDialog;
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.StringRequest;
//import com.android.volley.toolbox.Volley;
//import com.example.donde.R;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//public class LoginDonde extends AppCompatActivity {
//    TextView registerUser;
//    EditText username, password;
//    Button loginButton;
//    String user, pass;
//    Button autofill;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login_donde);
//
//        registerUser = findViewById(R.id.register);
//        autofill = findViewById(R.id.autofill);
//        username = findViewById(R.id.username);
//        password = findViewById(R.id.password);
//        loginButton = findViewById(R.id.loginButton);
//
//        autofill.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.e("press","press");
//                user = "alonem";
//                pass = "alon1607";
//                loginButton.callOnClick();
//            }
//        });
//
//        registerUser.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(LoginDonde.this, Register.class));
//            }
//        });
//
//        loginButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                user = username.getText().toString();
////                pass = password.getText().toString();
//
////                if (user.equals("")) {
////                    username.setError("can't be blank");
////                } else if (pass.equals("")) {
////                    password.setError("can't be blank");
//                if (false) {
//                } else {
//                    String url = "https://donde-4cda4.firebaseio.com/users.json";
//                    final ProgressDialog pd = new ProgressDialog(LoginDonde.this);
//                    pd.setMessage("Loading...");
//                    pd.show();
//
//                    StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String s) {
//                            if (s.equals("null")) {
//                                Toast.makeText(LoginDonde.this, "user not found", Toast.LENGTH_LONG).show();
//                            } else {
//                                try {
//                                    JSONObject obj = new JSONObject(s);
//
//                                    if (!obj.has(user)) {
//                                        Toast.makeText(LoginDonde.this, "user not found", Toast.LENGTH_LONG).show();
//                                    } else if (obj.getJSONObject(user).getString("password").equals(pass)) {
//                                        UserDetails.username = user;
//                                        UserDetails.password = pass;
//                                        startActivity(new Intent(LoginDonde.this, Events.class));
//                                    } else {
//                                        Toast.makeText(LoginDonde.this, "incorrect password", Toast.LENGTH_LONG).show();
//                                    }
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//
//                            pd.dismiss();
//                        }
//                    }, new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError volleyError) {
//                            System.out.println("" + volleyError);
//                            pd.dismiss();
//                        }
//                    });
//
//                    RequestQueue rQueue = Volley.newRequestQueue(LoginDonde.this);
//                    rQueue.add(request);
//                }
//
//            }
//        });
//    }
//}