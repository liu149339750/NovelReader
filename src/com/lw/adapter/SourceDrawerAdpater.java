package com.lw.adapter;

import java.util.List;
import com.lw.ttzw.DataInterface;
import com.lw.ttzw.SourceSelector;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SourceDrawerAdpater extends BaseAdapter{

	private LayoutInflater mInflater;
	private List<DataInterface> mDataInterfaces;
 	public SourceDrawerAdpater(Context mainActivity) {
		mInflater = LayoutInflater.from(mainActivity);
		mDataInterfaces = SourceSelector.getAllSourceInterface();
	}

	@Override
	public int getCount() {
		return mDataInterfaces.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mDataInterfaces.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null) {
			convertView = mInflater.inflate(android.R.layout.simple_list_item_1, null);
		} 
		TextView text = (TextView) convertView;
		DataInterface di = mDataInterfaces.get(position);
		text.setText(di.getTag());
		return convertView;
	}

}
