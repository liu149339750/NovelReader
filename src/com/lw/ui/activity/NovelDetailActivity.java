package com.lw.ui.activity;


import org.greenrobot.eventbus.EventBus;
import com.lw.bean.Novel;
import com.lw.novelreader.R;
import com.lw.ttzw.NovelManager;
import com.lw.ui.fragment.NovelDetailFragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;

public class NovelDetailActivity extends BaseFragmentActivity{

	
	@Override
	protected void onStart() {
		super.onStart();
//		EventBus.getDefault().register(this);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
//		EventBus.getDefault().unregister(this);
	}
	
	@Override
	protected int getLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.empty_activity_layout;
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
	
//	@Subscribe(threadMode=ThreadMode.MAIN)
//	public void onEventMainThread(EventMessage event) {
//		System.out.println("onEventMainThread event>"+event.arg);
//		switch (event.msgId) {
//		case EventMessage.START_READ:
//			Intent intent = new Intent();
//			Bundle bundle = new Bundle();
//			bundle.putInt("chapter", event.arg);
//			intent.putExtras(bundle);
//			intent.setClass(this, NovelReadActivity.class);
//			startActivity(intent);
//			break;
//		default:
//			break;
//		}
//
//	}
	
//	@Subscribe(threadMode=ThreadMode.MAIN)
//	public void onEventMainThread(Integer event) {
//		System.out.println(event);
//		switch (event) {
//		case 0:
//			getFragmentManager().beginTransaction().replace(R.id.container, new ChapterListFragment()).addToBackStack(null).commit();
//			break;
//
//		default:
//			break;
//		}
//	}

	@Override
	protected Fragment getFragment() {
		return new NovelDetailFragment();
	}
	
	
	public static void startDetailActivity(Context context,Novel novel) {
		NovelManager nm = NovelManager.getInstance();
		nm.setCurrentNovel(novel);
		Intent intent = new Intent();
		intent.setClass(context, NovelDetailActivity.class);
		context.startActivity(intent);
	}

}
