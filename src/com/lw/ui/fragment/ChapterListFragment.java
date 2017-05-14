package com.lw.ui.fragment;

import java.util.List;

import com.justwayward.reader.view.readview.SettingManager;
import com.lw.bean.Chapter;
import com.lw.bean.Novel;
import com.lw.novel.common.FileUtil;
import com.lw.novelreader.R;
import com.lw.ttzw.NovelManager;
import com.lw.ui.activity.NovelReadActivity;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ChapterListFragment extends ListFragment{

	private ChapterAdapter mChapterAdapter;
	private Novel mCurrentNovel;
	
	private int curPos = 1;
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mCurrentNovel = NovelManager.getInstance().getCurrentNovel();
		curPos = SettingManager.getInstance().getReadProgress(mCurrentNovel.id + "")[0];
		List<Chapter> data = NovelManager.getInstance().getChapers();
//		for(Chapter chapter : data){
//			chapter.isDownload = FileUtil.isChapterExist(chapter);
//		}
		mChapterAdapter = new ChapterAdapter(data);
		getListView().setFastScrollEnabled(true);
		setListAdapter(mChapterAdapter);
		
		setSelection(curPos - 1);
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		NovelManager.getInstance().setChapterId(position + 1);
		NovelReadActivity.startNovelReadActivity(getActivity(), position + 1);
		getActivity().finish();
//		EventBus.getDefault().post(new EventMessage(EventMessage.START_READ, position + 1));
	}
	
	
	class ChapterAdapter extends BaseAdapter {
		private List<Chapter> mData;
		private LayoutInflater mInflater;
		
		public ChapterAdapter(List<Chapter> chapters) {
			mData = chapters;
			mInflater = LayoutInflater.from(getActivity());
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mData.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return mData.get(arg0);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if(convertView == null) {
				convertView = mInflater.inflate(R.layout.chapter_item, null);
				holder = new ViewHolder();
				holder.icon = (ImageView) convertView.findViewById(R.id.downloaded);
				holder.title = (TextView) convertView.findViewById(R.id.chapter_title);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			Chapter chapter = mData.get(position);
			holder.title.setText(chapter.getTitle());
			if(curPos == position + 1) {
				holder.icon.setImageResource(R.drawable.red_choose);
			} else if(FileUtil.isChapterExist(chapter)) {
				holder.icon.setImageResource(R.drawable.point_select_green);
			} else {
				holder.icon.setImageResource(R.drawable.point_unselect);
			}
			
			return convertView;
		}
		
		class ViewHolder {
			ImageView icon;
			TextView title;
		}
		
	}
}
