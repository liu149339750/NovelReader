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

	public NovelItem(View view) {
		mView = view;
		thumb = (ImageView) view.findViewById(R.id.thumb);
		name = (TextView) view.findViewById(R.id.novel_name);
		author = (TextView) view.findViewById(R.id.author);
		kind = (TextView) view.findViewById(R.id.kind);
		last_chapter = (TextView) view.findViewById(R.id.last_chapter);
		lastUpdateTime = (TextView) view.findViewById(R.id.last_update_time);

	}

	public void bind(Novel novel) {
		last_chapter.setText(novel.getLastUpdateChapter());
		name.setText(novel.getName());
		if(lastUpdateTime != null)
			lastUpdateTime.setText(novel.getLastUpdateTime());
		author.setText(mView.getContext().getString(R.string.author) + novel.getAuthor());
		kind.setText(novel.getKind());
		Glide.with(mView.getContext()).load(novel.getThumb()).placeholder(R.drawable.default_cover).crossFade()
				.into(thumb);
	}
}
