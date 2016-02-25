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
 *自定义绘画歌词，产生滚动效果
 * @author YONGJIAN
 *
 */
public class LrcView extends TextView {

	private float width;        //歌词视图宽度  
	private float height;       //歌词视图高度  
	private Paint currentPaint; //当前画笔对象  
	private Paint notCurrentPaint;  //非当前画笔对象  
	private float textHeight = 25;  //文本高度  
	private float textSize = 24;        //文本大小  
	private int index = 0;     	 //list集合下标  
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
		setFocusable(true);         //设置可对焦
		//高亮部分
		currentPaint = new Paint();
		currentPaint.setAntiAlias(true);     //设置抗锯齿，让文字美观饱满  
		currentPaint.setTextAlign(Align.CENTER);
		//非高亮部分  
        notCurrentPaint = new Paint();  
        notCurrentPaint.setAntiAlias(true);  
        notCurrentPaint.setTextAlign(Paint.Align.CENTER);  
	}
	/**
	 * 绘画歌词
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
			//画出本句之前的句子
			for (int i = index-1; i >=0; i--) {
				tempY = tempY - textHeight;
				canvas.drawText(lrcInfos.get(i).getLrcStr(), width/2, tempY, notCurrentPaint);
			}
			tempY = height/2;
			// 画出本句之后的句子
			for (int i = index+1; i < lrcInfos.size(); i++) {
				tempY = tempY + textHeight;
				canvas.drawText(lrcInfos.get(i).getLrcStr(), width/2, tempY, notCurrentPaint);
			}
		} catch (Exception e) {
			// TODO: handle exception
			//setText("没有歌词文件，赶紧去下载");
			canvas.drawText("没有歌词文件，赶紧去下载", width/2, height/2, currentPaint);
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

