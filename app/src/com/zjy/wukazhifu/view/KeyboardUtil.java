package com.zjy.wukazhifu.view;

import org.feezu.liuli.timeselector.Utils.ScreenUtil;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.text.Editable;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.zjy.wukazhifu.R;
import com.zjy.wukazhifu.util.ToastHelper;

public class KeyboardUtil {
	private Context ctx;
	private EditText ed;

	private Dialog dialog;
	private KeyboardListener mKeyboardListener;

	public interface KeyboardListener {
		public void completed();
	}

	public KeyboardUtil(Context ctx, EditText edit, KeyboardListener listener) {
		this.ctx = ctx;
		this.ed = edit;
		this.mKeyboardListener = listener;
	}

	public void show() {
		if (dialog != null && dialog.isShowing()) {
			return;
		}
		dialog = new Dialog(ctx, R.style.MyDialogStyleBottom);
		dialog.setCancelable(true);
		dialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				mKeyboardListener.completed();
			}
		});
		dialog.setContentView(R.layout.dlg_keyboard);
		Window window = dialog.getWindow();
		window.setGravity(Gravity.BOTTOM);
		WindowManager.LayoutParams lp = window.getAttributes();
		int width = ScreenUtil.getInstance(ctx).getScreenWidth();
		lp.width = width;
		window.setAttributes(lp);
		dialog.show();
		Keyboard k1 = new Keyboard(ctx, R.xml.number);
		KeyboardView keyboardView = (KeyboardView) dialog.findViewById(R.id.keyboard_view);
		keyboardView.setKeyboard(k1);
		keyboardView.setEnabled(true);
		keyboardView.setPreviewEnabled(true);
		keyboardView.setOnKeyboardActionListener(listener);

		TextView completeTxt = (TextView) dialog.findViewById(R.id.completeTxt);
		completeTxt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.cancel();
			}
		});
	}

	public void cancel() {
		if (dialog != null && dialog.isShowing()) {
			dialog.cancel();
		}
	}

	private OnKeyboardActionListener listener = new OnKeyboardActionListener() {
		@Override
		public void swipeUp() {
		}

		@Override
		public void swipeRight() {
		}

		@Override
		public void swipeLeft() {
		}

		@Override
		public void swipeDown() {
		}

		@Override
		public void onText(CharSequence text) {
		}

		@Override
		public void onRelease(int primaryCode) {
		}

		@Override
		public void onPress(int primaryCode) {
		}

		@Override
		public void onKey(int primaryCode, int[] keyCodes) {
			Editable editable = ed.getText();
			int start = ed.getSelectionStart();
			if (primaryCode == 46) { // 小数点
				String text = ed.getText().toString();
				if (!text.contains(".") && text.length() <= 5) {
					editable.insert(start, Character.toString((char) primaryCode));
				}
			} else if (primaryCode == Keyboard.KEYCODE_DELETE) {// 回退
				if (editable != null && editable.length() > 0) {
					if (start > 0) {
						editable.delete(start - 1, start);
					}
				}
			} else {
				String text = ed.getText().toString();
				if ((!text.contains(".") && text.length() < 5) || (text.contains(".") && text.length() - text.indexOf(".") <= 2)) {
					editable.insert(start, Character.toString((char) primaryCode));
				} else {
					ToastHelper.toast(ctx, "小数点前最多5位，小数点后最多2位");
				}
			}
		}
	};
}