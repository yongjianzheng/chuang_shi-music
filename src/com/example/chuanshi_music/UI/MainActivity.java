package com.example.chuanshi_music.UI;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import android.view.View.OnClickListener;
import com.example.chuanshi_music.model.*;
import com.example.chuanshi_music.R;
import com.example.chuanshi_music.model.MusicInfo;
import com.example.chuanshi_music.service.PlayService;
import com.example.chuanshi_music.util.MediaUtil;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends Activity implements OnClickListener{
	
	private TextView openMenu;
	private TextView lrc;
	private TextView musicName;
	private TextView musicSinger;
	private ImageView img;
	private TextView nowTime;
	private SeekBar seekBar;
	private TextView endTime;
	private ImageButton orderButton;
	private ImageButton preButton;
	private ImageButton startButton;
	private ImageButton nextButton;
	private ImageButton voice;
	SlidingMenu slidingMenu;
	private ListView musicList;
	private List<MusicInfo> musicInfos;
	private SimpleAdapter adapter;
	private int listPosition;
	private HomeReceiver homeReceiver;
	
	private int currentTime;
	private int duration;
	private String title;		//歌曲标题
	private String artist;		//歌曲艺术家
	private String url;	
	private int flag;	
	private int status = 3;         //播放状态，默认为顺序播放  
	

	private boolean isPlaying; 				// 正在播放
	private boolean isPause = true; 		// 暂停
	private boolean isFirstTime = true;
	
	public static final String UPDATE_ACTION = "com.action.UPDATE_ACTION";  
    public static final String CTL_ACTION = "com.action.CTL_ACTION";  
    public static final String MUSIC_CURRENT = "com.action.MUSIC_CURRENT";  
    public static final String MUSIC_DURATION = "com.action.MUSIC_DURATION";  
    public static final String REPEAT_ACTION = "com.action.REPEAT_ACTION";  
    public static final String SHUFFLE_ACTION = "com.action.SHUFFLE_ACTION";  
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		findViewById();
		menuInitView();
		openMenu.setOnClickListener(this);
		startButton.setOnClickListener(this);
		preButton.setOnClickListener(this);
		nextButton.setOnClickListener(this);
		orderButton.setOnClickListener(this);
		lrc.setOnClickListener(this);
		
		homeReceiver = new HomeReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(CTL_ACTION);
		filter.addAction(REPEAT_ACTION);
		filter.addAction(SHUFFLE_ACTION);
		filter.addAction(UPDATE_ACTION);
		filter.addAction(MUSIC_DURATION);
		filter.addAction(MUSIC_CURRENT);
		registerReceiver(homeReceiver, filter);
		
	}
	private void findViewById() {
		// TODO Auto-generated method stub
		openMenu = (TextView) findViewById(R.id.open_menu);
		lrc = (TextView) findViewById(R.id.lrc);
		musicName = (TextView) findViewById(R.id.music_name);
		musicSinger = (TextView) findViewById(R.id.music_singer);
		img = (ImageView) findViewById(R.id.imgView);
		nowTime = (TextView) findViewById(R.id.nowTime);
		seekBar = (SeekBar) findViewById(R.id.sk_seekBar);
		endTime = (TextView) findViewById(R.id.tv_endTime);
		orderButton  = (ImageButton) findViewById(R.id.order);
		preButton  = (ImageButton) findViewById(R.id.prev);
		startButton = (ImageButton) findViewById(R.id.startAndpause);
		nextButton = (ImageButton) findViewById(R.id.next);
		voice = (ImageButton) findViewById(R.id.voice);
		seekBar.setOnSeekBarChangeListener(new SeekBarChangeListener());	
	}

	private void menuInitView() {
		// TODO Auto-generated method stub
		slidingMenu = new SlidingMenu(this);
		slidingMenu.setMode(SlidingMenu.LEFT);
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		slidingMenu.setBehindOffset(150);
		slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		View view= LayoutInflater.from(this).inflate(R.layout.menu_layout, null);
		slidingMenu.setMenu(view);
		musicList = (ListView) findViewById(R.id.music_list);
		musicInfos = MediaUtil.getMusic(this);
		setListAdapter(musicInfos);
		musicList.setOnItemClickListener(new MusicListItemClickListener());
		
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.open_menu:
			slidingMenu.showMenu();
			break;
		case R.id.startAndpause:
			if (isFirstTime) {
				playMusic(0);
				isPause = false;
				isPlaying = true;
				isFirstTime = false;
				startButton.setImageResource(R.drawable.start);
			}else {
				if (isPlaying) {
				startButton.setImageResource(R.drawable.pause);
				intent.setClass(MainActivity.this, PlayService.class);
				intent.putExtra("MSG", AppConstant.PAUSE_MSG);
				startService(intent);
				isPlaying = false;
				isPause = true;
			} else if (isPause) {
				startButton.setImageResource(R.drawable.start);
				intent.setClass(MainActivity.this, PlayService.class);
				intent.putExtra("MSG", AppConstant.CONTINUE_MSG);
				startService(intent);
				isPause = false;
				isPlaying = true;
			}
			}		
			break;
		case R.id.prev:
			previous_music();
			break;
		case R.id.next:
			next_music();
			break;
		case R.id.order:
			if (status == 1) {
				status = 2;
				Toast.makeText(MainActivity.this, "全部循环", Toast.LENGTH_SHORT).show();
				orderButton.setImageResource(R.drawable.mode_loopall);												//转换图标
			}else if (status == 2) {
				status = 3;
				Toast.makeText(MainActivity.this, "顺序播放", Toast.LENGTH_SHORT).show();
				orderButton.setImageResource(R.drawable.mode_order);										//转换图标
			}else if (status == 3) {
				status = 4;
				Toast.makeText(MainActivity.this, "随机播放", Toast.LENGTH_SHORT).show();	
				orderButton.setImageResource(R.drawable.mode_random);												//转换图标
			}else if (status == 4) {
				status = 1;
				Toast.makeText(MainActivity.this, "单曲循环", Toast.LENGTH_SHORT).show();	
				orderButton.setImageResource(R.drawable.mode_loop);										//转换图标
			}
			intent.setAction(CTL_ACTION);
			intent.putExtra("control", status);
			sendBroadcast(intent);
			break;
		case R.id.lrc:
			if (isPlaying) {
				intent.setClass(MainActivity.this, ShoLrcView.class);
				intent.putExtra("current", listPosition);
				startActivity(intent);
			}
		default:
			break;
		}
	}

	private class MusicListItemClickListener implements OnItemClickListener {
    	@Override
    	public void onItemClick(AdapterView<?> parent, View view, int position,
    			long id) {
    		listPosition = position;  
    		slidingMenu.showContent();
            playMusic(listPosition); 
            
    	}
    }

	

	private void setListAdapter(List<MusicInfo> musicInfos2) {
		// TODO Auto-generated method stub
		List<HashMap<String, String>> mp3List  = new ArrayList<HashMap<String,String>>();
		for (Iterator iterator = musicInfos2.iterator();iterator.hasNext();) {
			MusicInfo info =(MusicInfo) iterator.next();
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("order", String.valueOf(info.getId()));
			map.put("musicName", info.getTitle());
			map.put("musicSinger", info.getArtist());
			map.put("duration",MediaUtil.formatTime(info.getDuration()));
			mp3List.add(map);
			adapter = new SimpleAdapter(this, mp3List,R.layout.list_music, 
					new String[]{"order","musicName","musicSinger","duration"}, 
					new int[]{R.id.order,R.id.musicName,R.id.singerName,R.id.musicTime});
			musicList.setAdapter(adapter);
		}
		
	}

	public void playMusic(int listPosition) {
		// TODO Auto-generated method stub
		if (musicInfos != null) {
			MusicInfo musicInfo = musicInfos.get(listPosition);
			title = musicInfo.getTitle();
			musicName.setText(title);
			musicSinger.setText(musicInfo.getArtist());
			endTime.setText(MediaUtil.formatTime(musicInfo.getDuration()));
			duration = (int) musicInfo.getDuration();
			seekBar.setMax(duration);
			startButton.setImageResource(R.drawable.start);
			play();
		}
	}
	private void next_music() {
		// TODO Auto-generated method stub
		if (status == 4) {
			listPosition = getRandomIndex(musicInfos.size()-1);
		}else{
			listPosition = listPosition +1;
		}
			if (listPosition <= musicInfos.size()-1) {
				playMusic(listPosition);
			}else {
				if (status == 3) {
					Toast.makeText(MainActivity.this, "没有下一首了", Toast.LENGTH_SHORT).show();
				} else {
						listPosition = 0;	
						playMusic(listPosition);
						}
				}
	
	}

	private void previous_music() {
		// TODO Auto-generated method stub
		if (status == 4) {
			listPosition = getRandomIndex(musicInfos.size()-1);
		}else{
			listPosition = listPosition -1;
		}
		if (listPosition >=0) {
			playMusic(listPosition);
		}else {
			if (status == 3) {
				Toast.makeText(MainActivity.this, "没有上一首了", Toast.LENGTH_SHORT).show();
			}else {
				listPosition = musicInfos.size()-1;
				playMusic(listPosition);
			}
			
		}
	}
	protected int getRandomIndex(int end) {  
        int index = (int) (Math.random() * end);  
        return index;  
    }  
	



	private void play() {
		// TODO Auto-generated method stub
		MusicInfo musicInfo = musicInfos.get(listPosition);
		Intent intent = new Intent(MainActivity.this, PlayService.class);
		url = musicInfo.getUrl();
		intent.putExtra("url",url );
		intent.putExtra("listPosition", listPosition);
		intent.putExtra("MSG", AppConstant.PLAY_MSG);
		startService(intent);
		isPause = false;
		isFirstTime = false;
		isPlaying = true;
	}
	

	public class HomeReceiver extends BroadcastReceiver{
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action = intent.getAction();
		if (action.equals(MUSIC_CURRENT)) {
			currentTime = intent.getIntExtra("currentTime", -1);
			nowTime.setText(MediaUtil.formatTime(currentTime));
			seekBar.setProgress(currentTime);
		}else if (action.equals(MUSIC_DURATION)) {
			duration = intent.getIntExtra("duration", -1);
		}else if (action.equals(UPDATE_ACTION)) {
			listPosition = intent.getIntExtra("current", -1);
			url = musicInfos.get(listPosition).getUrl();
			if (listPosition >= 0) {
				musicName.setText(musicInfos.get(listPosition).getTitle());
				musicSinger.setText(musicInfos.get(listPosition).getArtist());
			}
			if (listPosition == 0 && status == 3) {
				endTime.setText(MediaUtil.formatTime(musicInfos.get(listPosition).getDuration()));
				startButton.setImageResource(R.drawable.pause);
				isPause = true;
			}
		}
	}
	}
	
	
	private class SeekBarChangeListener implements OnSeekBarChangeListener{

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// TODO Auto-generated method stub
			if (fromUser) {
				audioTrackChange(progress);
			}
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}
		
	}

	public void audioTrackChange(int progress) {
		// TODO Auto-generated method stub
		seekBar.setProgress(currentTime);
		Intent intent = new Intent();
		intent.setClass(MainActivity.this, PlayService.class);
		intent.putExtra("url", url);
		intent.putExtra("listPosition", listPosition);
		if(isPause) {
			intent.putExtra("MSG", AppConstant.PAUSE_MSG);
		}
		else {
			intent.putExtra("MSG", AppConstant.PROGRESS_CHANGE);
		}
		intent.putExtra("progress", progress);
		startService(intent);
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		musicName.setText(musicInfos.get(listPosition).getTitle());
		musicSinger.setText(musicInfos.get(listPosition).getArtist());
		endTime.setText(MediaUtil.formatTime(musicInfos.get(listPosition).getDuration()));
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction()== KeyEvent.ACTION_DOWN ) {
			new AlertDialog.Builder(this)
				.setIcon(R.drawable.ic_launcher)
				.setTitle("退出")
				.setMessage("您确定要退出吗")
				.setNegativeButton("取消", null)
				.setPositiveButton("确定", 
						new  DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								finish();
								Intent intent = new Intent(MainActivity.this,PlayService.class);
								unregisterReceiver(homeReceiver);
								stopService(intent);
							}
						}).show();
			
		}
		return super.onKeyDown(keyCode, event);
	}
	

}
