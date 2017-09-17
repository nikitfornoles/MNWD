package org.mnwd.mnwd;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ChooseAccount extends AppCompatActivity {
    private Spinner spinnerLoginAccount;
    private Button btnLoginAccount;

    private String accountno, accountid, result;
    private String arrAccountNo[], arrAccountID[];

    //content
    private String JSON_STRING;
    //

    //SESSION
    private String session_userid;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_account);

        spinnerLoginAccount = (Spinner) findViewById(R.id.idSpinnerLoginAccount);
        btnLoginAccount = (Button) findViewById(R.id.idBtnLoginAccount);
        btnLoginAccount.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        goToHome();
                    }
                }
        );

        //SESSION
        sharedPreferences = getSharedPreferences(Config.FILENAME_SESSION, Context.MODE_PRIVATE);

        //content
        getJSON();
        //
    }

    //content
    private void showLoginAccountOptions(){
        JSONObject jsonObject = null;
        ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String, String>>();
        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray(Config.TAG_JSON_ARRAY);

            arrAccountNo = new String [result.length()];
            arrAccountID = new String[result.length()];
            for(int i = 0; i < result.length(); i++){
                JSONObject jo = result.getJSONObject(i);
                String accountnumber = jo.getString(Config.TAG_ACCOUNT_ACCOUNTNO);
                String id = jo.getString(Config.TAG_ACCOUNT_ACCOUNTID);

                arrAccountNo[i] = accountnumber;
                arrAccountID[i] = id;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        ArrayAdapter<String> arrayAdapterAccountNo = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, arrAccountNo);
        spinnerLoginAccount = (Spinner) findViewById (R.id.idSpinnerLoginAccount);
        spinnerLoginAccount.setAdapter(arrayAdapterAccountNo);
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
                    showLoginAccountOptions();
                }
                else {
                    Toast.makeText (ChooseAccount.this, s, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            protected String doInBackground(Void... params) {
                //RETRIEVE SESSION DATA
                session_userid = sharedPreferences.getString(Config.SESSION_USERID, null);

                //argument for the php script
                HashMap<String,String> parameter = new HashMap<> ();
                parameter.put(Config.KEY_CON_USERID, session_userid);

                RequestHandler rh = new RequestHandler();
                String s = rh.sendPostRequest(Config.URL_GETALLLOGINOPTIONS, parameter);
                return s;
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }
    //

    private void goToHome() {
        accountno = String.valueOf(spinnerLoginAccount.getSelectedItem());

        int index = spinnerLoginAccount.getSelectedItemPosition();
        accountid = arrAccountID[index];

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Config.SESSION_ACCOUNTNO, accountno);
        editor.putString(Config.SESSION_ACCOUNTID, accountid);
        editor.apply();

        Intent startIntent = new Intent(getApplicationContext(), Home.class);
        startActivity(startIntent);
    }
}