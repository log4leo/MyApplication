package com.zjy.wukazhifu.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.zjy.wukazhifu.R;
import com.zjy.wukazhifu.activity.ADActivity;
import com.zjy.wukazhifu.activity.BangdanActivity;
import com.zjy.wukazhifu.activity.FeilvActivity;
import com.zjy.wukazhifu.activity.HuiyuanActivity;
import com.zjy.wukazhifu.activity.JiaoyiActivity;
import com.zjy.wukazhifu.activity.LoginActivity;
import com.zjy.wukazhifu.activity.OrderActivity;
import com.zjy.wukazhifu.activity.ShareActivity;
import com.zjy.wukazhifu.activity.ShengjiActivity;
import com.zjy.wukazhifu.activity.WodeshouyiActivity;
import com.zjy.wukazhifu.activity.XinshouActivity;
import com.zjy.wukazhifu.activity.YueActivity;
import com.zjy.wukazhifu.entity.Ad;
import com.zjy.wukazhifu.util.Constants;
import com.zjy.wukazhifu.util.MyVolley;
import com.zjy.wukazhifu.util.PostJsonObjectRequest;
import com.zjy.wukazhifu.util.PreferenceHelper;
import com.zjy.wukazhifu.util.ToastHelper;
import com.zjy.wukazhifu.view.LoadingDialog;
import com.zjy.wukazhifu.view.MyViewPager;

public class HomeFragment extends Fragment implements OnClickListener {
	private View view;

	private LinearLayout dmfLayout;
	private LinearLayout wxLayout;
	private LinearLayout zfbLayout;

	private LinearLayout zhyeLayout;
	private LinearLayout frmxLayout;
	private LinearLayout hyglLayout;

	private LinearLayout zdcxLayout;
	private LinearLayout wdflLayout;
	private LinearLayout wysjLayout;

	private LinearLayout lhbdLayout;
	private LinearLayout wyfxLayout;
	private LinearLayout xsrmLayout;

	private String custStatus;
	private String cardBundingStatus;

	// 广告切换
	private int previousPointEnale = 0;
	private LinearLayout pointLayout;
	private MyViewPager mViewPager;
	private MyAdapter myAdapter;
	private List<ImageView> mImageList = new ArrayList<ImageView>();
	private List<Ad> mAdList = new ArrayList<Ad>();
	Handler handler = new Handler();
	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			try {
				mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
				handler.postDelayed(this, 5000);
			} catch (Exception e) {
			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_home, container, false);
		initView();
		my();
		ads();
		getHongbao();
		return view;
	}

	private void initView() {
		mViewPager = (MyViewPager) view.findViewById(R.id.viewpager);
		pointLayout = (LinearLayout) view.findViewById(R.id.pointLayout);

		dmfLayout = (LinearLayout) view.findViewById(R.id.dmfLayout);
		wxLayout = (LinearLayout) view.findViewById(R.id.wxLayout);
		zfbLayout = (LinearLayout) view.findViewById(R.id.zfbLayout);

		zhyeLayout = (LinearLayout) view.findViewById(R.id.zhyeLayout);
		frmxLayout = (LinearLayout) view.findViewById(R.id.frmxLayout);
		hyglLayout = (LinearLayout) view.findViewById(R.id.hyglLayout);

		zdcxLayout = (LinearLayout) view.findViewById(R.id.zdcxLayout);
		wdflLayout = (LinearLayout) view.findViewById(R.id.wdflLayout);
		wysjLayout = (LinearLayout) view.findViewById(R.id.wysjLayout);

		lhbdLayout = (LinearLayout) view.findViewById(R.id.lhbdLayout);
		wyfxLayout = (LinearLayout) view.findViewById(R.id.wyfxLayout);
		xsrmLayout = (LinearLayout) view.findViewById(R.id.xsrmLayout);

		dmfLayout.setOnClickListener(this);
		wxLayout.setOnClickListener(this);
		zfbLayout.setOnClickListener(this);

		zhyeLayout.setOnClickListener(this);
		frmxLayout.setOnClickListener(this);
		hyglLayout.setOnClickListener(this);

		zdcxLayout.setOnClickListener(this);
		wdflLayout.setOnClickListener(this);
		wysjLayout.setOnClickListener(this);

		lhbdLayout.setOnClickListener(this);
		wyfxLayout.setOnClickListener(this);
		xsrmLayout.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		if (!PreferenceHelper.getLogin()) {
			intent = new Intent(getActivity(), LoginActivity.class);
			startActivity(intent);
			getActivity().finish();
			return;
		}
		switch (v.getId()) {
		case R.id.dmfLayout:
			if (!"2".equals(custStatus) || !"2".equals(cardBundingStatus)) {
				String msg = "";
				if ("0".equals(custStatus)) {
					msg += "请先通过实名认证，并绑定结算卡！";
					ToastHelper.toast(getActivity(), msg);
					return;
				} else if ("1".equals(custStatus)) {
					msg += "您的实名认证正在审核中\n";
				} else if ("3".equals(custStatus)) {
					msg += "您的实名认证需重新认证 \n";
				}
				if ("0".equals(cardBundingStatus)) {
					msg += "请先绑定结算卡！";
				} else if ("1".equals(cardBundingStatus)) {
					msg += "您的银行卡正在审核中";
				} else if ("3".equals(cardBundingStatus)) {
					msg += "您的银行卡审核未通过";
				}
				ToastHelper.toast(getActivity(), msg);
				return;
			}
			intent = new Intent(getActivity(), JiaoyiActivity.class);
			intent.putExtra("type", "dmf");
			startActivity(intent);
			break;
		case R.id.wxLayout:
			if (!"2".equals(custStatus) || !"2".equals(cardBundingStatus)) {
				String msg = "";
				if ("0".equals(custStatus)) {
					msg += "请先通过实名认证，并绑定结算卡！";
					ToastHelper.toast(getActivity(), msg);
					return;
				} else if ("1".equals(custStatus)) {
					msg += "您的实名认证正在审核中\n";
				} else if ("3".equals(custStatus)) {
					msg += "您的实名认证需重新认证 \n";
				}
				if ("0".equals(cardBundingStatus)) {
					msg += "请先绑定结算卡！";
				} else if ("1".equals(cardBundingStatus)) {
					msg += "您的银行卡正在审核中";
				} else if ("3".equals(cardBundingStatus)) {
					msg += "您的银行卡审核未通过";
				}
				ToastHelper.toast(getActivity(), msg);
				return;
			}
			intent = new Intent(getActivity(), JiaoyiActivity.class);
			intent.putExtra("type", "wx");
			startActivity(intent);
			break;
		case R.id.zfbLayout:
			if (!"2".equals(custStatus) || !"2".equals(cardBundingStatus)) {
				String msg = "";
				if ("0".equals(custStatus)) {
					msg += "请先通过实名认证，并绑定结算卡！";
					ToastHelper.toast(getActivity(), msg);
					return;
				} else if ("1".equals(custStatus)) {
					msg += "您的实名认证正在审核中\n";
				} else if ("3".equals(custStatus)) {
					msg += "您的实名认证需重新认证 \n";
				}
				if ("0".equals(cardBundingStatus)) {
					msg += "请先绑定结算卡！";
				} else if ("1".equals(cardBundingStatus)) {
					msg += "您的银行卡正在审核中";
				} else if ("3".equals(cardBundingStatus)) {
					msg += "您的银行卡审核未通过";
				}
				ToastHelper.toast(getActivity(), msg);
				return;
			}
			intent = new Intent(getActivity(), JiaoyiActivity.class);
			intent.putExtra("type", "zfb");
			startActivity(intent);
			break;
		case R.id.zhyeLayout:
			intent = new Intent(getActivity(), YueActivity.class);
			startActivity(intent);
			break;
		case R.id.frmxLayout:
			intent = new Intent(getActivity(), WodeshouyiActivity.class);
			startActivity(intent);
			break;
		case R.id.hyglLayout:
			intent = new Intent(getActivity(), HuiyuanActivity.class);
			startActivity(intent);
			break;
		case R.id.zdcxLayout:
			intent = new Intent(getActivity(), OrderActivity.class);
			startActivity(intent);
			break;
		case R.id.wdflLayout:
			intent = new Intent(getActivity(), FeilvActivity.class);
			startActivity(intent);
			break;
		case R.id.wysjLayout:
			intent = new Intent(getActivity(), ShengjiActivity.class);
			startActivity(intent);
			break;
		case R.id.lhbdLayout:
			intent = new Intent(getActivity(), BangdanActivity.class);
			startActivity(intent);
			break;
		case R.id.wyfxLayout:
			intent = new Intent(getActivity(), ShareActivity.class);
			startActivity(intent);
			break;
		case R.id.xsrmLayout:
			intent = new Intent(getActivity(), XinshouActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	private void getHongbao() {
		String url = Constants.ADDR_hongbao;
		String cookie = PreferenceHelper.getCookie();
		PostJsonObjectRequest request = new PostJsonObjectRequest(url, cookie, null, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					JSONObject REP_BODY = response.optJSONObject("REP_BODY");
					String RSPCOD = REP_BODY.optString("RSPCOD");
					if ("000000".equals(RSPCOD)) {
						String redNum00 = REP_BODY.optString("redNum00");
						if (redNum00 == null || redNum00.length() == 0 || "0".equals(redNum00)) {
						} else {
							final AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
							dialog.show();
							View view = LayoutInflater.from(getActivity()).inflate(R.layout.alert_hongbaotishi, null);
							Window window = dialog.getWindow();
							dialog.setContentView(view);
							WindowManager.LayoutParams lp = window.getAttributes();
							window.setGravity(Gravity.CENTER);
							DisplayMetrics dm = getActivity().getResources().getDisplayMetrics();
							lp.width = (int) (dm.widthPixels * 0.8);
							window.setAttributes(lp);

							ImageView closeImg = (ImageView) view.findViewById(R.id.closeImg);
							LinearLayout openLayout = (LinearLayout) view.findViewById(R.id.openLayout);

							closeImg.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									dialog.dismiss();
								}
							});
							openLayout.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									dialog.dismiss();
									Intent intent = new Intent(getActivity(), WodeshouyiActivity.class);
									intent.putExtra("open", true);
									startActivity(intent);
								}
							});
						}
					} else {
						String RSPMSG = REP_BODY.optString("RSPMSG");
						ToastHelper.toast(getActivity(), RSPMSG);
					}
				} catch (Exception e) {
					ToastHelper.toast(getActivity(), "服务器错误");
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				ToastHelper.toast(getActivity(), "无法连接服务器");
			}
		});
		MyVolley.addRequest(request);
	}

	public void my() {
		if (!PreferenceHelper.getLogin()) {
			return;
		}
		LoadingDialog.show(getActivity());
		String url = Constants.ADDR_my;
		Map<String, String> param = new HashMap<String, String>();
		param.put("custLogin", PreferenceHelper.getCustLogin());
		String cookie = PreferenceHelper.getCookie();
		PostJsonObjectRequest request = new PostJsonObjectRequest(url, cookie, param, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				LoadingDialog.dismiss();
				try {
					JSONObject REP_BODY = response.optJSONObject("REP_BODY");
					String RSPCOD = REP_BODY.optString("RSPCOD");

					if ("000000".equals(RSPCOD)) {
						custStatus = REP_BODY.optString("custStatus");
						cardBundingStatus = REP_BODY.optString("cardBundingStatus");
					} else {
						String RSPMSG = REP_BODY.optString("RSPMSG");
						ToastHelper.toast(getActivity(), RSPMSG);
					}
				} catch (Exception e) {
					ToastHelper.toast(getActivity(), "服务器错误");
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				LoadingDialog.dismiss();
				ToastHelper.toast(getActivity(), "无法连接服务器");
			}
		});
		MyVolley.addRequest(request);
	}

	public void ads() {
		String url = Constants.ADDR_ads;
		String cookie = PreferenceHelper.getCookie();
		Map<String, String> param = new HashMap<String, String>();
		param.put("oemId", "8170600232");
		PostJsonObjectRequest request = new PostJsonObjectRequest(url, cookie, param, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					JSONObject REP_BODY = response.optJSONObject("REP_BODY");
					String RSPCOD = REP_BODY.optString("RSPCOD");
					if ("000000".equals(RSPCOD)) {
						JSONArray data = REP_BODY.optJSONArray("imgList");
						mImageList.clear();
						mAdList.clear();
						pointLayout.removeAllViews();
						ImageView mImageView;
						LayoutParams params;
						for (int i = 0; i < data.length(); i++) {
							JSONObject jo = data.optJSONObject(i);
							String path = jo.optString("appimgPath");
							String url = jo.optString("appimgDesc");
							Ad ad = new Ad();
							ad.setAppimgDesc(url);
							ad.setAppimgPath(path);
							mImageView = new ImageView(getActivity());
							mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
							MyVolley.getImage(path, mImageView);
							mImageList.add(mImageView);
							mAdList.add(ad);
							View dot = new View(getActivity());
							dot.setBackgroundResource(R.drawable.point_background);
							params = new LayoutParams(10, 10);
							params.leftMargin = 10;
							dot.setLayoutParams(params);
							dot.setEnabled(false);
							pointLayout.addView(dot);
						}
						if (mImageList.size() > 0) {
							myAdapter = new MyAdapter();
							mViewPager.setAdapter(myAdapter);
							mViewPager.setOnPageChangeListener(new MyListener());
							pointLayout.getChildAt(0).setEnabled(true);
							if (mImageList.size() > 1) {
								handler.postDelayed(runnable, 5000);
							}
						}
					} else {
						String RSPMSG = REP_BODY.optString("RSPMSG");
						ToastHelper.toast(getActivity(), RSPMSG);
					}
				} catch (Exception e) {
					ToastHelper.toast(getActivity(), "服务器错误");
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				ToastHelper.toast(getActivity(), "无法连接服务器");
			}
		});
		MyVolley.addRequest(request);
	}

	private class MyListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int arg0) {
			int newPosition = arg0 % mImageList.size();
			pointLayout.getChildAt(previousPointEnale).setEnabled(false);
			pointLayout.getChildAt(newPosition).setEnabled(true);
			previousPointEnale = newPosition;
		}
	}

	private class MyAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return mImageList.size() == 0 ? 0 : Integer.MAX_VALUE;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			ImageView iv;
			final Ad ad = mAdList.get(position % mAdList.size());
			if (position > mImageList.size() - 1) {
				iv = new ImageView(getActivity());
				iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
				String url = ad.getAppimgPath();
				MyVolley.getImage(url, iv);
			} else {
				iv = mImageList.get(position % mImageList.size());
			}
			iv.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					String url = ad.getAppimgDesc();
					if (url != null && url.length() > 0 && url.startsWith("http")) {
						Intent intent = new Intent(getActivity(), ADActivity.class);
						intent.putExtra("url", url);
						startActivity(intent);
					}
				}
			});
			container.addView(iv);
			return iv;
		}

	}
}
