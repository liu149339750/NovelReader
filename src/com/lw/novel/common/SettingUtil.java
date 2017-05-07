package com.lw.novel.common;

import com.justwayward.reader.view.readview.SharedPreferencesUtil;

public class SettingUtil {

	public static final String PULL_UPDATE_NOVEL_NUM = "pull_update_novel_num";
	
	public static int getPullUpdateNovelNum() {
		return SharedPreferencesUtil.getInstance().getInt(PULL_UPDATE_NOVEL_NUM, 8);
	}
	
	public static void setPullUpdateNovelNum(int num) {
		SharedPreferencesUtil.getInstance().putInt(PULL_UPDATE_NOVEL_NUM, num);
	}
}
