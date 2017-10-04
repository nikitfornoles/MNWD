package org.mnwd.mnwd;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class Notification extends AppCompatActivity {
    private String passedValue, notif1, notif2, notif3;
    private TextView txtNotif1, txtNotif2, txtNotif3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        txtNotif1 = (TextView) findViewById(R.id.idTxtNotif1);
        txtNotif2 = (TextView) findViewById(R.id.idTxtNotif2);
        txtNotif3 = (TextView) findViewById(R.id.idTxtNotif3);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getPassedValue();
        showNotification();
    }

    private void getPassedValue () {
        passedValue = getIntent().getExtras().getString("mixed");
        String [] infobits = passedValue.split("~");
        notif1 = infobits [0];
        notif2 = infobits [1];
        notif3 = infobits [2];
    }

    private void showNotification () {
        if (notif1.equals("-1")) {
            txtNotif1.setVisibility(View.GONE);
        }
        else {
            txtNotif1.setText("You have " + notif1 + " non-activated account/s. Kindly activate it by choosing 'Activate Account' in the Menu.");
        }

        if (notif2.equals("-1")) {
            txtNotif2.setVisibility(View.GONE);
        }
        else if (notif2.equals("0")) {
            txtNotif2.setText("Today is the duedate of your bill. To avoid penalty charges, please pay at the nearest payment center near you.");
        }
        else {
            txtNotif2.setText("You only have " + notif2 + " day/s to pay your bills on time.");
        }

        if (notif3.equals("-1")) {
            txtNotif3.setVisibility(View.GONE);
        }
        else if (notif3.equals("0")){
            txtNotif3.setText("Today is the disconnection date. To avoid disconnection of service, please pay at the nearest payment center near you.");
        }
        else {
            txtNotif3.setText("You only have " + notif3 + " day/s to pay your bills to avoid disconnection.");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        this.finish();
        return super.onOptionsItemSelected(item);
    }
}
