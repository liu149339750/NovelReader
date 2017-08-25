package com.justwayward.reader.view.readview;

import java.io.File;

public interface ReadViewAdapter {

    
    public int getChapterCount();
    
    public File getChapterFile(int chapter);
    
    public String getChapterTitle(int chapter);
}
