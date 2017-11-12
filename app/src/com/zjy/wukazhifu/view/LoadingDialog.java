package com.zjy.wukazhifu.view;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.zjy.wukazhifu.R;

public class LoadingDialog {

	private static Dialog dialog;
	private static int numflag = 0;

	public static void show(Context context, String message, boolean isCancelable, boolean isCancelOutside) {
		numflag++;
		if (dialog != null && dialog.isShowing()) {
			return;
		}
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.dialog_loading, null);
		dialog = new Dialog(context, R.style.MyDialogStyle);
		TextView msgText = (TextView) view.findViewById(R.id.tipTextView);
		msgText.setText(message);
		dialog.setContentView(view);
		dialog.setCancelable(isCancelable);
		dialog.setCanceledOnTouchOutside(isCancelOutside);
		dialog.show();
	}

	public static void show(Context context, String message) {
		show(context, message, false, false);
	}

	public static void show(Context context) {
		show(context, "加载中...", false, false);
	}

	public static void dismiss() {
		numflag--;
		if (numflag <= 0 && dialog != null && dialog.isShowing()) {
			dialog.dismiss();
			numflag = 0;
		}
	}
}
