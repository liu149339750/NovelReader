package com.lw.ui.activity;

import java.util.List;

import com.lw.bean.Chapter;
import com.lw.bean.ChapterUrl;
import com.lw.db.DBUtil;
import com.lw.novel.utils.LogUtils;
import com.lw.novelreader.R;
import com.lw.ttzw.NovelManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;

public class SourceActivity extends AppCompatActivity implements OnItemClickListener{

	
	@Bind(R.id.source)
	TextView mSourceTextView;
	@Bind(R.id.title)
	TextView mTitleTextView;
	@Bind(android.R.id.list)
	ListView mListView;
	
	private static final String TAG = "SourceActivity";
	private Chapter mChapter;
	private List<ChapterUrl> mChapterUrls;
	private ChapterUrl mChapterUrl;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.source_activity_layout);
		ButterKnife.bind(this);
		
		mChapter = NovelManager.getInstance().getChapter();
		
		String tag = mChapter.getSource();
		LogUtils.v(TAG, "tag = " + tag + ",ID = " + mChapter.getId());
		mChapterUrls = DBUtil.queryNovelChapterURLByChapterID(mChapter.getId());
		for(int i=0;i<mChapterUrls.size();i++) {
			String source = mChapterUrls.get(i).getSource();
			if(tag.equals(source)) {
				mChapterUrl = mChapterUrls.remove(i);
				break;
			}
		}
		if(mChapterUrl != null) {
			LogUtils.v(TAG, "mChapterUrl ID = " + mChapterUrl.getId());
			mSourceTextView.setText(mChapterUrl.getSource());
			mTitleTextView.setText(mChapterUrl.getTitle());
		} else {
			mSourceTextView.setText(mChapter.getSource());
			mTitleTextView.setText(mChapter.getTitle());
		}
		mListView.setOnItemClickListener(this);
		setListAdapter(new SourceAdapter(this, mChapterUrls));
		
		setUpToolbar();
	}
	
	private void setUpToolbar() {
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setNavigationIcon(R.drawable.kd_back_white);
		toolbar.setTitle("");
		setSupportActionBar(toolbar);
		
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if(id == android.R.id.home) {
			finish();
		}
		return super.onOptionsItemSelected(item);
	}
	private void setListAdapter(SourceAdapter sourceAdapter) {
		mListView.setAdapter(sourceAdapter);
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		ButterKnife.unbind(this);
	}
	
	
	public static void startChangeSourceActivity(Context context) {
		Intent intent = new Intent();
		intent.setClass(context, SourceActivity.class);
		if(context instanceof Activity) {
			Activity activity = (Activity) context;
			activity.startActivityForResult(intent, 100);
		} else {
			context.startActivity(intent);
		}
		
	}
	
	public static class SourceAdapter extends BaseAdapter {

		private LayoutInflater mInflater;
		private List<ChapterUrl> mChapterUrls;
	   public SourceAdapter(Context context,List<ChapterUrl> ch) {
		   mInflater = LayoutInflater.from(context);
		   mChapterUrls = ch;
		}
		@Override
		public int getCount() {
			return mChapterUrls.size();
		}

		@Override
		public ChapterUrl getItem(int position) {
			return mChapterUrls.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if(convertView == null) {
				convertView = mInflater.inflate(R.layout.source_select_item, null);
				holder = new ViewHolder(convertView);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			ChapterUrl cu = getItem(position);
			holder.source.setText(cu.getSource());
			holder.title.setText(cu.getTitle());
			return convertView;
		}
		
		class ViewHolder {
			@Bind(R.id.source)
			TextView source;
			@Bind(R.id.title)
			TextView title;
			
			public ViewHolder(View v) {
				ButterKnife.bind(this,v);
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		ChapterUrl cu = (ChapterUrl) parent.getItemAtPosition(position);
		setResultOk(cu);
	}

	private void setResultOk(ChapterUrl cu) {
		updateChapterDb(cu);
		setResult(RESULT_OK);
		finish();
	}

	private void updateChapterDb(ChapterUrl cu) {
		DBUtil.updateChapterUrlInfo(cu.getId(), mChapter.getSource(), mChapter.getTitle(), mChapter.getUrl());
		mChapter.setTitle(cu.getTitle());
		mChapter.setUrl(cu.getUrl());
		mChapter.setSource(cu.getSource());
		DBUtil.updateChapterInfo(mChapter);
	}
}
