package com.lw.ui.activity;

import java.util.ArrayList;
import java.util.List;

import com.lw.bean.Novel;
import com.lw.bean.Novels;
import com.lw.novelreader.NovelListAdpater;
import com.lw.novelreader.R;
import com.lw.presenter.SearchPresenter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class SearchActvity extends Activity implements OnClickListener,ISearchView,OnItemClickListener{

	private Button mBack;
	private EditText mSearchEdit;
	private TextView mSearch;
	private ListView mListView;
	
	private SearchPresenter mSearchPresenter;
	
	private String mNextUrl;
	
	protected boolean isInEnd;
	
	private List<Novel> mListData;
	private NovelListAdpater mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_layout);
		
		mSearchEdit = (EditText) findViewById(R.id.search_edit);
		mSearch = (TextView) findViewById(R.id.search);
		mListView = (ListView) findViewById(android.R.id.list);
		findViewById(R.id.back).setOnClickListener(this);
		mSearchEdit.setOnClickListener(this);
		mSearch.setOnClickListener(this);
		
		mSearchPresenter = new SearchPresenter(this);
		mListData = new ArrayList<Novel>();
		mAdapter = new NovelListAdpater(this, new Novels(mListData));
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
		
		mListView.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if(isInEnd && scrollState == SCROLL_STATE_IDLE) {
					reloadMore();
				}
			}
			

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				if(isInEnd)
					return;
				if(firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount > visibleItemCount) {
					isInEnd = true;
				}
			}
		});
	}
	
	
	private void reloadMore() {
		if(!TextUtils.isEmpty(mNextUrl)) {
			mSearchPresenter.loadSearch(mNextUrl);
		}
	}
	
	public static void startSearchActivity(Context context) {
		Intent intent = new Intent();
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setClass(context, SearchActvity.class);
		context.startActivity(intent);
	}



	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.back:
			finish();
			break;
		case R.id.search_edit:
		
			break;
		case R.id.search:
			mSearchPresenter.search(mSearchEdit.getEditableText().toString());
			break;
		default:
			break;
		}
	}



	@Override
	public void showSearchResult(Novels sr) {
		mNextUrl = sr.getNextUrl();
		System.out.println(sr.getNovels().size() + "?>>"+sr.getNextUrl());
		mListData.addAll(sr.getNovels());
		mAdapter.notifyDataSetChanged();
	}



	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Novel novel = (Novel) parent.getItemAtPosition(position);
		NovelDetailActivity.startDetailActivity(this, novel);
	}

}
