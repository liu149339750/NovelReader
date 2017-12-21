package com.lw.db;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.lw.bean.BookSource;
import com.lw.bean.Chapter;
import com.lw.bean.ChapterUrl;
import com.lw.bean.Novel;
import com.lw.bean.NovelDetail;
import com.lw.bean.ShelftBook;
import com.lw.db.SqliteHelper.BookShelft;
import com.lw.db.SqliteHelper.Books;
import com.lw.db.SqliteHelper.ChapterURL;
import com.lw.db.SqliteHelper.Source;
import com.lw.novel.utils.AppUtils;
import com.lw.novel.utils.LogUtils;
import com.lw.novelreader.BookShelftManager;
import com.lw.ttzw.SourceSelector;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

public class DBUtil {

	private static final String TAG = "DBUtil";

	// -----------insert db ---------------

	public static Uri saveChapterToDb(ContentResolver cr, int novelId, Chapter chapter) {
		ContentValues cv = new ContentValues();
		cv.put(com.lw.db.SqliteHelper.Chapter.book_id, novelId);
		cv.put(com.lw.db.SqliteHelper.Chapter.chapter_title, chapter.getTitle());
		cv.put(com.lw.db.SqliteHelper.Chapter.chapter_content_uri, chapter.getContentPath());
		return cr.insert(NovelProvider.CHAPTER_URI, cv);
	}

	public static int saveChaptersToDb(int novelId, List<Chapter> chapters) {
		ContentResolver cr = AppUtils.getAppContext().getContentResolver();
		ContentValues values[] = new ContentValues[chapters.size()];
		int size = chapters.size();
		for (int i = 0; i < size; i++) {
			Chapter chapter = chapters.get(i);
			ContentValues cv = new ContentValues();
			cv.put(com.lw.db.SqliteHelper.Chapter.book_id, novelId);
			cv.put(com.lw.db.SqliteHelper.Chapter.chapter_title, chapter.getTitle());
			cv.put(com.lw.db.SqliteHelper.Chapter.chapter_url, chapter.getUrl());
			cv.put(com.lw.db.SqliteHelper.Chapter.chapter_content_uri, chapter.getContentPath());
			cv.put(com.lw.db.SqliteHelper.Chapter.SOURCE, chapter.getSource());
			values[i] = cv;
		}
		return cr.bulkInsert(NovelProvider.CHAPTER_URI, values);
	}

	public static int saveChapterUrlToDB(int bookId,List<Chapter> chapters, String source) {
		ContentResolver cr = AppUtils.getAppContext().getContentResolver();
		ContentValues values[] = new ContentValues[chapters.size()];
		int size = chapters.size();
		for (int i = 0; i < size; i++) {
			Chapter chapter = chapters.get(i);
			ContentValues cv = new ContentValues();
			cv.put(com.lw.db.SqliteHelper.ChapterURL.CHAPTER_ID, chapter.getId());
			cv.put(com.lw.db.SqliteHelper.ChapterURL.URL, chapter.getUrl());
			cv.put(com.lw.db.SqliteHelper.ChapterURL.SOURCE, source);
			cv.put(com.lw.db.SqliteHelper.ChapterURL.BOOK_ID, bookId);
			cv.put(com.lw.db.SqliteHelper.ChapterURL.TITLE, chapter.getTitle());
			values[i] = cv;
		}
		return cr.bulkInsert(NovelProvider.CHAPTER_URL_URI, values);
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
		cv.put(Books.CHAPTER_URL, novel.getChapterUrl());
		return cr.insert(NovelProvider.NOVEL_URI, cv);
	}

	public static Uri saveBookSource(int bookid, String source, String url, String chapterUrl,String chaptersFile) {
		ContentResolver cr = AppUtils.getAppContext().getContentResolver();
		ContentValues cv = new ContentValues();
		cv.put(Source.BOOK_ID, bookid);
		cv.put(Source.SOURCE, source);
		cv.put(Source.URL, url);
		cv.put(Source.CHAPTER_URL, chapterUrl);
		if(!TextUtils.isEmpty(chaptersFile))
		    cv.put(Source.CHAPTER_LIST, chaptersFile);
		return cr.insert(NovelProvider.SOURCE_URI, cv);
	}

	public static int setCurrentReadChapterPosition(int bookid, int p) {
		ContentResolver cr = AppUtils.getAppContext().getContentResolver();
		ContentValues cv = new ContentValues();
		cv.put(BookShelft.current_chapterposition, p);
		return cr.update(Uri.withAppendedPath(NovelProvider.BOOKSHELFT_URI, bookid + ""), cv, "book_id = " + bookid,
				null);
	}

	public static boolean addToBookShelft(int bookid, int chapterSize) {
		ContentResolver cr = AppUtils.getAppContext().getContentResolver();
		ContentValues cv = new ContentValues();
		cv.put(BookShelft.BOOK_ID, bookid);
		cv.put(BookShelft.chapter_count, chapterSize);
		cv.put(BookShelft.readtime, getCurrentReadTimeString());
		cv.put(BookShelft.SOURCE, SourceSelector.getDefaultSourceTag());
		Uri uri = cr.insert(NovelProvider.BOOKSHELFT_URI, cv);
		if (!"-1".equals(uri.getLastPathSegment())) {
			return true;
		}
		return false;
	}
	
	public static boolean addToSource(int bookid) {
        return false;
	}

	public static void addSearchKeyword(String keyword) {

	}

	// --------------delete from db --------------

	public static void rmFromBookShelft(int bookId) {
		ContentResolver cr = AppUtils.getAppContext().getContentResolver();
		cr.delete(NovelProvider.BOOKSHELFT_URI, BookShelft.BOOK_ID + " = " + bookId, null);
	}

	public static void deleteNovelByNameAndAuthor(String name, String author) {
		ContentResolver cr = AppUtils.getAppContext().getContentResolver();
		cr.delete(NovelProvider.NOVEL_URI, Books.name + " = " + name + " and " + Books.author + " = " + author, null);
	}

	// -------------query db ------------
	
	public static List<BookSource> queryBookSource(int bookId) {
		ContentResolver cr = AppUtils.getAppContext().getContentResolver();
		Cursor cursor = cr.query(NovelProvider.SOURCE_URI,
				new String[] {Source.CHAPTER_URL,Source.SOURCE,Source.URL},
				"book_id = ?", new String[] { bookId + "" }, null);
		List<BookSource> chapters = new ArrayList<BookSource>();
		while (cursor.moveToNext()) {
			BookSource source = new BookSource();
			source.setChapterUrl(cursor.getString(0));
			source.setSource(cursor.getString(1));
			source.setUrl(cursor.getString(2));
			source.setBookId(bookId);
			chapters.add(source);
		}
		cursor.close();
		return chapters;
	}
	
	public static BookSource queryBookSourceWithSource(int bookId,String tag) {
		ContentResolver cr = AppUtils.getAppContext().getContentResolver();
		Cursor cursor = cr.query(NovelProvider.SOURCE_URI,
				new String[] {Source.CHAPTER_URL,Source.SOURCE,Source.URL},
				"book_id = ? and " + Source.SOURCE + " = ?", new String[] { bookId + "",tag }, null);
		if (cursor.moveToNext()) {
			BookSource source = new BookSource();
			source.setChapterUrl(cursor.getString(0));
			source.setSource(cursor.getString(1));
			source.setUrl(cursor.getString(2));
			source.setBookId(bookId);
			cursor.close();
			return source;
		}
		cursor.close();
		return null;
	}

	public static List<Chapter> queryNovelChapterList(int bookId) {
		ContentResolver cr = AppUtils.getAppContext().getContentResolver();
		Cursor cursor = cr.query(NovelProvider.CHAPTER_URI,
				new String[] { com.lw.db.SqliteHelper.Chapter.chapter_title, com.lw.db.SqliteHelper.Chapter.chapter_url,
						com.lw.db.SqliteHelper.Chapter.chapter_content_uri, SqliteHelper.ID,com.lw.db.SqliteHelper.Chapter.SOURCE },
				"book_id = ?", new String[] { bookId + "" }, null);
		List<Chapter> chapters = new ArrayList<Chapter>();
		while (cursor.moveToNext()) {
			Chapter chapter = new Chapter();
			chapter.setTitle(cursor.getString(0));
			chapter.setUrl(cursor.getString(1));
			chapter.setContentPath(cursor.getString(2));
			chapter.setId(cursor.getInt(3));
			chapter.setSource(cursor.getString(4));
			chapter.setBookId(bookId);
			chapters.add(chapter);
		}
		cursor.close();
		return chapters;
	}

	public static List<ChapterUrl> queryNovelChapterURL(int bookId) {
		ContentResolver cr = AppUtils.getAppContext().getContentResolver();
		Cursor cursor = cr.query(NovelProvider.CHAPTER_URL_URI,
				new String[] { ChapterURL.CHAPTER_ID, ChapterURL.SOURCE, ChapterURL.URL,SqliteHelper.ID }, ChapterURL.BOOK_ID + " = ?",
				new String[] { bookId + "" }, null);
		List<ChapterUrl> chapters = new ArrayList<ChapterUrl>();
		while (cursor.moveToNext()) {
			ChapterUrl chapter = new ChapterUrl();
			chapter.setBookId(bookId);
			chapter.setChapterId(cursor.getInt(0));
			chapter.setSource(cursor.getString(1));
			chapter.setUrl(cursor.getString(2));
			chapter.setId(cursor.getInt(3));
			chapters.add(chapter);
		}
		cursor.close();
		return chapters;
	}
	
	public static List<ChapterUrl> queryNovelChapterURLBySource(int bookId,String source) {
		ContentResolver cr = AppUtils.getAppContext().getContentResolver();
		Cursor cursor = cr.query(NovelProvider.CHAPTER_URL_URI,
				new String[] { ChapterURL.CHAPTER_ID, ChapterURL.SOURCE, ChapterURL.URL,SqliteHelper.ID }, ChapterURL.BOOK_ID + " = ? and " + ChapterURL.SOURCE + " = ?" ,
				new String[] { bookId + "",source }, null);
		List<ChapterUrl> chapters = new ArrayList<ChapterUrl>();
		while (cursor.moveToNext()) {
			ChapterUrl chapter = new ChapterUrl();
			chapter.setBookId(bookId);
			chapter.setChapterId(cursor.getInt(0));
			chapter.setSource(cursor.getString(1));
			chapter.setUrl(cursor.getString(2));
			chapter.setId(cursor.getInt(3));
			chapters.add(chapter);
		}
		cursor.close();
		return chapters;
	}
	
	public static List<ChapterUrl> queryNovelChapterURLByChapterID(int chapterId) {
		ContentResolver cr = AppUtils.getAppContext().getContentResolver();
		Cursor cursor = cr.query(NovelProvider.CHAPTER_URL_URI,
				new String[] { ChapterURL.BOOK_ID, ChapterURL.SOURCE, ChapterURL.URL,SqliteHelper.ID,ChapterURL.TITLE }, ChapterURL.CHAPTER_ID + " = ?",
				new String[] { chapterId + "" }, null);
		List<ChapterUrl> chapters = new ArrayList<ChapterUrl>();
		while (cursor.moveToNext()) {
			ChapterUrl chapter = new ChapterUrl();
			chapter.setChapterId(chapterId);
			chapter.setBookId(cursor.getInt(0));
			chapter.setSource(cursor.getString(1));
			chapter.setUrl(cursor.getString(2));
			chapter.setId(cursor.getInt(3));
			chapter.setTitle(cursor.getString(4));
			chapters.add(chapter);
		}
		cursor.close();
		return chapters;
	}

	public static NovelDetail queryNovelDetail(Novel novel) {
		NovelDetail detail = new NovelDetail();
		ContentResolver cr = AppUtils.getAppContext().getContentResolver();
		System.out.println("111");
		Cursor cursor = cr.query(NovelProvider.NOVEL_URI, null, "author = ? and name = ?",
				new String[] { novel.author, novel.name }, null);
		System.out.println("2222");
		if (cursor.moveToNext()) {
			novel.id = cursor.getInt(cursor.getColumnIndex("ID"));
			novel.brief = cursor.getString(cursor.getColumnIndex(Books.detail));
			novel.kind = cursor.getString(cursor.getColumnIndex(Books.kind));
			novel.lastUpdateChapter = cursor.getString(cursor.getColumnIndex("lastUpdateChapter"));
			novel.lastUpdateTime = cursor.getString(cursor.getColumnIndex("lastUpdateTime"));
			detail.setNovel(novel);
			cursor.close();
			cursor = cr.query(NovelProvider.CHAPTER_URI, new String[] { com.lw.db.SqliteHelper.Chapter.chapter_title,
					com.lw.db.SqliteHelper.Chapter.chapter_url, com.lw.db.SqliteHelper.Chapter.chapter_content_uri },
					"book_id = ?", new String[] { novel.id + "" }, null);
			System.out.println("chapter size = " + cursor.getCount());
			List<Chapter> chapters = new ArrayList<Chapter>();
			while (cursor.moveToNext()) {
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
		while (cursor.moveToNext()) {
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
			novel.ChapterUrl = cursor.getString(cursor.getColumnIndex("chapter_url"));
			novel.setCurrentChapterPosition(
					cursor.getInt(cursor.getColumnIndex(SqliteHelper.BookShelft.current_chapterposition)));
			novels.add(novel);
		}
		cursor.close();
		return novels;
	}

	public static Novel queryNovelById(int id) {
		ContentResolver cr = AppUtils.getAppContext().getContentResolver();
		Cursor cursor = cr.query(NovelProvider.NOVEL_URI, null, "ID = " + id, null, null);
		if (cursor.moveToNext()) {
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
		Cursor cursor = cr.query(NovelProvider.NOVEL_URI, null, Books.url + " = ?", new String[] { url }, null);
		if (cursor.moveToNext()) {
			Novel novel = new Novel();
			novel.id = cursor.getInt(cursor.getColumnIndex(SqliteHelper.ID));
			;
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

	// ------------update db -------------

	public static void updateReadtime(int bookid) {
		String date = getCurrentReadTimeString();
		ContentResolver cr = AppUtils.getAppContext().getContentResolver();
		ContentValues cv = new ContentValues();
		cv.put(BookShelft.readtime, date);
		cr.update(NovelProvider.BOOKSHELFT_URI, cv, BookShelft.BOOK_ID + " = " + bookid, null);
	}

	public static void updateNovelById(int id, Novel novel) {
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
		if (BookShelftManager.instance().isInbookShelft(id)) {
			cr.notifyChange(NovelProvider.BOOKSHELFT_URI, null);
		}
	}

	public static void updateChapterCount(int id, int count) {
		LogUtils.v(TAG, "updateChapterCount = " + count);
		ContentResolver cr = AppUtils.getAppContext().getContentResolver();
		ContentValues cv = new ContentValues();
		cv.put(BookShelft.chapter_count, count);
		// cv.put(BookShelft.readtime, getCurrentReadTimeString());
		cr.update(NovelProvider.BOOKSHELFT_URI, cv, BookShelft.BOOK_ID + " = " + id, null);
	}
	
	public static void updateChapterInfo(Chapter chapter) {
		ContentResolver cr = AppUtils.getAppContext().getContentResolver();
		ContentValues cv = new ContentValues();
		cv.put(com.lw.db.SqliteHelper.Chapter.chapter_title, chapter.getTitle());
		cv.put(com.lw.db.SqliteHelper.Chapter.chapter_url, chapter.getUrl());
		cv.put(com.lw.db.SqliteHelper.Chapter.chapter_content_uri, chapter.getContentPath());
		cv.put(com.lw.db.SqliteHelper.Chapter.SOURCE, chapter.getSource());
		cr.update(NovelProvider.CHAPTER_URI, cv, SqliteHelper.ID + " = " + chapter.getId(), null);
	}
	
	public static void updateChapterUrlInfo(int id,String source,String title,String url) {
		ContentResolver cr = AppUtils.getAppContext().getContentResolver();
		ContentValues cv = new ContentValues();
		cv.put(ChapterURL.SOURCE, source);
		cv.put(ChapterURL.TITLE, title);
		cv.put(ChapterURL.URL, url);
		cr.update(NovelProvider.CHAPTER_URL_URI, cv, SqliteHelper.ID + " = " + id, null);
	}
	
    public static void updateChaptersFileUrl(int bookid, String tag, String path) {
        ContentResolver cr = AppUtils.getAppContext().getContentResolver();
        ContentValues cv = new ContentValues();
        cv.put(Source.CHAPTER_LIST, path);
        cr.update(NovelProvider.SOURCE_URI, cv, SqliteHelper.Source.BOOK_ID + " = " + bookid
                + " and source = '" + tag + "'", null);
    }

	// ----------------------------------------

	private static String getCurrentReadTimeString() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date = format.format(new java.util.Date());
		return date;
	}
}
