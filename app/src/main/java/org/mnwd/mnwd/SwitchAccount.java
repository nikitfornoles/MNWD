package org.mnwd.mnwd;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/*
switch icon: swap-horizontal-orientation-arrows
<div>
	Icons made by <a href="https://www.flaticon.com/authors/google" title="Google">Google</a>
	from <a href="https://www.flaticon.com/" title="Flaticon">www.flaticon.com</a> is licensed by
	<a href="http://creativecommons.org/licenses/by/3.0/" title="Creative Commons BY 3.0" target="_blank">CC 3.0 BY</a>
</div>
*/

public class SwitchAccount extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private Toolbar toolbar = null;

    private TextView txtSwitchInfo1, txtSwitchInfo3;
    private Spinner spinnerSwitchAccountNo;
    private Button btnSwitchAccount;

    private String accountno, accountid, result;
    private String arrAccountNo[], arrAccountID[];

    //content
    private String JSON_STRING;
    //

    //SESSION
    private String session_userid, session_accountid;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch_account);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //SESSION
        sharedPreferences = getSharedPreferences(Config.FILENAME_SESSION, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        ReusableFunctions.changeNavDrawerTitle (navigationView, sharedPreferences);

        txtSwitchInfo1 = (TextView) findViewById(R.id.idTxtSwitchInfo1);
        txtSwitchInfo3 = (TextView) findViewById(R.id.idTxtSwitchInfo3);
        spinnerSwitchAccountNo = (Spinner) findViewById(R.id.idSpinnerSwitchAccountNo);

        btnSwitchAccount = (Button) findViewById(R.id.idBtnSwitchAccount);
        btnSwitchAccount.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        switchAccount();
                    }
                }
        );

        //content
        getJSON();
        //
    }

    //content
    private void showAllActivatedAccounts() {
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
        spinnerSwitchAccountNo = (Spinner) findViewById(R.id.idSpinnerSwitchAccountNo);
        spinnerSwitchAccountNo.setAdapter(arrayAdapterAccountNo);
    }

    private void getJSON(){
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
                    String switch404 = "~";
                    if (JSON_STRING.contains(switch404)) {
                        txtSwitchInfo3.setText(JSON_STRING.replaceAll(switch404, ""));
                    }
                    else {
                        txtSwitchInfo1.setVisibility(View.VISIBLE);
                        spinnerSwitchAccountNo.setVisibility(View.VISIBLE);
                        btnSwitchAccount.setVisibility(View.VISIBLE);
                        showAllActivatedAccounts();
                        txtSwitchInfo3.setVisibility(View.GONE);
                    }
                }
                else {
                    Toast.makeText (SwitchAccount.this, s, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            protected String doInBackground(Void... params) {
                //RETRIEVE SESSION DATA
                session_userid = sharedPreferences.getString(Config.SESSION_USERID, null);
                session_accountid = sharedPreferences.getString(Config.SESSION_ACCOUNTID, null);

                //argument for the php script
                HashMap<String,String> parameter = new HashMap<> ();
                parameter.put(Config.KEY_CON_USERID, session_userid);
                parameter.put(Config.KEY_CON_ACCOUNTID, session_accountid);

                RequestHandler rh = new RequestHandler();
                String s = rh.sendPostRequest(Config.URL_GETALLACTIVATEDACCOUNTS, parameter);
                return s;
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }
    //

    private void switchAccount() {
        accountno = String.valueOf(spinnerSwitchAccountNo.getSelectedItem());

        int index = spinnerSwitchAccountNo.getSelectedItemPosition();
        accountid = arrAccountID[index];

        editor.putString(Config.SESSION_ACCOUNTNO, accountno);
        editor.putString(Config.SESSION_ACCOUNTID, accountid);
        editor.apply();

        Toast.makeText (SwitchAccount.this, "Account switched to " + accountno, Toast.LENGTH_LONG).show();
        Intent startIntent = new Intent(getApplicationContext(), Home.class);
        startActivity(startIntent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Intent intent = ReusableFunctions.navigateOptions(id, SwitchAccount.this, editor);
        startActivity(intent);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}