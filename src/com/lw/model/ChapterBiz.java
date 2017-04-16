package com.lw.model;

import java.io.IOException;

import org.htmlparser.util.ParserException;

import com.lw.bean.Chapter;
import com.lw.bean.Novel;
import com.lw.novel.common.FileUtil;
import com.lw.ttzw.TTZWManager;

import android.os.AsyncTask;

public class ChapterBiz implements IChapterBiz {

	@Override
	public void getChapterContent(final Novel novel, final Chapter chapter, final OnChapterContentListener listener) {
		new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				String path = null;
				try {
					path = FileUtil.saveChapter(novel.getName(), novel.getAuthor(), chapter.getTitle(),
							TTZWManager.getChapterContent(chapter.getUrl()));
					chapter.setContentPath(path);
				} catch (ParserException e) {
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// try {
				// path = FileUtil.saveChapter("s", "s", "s", "ds");
				// System.out.println("ddd");
				// } catch (IOException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }
				return path;
			}

			@Override
			protected void onPostExecute(String result) {
				listener.onFile(result);
			}

		}.execute();
	}

}
