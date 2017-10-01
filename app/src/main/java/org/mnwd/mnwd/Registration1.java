package org.mnwd.mnwd;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.HashMap;

public class Registration1 extends AppCompatActivity {
    public String result = "";
    TextInputEditText editAccountNo1, editAccountNo2, editAccountNo3;
    Button btnNext;

    //SESSION
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration1);

        editAccountNo1 = (TextInputEditText) findViewById(R.id.idEditAccountNo1);
        editAccountNo2 = (TextInputEditText) findViewById(R.id.idEditAccountNo2);
        editAccountNo3 = (TextInputEditText) findViewById(R.id.idEditAccountNo3);

        editAccountNo1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                int inputLength = editAccountNo1.getText().length();

                if (inputLength == 3) {
                    editAccountNo2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        editAccountNo2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                int inputLength = editAccountNo2.getText().length();

                if (inputLength == 2) {
                    editAccountNo3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        btnNext = (Button) findViewById(R.id.idBtnNext);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoReg2();
            }
        });

        sharedpreferences = getSharedPreferences(Config.FILENAME_SESSION, Context.MODE_PRIVATE);
    }

    private void gotoReg2 () {
        if (TextUtils.isEmpty(editAccountNo1.getText().toString().trim())
                || TextUtils.isEmpty(editAccountNo2.getText().toString().trim())
                || TextUtils.isEmpty(editAccountNo3.getText().toString().trim())) {
            if (TextUtils.isEmpty(editAccountNo1.getText().toString().trim())) {
                editAccountNo1.setError("This can't be blank");
            }
            if (TextUtils.isEmpty(editAccountNo2.getText().toString().trim())) {
                editAccountNo2.setError("This can't be blank");
            }
            if (TextUtils.isEmpty(editAccountNo3.getText().toString().trim())) {
                editAccountNo3.setError("This can't be blank");
            }
        }
        else {
            final String accountNo1 = editAccountNo1.getText().toString().trim();
            final String accountNo2 = editAccountNo2.getText().toString().trim();
            final String accountNo3 = editAccountNo3.getText().toString().trim();
            final String accountNo = accountNo1 + "-" + accountNo2 + "-" + accountNo3;

            class FindAccountNo extends AsyncTask<Void, Void, String> {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);

                    String conn_success = "connection success~";
                    if (s.contains(conn_success)) {
                        String reg1_msg = s.replaceAll(conn_success, "");
                        String reg1_success = "Account ID: ";
                        if (reg1_msg.contains(reg1_success)) {
                            String id = reg1_msg.replaceAll(reg1_success, "");

                            //store session in sys/data/data/<package name>/shared preferences
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString(Config.SESSION_ACCOUNTID, id);
                            editor.apply();

                            Intent startIntent = new Intent(getApplicationContext(), Registration2.class);
                            startActivity(startIntent);
                        }
                        else {
                            Toast.makeText (Registration1.this, reg1_msg, Toast.LENGTH_LONG).show();
                        }
                    }
                    else {
                        Toast.makeText (Registration1.this, s, Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                protected String doInBackground(Void... voids) {
                    //store session in sys/data/data/<package name>/shared preferences
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString(Config.SESSION_ACCOUNTNO, accountNo);
                    editor.putString(Config.SESSION_ISSIGNINGUP, "true");
                    editor.apply();

                    //argument for the php script
                    HashMap<String,String> params = new HashMap<> ();
                    params.put(Config.KEY_CON_ACCOUNTNO, accountNo);

                    RequestHandler rh = new RequestHandler();
                    result = rh.sendPostRequest(Config.URL_FINDACCOUNTNO, params);
                    return result;
                }
            }
            FindAccountNo f = new FindAccountNo();
            f.execute();
        }
    }
}