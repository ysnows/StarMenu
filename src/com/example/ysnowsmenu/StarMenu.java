package com.example.ysnowsmenu;

import android.R.transition;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.Interpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

public class StarMenu extends ViewGroup implements OnClickListener {
	
	public static final int POS_LEFT_TOP=0;
	private static final int POS_LEFT_BOTTOM=1;
	private static final int POS_RIGHT_TOP=2;
	private static final int POS_RIGHT_BOTTOM=3;
	
	private View mCButton;
	private Status mCurrentStatus=Status.CLOSE;
	private Position mPosition=Position.RIGHT_BOTTOM;
	private int mRadius;
	private OnMenuItemClickListener listener;
	
	public void setListener(OnMenuItemClickListener listener) {
		this.listener = listener;
	}

	private enum Status{
		CLOSE,OPEN
	}
	
	private enum Position{
		LEFT_TOP,LEFT_BOTTOM,RIGHT_TOP,RIGHT_BOTTOM
	}
	
	private interface OnMenuItemClickListener{
		void onClick(View v,int postion);
	}
	
	
	public StarMenu(Context context, AttributeSet attrs) {
		this(context, attrs,0);
	}
	public StarMenu(Context context) {
		this(context,null);
	}
	public StarMenu(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mRadius=(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
		//获取自定义的属性
		TypedArray a=context.getTheme().obtainStyledAttributes(attrs, R.styleable.StarMenu, defStyleAttr, 0);
		int pos=a.getInt(R.styleable.StarMenu_position, POS_RIGHT_BOTTOM);
		switch (pos) {
		case POS_LEFT_TOP:
			mPosition=Position.LEFT_TOP;
			break;
		case POS_LEFT_BOTTOM:
			mPosition=Position.LEFT_BOTTOM;
			break;
		case POS_RIGHT_TOP:
			mPosition=Position.RIGHT_TOP;
			break;
		case POS_RIGHT_BOTTOM:
			mPosition=Position.RIGHT_BOTTOM;
			break;

		default:
			break;
		}
		
		mRadius=(int) a.getDimension(R.styleable.StarMenu_radius, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics()));
		
		Log.e("TAG", mPosition+"**"+mRadius);
		
		a.recycle();
		
		
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		
		//测量子控件 
		 int count=getChildCount();
		for(int i=0;i<count;i++){
			measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec);
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		
		if(changed){//如果布局发生改变
			layoutCButton();
			layoutOthers();
		}
		
	}
	
	private void layoutOthers() {
		//首先要得到others的数量->计算出平均的角度->通过数学定理得出每个的l和t
		 int count=getChildCount();
		double angle = Math.PI/2/(count-2);
		
		for(int i=0;i<count-1;i++){
			View child=getChildAt(i+1);
			child.setVisibility(View.GONE);
			
			int cl=(int) (mRadius*Math.sin(angle*i));
			int ct=(int)(mRadius*Math.cos(angle*i));
			
			int cHeight=child.getMeasuredHeight();
			int cWidth=child.getMeasuredWidth();
			
			if(mPosition==Position.LEFT_BOTTOM||mPosition==Position.RIGHT_BOTTOM){
				ct=getMeasuredHeight()-ct-cHeight;
			}
			
			if(mPosition==Position.RIGHT_BOTTOM||mPosition==Position.RIGHT_TOP){
				cl=getMeasuredWidth()-cl-cWidth;
			}
			
			
			child.layout(cl, ct, cl+cWidth, ct+cHeight);
			
			
		}
		
		
		
		
	}
	//定位主菜单
	private void layoutCButton() {
		//初始化mCButton
		mCButton=getChildAt(0);
		mCButton.setOnClickListener(this);
		
		//根据传递进来的mPosition对mCButton进行摆放
		int l=0;
		int t=0;
		int height=mCButton.getMeasuredHeight();
		int width=mCButton.getMeasuredWidth();
		
		switch (mPosition) {
		case LEFT_TOP:
			 l=0;
			 t=0;
			break;
		case LEFT_BOTTOM:
			 l=0;
			 t=getMeasuredHeight()-height;
			break;
		case RIGHT_TOP:
			 l=getMeasuredWidth()-width;
			 t=0;
			break;
		case RIGHT_BOTTOM:
			 l=getMeasuredWidth()-width;
			 t=getMeasuredHeight()-height;	
			break;

		default:
			break;
		}
		
		mCButton.layout(l, t, l+width, t+height);
		
	}
	@Override
	public void onClick(View v) {
		//旋转按钮
		rotateCButton(v,0,360,300);
		
		//toggle菜单
		toggleMenu(300);
		
	}
	
	/**
	 * @param duration 
	 * 
	 */
	private void toggleMenu(int duration) {
		//位所有item添加两个动画,平移动画和旋转动画
		 int count=getChildCount();
		double angle = Math.PI/2/(count-2);
		for(int i=0;i<count-1;i++){
			final View child=getChildAt(i+1);
			int cl=(int) (mRadius*Math.sin(angle*i));
			int ct=(int)(mRadius*Math.cos(angle*i));
			
			int xFlag=1;
			int yFlag=1;
			
			if(mPosition==Position.LEFT_TOP||mPosition==Position.LEFT_BOTTOM){
				xFlag=-1;
			}
			if(mPosition==Position.LEFT_TOP||mPosition==Position.RIGHT_TOP){
				yFlag=-1;
			}
			
			TranslateAnimation tranAnim=null;
			child.setVisibility(View.VISIBLE);
			if(mCurrentStatus==Status.CLOSE){
				tranAnim=new TranslateAnimation(xFlag*cl,0,yFlag*ct, 0);
				child.setClickable(true);
				child.setFocusable(true);
			}else {
				tranAnim=new TranslateAnimation(0, xFlag*cl, 0, yFlag*ct);
				child.setClickable(false);
				child.setFocusable(false);
			}
			
			tranAnim.setFillAfter(true);
			tranAnim.setDuration(duration);
			tranAnim.setStartOffset(i*150/count);
			tranAnim.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {
					
				}
				@Override
				public void onAnimationRepeat(Animation animation) {
					
				}
				@Override
				public void onAnimationEnd(Animation animation) {
					//动画结束的时候改变View的visibility
					if(mCurrentStatus==Status.CLOSE){
						child.setVisibility(View.GONE);
					}
				}
			});
			
			RotateAnimation rotate=new RotateAnimation(0, 720, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
			rotate.setDuration(duration);
			rotate.setFillAfter(true);
			
			AnimationSet animSet=new AnimationSet(true);
			animSet.addAnimation(rotate);
			animSet.addAnimation(tranAnim);
			
			child.setAnimation(animSet);
			final int pos=i+1;
			child.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(listener!=null){
						listener.onClick(child, pos);
					}
					setMenuItemAnim(pos);
					changeStatus();
				}

			});
			
			
			
		}
		
		changeStatus();
		 
	}
	
	private void setMenuItemAnim(int pos) {
		 int count=getChildCount();
		for(int i=0;i<count-1;i++){
			View child=getChildAt(i+1);
			if(pos==(i+1)){
				//点击的按钮
				child.startAnimation(ScaleBigAnim(300));
			}else {
				child.startAnimation(ScaleSmallAnim(300));
			}
			
			child.setClickable(false);
			child.setFocusable(false);
		}
		
		
		
	}
	private Animation ScaleBigAnim(int duration) {
		AnimationSet animSet=new AnimationSet(true);
		ScaleAnimation scale=new ScaleAnimation(1f, 4f, 1f, 4f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		AlphaAnimation alpha=new AlphaAnimation(1f, 0f);
		animSet.addAnimation(scale);
		animSet.addAnimation(alpha);
		animSet.setDuration(duration);
		animSet.setFillAfter(true);
		return animSet;
	}
	private Animation ScaleSmallAnim(int duration) {
		AnimationSet animSet=new AnimationSet(true);
		ScaleAnimation scale=new ScaleAnimation(1f, 0f, 1f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		AlphaAnimation alpha=new AlphaAnimation(1f, 0f);
		animSet.addAnimation(scale);
		animSet.addAnimation(alpha);
		animSet.setDuration(duration);
		animSet.setFillAfter(true);
		return animSet;
	}
	private void changeStatus() {
		mCurrentStatus=mCurrentStatus==Status.CLOSE?Status.OPEN:Status.CLOSE;
	}
	public void rotateCButton(View v, int start, int end, int duration) {
		RotateAnimation anim=new RotateAnimation(start, end, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		anim.setDuration(duration);
		anim.setFillAfter(true);
		v.startAnimation(anim);
	}
	
	
}
