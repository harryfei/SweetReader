package com.fxbandroid.bookreader.widget; 

import android.content.Context;
import android.util.AttributeSet; 
import android.view.View;
import android.view.ViewGroup; 
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.support.v4.view.ViewPager;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.graphics.Color;
import android.widget.RelativeLayout;

public class TabSwitcher extends LinearLayout {//implements ViewPager.OnPageChangeListener { 

    private int selectedTabIndex;


    private ViewPager viewPager;

    private TextView title0;
    private TextView title1;
    private ImageButton menu;

    private RelativeLayout middleMenuWraper;

    private int currentPage=0;

    public TabSwitcher(Context context) {
        super(context); 
      }

    public TabSwitcher(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(com.fxbandroid.bookreader.bookreader.R.layout.tabswitcher,this, true);
        title0 = (TextView)findViewById(com.fxbandroid.bookreader.bookreader.R.id.book_bt);   
        title1 = (TextView)findViewById(com.fxbandroid.bookreader.bookreader.R.id.file_bt);
        menu = (ImageButton)findViewById(com.fxbandroid.bookreader.bookreader.R.id.menu_bt); 
    }


    public void setViewPager(ViewPager view) {
        viewPager = view;
        setListenOn(); 
        setCurrentPage(0);
    }

    private void setListenOn(){
               
        OnClickListener tabClickListener = new OnClickListener() {
            public void onClick(View view) {
                if(view == title0) {
                    viewPager.setCurrentItem(0);
                }
                else if(view == title1){
                    viewPager.setCurrentItem(1);
                }
            }
        };

         title0.setOnClickListener(tabClickListener);
         title1.setOnClickListener(tabClickListener);
    } 


   
    public void setCurrentPage(int arg0){
        currentPage = arg0;
        if(arg0 == 0){
            title0.setBackgroundResource(com.fxbandroid.bookreader.bookreader.R.drawable.tab_select);
            title1.setBackgroundResource(com.fxbandroid.bookreader.bookreader.R.drawable.tab_normal);
        }
        if(arg0 == 1){
            title1.setBackgroundResource(com.fxbandroid.bookreader.bookreader.R.drawable.tab_select);
            title0.setBackgroundResource(com.fxbandroid.bookreader.bookreader.R.drawable.tab_normal);
        }
    }

    public int getCurrentPage(){
        return currentPage;
    }

    public void setOnClickListenerForMenu(View.OnClickListener listener){
        menu.setOnClickListener(listener);
    }

    public void setOnLongClickListenerForMenu(View.OnLongClickListener listener){
        menu.setOnLongClickListener(listener);
    }

   
    public void setBackgroundForMenu(int resource){
        menu.setBackgroundResource(resource);
    }

    public void setImageForMenu(int resource){
        menu.setImageResource(resource);
    }

    //各种动画

	// 用来适配不同的分辨率
	//private static int xOffset = 15;
	//private static int yOffset = -13;

	//public static void initOffset(Context context) {
	//	xOffset = (int) (10.667 * context.getResources().getDisplayMetrics().density);
	//	yOffset = -(int) (8.667 * context.getResources().getDisplayMetrics().density);
	//}

	// 加号的动画
	public static Animation getRotateAnimation(float fromDegrees, float toDegrees, int durationMillis) {
		RotateAnimation rotate = new RotateAnimation(fromDegrees, toDegrees, Animation.RELATIVE_TO_SELF, 
                                                            0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		rotate.setDuration(durationMillis);
		rotate.setFillAfter(true);
		return rotate;
	}

	// 图标的动画(收入动画)
	public static void startAnimationsIn(ViewGroup viewgroup, int durationMillis) {
		for (int i = 0; i < viewgroup.getChildCount(); i++) {
			ImageButton inoutimagebutton = (ImageButton) viewgroup.getChildAt(i);
			inoutimagebutton.setVisibility(0);inoutimagebutton.setClickable(true);inoutimagebutton.setFocusable(true);
			MarginLayoutParams mlp = (MarginLayoutParams) inoutimagebutton.getLayoutParams();
			Animation animation = new TranslateAnimation(mlp.rightMargin - xOffset, 0F, yOffset + mlp.bottomMargin, 0F);

			animation.setFillAfter(true);
			animation.setDuration(durationMillis);
			animation.setStartOffset((i * 100) / (-1 + viewgroup.getChildCount()));// 下一个动画的偏移时间
			animation.setInterpolator(new OvershootInterpolator(2F));// 动画的效果 弹出再回来的效果
			inoutimagebutton.startAnimation(animation);

		}
	}

	// 图标的动画(弹出动画)
	public static void startAnimationsOut(ViewGroup viewgroup, int durationMillis) {
		for (int i = 0; i < viewgroup.getChildCount(); i++) {
			final ImageButton inoutimagebutton = (ImageButton) viewgroup.getChildAt(i);
			MarginLayoutParams mlp = (MarginLayoutParams) inoutimagebutton.getLayoutParams();
			Animation animation = new TranslateAnimation(0F, mlp.rightMargin - xOffset, 0F, yOffset + mlp.bottomMargin);

			animation.setFillAfter(true);
			animation.setDuration(durationMillis);
			animation.setStartOffset(((viewgroup.getChildCount() - i) * 100) / (-1 + viewgroup.getChildCount()));// 下一个动画的偏移时间
			animation.setAnimationListener(new Animation.AnimationListener() {
				@Override
				public void onAnimationStart(Animation arg0) {
				}

				@Override
				public void onAnimationRepeat(Animation arg0) {
				}

				@Override
				public void onAnimationEnd(Animation arg0) {
					inoutimagebutton.setVisibility(8);inoutimagebutton.setClickable(false);inoutimagebutton.setFocusable(false);
				}
			});
			inoutimagebutton.startAnimation(animation);
		}
	}

	// icon缩小消失的动画
	public static Animation getMiniAnimation(int durationMillis) {
		Animation miniAnimation = new ScaleAnimation(1.0f, 0f, 1.0f, 0f, Animation.RELATIVE_TO_SELF, 
                                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		miniAnimation.setDuration(durationMillis);
		miniAnimation.setFillAfter(true);
		return miniAnimation;
	}

	// icon放大渐变消失的动画
	public static Animation getMaxAnimation(int durationMillis) {
		AnimationSet animationset = new AnimationSet(true);

		Animation maxAnimation = new ScaleAnimation(1.0f, 4.0f, 1.0f, 4.0f, Animation.RELATIVE_TO_SELF, 
                                                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		Animation alphaAnimation = new AlphaAnimation(1, 0);

		animationset.addAnimation(maxAnimation);
		animationset.addAnimation(alphaAnimation);

		animationset.setDuration(durationMillis);
		animationset.setFillAfter(true);
		return animationset;
	}

}
