package com.lw.presenter;

import com.lw.model.ChapterBiz;
import com.lw.model.IChapterBiz;
import com.lw.model.OnChapterContentListener;
import com.lw.ttzw.NovelManager;
import com.lw.ui.fragment.IChapterContentView;

import android.text.TextUtils;

public class ChapterContentPresenter {

	private IChapterBiz mChapterBiz;
	private IChapterContentView mChapterContentView;

	public ChapterContentPresenter(IChapterContentView chapterContentView) {
		mChapterContentView = chapterContentView;
		mChapterBiz = new ChapterBiz();
	}

	public void prepareChapterContent(final int chapter) {
		// System.out.println("prepareChapterContent bookid = " +
		// NovelManager.getInstance().getCurrentNovel().getId());
		mChapterContentView.showLoading(NovelManager.getInstance().getChapter(chapter).getTitle());
		mChapterBiz.getChapterContent(NovelManager.getInstance().getCurrentNovel(),
				NovelManager.getInstance().getChapter(chapter), new OnChapterContentListener() {

					@Override
					public void onFile(String path) {
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

}
