package com.example.chuanshi_music.UI;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import com.example.chuanshi_music.R;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.view.View.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import com.example.chuanshi_music.View.LrcView;
import com.example.chuanshi_music.model.LrcInfo;
import com.example.chuanshi_music.model.MusicInfo;
import com.example.chuanshi_music.util.LrcUtil;
import com.example.chuanshi_music.util.MediaUtil;

public class ShoLrcView extends Activity implements OnClickListener{
	
	private TextView returnMusic;
	private TextView load;
	private LrcView lrcView;
	
	private lrcReceiver receiver;
	private int current;
	private List<LrcInfo> lrcInfos;
	private LrcUtil lrcUtil;
	private int index = 0;
	private int currentTime;
	private int duration;
	public static final String MUSIC_CURRENT = "com.action.MUSIC_CURRENT";  
	
	List<MusicInfo> list = new ArrayList<MusicInfo>(); 
	
	private Handler handler = new Handler();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.lrc_layout);
		lrcView = (LrcView) findViewById(R.id.lrcShowView);
		returnMusic = (TextView) findViewById(R.id.returnMusic);
		load = (TextView) findViewById(R.id.load);
		returnMusic.setOnClickListener(this);
		load.setOnClickListener(this);
		Intent intent = getIntent();
		if (intent != null) {
			current = intent.getIntExtra("current", -1);
		}
		list = MediaUtil.getMusic(this);
		initLrc();
		
		receiver = new lrcReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(MUSIC_CURRENT);
		registerReceiver(receiver, filter);
	}
	private void initLrc() {
		lrcUtil = new LrcUtil();
		lrcUtil.readLrc(list.get(current).getUrl());
		//System.out.println(list.get(current).getUrl());
		lrcInfos = lrcUtil.getLrcList();
		lrcView.setLrcInfos(lrcInfos);
		lrcView.setAnimation(AnimationUtils.loadAnimation(ShoLrcView.this, R.anim.alpha_z));
		handler.post(runnable);
	}
	/**
	 * 根据时间获取歌词显示的索引值
	 * @return
	 */
	public int lrcIndex() {
		
		if(currentTime < duration) {
			for (int i = 0; i < lrcInfos.size(); i++) {
				if (i < lrcInfos.size() - 1) {
					if (currentTime < lrcInfos.get(i).getLrcTime() && i == 0) {
						index = i;
					}
					if (currentTime > lrcInfos.get(i).getLrcTime()
							&& currentTime < lrcInfos.get(i + 1).getLrcTime()) {
						index = i;
					}
				}
				if (i == lrcInfos.size() - 1
						&& currentTime > lrcInfos.get(i).getLrcTime()) {
					index = i;
				}
			}
		}
		return index;
	}
	Runnable runnable = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			lrcView.setIndex(lrcIndex());
			lrcView.invalidate();
			handler.postDelayed(runnable, 100);
		}
	};
	
	
	private class lrcReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			currentTime = intent.getIntExtra("currentTime", -1);
			duration = intent.getIntExtra("duration", -1);
		}
		
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		finish();
		unregisterReceiver(receiver);
	}
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		switch (arg0.getId()) {
		case R.id.returnMusic:
			intent.setClass(ShoLrcView.this, MainActivity.class);
			this.finish();
			startActivity(intent);
			break;
		case R.id.load:
			
			break;
		default:
			break;
		}
	}

	
}
