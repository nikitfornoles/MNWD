package org.mnwd.mnwd;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private Toolbar toolbar = null;
    private FloatingActionButton fab;

    private String billingmonth, billingyear, billingdate, previous_reading, present_reading, consumption;
    private String billamount, duedate, disconnection_date, refno, previous_billingdate;
    private double bill_with_penalty;

    private TextView txtBillingMonth, txtBill, txtDuedate, txtDisconnectionDate, txtBillWithPenalty;

    //refresh
    private SwipeRefreshLayout swipeRefreshLayout;

    //content
    private String JSON_STRING;

    //notification
    private String notif1, notif2, notif3, mixed;

    //SESSION
    private String session_accountid;
    private String session_userid;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //refresh
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.idSwipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.swipe1, R.color.swipe2, R.color.swipe3);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        Intent startIntent = new Intent(getApplicationContext(), Home.class);
                        startActivity(startIntent);
                    }
                }, 1000);
            }
        });

        //notif
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(getApplicationContext(), Notification.class);
                mixed = notif1 + "~" + notif2 + "~" + notif3;
                startIntent.putExtra("mixed", mixed);
                startActivity(startIntent);
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

        //notification
        checkNotification ();
    }

    //notification
    private void showNotificationIcon () {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray(Config.TAG_JSON_ARRAY);

            JSONObject jo = result.getJSONObject(0);
            notif1 = jo.getString("notif1");
            notif2 = jo.getString("notif2");
            notif3 = jo.getString("notif3");

            if (notif1 != "-1" || notif2 != "-1" || notif3 != "-1") {
                fab.setVisibility(View.VISIBLE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void checkNotification () {
        class CheckNotification extends AsyncTask<Void,Void,String> {

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
                    showNotificationIcon();
                }
                else {
                    Toast.makeText (Home.this, s, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            protected String doInBackground(Void... params) {
                //RETRIEVE SESSION DATA
                session_accountid = sharedPreferences.getString(Config.SESSION_ACCOUNTID, null);
                session_userid = sharedPreferences.getString(Config.SESSION_USERID, null);

                //argument for the php script
                HashMap<String,String> parameter = new HashMap<> ();
                parameter.put(Config.KEY_CON_ACCOUNTID, session_accountid);
                parameter.put(Config.KEY_CON_USERID, session_userid);

                RequestHandler rh = new RequestHandler();
                String s = rh.sendPostRequest(Config.URL_CHECKNOTIFICATION, parameter);
                return s;
            }
        }
        CheckNotification cn = new CheckNotification();
        cn.execute();
    }
    //

    private void showCurrentBill () {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray(Config.TAG_JSON_ARRAY);

            JSONObject jo = result.getJSONObject(0);
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