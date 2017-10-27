package com.lw.presenter;


import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import com.lw.bean.NovelDetail;
import com.lw.model.ChapterBiz;
import com.lw.model.IChapterBiz;
import com.lw.model.INovelBiz;
import com.lw.model.NovelBiz;
import com.lw.model.OnChapterContentListener;
import com.lw.novel.utils.LogUtils;
import com.lw.ttzw.NovelManager;
import com.lw.ui.fragment.IChapterContentView;

import android.text.TextUtils;

public class ChapterContentPresenter {

	private IChapterBiz mChapterBiz;
	private IChapterContentView mChapterContentView;
	
	private INovelBiz mINovelBiz;
	private static final String TAG = "ChapterContentPresenter";
	private boolean isCancel;

	public ChapterContentPresenter(IChapterContentView chapterContentView) {
		mChapterContentView = chapterContentView;
		mChapterBiz = new ChapterBiz();
		mINovelBiz = new NovelBiz();
	}

	public void prepareChapterContent(final int chapter) {
		// System.out.println("prepareChapterContent bookid = " +
		// NovelManager.getInstance().getCurrentNovel().getId());
	    isCancel = false;
		mChapterContentView.showLoading(NovelManager.getInstance().getChapter(chapter).getTitle());
		mChapterBiz.getChapterContent(NovelManager.getInstance().getCurrentNovel(),
				NovelManager.getInstance().getChapter(chapter), new OnChapterContentListener() {

					@Override
					public void onFile(String path) {
					    if(isCancel) {
					        LogUtils.v(TAG, "cancel prepareChapterContent");
					        return;
					    }
						if (TextUtils.isEmpty(path)) {
							mChapterContentView.hideLoading();
							mChapterContentView.onLoadFail(chapter);
						} else {
							System.out.println(path);
							mChapterContentView.hideLoading();
							mChapterContentView.showChapterContent(chapter);
						}
					}
				});
	}
	
	/**when open a novel,update the novel detail info*/
	public void updateNovelChapters(String url) {
	    LogUtils.v(TAG, "updateNovelChapters url = " + url);
	    isCancel = false;
	    Observable.just(url).observeOn(Schedulers.io())
	    .map(new Func1<String, NovelDetail>() {

            @Override
            public NovelDetail call(String arg0) {
                return mINovelBiz.getNovelInfo(arg0);
            }
        })
	    .subscribe(new Action1<NovelDetail>() {

            @Override
            public void call(NovelDetail arg0) {
                if(arg0 == null) {
                    LogUtils.v(TAG, "updateNovelChapters fail");
                    return;
                }
                if(isCancel) {
                    LogUtils.v(TAG, "cancel updateNovelChapters");
                    return;
                }
                int size =  NovelManager.getInstance().getChapterSize();
                LogUtils.v(TAG, "updateNovelChapters size ");
                if(arg0.getChapters().size() > size) {
                    NovelManager.getInstance().setChapers(arg0.getChapters());
                    mChapterContentView.onChapterChange(arg0.getChapters());
                }
            }
        });
	}
	
	public void cancel() {
	    isCancel = true;
	}

}
