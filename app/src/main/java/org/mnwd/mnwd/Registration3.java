package org.mnwd.mnwd;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.HashMap;

public class Registration3 extends AppCompatActivity {
    private TextInputEditText editEmailAddress;
    private Button btnNext;
    private ProgressBar progressBar;
    private String email, result;

    //SESSION
    private String session_accountid;
    private SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration3);

        editEmailAddress = (TextInputEditText) findViewById(R.id.idEditEmailAdReg3);
        sharedpreferences = getSharedPreferences(Config.FILENAME_SESSION, Context.MODE_PRIVATE);
        progressBar = (ProgressBar) findViewById(R.id.idProgressBar);
        progressBar.setVisibility(View.GONE);
        btnNext = (Button) findViewById(R.id.idBtnNextReg3);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoRegMessage();
            }
        });
    }

    private void gotoRegMessage () {
        if (TextUtils.isEmpty(editEmailAddress.getText().toString().trim())) {
            editEmailAddress.setError("Email address can't be blank");
        }
        else {
            email = editEmailAddress.getText().toString().trim();

            class SetEmailAddress extends AsyncTask<Void, Void, String> {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);

                    progressBar.setVisibility(View.GONE);
                    String conn_success = "connection success~";
                    if (s.contains(conn_success)) {
                        String reg3_msg = s.replaceAll(conn_success, "");
                        String reg3_success = "Password successfully sent!";
                        if (reg3_msg.contains(reg3_success)) {
                            Toast.makeText (Registration3.this, reg3_success, Toast.LENGTH_LONG).show();

                            //store session in sys/data/data/<package name>/shared preferences
                            String userid = reg3_msg.replaceAll(reg3_success, "");
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString(Config.SESSION_USERID, userid);
                            editor.putString(Config.SESSION_EMAIL, email);
                            editor.apply();

                            Intent startIntent = new Intent(getApplicationContext(), RegistrationMessage.class);
                            startActivity(startIntent);
                        }
                        else {
                            Toast.makeText (Registration3.this, reg3_msg, Toast.LENGTH_LONG).show();
                        }
                    }
                    else {
                        Toast.makeText (Registration3.this, s, Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                protected String doInBackground(Void... voids) {
                    //RETRIEVE SESSION DATA
                    session_accountid = sharedpreferences.getString(Config.SESSION_ACCOUNTID, null);

                    HashMap<String,String> params = new HashMap<> ();
                    params.put(Config.KEY_CON_ACCOUNTID, session_accountid);
                    params.put(Config.KEY_CON_EMAIL, email);

                    RequestHandler rh = new RequestHandler();
                    result = rh.sendPostRequest(Config.URL_SENDEMAIL, params);
                    return result;
                }
            }

            SetEmailAddress s = new SetEmailAddress();
            s.execute();
        }
    }
}