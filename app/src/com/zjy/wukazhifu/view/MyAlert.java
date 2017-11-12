package com.zjy.wukazhifu.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface.OnCancelListener;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zjy.wukazhifu.R;

public class MyAlert {

	private static AlertDialog dialog;

	public static void alert(Context content, View contentView) {
		if (dialog != null && dialog.isShowing()) {
			return;
		}
		dialog = new AlertDialog.Builder(content).create();
		dialog.show();
		View view = LayoutInflater.from(content).inflate(R.layout.alert_msg, null);
		Window window = dialog.getWindow();
		dialog.setContentView(view);
		WindowManager.LayoutParams lp = window.getAttributes();
		window.setGravity(Gravity.CENTER);
		DisplayMetrics dm = content.getResources().getDisplayMetrics();
		lp.width = (int) (dm.widthPixels * 0.8);
		window.setAttributes(lp);

		LinearLayout contentLayout = (LinearLayout) view.findViewById(R.id.contentLayout);
		contentLayout.addView(contentView);

		TextView confirmTxt = (TextView) view.findViewById(R.id.confirmTxt);
		confirmTxt.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
	}

	public static void alert(Context content, View contentView, OnClickListener l) {
		if (dialog != null && dialog.isShowing()) {
			return;
		}
		dialog = new AlertDialog.Builder(content).create();
		dialog.show();
		View view = LayoutInflater.from(content).inflate(R.layout.alert_msg, null);
		Window window = dialog.getWindow();
		dialog.setContentView(view);
		WindowManager.LayoutParams lp = window.getAttributes();
		window.setGravity(Gravity.CENTER);
		DisplayMetrics dm = content.getResources().getDisplayMetrics();
		lp.width = (int) (dm.widthPixels * 0.8);
		window.setAttributes(lp);

		LinearLayout contentLayout = (LinearLayout) view.findViewById(R.id.contentLayout);
		contentLayout.addView(contentView);

		TextView confirmTxt = (TextView) view.findViewById(R.id.confirmTxt);
		confirmTxt.setOnClickListener(l);
	}
	
	public static void alert(Context content,String titile,String confirm, View contentView, OnClickListener l,OnCancelListener l2) {
		if (dialog != null && dialog.isShowing()) {
			return;
		}
		dialog = new AlertDialog.Builder(content).create();
		dialog.show();
		View view = LayoutInflater.from(content).inflate(R.layout.alert_msg, null);
		Window window = dialog.getWindow();
		dialog.setContentView(view);
		WindowManager.LayoutParams lp = window.getAttributes();
		window.setGravity(Gravity.CENTER);
		DisplayMetrics dm = content.getResources().getDisplayMetrics();
		lp.width = (int) (dm.widthPixels * 0.8);
		window.setAttributes(lp);

		LinearLayout contentLayout = (LinearLayout) view.findViewById(R.id.contentLayout);
		contentLayout.addView(contentView);
		
		TextView titleTxt = (TextView) view.findViewById(R.id.titleTxt);
		titleTxt.setText(titile);
		TextView confirmTxt = (TextView) view.findViewById(R.id.confirmTxt);
		confirmTxt.setText(confirm);
		confirmTxt.setOnClickListener(l);
		dialog.setOnCancelListener(l2);
	}

	public static void confirm(Context content, View contentView, OnClickListener l) {
		if (dialog != null && dialog.isShowing()) {
			return;
		}
		dialog = new AlertDialog.Builder(content).create();
		dialog.show();
		View view = LayoutInflater.from(content).inflate(R.layout.alert_confirm, null);
		Window window = dialog.getWindow();
		dialog.setContentView(view);
		WindowManager.LayoutParams lp = window.getAttributes();
		window.setGravity(Gravity.CENTER);
		DisplayMetrics dm = content.getResources().getDisplayMetrics();
		lp.width = (int) (dm.widthPixels * 0.8);
		window.setAttributes(lp);

		LinearLayout contentLayout = (LinearLayout) view.findViewById(R.id.contentLayout);
		contentLayout.addView(contentView);

		TextView confirmTxt = (TextView) view.findViewById(R.id.confirmTxt);
		confirmTxt.setOnClickListener(l);
		TextView cancleTxt = (TextView) view.findViewById(R.id.cancleTxt);
		cancleTxt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
	}
	
	public static void confirm(Context content, String title,View contentView, OnClickListener l) {
		if (dialog != null && dialog.isShowing()) {
			return;
		}
		dialog = new AlertDialog.Builder(content).create();
		dialog.show();
		View view = LayoutInflater.from(content).inflate(R.layout.alert_confirm, null);
		Window window = dialog.getWindow();
		dialog.setContentView(view);
		WindowManager.LayoutParams lp = window.getAttributes();
		window.setGravity(Gravity.CENTER);
		DisplayMetrics dm = content.getResources().getDisplayMetrics();
		lp.width = (int) (dm.widthPixels * 0.8);
		window.setAttributes(lp);

		LinearLayout contentLayout = (LinearLayout) view.findViewById(R.id.contentLayout);
		contentLayout.addView(contentView);


		TextView titleTxt = (TextView) view.findViewById(R.id.titleTxt);
		titleTxt.setText(title);
		
		TextView confirmTxt = (TextView) view.findViewById(R.id.confirmTxt);
		confirmTxt.setOnClickListener(l);
		TextView cancleTxt = (TextView) view.findViewById(R.id.cancleTxt);
		cancleTxt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
	}
	
	public static void confirm(Context content, String title,String confirm,String cancle,View contentView, OnClickListener l) {
		if (dialog != null && dialog.isShowing()) {
			return;
		}
		dialog = new AlertDialog.Builder(content).create();
		dialog.show();
		View view = LayoutInflater.from(content).inflate(R.layout.alert_confirm, null);
		Window window = dialog.getWindow();
		dialog.setContentView(view);
		WindowManager.LayoutParams lp = window.getAttributes();
		window.setGravity(Gravity.CENTER);
		DisplayMetrics dm = content.getResources().getDisplayMetrics();
		lp.width = (int) (dm.widthPixels * 0.8);
		window.setAttributes(lp);

		LinearLayout contentLayout = (LinearLayout) view.findViewById(R.id.contentLayout);
		contentLayout.addView(contentView);


		TextView titleTxt = (TextView) view.findViewById(R.id.titleTxt);
		titleTxt.setText(title);
		
		TextView confirmTxt = (TextView) view.findViewById(R.id.confirmTxt);
		confirmTxt.setOnClickListener(l);
		confirmTxt.setText(confirm);
		TextView cancleTxt = (TextView) view.findViewById(R.id.cancleTxt);
		cancleTxt.setText(cancle);
		cancleTxt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
	}

	public static void dismiss() {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
	}
}
