package com.justwayward.reader.view.readview;

import java.io.File;
import java.util.List;

import com.lw.bean.Chapter;

public interface ReadViewAdapter {

    
    public int getChapterCount();
    
    public File getChapterFile(int chapter);
    
    public String getChapterTitle(int chapter);
    
    public void changeData(List<Chapter> chapters);
}
