package org.mnwd.mnwd;

/**
 * Created by Camille Fornoles on 7/24/2017.
 */

public class Config {
    //Address of our scripts of the CRUD

    public static final String http = "http://";
    public static final String IP = "192.168.1.33";
    public static final String port_no = "8080";
    public static final String repository = "/mnwd_/";
    public static final String URL = http + IP + ":" + port_no + repository;
    //public static final String URL = "https://mnwdtest.000webhostapp.com/";
    public static final String URL_FINDACCOUNTNO = URL + "mobile_findaccountnumber.php";
    public static final String URL_FINDBILLINGDETAILS = URL + "mobile_findbillingdetails.php";
    public static final String URL_SENDEMAIL = URL + "mobile_sendemail.php";
    public static final String URL_LOGIN = URL + "mobile_login.php";
    public static final String URL_GETALLANNOUNCEMENTS = URL + "mobile_announcements.php";
    public static final String URL_GETBILLHISTORY = URL + "mobile_billhistory.php";
    public static final String URL_GRAPH = URL + "mobile_graph.php";
    public static final String URL_REPORTINCIDENT = URL + "mobile_reportincident.php";
    public static final String URL_BILLCALCULATOR = URL + "mobile_calculator.php";
    public static final String URL_GETALLINACTIVEACCOUNTS = URL + "mobile_getallinactiveaccounts.php";
    public static final String URL_GETALLACTIVATEDACCOUNTS = URL + "mobile_getallactivatedaccounts.php";
    public static final String URL_ACTIVATEACCOUNT = URL + "mobile_activateaccount.php";
    public static final String URL_GETALLLOGINOPTIONS = URL + "mobile_getallloginoptions.php";
    public static final String URL_EDITPASSWORD = URL + "mobile_editpassword.php";
    public static final String URL_GETLATESTBILL = URL + "mobile_getlatestbill.php";
    public static final String URL_GETLATESTBILLINGYEAR = URL + "mobile_getlatestbillingyear.php";
    public static final String URL_CHECKNOTIFICATION = URL + "mobile_notification_manager.php";
    public static final String URL_GETBILLPREDICTION = URL + "mobile_billprediction.php";
    public static final String URL_SENDACTIVATIONCODE = URL + "mobile_sendactivationcode.php";
    public static final String URL_DOWNLOADPDF = URL + "mobile_billpdf.php";
    public static final String URL_GETBILLINGYEARS = URL + "mobile_getbillingyears.php";

    //Keys that will be used to send the request to php scripts
    public static final String KEY_CON_ACCOUNTNO = "accountno";
    public static final String KEY_CON_ACCOUNTID = "accountid";
    public static final String KEY_CON_USERID = "userid";
    public static final String KEY_READING_BILLINGDATE = "billingdate";
    public static final String KEY_READING_REFERENCENO = "referenceno";
    public static final String KEY_CON_EMAIL = "email";
    public static final String KEY_CON_PASSWORD = "password";
    public static final String KEY_CON_PASSWORD_NEW = "password_new";
    public static final String KEY_CON_PASSWORD_CONFIRM = "password_confirm";
    public static final String KEY_INCIDENT_INCIDENTID = "incidentid";
    public static final String KEY_INCIDENT_DESCRIPTION = "description";
    public static final String KEY_AC_CLASSIFICATION = "classification";
    public static final String KEY_READING_CUM = "cubicmeterused";
    public static final String KEY_MS_SIZE = "size";
    public static final String KEY_ACC_TYPE = "type";
    public static final String KEY_ACC_ACTIVATION_CODE = "activation_code";
    public static final String KEY_IP = "ip";
    public static final String KEY_PORT = "port";

    //Android Session Management Keys
    public static final String FILENAME_SESSION = "UserPrefs";
    public static final String SESSION_ACCOUNTNO = "SESSION_ACCOUNTNO";
    public static final String SESSION_ACCOUNTID = "SESSION_ACCOUNTID";
    public static final String SESSION_ISSIGNINGUP = "SESSION_ISSIGNINGUP";
    public static final String SESSION_USERID = "SESSION_USERID";
    public static final String SESSION_EMAIL = "SESSION_EMAIL";
    public static final String SESSION_TOTALACCOUNT = "SESSION_TOTALACCOUNT";
    public static final String SESSION_TOTALACTIVATEDACCOUNT = "SESSION_TOTALACTIVATEDACCOUNT";
    public static final String SESSION_FIRSTNAME = "SESSION_FIRSTNAME";
    public static final String SESSION_LASTNAME = "SESSION_LASTNAME";

    //JSON Tags
    public static final String TAG_JSON_ARRAY = "result";
    public static final String TAG_READING_BILLINGMONTH = "billingmonth";
    public static final String TAG_READING_BILLINGYEAR = "billingyear";
    public static final String TAG_READING_BILLINGDATE = "billingdate";
    public static final String TAG_READING_CONSUMPTION = "consumption";
    public static final String TAG_READING_BILLAMOUNT = "billamount";
    public static final String TAG_READING_DUEDATE = "duedate";
    public static final String TAG_READING_DISCONNECTIONDATE = "disconnection_date";
    public static final String TAG_READING_BILLWPENALTY = "bill_w_penalty";
    public static final String TAG_ACCOUNT_ACCOUNTNO = "accountno";
    public static final String TAG_ACCOUNT_ACCOUNTID = "accountid";

    //accountno to pass with intent
    public static final String ACCOUNT_NO = "accountno";
}