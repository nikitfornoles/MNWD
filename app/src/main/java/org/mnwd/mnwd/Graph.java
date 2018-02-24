package org.mnwd.mnwd;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Graph extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private Toolbar toolbar = null;
    private FloatingActionButton fab;

    //GRAPH
    private Spinner spinner;
    private int pos;
    private LineChart graph1, graph2;
    private HashMap <String, String> hmapMonth;
    private ArrayList<Entry> y1Values, y2Values;

    //refresh
    private SwipeRefreshLayout swipeRefreshLayout;

    //content
    private String JSON_STRING;
    //

    //notification
    private String notif1, notif2, notif3, mixed;

    //SESSION
    private String session_accountid;
    private String session_userid;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
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
                        Intent startIntent = new Intent(getApplicationContext(), Graph.class);
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

        //GRAPH
        graph1 = (LineChart) findViewById(R.id.idGraphBillAmount);
        graph2 = (LineChart) findViewById(R.id.idGraphCubicMeterUsed);

        graph1.setDragEnabled(true);
        graph1.setScaleEnabled(true);

        graph2.setDragEnabled(true);
        graph2.setScaleEnabled(true);

        y1Values = new ArrayList<>();
        y2Values = new ArrayList<>();
        //

        //HashMap
        setHashMapValues();
        //

        //content
        getJSON();
        //

        //GRAPH
        spinner = (Spinner) findViewById(R.id.idSpinner);
        String options [] = {"Bill Amount (Php)", "Cubic Meter Used (CuM)"};
        ArrayAdapter<String> adapter = new ArrayAdapter <> (this, android.R.layout.simple_spinner_dropdown_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                        pos = position;
                        showGraph();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                    }
                }
        );
        //

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
                    Toast.makeText (Graph.this, s, Toast.LENGTH_LONG).show();
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

    private void setHashMapValues() {
        hmapMonth = new HashMap<>();
        hmapMonth.put("01", "Jan");
        hmapMonth.put("02", "Feb");
        hmapMonth.put("03", "Mar");
        hmapMonth.put("04", "Apr");
        hmapMonth.put("05", "May");
        hmapMonth.put("06", "Jun");
        hmapMonth.put("07", "Jul");
        hmapMonth.put("08", "Aug");
        hmapMonth.put("09", "Sep");
        hmapMonth.put("10", "Oct");
        hmapMonth.put("11", "Nov");
        hmapMonth.put("12", "Dec");
    }

    //content
    public class MyAxisValueFormatter implements IAxisValueFormatter {
        private String[] mValues;

        public MyAxisValueFormatter(String[] values) {
            this.mValues = values;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return mValues[(int) value];
        }
    }

    private void getBillHistory(){
        JSONObject jsonObject = null;
        String [] dateString;
        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray(Config.TAG_JSON_ARRAY);

             dateString = new String[result.length()];

            for(int i = 0; i<result.length(); i++){
                JSONObject jo = result.getJSONObject(i);
                String billingdateString = jo.getString(Config.TAG_READING_BILLINGDATE);
                String consumption = jo.getString(Config.TAG_READING_CONSUMPTION);
                String billamount = jo.getString(Config.TAG_READING_BILLAMOUNT);

                String infobits [] = billingdateString.split("-");
                String yyyy = infobits [0];
                String MM = infobits [1];
                String dd = infobits [2];

                y1Values.add(new Entry(i, Float.parseFloat(billamount)));
                y2Values.add(new Entry(i, Float.parseFloat(consumption)));
                dateString [i] = hmapMonth.get(MM) + yyyy;

            }
            LineDataSet set1 = new LineDataSet(y1Values, "Monthly Bill");
            set1.setFillAlpha(110);
            set1.setColor(Color.RED);
            set1.setLineWidth(3f);
            set1.setValueTextSize(10f);
            set1.setValueTextColor(Color.BLUE);

            ArrayList<ILineDataSet> dataSets1 = new ArrayList<>();
            dataSets1.add(set1);

            LineData data1 = new LineData(dataSets1);
            graph1.setData(data1);

            XAxis xAxis1 = graph1.getXAxis();
            xAxis1.setValueFormatter(new MyAxisValueFormatter(dateString));
            xAxis1.setGranularity(1);
            xAxis1.setPosition(XAxis.XAxisPosition.BOTTOM);

            //------------------------------------------

            LineDataSet set2 = new LineDataSet(y2Values, "Monthly Consumption");
            set2.setFillAlpha(110);
            set2.setColor(Color.RED);
            set2.setLineWidth(3f);
            set2.setValueTextSize(10f);
            set2.setValueTextColor(Color.BLUE);

            ArrayList<ILineDataSet> dataSets2 = new ArrayList<>();
            dataSets2.add(set2);

            LineData data2 = new LineData(dataSets2);
            graph2.setData(data2);

            XAxis xAxis2 = graph2.getXAxis();
            xAxis2.setValueFormatter(new MyAxisValueFormatter(dateString));
            xAxis2.setGranularity(1);
            xAxis2.setPosition(XAxis.XAxisPosition.BOTTOM);

        } catch (JSONException e) {
            e.printStackTrace();
        }
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
                    getBillHistory();
                }
                else {
                    Toast.makeText (Graph.this, s, Toast.LENGTH_LONG).show();
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
                String s = rh.sendPostRequest(Config.URL_GRAPH, parameter);
                return s;
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }
    //

    private void showGraph () {
        if (pos == 0) {
            graph1.setVisibility(View.VISIBLE);
            graph2.setVisibility(View.GONE);
        }
        else {
            graph2.setVisibility(View.VISIBLE);
            graph1.setVisibility(View.GONE);
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
        Intent intent = ReusableFunctions.navigateOptions(id, Graph.this, editor);
        startActivity(intent);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}