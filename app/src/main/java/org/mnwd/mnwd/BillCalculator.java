package org.mnwd.mnwd;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;
import android.view.Gravity;
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
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.HashMap;

public class BillCalculator extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private Toolbar toolbar = null;

    private String classification, cubicMeterUsed, metersize, type = "", totalbill;
    private String result;

    private Spinner spinnerAccountClassification, spinnerMeterSize;
    private TextInputEditText editCubicMeterUsed;
    private RadioGroup radioType;
    private Button btnCalculate;
    private TextView txtTotalBill;

    private String [] CUSTTYPE = {"Residential", "Commercial", "Commercial A", "Commercial B", "Commercial C", "Bulk/Wholesale"};
    private String [] METERSIZE = {"1/2", "3/4", "1", "1.5", "2", "3", "4", "6", "8", "10"};

    //SESSION
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_calculator);
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
                        String calculator_success = "billamount~";
                        if (calculator_status.contains(calculator_success)) {
                            totalbill = calculator_status.replaceAll(calculator_success, "");
                            txtTotalBill.setText(totalbill);
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