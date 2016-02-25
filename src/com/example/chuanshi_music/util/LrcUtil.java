package com.example.chuanshi_music.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.graphics.Path;

import com.example.chuanshi_music.model.LrcInfo;

/**
 * 处理歌词的类
 * @author YONGJIAN
 *
 */
public class LrcUtil {
	private List<LrcInfo> lrcList;				 //List集合存放歌词内容对象  
	private LrcInfo lrcInfo;				   //声明一个歌词内容对象  
	//无参构造函数用来实例化对象
	public LrcUtil() {
		lrcList = new ArrayList<LrcInfo>();
		lrcInfo = new LrcInfo();
	}
	
	/**
	 * 读取歌词
	 */
	public String readLrc(String path) {
		
		StringBuilder stringBuilder = new StringBuilder();
		File f = new File(path.replace(".mp3", ".lrc"));
		
		try {
			FileInputStream fis =  new FileInputStream(f);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader br = new BufferedReader(isr);
			String s = null;
			while ((s = br.readLine()) != null){
				//替换字符 
				s = s.replace("[", "");
				s = s.replace("]", "@");
				//分离“@”字符  
				String splitLrcData[] = s.split("@");
				if (splitLrcData.length>1) {
					lrcInfo.setLrcStr(splitLrcData[1]);
					  //处理歌词取得歌曲的时间  
				    int lrcTime = time2Str(splitLrcData[0]);  
					lrcInfo.setLrcTime(lrcTime);lrcInfo.setLrcTime(lrcTime);
					//添加进列表数组  
					lrcList.add(lrcInfo);
					//新创建歌词内容对象  
					lrcInfo = new LrcInfo();
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();  
            stringBuilder.append("没有歌词文件，赶紧去下载！");  
		}catch (IOException e) {
			 e.printStackTrace();  
	         stringBuilder.append("没有读取到歌词哦！");  
		}
		return stringBuilder.toString();
	}
	/**
	 * 解析歌词时间
	 * [00:02.32]陈奕迅 
     * [00:03.43]好久不见 
     * [00:05.22]歌词制作  王涛 
	 * @param timeStr
	 * @return
	 */
	private int time2Str(String timeStr) {
		timeStr = timeStr.replace(":", ".");
		timeStr = timeStr.replace(".", "@");
		
		String timeData[]= timeStr.split("@");
		int minute = Integer.parseInt(timeData[0]);
		int second = Integer.parseInt(timeData[1]);
		int milisecond =  Integer.parseInt(timeData[2]);
		
		int currentTime = (minute*60+second)*1000+milisecond*10;
		return currentTime;
	}
	public List<LrcInfo> getLrcList(){
		return lrcList;
	}
}
