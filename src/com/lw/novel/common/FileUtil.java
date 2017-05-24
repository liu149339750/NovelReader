package com.lw.novel.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.justwayward.reader.view.readview.LogUtils;
import com.lw.bean.Chapter;
import com.lw.bean.Novel;
import com.lw.ttzw.NovelManager;

import android.os.Environment;

public class FileUtil {

	
	private static final String DIRECTORY = "lwreader";
	
	public static void init() {
		File baseDir = Environment.getExternalStorageDirectory();
		File file = new File(baseDir, DIRECTORY);
		if(file.exists()) {
			if(!file.isDirectory()) {
				file.delete();
				file.mkdir();
			}
		} else {
			file.mkdir();
		}
	}
	
	public static void deleteNovel(Novel n) {
		String base = getBaseDir();
		File dir = new File(new File(base), n.getName() + "/" + n.getAuthor());
		File files[] = dir.listFiles();
		if(files == null) 
			return;
		for(File file : files) {
			file.delete();
		}
		dir.delete();
	}
	
	public static String getBaseDir() {
		File baseDir = Environment.getExternalStorageDirectory();
		File file = new File(baseDir, DIRECTORY);
		return file.getPath();
	}
	
	public static String saveChapter(String novelName,String author,String chapterTitle,String content) throws IOException {
		File baseDir = Environment.getExternalStorageDirectory();
		File distFile = new File(baseDir, DIRECTORY + "/" +novelName +"/" + author  + "/" + chapterTitle.trim() + ".txt");
		if(!distFile.getParentFile().exists()) {
			boolean b= distFile.getParentFile().mkdirs();
		}
		FileOutputStream fos = new FileOutputStream(distFile.getPath());
		fos.write(content.getBytes("utf-8"));
		fos.flush();
		fos.close();
		if(distFile.length() == 0) {
			return null;
		}
		return distFile.getPath();
	}
	
	public static String getChapterPath(Novel novel,int c) {
		File baseDir = Environment.getExternalStorageDirectory();
		Chapter chapter = NovelManager.getInstance().getChapter(c);
		File distFile = new File(baseDir, DIRECTORY + "/" +novel.getName() +"/" + novel.getAuthor()  + "/" + chapter.getTitle().trim() + ".txt");
		return distFile.getPath();
	}
	
	public static String getChapterPath(Novel novel,Chapter chapter) {
		File baseDir = Environment.getExternalStorageDirectory();
		File distFile = new File(baseDir, DIRECTORY + "/" +novel.getName() +"/" + novel.getAuthor()  + "/" + chapter.getTitle().trim() + ".txt");
		return distFile.getPath();
	}

	public static File getChapterFile(String bookId, int chapter) {
        File file = new File(getChapterPath(NovelManager.getInstance().getCurrentNovel(), chapter));
        if (!file.exists())
            createFile(file);
        return file;
	}
	
	public static boolean isChapterExist(int chapter) {
		Novel novel = NovelManager.getInstance().getCurrentNovel();
		String path = getChapterPath(novel, chapter);
		File file = new File(path);
		return file.exists() && file.length() > 10;
	} 
	
	public static boolean isChapterExist(Novel novel, Chapter chapter) {
		String path = getChapterPath(novel, chapter);
		File file = new File(path);
		return file.exists() && file.length() > 10;
	}
	
	public static boolean isChapterExist(Chapter chapter) {
		Novel novel = NovelManager.getInstance().getCurrentNovel();
		String path = getChapterPath(novel, chapter);
		File file = new File(path);
		return file.exists() && file.length() > 10;
	} 
	
    public static String createFile(File file) {
        try {
            if (file.getParentFile().exists()) {
                LogUtils.i("----- 创建文件" + file.getAbsolutePath());
                file.createNewFile();
                return file.getAbsolutePath();
            } else {
                createDir(file.getParentFile().getAbsolutePath());
                file.createNewFile();
                LogUtils.i("----- 创建文件" + file.getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    
    public static String createDir(String dirPath) {
        try {
            File file = new File(dirPath);
            if (file.getParentFile().exists()) {
                LogUtils.i("----- 创建文件夹" + file.getAbsolutePath());
                file.mkdir();
                return file.getAbsolutePath();
            } else {
                createDir(file.getParentFile().getAbsolutePath());
                LogUtils.i("----- 创建文件夹" + file.getAbsolutePath());
                file.mkdir();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dirPath;
    }


}
