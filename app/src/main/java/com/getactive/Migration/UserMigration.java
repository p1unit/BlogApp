package com.getactive.Migration;

import android.content.Context;
import android.content.SharedPreferences;

import com.getactive.Utils.ApplicationContextProvider;
import com.getactive.Utils.Config;

public class UserMigration {

    public Context context = ApplicationContextProvider.getContext();
    private SharedPreferences sharedPreferences = context.getSharedPreferences(Config.SHARED_PREFERENCE_USER_DATA, Context.MODE_PRIVATE);

    public void removeUserId(){
        sharedPreferences.edit().putString("userid","").apply();
    }

    public void setUserId(String userId){
        sharedPreferences.edit().putString("userid",userId).apply();
    }

    public String getUserId(){
        return sharedPreferences.getString("userid","");
    }


}
