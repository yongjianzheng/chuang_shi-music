package com.example.chuanshi_music.service;

import java.util.ArrayList;
import java.util.List;


import com.example.chuanshi_music.UI.MainActivity;
import com.example.chuanshi_music.model.*;
import com.example.chuanshi_music.util.MediaUtil;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Handler;
import android.os.IBinder;

public class PlayService extends Service {

	private MediaPlayer mediaPlayer;						// 媒体播放器对象
	private String path;                         		   // 音乐文件路径  
    private int msg; 	
    private int current = 0;                	  		  // 记录当前正在播放的音乐  
    private int currentTime;  
    private int duration;        				 		   //播放长度  
    private boolean isPause;                     		   // 暂停状态  
    private int status = 3;      						   //播放状态，默认为顺序播放  
	List<MusicInfo> list = new ArrayList<MusicInfo>();     // 存放music对象的集合
	private myReceiver receiver;
	
	public static final String UPDATE_ACTION = "com.action.UPDATE_ACTION";  //更新动作  
    public static final String CTL_ACTION = "com.action.CTL_ACTION";        //控制动作  
    public static final String MUSIC_CURRENT = "com.action.MUSIC_CURRENT";  //当前音乐播放时间更新动作  
    public static final String MUSIC_DURATION = "com.action.MUSIC_DURATION";//新音乐长度更新动作  
	
    
    /**
     * handler 用来接收消息，来发送广播更新播放进度条
     */
    private Handler handler = new Handler() {  
        public void handleMessage(android.os.Message msg) {  
            if (msg.what == 1) {  
                if(mediaPlayer != null) {  
                    currentTime = mediaPlayer.getCurrentPosition(); // 获取当前音乐播放的位置  
                    Intent intent = new Intent();  
                    intent.setAction(MUSIC_CURRENT);  
                    intent.putExtra("currentTime", currentTime);  
                    intent.putExtra("duration", duration);
                    sendBroadcast(intent); 
                    handler.sendEmptyMessageDelayed(1, 1000);  
                }  
                  
            }  
        };  
    };
	
    
    @Override
	public void onCreate() {
    	
		super.onCreate();
		mediaPlayer = new MediaPlayer();
		list = MediaUtil.getMusic(PlayService.this);
		

        /** 
         * 设置音乐播放完成时的监听器 
         */  
		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer mp) {
				if (status == 1) {							//单曲循环
					mediaPlayer.start();
				}else if (status == 2) {					//全部循环
					current++;
					if (current>list.size()-1) {
						current = 0;
					}
					Intent sendIntent = new Intent(UPDATE_ACTION);
					sendIntent.putExtra("current", current);
					sendBroadcast(sendIntent);
					path = list.get(current).getUrl();
					play(0);
				}else if (status == 3) {					//顺序播放
					current++;
					if (current<= list.size()-1) {
						Intent sendIntent = new Intent(UPDATE_ACTION);
						sendIntent.putExtra("current", current);
						sendBroadcast(sendIntent);
						path = list.get(current).getUrl();
						play(0);
					}else {
						current = 0;
						mediaPlayer.seekTo(0);
						Intent sendIntent = new Intent(UPDATE_ACTION);
						sendIntent.putExtra("current", current);
						sendBroadcast(sendIntent);
					}
				}else if (status == 4) {					//随机播放
					current = getRandomIndex(list.size()-1);
					Intent sendIntent = new Intent(UPDATE_ACTION);
					sendIntent.putExtra("current", current);
					sendBroadcast(sendIntent);
					path = list.get(current).getUrl();
					play(0);
				}
				
			}
		});
		
		 receiver = new myReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(MainActivity.CTL_ACTION);
		registerReceiver(receiver, filter);
	}
    
    protected int getRandomIndex(int end) {  
        int index = (int) (Math.random() * end);  
        return index;  
    }  
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		path = intent.getStringExtra("url");     			//歌曲路径 
        current = intent.getIntExtra("listPosition", -1);   //当前播放歌曲的在musicInfos的位置  
        msg = intent.getIntExtra("MSG", 0);      		    //播放信息  
        if (msg == AppConstant.PLAY_MSG) {    				//直接播放音乐  
            play(0);  
        }else if (msg== AppConstant.PROGRESS_CHANGE) {         // 进度更新
        	currentTime = intent.getIntExtra("progress", -1);  
        	play(currentTime);  
        }else if (msg==AppConstant.PAUSE_MSG) {				   //暂停播放	
        	pause();
        }else if (msg==AppConstant.CONTINUE_MSG) {				//停止播放
        	resume();
        }else if (msg==AppConstant.PRIVIOUS_MSG) {
			play(0);
		}else if (msg==AppConstant.NEXT_MSG) {
			play(0);
		}
        return super.onStartCommand(intent, flags, startId);
  }
	
	private void previous() {
		// TODO Auto-generated method stub
		
	}
	private void next() {
		// TODO Auto-generated method stub

	}

	private void resume() {
		// TODO Auto-generated method stub
		if (isPause) {
			mediaPlayer.start();
			isPause = true;
		}
	}
	private void pause() {
		// TODO Auto-generated method stub
		if (mediaPlayer !=null && mediaPlayer.isPlaying()) {
			mediaPlayer.pause();
			isPause = true;
		}
	}
	private void play(int currentTime) {
		// TODO Auto-generated method stub
		try {  
            mediaPlayer.reset();// 把各项参数恢复到初始状态  
            mediaPlayer.setDataSource(path);  
            mediaPlayer.prepare(); // 进行缓冲  
            mediaPlayer.setOnPreparedListener(new PreparedListener(currentTime));// 注册一个监听器  
            handler.sendEmptyMessage(1);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
	}
	 private final class PreparedListener implements OnPreparedListener {  
	        private int currentTime;  
	  
	        public PreparedListener(int currentTime) {  
	            this.currentTime = currentTime;  
	        }  
	  
	        @Override  
	        public void onPrepared(MediaPlayer mp) {  
	            mediaPlayer.start(); // 开始播放  
	            if (currentTime > 0) { // 如果音乐不是从头播放  
	                mediaPlayer.seekTo(currentTime);  
	            }  
	            Intent intent = new Intent();  
	            intent.setAction(MUSIC_DURATION);  
	            duration = mediaPlayer.getDuration();  
	            intent.putExtra("duration", duration);  //通过Intent来传递歌曲的总长度  
	            sendBroadcast(intent);  
	        }  
	    }  
	 public class myReceiver extends BroadcastReceiver{

			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				int control = intent.getIntExtra("control", -1);
				switch (control) {
				case 1:
					status = 1;
					break;
				case 2:
					status = 2;
					break;
				case 3:
					status = 3;
					break;
				case 4:
					status = 4;
					break;
				default:
					break;
				}
			}
		  
	 }
	 
	    @Override
		public IBinder onBind(Intent arg0) {
			
			return null;
		}
	    @Override
	    public void onDestroy() {
	    	// TODO Auto-generated method stub
	    	super.onDestroy();
	    	unregisterReceiver(receiver);
	    	if (mediaPlayer != null) {
			 mediaPlayer.stop();
			 mediaPlayer.release();
			 mediaPlayer = null;
			}
	    	
	    }



	

}