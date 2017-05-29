package com.lw.db;

import com.lw.db.SqliteHelper.BookShelft;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class NovelProvider extends ContentProvider{
	
	private SqliteHelper mSql;
	
	private UriMatcher mUriMatcher = new UriMatcher(0);
	
	public static final String AUTHORITY = "com.lw.reader";
	
	public static final String BOOKSHELFT = "bookshelft";
	public static final String BOOKSHELFT_BOOKID = "bookshelft/#";
	
	public static final String CHAPTER = "chapter";
	public static final String NOVEL = "book";
	public static final String SHELFT_INFO = "bookshelft/info";
	public static final String CHAPTER_URL = "chapter_url";
	public static final String SOURCE = "source";
	
	public static final int BOOKSHELFT_ID = 1;
	private static final int CHAPTER_ID = 2;
	private static final int NOVEL_ID = 3;
	private static final int SHELFT_INFO_ID = 4;
	private static final int CHAPTER_URL_ID = 5;
	private static final int SOURCE_ID = 6;
	
	private static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);
	public static final Uri CHAPTER_URI = Uri.parse("content://" + AUTHORITY + "/" + CHAPTER);
	public static final Uri NOVEL_URI = Uri.withAppendedPath(BASE_URI, NOVEL);
	public static final Uri BOOKSHELFT_URI = Uri.withAppendedPath(BASE_URI, BOOKSHELFT);
	public static final Uri BOOKSHELFT_VIEW_URI = Uri.withAppendedPath(BASE_URI, SHELFT_INFO);
	
	public static final Uri CHAPTER_URL_URI = Uri.withAppendedPath(BASE_URI, CHAPTER_URL);
	public static final Uri SOURCE_URI = Uri.withAppendedPath(BASE_URI, SOURCE);
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int match = mUriMatcher.match(uri);
		SQLiteDatabase sql = mSql.getWritableDatabase();
		int num = 0;
		switch (match) {
		case BOOKSHELFT_ID:
			num = sql.delete(SqliteHelper.BOOKSHELFT_TABLE, selection, selectionArgs);
			break;
		case CHAPTER_ID:
			num = sql.delete(SqliteHelper.CHAPTER_TABLE, selection, selectionArgs);
			break;
		case NOVEL_ID:
			num = sql.delete(SqliteHelper.BOOK_TABLE, selection, selectionArgs);
			break;
		default:
			break;
		}
		return num;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		int match = mUriMatcher.match(uri);
		SQLiteDatabase sql = mSql.getWritableDatabase();
		long id = 0;
		switch (match) {
		case BOOKSHELFT_ID:
			id = sql.insert(SqliteHelper.BOOKSHELFT_TABLE, null, values);
			break;
		case CHAPTER_ID:
			id = sql.insert(SqliteHelper.CHAPTER_TABLE, null, values);
			break;
		case NOVEL_ID:
			id = sql.insert(SqliteHelper.BOOK_TABLE, null, values);
			break;
		case SOURCE_ID:
			id = sql.insert(SqliteHelper.SOURCE_TABLE, null, values);
			break;
		default:
			break;
		}
		System.out.println("insert");
		getContext().getContentResolver().notifyChange(uri, null);
		return Uri.withAppendedPath(uri, ""+id);
	}
	
	@Override
	public int bulkInsert(Uri uri, ContentValues[] values) {
		System.out.println("bulkInsert > " + uri.toString());
		SQLiteDatabase db = mSql.getWritableDatabase();
		int match = mUriMatcher.match(uri);
		db.beginTransaction();
		long count = 0;
		try{
			switch (match) {
			case CHAPTER_ID:
				for(ContentValues cv : values) {
					long i = db.insert(SqliteHelper.CHAPTER_TABLE, null, cv);
					if(i > 0)
						count ++;
					else
						System.err.println("insert Fail!!");
				}
				db.setTransactionSuccessful();
				break;
			case CHAPTER_URL_ID:
				for(ContentValues cv : values) {
					long i = db.insert(SqliteHelper.CHAPTER_URL_TABLE, null, cv);
					if(i > 0)
						count ++;
					else
						System.err.println("insert Fail!!");
				}
				db.setTransactionSuccessful();
				break;
			default:
				break;
			}
		}finally {
			db.endTransaction();
		}
		System.out.println("bulkInsert count= " +count);
		getContext().getContentResolver().notifyChange(uri, null);
		return (int) count;
	}

	@Override
	public boolean onCreate() {
		mSql = new SqliteHelper(getContext());
		mUriMatcher.addURI(AUTHORITY, BOOKSHELFT, BOOKSHELFT_ID);
		mUriMatcher.addURI(AUTHORITY, BOOKSHELFT_BOOKID, BOOKSHELFT_ID);
		mUriMatcher.addURI(AUTHORITY, CHAPTER, CHAPTER_ID);
		mUriMatcher.addURI(AUTHORITY, NOVEL, NOVEL_ID);
		mUriMatcher.addURI(AUTHORITY, SHELFT_INFO, SHELFT_INFO_ID);
		mUriMatcher.addURI(AUTHORITY, CHAPTER_URL, CHAPTER_URL_ID);
		mUriMatcher.addURI(AUTHORITY, SOURCE, SOURCE_ID);
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		int code = mUriMatcher.match(uri);
		Cursor cursor = null;
		switch (code) {
		case 1:
			cursor = mSql.getReadableDatabase().query(SqliteHelper.BOOKSHELFT_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
			break;
		case CHAPTER_ID:
			cursor = mSql.getReadableDatabase().query(SqliteHelper.CHAPTER_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
			break;
		case NOVEL_ID:
			cursor = mSql.getReadableDatabase().query(SqliteHelper.BOOK_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
			break;
		case SHELFT_INFO_ID:
			cursor = mSql.getReadableDatabase().query(SqliteHelper.BOOKSHELFT_VIEW, projection, selection, selectionArgs, null, null, sortOrder);
			break;
		case SOURCE_ID:
			cursor = mSql.getReadableDatabase().query(SqliteHelper.SOURCE_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
			break;
		case CHAPTER_URL_ID:
			cursor = mSql.getReadableDatabase().query(SqliteHelper.CHAPTER_URL_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
		default:
			break;
		}
		return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		int code = mUriMatcher.match(uri);
		int c = 0;
		switch (code) {
		case BOOKSHELFT_ID:
			System.out.println("update count="+ values.getAsInteger(BookShelft.chapter_count));
			c = mSql.getWritableDatabase().update(SqliteHelper.BOOKSHELFT_TABLE, values, selection, selectionArgs);
			break;
		case CHAPTER_ID:
			c = mSql.getWritableDatabase().update(SqliteHelper.CHAPTER_TABLE, values, selection, selectionArgs);
			break;
		case NOVEL_ID:
			c = mSql.getWritableDatabase().update(SqliteHelper.BOOK_TABLE, values, selection, selectionArgs);
			break;
		case SHELFT_INFO_ID:
			c = mSql.getWritableDatabase().update(SqliteHelper.BOOKSHELFT_VIEW, values, selection, selectionArgs);
			break;
		case CHAPTER_URL_ID:
			c = mSql.getWritableDatabase().update(SqliteHelper.CHAPTER_URL_TABLE, values, selection, selectionArgs);
			break;
		default:
			break;
		}
		System.out.println("update uri =" +uri);
		getContext().getContentResolver().notifyChange(uri, null);
		return c;
	}
	

}
