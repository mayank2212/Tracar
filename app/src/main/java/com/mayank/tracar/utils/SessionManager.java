package com.mayank.tracar.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

/**
 * Created by Mayank on 13-09-2017.
 */
public class SessionManager {

    // Shared Preferences
    SharedPreferences pref;
    // Editor for Shared preferences
    SharedPreferences.Editor editor;
    // Context
    Context _context;
    // Shared pref mode
    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = Constants.PREF_NAME;
    // All Shared Preferences Keys
    private static final String IS_LOGIN = Constants.IS_LOGIN;
    // User name (make variable public to access from outside)
    public static final String KEY_SESSION_ID = Constants.KEY_SESSION_ID;
    public static final String KEY_USER_ID = Constants.KEY_USER_ID;
    public static final String KEY_USER_NAME = Constants.KEY_USER_NAME;
    public static final String KEY_USER_EMAIL = Constants.KEY_USER_EMAIL;

    // Constructor
    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /*** Create login session   **/
    public void createLoginSession(String sessionId, String userid, String userName){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);
        // Storing name in pref
        editor.putString(KEY_SESSION_ID, sessionId);
        editor.putString(KEY_USER_ID, userid);
        editor.putString(KEY_USER_NAME, userName);
        // commit changes
        editor.commit();
    }

    public Boolean checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            /*Intent i = new Intent(_context, LoginActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);*/
            return false;
        }
        else {
            return true;
        }
    }

    /* Get stored session data */
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_SESSION_ID, pref.getString(KEY_SESSION_ID, null));
        // return user
        return user;
    }

    /* Clear session details */
    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Login Activity
        //Intent i = new Intent(_context, LoginActivity.class);
        // Closing all the Activities
        //i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        //_context.startActivity(i);
    }

    /* Quick check for login */
    // Get Login State
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }

    public String getUserEmail(){
        return pref.getString(KEY_USER_EMAIL, "");
    }

    public String getSessionId(){
        return pref.getString(KEY_SESSION_ID, "");
    }


    public void saveUserEmail( String user_email ){
        editor.putString(KEY_USER_EMAIL, user_email);
        // commit changes
        editor.commit();
    }

}
