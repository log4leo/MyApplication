package com.zjy.wukazhifu.util;

import android.content.Context;
import android.widget.ImageView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Volley框架工具类
 * 
 * @author zhou jiyi
 * 
 */
public class MyVolley {
	private static RequestQueue mRequestQueue;
	private static ImageLoader mImageLoader;

	private MyVolley(Context context) {
		HTTPSTrustManager.allowAllSSL();// 信任所有证书
		mRequestQueue = Volley.newRequestQueue(context);
		// BitmapLruCache自定义缓存class，android本身支持二级缓存，在BitmapLruCache封装一个软引用缓存
		mImageLoader = new ImageLoader(mRequestQueue, new BitmapCache());
	}

	public static void init(Context context) {
		HTTPSTrustManager.allowAllSSL();// 信任所有证书
		if (mRequestQueue == null) {
			mRequestQueue = Volley.newRequestQueue(context);
		}
		if (mImageLoader == null) {
			// BitmapLruCache自定义缓存class，android本身支持二级缓存，在BitmapLruCache封装一个软引用缓存
			mImageLoader = new ImageLoader(mRequestQueue, new BitmapCache());
		}
	}

	/**
	 * 得到请求队列对象
	 * 
	 * @return
	 */
	private static RequestQueue getRequestQueue() {
		return mRequestQueue;
	}

	/**
	 * 得到ImageLoader对象
	 * 
	 * @return
	 */
	public static ImageLoader getImageLoader() {
		return mImageLoader;
	}

	public static void addRequest(Request<?> request) {
		request.setRetryPolicy(new DefaultRetryPolicy(30 * 1000, 1, 1.0f));
		getRequestQueue().add(request);
	}

	public static void getImage(String requestUrl, ImageView imageView) {
		getImage(requestUrl, imageView, 0, 0);
	}

	public static void getImage(String requestUrl, ImageView imageView, int defaultImageResId, int errorImageResId) {
		getImage(requestUrl, imageView, defaultImageResId, errorImageResId, 0, 0);
	}

	public static void getImage(String requestUrl, ImageView imageView, int defaultImageResId, int errorImageResId, int maxWidth, int maxHeight) {
		getImageLoader().get(requestUrl, ImageLoader.getImageListener(imageView, defaultImageResId, errorImageResId), maxWidth, maxHeight);
	}
}
