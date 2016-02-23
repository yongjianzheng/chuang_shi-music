package com.example.chuanshi_music.util;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.example.chuanshi_music.model.MusicInfo;

public class MediaUtil {
	
	public static List<MusicInfo> getMusic(Context context){
		Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
		List<MusicInfo> musicInfos  = new ArrayList<MusicInfo>();
		for (int i = 0; i < cursor.getCount(); i++) {
			cursor.moveToNext();
			MusicInfo info =  new MusicInfo();
		//	long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
			long duration =  cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
			String artist= cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
			String url= cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
			long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
			String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
			int isMusic = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));
			if (isMusic != 0 ) {
		//		info.setId(id);
				info.setDuration(duration);
				info.setArtist(artist);
				info.setSize(size);
				info.setUrl(url);
				info.setTitle(title);
				musicInfos.add(info);
			}
		}
		return musicInfos;
	}
	public static String formatTime(long time) {
		String min = time / (1000 * 60) + "";
		String sec = time % (1000 * 60) + "";
		if (min.length() < 2) {
			min = "0" + time / (1000 * 60) + "";
		} else {
			min = time / (1000 * 60) + "";
		}
		if (sec.length() == 4) {
			sec = "0" + (time % (1000 * 60)) + "";
		} else if (sec.length() == 3) {
			sec = "00" + (time % (1000 * 60)) + "";
		} else if (sec.length() == 2) {
			sec = "000" + (time % (1000 * 60)) + "";
		} else if (sec.length() == 1) {
			sec = "0000" + (time % (1000 * 60)) + "";
		}
		return min + ":" + sec.trim().substring(0, 2);
	}

}
	