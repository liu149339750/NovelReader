package com.lw.ui.activity;

import com.lw.novelreader.R;
import com.lw.ui.fragment.ChapterListFragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;

public class NovelChapterListActivity extends BaseFragmentActivity{

	
	@Override
	protected int getLayoutId() {
		return R.layout.empty_activity_layout;
	}
	
	@Override
	protected Fragment getFragment() {
		return new ChapterListFragment();
	}
	
	public static void startChapterListActivity(Context context) {
		System.out.println("startChapterListActivity");
		Intent intent =  new Intent();
		intent.setClass(context, NovelChapterListActivity.class);
		context.startActivity(intent);
	}

}
