package com.lw.novelreader;

import com.bumptech.glide.Glide;
import com.lw.bean.Novel;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class NovelItem {

	private View mView;

	private ImageView thumb;
	private TextView name;
	private TextView last_chapter;
	private TextView author;
	private TextView kind;

	private TextView lastUpdateTime;
	
	private TextView brief;

	public NovelItem(View view) {
		mView = view;
		thumb = (ImageView) view.findViewById(R.id.thumb);
		name = (TextView) view.findViewById(R.id.novel_name);
		author = (TextView) view.findViewById(R.id.author);
		kind = (TextView) view.findViewById(R.id.kind);
		last_chapter = (TextView) view.findViewById(R.id.last_chapter);
		lastUpdateTime = (TextView) view.findViewById(R.id.last_update_time);
		brief = (TextView) view.findViewById(R.id.brief);
	}

	public void bind(Novel novel) {
		name.setText(novel.getName());
		author.setText(mView.getContext().getString(R.string.author) + novel.getAuthor());
		if(lastUpdateTime != null)
			lastUpdateTime.setText(novel.getLastUpdateTime());
		if(last_chapter != null)
			last_chapter.setText(novel.getLastUpdateChapter());
		if(kind != null)
			kind.setText(novel.getKind());
		if(brief != null)
			brief.setText(novel.getBrief());
		Glide.with(mView.getContext()).load(novel.getThumb()).placeholder(R.drawable.default_cover).centerCrop()
				.into(thumb);
	}
}
