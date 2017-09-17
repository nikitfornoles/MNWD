package org.mnwd.mnwd;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class RegistrationMessage extends AppCompatActivity {
    TextView txtLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_message);

        txtLogin = (TextView) findViewById(R.id.idTxtLoginLink);
        txtLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                gotologin();
            }
        });
    }

    private void gotologin () {
        //reset session
        SharedPreferences sharedpreferences = getSharedPreferences(Config.FILENAME_SESSION, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.apply();

        //redirect to login activity
        Intent startIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(startIntent);
    }
}