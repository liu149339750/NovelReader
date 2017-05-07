package com.lw.novel.common;

import java.util.List;

import com.justwayward.reader.view.readview.AppUtils;
import com.lw.bean.ShelftBook;
import com.lw.db.DBUtil;
import com.lw.db.NovelProvider;

import android.database.ContentObserver;
import android.os.Handler;

public class DataManager {

	private static DataManager mDataManager;
	private OnDataChangeListener mChangeListener;
	
	private DataCache mShelftCache;
	
	public static DataManager instance() {
		if(mDataManager == null) 
			mDataManager = new DataManager();
		return mDataManager;
	}
	
	private ContentObserver mContentObserver = 	new ContentObserver(new Handler()) {
		@Override
		public void onChange(boolean selfChange) {
			System.out.println("DataManager onChange");
			queryShelftBookList(false);
			if(mChangeListener != null) {
				mChangeListener.onBookShelftChange();
			}
		}
	};
	
	public DataManager() {
		mShelftCache = DataCache.instance();
		AppUtils.getAppContext().getContentResolver().registerContentObserver(NovelProvider.BOOKSHELFT_URI, true,mContentObserver);
	}
	
	public void setDataListener(OnDataChangeListener listener) {
		mChangeListener = listener;
	}
	
	public boolean isShelftCached() {
		return mShelftCache.size() > 0;
	}
	
	public List<ShelftBook> queryShelftBookList(boolean useCache) {
		if(useCache) {
			List<ShelftBook> cacheData = mShelftCache.getCacheShelft();
			if(cacheData != null && cacheData.size() > 0)
				return cacheData;
		}
		List<ShelftBook> novels = DBUtil.queryBookShelft();
		SortUtil.sort(novels);
		DataCache.instance().setCacheShelft(novels);
		return novels;
	}
	
	public void removeCacheShelftBook(int id) {
		mShelftCache.removeBook(id);
	}
	
	public interface OnDataChangeListener {
		public void onBookShelftChange();
	}
}
