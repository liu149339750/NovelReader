package com.lw.novelreader;

import java.util.ArrayList;
import java.util.List;

import com.bumptech.glide.Glide;
import com.lw.bean.Novel;
import com.lw.bean.ShelftBook;
import com.lw.ui.activity.NovelDetailActivity;
import com.lw.widget.DownloadText;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

public class BookItemAdpater extends BaseAdapter {

	private List<Novel>  mNovels;
	private LayoutInflater mInflater;
	private List<Integer> mShowMore = new ArrayList<Integer>();
	
	public BookItemAdpater(List<? extends  Novel> n,LayoutInflater inflate) {
		mNovels =   (List<Novel>) n;
		mInflater = inflate;
	}
	
	public BookItemAdpater(LayoutInflater inflate) {
		mNovels =   new ArrayList<Novel>();
		mInflater = inflate;
	}
	
	public List<Novel> getNovels() {
		return mNovels;
	}
	
	public void changeData(List<? extends  Novel> novel) {
		mNovels.clear();
		mNovels.addAll(novel);
		notifyDataSetChanged();
	}
	
	public View bind(View view,int layoutId,int position) {
		ViewHolder holder = null;
		final Novel novel = mNovels.get(position);
		if(view == null) {
			view = mInflater.inflate(layoutId, null);
			holder = new ViewHolder();
			holder.thumb = (ImageView) view.findViewById(R.id.thumb);
			holder.name = (TextView) view.findViewById(R.id.novel_name);
			holder.last_chapter = (TextView) view.findViewById(R.id.last_chapter);
			holder.hideMenuLayout = view.findViewById(R.id.hideMenuLayout);
			holder.toggle_more = (ToggleButton) view.findViewById(R.id.toggle_more);
			holder.lastUpdateTime = (TextView) view.findViewById(R.id.last_update_time);
			holder.chapterCount = (TextView) view.findViewById(R.id.chapter_count);
			holder.delete = (TextView) view.findViewById(R.id.delete);
			holder.detail = (TextView) view.findViewById(R.id.detail);
			holder.downloadText =  (DownloadText) view.findViewById(R.id.download);
			view.setTag(layoutId, holder);
		} else {
			holder = (ViewHolder) view.getTag(layoutId);
		}
		holder.detail.setOnClickListener(new ClickListener(novel));
		holder.delete.setOnClickListener(new ClickListener(novel));
		holder.last_chapter.setText(novel.getLastUpdateChapter());
		holder.name.setText(novel.getName());
		holder.lastUpdateTime.setText(novel.getLastUpdateTime());
		if(novel instanceof ShelftBook) {
			ShelftBook sb = (ShelftBook) novel;
			holder.chapterCount.setText( sb.getCurrentChapterPosition()  + "/" + sb.chapterCount);
		}
		Glide.with(view.getContext()).load(novel.getThumb()).placeholder(R.drawable.default_cover).crossFade()
				.into(holder.thumb);
		
		
		final ViewHolder vh = holder;
		vh.downloadText.bindText(novel);
		view.findViewById(R.id.toggleView).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				vh.toggle_more.setChecked(!vh.toggle_more.isChecked());
			}
		});
		holder.toggle_more.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					vh.hideMenuLayout.setVisibility(View.VISIBLE);
					if(!mShowMore.contains(novel.getId()))
						mShowMore.add(novel.getId());
				} else {
					vh.hideMenuLayout.setVisibility(View.GONE);
					boolean b = mShowMore.remove(Integer.valueOf(novel.getId()));
				}
			}
		});
		if(mShowMore.contains(novel.getId())) {
			holder.toggle_more.setChecked(true);
		} else {
			holder.toggle_more.setChecked(false);
		}
		return view;
	}
	
	public class ViewHolder {
		 ImageView thumb;
		 TextView name;
		 TextView last_chapter;
		 View hideMenuLayout;
		 ToggleButton toggle_more;
		 TextView lastUpdateTime;
		 TextView chapterCount;
		 
		 DownloadText downloadText;
		 TextView detail;
		 TextView delete;
	}

	@Override
	public int getCount() {
		return mNovels.size();
	}


	@Override
	public Object getItem(int position) {
		return mNovels.get(position);
	}


	@Override
	public long getItemId(int position) {
		return position;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return bind(convertView, R.layout.novel_item, position);
	}

	
	class ClickListener implements View.OnClickListener {
		
		private Novel mNovel;
		public ClickListener(Novel novel) {
			mNovel = novel;
		}
		
		@Override
		public void onClick(View v) {
			int id = v.getId();
			switch (id) {
			case R.id.delete:
				BookShelftManager.instance().rmBookFromShelft(mNovel.getId());
				mNovels.remove(mNovel);
				mShowMore.remove(Integer.valueOf(mNovel.getId()));
				notifyDataSetChanged();
				break;
			case R.id.detail:
				NovelDetailActivity.startDetailActivity(v.getContext(), mNovel);
				break;
			default:
				break;
			}
		}
		
	}
}