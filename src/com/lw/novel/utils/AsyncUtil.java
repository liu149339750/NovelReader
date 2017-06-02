package com.lw.novel.utils;

import android.os.AsyncTask;

public class AsyncUtil {

	
	public static void run(final Runnable run) {
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				run.run();
				return null;
			}
		};
	}
}
