package org.mnwd.mnwd;

/**
 * Created by Camille Fornoles on 7/24/2017.
 */

public class Config {
    //Address of our scripts of the CRUD
    public static final String http = "http://";
    public static final String IP = "192.168.1.6";
    public static final String URL_FINDACCOUNTNO = http + IP + "/mnwd_/mobile_findaccountnumber.php";
    public static final String URL_FINDBILLINGDETAILS = http + IP + "/mnwd_/mobile_findbillingdetails.php";
    public static final String URL_SENDEMAIL = http + IP + "/mnwd_/mobile_sendemail.php";
    public static final String URL_LOGIN = http + IP + "/mnwd_/mobile_login.php";
    public static final String URL_GETALLANNOUNCEMENTS = http + IP + "/mnwd_/mobile_announcements.php";
    public static final String URL_GETBILLHISTORY = http + IP + "/mnwd_/mobile_billhistory.php";
    public static final String URL_REPORTINCIDENT = http + IP + "/mnwd_/mobile_reportincident.php";
    public static final String URL_BILLCALCULATOR = http + IP + "/mnwd_/mobile_calculator.php";
    public static final String URL_GETALLINACTIVEACCOUNTS = http + IP + "/mnwd_/mobile_getallinactiveaccounts.php";
    public static final String URL_GETALLACTIVATEDACCOUNTS = http + IP + "/mnwd_/mobile_getallactivatedaccounts.php";
    public static final String URL_ACTIVATEACCOUNT = http + IP + "/mnwd_/mobile_activateaccount.php";
    public static final String URL_GETALLLOGINOPTIONS = http + IP + "/mnwd_/mobile_getallloginoptions.php";
    public static final String URL_EDITPASSWORD = http + IP + "/mnwd_/mobile_editpassword.php";
    public static final String URL_GETLATESTBILL = http + IP + "/mnwd_/mobile_getlatestbill.php";
    public static final String URL_GETLATESTBILLINGYEAR = http + IP + "/mnwd_/mobile_getlatestbillingyear.php";

    //Keys that will be used to send the request to php scripts
    public static final String KEY_CON_ACCOUNTNO = "accountno";
    public static final String KEY_CON_ACCOUNTID = "accountid";
    public static final String KEY_CON_USERID = "userid";
    public static final String KEY_READING_BILLINGDATE = "billingdate";
    public static final String KEY_READING_REFERENCENO = "referenceno";
    public static final String KEY_READING_BILLAMOUNT = "billamount";
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
    public static final String TAG_ANNOUNCEMENT_ANNOUNCEMENTID = "announcementid";
    public static final String TAG_ANNOUNCEMENT_ANNOUNCEMENT = "announcement";
    public static final String TAG_ANNOUNCEMENT_DATE = "date";
    public static final String TAG_READING_BILLINGMONTH = "billingmonth";
    public static final String TAG_READING_BILLINGYEAR = "billingyear";
    public static final String TAG_READING_BILLINGDATE = "billingdate";
    public static final String TAG_READING_PREVIOUS = "previous_reading";
    public static final String TAG_READING_PRESENT = "present_reading";
    public static final String TAG_READING_CONSUMPTION = "consumption";
    public static final String TAG_READING_BILLAMOUNT = "billamount";
    public static final String TAG_READING_DUEDATE = "duedate";
    public static final String TAG_READING_DISCONNECTIONDATE = "disconnection_date";
    public static final String TAG_READING_REFNO = "refno";
    public static final String TAG_READING_PREVIOUSBILLINGDATE = "previous_billingdate";
    public static final String TAG_ACCOUNT_ACCOUNTNO = "accountno";
    public static final String TAG_ACCOUNT_ACCOUNTID = "accountid";

    //accountno to pass with intent
    public static final String ACCOUNT_NO = "accountno";
}