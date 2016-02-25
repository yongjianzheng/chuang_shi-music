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
 * �����ʵ���
 * @author YONGJIAN
 *
 */
public class LrcUtil {
	private List<LrcInfo> lrcList;				 //List���ϴ�Ÿ�����ݶ���  
	private LrcInfo lrcInfo;				   //����һ��������ݶ���  
	//�޲ι��캯������ʵ��������
	public LrcUtil() {
		lrcList = new ArrayList<LrcInfo>();
		lrcInfo = new LrcInfo();
	}
	
	/**
	 * ��ȡ���
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
				//�滻�ַ� 
				s = s.replace("[", "");
				s = s.replace("]", "@");
				//���롰@���ַ�  
				String splitLrcData[] = s.split("@");
				if (splitLrcData.length>1) {
					lrcInfo.setLrcStr(splitLrcData[1]);
					  //������ȡ�ø�����ʱ��  
				    int lrcTime = time2Str(splitLrcData[0]);  
					lrcInfo.setLrcTime(lrcTime);lrcInfo.setLrcTime(lrcTime);
					//��ӽ��б�����  
					lrcList.add(lrcInfo);
					//�´���������ݶ���  
					lrcInfo = new LrcInfo();
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();  
            stringBuilder.append("û�и���ļ����Ͻ�ȥ���أ�");  
		}catch (IOException e) {
			 e.printStackTrace();  
	         stringBuilder.append("û�ж�ȡ�����Ŷ��");  
		}
		return stringBuilder.toString();
	}
	/**
	 * �������ʱ��
	 * [00:02.32]����Ѹ 
     * [00:03.43]�þò��� 
     * [00:05.22]�������  ���� 
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
