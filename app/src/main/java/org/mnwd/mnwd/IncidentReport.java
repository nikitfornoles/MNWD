package org.mnwd.mnwd;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IdRes;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.HashMap;

public class IncidentReport extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private Toolbar toolbar = null;

    private String result;

    //SESSION
    private String session_accountid;
    private SharedPreferences sharedPreferences;

    //user input
    private String incidentid = "";

    //widgets
    private RadioGroup radioIncidentType;
    private EditText editDescription;
    private Button btnReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incident_report);
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

        radioIncidentType = (RadioGroup) findViewById(R.id.idRadioType);
        radioIncidentType.setOnCheckedChangeListener(
                new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                        switch (i) {
                            case R.id.idRadioLeakage:
                                incidentid = "1";
                                break;
                            case R.id.idRadioIllegalDisconnection:
                                incidentid = "2";
                                break;
                            case R.id.idRadioDefectiveMeter:
                                incidentid = "3";
                                break;
                            case R.id.idRadioDirtyWater:
                                incidentid = "4";
                                break;
                            case R.id.idRadioOthers:
                                incidentid = "5";
                                break;
                        }
                    }
                }
        );

        editDescription = (EditText) findViewById(R.id.idEditDescription);

        btnReport = (Button) findViewById(R.id.idBtnReport);
        btnReport.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        reportIncident();
                    }
                }
        );
    }

    private void reportIncident () {
        final String incident_description = editDescription.getText().toString().trim();

        class ReportIncident extends AsyncTask <Void, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                String conn_success = "connection success~";
                if (s.contains(conn_success)) {
                    String ir_msg = s.replaceAll(conn_success, "");
                    String ir_success = "Incident successfully reported";
                    if (ir_msg.contains(ir_success)) {
                        Toast.makeText (IncidentReport.this, ir_success, Toast.LENGTH_LONG).show();
                        editDescription.setText("");
                        radioIncidentType.clearCheck();
                        incidentid = "";
                    }
                    else {
                        Toast.makeText (IncidentReport.this, ir_msg, Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    Toast.makeText (IncidentReport.this, s, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                //RETRIEVE SESSION DATA
                session_accountid = sharedPreferences.getString(Config.SESSION_ACCOUNTID, null);

                //argument for the php script
                HashMap<String,String> params = new HashMap<> ();
                params.put(Config.KEY_CON_ACCOUNTID, session_accountid);
                params.put(Config.KEY_INCIDENT_INCIDENTID, incidentid);
                params.put(Config.KEY_INCIDENT_DESCRIPTION, incident_description);

                RequestHandler rh = new RequestHandler();
                result = rh.sendPostRequest(Config.URL_REPORTINCIDENT, params);
                return result;
            }
        }

        ReportIncident r = new ReportIncident();
        r.execute();
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
        Intent intent = ReusableFunctions.navigateOptions(id, IncidentReport.this, editor);
        startActivity(intent);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}