package com.lw.ttzw;

import java.util.ArrayList;
import java.util.List;

public class SourceSelector {

	private static List<DataInterface> mDataInterfaces;
	
	private static DataInterface mDataInterface;
	
	public static void init() {
		mDataInterfaces = new ArrayList<DataInterface>();
		TTZWImpl tt = new TTZWImpl();
		mDataInterface = tt;
		mDataInterfaces.add(tt);
		mDataInterfaces.add(new XS84Impl());
	}
	
	public static DataInterface selectDataInterface(String url) {
		for(DataInterface d : mDataInterfaces) {
			if(d.select(url) != null) {
				System.out.println(d.getTag() + "::" + url);
				return d;
			}
		}
		return null;
	}
	
	public static List<DataInterface> getAllSourceInterface() {
		return mDataInterfaces;
	}
	
	public static DataInterface getDefaultSource() {
		return mDataInterface;
	}
	
	public static void setDefaultSource(DataInterface d) {
		mDataInterface = d;
	}
	
	public static String getDefaultSourceTag() {
		return mDataInterface.getTag();
	}
}
