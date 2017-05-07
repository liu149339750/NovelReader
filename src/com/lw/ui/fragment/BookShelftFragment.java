package com.lw.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import com.lw.bean.Novel;
import com.lw.bean.ShelftBook;
import com.lw.novel.common.DataManager;
import com.lw.novel.common.DataManager.OnDataChangeListener;
import com.lw.novelreader.BookItemAdpater;
import com.lw.presenter.BookShelftPresenter;
import com.lw.ttzw.NovelManager;
import com.lw.ui.activity.NovelReadActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

public class BookShelftFragment extends BaseListFreshFragment implements OnDataChangeListener,IBookShelftView{

	
	
	private BookItemAdpater mAdapter;
	private int mY;
	
	private BookShelftPresenter mPresenter = new BookShelftPresenter(this);
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}
	
	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		super.onDetach();
		System.out.println("onDetach");
//		saveData();
//		AppUtils.getAppContext().getContentResolver().unregisterContentObserver(mContentObserver);
		DataManager.instance().setDataListener(null);
	}


	private void saveData() {
//		Bundle b = getArguments();
//		b.putParcelableArrayList("data", (ArrayList)mAdapter.getNovels());
		Bundle b = getArguments();
		b.putInt("y", getListView().getFirstVisiblePosition());
		System.out.println(" getListView().getScrollY()="+ getListView().getScrollY());
	}
	
	private int getScrollY() {
		Bundle b = getArguments();
		System.out.println("b.getInt(="+b.getInt("y"));
		return b.getInt("y");
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Bundle bundle = getArguments();
//		if(bundle != null) {
//			List<ShelftBook> result = bundle.getParcelableArrayList("data");
//			result = DataManager.instance().queryShelftBookList(true);
//			if(result != null && result.size() > 0) {
//				mAdapter = new BookItemAdpater(result, LayoutInflater.from(getActivity()));
//				setListAdapter(mAdapter);
//				System.out.println("use bundle");
//				return;
//			}
//		}
		
		
		DataManager.instance().setDataListener(this);
		mAdapter = new BookItemAdpater(LayoutInflater.from(getActivity()));
		setListAdapter(mAdapter);
		
		loadMyShelft(true);
//		getListView().setOnScrollChangeListener(new OnScrollChangeListener() {
//			
//			@Override
//			public void onScrollChange(View v, int scrollX, int scrollY,
//					int oldScrollX, int oldScrollY) {
//				mY = scrollY;
//				System.out.println("onScrollChange = " + getListView().getScrollY());
//			}
//		});
//		getListView().setOnScrollListener(new OnScrollListener() {
//			
//			@Override
//			public void onScrollStateChanged(AbsListView view, int scrollState) {
//				// TODO Auto-generated method stub
//				System.out.println("scrollState="+scrollState);
//			}
//			
//			@Override
//			public void onScroll(AbsListView view, int firstVisibleItem,
//					int visibleItemCount, int totalItemCount) {
//				System.out.println(view.getScrollY());
//			}
//		});
//		AppUtils.getAppContext().getContentResolver().registerContentObserver(NovelProvider.BOOKSHELFT_URI, false,mContentObserver);
	}
	
	@Override
	public void onStop() {
		System.out.println(getListView().getFirstVisiblePosition());
		System.out.println("mm>"+getListView().getScrollY());
		saveData();
		super.onStop();
	}


	private void loadMyShelft(final boolean cache) {
		if(cache) {
			if(DataManager.instance().isShelftCached()) {
				mAdapter.changeData(DataManager.instance().queryShelftBookList(cache));
				return;
			}
		}
		new AsyncTask<Void, Void, List<ShelftBook>>() {

			@Override
			protected  List<ShelftBook> doInBackground(Void... params) {
				System.out.println("loadMyShelft cache = " + cache);
				return DataManager.instance().queryShelftBookList(cache);
			}
			
			@Override
			protected void onPostExecute(List<ShelftBook> result) {
				if(mAdapter != null) {
					mAdapter.changeData(result);
					System.out.println("---");
					getListView().setSelection(getScrollY());
					System.out.println("my="+mY);
//					getListView().setScrollY(60);
//					getListView().scrollBy(0, 80);
//					getListView().scrollTo(0, 80);
				}
				
			}
		}.execute();
	}
	
	
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Novel novel = (Novel) l.getItemAtPosition(position);
		NovelManager.getInstance().setCurrentNovel(novel);
		NovelReadActivity.startNovelReadActivity(getActivity(), -1);
	}
	
	@Override
	protected void pullRefreshData() {
		mPresenter.updateBookShelft();
	}

	@Override
	public void onBookShelftChange() {
		loadMyShelft(true);
	}

	@Override
	public void showLoading() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hideLoading() {
		System.out.println("hideLoading : " + Thread.currentThread().getName());
//		super.pullRefreshData();
		if(mSwipeRefresh != null) {
			mSwipeRefresh.post(new Runnable() {
				
				@Override
				public void run() {
					mSwipeRefresh.setRefreshing(false);					
				}
			});
		}
	}

	@Override
	public List<Novel> getNeedUpdateNovels() {
		// TODO Auto-generated method stub
		return new ArrayList<Novel>(mAdapter.getNovels());
	}
	
//	private ContentObserver mContentObserver = 	new ContentObserver(new Handler()) {
//		@Override
//		public void onChange(boolean selfChange) {
//			loadMyShelft(false);
////			saveData();
//		}
//	};
	
}
