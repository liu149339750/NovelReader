package com.lw.db;

import java.io.File;

import com.lw.novel.common.FileUtil;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SqliteHelper extends SQLiteOpenHelper{
	
	private static final String DB_NAME = "novels";
	
	public static final String BOOK_TABLE = "BOOKS";
	public static final String CHAPTER_TABLE = "CHAPTERS";
	public static final String CHAPTER_URL_TABLE = "CHAPTER_URL";
	public static final String BOOKSHELFT_TABLE = "BOOKSHELFT";
	public static final String SOURCE_TABLE = "SOURCE";
	public static final String BOOKSHELFT_VIEW = "booshelft_info";
	
	public static final String ID = "ID";
	
	private static final String CREATE_HISTORY_TABLE = "CREATE TABLE IF NOT EXISTS HISTORY ("
			+ "ID INTEGER PRIMARY KEY,"
			+ "author varchar(50),"
			+ "name varchar(50),"
			+ "url varchar(50),"
			+ "kind varchar(50),"
			+ "thumb varchar(100),"
			+ "detail text)";
	
	private static final String CREATE_BOOKS_TABLE = "CREATE TABLE if not exists BOOKS ("
			+ "ID INTEGER PRIMARY KEY,"
			+ "author varchar(50),"
			+ "name varchar(50),"
			+ "url varchar(50),"
			+ "kind varchar(50),"
			+ "lastUpdateTime varchar(100),"
			+ "lastUpdateChapter varchar(100),"
			+ "thumb varchar(100),"
			+  Books.CHAPTER_URL + " varchar(100),"
			+ "detail text)";
	
	private static final String CREATE_CHAPTERS_TABLE = "CREATE TABLE if not exists CHAPTERS ("
			+ "ID INTEGER PRIMARY KEY,"
			+ "book_id INTEGER,"
			+ "chapter_title char(100),"
			+ "chapter_content_uri char(100),"
			+ "source varchar(20),"
			+ "chapter_url char(100))";
	
	private static final String CREATE_CHAPTER_URL_TABLE = "CREATE TABLE if not exists CHAPTER_URL ("
			+ "ID INTEGER PRIMARY KEY,"
			+ "book_id INTEGER,"
			+ "chapter_id INTEGER,"
			+ "source varchar(20),"
			+ "title varchar(50),"
			+ "URL CHAR(100),"
			+ "PREF INTEGER DEFAULT 0)";
	
	private static final String CREATE_MY_BOOKSHELFT = "CREATE TABLE if not exists BOOKSHELFT ("
			+ "ID INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ "book_id INTEGER,"
			+ "readtime varchar(50),"
			+ "chapter_count INTEGER default 0,"
			+ "source varchar(20),"
			+ "current_chapter_id INTEGER default 1,"
			+ "current_chapterposition INTEGER default 1)";
	
	private static final String CREATE_TRIGE_INSERT_BOOKSHELFT = "CREATE trigger if not exists auto_insert_book "
			+ "after insert on BOOKS "
			+ "begin "
			+ "insert into BOOKSHELFT (book_id,current_chapter_id,current_chapterposition) "
			+ "values (new.ID,0,0); "
			+ "end;";
	
	private static final String CREATE_TRIGE_CHAPTER_COUNT = "CREATE trigger if not exists insert_book_chapter_count "
			+ "after insert on CHAPTERS "
			+ "begin "
			+ "UPDATE BOOKSHELFT SET chapter_count =  (SELECT COUNT(*) from CHAPTERS where CHAPTERS.book_id = book_id) "
			+ "where book_id = new.book_id; "
			+ "end;";
	
	private static final String CREATE_TRIGE_DELETE_BOOK = "CREATE trigger if not exists delete_books  "
			+ "after delete on BOOKS "
			+ "begin "
			+ "delete from CHAPTERS where old.ID = book_id;"
			+ "delete from CHAPTER_URL where old.ID = book_id;"
			+ "delete from SOURCE where old.ID = book_id;"
			+ "end;";
	
	private static final String CREATE_TRIGE_DELETE_CHAPTER = "CREATE trigger if not exists delete_chapter  "
			+ "after delete on CHAPTERS "
			+ "begin "
			+ "delete from CHAPTER_URL where old.ID = chapter_id;"
			+ "end;";
	

	private static final String CREATE_BOOKSHELFT_VIEW = "CREATE view if not exists booshelft_info AS "
			+ "SELECT BOOKS.ID as ID,author,name,url,kind,lastUpdateTime,lastUpdateChapter,thumb,detail,readtime,chapter_count,"
			+ "current_chapterposition," + Books.CHAPTER_URL
			+ " FROM BOOKS,BOOKSHELFT WHERE BOOKS.ID = BOOKSHELFT.book_id";
	
	private static final String CREATE_SOURCE_TABLE = "create table if not exists " + SOURCE_TABLE + " ("
			+ ID + " INTEGER PRIMARY KEY,"
			+ "book_id INTEGER,"
			+ "source varchar(20),"
			+ "url varchar(100),"
			+ "chapter_url varchar(100))";
			
	
	public SqliteHelper(Context context) {
		super(context, FileUtil.getBaseDir() + "/" + DB_NAME, null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		createTable(db);
		createView(db);
		createTrgger(db);
	}

	private void createView(SQLiteDatabase db) {
		db.execSQL(CREATE_BOOKSHELFT_VIEW);
	}

	private void createTrgger(SQLiteDatabase db) {
//		db.execSQL(CREATE_TRIGE_INSERT_BOOKSHELFT);
		db.execSQL(CREATE_TRIGE_CHAPTER_COUNT);
		db.execSQL(CREATE_TRIGE_DELETE_BOOK);
	}

	private void createTable(SQLiteDatabase db) {
		db.execSQL(CREATE_BOOKS_TABLE);
		db.execSQL(CREATE_CHAPTERS_TABLE);
		db.execSQL(CREATE_CHAPTER_URL_TABLE);
		db.execSQL(CREATE_MY_BOOKSHELFT);
		db.execSQL(CREATE_SOURCE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	
	public static class Books {
		public static String author = "author";
		public static String name = "name";
		public static String url = "url";
		public static String kind = "kind";
		public static String thumb = "thumb";
		public static String detail = "detail";
		public static String lastUpdateTime = "lastUpdateTime";
		public static String lastUpdateChapter = "lastUpdateChapter";
		public static String CHAPTER_URL = "chapter_url";
	}
	
	public static class Chapter {
		public static String book_id = "book_id";
		public static String chapter_title = "chapter_title";
		public static String chapter_content_uri = "chapter_content_uri";
		public static String chapter_url = "chapter_url";
		public static String SOURCE = "source";
	}
	
	public static class BookShelft{
		public static String BOOK_ID = "book_id";
		public static String readtime = "readtime"; //update time
		public static String chapter_count = "chapter_count";
		public static String SOURCE = "source";
		public static String current_chapter_id = "current_chapter_id";
		public static String current_chapterposition = "current_chapterposition";
	}

	public static class ChapterURL {
		public static String CHAPTER_ID = "chapter_id";
		public static String URL = "URL";
		public static String SOURCE = "source";
		public static String BOOK_ID = "book_id";
		public static String TITLE = "title";
	}
	
	public static class Source {
		public static String BOOK_ID = "book_id";
		public static String URL = "url";
		public static String SOURCE = "source";
		public static String CHAPTER_URL = "chapter_url";
	}
	
}
