package com.twocity.bookworm.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncImageLoader {
    final static String TAG = "AsyncImageLoader";
	Bitmap bitmap;
	private ExecutorService executorService = Executors.newFixedThreadPool(20);
	private final Handler mHandler = new Handler();

	public void loadDrawable(final String imageUrl,
			final ImageCallback callback) {
		executorService.execute(new Runnable() {
			public void run() {
				try {
					bitmap = loadImageFromUrl(imageUrl);
					Log.d(TAG,"bitmap is null" + bitmap==null?"yes":"no");
					mHandler.post(new Runnable() {
						public void run() {
							callback.imageLoaded(bitmap, imageUrl);
						}
					});
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		});
	}

	public void closeExecutorService() {
		executorService.shutdownNow();
		executorService = null;
		executorService = Executors.newFixedThreadPool(20);
	}

	protected Bitmap loadImageFromUrl(String imageUrl) {
		try {
		    Log.d(TAG,"Book image: "+imageUrl);
			URL url = new URL(imageUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(1000);
			
			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				InputStream is = conn.getInputStream();
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 2;
				Bitmap bm = BitmapFactory.decodeStream(is, null, options);
				if (is != null) {
					is.close();
					is = null;
				}

				return bm;
			}

			return null;
		} catch (Exception e) {
			return null;
		}
	}

	public interface ImageCallback {
		public void imageLoaded(Bitmap imageBitmap, String imageUrl);
	}
}

