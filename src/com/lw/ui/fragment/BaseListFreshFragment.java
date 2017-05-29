package com.lw.ui.fragment;

import com.lw.novelreader.R;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

public class BaseListFreshFragment extends ListFragment{

	protected SwipeRefreshLayout mSwipeRefresh;
	
	protected boolean isReloadMore;
	protected boolean isLoading;
	
	public BaseListFreshFragment() {
		setArguments(new Bundle());
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_list_pullload_view, null);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mSwipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.fresh);
		mSwipeRefresh.setSize(SwipeRefreshLayout.DEFAULT);
		mSwipeRefresh.setColorSchemeResources(android.R.color.holo_red_light,android.R.color.holo_green_light,android.R.color.holo_blue_light);
		mSwipeRefresh.setBackgroundColor(getResources().getColor(R.color.swipe_backgroud_color));
		mSwipeRefresh.setOnRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				pullRefreshData();
			}
		});
		
		getListView().setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
//				if(scrollState == SCROLL_STATE_IDLE) {
				if(isReloadMore && !isLoading) {
					isLoading = true;
					reloadMore();
				}
//				}
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//				Log.v("lw", "firstVisibleItem = " + firstVisibleItem + ",visibleItemCount = " + visibleItemCount + ",totalItemCount = " + totalItemCount);
				
				if(isReloadMore)
					return;
				if(firstVisibleItem + visibleItemCount >= totalItemCount - getReloadSpace() && totalItemCount > visibleItemCount) {
					isReloadMore = true;
				}
			}

		});
	}
	
	protected void reloadMore() {
		isReloadMore = false;
	}

	protected void pullRefreshData() {
		mSwipeRefresh.setRefreshing(false);
	}
	
	protected int getReloadSpace() {
		return 0;
	}
}
