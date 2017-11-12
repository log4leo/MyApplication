package com.zjy.wukazhifu.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zjy.wukazhifu.R;
import com.zjy.wukazhifu.util.PreferenceHelper;
import com.zjy.wukazhifu.util.ToolUtil;

public class SplashActivity extends Activity {
	private TextView banbenTxt;
	private int previousPointEnale = 0;
	private LinearLayout pointLayout;
	private ViewPager mViewPager;
	private MyAdapter myAdapter;
	private List<ImageView> mImageList = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		banbenTxt = (TextView) findViewById(R.id.banbenTxt);
		banbenTxt.setText("版本："+ToolUtil.getLocalVersionName(this));
		if (PreferenceHelper.getFirst()) {
			new Handler().postDelayed(new Runnable() {
				public void run() {
					if (PreferenceHelper.getLogin()) {
						Intent intent = new Intent(SplashActivity.this, MainActivity.class);
						startActivity(intent);
						SplashActivity.this.finish();
					} else {
						Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
						startActivity(intent);
						SplashActivity.this.finish();
					}
				}
			}, 2000);
		} else {
			PreferenceHelper.setFirst(true);
			initView();
		}
	}

	private void initView() {
		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		pointLayout = (LinearLayout) findViewById(R.id.pointLayout);

		mImageList.clear();
		pointLayout.removeAllViews();
		ImageView mImageView;
		LinearLayout.LayoutParams params;

		mImageView = new ImageView(SplashActivity.this);
		mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		mImageView.setImageResource(R.drawable.pic2);
		mImageList.add(mImageView);

		mImageView = new ImageView(SplashActivity.this);
		mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		mImageView.setImageResource(R.drawable.pic3);
		mImageList.add(mImageView);

		mImageView = new ImageView(SplashActivity.this);
		mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		mImageView.setImageResource(R.drawable.pic4);
		mImageList.add(mImageView);
		mImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
				startActivity(intent);
				SplashActivity.this.finish();
			}
		});

		for (int i = 0; i < mImageList.size(); i++) {
			View dot = new View(SplashActivity.this);
			dot.setBackgroundResource(R.drawable.point_background);
			params = new LinearLayout.LayoutParams(30, 30);
			params.leftMargin = 50;
			dot.setLayoutParams(params);
			dot.setEnabled(false);
			pointLayout.addView(dot);
		}

		myAdapter = new MyAdapter();
		mViewPager.setAdapter(myAdapter);
		mViewPager.setOnPageChangeListener(new MyListener());
		pointLayout.getChildAt(0).setEnabled(true);
	}

	private class MyListener implements ViewPager.OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int arg0) {
			pointLayout.getChildAt(previousPointEnale).setEnabled(false);
			pointLayout.getChildAt(arg0).setEnabled(true);
			previousPointEnale = arg0;
		}
	}

	private class MyAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return mImageList.size();
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			try {
				container.addView(mImageList.get(position));
			} catch (Exception e) {
			}
			return mImageList.get(position);
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			// container.removeView(mImageList.get(position %
			// mImageList.size()));
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}
	}

}
