package com.lw.bean;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

public class Novel implements Parcelable{

	public int id;
	public String name;
	public String author;
	public String brief;
	public String thumb;
	public String url;
	public String kind;

	public String lastUpdateTime;
	public String lastUpdateChapter;
	public String lastUpdateChapterUrl;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		System.out.println("novel id = " + id);
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getBrief() {
		return brief;
	}

	public void setBrief(String brief) {
		this.brief = brief;
	}

	public String getThumb() {
		return thumb;
	}

	public void setThumb(String thumb) {
		this.thumb = thumb;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public String getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(String lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public String getLastUpdateChapter() {
		return lastUpdateChapter;
	}

	public void setLastUpdateChapter(String lastUpdateChapter) {
		this.lastUpdateChapter = lastUpdateChapter;
	}

	public String getLastUpdateChapterUrl() {
		return lastUpdateChapterUrl;
	}

	public void setLastUpdateChapterUrl(String lastUpdateChapterUrl) {
		this.lastUpdateChapterUrl = lastUpdateChapterUrl;
	}

	@Override
	public String toString() {
		return "=======\n" + kind + "\n" + name + "\n" + url + "\n" + author + "\n" + lastUpdateChapter + "\n"
				+ lastUpdateChapterUrl + "\n" + lastUpdateTime + "\n" + brief + "\n=======";
	}

	@Override
	public int describeContents() {
		return 0;
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
	}
	
	public static final Parcelable.Creator<Novel> CREATOR = new Creator<Novel>() {
		
		@Override
		public Novel[] newArray(int size) {
			// TODO Auto-generated method stub
			return new Novel[size];
		}
		
		@Override
		public Novel createFromParcel(Parcel source) {
			Novel novel = new Novel();
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
			return novel;
		}
	};
}
