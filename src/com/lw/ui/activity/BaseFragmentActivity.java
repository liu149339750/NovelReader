package com.lw.ui.activity;

import com.lw.novelreader.R;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

public abstract class BaseFragmentActivity extends Activity{

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getLayoutId());
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().add(R.id.container, getFragment()).commit();
		}
	}

	protected int getLayoutId() {
		return R.layout.activity_main;
	}

	protected abstract Fragment getFragment();
}
