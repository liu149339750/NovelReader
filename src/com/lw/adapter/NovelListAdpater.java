package com.lw.adapter;

import java.util.ArrayList;
import java.util.List;

import com.lw.bean.Novel;
import com.lw.bean.Novels;
import com.lw.novelreader.NovelItem;
import com.lw.novelreader.R;
import com.lw.novelreader.R.layout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class NovelListAdpater extends BaseAdapter {
//		private List<Novel> result;
		private List<Novels> mNovels;
		private LayoutInflater mInflater;
		private int mLayout;
//		public NovelListAdpater(Context context,List<Novel> result) {
//			this.result = result;
//			mInflater = LayoutInflater.from(context);
//			mLayout = R.layout.search_item;
//		}
//		
//		public NovelListAdpater(Context context,List<Novel> result,int layout) {
//			this.result = result;
//			mInflater = LayoutInflater.from(context);
//			mLayout = layout;
//		}
		
		public NovelListAdpater(Context context,Novels result) {
			this.mNovels = new ArrayList<Novels>();
			mNovels.add(result);
			mInflater = LayoutInflater.from(context);
			mLayout = R.layout.search_item;
		}
		
		public NovelListAdpater(Context context,Novels result,int layout) {
			this.mNovels = new ArrayList<Novels>();
			mNovels.add(result);
			mInflater = LayoutInflater.from(context);
			mLayout = layout;
		}
		
		public NovelListAdpater(Context context,List<Novels> result) {
			this.mNovels = result;
			mInflater = LayoutInflater.from(context);
			mLayout = R.layout.search_item;
		}
		
		public NovelListAdpater(Context context,List<Novels> result,int layout) {
			this.mNovels = result;
			mInflater = LayoutInflater.from(context);
			mLayout = layout;
		}
		
		@Override
		public int getCount() {
			int c = 0;
			if(mNovels != null) {
				for(Novels n : mNovels) {
					c += n.getNovels().size();
				}
			}
			return c;
		}

		@Override
		public Novel getItem(int arg0) {
			int c = 0;
			Novel novel = null;
			if(mNovels != null) {
				for(Novels n : mNovels) {
					int size = n.getNovels().size();
					if(arg0 < c + size) {
						novel = n.getNovels().get(arg0 - c);
						break;
					}
					c = c + size;
				}
			}
			return novel;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			NovelItem item = null;
			if(convertView == null) {
				convertView = mInflater.inflate(mLayout, null);
				item = new NovelItem(convertView);
				convertView.setTag(item);
			} else {
				item = (NovelItem) convertView.getTag();
			}
			item.bind(getItem(position));
			return convertView;
		}
		
		
	}