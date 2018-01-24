package com.lw.ttzw;

import java.util.ArrayList;
import java.util.List;

import com.lw.datainterfaceimpl.BiqugeImpl;
import com.lw.datainterfaceimpl.TTZWImpl;
import com.lw.datainterfaceimpl.XS84Impl;
import com.lw.datainterfaceimpl.ZhuishuImpl;
import com.lw.novel.utils.LogUtils;

public class SourceSelector {

	private static List<DataInterface> mDataInterfaces;
	
	private static DataInterface mDataInterface;
	
	private static final String TAG = "SourceSelector";
	
	public static void init() {
		mDataInterfaces = new ArrayList<DataInterface>();
		TTZWImpl tt = new TTZWImpl();
		mDataInterface = tt;
		mDataInterfaces.add(tt);
		mDataInterfaces.add(new XS84Impl());
		mDataInterfaces.add(new ZhuishuImpl());
		mDataInterfaces.add(new BiqugeImpl());
	}
	
	public static DataInterface selectDataInterface(String url) {
		for(DataInterface d : mDataInterfaces) {
			if(d.select(url) != null) {
				LogUtils.v(TAG, "selectDataInterface :" + d.getTag() + "::" + url);
				return d;
			}
		}
		return null;
	}
	
	public static void addDataInterface(DataInterface in) {
		mDataInterfaces.add(in);
	}
	
	public static List<DataInterface> getAllSourceInterface() {
		return mDataInterfaces;
	}
	
	public static List<String> getAllSourceString() {
		List<DataInterface> list = getAllSourceInterface();
		List<String> data = new ArrayList<>();
		for(DataInterface di : list) {
			data.add(di.getTag());
		}
		return data;
	}
	
	public static DataInterface getDefaultSource() {
		return mDataInterface;
	}
	
	public static void setDefaultSource(DataInterface d) {
		System.out.println("current source " + d.getTag());
		mDataInterface = d;
	}
	
	public static void setDefaultSource(String tag) {
		for(DataInterface d : mDataInterfaces) {
			if(d.getTag().equals(tag)) {
				mDataInterface = d;
				break;
			}
		}
	}
	
	public static String getDefaultSourceTag() {
		return mDataInterface.getTag();
	}
}
