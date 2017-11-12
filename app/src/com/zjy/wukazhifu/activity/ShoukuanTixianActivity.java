package com.zjy.wukazhifu.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.zjy.wukazhifu.R;
import com.zjy.wukazhifu.fragment.ShoukuantixianFragment;
import com.zjy.wukazhifu.view.PagerSlidingTabStrip;

public class ShoukuanTixianActivity extends FragmentActivity implements OnClickListener {

	private ImageView backImg;
	private ImageView searchImg;

	private ShoukuantixianFragment shoukuanFragment = new ShoukuantixianFragment();
	private ShoukuantixianFragment tixianFragment = new ShoukuantixianFragment();

	private DisplayMetrics dm;
	private PagerSlidingTabStrip tabs;
	private MyPagerAdapter adapter;
	private ViewPager pager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shoukuantixian);
		initView();
	}

	private void initView() {
		backImg = (ImageView) findViewById(R.id.backImg);
		backImg.setOnClickListener(this);
		searchImg = (ImageView) findViewById(R.id.searchImg);
		searchImg.setOnClickListener(this);

		dm = getResources().getDisplayMetrics();
		pager = (ViewPager) findViewById(R.id.pager);
		tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		adapter = new MyPagerAdapter(getSupportFragmentManager());
		pager.setAdapter(adapter);
		tabs.setViewPager(pager);
		setTabsValue();
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
		case R.id.searchImg:
			Intent intent = new Intent(ShoukuanTixianActivity.this, OrderSearchActivity.class);
			String busType;
			if (pager.getCurrentItem() == 0) {
				busType = "01";
			} else {
				busType = "03";
			}
			intent.putExtra("busType", busType);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	public class MyPagerAdapter extends FragmentPagerAdapter {
		private FragmentManager fm;

		private final String[] titles = { "收款", "提现" };

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
			return 2;
		}

		@Override
		public Fragment getItem(int position) {
			Bundle b;
			switch (position) {
			case 0:
				if (shoukuanFragment == null) {
					shoukuanFragment = new ShoukuantixianFragment();
				}
				b = new Bundle();
				b.putString("busType", "01");
				shoukuanFragment.setArguments(b);
				return shoukuanFragment;
			case 1:
				if (tixianFragment == null) {
					tixianFragment = new ShoukuantixianFragment();
				}
				b = new Bundle();
				b.putString("busType", "03");
				tixianFragment.setArguments(b);
				return tixianFragment;
			default:
				return null;
			}
		}
	}

}