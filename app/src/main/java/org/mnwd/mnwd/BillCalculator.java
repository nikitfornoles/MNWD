package org.mnwd.mnwd;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputEditText;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class BillCalculator extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private Toolbar toolbar = null;
    private FloatingActionButton fab;

    private String classification, cubicMeterUsed, metersize, type = "", totalbill, discountRate, discountAmount;
    private String result;

    private Spinner spinnerAccountClassification, spinnerMeterSize;
    private TextInputEditText editCubicMeterUsed;
    private RadioGroup radioType;
    private Button btnCalculate;
    private TextView txtTotalBill, txtDiscountRate, txtDiscountAmount;

    private String [] CUSTTYPE = {"Residential", "Commercial", "Commercial A", "Commercial B", "Commercial C", "Bulk/Wholesale"};
    private String [] METERSIZE = {"1/2", "3/4", "1", "1.5", "2", "3", "4", "6", "8", "10"};

    //refresh
    private SwipeRefreshLayout swipeRefreshLayout;

    //content
    private String JSON_STRING, JSON_RESULT;

    //notification
    private String notif1, notif2, notif3, mixed;

    //SESSION
    private String session_accountid;
    private String session_userid;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_calculator);
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
                        Intent startIntent = new Intent(getApplicationContext(), BillCalculator.class);
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

        spinnerAccountClassification = (Spinner) findViewById(R.id.idSpinnerAcctClass);
        spinnerMeterSize = (Spinner) findViewById(R.id.idSpinnerMeterSize);
        editCubicMeterUsed = (TextInputEditText) findViewById(R.id.idEditCuM);
        radioType = (RadioGroup) findViewById(R.id.idRadioType);
        radioType.setOnCheckedChangeListener(
                new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                        switch (i) {
                            case R.id.idRadioSeniorCitizen:
                                type = "Senior Citizen";
                                break;
                            case R.id.idRadioRegular:
                                type = "Regular";
                                break;
                        }
                    }
                }
        );

        txtTotalBill = (TextView) findViewById(R.id.idTxtTotalBill);
        txtDiscountRate = (TextView) findViewById(R.id.idTxtDiscountRate);
        txtDiscountAmount = (TextView) findViewById(R.id.idTxtDiscountAmount);

        ArrayAdapter <String> arrayAdapterCusttype = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, CUSTTYPE);
        spinnerAccountClassification = (Spinner) findViewById(R.id.idSpinnerAcctClass);
        spinnerAccountClassification.setAdapter(arrayAdapterCusttype);

        ArrayAdapter<String> arrayAdapterMetersize=new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, METERSIZE);
        spinnerMeterSize = (Spinner) findViewById (R.id.idSpinnerMeterSize);
        spinnerMeterSize.setAdapter(arrayAdapterMetersize);

        btnCalculate = (Button) findViewById(R.id.idBtnCalculate);
        btnCalculate.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        calculateBill();
                    }
                }
        );

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
                    Toast.makeText (BillCalculator.this, s, Toast.LENGTH_LONG).show();
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

    private void displayResult () {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(JSON_RESULT);
            JSONArray result = jsonObject.getJSONArray(Config.TAG_JSON_ARRAY);

            JSONObject jo = result.getJSONObject(0);
            totalbill = jo.getString(Config.TAG_READING_BILLAMOUNT);
            discountRate = jo.getString("discount_rate");
            discountAmount = jo.getString("discount_amount");

            txtTotalBill.setText(totalbill);
            txtDiscountRate.setText(discountRate);
            txtDiscountAmount.setText(discountAmount);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void calculateBill () {
        if (TextUtils.isEmpty(editCubicMeterUsed.getText().toString().trim()) || type.isEmpty()) {
            if (TextUtils.isEmpty(editCubicMeterUsed.getText().toString().trim())) {
                editCubicMeterUsed.setError("Usage can't be empty");
            }
            if (type.isEmpty()) {
                Toast t = Toast.makeText (BillCalculator.this, "Choose Type", Toast.LENGTH_LONG);
                t.setGravity(Gravity.CENTER, 0, 0);
                t.show();
            }
            txtTotalBill.setText("");
        }
        else {
            classification = String.valueOf(spinnerAccountClassification.getSelectedItem());
            metersize = String.valueOf(spinnerMeterSize.getSelectedItem());
            cubicMeterUsed = editCubicMeterUsed.getText().toString().trim();

            class CalculateBill extends AsyncTask <Void, Void, String> {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);

                    String conn_success = "connection success~";
                    if (s.contains(conn_success)) {
                        String calculator_status = s.replaceAll(conn_success, "");
                        String calculator_success = "result";
                        if (calculator_status.contains(calculator_success)) {
                            JSON_RESULT = calculator_status;
                            displayResult();
                        }
                        else {
                            Toast.makeText (BillCalculator.this, calculator_status, Toast.LENGTH_LONG).show();
                        }
                    }
                    else {
                        Toast.makeText (BillCalculator.this, s, Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                protected String doInBackground(Void... voids) {
                    HashMap <String, String> params = new HashMap<>();
                    params.put(Config.KEY_AC_CLASSIFICATION, classification);
                    params.put(Config.KEY_READING_CUM, cubicMeterUsed);
                    params.put(Config.KEY_MS_SIZE, metersize);
                    params.put(Config.KEY_ACC_TYPE, type);

                    RequestHandler rh = new RequestHandler();
                    result = rh.sendPostRequest(Config.URL_BILLCALCULATOR, params);
                    return result;
                }
            }
            CalculateBill c = new CalculateBill();
            c.execute();
        }
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
        Intent intent = ReusableFunctions.navigateOptions(id, BillCalculator.this, editor);
        startActivity(intent);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}