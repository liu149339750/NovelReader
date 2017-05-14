package com.lw.ui.fragment;

import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;

public class BaseFragment extends Fragment implements OnKeyListener{

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		int action = event.getAction();
		System.out.println("keyCode = " + keyCode + ",action="+ action);
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			if(action == KeyEvent.ACTION_DOWN) {
				return onBackPress();
			}
			break;

		default:
			break;
		}
		return false;
	}

	protected boolean onBackPress() {
		return false;
	}
	
	
	
	

}
