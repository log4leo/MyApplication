package com.zjy.wukazhifu.view.choose;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.zjy.wukazhifu.R;
import com.zjy.wukazhifu.view.choose.adapter.ArrayWheelAdapter;
import com.zjy.wukazhifu.view.choose.myinterface.OnWheelChangedListener;
import com.zjy.wukazhifu.view.choose.myinterface.SelectAddressInterface;

public class SelectAddressDialog implements OnClickListener, OnWheelChangedListener {
	private boolean isMyDatas;// 是否自定义数据

	public static final int STYLE_ONE = 1;// 一级联动
	public static final int STYLE_TWO = 2;// 二级联动
	public static final int STYLE_THREE = 3;// 三级联动
	/**
	 * 所有省
	 */
	protected String[] mProvinceDatas;
	/**
	 * key - 省 value - 市
	 */
	protected Map<String, String[]> mCitisDatasMap = new HashMap<String, String[]>();
	/**
	 * key - 市 values - 区
	 */
	protected Map<String, String[]> mDistrictDatasMap = new HashMap<String, String[]>();

	/**
	 * key - 区 values - 邮编
	 */
	protected Map<String, String> mZipcodeDatasMap = new HashMap<String, String>();

	/**
	 * 当前区的postion
	 */
	protected int mCurrentDistrictNamePosition;
	/**
	 * 当前省的postion
	 */
	protected int mCurrentProviceNamePosition;
	/**
	 * 当前市的postion
	 */
	protected int mCurrentCityNamePosition;
	/**
	 * 当前省的名称
	 */
	protected String mCurrentProviceName;
	/**
	 * 当前市的名称
	 */
	protected String mCurrentCityName;
	/**
	 * 当前区的名称
	 */
	protected String mCurrentDistrictName = "";
	/**
	 * 当前区的邮政编码
	 */
	protected String mCurrentZipCode = "";

	private WheelView mViewProvince;
	private WheelView mViewCity;
	private WheelView mViewDistrict;
	private Button mBtnConfirm, mBtnCancel;
	private Activity context;
	private Dialog overdialog;
	private SelectAddressInterface selectAdd;
	private int tmp1, tmp2, tmp3;
	private int type;

	public SelectAddressDialog(Activity context, SelectAddressInterface selectAdd, int type, String[] mProvinceDatas) {
		this.selectAdd = selectAdd;
		this.type = type;
		this.context = context;
		View overdiaView = View.inflate(context, R.layout.dialog_select_address, null);

		mViewProvince = (WheelView) overdiaView.findViewById(R.id.id_province);
		mViewCity = (WheelView) overdiaView.findViewById(R.id.id_city);
		mViewDistrict = (WheelView) overdiaView.findViewById(R.id.id_district);
		if (STYLE_TWO == type) {
			mViewDistrict.setVisibility(View.GONE);
		}
		if (STYLE_ONE == type) {
			mViewDistrict.setVisibility(View.GONE);
			mViewCity.setVisibility(View.GONE);
		}
		mBtnConfirm = (Button) overdiaView.findViewById(R.id.btn_confirm);
		mBtnCancel = (Button) overdiaView.findViewById(R.id.btn_cancel);
		overdialog = new Dialog(context, R.style.dialog_lhp);
		Window window = overdialog.getWindow();
		window.setWindowAnimations(R.style.mystyle); // 添加动画
		overdialog.setContentView(overdiaView);
		overdialog.setCanceledOnTouchOutside(false);
		setUpListener();
		if (mProvinceDatas == null) {
			setUpData();
			isMyDatas = false;
		} else {
			isMyDatas = true;
			this.mProvinceDatas = mProvinceDatas;
			mCurrentProviceName = mProvinceDatas[0];
			mViewProvince.setViewAdapter(new ArrayWheelAdapter<String>(context, this.mProvinceDatas));
			// 设置可见条目数量
			mViewProvince.setVisibleItems(7);
			mViewCity.setVisibleItems(7);
			mViewDistrict.setVisibleItems(7);
		}

	}

	public void initDate() {
		if (mViewProvince != null)
			mViewProvince.setCurrentItem(mCurrentProviceNamePosition);
		if (mViewCity != null)
			mViewCity.setCurrentItem(mCurrentCityNamePosition);
		if (mViewDistrict != null)
			mViewDistrict.setCurrentItem(mCurrentDistrictNamePosition);
	}

	public void showDialog() {

		if (overdialog != null) {
			if (mViewProvince != null)
				mViewProvince.setCurrentItem(mCurrentProviceNamePosition);
			if (mViewCity != null)
				mViewCity.setCurrentItem(mCurrentCityNamePosition);
			if (mViewDistrict != null)
				mViewDistrict.setCurrentItem(mCurrentDistrictNamePosition);
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
		// TODO Auto-generated method stub
		if (wheel == mViewProvince) {
			updateCities();
		} else if (wheel == mViewCity) {
			updateAreas();
		} else if (wheel == mViewDistrict) {
			mCurrentDistrictName = mDistrictDatasMap.get(mCurrentCityName)[newValue];
			tmp3 = newValue;
			mCurrentZipCode = mZipcodeDatasMap.get(mCurrentDistrictName);
		}
	}

	/**
	 * 根据当前的市，更新区WheelView的信息
	 */
	private void updateAreas() {

		int pCurrent = mViewCity.getCurrentItem();
		tmp2 = pCurrent;
		mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[pCurrent];
		String[] areas = mDistrictDatasMap.get(mCurrentCityName);

		if (areas == null) {
			areas = new String[] { "" };
		}
		mCurrentDistrictName = areas[0];
		mViewDistrict.setViewAdapter(new ArrayWheelAdapter<String>(context, areas));
		mViewDistrict.setCurrentItem(0);
	}

	/**
	 * 根据当前的省，更新市WheelView的信息
	 */
	private void updateCities() {
		int pCurrent = mViewProvince.getCurrentItem();
		tmp1 = pCurrent;
		mCurrentProviceName = mProvinceDatas[pCurrent];
		if (!isMyDatas) {// 不是自定义数据
			String[] cities = mCitisDatasMap.get(mCurrentProviceName);
			if (cities == null) {
				cities = new String[] { "" };
			}
			mViewCity.setViewAdapter(new ArrayWheelAdapter<String>(context, cities));
			mViewCity.setCurrentItem(0);
			updateAreas();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_confirm:
			if (type == STYLE_TWO) {
				selectAdd.setAreaString(mCurrentProviceName + " " + mCurrentCityName);
			} else if (type == STYLE_ONE) {
				selectAdd.setAreaString(mCurrentProviceName);
			} else {
				selectAdd.setAreaString(mCurrentProviceName + " " + mCurrentCityName + " " + mCurrentDistrictName);
			}
			mCurrentProviceNamePosition = tmp1;
			mCurrentCityNamePosition = tmp2;
			mCurrentDistrictNamePosition = tmp3;
			// ToastUtil.show(context,mCurrentCityNamePosition+":"+mCurrentCityNamePosition+":"+mCurrentDistrictNamePosition);
			overdialog.cancel();
			break;

		case R.id.btn_cancel:
			overdialog.cancel();
			break;
		default:
			break;
		}
	}

	private void showSelectedResult() {
		Toast.makeText(context, "当前选中:" + mCurrentProviceName + "," + mCurrentCityName + "," + mCurrentDistrictName + "," + mCurrentZipCode, Toast.LENGTH_SHORT).show();
	}

	private void setUpData() {
		initProvinceDatas();
		mViewProvince.setViewAdapter(new ArrayWheelAdapter<String>(context, mProvinceDatas));
		// 设置可见条目数量
		mViewProvince.setVisibleItems(7);
		mViewCity.setVisibleItems(7);
		mViewDistrict.setVisibleItems(7);
		updateCities();
		updateAreas();
	}

	private void setUpListener() {
		// 添加change事件
		mViewProvince.addChangingListener(this);
		// 添加change事件
		mViewCity.addChangingListener(this);
		// 添加change事件
		mViewDistrict.addChangingListener(this);
		// 添加onclick事件
		mBtnConfirm.setOnClickListener(this);
		// 添加onclick事件
		mBtnCancel.setOnClickListener(this);

	}

	/**
	 * 解析省市区的XML数据
	 */

	protected void initProvinceDatas() {
	}

}
