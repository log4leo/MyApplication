package com.zjy.wukazhifu.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.zjy.wukazhifu.R;
import com.zjy.wukazhifu.fragment.PutongFragment;
import com.zjy.wukazhifu.fragment.ShengjiFragment;
import com.zjy.wukazhifu.util.PreferenceHelper;
import com.zjy.wukazhifu.view.PagerSlidingTabStrip;

public class ShouyiActivity extends FragmentActivity implements OnClickListener {

	private ImageView backImg;
	private ImageView searchImg;

	private PutongFragment putongFragment = new PutongFragment();
	private ShengjiFragment shengjiFragment = new ShengjiFragment();

	private DisplayMetrics dm;
	private PagerSlidingTabStrip tabs;
	private MyPagerAdapter adapter;
	private ViewPager pager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shouyi);
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
		tabs.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				if (arg0 == 0) {
					searchImg.setVisibility(View.VISIBLE);
				}else{
					searchImg.setVisibility(View.GONE);
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
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
		Intent intent;
		switch (v.getId()) {
		case R.id.backImg:
			finish();
			break;
		case R.id.searchImg:
			intent = new Intent(ShouyiActivity.this, ShouyiSearchActivity.class);
			startActivity(intent);
			/*if (pager.getCurrentItem() == 0) {
				intent = new Intent(ShouyiActivity.this, ShouyiSearchActivity.class);
				startActivity(intent);
			} else {
				intent = new Intent(ShouyiActivity.this, HongbaoSearchActivity.class);
				startActivity(intent);
			}*/
			break;
		default:
			break;
		}
	}

	public class MyPagerAdapter extends FragmentPagerAdapter {
		private FragmentManager fm;

		private final String[] titles = { "交易收入", "红包收入" };

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
			switch (position) {
			case 0:
				if (putongFragment == null) {
					putongFragment = new PutongFragment();
				}
				return putongFragment;
			case 1:
				if (shengjiFragment == null) {
					shengjiFragment = new ShengjiFragment();
				}
				return shengjiFragment;
			default:
				return null;
			}
		}
	}

}