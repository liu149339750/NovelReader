package com.lw.novel.utils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.lw.bean.Novel;
import com.lw.bean.ShelftBook;

import android.text.TextUtils;

public class SortUtil {

	
	public static void sort(List<ShelftBook> novels) {
		Collections.sort(novels, c1);
	}
	
	 static  Comparator<ShelftBook> c1 = new Comparator<ShelftBook>() {

		@Override
		public int compare(ShelftBook o1, ShelftBook o2) {
			if(TextUtils.isEmpty(o2.readtime)) {
				if(TextUtils.isEmpty(o1.readtime))
					return 0;
				return -1;
			}
			if(TextUtils.isEmpty(o1.readtime)) {
				return 1;
			}
			return o2.readtime.compareTo(o1.readtime);
		}
	};
	
	 static  Comparator<ShelftBook> c2 = new Comparator<ShelftBook>() {

		@Override
		public int compare(ShelftBook o1, ShelftBook o2) {
			return o1.name.compareTo(o2.name);
		}
	};
}
