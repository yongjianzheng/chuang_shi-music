package com.example.chuanshi_music.model;

public class MusicInfo {
	private static long id = 1;
	private long duration;
	private String artist;
	private String url;
	private long size;
	private String title;
	
	public long getId() {
		return id++;
	}
	//public void setId(long id) {
	//	this.id = id;
	//}
	public long getDuration() {
		return duration;
	}
	public void setDuration(long duration) {
		this.duration = duration;
	}
	public String getArtist() {
		return artist;
	}
	public void setArtist(String artist) {
		this.artist = artist;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	
}
