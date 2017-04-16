package com.lw.novelreader;

import java.util.List;

import com.lw.bean.Novel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class NovelListAdpater extends BaseAdapter {
		private List<Novel> result;
		private LayoutInflater mInflater;
		public NovelListAdpater(Context context,List<Novel> result) {
			this.result = result;
			mInflater = LayoutInflater.from(context);
		}
		
		@Override
		public int getCount() {
			return result.size();
		}

		@Override
		public Novel getItem(int arg0) {
			return result.get(arg0);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			NovelItem item = null;
			if(convertView == null) {
				convertView = mInflater.inflate(R.layout.search_item, null);
				item = new NovelItem(convertView);
				convertView.setTag(item);
			} else {
				item = (NovelItem) convertView.getTag();
			}
			item.bind(getItem(position));
			return convertView;
		}
		
		
	}