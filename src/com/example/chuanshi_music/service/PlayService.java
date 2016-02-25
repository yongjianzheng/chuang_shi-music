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

	private MediaPlayer mediaPlayer;						// ý�岥��������
	private String path;                         		   // �����ļ�·��  
    private int msg; 	
    private int current = 0;                	  		  // ��¼��ǰ���ڲ��ŵ�����  
    private int currentTime;  
    private int duration;        				 		   //���ų���  
    private boolean isPause;                     		   // ��ͣ״̬  
    private int status = 3;      						   //����״̬��Ĭ��Ϊ˳�򲥷�  
	List<MusicInfo> list = new ArrayList<MusicInfo>();     // ���music����ļ���
	private myReceiver receiver;
	
	public static final String UPDATE_ACTION = "com.action.UPDATE_ACTION";  //���¶���  
    public static final String CTL_ACTION = "com.action.CTL_ACTION";        //���ƶ���  
    public static final String MUSIC_CURRENT = "com.action.MUSIC_CURRENT";  //��ǰ���ֲ���ʱ����¶���  
    public static final String MUSIC_DURATION = "com.action.MUSIC_DURATION";//�����ֳ��ȸ��¶���  
	
    
    /**
     * handler ����������Ϣ�������͹㲥���²��Ž�����
     */
    private Handler handler = new Handler() {  
        public void handleMessage(android.os.Message msg) {  
            if (msg.what == 1) {  
                if(mediaPlayer != null) {  
                    currentTime = mediaPlayer.getCurrentPosition(); // ��ȡ��ǰ���ֲ��ŵ�λ��  
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
         * �������ֲ������ʱ�ļ����� 
         */  
		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer mp) {
				if (status == 1) {							//����ѭ��
					mediaPlayer.start();
				}else if (status == 2) {					//ȫ��ѭ��
					current++;
					if (current>list.size()-1) {
						current = 0;
					}
					Intent sendIntent = new Intent(UPDATE_ACTION);
					sendIntent.putExtra("current", current);
					sendBroadcast(sendIntent);
					path = list.get(current).getUrl();
					play(0);
				}else if (status == 3) {					//˳�򲥷�
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
				}else if (status == 4) {					//�������
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

		path = intent.getStringExtra("url");     			//����·�� 
        current = intent.getIntExtra("listPosition", -1);   //��ǰ���Ÿ�������musicInfos��λ��  
        msg = intent.getIntExtra("MSG", 0);      		    //������Ϣ  
        if (msg == AppConstant.PLAY_MSG) {    				//ֱ�Ӳ�������  
            play(0);  
        }else if (msg== AppConstant.PROGRESS_CHANGE) {         // ���ȸ���
        	currentTime = intent.getIntExtra("progress", -1);  
        	play(currentTime);  
        }else if (msg==AppConstant.PAUSE_MSG) {				   //��ͣ����	
        	pause();
        }else if (msg==AppConstant.CONTINUE_MSG) {				//ֹͣ����
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
            mediaPlayer.reset();// �Ѹ�������ָ�����ʼ״̬  
            mediaPlayer.setDataSource(path);  
            mediaPlayer.prepare(); // ���л���  
            mediaPlayer.setOnPreparedListener(new PreparedListener(currentTime));// ע��һ��������  
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
	            mediaPlayer.start(); // ��ʼ����  
	            if (currentTime > 0) { // ������ֲ��Ǵ�ͷ����  
	                mediaPlayer.seekTo(currentTime);  
	            }  
	            Intent intent = new Intent();  
	            intent.setAction(MUSIC_DURATION);  
	            duration = mediaPlayer.getDuration();  
	            intent.putExtra("duration", duration);  //ͨ��Intent�����ݸ������ܳ���  
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