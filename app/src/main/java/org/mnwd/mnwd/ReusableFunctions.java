package org.mnwd.mnwd;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.NavigationView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Camille Fornoles on 8/12/2017.
 */

public class ReusableFunctions extends Activity {

    public static Intent navigateOptions (int id, Context context, SharedPreferences.Editor editor) {
        Intent intent = new Intent();
        if (id == R.id.nav_home) {
            intent = new Intent(context, Home.class);
        }
        else if (id == R.id.nav_announcement) {
            intent = new Intent(context, Announcement.class);
        }
        else if (id == R.id.nav_graph) {
            intent = new Intent(context, Graph.class);
        }
        else if (id == R.id.nav_bill_history) {
            intent = new Intent(context, BillHistory.class);
        }
        else if (id == R.id.nav_bill_calculator) {
            intent = new Intent(context, BillCalculator.class);
        }
        else if (id == R.id.nav_incident_report) {
            intent = new Intent(context, IncidentReport.class);
        }
        else if (id == R.id.nav_bill_prediction) {
            intent = new Intent(context, BillPrediction.class);
        }
        else if (id == R.id.nav_profile) {
            intent = new Intent(context, Profile.class);
        }
        else if (id == R.id.nav_switch_account) {
            intent = new Intent(context, SwitchAccount.class);
        }
        else if (id == R.id.nav_activate_account) {
            intent = new Intent(context, GetActivationCode.class);
        }
        else if (id == R.id.nav_logout) {
            //reset session
            editor.clear();
            editor.apply();

            intent = new Intent(context, MainActivity.class);
        }
        return intent;
    }

    public static void changeNavDrawerTitle (NavigationView navigationView, SharedPreferences sharedPreferences) {
        String session_accountno, session_firstname, session_lastname, name;

        //RETRIEVE SESSION DATA
        session_accountno = sharedPreferences.getString(Config.SESSION_ACCOUNTNO, null);
        session_firstname = sharedPreferences.getString(Config.SESSION_FIRSTNAME, null);
        session_lastname = sharedPreferences.getString(Config.SESSION_LASTNAME, null);
        name = session_firstname + " " + session_lastname;

        View mHeaderView = navigationView.getHeaderView(0);
        TextView accountno = (TextView) mHeaderView.findViewById(R.id.idTxtNavDrawerTitle);
        accountno.setText(session_accountno);

        TextView n = (TextView) mHeaderView.findViewById(R.id.idTxtName);
        n.setText(name);
    }
}