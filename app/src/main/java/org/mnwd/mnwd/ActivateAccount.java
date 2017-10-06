package org.mnwd.mnwd;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class ActivateAccount extends AppCompatActivity {

    private TextView txtAccountNo;
    private TextInputEditText editActivationCode;
    private String passedValue, result;
    private Button btnActivate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activate_account);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        passedValue = getIntent().getExtras().getString(Config.ACCOUNT_NO);

        txtAccountNo = (TextView) findViewById(R.id.idTxtAccountNo);
        txtAccountNo.setText("Account Number: " + passedValue);
        editActivationCode = (TextInputEditText) findViewById(R.id.idEditActivationCode);
        btnActivate = (Button) findViewById(R.id.idBtnActivate);
        btnActivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activateAccount();
            }
        });
    }

    private void activateAccount() {
        if (TextUtils.isEmpty(editActivationCode.getText().toString().trim())) {
            editActivationCode.setError("Activation code can't be empty");
        }
        else {
            final String activation_code = editActivationCode.getText().toString().trim();
            class ActivateAccountNo extends AsyncTask<Void, Void, String> {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);

                    String conn_success = "connection success~";
                    if (s.contains(conn_success)) {
                        String activate_status = s.replaceAll(conn_success, "");
                        String activate_sucess = "Account successfully activated.";
                        if (activate_status.contains(activate_sucess)) {
                            Toast.makeText (ActivateAccount.this, activate_status, Toast.LENGTH_LONG).show();
                            Intent startIntent = new Intent(getApplicationContext(), GetActivationCode.class);
                            startActivity(startIntent);
                        }
                        else {
                            Toast.makeText (ActivateAccount.this, activate_status, Toast.LENGTH_LONG).show();
                        }
                    }
                    else {
                        Toast.makeText (ActivateAccount.this, s, Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                protected String doInBackground(Void... voids) {
                    HashMap<String,String> params = new HashMap<> ();
                    params.put(Config.KEY_ACC_ACTIVATION_CODE, activation_code);
                    params.put(Config.KEY_CON_ACCOUNTNO, passedValue);


                    RequestHandler rh = new RequestHandler();
                    result = rh.sendPostRequest(Config.URL_ACTIVATEACCOUNT, params);
                    return result;
                }
            }
            ActivateAccountNo a = new ActivateAccountNo();
            a.execute();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        this.finish();
        return super.onOptionsItemSelected(item);
    }
}
