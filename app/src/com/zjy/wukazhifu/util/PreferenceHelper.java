package com.zjy.wukazhifu.util;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class PreferenceHelper {

	private static Context mContext;

	public static void init(Context context) {
		mContext = context;
	}

	public static boolean setFirst(boolean first) {
		Editor editor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
		editor.putBoolean("first", first);
		return editor.commit();
	}

	public static boolean getFirst() {
		return PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean("first", false);
	}
	
	public static boolean setLogin(boolean login) {
		Editor editor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
		editor.putBoolean("login", login);
		return editor.commit();
	}

	public static boolean getLogin() {
		return PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean("login", false);
	}

	public static boolean setCookie(String cookie) {
		Editor editor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
		editor.putString("cookie", cookie);
		return editor.commit();
	}

	public static String getCookie() {
		return PreferenceManager.getDefaultSharedPreferences(mContext).getString("cookie", "");
	}

	public static boolean setCustId(String custId) {
		Editor editor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
		editor.putString("custId", custId);
		return editor.commit();
	}

	public static String getCustId() {
		return PreferenceManager.getDefaultSharedPreferences(mContext).getString("custId", "");
	}
	
	public static boolean setCustLogin(String custLogin) {
		Editor editor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
		editor.putString("custLogin", custLogin);
		return editor.commit();
	}

	public static String getCustLogin() {
		return PreferenceManager.getDefaultSharedPreferences(mContext).getString("custLogin", "");
	}

}
