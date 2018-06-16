package com.ax.picpreview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.WindowManager;

/**
 * Created by VERTU on 2018/6/14.
 */

public class MyImageView extends android.support.v7.widget.AppCompatImageView {
	private int viewWidth, viewHeight;
	private float lastX, lastY;// 上一次记录的点
	private float firstDistance;//上一次两点间的距离
	private float offsetX, offsetY;//x,y轴的偏移距离
	private int bitmapLeft, bitmapRight, bitmapTop, bitmapBottom;
	private float scale = 1f;
	private int halfScreenWidth;

	private Bitmap bitmap;

	public MyImageView(Context context) {
		super(context);
		halfScreenWidth = getScreenWidth(context) / 2;
	}

	public MyImageView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		halfScreenWidth = getScreenWidth(context) / 2;
	}


	@Override
	public void setImageBitmap(Bitmap bm) {
		super.setImageBitmap(bm);
		bitmap = bm;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
				lastX = event.getX();
				lastY = event.getY();
				break;
			case MotionEvent.ACTION_POINTER_DOWN:
				firstDistance = getDistance(event);
				break;
			case MotionEvent.ACTION_MOVE:
				if (event.getPointerCount() == 2) {//两点触摸
					float dis = getDistance(event);
					float varScale = (float) Math.pow(dis*1.0 / firstDistance, 1.0 / 4);
					scale *= varScale;
					if (scale > 10) {
						scale = 10f;
					}
					if (scale < 0.1) {
						scale = 0.1f;
					}
				} else if (event.getPointerCount() == 1) {//单点触摸
					float currentX = event.getX();
					float currentY = event.getY();
					if (event.getRawX() < halfScreenWidth){
						offsetX += currentX - lastX;
					} else {
						offsetX += lastX - currentX;
					}
					offsetY += currentY - lastY;
					lastX = currentX;//替换上一次位置
					lastY = currentY;
				}
				break;
			case MotionEvent.ACTION_UP:
				break;
			default:
				break;
		}

		super.onTouchEvent(event);
		return true;
	}

	private float getDistance(MotionEvent event) {
		float disX = event.getX(0) - event.getX(1);//x轴的距离
		float disY = event.getY(0) - event.getY(1);//y轴的距离
		return (float) Math.sqrt(disX * disX + disY * disY);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		viewWidth = getMeasuredWidth();
		viewHeight = getMeasuredHeight();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (bitmap != null) {
			try {
				int scaleHeight = (int) (viewWidth / (float) bitmap.getWidth() * bitmap.getHeight());
				bitmapLeft = halfScreenWidth-(int) (viewWidth*scale - offsetX);
				bitmapRight = halfScreenWidth+(int) (viewWidth*scale - offsetX);
				bitmapTop = (int) (offsetY + (scaleHeight - scaleHeight * scale) / 2);
				bitmapBottom = (int) (bitmapTop + scaleHeight * scale);
				Paint paint = new Paint();
				canvas.drawBitmap(bitmap, new Rect(0, 0, (int) (bitmap.getWidth() - offsetX), bitmap.getHeight()),
						new Rect(bitmapLeft, bitmapTop, halfScreenWidth, bitmapBottom), paint);
				Matrix m = new Matrix();
				m.postScale(-1, 1);//水平方向镜像
				Bitmap b = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
				canvas.drawBitmap(b, new Rect((int) offsetX, 0, b.getWidth(), b.getHeight()),
						new Rect(halfScreenWidth, bitmapTop, bitmapRight, bitmapBottom), paint);
			} catch (Exception e) {
				e.printStackTrace();
			}

			invalidate();
		} else {
			try {
				bitmap = ((BitmapDrawable) getDrawable()).getBitmap();
			} catch (Exception e) {
				e.printStackTrace();
				bitmap = null;
			}
		}
	}

	public static int getScreenWidth(Context context)
	{
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		return outMetrics.widthPixels;
	}

}
