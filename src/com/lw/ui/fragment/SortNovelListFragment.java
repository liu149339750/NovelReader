package com.lw.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import org.htmlparser.util.ParserException;

import com.lw.bean.Novel;
import com.lw.bean.Novels;
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

public class SortNovelListFragment extends BaseListFreshFragment{

	private Activity mActivity;
	
	private NovelListAdpater mAdapter;
	
	private List<Novels> mData;
	
	private String mNextUrl;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	public static SortNovelListFragment newInstance(String title,String url) {
		SortNovelListFragment fragment = new SortNovelListFragment();
		Bundle bundle = new Bundle();
		bundle.putString("title", title);
		bundle.putString("url", url);
		fragment.setArguments(bundle);
		return fragment;
	}
	
	public static SortNovelListFragment newInstance(String title,String url,int itemLayout) {
		SortNovelListFragment fragment = new SortNovelListFragment();
		Bundle bundle = new Bundle();
		bundle.putString("title", title);
		bundle.putString("url", url);
		bundle.putInt("itemlayout", itemLayout);
		fragment.setArguments(bundle);
		return fragment;
	}
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		mActivity = activity;
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
		Bundle b =getArguments();
		b.putParcelableArrayList("data", (ArrayList)mData);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		Bundle bundle = getArguments();
		final int layout = bundle.getInt("itemlayout",-1);
		mData = new ArrayList<Novels>();
		if(layout == -1) {
			mAdapter = new NovelListAdpater(mActivity,mData);
		} else {
			mAdapter = new NovelListAdpater(mActivity,mData,layout);
		}
		setListAdapter(mAdapter);
		if(bundle != null) {
			List<Novels> result = bundle.getParcelableArrayList("data");
			if(result != null && result.size() > 0) {
				mData.addAll(result);
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
		
		String url = bundle.getString("url");
		loadLastUpdataData(url);
		
	}

	private void loadLastUpdataData(final String url) {
		new AsyncTask<Void, Void, Novels>() {

			@Override
			protected  Novels doInBackground(Void... params) {
				Novels data = null;
				int i = 0;
				while (i < 3) {
				try {
					data = TTZWManager.getMSortKindNovels(url);
					break;
				} catch (ParserException e) {
					e.printStackTrace();
					i ++;
				}
				}
				return data;
			}
			
			@Override
			protected void onPostExecute(Novels result) {
				if(isDetached())
					return;
				if(result == null) {
					
				}
				mSwipeRefresh.setRefreshing(false);
				mData.add(result);
				mAdapter.notifyDataSetChanged();
				mNextUrl = result.getNextUrl();
				isInEnd = false;
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
//		loadLastUpdataData();
		super.pullRefreshData();
	}
	
	@Override
	protected int getReloadSpace() {
		return 3;
	}
	
	@Override
	protected void reloadMore() {
		System.out.println("reloadMore");
		if(mNextUrl != null) {
			loadLastUpdataData(mNextUrl);
		}
	}

}
