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
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/*
home icon: home
<div>
	Icons made by <a href="https://www.flaticon.com/authors/dave-gandy" title="Dave Gandy">Dave Gandy</a> from
	<a href="https://www.flaticon.com/" title="Flaticon">www.flaticon.com</a> is licensed by
	<a href="http://creativecommons.org/licenses/by/3.0/" title="Creative Commons BY 3.0" target="_blank">CC 3.0 BY</a>
</div>
*/

/*
logout icon: logout
<div>
	Icons made by <a href="https://www.flaticon.com/authors/google" title="Google">Google</a>
	from <a href="https://www.flaticon.com/" title="Flaticon">www.flaticon.com</a> is licensed by
	<a href="http://creativecommons.org/licenses/by/3.0/" title="Creative Commons BY 3.0" target="_blank">CC 3.0 BY</a>
</div>
*/

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private Toolbar toolbar = null;

    private String billingmonth, billingyear, billingdate, previous_reading, present_reading, consumption;
    private String billamount, duedate, disconnection_date, refno, previous_billingdate;
    private double bill_with_penalty;

    private TextView txtBillingMonth, txtBill, txtDuedate, txtDisconnectionDate, txtBillWithPenalty;

    //content
    private String JSON_STRING;

    //SESSION
    private String session_accountid;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).
                        setAction("Action", null).show();
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

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        ReusableFunctions.changeNavDrawerTitle (navigationView, sharedPreferences);

        txtBillingMonth = (TextView) findViewById(R.id.idTxtBillingMonth);
        txtBill = (TextView) findViewById(R.id.idTxtBill);
        txtDuedate = (TextView) findViewById(R.id.idTxtDuedate);
        txtDisconnectionDate = (TextView) findViewById(R.id.idTxtDisconnectionDate);
        txtBillWithPenalty = (TextView) findViewById(R.id.idTxtBillWithPenalty);

        getJSON();
    }

    private void showCurrentBill () {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray(Config.TAG_JSON_ARRAY);

            for(int i = 0; i<result.length(); i++){
                JSONObject jo = result.getJSONObject(i);
                billingmonth = jo.getString(Config.TAG_READING_BILLINGMONTH);
                billingyear = jo.getString(Config.TAG_READING_BILLINGYEAR);
                billingdate = jo.getString(Config.TAG_READING_BILLINGDATE);
                previous_reading = jo.getString(Config.TAG_READING_PREVIOUS);
                present_reading = jo.getString(Config.TAG_READING_PRESENT);
                consumption = jo.getString(Config.TAG_READING_CONSUMPTION);
                billamount = jo.getString(Config.TAG_READING_BILLAMOUNT);
                duedate = jo.getString(Config.TAG_READING_DUEDATE);
                disconnection_date = jo.getString(Config.TAG_READING_DISCONNECTIONDATE);
                refno = jo.getString(Config.TAG_READING_REFNO);
                previous_billingdate = jo.getString(Config.TAG_READING_PREVIOUSBILLINGDATE);
            }

            bill_with_penalty = Double.parseDouble(billamount);
            bill_with_penalty = bill_with_penalty + (bill_with_penalty * 0.1);

            txtBillingMonth.setText("For the Month of " + billingmonth + " " + billingyear);
            txtBill.setText("P " + billamount);
            txtDuedate.setText(duedate);
            txtDisconnectionDate.setText(disconnection_date);
            txtBillWithPenalty.setText(String.format("P %,.2f", bill_with_penalty));


        } catch (JSONException e) {
            e.printStackTrace();
        }
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
                    showCurrentBill();
                }
                else {
                    Toast.makeText (Home.this, s, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            protected String doInBackground(Void... params) {
                //RETRIEVE SESSION DATA
                session_accountid = sharedPreferences.getString(Config.SESSION_ACCOUNTID, null);

                //argument for the php script
                HashMap<String,String> parameter = new HashMap<> ();
                parameter.put(Config.KEY_CON_ACCOUNTID, session_accountid);

                RequestHandler rh = new RequestHandler();
                String s = rh.sendPostRequest(Config.URL_GETLATESTBILL, parameter);
                return s;
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
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

        SharedPreferences.Editor editor = sharedPreferences.edit();
        Intent intent = ReusableFunctions.navigateOptions(id, Home.this, editor);
        startActivity(intent);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}