package com.lw.datainterfaceimpl;

import java.util.ArrayList;
import java.util.List;

import org.htmlparser.util.ParserException;

import com.justwayward.reader.bean.BookDetail;
import com.justwayward.reader.bean.BookMixAToc;
import com.justwayward.reader.bean.BookMixAToc.mixToc.Chapters;
import com.justwayward.reader.bean.BooksByCats;
import com.justwayward.reader.bean.CategoryList;
import com.justwayward.reader.bean.ChapterRead;
import com.justwayward.reader.bean.SearchDetail;
import com.justwayward.reader.bean.SearchDetail.SearchBooks;
import com.lw.bean.Chapter;
import com.lw.bean.Novel;
import com.lw.bean.NovelDetail;
import com.lw.bean.Novels;
import com.lw.novel.utils.BoolSet;
import com.lw.novel.utils.LogUtils;
import com.lw.ttzw.DataInterface;
import com.zhuishu.api.BookApi;
import com.zhuishu.api.BookApiModule;
import com.zhuishu.api.Constant;

import android.text.TextUtils;
import android.util.Pair;
import android.webkit.URLUtil;
import rx.Observer;
import rx.schedulers.Schedulers;

public class ZhuishuImpl implements DataInterface{
	
	BookApi mApi = BookApi.getInstance(BookApiModule.provideOkHttpClient());
	private final int LIMIT = 30;
	private static final String TAG = "zhuishu";

	@Override
	public Novels search(String keyword) throws ParserException {
		final BoolSet bool = new BoolSet(false);
		final Novels novels = new Novels();
		final List<Novel> ld = new ArrayList<Novel>();
		novels.setNovels(ld);
		mApi.getSearchResult(keyword).subscribe(new Observer<SearchDetail>() {

			@Override
			public void onCompleted() {
			}

			@Override
			public void onError(Throwable arg0) {
				bool.setValue(true);
			}

			@Override
			public void onNext(SearchDetail arg0) {
				for(SearchBooks book : arg0.books) {
					Novel novel = new Novel();
					novel.setName(book.title);
					novel.setAuthor(book.author);
					novel.setBrief(book.shortIntro);
					novel.setKind(book.cat);
					novel.setThumb(filterCover(book.cover));
					novel.setLastUpdateChapter(book.lastChapter);
					novel.setUrl(book._id);
					ld.add(novel);
				}
				bool.setValue(true);
			}
		});
		return novels;
	}

	@Override
	public Novels loadSearchNovel(String source) throws ParserException {
		return null;
	}

	@Override
	public String getChapterContent(String url) throws ParserException {
		final BoolSet bool = new BoolSet(false);
		final StringBuilder sb = new StringBuilder();
		mApi.getChapterRead(url).subscribe(new Observer<ChapterRead>() {

			@Override
			public void onCompleted() {
			}

			@Override
			public void onError(Throwable arg0) {
				bool.setValue(true);
				arg0.printStackTrace();
			}

			@Override
			public void onNext(ChapterRead arg0) {
				LogUtils.v(TAG, "getChapterContent :: arg0.ok = " + arg0.ok);
				sb.append(arg0.chapter.body);
				bool.setValue(true);
			}
		});
		bool.lockThread();
		return sb.toString();
	}

	@Override
	public List<Chapter> getNovelChapers(String bookId) throws ParserException {
		final BoolSet bool = new BoolSet(false);
		final List<Chapter> chapters = new ArrayList<Chapter>();
		mApi.getBookMixAToc(bookId, "chapters").subscribe(new Observer<BookMixAToc>() {

			@Override
			public void onCompleted() {
			}

			@Override
			public void onError(Throwable arg0) {
				bool.setValue(true);
				arg0.printStackTrace();
			}

			@Override
			public void onNext(BookMixAToc arg0) {
				System.out.println(arg0.mixToc.book+":"+arg0.mixToc.chaptersUpdated);
				for(Chapters cha : arg0.mixToc.chapters) {
					Chapter chapter = new Chapter();
					chapter.setSource(getTag());
					chapter.setTitle(cha.title);
					chapter.setUrl(cha.link);
					chapters.add(chapter);
				}
				bool.setValue(true);
			}
		});
		bool.lockThread();
		return chapters;
	}

	@Override
	public NovelDetail getNovelDetail(final String url) throws ParserException {
		final BoolSet bool = new BoolSet(false);
		final NovelDetail nd = new NovelDetail();
		mApi.getBookDetail(url).subscribe(new Observer<BookDetail>() {

			@Override
			public void onCompleted() {
			}

			@Override
			public void onError(Throwable arg0) {
				bool.setValue(true);
			}

			@Override
			public void onNext(BookDetail arg0) {
				Novel novel = new Novel();
				nd.setNovel(novel);
				novel.setAuthor(arg0.author);
				novel.setName(arg0.title);
				novel.setBrief(arg0.longIntro);
				novel.setThumb(filterCover(arg0.cover));
				novel.setLastUpdateChapter(arg0.lastChapter);
				novel.setLastUpdateTime(filterUpdate(arg0.updated));
				novel.setUrl(url);
				novel.setKind(arg0.cat);
				bool.setValue(true);
			}

		});
		bool.lockThread();
		nd.setChapters(getNovelChapers(url));
		return nd;
	}
	
	private String filterUpdate(String updated) {
		if(TextUtils.isEmpty(updated)) 
			return "";
		return updated.substring(0,updated.lastIndexOf(".")).replace("T", " ");
	}

	@Override
	public List<Novel> getLastUpdates(String url) throws ParserException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Novels getSortKindNovels(String url) throws ParserException {
		final BoolSet bool = new BoolSet(false);
		final Novels novels = new Novels();
		final List<Novel> listNovel = new ArrayList<Novel>();
		novels.setNovels(listNovel);
		int p = url.lastIndexOf("-");
		final String kind = url.substring(0, p == -1 ? url.length() : p);
		final int start = p == -1 ? 0:Integer.parseInt(url.substring(p + 1));
		mApi.getBooksByCats("male", "hot", kind, null, start, LIMIT)
		.subscribeOn(Schedulers.newThread())
		.subscribe(new Observer<BooksByCats>() {

			@Override
			public void onCompleted() {
			}

			@Override
			public void onError(Throwable arg0) {
				novels.setIsok(false);
				arg0.printStackTrace();
				bool.setValue(true);
			}

			@Override
			public void onNext(BooksByCats arg0) {
				LogUtils.i(TAG, "arg0.books size = " + arg0.books.size());
				for(BooksByCats.BooksBean bean : arg0.books) {
					Novel novel = new Novel();
					novel.setName(bean.title);
					novel.setBrief(bean.shortIntro);
					novel.setAuthor(bean.author);
					novel.setUrl(bean._id);
					novel.setKind(kind);
					novel.setThumb(filterCover(bean.cover));
					novel.setLastUpdateChapter(bean.lastChapter);
					listNovel.add(novel);
				}
				novels.setNextUrl(kind + "-" + (start + LIMIT));
				bool.setValue(true);
			}
		});
		bool.lockThread();
		return novels;
	}
	
	private String filterCover(String cover) {
//		int p = cover.indexOf("http");
//		if(p == -1) {
			return Constant.IMG_BASE_URL + cover;
//		}
//		return cover.substring(p);
	}

	@Override
	public Novels getRankNovels(String url) throws ParserException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Pair<String, String>> getSortKindUrlPairs() {
		final List<Pair<String, String>> result  = new ArrayList<Pair<String, String>>();
		final BoolSet bool = new BoolSet(false);
		mApi.getCategoryList().subscribeOn(Schedulers.io())
		.subscribe(new Observer<CategoryList>() {

			@Override
			public void onCompleted() {
				bool.setValue(true);
			}

			@Override
			public void onError(Throwable arg0) {
				bool.setValue(true);
			}

			@Override
			public void onNext(CategoryList arg0) {
				for(CategoryList.MaleBean bean : arg0.male) {
					Pair<String, String> P = new Pair<String, String>(bean.name, bean.name);
					result.add(P);
				}
				bool.setValue(true);
			}
		});
		bool.lockThread();
		System.out.println(result.size());
		return result;
	}

	private void waitResult(final BoolSet bool) {
		while(!bool.getValue()) {
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
		}
	}

	@Override
	public List<Pair<String, String>> getLastUpdateUrlPairs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Pair<String, String>> getWeekRankUrlPairs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Pair<String, String>> getMonthRankUrlPairs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Pair<String, String>> getAllRankUrlPairs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getChapterUrl(String url) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTag() {
		// TODO Auto-generated method stub
		return TAG;
	}

	@Override
	public DataInterface select(String url) {
		if(URLUtil.isHttpUrl(url) || URLUtil.isHttpsUrl(url))
			return null;
		LogUtils.i(TAG, "select : url = " + url);
		return this;
	}

}
