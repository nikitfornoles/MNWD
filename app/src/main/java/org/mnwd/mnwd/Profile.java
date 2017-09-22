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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class Profile extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private Toolbar toolbar = null;

    private String session_firstname, session_lastname, session_email, session_userid;
    private String password_curr, password_new, password_confirm;
    private String name, result;

    private TextView txtName, txtEmail;
    private EditText editCurrPassword, editNewPassword, editConfirmPassword;
    private Button btnSubmit;

    //SESSION
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
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

        //RETRIEVE SESSION DATA
        session_email = sharedPreferences.getString(Config.SESSION_EMAIL, null);
        session_firstname = sharedPreferences.getString(Config.SESSION_FIRSTNAME, null);
        session_lastname = sharedPreferences.getString(Config.SESSION_LASTNAME, null);
        name = session_firstname + " " + session_lastname;

        txtName = (TextView) findViewById(R.id.idTxtName);
        txtEmail = (TextView) findViewById(R.id.idTxtEmail);
        txtName.setText(name);
        txtEmail.setText(session_email);

        editCurrPassword = (EditText) findViewById(R.id.idEditCurrPassword);
        editNewPassword = (EditText) findViewById(R.id.idEditNewPassword);
        editConfirmPassword = (EditText) findViewById(R.id.idEditNewPassword2);

        btnSubmit = (Button) findViewById(R.id.idBtnSubmit);
        btnSubmit.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        editPassword();
                    }
                }
        );
    }

    private void editPassword () {
        password_curr = editCurrPassword.getText().toString().trim();
        password_new = editNewPassword.getText().toString().trim();
        password_confirm = editConfirmPassword.getText().toString().trim();

        class EditPassword extends AsyncTask<Void, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                String conn_success = "connection success~";
                if (s.contains(conn_success)) {
                    String profile_status = s.replaceAll(conn_success, "");
                    String edit_success = "Password updated successfully";
                    if (profile_status.contains(edit_success)) {
                        Toast.makeText (Profile.this, edit_success, Toast.LENGTH_LONG).show();
                        editCurrPassword.setText("");
                        editNewPassword.setText("");
                        editConfirmPassword.setText("");
                    }
                    else {
                        Toast.makeText (Profile.this, profile_status, Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    Toast.makeText (Profile.this, s, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                //RETRIEVE SESSION DATA
                session_userid = sharedPreferences.getString(Config.SESSION_USERID, null);

                HashMap<String, String> params = new HashMap<>();
                params.put(Config.KEY_CON_USERID, session_userid);
                params.put(Config.KEY_CON_PASSWORD, password_curr);
                params.put(Config.KEY_CON_PASSWORD_NEW, password_new);
                params.put(Config.KEY_CON_PASSWORD_CONFIRM, password_confirm);

                RequestHandler rh = new RequestHandler();
                result = rh.sendPostRequest(Config.URL_EDITPASSWORD, params);
                return result;
            }
        }
        EditPassword e = new EditPassword();
        e.execute();
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
        Intent intent = ReusableFunctions.navigateOptions(id, Profile.this, editor);
        startActivity(intent);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}