package com.lw.novelreader;

import com.justwayward.reader.view.readview.AppUtils;
import com.justwayward.reader.view.readview.SharedPreferencesUtil;
import com.lw.novel.common.FileUtil;
import com.lw.ttzw.NovelManager;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

public class MyApp extends Application{

	
	@Override
	public void onCreate() {
		super.onCreate();
		System.out.println("MyApp::onCreate");
		FileUtil.init();
		AppUtils.init(this);
		NovelManager.getInstance().init(this);
		SharedPreferencesUtil.init(this, getPackageName(), Context.MODE_PRIVATE);
		
		startService(new Intent(this, DownloadService.class));
	}
	

	
}
