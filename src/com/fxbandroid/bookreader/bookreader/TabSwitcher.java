package com.fxbandroid.bookreader.bookreader; 

import android.content.Context;
//import android.support.v4.view.PagerAdapter;
//import android.support.v4.view.ViewPager;
//import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet; 
import android.view.View;
import android.view.ViewGroup; 
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.LayoutInflater;

public class TabSwitcher extends TextView
//implements ViewPager.OnPageChangeListener 
{ 

    private int selectedTabIndex;

    private TextView tv0;
    private TextView tv1;

//    public TabSwitcher(Context context) {
 //       this(context, null); 
  //    }

    public TabSwitcher(Context context, AttributeSet attrs) {
        super(context, attrs);
       // LayoutInflater.from(context).inflate(R.layout.tab, this, true);
        //tv0 = (TextView)findViewById(R.id.shujia);   
        //tv1 = (TextView)findViewById(R.id.wenjian);  
    }


    private void initTab()
    { 

    }

    public void setPager(int a) {
    }
}
