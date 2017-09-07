package com.justwayward.reader.view.readview;

import java.io.File;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.lw.bean.Chapter;
import com.lw.novel.utils.FileUtil;

public class DefaultReadViewAdapter implements ReadViewAdapter{

    private List<Chapter> mChapters;
    private int mCurrentChapter;
    private String bookId;
    private Lock mLock = new ReentrantLock();
    
    public DefaultReadViewAdapter(String bookid,List<Chapter> c) {
        mChapters = c;
        bookId = bookid;
    }
    
    public File getChapterFile(int chapter) {
        File file = FileUtil.getChapterFile(bookId, chapter);
        return file;
    }
    
    
    public int getChapterCount() {
        mLock.lock();
        int size = 0;
        try{
            size = mChapters.size();
        } finally {
            mLock.unlock();
        }
        return size;
    }
    
    public String getChapterTitle(int position) {
        mLock.lock();
        String title = null;
        try{
            title = mChapters.get(position).getTitle();
        } finally {
            mLock.unlock();
        }
        return title;
    }

    @Override
    public void changeData(List<Chapter> chapters) {
        mLock.lock();
        try{
            mChapters.clear();
            mChapters.addAll(chapters);
        } finally {
            mLock.unlock();
        }
    }
}
