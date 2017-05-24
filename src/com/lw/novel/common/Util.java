package com.lw.novel.common;

import java.net.URI;

import com.lw.novelreader.R;
import com.viewpagerindicator.TitlePageIndicator;
import com.viewpagerindicator.TitlePageIndicator.IndicatorStyle;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;

public class Util {

	
    public static void hideNavigationBar(Activity context) {
        int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
            | View.SYSTEM_UI_FLAG_FULLSCREEN; // hide status bar

        if( android.os.Build.VERSION.SDK_INT >= 19 ){ 
            uiFlags |= 0x00001000;    //SYSTEM_UI_FLAG_IMMERSIVE_STICKY: hide navigation bars - compatibility: building API level is lower thatn 19, use magic number directly for higher API target level
        } else {
            uiFlags |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
        }
        context.getWindow().getDecorView().setSystemUiVisibility(uiFlags);
    }
    

    
    public static void setIndicatorTheme(Context context,TitlePageIndicator indicator) {
        final float density = context.getResources().getDisplayMetrics().density;
        indicator.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
        indicator.setFooterColor(0xFFFFFFFF);
        indicator.setFooterLineHeight(1 * density); //1dp
        indicator.setFooterIndicatorHeight(5 * density); //3dp
        indicator.setFooterIndicatorStyle(IndicatorStyle.Triangle);
        indicator.setTextColor(0xAA000000);
        indicator.setSelectedColor(0xFF000000);
        indicator.setSelectedBold(true);
    }
    
	public static  boolean isPhoneUrl(String url) {
		URI uri = URI.create(url); 
		String host = uri.getHost();
		if(!TextUtils.isEmpty(host) && host.startsWith("m")) {
			return true;
		}
		return false;
	}
}
