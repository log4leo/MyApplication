package com.zjy.wukazhifu.activity;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.zjy.wukazhifu.R;
import com.zjy.wukazhifu.fragment.HomeFragment;
import com.zjy.wukazhifu.fragment.MsgFragment;
import com.zjy.wukazhifu.fragment.MyFragment;
import com.zjy.wukazhifu.update.Updater;
import com.zjy.wukazhifu.update.UpdaterConfig;
import com.zjy.wukazhifu.update.UpdaterUtils;
import com.zjy.wukazhifu.util.Constants;
import com.zjy.wukazhifu.util.MyVolley;
import com.zjy.wukazhifu.util.PostJsonObjectRequest;
import com.zjy.wukazhifu.util.PreferenceHelper;
import com.zjy.wukazhifu.util.ToolUtil;
import com.zjy.wukazhifu.view.LoadingDialog;
import com.zjy.wukazhifu.view.MyAlert;

public class MainActivity extends FragmentActivity implements OnClickListener {

	private ViewPager pager;

	private LinearLayout homeLayout;
	private LinearLayout msgLayout;
	private LinearLayout myLayout;

	private ImageView homeImg;
	private ImageView msgImg;
	private ImageView myImg;

	private TextView homeTxt;
	private TextView msgTxt;
	private TextView myTxt;

	private HomeFragment homeFragment = new HomeFragment();
	private MsgFragment msgFragment = new MsgFragment();
	private MyFragment myFragment = new MyFragment();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
		update();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		int page = intent.getIntExtra("page", 0);
		pager.setCurrentItem(page, true);
		changePage(page);
		if (page == 2 && myFragment != null) {
			myFragment.my();
			myFragment.inThispage = true;
		}
		super.onNewIntent(intent);
	}

	private void initView() {
		homeLayout = (LinearLayout) findViewById(R.id.homeLayout);
		msgLayout = (LinearLayout) findViewById(R.id.msgLayout);
		myLayout = (LinearLayout) findViewById(R.id.myLayout);

		homeLayout.setOnClickListener(this);
		msgLayout.setOnClickListener(this);
		myLayout.setOnClickListener(this);

		homeImg = (ImageView) findViewById(R.id.homeImg);
		msgImg = (ImageView) findViewById(R.id.msgImg);
		myImg = (ImageView) findViewById(R.id.myImg);

		homeTxt = (TextView) findViewById(R.id.homeTxt);
		msgTxt = (TextView) findViewById(R.id.msgTxt);
		myTxt = (TextView) findViewById(R.id.myTxt);

		pager = (ViewPager) findViewById(R.id.pager);
		pager.setOffscreenPageLimit(2);
		pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
		pager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				if (!PreferenceHelper.getLogin() && arg0 != 0) {
					Intent intent = new Intent(MainActivity.this, LoginActivity.class);
					startActivity(intent);
					MainActivity.this.finish();
				} else {
					changePage(arg0);
				}
				if (arg0 == 0 && homeFragment != null) {
					homeFragment.my();
					myFragment.inThispage = false;
				} else if (arg0 == 2 && myFragment != null) {
					myFragment.my();
					myFragment.inThispage = true;
				} else  {
					if(msgFragment != null){
						msgFragment.msg();
					}
					myFragment.inThispage = false;
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
		changePage(0);
	}

	public int getCurPage() {
		return pager.getCurrentItem();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.homeLayout:
			pager.setCurrentItem(0, true);
			changePage(0);
			break;
		case R.id.msgLayout:
			if (!PreferenceHelper.getLogin()) {
				Intent intent = new Intent(MainActivity.this, LoginActivity.class);
				startActivity(intent);
				MainActivity.this.finish();
			} else {
				pager.setCurrentItem(1, true);
				changePage(1);
			}
			break;
		case R.id.myLayout:
			if (!PreferenceHelper.getLogin()) {
				Intent intent = new Intent(MainActivity.this, LoginActivity.class);
				startActivity(intent);
				MainActivity.this.finish();
			} else {
				pager.setCurrentItem(2, true);
				changePage(2);
			}
			break;
		default:
			break;
		}
	}

	private void changePage(int index) {
		homeImg.setImageResource(R.drawable.home);
		msgImg.setImageResource(R.drawable.msg);
		myImg.setImageResource(R.drawable.my);

		homeTxt.setTextColor(getResources().getColor(R.color.gray));
		msgTxt.setTextColor(getResources().getColor(R.color.gray));
		myTxt.setTextColor(getResources().getColor(R.color.gray));

		switch (index) {
		case 0:
			homeImg.setImageResource(R.drawable.home_select);
			homeTxt.setTextColor(getResources().getColor(R.color.theme));
			break;
		case 1:
			msgImg.setImageResource(R.drawable.msg_select);
			msgTxt.setTextColor(getResources().getColor(R.color.theme));
			break;
		case 2:
			myImg.setImageResource(R.drawable.my_select);
			myTxt.setTextColor(getResources().getColor(R.color.theme));
			break;
		default:
			break;
		}
	}

	public class MyPagerAdapter extends FragmentPagerAdapter {

		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() {
			return 3;
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0:
				if (homeFragment == null) {
					homeFragment = new HomeFragment();
				}
				return homeFragment;
			case 1:
				if (msgFragment == null) {
					msgFragment = new MsgFragment();
				}
				return msgFragment;
			case 2:
				if (myFragment == null) {
					myFragment = new MyFragment();
				}
				return myFragment;
			default:
				return null;
			}
		}
	}

	/**
	 * 返回键响应
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exitBy2Click(); // 调用双击退出函数
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 双击退出函数
	 */
	private static Boolean isExit = false;

	private void exitBy2Click() {
		Timer tExit = null;
		if (isExit == false) {
			isExit = true; // 准备退出
			Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
			tExit = new Timer();
			tExit.schedule(new TimerTask() {
				@Override
				public void run() {
					isExit = false; // 取消退出
				}
			}, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

		} else {
			finish();
			System.exit(0);
		}
	}

	private void update() {
		String url = Constants.ADDR_update;
		String cookie = PreferenceHelper.getCookie();
		Map<String, String> param = new HashMap<String, String>();
		param.put("oemId", "8170600232");
		param.put("sysType", "2");
		param.put("appName", "ssbh");
		param.put("appVersion", ToolUtil.getLocalVersionName(this));
		PostJsonObjectRequest request = new PostJsonObjectRequest(url, cookie, param, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					JSONObject REP_BODY = response.optJSONObject("REP_BODY");
					String RSPCOD = REP_BODY.optString("RSPCOD");
					if ("000000".equals(RSPCOD)) {
						String checkState = REP_BODY.optString("checkState");
						if (!"0".equals(checkState)) {
							String versionStates = REP_BODY.optString("versionStates");
							String fileUrl = REP_BODY.optString("fileUrl");
							String fileDesc = REP_BODY.optString("fileDesc");
							final String apkurl = fileUrl;
							if ("1".equals(versionStates)) {
								View contentView = LayoutInflater.from(MainActivity.this).inflate(R.layout.alert_text1, null);
								TextView txt1 = (TextView) contentView.findViewById(R.id.txt1);
								txt1.setText(fileDesc);
								MyAlert.confirm(MainActivity.this, "版本更新", "立即更新", "稍后更新", contentView, new OnClickListener() {
									@Override
									public void onClick(View v) {
										MyAlert.dismiss();
										UpdaterConfig config = new UpdaterConfig.Builder(MainActivity.this).setTitle(getResources().getString(R.string.app_name)).setDescription("版本更新")
												.setFileUrl(apkurl).setCanMediaScanner(true).build();
										Updater.get().showLog(true).download(config);

									}
								});
							} else if ("3".equals(versionStates)) {
								View contentView = LayoutInflater.from(MainActivity.this).inflate(R.layout.alert_text1, null);
								TextView txt1 = (TextView) contentView.findViewById(R.id.txt1);
								txt1.setText(fileDesc);
								MyAlert.alert(MainActivity.this, "版本更新", "立即更新", contentView, new OnClickListener() {

									@Override
									public void onClick(View v) {
										MyAlert.dismiss();
										UpdaterConfig config = new UpdaterConfig.Builder(MainActivity.this).setTitle(getResources().getString(R.string.app_name)).setDescription("版本更新")
												.setFileUrl(apkurl).setCanMediaScanner(true).setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN).build();
										Updater.get().showLog(true).download(config);
										LoadingDialog.show(MainActivity.this, "下载中...");
									}
								}, new OnCancelListener() {

									@Override
									public void onCancel(DialogInterface dialog) {
										MainActivity.this.finish();
										System.exit(0);
									}
								});
							}
						}
					}
				} catch (Exception e) {
					// ToastHelper.toast(MainActivity.this,"服务器错误");
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				// ToastHelper.toast(MainActivity.this,"无法连接服务器");
			}
		});
		MyVolley.addRequest(request);
	}

	public class ApkInstallReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			LoadingDialog.dismiss();
			if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
				long downloadApkId = UpdaterUtils.getLocalDownloadId(MainActivity.this);
				installApk(context, downloadApkId);
			}else if(DownloadManager.ACTION_NOTIFICATION_CLICKED.equals(intent.getAction())){  
                long[] ids = intent.getLongArrayExtra(DownloadManager.EXTRA_NOTIFICATION_CLICK_DOWNLOAD_IDS);  
                //点击通知栏取消下载  
                DownloadManager manager = (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
                manager.remove(ids);  
            }  
		}

		private void installApk(Context context, long downloadApkId) {
			DownloadManager dManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
			Intent install = new Intent(Intent.ACTION_VIEW);
			Uri downloadFileUri = dManager.getUriForDownloadedFile(downloadApkId);
			if (downloadFileUri != null) {
				install.setDataAndType(downloadFileUri, "application/vnd.android.package-archive");
				install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(install);
				MainActivity.this.finish();
			} else {
				MainActivity.this.finish();
			}
		}
	}

	private ApkInstallReceiver receiver = new ApkInstallReceiver();

	protected void onResume() {
		super.onResume();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(DownloadManager.ACTION_NOTIFICATION_CLICKED);
		intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
		registerReceiver(receiver, intentFilter);
	};

	protected void onPause() {
		super.onPause();
		unregisterReceiver(receiver);

	};
}