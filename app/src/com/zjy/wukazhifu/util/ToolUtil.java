package com.zjy.wukazhifu.util;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Base64;

public class ToolUtil {
	/**
	 * 验证手机号是否正确
	 */
	public static boolean phoneValidation(String phone) {
		String regex = "^[1][3,4,5,7,8][0-9]{9}$";
		return phone.matches(regex);
	}
	
	/**
	 * 验证密码
	 */
	public static boolean pwdValidation(String phone) {
		String regex = "[0-9a-zA-Z~!@#$%^&*()_+|<>,.?/:;'\\[\\]{}\"]{6,16}";
		return phone.matches(regex);
	}
	
	/**
	 * 验证是否是中文字符串
	 * @param string
	 * @return
	 */
	public static boolean isChinese(String string){
	    int n = 0;
	    for(int i = 0; i < string.length(); i++) {
	        n = (int)string.charAt(i);
	        if(!(19968 <= n && n <40869)) {
	            return false;
	        }
	    }
	    return true;
	}

	/**
	 * 验证邮箱格式是否正确
	 */
	public static boolean emailValidation(String email) {
		String regex = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
		return email.matches(regex);
	}

	/**
	 * 获取应用程序的版本号
	 * 
	 * @param context
	 * @return
	 */
	public static int getLocalVersionCode(Context context) {
		PackageManager pm = context.getPackageManager();
		try {
			PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
			return info.versionCode;
		} catch (NameNotFoundException e) {
			return -1;
		}
	}

	/**
	 * 获取应用程序的包名
	 * 
	 * @param context
	 * @return
	 */
	public static String getPackageName(Context context) {
		PackageManager pm = context.getPackageManager();
		try {
			PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
			return info.packageName;
		} catch (NameNotFoundException e) {
			return null;
		}
	}

	public static int getSDKVersionNumber() {
		int sdkVersion;
		try {
			sdkVersion = android.os.Build.VERSION.SDK_INT;
		} catch (NumberFormatException e) {
			sdkVersion = 0;
		}
		return sdkVersion;
	}

	/**
	 * 获取应用程序的版本名称
	 * 
	 * @param context
	 * @return
	 */
	public static String getLocalVersionName(Context context) {
		PackageManager pm = context.getPackageManager();
		try {
			PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
			return info.versionName;
		} catch (NameNotFoundException e) {
			return "";
		}
	}

	public static String getMoney(String s, boolean signFlag) {
		String result = "";
		String sign = "";
		if (s == null || s.length() < 1) {
			return "";
		}
		double num = 0;
		try {
			num = Double.parseDouble(s);
			num = num / 100.00;
		} catch (Exception e) {
		}
		if (num > 0) {
			sign = "+ ";
		} else if (num < 0) {
			sign = "- ";
		}
		NumberFormat formater = new DecimalFormat("###,##0.00");
		result = formater.format(num);
		if (signFlag) {
			result = sign + result;
		}
		return result;
	}

	public static String getMoney2(String s, boolean signFlag) {
		String result = "";
		String sign = "";
		if (s == null || s.length() < 1) {
			return "";
		}
		double num = 0;
		try {
			num = Double.parseDouble(s);
		} catch (Exception e) {
		}
		if (num > 0) {
			sign = "+ ";
		} else if (num < 0) {
			sign = "- ";
		}
		NumberFormat formater = new DecimalFormat("###,##0.00");
		result = formater.format(num);
		if (signFlag) {
			result = sign + result;
		}
		return result;
	}

	public static String formatTime(String time) {
		String result = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date dateTime = sdf.parse(time);
			result = formatter.format(dateTime);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static String formatMoney(String s) {
		String result = "";
		double num = 0;
		try {
			num = Double.parseDouble(s);
			num = num / 100.00;
		} catch (Exception e) {
		}
		NumberFormat formater = new DecimalFormat("###0.00");
		result = formater.format(num);
		return result;
	}

	// 加密
	public static String getBase64(String str) {
		String result = "";
		if (str != null) {
			try {
				result = new String(Base64.encode(str.getBytes("utf-8"), Base64.NO_WRAP), "utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	// 解密
	public static String getFromBase64(String str) {
		String result = "";
		if (str != null) {
			try {
				result = new String(Base64.decode(str, Base64.NO_WRAP), "utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

}
