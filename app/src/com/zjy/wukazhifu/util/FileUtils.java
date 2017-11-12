package com.zjy.wukazhifu.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

public class FileUtils {

	public static String APP_PATH = "shengshubaohe";

	public static boolean isSdcardExit() {
		if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
			return true;
		else
			return false;
	}

	public static String getAppPath() {
		String path = "";
		if (isSdcardExit()) {
			String fileBasePath = Environment.getExternalStorageDirectory().getAbsolutePath();
			path = fileBasePath + File.separator + APP_PATH;
		}
		return path;
	}

	/**
	 * 获得从相册选择的图片的路径
	 * 
	 * @param uri
	 * @return
	 */
	public static String getImagePath(Context context, Uri uri) {
		String imagePath = "";
		Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
		if (cursor != null && cursor.moveToFirst()) {
			imagePath = cursor.getString(1); // 图片文件路径
		} else if (cursor == null) {
			imagePath = uri.toString().substring(7, uri.toString().length());
		}
		if (cursor != null)
			cursor.close();
		return imagePath;
	}

	/**
	 * 获取图片
	 * 
	 * @param path
	 * @return
	 */
	public static Bitmap getBitmap(String path) {
		Bitmap bitmap = null;
		try {
			File file = new File(path);
			if (file.exists()) {
				BitmapFactory.Options opts = new BitmapFactory.Options();
				opts.inPreferredConfig = Config.ARGB_8888;
				opts.inPurgeable = true;
				opts.inInputShareable = true;
				bitmap = BitmapFactory.decodeFile(path, opts);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	/**
	 * 复制图片
	 * 
	 * @param fileFrom
	 * @param fileTo
	 * @return
	 */
	public static boolean copy(String fileFrom, String fileTo) {
		try {
			File f = new File(fileTo);
			File pFile = f.getParentFile();
			if (!pFile.exists()) {
				pFile.mkdirs();
			}
			if (f.exists()) {
				f.delete();
			}
			FileInputStream in = new java.io.FileInputStream(fileFrom);
			FileOutputStream out = new FileOutputStream(fileTo);
			byte[] bt = new byte[1024];
			int count;
			while ((count = in.read(bt)) > 0) {
				out.write(bt, 0, count);
			}
			in.close();
			out.close();
			return true;
		} catch (IOException ex) {
			return false;
		}
	}

	/**
	 * 保存图片
	 * 
	 * @param bmp
	 * @param imgName
	 */
	public static File savePic(Bitmap bmp, String path) {
		File f = null;
		try {
			f = new File(path);
			File pFile = f.getParentFile();
			if (!pFile.exists()) {
				pFile.mkdirs();
			}
			if (f.exists()) {
				f.delete();
			}
			f.createNewFile();
			FileOutputStream fout = new FileOutputStream(f);
			bmp.compress(Bitmap.CompressFormat.JPEG, 100, fout);
			// bmp.recycle();
			fout.flush();
			fout.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return f;
	}

	public static File savePic(Context context, Bitmap bmp, String path) {
		File f = null;
		try {
			f = new File(path);
			File pFile = f.getParentFile();
			if (!pFile.exists()) {
				pFile.mkdirs();
			}
			if (f.exists()) {
				f.delete();
			}
			f.createNewFile();
			FileOutputStream fout = new FileOutputStream(f);
			bmp.compress(Bitmap.CompressFormat.JPEG, 100, fout);
			// bmp.recycle();
			fout.flush();
			fout.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 其次把文件插入到系统图库
		try {
			MediaStore.Images.Media.insertImage(context.getContentResolver(), f.getAbsolutePath(), f.getName(), null);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		// 最后通知图库更新
		context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(f)));
		return f;
	}

	/**
	 * 缩放图片
	 * 
	 * @param path
	 * @param size
	 * @return
	 */
	public static Bitmap scalePic(String path, int size) {
		Bitmap bitmap = null;
		File file = new File(path);
		if (file.exists()) {
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(path, o);
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = Math.max(width_tmp, height_tmp) / size;
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			if (scale < 1) {
				scale = 1;
			}
			o2.inSampleSize = scale;
			o2.inPreferredConfig = Config.ARGB_8888;
			o2.inPurgeable = true;
			o2.inInputShareable = true;
			bitmap = BitmapFactory.decodeFile(path, o2);
		}
		return bitmap;
	}

	/**
	 * 缩放图片
	 * 
	 * @param path
	 * @param size
	 * @return
	 */
	public static Bitmap scalePic(Bitmap bmp, int size) {
		Bitmap bitmap = null;
		if (bmp != null) {
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			byte[] data = bitmap2Bytes(bmp);
			BitmapFactory.decodeByteArray(data, 0, data.length, o);
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = Math.max(width_tmp, height_tmp) / size;
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			if (scale < 1) {
				scale = 1;
			}
			o2.inSampleSize = scale;
			o2.inPreferredConfig = Config.RGB_565;
			o2.inPurgeable = true;
			o2.inInputShareable = true;
			bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, o2);
		}
		return bitmap;
	}

	public static Bitmap compressAndGenImage(Bitmap image, int maxSize) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		// scale
		int options = 100;
		// Store the bitmap into output stream(no compress)
		image.compress(Bitmap.CompressFormat.JPEG, options, os);
		// Compress by loop
		while (os.toByteArray().length / 1024 > maxSize) {
			// Clean up os
			os.reset();
			// interval 10
			options -= 10;
			image.compress(Bitmap.CompressFormat.JPEG, options, os);
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(os.toByteArray());
		Bitmap bitmap = BitmapFactory.decodeStream(isBm);
		return bitmap;
	}

	/**
	 * Bitmap转换成byte[]
	 * 
	 * @param bm
	 * @return
	 */
	public static byte[] bitmap2Bytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.JPEG, 80, baos);
		return baos.toByteArray();
	}
}
