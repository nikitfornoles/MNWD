package org.mnwd.mnwd;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private TextInputEditText txtEmailAd,txtPassword;
    private String result = "";
    private Button loginBtn;
    private TextView txtRegistrationLink;

    //SESSION
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtEmailAd = (TextInputEditText) findViewById(R.id.idEditEmailAd);
        txtPassword = (TextInputEditText) findViewById(R.id.idEditPassword);

        loginBtn = (Button) findViewById(R.id.idBtnLogin);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signin();
            }
        });

        txtRegistrationLink = (TextView)findViewById(R.id.idRegisterLink);
        txtRegistrationLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent(getApplicationContext(), Registration1.class);
                startActivity(startIntent);
            }
        });
        sharedPreferences = getSharedPreferences(Config.FILENAME_SESSION, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    private void signin() {
        if (TextUtils.isEmpty(txtEmailAd.getText().toString().trim()) || TextUtils.isEmpty(txtPassword.getText().toString().trim())) {
            if (TextUtils.isEmpty(txtEmailAd.getText().toString().trim())) {
                txtEmailAd.setError("Email address can't be empty");
            }
            if (TextUtils.isEmpty(txtPassword.getText().toString().trim())) {
                txtPassword.setError("Password can't be empty");
            }
        }
        else {
            final String email = txtEmailAd.getText().toString().trim();
            final String password = txtPassword.getText().toString().trim();

            class SignIn extends AsyncTask<Void, Void, String> {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);

                    String conn_success = "connection success~";
                    if (s.contains(conn_success)) {
                        String login_status = s.replaceAll(conn_success, "");
                        String login_success = "login successful!";
                        if (login_status.contains(login_success)) {
                            //store session in sys/data/data/<package name>/shared preferences
                            String info = login_status.replaceAll(login_success, "");
                            String [] infobits = info.split("~");
                            String userid = infobits [0];
                            String total_account = infobits [1];
                            String total_activated_account = infobits [2];
                            String accountid = infobits [3];
                            String accountno = infobits [4];
                            String firstname = infobits [5];
                            String lastname = infobits [6];

                            editor.putString(Config.SESSION_USERID, userid);
                            editor.putString(Config.SESSION_TOTALACCOUNT, total_account);
                            editor.putString(Config.SESSION_TOTALACTIVATEDACCOUNT, total_activated_account);
                            editor.putString(Config.SESSION_FIRSTNAME, firstname);
                            editor.putString(Config.SESSION_LASTNAME, lastname);
                            editor.apply();

                            if (total_activated_account.equals("1")) {
                                editor.putString(Config.SESSION_ACCOUNTID, accountid);
                                editor.putString(Config.SESSION_ACCOUNTNO, accountno);
                                editor.apply();

                                Toast.makeText (MainActivity.this, login_success, Toast.LENGTH_LONG).show();
                                Intent startIntent = new Intent(getApplicationContext(), Home.class);
                                startActivity(startIntent);
                            }
                            else {
                                Intent startIntent = new Intent(getApplicationContext(), ChooseAccount.class);
                                startActivity(startIntent);
                            }
                        }
                        else {
                            Toast.makeText (MainActivity.this, login_status, Toast.LENGTH_LONG).show();
                        }
                    }
                    else {
                        Toast.makeText (MainActivity.this, s, Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                protected String doInBackground(Void... voids) {
                    //store session in sys/data/data/<package name>/shared preferences
                    editor.putString(Config.SESSION_EMAIL, email);
                    editor.apply();

                    HashMap<String,String> params = new HashMap<> ();
                    params.put(Config.KEY_CON_EMAIL,email);
                    params.put(Config.KEY_CON_PASSWORD,password);

                    RequestHandler rh = new RequestHandler();
                    result = rh.sendPostRequest(Config.URL_LOGIN, params);
                    return result;
                }
            }
            SignIn s = new SignIn();
            s.execute();
        }
    }
}