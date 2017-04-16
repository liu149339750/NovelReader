package com.lw.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import org.htmlparser.util.ParserException;

import com.lw.bean.Novel;
import com.lw.novelreader.NovelListAdpater;
import com.lw.ttzw.NovelManager;
import com.lw.ttzw.TTZWManager;
import com.lw.ui.activity.NovelDetailActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

public class LastNovelListFragment extends BaseListFreshFragment{

	private Activity mActivity;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		mActivity = activity;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		System.out.println("onActivityCreated");
		
		Bundle bundle = getArguments();
		if(bundle != null) {
			List<Novel> result = bundle.getParcelableArrayList("data");
			if(result != null && result.size() > 0) {
				setListAdapter(new NovelListAdpater(mActivity,result));
				System.out.println("use bundle");
				return;
			}
		}
		mSwipeRefresh.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				mSwipeRefresh.setRefreshing(true);
			}
		});
		loadLastUpdataData();
		
	}

	private void loadLastUpdataData() {
		new AsyncTask<Void, Void, List<Novel>>() {

			@Override
			protected  List<Novel> doInBackground(Void... params) {
				List<Novel> data = new ArrayList<Novel>();
				try {
					data = TTZWManager.getLastUpdates(TTZWManager.BASE_URL);
				} catch (ParserException e) {
					e.printStackTrace();
				}
				return data;
			}
			
			@Override
			protected void onPostExecute(List<Novel> result) {
				if(isDetached())
					return;
				setListAdapter(new NovelListAdpater(mActivity,result));
				mSwipeRefresh.setRefreshing(false);
				
				Bundle b =getArguments();
				b.putParcelableArrayList("data", (ArrayList)result);
			}
		}.execute();
	}
	

	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		NovelManager.getInstance().setCurrentNovel((Novel)l.getItemAtPosition(position));
		Intent intent = new Intent();
		intent.setClass(mActivity, NovelDetailActivity.class);
		startActivity(intent);
	}
	
	@Override
	protected void pullRefreshData() {
		loadLastUpdataData();
	}
	
	

}