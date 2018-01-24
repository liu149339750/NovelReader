package com.lw.novelreader;

import java.util.ArrayList;
import java.util.List;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.htmlparser.util.ParserException;

import com.lw.bean.BookSource;
import com.lw.bean.Chapter;
import com.lw.bean.ChapterUrl;
import com.lw.bean.Novel;
import com.lw.bean.Novels;
import com.lw.bean.Chapter.Chapters;
import com.lw.db.DBUtil;
import com.lw.novel.utils.AppUtils;
import com.lw.novel.utils.FileUtil;
import com.lw.novel.utils.LogUtils;
import com.lw.ttzw.DataInterface;
import com.lw.ttzw.SourceSelector;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;
import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class SearchSourceService extends Service{

	
	private static final String TAG = "SearchSourceService";
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		LogUtils.v(TAG, "onCreate");
		EventBus.getDefault().register(this);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}

	
	public static void postBackgroundSearch(Novel novel) {
		LogUtils.v(TAG, "postBackgroundSearch");
		if(novel != null && novel.getId() != 0)
		    EventBus.getDefault().post(novel);
	}
	
	private Observable<String> getChapterUrlFromDb(final DataInterface df,final Novel novel) {
		return Observable.create(new OnSubscribe<String>() {

			@Override
			public void call(Subscriber<? super String> arg0) {
				BookSource source = DBUtil.queryBookSourceWithSource(novel.getId(), df.getTag());
				if(source != null) {
					LogUtils.v(TAG, "getChapterUrlFromDb");
					arg0.onNext(source.getChapterUrl());
				}
				arg0.onCompleted();
			}
		});
	}
	
	private Observable<String> getChapterUrlFromNet(final DataInterface df,final Novel novel) {
		return Observable.create(new OnSubscribe<Novel>() {

			@Override
			public void call(Subscriber<? super Novel> arg0) {
				Novels novels = null;
				LogUtils.v(TAG, "getChapterUrlFromNet : " + df.getTag());
				for(int i=0;i<3;i++ ) {
					try {
						LogUtils.v(TAG, "search");
						novels =  df.search(novel.getName());
						break;
					} catch (ParserException e) {
						e.printStackTrace();
						try {
							Thread.sleep(200);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
					}
				}
				if(novels != null) {
					List<Novel> nl = novels.getNovels();
					if(nl != null) {
						LogUtils.v(TAG, "query size = " + nl.size());
						for(Novel n : nl) {
							if(n.getName().equals(novel.getName()) && n.getAuthor().equals(novel.getAuthor())) {
								LogUtils.v(TAG, "call onNext");
								arg0.onNext(n);
								break;
							}
						}
					}
				}
				LogUtils.v(TAG, "call onCompleted");
				arg0.onCompleted();
			}
		}).map(new Func1<Novel, String>() {

			@Override
			public String call(Novel arg0) {
				String url = df.getChapterUrl(arg0.getUrl());
				LogUtils.v(TAG, "saveBookSource : url = " + url + ",baseUrl="+arg0.getUrl());
				if(TextUtils.isEmpty(url)) {
					try {
						url = df.getNovelDetail(arg0.getUrl()).getChapterUrl();
						LogUtils.v(TAG, "saveBookSource : lasturl = " + url);
						arg0.setChapterUrl(url);
					} catch (ParserException e) {
						e.printStackTrace();
					}
				}
				if(!TextUtils.isEmpty(url)) {
					LogUtils.v(TAG, "url = " +url);
					DBUtil.saveBookSource(novel.getId(), df.getTag(), arg0.getUrl(), url,null);
				}
				return url;
			}
		});
	}
	
	@Subscribe(threadMode=ThreadMode.POSTING)
	public synchronized void onBackgroudSync(final Novel novel) {
		LogUtils.v(TAG, "onBackgroudSearch");
		List<DataInterface> sources = SourceSelector.getAllSourceInterface();
//		String defaultTag = SourceSelector.getDefaultSourceTag();
		String defaultTag = SourceSelector.selectDataInterface(novel.getUrl()) != null ? SourceSelector.selectDataInterface(novel.getUrl()).getTag():SourceSelector.getDefaultSourceTag();
		for(DataInterface datainterface : sources) {
			if(datainterface.getTag().equals(defaultTag)) 
				continue;
			final DataInterface df = datainterface;
			Observable.concat(getChapterUrlFromDb(df,novel),getChapterUrlFromNet(df, novel))
			.first()
			.subscribeOn(Schedulers.from(AppUtils.getExecutor()))
			.map(new Func1<String, List<Chapter>>() {

				@Override
				public List<Chapter> call(String url) {
					if(!TextUtils.isEmpty(url)) {
					for(int i=0;i<3;i++) {
						try {
							LogUtils.v(TAG, "getNovelChapers");
							return df.getNovelChapers(url);
						} catch (ParserException e) {
							e.printStackTrace();
						}
					}
					}
					return null;
				}
			})
			.map(new Func1<List<Chapter>, List<Chapter>>() {

				@Override
				public List<Chapter> call(List<Chapter> arg0) {
					if(arg0 != null) {
					    Chapters chapters = new Chapters();
					    chapters.bookid = novel.getId();
					    chapters.chapters = arg0;
					    chapters.source = df.getTag();
					    String path = FileUtil.saveChapterList(chapters);
					    DBUtil.updateChaptersFileUrl(novel.getId(), df.getTag(), path);
					    
						List<ChapterUrl> chapterUrls = DBUtil.queryNovelChapterURLBySource(novel.getId(), df.getTag());
						List<Chapter> dbChapter = DBUtil.queryNovelChapterList(novel.getId());
						LogUtils.v(TAG, df.getTag()+">chapterUrls size="+chapterUrls.size()+",dbChapter size="+dbChapter.size());
						for(ChapterUrl chapterUrl : chapterUrls) {
							for(int i =0;i<dbChapter.size();i++) {
								if(chapterUrl.getChapterId() == dbChapter.get(i).getId()) {
									dbChapter.remove(i);
									break;
								}
							}
						}
						LogUtils.v(TAG, "chapterUrls size="+chapterUrls.size()+",dbChapter size="+dbChapter.size());
						List<Chapter> insertChapters = new ArrayList<Chapter>();
						for(int i=0;i<dbChapter.size();i++) {
							Chapter dc = dbChapter.get(i);
							for(int j=0;j<arg0.size();j++) {
								Chapter chapter = arg0.get(j);
								if(removeMark(dc.getTitle()).equals(removeMark(chapter.getTitle()))) {
									chapter.setId(dc.getId());
									insertChapters.add(chapter);
									arg0.remove(j);
									break;
								}
							}
						}
						LogUtils.v(TAG, "insertChapters size =  " + insertChapters.size() + ",arg0.size()="+arg0.size());
						return insertChapters;
					}
					LogUtils.v(TAG, "arg0 is null");
					return null;
				}
			})
			.map(new Func1<List<Chapter>, Integer>() {

				@Override
				public Integer call(List<Chapter> arg0) {
					if(arg0 != null && arg0.size() > 0) {
						LogUtils.v(TAG, "saveChapterUrlToDB  " );
						return DBUtil.saveChapterUrlToDB(novel.getId(),arg0, df.getTag());
					}
					return 0;
				}
			})
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe(new Subscriber<Integer>() {

				@Override
				public void onCompleted() {
					
				}

				@Override
				public void onError(Throwable arg0) {
					LogUtils.e(TAG, arg0);
				}

				@Override
				public void onNext(Integer arg0) {
					LogUtils.v(TAG, "saveChapterUrlToDB count = " + arg0);
				}
			});
			
		}
	}
	
	private String removeMark(String text) {
		return text.trim().replace(" ", "").replace("：", "").replace(":", "").replaceAll("\\【.+\\】", "");
	}
	
}
