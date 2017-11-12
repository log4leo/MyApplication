package com.zjy.wukazhifu.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.zjy.wukazhifu.R;
import com.zjy.wukazhifu.view.MyAlert;

public class ToastHelper {

	private static Context mContext;

	public static void init(Context context) {
		mContext = context;
	}

	public static void toast(Context context,String msg) {
		View contentView = LayoutInflater.from(context).inflate(R.layout.alert_text1, null);
		TextView txt1 = (TextView) contentView.findViewById(R.id.txt1);
		txt1.setText(msg);
		MyAlert.alert(context, contentView);
	}
	
	public static void toast(Context context,String msg,OnClickListener l) {
		View contentView = LayoutInflater.from(context).inflate(R.layout.alert_text1, null);
		TextView txt1 = (TextView) contentView.findViewById(R.id.txt1);
		txt1.setText(msg);
		MyAlert.alert(context, contentView,l);
	}
}
