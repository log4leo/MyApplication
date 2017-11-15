package com.zjy.wukazhifu.util;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;

public class PostJsonObjectRequest extends Request<JSONObject> {
	private final Listener<JSONObject> mListener;
	private Map<String, String> mParams;
	private String mCookie;
	private CookieUtil mCookieUtil;

	public PostJsonObjectRequest(String url, Map<String, String> params, Listener<JSONObject> listener, ErrorListener errorListener) {
		super(Method.POST, url, errorListener);
		mListener = listener;
		mParams = params;
	}

	public PostJsonObjectRequest(String url, String cookie, Map<String, String> params, Listener<JSONObject> listener, ErrorListener errorListener) {
		super(Method.POST, url, errorListener);
		mListener = listener;
		mParams = params;
		mCookie = cookie;
	}

	public PostJsonObjectRequest(String url, Map<String, String> params, Listener<JSONObject> listener, ErrorListener errorListener, CookieUtil cookieUtil) {
		super(Method.POST, url, errorListener);
		mListener = listener;
		mParams = params;
		mCookieUtil = cookieUtil;
	}

	@Override
	protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
		try {
			if (mCookieUtil != null) {
				Map<String, String> responseHeaders = response.headers;
				String rawCookies = responseHeaders.get("Set-Cookie");
				if(rawCookies!=null && rawCookies.length()>0){
					String cookie = parseVooleyCookie(rawCookies);
					mCookieUtil.getCookie(cookie);
				}
			}

			String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
			return Response.success(new JSONObject(jsonString), HttpHeaderParser.parseCacheHeaders(response));
		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		} catch (JSONException je) {
			return Response.error(new ParseError(je));
		}
	}

	public String parseVooleyCookie(String cookie) {
		StringBuilder sb = new StringBuilder();
		String[] cookies = cookie.split("/");
		for (int i = 0; i < cookies.length; i++) {
			String[] keyPair = cookies[i].split(";");
			if (keyPair != null && keyPair.length > 0) {
				sb.append(keyPair[0]);
				sb.append(";");
			}
		}
		return sb.toString();
	}

	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		Map<String, String> headers = new HashMap<String, String>();
		if (mCookie != null && mCookie.length() > 0) {
			headers.put("Cookie", mCookie);
		}
		return headers;
	}

	@Override
	protected Map<String, String> getParams() throws AuthFailureError {
		return mParams;
	}

	@Override
	protected void deliverResponse(JSONObject response) {
		mListener.onResponse(response);
	}

	public interface CookieUtil {
		public void getCookie(String cookie);
	}

}
