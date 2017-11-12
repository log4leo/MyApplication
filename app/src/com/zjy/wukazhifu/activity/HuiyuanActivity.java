package com.zjy.wukazhifu.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zjy.wukazhifu.R;
import com.zjy.wukazhifu.entity.Huiyuan;
import com.zjy.wukazhifu.fragment.HuiyuanFragment;
import com.zjy.wukazhifu.util.Constants;
import com.zjy.wukazhifu.util.MyVolley;
import com.zjy.wukazhifu.util.PostJsonObjectRequest;
import com.zjy.wukazhifu.util.PreferenceHelper;
import com.zjy.wukazhifu.util.ToastHelper;
import com.zjy.wukazhifu.view.PagerSlidingTabStrip;

public class HuiyuanActivity extends FragmentActivity implements OnClickListener {

	private ImageView backImg;
	private DisplayMetrics dm;
	private PagerSlidingTabStrip tabs;
	private MyPagerAdapter adapter;
	private ViewPager pager;
	private TextView tipTxt;

	private List<Huiyuan> dataList = new ArrayList<Huiyuan>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_huiyuan);
		initView();
		huiyuan();
	}

	private void initView() {
		backImg = (ImageView) findViewById(R.id.backImg);
		backImg.setOnClickListener(this);

		tipTxt = (TextView) findViewById(R.id.tipTxt);

		dm = getResources().getDisplayMetrics();
		pager = (ViewPager) findViewById(R.id.pager);
		tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		adapter = new MyPagerAdapter(getSupportFragmentManager());
	}

	/**
	 * 对PagerSlidingTabStrip的各项属性进行赋值。
	 */
	private void setTabsValue() {
		// 设置Tab是自动填充满屏幕的
		tabs.setShouldExpand(true);
		// 设置Tab的分割线是透明的
		tabs.setDividerColor(Color.TRANSPARENT);
		// 设置Tab底部线的高度
		tabs.setUnderlineHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, dm));
		// 设置Tab Indicator的高度
		tabs.setIndicatorHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, dm));
		// 设置Tab标题文字的大小
		tabs.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, dm));
		// 设置Tab Indicator的颜色
		tabs.setIndicatorColor(Color.parseColor("#28A6E1"));
		// 设置选中Tab文字的颜色 (这是我自定义的一个方法)
		tabs.setSelectedTextColor(Color.parseColor("#28A6E1"));
		// 取消点击Tab时的背景色
		tabs.setTabBackground(0);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.backImg:
			finish();
			break;
		default:
			break;
		}
	}

	private void huiyuan() {
		String url = Constants.ADDR_huiyuan;
		String cookie = PreferenceHelper.getCookie();
		PostJsonObjectRequest request = new PostJsonObjectRequest(url, cookie, null, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					JSONObject REP_BODY = response.optJSONObject("REP_BODY");
					String RSPCOD = REP_BODY.optString("RSPCOD");
					if ("000000".equals(RSPCOD)) {
						JSONArray managementList = REP_BODY.optJSONArray("managementList");
						Gson gson = new Gson();
						dataList = gson.fromJson(managementList.toString(), new TypeToken<List<Huiyuan>>() {
						}.getType());

						String managementNum = REP_BODY.optString("managementNum");
						String realNameNum = REP_BODY.optString("realNameNum");
						String tip = "您有" + managementNum + "个会员，通过认证的有" + realNameNum + "人";
						tipTxt.setText(tip);

						pager.setAdapter(adapter);
						adapter.notifyDataSetChanged();
						tabs.setViewPager(pager);
						setTabsValue();
					} else {
						String RSPMSG = REP_BODY.optString("RSPMSG");
						ToastHelper.toast(HuiyuanActivity.this,RSPMSG);
					}
				} catch (Exception e) {
					ToastHelper.toast(HuiyuanActivity.this,"服务器错误");
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				ToastHelper.toast(HuiyuanActivity.this,"无法连接服务器");
			}
		});
		MyVolley.addRequest(request);
	}

	private ArrayList<Huiyuan> getData(int position) {
		ArrayList<Huiyuan> data = new ArrayList<Huiyuan>();
		switch (position) {
		case 0:
			for (Huiyuan item : dataList) {
				if ("2".equals(item.getCustStatus())) {
					data.add(item);
				}
			}
			break;
		case 1:
			for (Huiyuan item : dataList) {
				if ("1".equals(item.getCustStatus())) {
					data.add(item);
				}
			}
			break;
		case 2:
			for (Huiyuan item : dataList) {
				if ("0".equals(item.getCustStatus())) {
					data.add(item);
				}
			}
			break;
		case 3:
			for (Huiyuan item : dataList) {
				if ("3".equals(item.getCustStatus())) {
					data.add(item);
				}
			}
			break;
		case 4:
			data.addAll(dataList);
			break;
		default:
			break;
		}
		return data;
	}

	public class MyPagerAdapter extends FragmentPagerAdapter {
		private FragmentManager fm;

		private final String[] titles = { "已认证", "审核中", "未完善", "未通过", "全部" };

		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
			this.fm = fm;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return titles[position];
		}

		@Override
		public int getCount() {
			return 5;
		}

		@Override
		public Fragment getItem(int position) {
			Fragment f = new HuiyuanFragment();
			Bundle b = new Bundle();
			b.putSerializable("data", getData(position));
			f.setArguments(b);
			return f;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			String name = makeFragmentName(container.getId(), position);
			Fragment fragment = fm.findFragmentByTag(name);
			FragmentTransaction ft = fm.beginTransaction();
			if (fragment == null) {
				fragment = getItem(position);
				ft.add(container.getId(), fragment, name);
			}
			ft.attach(fragment);
			ft.commit();
			return fragment;
		}

		private String makeFragmentName(int viewId, int position) {
			return (new StringBuilder()).append("android:switcher:").append(viewId).append(":").append(position).toString();
		}

		@Override
		public int getItemPosition(Object object) {
			return PagerAdapter.POSITION_NONE;
		}
	}

}