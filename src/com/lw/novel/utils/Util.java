package com.lw.novel.utils;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import com.lw.novelreader.R;
import com.viewpagerindicator.TitlePageIndicator;
import com.viewpagerindicator.TitlePageIndicator.IndicatorStyle;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;

public class Util {

	private static final String TAG = "util";
	
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
    

	public static Dialog CreateProgressDialog(Activity activity){
		ProgressDialog dialog= new ProgressDialog(activity);
		dialog.setCanceledOnTouchOutside(false);
		return dialog;
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
	
	public static boolean isUrl(String resource) {
	    int length = resource.length ();
        for (int i = 0; i < length; i++)
        {
            char ch = resource.charAt (i);
            if (!Character.isWhitespace (ch))
            {
                if ('<' == ch)
                    return false;
            }
        }
        return true;
	}
	
	public static String getDeviceId() {
		TelephonyManager tm = (TelephonyManager) AppUtils.getAppContext().getSystemService(Context.TELEPHONY_SERVICE);
		String imei = tm.getDeviceId();
		LogUtils.i(TAG, "imei = " + imei);
		return tm.getDeviceId();
	}
	
    public static String removeMark(String text) {
        return replaceDigitToChinese(text.trim().replace(" ", "").replace("：", "").replace(":", "")
                .replaceAll("\\【.+\\】|\\(|\\)|\\（|\\）", "")); //去掉【】与其中内容,去掉括号
    }
    
    public static String replaceDigitToChinese(String str) {
        int len = str.length();
        StringBuffer sb = new StringBuffer();
        List<A> numbers = new ArrayList<A>();
        int s = -1;
        for(int i=0;i<len;i++) {
            char ch = str.charAt(i);
            if(Character.isDigit(ch)) {
                if(s != -1)
                    continue;
                s = i;
            } else {
                if(s != -1) {
                    A a = new A();
                    a.start = s;
                    a.end = i;
                    a.str = str.substring(s, i);
                    numbers.add(a);
                    s = -1;
                }
                sb.append(ch);
            }
        }
        for(A num : numbers) {
            String rStr = DigitStrToChinese(num.str);
            System.out.println(num.start + ":" + num.end + ":" + num.str);
            str = str.replace(str.substring(num.start, num.end), rStr);
        }
        return str;
    }
    
    public static String DigitStrToChinese(String str) {
        int len = str.length();
        StringBuffer sb = new StringBuffer();
        for(int i=0;i<len;i++){
            char ch = str.charAt(i);
            if(Character.isDigit(ch)) {
                sb.append(getChineseNumByDigit(ch));
            } else {
                sb.append(ch);
            }
        }
        if(len == 2) {
            if(str.charAt(1) == '0') {
                sb.delete(1, 2);
            }
            sb.insert(1, "十");
        }
        return sb.toString();
    }

    private static String getChineseNumByDigit(char ch) {
        String r = ch + "";
        switch (ch) {
            case '0':
                r = "零";
                break;
            case '1':
                r = "一";
                break;
            case '2':
                r = "二";
                break;
            case '3':
                r = "三";
                break;
            case '4':
                r = "四";
                break;
            case '5':
                r = "五";
                break;
            case '6':
                r = "六";
                break;
            case '7':
                r = "七";
                break;
            case '8':
                r = "八";
                break;
            case '9':
                r = "九";
                break;
            default:
                break;
        }
        return r;
    }
    
    static class A {
        int start;
        int end;
        String str;
    }
}
