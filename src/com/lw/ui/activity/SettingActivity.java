package com.lw.ui.activity;

import com.lw.novel.utils.AppUtils;
import com.lw.novel.utils.SettingUtil;
import com.lw.novelreader.R;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

public class SettingActivity extends PreferenceActivity implements OnPreferenceChangeListener{

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.setting_preference);
        findPreference(SettingUtil.PULL_UPDATE_NOVEL_NUM).setOnPreferenceChangeListener(this);
    }
    
    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        // TODO Auto-generated method stub
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
    
    public static void startSettingActivity() {
        Intent intent = new Intent();
        intent.setClass(AppUtils.getAppContext(), SettingActivity.class);
        AppUtils.getAppContext().startActivity(intent);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String key = preference.getKey();
        if(SettingUtil.PULL_UPDATE_NOVEL_NUM.equals(key)) {
            SettingUtil.setPullUpdateNovelNum(Integer.parseInt(newValue.toString()));
        }
        return true;
    }
}
