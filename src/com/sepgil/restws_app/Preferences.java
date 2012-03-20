package com.sepgil.restws_app;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * 
 * @author Sebastian Gilits
 *
 */
public class Preferences {
	public static String getPref(Context ctxt, String pref, String DefaultValue) {
		SharedPreferences sharedPreferences = ctxt.getSharedPreferences("RestWSApp", Context.MODE_PRIVATE);
	    return sharedPreferences.getString(pref, DefaultValue);
	}
	
	public static void delPref(Context ctxt, String pref) {
		SharedPreferences sharedPreferences = ctxt.getSharedPreferences("RestWSApp", Context.MODE_PRIVATE);
		sharedPreferences.edit().remove(pref).apply();
	}


	public static void setPref(Context ctxt, String pref, String value) {
		SharedPreferences sharedPreferences = ctxt.getSharedPreferences("RestWSApp", Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putString(pref, value);
		editor.apply();
	}
}
