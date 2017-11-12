package com.zjy.wukazhifu.view.choose;

import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.zjy.wukazhifu.R;
import com.zjy.wukazhifu.view.choose.adapter.ArrayWheelAdapter;
import com.zjy.wukazhifu.view.choose.myinterface.OnWheelChangedListener;
import com.zjy.wukazhifu.view.choose.myinterface.SelectAddressInterface;

public class SelectOneDialog implements OnClickListener, OnWheelChangedListener {

	private Activity context;
	private SelectAddressInterface selectAdd;
	private WheelView wheelView;
	private Button mBtnConfirm;

	private Dialog overdialog;
	private String[] data;
	private int selectValue;

	public SelectOneDialog(Activity context, String[] data, SelectAddressInterface selectAdd) {
		this.context = context;
		this.data = data;
		this.selectAdd = selectAdd;

		View overdiaView = View.inflate(context, R.layout.dialog_select_one, null);
		wheelView = (WheelView) overdiaView.findViewById(R.id.wheelView);
		wheelView.setViewAdapter(new ArrayWheelAdapter<String>(context, data));
		wheelView.setVisibleItems(7);
		wheelView.setCurrentItem(0);
		wheelView.addChangingListener(this);

		mBtnConfirm = (Button) overdiaView.findViewById(R.id.btn_confirm);
		mBtnConfirm.setOnClickListener(this);

		overdialog = new Dialog(context, R.style.dialog_lhp);
		Window window = overdialog.getWindow();
		window.setWindowAnimations(R.style.mystyle); // 添加动画
		overdialog.setContentView(overdiaView);
		overdialog.setCanceledOnTouchOutside(true);
	}

	public void showDialog() {
		if (overdialog != null) {
			overdialog.show();
			Window win = overdialog.getWindow();
			win.getDecorView().setPadding(0, 0, 0, 0);
			WindowManager.LayoutParams lp = win.getAttributes();
			lp.width = WindowManager.LayoutParams.MATCH_PARENT;
			lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
			win.setGravity(Gravity.BOTTOM);
			win.setAttributes(lp);
		}
	}

	@Override
	public void onChanged(WheelView wheel, int oldValue, int newValue) {
		if (data != null) {
			selectValue = newValue;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_confirm:
			selectAdd.setAreaString(data[selectValue]);
			overdialog.cancel();
			break;
		default:
			break;
		}
	}

}
