package com.lw.model;

import com.lw.bean.NovelDetail;

public interface OnDataListener {

	
	public void onSucess(NovelDetail novel);
	public void onFail();
	public void onCancel();
}
