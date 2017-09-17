package org.mnwd.mnwd;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.HashMap;

public class Registration2 extends AppCompatActivity {
    private Spinner spinnerMonth, spinnerDay, spinnerYear;
    private EditText editBillAmount;
    private Button btnNext;

    private String result;
    private String month, day, year, monthno;
    private String billingdate, billamount;

    //SESSION
    private String session_accountid;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration2);

        editBillAmount = (EditText) findViewById(R.id.idEditBillAmount);
        spinnerMonth = (Spinner) findViewById(R.id.idSpinnerMonth);
        spinnerDay = (Spinner) findViewById(R.id.idSpinnerDay);
        spinnerYear = (Spinner) findViewById(R.id.idSpinnerYear);

        sp = getSharedPreferences(Config.FILENAME_SESSION, Context.MODE_PRIVATE);

        btnNext = (Button) findViewById(R.id.idBtnNextReg2);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoReg3();
            }
        });
    }

    private void gotoReg3 () {
        month = String.valueOf(spinnerMonth.getSelectedItem());
        day = String.valueOf(spinnerDay.getSelectedItem());
        year = String.valueOf(spinnerYear.getSelectedItem());
        billamount = editBillAmount.getText().toString().trim();

        int i = spinnerMonth.getSelectedItemPosition() + 1;
        monthno = Integer.toString(i);

        if (i < 10) {
            monthno = "0" + monthno;
        }
        billingdate = year + "-" + monthno + "-" + day;

        class FindBillDetails extends AsyncTask<Void, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                String conn_success = "connection success~";
                if (s.contains(conn_success)) {
                    String reg2_msg = s.replaceAll(conn_success, "");
                    String reg2_success = "account billing details found";
                    if (reg2_msg.equals(reg2_success)) {
                        Toast.makeText (Registration2.this, reg2_success, Toast.LENGTH_LONG).show();
                        Intent startIntent = new Intent(getApplicationContext(), Registration3.class);
                        startActivity(startIntent);
                    }
                    else {
                        Toast.makeText (Registration2.this, reg2_msg, Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    Toast.makeText (Registration2.this, s, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                //RETRIEVE SESSION DATA
                session_accountid = sp.getString(Config.SESSION_ACCOUNTID, null);

                //argument for the php script
                HashMap<String,String> params = new HashMap<> ();
                params.put(Config.KEY_CON_ACCOUNTID, session_accountid);
                params.put(Config.KEY_READING_BILLAMOUNT, billamount);
                params.put(Config.KEY_READING_BILLINGDATE, billingdate);

                RequestHandler rh = new RequestHandler();
                result = rh.sendPostRequest(Config.URL_FINDBILLINGDETAILS, params);
                return result;
            }
        }

        FindBillDetails f = new FindBillDetails();
        f.execute();
    }
}