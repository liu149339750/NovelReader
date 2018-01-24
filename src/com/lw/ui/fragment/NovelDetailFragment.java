package com.lw.ui.fragment;

import org.greenrobot.eventbus.EventBus;

import com.bumptech.glide.Glide;
import com.lw.bean.Novel;
import com.lw.db.DBUtil;
import com.lw.novel.utils.LogUtils;
import com.lw.novel.utils.Util;
import com.lw.novelreader.BookShelftManager;
import com.lw.novelreader.DownloadStatus;
import com.lw.novelreader.R;
import com.lw.presenter.NovelInfoPresenter;
import com.lw.ttzw.NovelManager;
import com.lw.ui.activity.NovelChapterListActivity;
import com.lw.ui.activity.NovelReadActivity;
import com.mingle.widget.LoadingView;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class NovelDetailFragment extends Fragment implements INovelInfoView,OnClickListener{

	private NovelInfoPresenter mPresenter;
	
	private TextView mNovelName;
	private TextView mNovelDetail;
	private TextView mAuthor;
	private ImageView mThumb;
	private TextView mUpdateTime;
	private TextView mChapter;
	
	private Button mStartRead;
	private Button mAddBookShelt;
	private Button mDownload;
	
	private View mViewChapters;
	
	private LoadingView mLoadView;
	private View mContentView;
	
	private int downloadStatus;
	
	private Activity mActivity;
	
	private final int RETRY_COUNT = 3;
	private int retry;
	
	private final String TAG = "NovelDetailFragment";
	
	private Dialog mDialog;
	 
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = activity;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.novel_detail, null);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		mNovelDetail = (TextView) view.findViewById(R.id.detail);
		mNovelName = (TextView) view.findViewById(R.id.name);
		mAuthor = (TextView) view.findViewById(R.id.author);
		mUpdateTime = (TextView) view.findViewById(R.id.update_time);
		mChapter = (TextView) view.findViewById(R.id.last_chapter);
		
		mThumb = (ImageView) view.findViewById(R.id.thumb);
		mStartRead = (Button) view.findViewById(R.id.read);
		mDownload = (Button) view.findViewById(R.id.download);
		mAddBookShelt = (Button) view.findViewById(R.id.addtoshelft);
		mStartRead.setOnClickListener(this);
		mDownload.setOnClickListener(this);
		mAddBookShelt.setOnClickListener(this);
		
		mViewChapters = view.findViewById(R.id.chapters);
		mViewChapters.setOnClickListener(this);
		
		mContentView = view.findViewById(R.id.content);
		mLoadView = (LoadingView) view.findViewById(R.id.loadView);
		mPresenter = new NovelInfoPresenter(this);
		mPresenter.getNovelInfo();
		
	}

	@Override
	public void showLoading() {
	    LogUtils.v(TAG, "showLoading");
		mContentView.setVisibility(View.GONE);
		mLoadView.setVisibility(View.VISIBLE);
	}

	@Override
	public void hideLoading() {
	    LogUtils.v(TAG, "hideLoading");
		mContentView.setVisibility(View.VISIBLE);
		mLoadView.setVisibility(View.GONE);
	}

	@Override
	public void startRead() {
//		EventBus.getDefault().post(new EventMessage(EventMessage.START_READ, 0));
		NovelReadActivity.startNovelReadActivity(mActivity, -1);
	}

	@Override
	public void removeBookShelt(int id) {
		System.out.println("removeBookShelt id = " + id);
		mAddBookShelt.setText(R.string.addtobookshelft);
	}

	@Override
	public void addBookShelft(int id) {		
		mAddBookShelt.setText(R.string.removebookshelft);
	}
	
	@Override
	public void showDownloadProgress(String progress) {
		// TODO Auto-generated method stub
		mDownload.setText(progress);
	}
	
	@Override
	public void showDownloadComplete() {
		downloadStatus = DownloadStatus.DOWNLOAD_IDLE;
		mDownload.setText(R.string.download_complete);
	}

	@Override
	public void showDownloadBook() {
		downloadStatus = DownloadStatus.DOWNLOAD_START;
		mDownload.setText(R.string.download_prepare);
	}

	@Override
	public void showDownloadPause() {
		downloadStatus = DownloadStatus.DOWNLOAD_PAUSE;
		mDownload.setText(R.string.download_continue);
	}


	@Override
	public void showChapters() {
//		EventBus.getDefault().post(0);
		NovelChapterListActivity.startChapterListActivity(getActivity());
	}

	@Override
	public void showNovelInfo(Novel novel) {
		System.out.println("showNovelInfo");
		if(novel == null) {
			return;
		}
		//visiblity will case a bug when use the cache data.
		if(isDetached() || !isAdded()) {
			System.out.println("invis");
			return;
		}
		mNovelName.setText(novel.getName());
		mNovelDetail.setText(novel.getBrief());
		mAuthor.setText(getString(R.string.author) + novel.getAuthor());
		mChapter.setText(novel.getLastUpdateChapter());
		mUpdateTime.setText(novel.getLastUpdateTime());
		Glide.with(this).load(novel.getThumb()).placeholder(R.drawable.default_cover).into(mThumb);
		
		if(isInbookShelf(novel.getId())) {
			mAddBookShelt.setText(R.string.removebookshelft);
		} else {
			mAddBookShelt.setText(R.string.addtobookshelft);
		}
	}

	@Override
	public int getDownloadState() {
		return downloadStatus;
	}

	@Override
	public boolean isInbookShelf(int bookid) {
		return NovelManager.getInstance().isInbookShelft(bookid);
	}

	@Override
	public void onClick(View v) {
		if(v == mViewChapters) {
			mPresenter.showChapterList();
		} else if(v == mStartRead) {
			mPresenter.readNow();
		} else if(v == mAddBookShelt) {
			mPresenter.addOrRemoveShelft();
		} else if(v == mDownload) {
			mPresenter.downloadBook();
		}
	}

	@Override
	public void onLoadFail() {
	    LogUtils.v(TAG, "onLoadFail retry = " + retry);
	    retry ++;
		if(retry < RETRY_COUNT) {
//			showLoading();
			mPresenter.getNovelInfo();
		} else {
			//show load fail pager
		    Novel novel = NovelManager.getInstance().getCurrentNovel();
		    showNovelInfo(novel);
		}
	}
	
	@Override
	public void onStart() {
		super.onStart();
		EventBus.getDefault().register(mPresenter);
	}

	
	@Override
	public void onStop() {
		super.onStop();
		EventBus.getDefault().unregister(mPresenter);
	}

	@Override
	public void showProgress() {
		LogUtils.v(TAG, "showProgress");
		if(mDialog == null) {
			mDialog = Util.CreateProgressDialog(getActivity());
			mDialog.setCanceledOnTouchOutside(false);
		}
		mDialog.show();
	}

	@Override
	public void hideProgress() {
		LogUtils.v(TAG, "hideProgress");
		if(mDialog != null) {
			mDialog.hide();
		}
	}

	
}
