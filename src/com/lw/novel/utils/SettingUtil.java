package com.lw.novel.utils;

import com.justwayward.reader.view.readview.SharedPreferencesUtil;
import com.lw.novelreader.R;


public class SettingUtil {

	public static final String PULL_UPDATE_NOVEL_NUM = "pull_update_novel_num";
	
	/**书架下拉更新的更新数目*/
	public static int getPullUpdateNovelNum() {
		return SharedPreferencesUtil.getInstance().getInt(PULL_UPDATE_NOVEL_NUM, 8);
	}
	
	public static void setPullUpdateNovelNum(int num) {
		SharedPreferencesUtil.getInstance().putInt(PULL_UPDATE_NOVEL_NUM, num);
	}
	
	/**上拉更新触发时距离结尾的数目*/
	public static int getReloadSpace() {
		return AppUtils.getResource().getInteger(R.integer.reload_before_end);
	}
}
