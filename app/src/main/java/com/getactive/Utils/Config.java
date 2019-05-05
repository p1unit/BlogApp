package com.getactive.Utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class Config {

    public static final String ERROR_405="Error Occurred. Please try Again :)";
    public static final String ERROR_401="Error Occurred. Please try Again :)";
    public static final String ERROR_403="Error Occurred. Please try Again :)";
    public static final String ERROR_409="Error Occurred. Please try Again :)";
    public static final String ERROR_422="Error Occurred. Please try Again :)";
    public static final String ERROR_500="Error Occurred. Please try Again :)";
    public static final String ERROR_INTERNET="Internet Problem. Kindly check :(";
    public static final String ERROR_TOAST="Error Occurred. Please try Again :)";
    public static final String ERROR_GENERAL="Error Occurred. Kindly try again :(";
    public static final String ERROR_ILLEGAL_STATE="Internet Problem. Kindly check :(";
    public static final String PERMISSION_REQUEST = "Please grant necessary permission to continue :) ";
    public static final String SHARED_PREFERENCE_FIREBASE_DATA="FIREBASEDATA";
    public static final String SHARED_PREFERENCE_USER_DATA="USERDATA";
    public static final String POST_MSG ="Post is Added" ;


    public static void logW(String TAG,String str){
        Log.w(TAG,str);
    }

    public static void logE(String TAG,String str){
        Log.e(TAG,str);
    }

    public static void toastShort(Context ctx,String msg){
        Toast.makeText(ctx,msg,Toast.LENGTH_SHORT).show();
    }

    public static void toastLong(Context ctx,String msg){
        Toast.makeText(ctx,msg,Toast.LENGTH_LONG).show();
    }
    public static void logd(String tag, String message){
        Log.d(tag, message);
    }
}
