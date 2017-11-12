package com.zjy.wukazhifu.view.choose;

import java.util.Map;

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

public class SelectTwoDialog implements OnClickListener, OnWheelChangedListener {

	private Activity context;
	private SelectAddressInterface selectAdd;
	private WheelView wheelView1;
	private WheelView wheelView2;
	private Button mBtnConfirm;

	private Dialog overdialog;
	private String[] data1, data2;
	private Map<String, String[]> dataMap;

	private int selectValue1;
	private int selectValue2;

	public SelectTwoDialog(Activity context, String[] data1, Map<String, String[]> dataMap, SelectAddressInterface selectAdd) {
		this.context = context;
		this.data1 = data1;
		this.dataMap = dataMap;
		this.selectAdd = selectAdd;

		View overdiaView = View.inflate(context, R.layout.dialog_select_two, null);
		wheelView1 = (WheelView) overdiaView.findViewById(R.id.wheelView1);
		wheelView1.setViewAdapter(new ArrayWheelAdapter<String>(context, data1));
		wheelView1.setVisibleItems(7);
		wheelView1.setCurrentItem(0);
		wheelView1.addChangingListener(this);

		data2 = dataMap.get(data1[0]);
		wheelView2 = (WheelView) overdiaView.findViewById(R.id.wheelView2);
		wheelView2.setViewAdapter(new ArrayWheelAdapter<String>(context, data2));
		wheelView2.setVisibleItems(7);
		wheelView2.setCurrentItem(0);
		wheelView2.addChangingListener(this);

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
		if (wheel == wheelView1) {
			selectValue1 = newValue;
			data2 = dataMap.get(data1[selectValue1]);
			wheelView2.setViewAdapter(new ArrayWheelAdapter<String>(context, data2));
			wheelView2.setCurrentItem(0);
			selectValue2 = 0;
		} else if (wheel == wheelView2) {
			selectValue2 = newValue;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_confirm:
			selectAdd.setAreaString(data1[selectValue1] + " " + data2[selectValue2]);
			overdialog.cancel();
			break;
		default:
			break;
		}
	}

}
