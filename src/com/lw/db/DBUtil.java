package com.lw.db;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.justwayward.reader.view.readview.AppUtils;
import com.justwayward.reader.view.readview.LogUtils;
import com.lw.bean.Chapter;
import com.lw.bean.Novel;
import com.lw.bean.NovelDetail;
import com.lw.bean.ShelftBook;
import com.lw.db.SqliteHelper.BookShelft;
import com.lw.db.SqliteHelper.Books;
import com.lw.novelreader.BookShelftManager;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

public class DBUtil {

	private static final String TAG = "DBUtil";
	
	//-----------insert db ---------------
	
	public static Uri saveChapterToDb(ContentResolver cr,int novelId,Chapter chapter) {
		ContentValues cv = new ContentValues();
		cv.put(com.lw.db.SqliteHelper.Chapter.book_id, novelId);
		cv.put(com.lw.db.SqliteHelper.Chapter.chapter_title, chapter.getTitle());
		cv.put(com.lw.db.SqliteHelper.Chapter.chapter_content_uri, chapter.getContentPath());
		return cr.insert(NovelProvider.CHAPTER_URI, cv);
	}
	
	public static int saveChaptersToDb(int novelId,List<Chapter> chapters) {
		ContentResolver cr = AppUtils.getAppContext().getContentResolver();
		ContentValues values[] = new ContentValues[chapters.size()];
		int size = chapters.size();
		for(int i=0;i<size;i++) {
			Chapter chapter = chapters.get(i);
			ContentValues cv = new ContentValues();
			cv.put(com.lw.db.SqliteHelper.Chapter.book_id, novelId);
			cv.put(com.lw.db.SqliteHelper.Chapter.chapter_title, chapter.getTitle());
			cv.put(com.lw.db.SqliteHelper.Chapter.chapter_url, chapter.getUrl());
			cv.put(com.lw.db.SqliteHelper.Chapter.chapter_content_uri, chapter.getContentPath());
			values[i] = cv;
		}
		return cr.bulkInsert(NovelProvider.CHAPTER_URI, values);
	}
	
	public static Uri saveBookInfo(Novel novel) {
		ContentResolver cr = AppUtils.getAppContext().getContentResolver();
		ContentValues cv = new ContentValues();
		cv.put(Books.author, novel.getAuthor());
		cv.put(Books.detail, novel.getBrief());
		cv.put(Books.kind, novel.getKind());
		cv.put(Books.name, novel.getName());
		cv.put(Books.thumb, novel.getThumb());
		cv.put(Books.url, novel.getUrl());
		cv.put(Books.lastUpdateChapter, novel.getLastUpdateChapter());
		cv.put(Books.lastUpdateTime, novel.getLastUpdateTime());
		return cr.insert(NovelProvider.NOVEL_URI, cv);
	}
	
	public static int setCurrentReadChapterPosition(int bookid,int p) {
		ContentResolver cr = AppUtils.getAppContext().getContentResolver();
		ContentValues cv = new ContentValues();
		cv.put(BookShelft.current_chapterposition, p);
		return cr.update(Uri.withAppendedPath(NovelProvider.BOOKSHELFT_URI, bookid+""), cv, "book_id = " + bookid, null);
	}
	
	public static boolean addToBookShelft(int bookid, int chapterSize) {
		ContentResolver cr = AppUtils.getAppContext().getContentResolver();
		ContentValues cv = new ContentValues();
		cv.put(BookShelft.BOOK_ID, bookid);
		cv.put(BookShelft.chapter_count, chapterSize);
		cv.put(BookShelft.readtime, getCurrentReadTimeString());
		Uri uri = cr.insert(NovelProvider.BOOKSHELFT_URI, cv);
		if(!"-1".equals(uri.getLastPathSegment())) {
			return true;
		}
		return false;
	}
	
	public static void addSearchKeyword(String keyword) {
		
	}
	
	//--------------delete from db --------------
	
	public static void rmFromBookShelft (int bookId) {
		ContentResolver cr = AppUtils.getAppContext().getContentResolver();
		cr.delete(NovelProvider.BOOKSHELFT_URI, BookShelft.BOOK_ID + " = " + bookId, null );
	}
	
	public static void deleteNovelByNameAndAuthor(String name,String author){
		ContentResolver cr = AppUtils.getAppContext().getContentResolver();
		cr.delete(NovelProvider.NOVEL_URI, Books.name + " = " + name + " and " + Books.author + " = " + author, null);
	}
	
	//-------------query db ------------
	
	public static List<Chapter> queryNovelChapterList(int bookId) {
		ContentResolver cr = AppUtils.getAppContext().getContentResolver();
		Cursor cursor = cr.query(NovelProvider.CHAPTER_URI, new String[]{com.lw.db.SqliteHelper.Chapter.chapter_title,com.lw.db.SqliteHelper.Chapter.chapter_url,com.lw.db.SqliteHelper.Chapter.chapter_content_uri}, "book_id = ?", new String[]{bookId + ""}, null);
		List<Chapter> chapters = new ArrayList<Chapter>();
		while(cursor.moveToNext()) {
			Chapter chapter = new Chapter();
			chapter.setTitle(cursor.getString(0));
			chapter.setUrl(cursor.getString(1));
			chapter.setContentPath(cursor.getString(2));
			chapters.add(chapter);
		}
		cursor.close();
		return chapters;
	}
	
	public static NovelDetail queryNovelDetail(Novel novel) {
		NovelDetail detail = new NovelDetail();
		ContentResolver cr = AppUtils.getAppContext().getContentResolver();
		System.out.println("111");
		Cursor cursor = cr.query(NovelProvider.NOVEL_URI, null, "author = ? and name = ?", new String[]{novel.author,novel.name}, null);
		System.out.println("2222");
		if(cursor.moveToNext()) {
			novel.id = cursor.getInt(cursor.getColumnIndex("ID"));
			novel.brief = cursor.getString(cursor.getColumnIndex(Books.detail));
			novel.kind = cursor.getString(cursor.getColumnIndex(Books.kind));
			novel.lastUpdateChapter = cursor.getString(cursor.getColumnIndex("lastUpdateChapter"));
			novel.lastUpdateTime = cursor.getString(cursor.getColumnIndex("lastUpdateTime"));
			detail.setNovel(novel);
			cursor.close();
			cursor = cr.query(NovelProvider.CHAPTER_URI, new String[]{com.lw.db.SqliteHelper.Chapter.chapter_title,com.lw.db.SqliteHelper.Chapter.chapter_url,com.lw.db.SqliteHelper.Chapter.chapter_content_uri}, "book_id = ?", new String[]{novel.id + ""}, null);
			System.out.println("chapter size = " + cursor.getCount());
			List<Chapter> chapters = new ArrayList<Chapter>();
			while(cursor.moveToNext()) {
				Chapter chapter = new Chapter();
				chapter.setTitle(cursor.getString(0));
				chapter.setUrl(cursor.getString(1));
				chapter.setContentPath(cursor.getString(2));
				chapters.add(chapter);
			}
			detail.setChapters(chapters);
			cursor.close();
			return detail;
		}
		return null;
	}
	
	public static List<ShelftBook> queryBookShelft() {
		List<ShelftBook> novels = new ArrayList<ShelftBook>();
		ContentResolver cr = AppUtils.getAppContext().getContentResolver();
		Cursor cursor = cr.query(NovelProvider.BOOKSHELFT_VIEW_URI, null, null, null, null);
		while(cursor.moveToNext()) {
			ShelftBook novel = new ShelftBook();
			novel.id = cursor.getInt(cursor.getColumnIndex("ID"));
			novel.name = cursor.getString(cursor.getColumnIndex(SqliteHelper.Books.name));
			novel.readtime = cursor.getString(cursor.getColumnIndex(SqliteHelper.BookShelft.readtime));
			novel.chapterCount = cursor.getInt(cursor.getColumnIndex(SqliteHelper.BookShelft.chapter_count));
			novel.thumb = cursor.getString(cursor.getColumnIndex(SqliteHelper.Books.thumb));
			novel.url = cursor.getString(cursor.getColumnIndex(SqliteHelper.Books.url));
			novel.author = cursor.getString(cursor.getColumnIndex(SqliteHelper.Books.author));
			novel.brief = cursor.getString(cursor.getColumnIndex(Books.detail));
			novel.kind = cursor.getString(cursor.getColumnIndex(Books.kind));
			novel.lastUpdateChapter = cursor.getString(cursor.getColumnIndex("lastUpdateChapter"));
			novel.lastUpdateTime = cursor.getString(cursor.getColumnIndex("lastUpdateTime"));
			novel.setCurrentChapterPosition(cursor.getInt(cursor.getColumnIndex(SqliteHelper.BookShelft.current_chapterposition)));
			novels.add(novel);
		}
		cursor.close();
		return novels;
	}
	
	public static Novel queryNovelById(int id) {
		ContentResolver cr = AppUtils.getAppContext().getContentResolver();
		Cursor cursor = cr.query(NovelProvider.NOVEL_URI, null, "ID = " + id, null, null);
		if(cursor.moveToNext()) {
			Novel novel = new Novel();
			novel.id = id;
			novel.name = cursor.getString(cursor.getColumnIndex(SqliteHelper.Books.name));
			novel.thumb = cursor.getString(cursor.getColumnIndex(SqliteHelper.Books.thumb));
			novel.url = cursor.getString(cursor.getColumnIndex(SqliteHelper.Books.url));
			novel.author = cursor.getString(cursor.getColumnIndex(SqliteHelper.Books.author));
			novel.brief = cursor.getString(cursor.getColumnIndex(Books.detail));
			novel.kind = cursor.getString(cursor.getColumnIndex(Books.kind));
			novel.lastUpdateChapter = cursor.getString(cursor.getColumnIndex("lastUpdateChapter"));
			novel.lastUpdateTime = cursor.getString(cursor.getColumnIndex("lastUpdateTime"));
			
			return novel;
		}
		return null;
	}
	
	public static Novel queryNovelByUrl(String url) {
		ContentResolver cr = AppUtils.getAppContext().getContentResolver();
		Cursor cursor = cr.query(NovelProvider.NOVEL_URI, null, Books.url + " = ?", new String[]{url}, null);
		if(cursor.moveToNext()) {
			Novel novel = new Novel();
			novel.id = cursor.getInt(cursor.getColumnIndex(SqliteHelper.ID));;
			novel.name = cursor.getString(cursor.getColumnIndex(SqliteHelper.Books.name));
			novel.thumb = cursor.getString(cursor.getColumnIndex(SqliteHelper.Books.thumb));
			novel.url = cursor.getString(cursor.getColumnIndex(SqliteHelper.Books.url));
			novel.author = cursor.getString(cursor.getColumnIndex(SqliteHelper.Books.author));
			novel.brief = cursor.getString(cursor.getColumnIndex(Books.detail));
			novel.kind = cursor.getString(cursor.getColumnIndex(Books.kind));
			novel.lastUpdateChapter = cursor.getString(cursor.getColumnIndex("lastUpdateChapter"));
			novel.lastUpdateTime = cursor.getString(cursor.getColumnIndex("lastUpdateTime"));
			return novel;
		}
		return null;
	}
	
	//------------update db -------------

	public static void updateReadtime(int bookid) {
		String date = getCurrentReadTimeString();
		ContentResolver cr = AppUtils.getAppContext().getContentResolver();
		ContentValues cv = new ContentValues();
		cv.put(BookShelft.readtime, date);
		cr.update(NovelProvider.BOOKSHELFT_URI, cv, BookShelft.BOOK_ID + " = " + bookid, null);
	}
	
	public static void updateNovelById(int id,Novel novel) {
		ContentResolver cr = AppUtils.getAppContext().getContentResolver();
		ContentValues cv = new ContentValues();
		cv.put(Books.author, novel.getAuthor());
		cv.put(Books.detail, novel.getBrief());
		cv.put(Books.kind, novel.getKind());
		cv.put(Books.name, novel.getName());
		cv.put(Books.thumb, novel.getThumb());
		cv.put(Books.url, novel.getUrl());
		cv.put(Books.lastUpdateChapter, novel.getLastUpdateChapter());
		cv.put(Books.lastUpdateTime, novel.getLastUpdateTime());
		cr.update(NovelProvider.NOVEL_URI, cv, "ID = " + id, null);
		if(BookShelftManager.instance().isInbookShelft(id)) {
			cr.notifyChange(NovelProvider.BOOKSHELFT_URI, null);
		}
	}
	
	public static void updateChapterCount(int id,int count) {
		LogUtils.v(TAG, "updateChapterCount = " + count);
		ContentResolver cr = AppUtils.getAppContext().getContentResolver();
		ContentValues cv = new ContentValues();
		cv.put(BookShelft.chapter_count,count);
		cv.put(BookShelft.readtime, getCurrentReadTimeString());
		cr.update(NovelProvider.BOOKSHELFT_URI, cv, BookShelft.BOOK_ID + " = " + id, null);
	}
	
	//----------------------------------------

	private static String getCurrentReadTimeString() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date = format.format(new java.util.Date());
		return date;
	}
}
