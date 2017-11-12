package com.zjy.wukazhifu.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.zjy.wukazhifu.R;
import com.zjy.wukazhifu.util.IdcardValidator;
import com.zjy.wukazhifu.util.ToolUtil;
import com.zjy.wukazhifu.view.MyAlert;

public class ShimingActivity extends Activity implements OnClickListener {

	private ImageView backImg;
	private EditText nameEdt;
	private EditText sfzEdt;
	private TextView nextTxt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shiming);
		initView();
	}

	private void initView() {
		backImg = (ImageView) findViewById(R.id.backImg);
		backImg.setOnClickListener(this);

		nameEdt = (EditText) findViewById(R.id.nameEdt);
		sfzEdt = (EditText) findViewById(R.id.sfzEdt);

		nextTxt = (TextView) findViewById(R.id.nextTxt);
		nextTxt.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.backImg:
			finish();
			break;
		case R.id.nextTxt:
			String name = nameEdt.getText().toString().trim();
			String sfzh = sfzEdt.getText().toString().trim();
			IdcardValidator iv = new IdcardValidator();
			if ("".equals(name)) {
				View contentView = LayoutInflater.from(ShimingActivity.this).inflate(R.layout.alert_text1, null);
				TextView txt1 = (TextView) contentView.findViewById(R.id.txt1);
				txt1.setText("请输入真实姓名！");
				MyAlert.alert(ShimingActivity.this, contentView);
				return;
			} else if (!ToolUtil.isChinese(name)) {
				View contentView = LayoutInflater.from(ShimingActivity.this).inflate(R.layout.alert_text1, null);
				TextView txt1 = (TextView) contentView.findViewById(R.id.txt1);
				txt1.setText("请输入中文姓名！");
				MyAlert.alert(ShimingActivity.this, contentView);
				return;
			}
			if ("".equals(sfzh)) {
				View contentView = LayoutInflater.from(ShimingActivity.this).inflate(R.layout.alert_text1, null);
				TextView txt1 = (TextView) contentView.findViewById(R.id.txt1);
				txt1.setText("请输入身份证号！");
				MyAlert.alert(ShimingActivity.this, contentView);
				return;
			} else if (!iv.isValidatedAllIdcard(sfzh)) {
				View contentView = LayoutInflater.from(ShimingActivity.this).inflate(R.layout.alert_text1, null);
				TextView txt1 = (TextView) contentView.findViewById(R.id.txt1);
				txt1.setText("请输入正确的身份证号！");
				MyAlert.alert(ShimingActivity.this, contentView);
				return;
			}
			Intent intent = new Intent(ShimingActivity.this, Shiming2Activity.class);
			intent.putExtra("name", name);
			intent.putExtra("sfzh", sfzh);
			startActivity(intent);
			break;
		default:
			break;
		}
	}
}