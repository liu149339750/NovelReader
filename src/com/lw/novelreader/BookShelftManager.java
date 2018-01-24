package com.lw.novelreader;

import java.util.ArrayList;
import java.util.List;

import com.lw.bean.Chapter;
import com.lw.bean.Novel;
import com.lw.bean.ShelftBook;
import com.lw.db.DBUtil;
import com.lw.db.NovelProvider;
import com.lw.db.SqliteHelper.BookShelft;
import com.lw.novel.utils.DataManager;
import com.lw.novel.utils.FileUtil;
import com.lw.novel.utils.LogUtils;
import com.lw.ttzw.NovelManager;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

public class BookShelftManager {

	private static BookShelftManager manager;
	private static final String TAG = "BookShelftManager";
	
	private List<ShelftBook> mShelftBooks;
	
	public static BookShelftManager instance() {
		if(manager == null)
			manager = new BookShelftManager();
		return manager;
	}
	
	public void loadBookShelft(Context context) {
		mShelftBooks = new ArrayList<ShelftBook>();
		Cursor cursor = context.getContentResolver().query(Uri.parse("content://"+NovelProvider.AUTHORITY+"/"+NovelProvider.BOOKSHELFT), null, null, null, null);
		if(cursor != null) {
			while(cursor.moveToNext()) {
				ShelftBook book = new ShelftBook();
				book.setBookId(cursor.getInt(cursor.getColumnIndex(BookShelft.BOOK_ID)));
				book.setCurrentChapterId(cursor.getInt(cursor.getColumnIndex(BookShelft.current_chapter_id)));
				book.setCurrentChapterPosition(cursor.getInt(cursor.getColumnIndex(BookShelft.current_chapterposition)));
				mShelftBooks.add(book);
			}
		}
		cursor.close();
	}
	
	public boolean isInbookShelft(int bookid) {
		for(ShelftBook book : mShelftBooks) {
			if(book.getBookId() == bookid) {
				return true;
			}
		}
		return false;
	}
	
	public void addBookToShelft(int book) {
		mShelftBooks.add(new ShelftBook(book));
		DBUtil.addToBookShelft(book,NovelManager.getInstance().getChapterSize());
		List<Chapter> chapters = DBUtil
				.queryNovelChapterList(book);
		List<Chapter> newChapters = NovelManager.getInstance().getChapers();

		if (chapters.size() < newChapters.size()) {
			List<Chapter> newInsert = new ArrayList<Chapter>();
			for (int i = chapters.size(); i < newChapters
					.size(); i++) {
				newInsert.add(newChapters.get(i));
			}
			int c = DBUtil.saveChaptersToDb(book, newInsert);
			if (c > 0) {
				DBUtil.updateChapterCount(book,
						newChapters.size());
			}
		}
	}
	
	public void rmBookFromShelft(int bookid) {
		LogUtils.v(TAG, "rmBookFromShelft bookid = " + bookid);
		for(ShelftBook book: mShelftBooks) {
			if(book.getBookId() == bookid) {
				mShelftBooks.remove(book);
				break;
			}
		}
		DBUtil.rmFromBookShelft(bookid);
		DBUtil.deleteNovelChapters(bookid);
		DataManager.instance().removeCacheShelftBook(bookid);
		new AsyncTask<Integer, Void, Void>() {

			@Override
			protected Void doInBackground(Integer... params) {
				Novel novel = DBUtil.queryNovelById(params[0]);
				FileUtil.deleteNovel(novel);
				return null;
			}
		}.execute(bookid);
	}
}
