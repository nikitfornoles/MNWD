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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class Registration2 extends AppCompatActivity {
    private Spinner spinnerMonth, spinnerDay, spinnerYear;
    private TextInputEditText editReferenceNo;
    private Button btnNext;

    private String result, resultYear[];
    private String month, day, year, monthno;
    private String billingdate, referenceno;

    //content
    private String JSON_STRING;
    //

    //SESSION
    private String session_accountid;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration2);

        editReferenceNo = (TextInputEditText) findViewById(R.id.idEditReferenceNo);
        spinnerMonth = (Spinner) findViewById(R.id.idSpinnerMonth);
        spinnerDay = (Spinner) findViewById(R.id.idSpinnerDay);
        spinnerYear = (Spinner) findViewById(R.id.idSpinnerYear);

        btnNext = (Button) findViewById(R.id.idBtnNextReg2);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoReg3();
            }
        });

        //SESSION
        sharedPreferences = getSharedPreferences(Config.FILENAME_SESSION, Context.MODE_PRIVATE);

        //content
        getJSON();
    }

    //content
    private void showYear () {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray(Config.TAG_JSON_ARRAY);

            resultYear = new String [result.length()];
            for(int i = 0; i < result.length(); i++){
                JSONObject jo = result.getJSONObject(i);
                String yyyy = jo.getString(Config.TAG_READING_BILLINGYEAR);
                resultYear[i] = yyyy;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        ArrayAdapter<String> arrayAdapterYear = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, resultYear);
        spinnerYear = (Spinner) findViewById (R.id.idSpinnerYear);
        spinnerYear.setAdapter(arrayAdapterYear);
    }

    private void getJSON () {
        class GetJSON extends AsyncTask<Void,Void,String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                String conn_success = "connection success~";
                if (s.contains(conn_success)) {
                    JSON_STRING = s.replaceAll(conn_success, "");
                    showYear();
                }
                else {
                    Toast.makeText (Registration2.this, s, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                //RETRIEVE SESSION DATA
                session_accountid = sharedPreferences.getString(Config.SESSION_ACCOUNTID, null);

                //argument for the php script
                HashMap<String,String> parameter = new HashMap<> ();
                parameter.put(Config.KEY_CON_ACCOUNTID, session_accountid);

                RequestHandler rh = new RequestHandler();
                String s = rh.sendPostRequest(Config.URL_GETLATESTBILLINGYEAR, parameter);
                return s;
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }
    //

    private void gotoReg3 () {
        month = String.valueOf(spinnerMonth.getSelectedItem());
        day = String.valueOf(spinnerDay.getSelectedItem());
        year = String.valueOf(spinnerYear.getSelectedItem());

        if (TextUtils.isEmpty(editReferenceNo.getText().toString().trim())) {
            editReferenceNo.setError("Reference number can't be empty.");
        }
        else {
            referenceno = editReferenceNo.getText().toString().trim();

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
                    session_accountid = sharedPreferences.getString(Config.SESSION_ACCOUNTID, null);

                    //argument for the php script
                    HashMap<String,String> params = new HashMap<> ();
                    params.put(Config.KEY_CON_ACCOUNTID, session_accountid);
                    params.put(Config.KEY_READING_REFERENCENO, referenceno);
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
}