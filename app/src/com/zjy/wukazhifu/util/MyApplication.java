package com.zjy.wukazhifu.util;

import android.app.Application;

import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

public class MyApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		MyVolley.init(this);
		PreferenceHelper.init(this);
		ToastHelper.init(this);

		Config.DEBUG = true;
		UMShareAPI.get(this);
	}

	{
		PlatformConfig.setWeixin("wx01cf7249b6556d48", "e759dd09fa8c60dbc49d4477a462128b");
		// PlatformConfig.setQQZone("", "");
	}

	@Override
	public void onTerminate() {
		// 程序终止的时候执行
		UMShareAPI.get(this).release();
		super.onTerminate();
	}

}
