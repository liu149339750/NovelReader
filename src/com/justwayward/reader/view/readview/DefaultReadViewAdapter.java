package com.justwayward.reader.view.readview;

import java.io.File;
import java.util.List;

import com.lw.bean.Chapter;
import com.lw.novel.utils.FileUtil;

public class DefaultReadViewAdapter implements ReadViewAdapter{

    private List<Chapter> mChapters;
    private int mCurrentChapter;
    private String bookId;
    private BaseReadView mBaseReadView;
    
    public DefaultReadViewAdapter(BaseReadView view,String bookid,List<Chapter> c) {
        mChapters = c;
        bookId = bookid;
        mBaseReadView = view;
    }
    
    public File getChapterFile(int chapter) {
        File file = FileUtil.getChapterFile(bookId, chapter);
        return file;
    }
    
    
    public int getChapterCount() {
        return mChapters.size();
    }
    
    public String getChapterTitle(int position) {
        return mChapters.get(position).getTitle();
    }
}
