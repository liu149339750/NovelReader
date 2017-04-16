package com.lw.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class ShelftBook extends Novel implements Parcelable{

	private int currentChapterId;
	private int currentChapterPosition;
	
	public int chapterCount;
	public String readtime;
	
	
	
	public String getReadtime() {
		return readtime;
	}

	public void setReadtime(String readtime) {
		this.readtime = readtime;
	}
	
	public ShelftBook() {
	}
	
	public ShelftBook(int id) {
		this.id = id;
	}
	
	public int getBookId() {
		return id;
	}
	public void setBookId(int bookId) {
		this.id = bookId;
	}
	public int getCurrentChapterId() {
		return currentChapterId;
	}
	public void setCurrentChapterId(int currentChapterId) {
		this.currentChapterId = currentChapterId;
	}
	public int getCurrentChapterPosition() {
		return currentChapterPosition;
	}
	public void setCurrentChapterPosition(int currentChapterPosition) {
		this.currentChapterPosition = currentChapterPosition;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeString(name);
		dest.writeString(author);
		dest.writeString(brief);
		dest.writeString(thumb);
		dest.writeString(url);
		dest.writeString(kind);
		dest.writeString(lastUpdateTime);
		dest.writeString(lastUpdateChapter);
		dest.writeString(lastUpdateChapterUrl);
		dest.writeInt(currentChapterId);
		dest.writeInt(currentChapterPosition);
		dest.writeInt(chapterCount);
		dest.writeString(readtime);
	}
	
	public static final Parcelable.Creator<ShelftBook> CREATOR = new Creator<ShelftBook>() {
		
		@Override
		public ShelftBook[] newArray(int size) {
			// TODO Auto-generated method stub
			return new ShelftBook[size];
		}
		
		@Override
		public ShelftBook createFromParcel(Parcel source) {
			ShelftBook novel = new ShelftBook();
			novel.id = source.readInt();
			novel.name = source.readString();
			novel.author = source.readString();
			novel.brief = source.readString();
			novel.thumb = source.readString();
			novel.url = source.readString();
			novel.kind = source.readString();
			novel.lastUpdateTime = source.readString();
			novel.lastUpdateChapter = source.readString();
			novel.lastUpdateChapterUrl = source.readString();
			novel.currentChapterId = source.readInt();
			novel.currentChapterPosition = source.readInt();
			novel.chapterCount = source.readInt();
			novel.readtime = source.readString();
			return novel;
		}
	};
}
 