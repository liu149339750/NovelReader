package com.lw.ui.activity;

import java.util.ArrayList;
import java.util.List;

import com.lw.adapter.NovelListAdpater;
import com.lw.bean.Novel;
import com.lw.bean.Novels;
import com.lw.novel.utils.LogUtils;
import com.lw.novel.utils.SettingUtil;
import com.lw.novelreader.R;
import com.lw.presenter.SearchPresenter;
import com.lw.ttzw.SourceSelector;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toast;

public class SearchActvity extends Activity implements OnClickListener,ISearchView,OnItemClickListener,OnQueryTextListener{

	private Button mBack;
	private SearchView mSearchEdit;
	private TextView mSearch;
	private ListView mListView;
	private View mSource;
	
	
	private SearchPresenter mSearchPresenter;
	
	private String mNextUrl;
	
	
	private List<Novel> mListData;
	private NovelListAdpater mAdapter;
	
	protected boolean isReloadMore;
	protected boolean isLoading;
	
	private int mReloadSpace;
	
	private final int RETRY_COUNT = 3;
	private int retry;
	private boolean firstQuery;
	
	private final String TAG = "SearchActvity";
	
	private ProgressDialog mProgressDialog;
	
	private List<String>mSources;
	
	private ArrayAdapter<String> mPopAdapter;
	private ListPopupWindow mListPopupWindow;
	private PopupMenu mPopupMenu;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_layout);
		initDialog();
		mReloadSpace = SettingUtil.getReloadSpace();
		mSearchEdit = (SearchView) findViewById(R.id.search_edit);
		mSearch = (TextView) findViewById(R.id.search);
		mListView = (ListView) findViewById(android.R.id.list);
		mSource = findViewById(R.id.source);
		mSource.setOnClickListener(this);
		findViewById(R.id.back).setOnClickListener(this);
		mSearchEdit.setOnClickListener(this);
	    int search_mag_icon_id = mSearchEdit.getContext().getResources().getIdentifier("android:id/search_mag_icon", null, null);
	    ImageView mSearchViewIcon = (ImageView) mSearchEdit.findViewById(search_mag_icon_id);// 获取搜索图标
	    mSearchViewIcon.setImageResource(0);
	    mSearchViewIcon.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
	    mSearchEdit.setOnQueryTextListener(this);
	    
		mSearch.setOnClickListener(this);
		
		mSearchPresenter = new SearchPresenter(this);
		mListData = new ArrayList<Novel>();
		mAdapter = new NovelListAdpater(this, new Novels(mListData));
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
		
		mListView.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
					if(isReloadMore && !isLoading) {
						isLoading = true;
						reloadMore();
					}
			}
			

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				if(isReloadMore)
					return;
				if(firstVisibleItem + visibleItemCount >= totalItemCount - mReloadSpace && totalItemCount > visibleItemCount) {
					isReloadMore = true;
				}
			}
		});
		
		mSearchPresenter.queryKeywords();
		
		mSources = new ArrayList<>();
		mSources = SourceSelector.getAllSourceString();
		((TextView)mSource).setText(SourceSelector.getDefaultSourceTag());
		mPopAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mSources);
		initSourceChangePop(mSource);
	}

	
	private void reloadMore() {
		if(!TextUtils.isEmpty(mNextUrl)) {
			System.out.println("load mNextUrl = " + mNextUrl);
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
			firstQuery = true;
			mListData.clear();
			mSearchPresenter.search(mSearchEdit.getQuery().toString());
			break;
		case R.id.source:
//			mListPopupWindow.show();
			mPopupMenu.show();
			break;
		default:
			break;
		}
	}


	private void initSourceChangePop(View v) {
		mListPopupWindow = new ListPopupWindow(this);
		
		mListPopupWindow.setAdapter(mPopAdapter);
		mListPopupWindow.setAnchorView(v);
		mListPopupWindow.setModal(true);
		mListPopupWindow.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				SourceSelector.setDefaultSource(mSources.get(position));
				((TextView)mSource).setText(SourceSelector.getDefaultSourceTag());
				mListPopupWindow.dismiss();
			}
		});
		
		mPopupMenu = new PopupMenu(this, mSource);
		Menu menu = mPopupMenu.getMenu();
		for(String tag : mSources) {
			menu.add(tag);
		}
		mPopupMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				SourceSelector.setDefaultSource(item.getTitle().toString());
				((TextView)mSource).setText(SourceSelector.getDefaultSourceTag());
				mListPopupWindow.dismiss();
				return true;
			}
		});
	}



	@Override
	public void showSearchResult(Novels sr) {
		LogUtils.v(TAG, "showSearchResult");
		if(sr == null || sr.getNovels().size() == 0) {
			onSearchFail();
			return;
		}
		firstQuery = false;
		mNextUrl = sr.getNextUrl();
		System.out.println(sr.getNovels().size() + "?>>"+sr.getNextUrl());
		mListData.addAll(sr.getNovels());
		mAdapter.notifyDataSetChanged();
		isReloadMore = false;
		isLoading = false;
	}



	private void onSearchFail() {
		LogUtils.v(TAG, "onSearchFail firstQuery=" + firstQuery);
		if(!firstQuery)
			return;
		if(retry < RETRY_COUNT) {
			retry ++;
			if(mSearchEdit != null) {
				mSearchPresenter.search(mSearchEdit.getQuery().toString());
			}
		} else {
			//show load fail pager
			Toast.makeText(this, "fail", Toast.LENGTH_SHORT).show();
		}
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Novel novel = (Novel) parent.getItemAtPosition(position);
		LogUtils.v(TAG, "onItemClick = " + novel);
		NovelDetailActivity.startDetailActivity(this, novel);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mSearchPresenter.cancel();
		mSearchPresenter = null;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		firstQuery = true;
		mListData.clear();
		mSearchPresenter.search(mSearchEdit.getQuery().toString());
		return true;
	}


	@Override
	public boolean onQueryTextChange(String newText) {
		// TODO Auto-generated method stub
		return false;
	}

	private void initDialog(){
		mProgressDialog= new ProgressDialog(this);
		mProgressDialog.setCanceledOnTouchOutside(false);
		mProgressDialog.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				mSearchPresenter.cancel();
			}
		});
		
	}

	@Override
	public void showProgressDialog() {
		// TODO Auto-generated method stub
		if(mProgressDialog == null) {
			initDialog();
		}
		mProgressDialog.show();
	}


	@Override
	public void hideProgressDialog() {
		if(mProgressDialog != null) {
			mProgressDialog.dismiss();
		}
	}


	@Override
	public void showSearchHistory(List<String> history) {
		
	}

}
