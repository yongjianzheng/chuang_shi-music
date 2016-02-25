package com.example.chuanshi_music.View;

import java.util.ArrayList;
import java.util.List;

import com.example.chuanshi_music.R.color;
import com.example.chuanshi_music.model.LrcInfo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.Paint.Align;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 *�Զ���滭��ʣ���������Ч��
 * @author YONGJIAN
 *
 */
public class LrcView extends TextView {

	private float width;        //�����ͼ���  
	private float height;       //�����ͼ�߶�  
	private Paint currentPaint; //��ǰ���ʶ���  
	private Paint notCurrentPaint;  //�ǵ�ǰ���ʶ���  
	private float textHeight = 25;  //�ı��߶�  
	private float textSize = 24;        //�ı���С  
	private int index = 0;     	 //list�����±�  
	private List<LrcInfo>  lrcInfos = new ArrayList<LrcInfo>();
	
	public List<LrcInfo> getLrcInfos() {
		return lrcInfos;
	}
	public void setLrcInfos(List<LrcInfo> lrcInfos) {
		this.lrcInfos = lrcInfos;
	}
	
	
	public LrcView(Context context) {
		super(context);
		init();
	}
	public LrcView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	public LrcView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}
	private void init() {
		setFocusable(true);         //���ÿɶԽ�
		//��������
		currentPaint = new Paint();
		currentPaint.setAntiAlias(true);     //���ÿ���ݣ����������۱���  
		currentPaint.setTextAlign(Align.CENTER);
		//�Ǹ�������  
        notCurrentPaint = new Paint();  
        notCurrentPaint.setAntiAlias(true);  
        notCurrentPaint.setTextAlign(Paint.Align.CENTER);  
	}
	/**
	 * �滭���
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		if (canvas == null) {
			return;
		}
		currentPaint.setColor(Color.argb(210, 251, 248, 29));
		notCurrentPaint.setColor(Color.GRAY);  
		
		currentPaint.setTextSize(32);
		currentPaint.setTypeface(Typeface.SERIF);
		notCurrentPaint.setTextSize(textSize);
		notCurrentPaint.setTypeface(Typeface.DEFAULT);
		
		try {
			setText("");
			canvas.drawText(lrcInfos.get(index).getLrcStr(), width/2, height/2, currentPaint);
			float tempY = height/2;
			//��������֮ǰ�ľ���
			for (int i = index-1; i >=0; i--) {
				tempY = tempY - textHeight;
				canvas.drawText(lrcInfos.get(i).getLrcStr(), width/2, tempY, notCurrentPaint);
			}
			tempY = height/2;
			// ��������֮��ľ���
			for (int i = index+1; i < lrcInfos.size(); i++) {
				tempY = tempY + textHeight;
				canvas.drawText(lrcInfos.get(i).getLrcStr(), width/2, tempY, notCurrentPaint);
			}
		} catch (Exception e) {
			// TODO: handle exception
			//setText("û�и���ļ����Ͻ�ȥ����");
			canvas.drawText("û�и���ļ����Ͻ�ȥ����", width/2, height/2, currentPaint);
		}
		
	}
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
		this.width = w;
		this.height = h;
	}
	public void setIndex(int index) {
		this.index = index;
	}
}

