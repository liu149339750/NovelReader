package com.lw.ui.activity;

import com.lw.bean.Novel;
import com.lw.novelreader.NovelListAdpater;
import com.lw.novelreader.R;
import com.lw.presenter.SearchPresenter;
import com.lw.ttzw.NovelManager;
import com.lw.ttzw.SearchResult;
import com.lw.ui.fragment.NovelDetailFragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
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
		
		mListView.setOnItemClickListener(this);
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
	public void showSearchResult(SearchResult sr) {
		System.out.println("?>>"+sr.getNextUrl());
		mListView.setAdapter(new NovelListAdpater(this, sr.getNovels()));
	}



	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Novel novel = (Novel) parent.getItemAtPosition(position);
		NovelDetailActivity.startDetailActivity(this, novel);
	}

}
