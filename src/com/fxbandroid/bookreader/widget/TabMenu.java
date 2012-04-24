package com.fxbandroid.bookreader.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;

public class TabMenu extends PopupWindow{
	private GridView body,title;
	private LinearLayout layout;
	private MenuTitleAdapter titleAdapter;

	public TabMenu(Context context,OnItemClickListener titleClick,OnItemClickListener bodyClick,
			MenuTitleAdapter titleAdapter,int colorBgTabMenu,int aniTabMenu){
		super(context);
		
		layout = new LinearLayout(context);
		layout.setOrientation(LinearLayout.VERTICAL);
		//标题选项栏
		title = new GridView(context);
        title.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT)); 
        title.setNumColumns(titleAdapter.getCount());
		title.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
		title.setVerticalSpacing(1);
		title.setHorizontalSpacing(1);
		title.setGravity(Gravity.CENTER);
		title.setOnItemClickListener(titleClick);
		title.setAdapter(titleAdapter);
		title.setSelector(new ColorDrawable(Color.TRANSPARENT));//选中的时候为透明色
		this.titleAdapter=titleAdapter;
		//子选项栏
		body = new GridView(context);
		body.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
		body.setSelector(new ColorDrawable(Color.TRANSPARENT));//选中的时候为透明色
		body.setNumColumns(4);
		body.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
		body.setVerticalSpacing(10);
		body.setHorizontalSpacing(10);
		body.setPadding(10, 10, 10, 10);
		body.setGravity(Gravity.CENTER);
		body.setOnItemClickListener(bodyClick);

		layout.addView(title);
		layout.addView(body);
		
		//设置默认项
		this.setContentView(layout);
		this.setWidth(LayoutParams.FILL_PARENT);
		this.setHeight(LayoutParams.WRAP_CONTENT);
		this.setBackgroundDrawable(new ColorDrawable(colorBgTabMenu));// 设置TabMenu菜单背景
		this.setAnimationStyle(aniTabMenu);
		this.setFocusable(true);// menu菜单获得焦点 如果没有获得焦点menu菜单中的控件事件无法响应
	}
	
	
	public void SetTitleSelect(int index)
	{
		title.setSelection(index);
		this.titleAdapter.SetFocus(index);
	}
	
	public void SetBodySelect(int index,int colorSelBody)
	{
		int count=body.getChildCount();
		for(int i=0;i<count;i++)
		{
			if(i!=index)
				((LinearLayout)body.getChildAt(i)).setBackgroundColor(Color.TRANSPARENT);
		}
		((LinearLayout)body.getChildAt(index)).setBackgroundColor(colorSelBody);
	}
	
	public void SetBodyAdapter(MenuBodyAdapter bodyAdapter)
	{
		body.setAdapter(bodyAdapter);
	}
	
	/**
	 * 自定义Adapter，TabMenu的每个分页的主体
	 * 
	 */
	static public class MenuBodyAdapter extends BaseAdapter {
		private Context context;
		private int fontColor,fontSize;
		private String[] texts;
		private int[] resID;
		/**
		 * 设置TabMenu的分页主体
		 * @param context 调用方的上下文
		 * @param texts 按钮集合的字符串数组
		 * @param resID 按钮集合的图标资源数组
		 * @param fontSize 按钮字体大小
		 * @param color 按钮字体颜色
		 */
		public MenuBodyAdapter(Context context, String[] texts,int[] resID, int fontSize,int fontColor) 
		{
			this.context = context;
			this.fontColor = fontColor;
			this.texts = texts;
			this.fontSize=fontSize;
			this.resID=resID;
		}
		public int getCount() {
			return texts.length;
		}
		public Object getItem(int position) {
			
			return makeMenyBody(position);
		}
		public long getItemId(int position) {
			return position;
		}
		
		private LinearLayout makeMenyBody(int position)
		{
			LinearLayout result=new LinearLayout(this.context);
			result.setOrientation(LinearLayout.VERTICAL);
			result.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);	
			result.setPadding(10, 10, 10, 10);
			
			TextView text = new TextView(this.context);
			text.setText(texts[position]);
			text.setTextSize(fontSize);
			text.setTextColor(fontColor);
			text.setGravity(Gravity.CENTER);
			text.setPadding(5, 5, 5, 5);
			ImageView img=new ImageView(this.context);
			img.setBackgroundResource(resID[position]);
			result.addView(img,new LinearLayout.LayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT)));
			result.addView(text);
			return result;
		}
		
		public View getView(int position, View convertView, ViewGroup parent) {
			return makeMenyBody(position);
		}
	}
	
	
	/**
	 * 自定义Adapter,TabMenu的分页标签部分
	 * 
	 */
	static public class MenuTitleAdapter extends BaseAdapter {
		private Context context;
        private int fontColor,unselcolor,selcolor;
        private TextView[] title;
        /**
         * 设置TabMenu的title
         * @param context 调用方的上下文
         * @param titles 分页标签的字符串数组
         * @param fontSize 字体大小
         * @param fontcolor 字体颜色
         * @param unselcolor 未选中项的背景色
         * @param selcolor 选中项的背景色
         */
        public MenuTitleAdapter(Context context, String[] titles, int fontSize,
                int fontcolor,int unselcolor,int selcolor) {
            this.context = context;
            this.fontColor = fontcolor;
            this.unselcolor = unselcolor;
            this.selcolor=selcolor;
            this.title = new TextView[titles.length];
            for (int i = 0; i < titles.length; i++) {
                title[i] = new TextView(context);
                title[i].setText(titles[i]);
                title[i].setTextSize(fontSize);
                title[i].setTextColor(fontColor);
                title[i].setGravity(Gravity.CENTER);
                title[i].setPadding(10, 10, 10, 10);
            }
        }
        public int getCount() {
            return title.length;
        }
        public Object getItem(int position) {
            return title[position];
        }
        public long getItemId(int position) {
            return title[position].getId();
        }
        /**
         * 设置选中的效果
         */
        private void SetFocus(int index)
        {
        	for(int i=0;i<title.length;i++)
        	{
        		if(i!=index)
        		{
        			title[i].setBackgroundDrawable(new ColorDrawable(unselcolor));//设置没选中的颜色
                    title[i].setTextColor(fontColor);//设置没选中项的字体颜色
        		}
        	}
        	title[index].setBackgroundColor(0x00);//设置选中项的颜色
            title[index].setTextColor(selcolor);//设置选中项的字体颜色
        }
        
        public View getView(int position, View convertView, ViewGroup parent) {
            View v;
            if (convertView == null) {
                v = title[position];
            } else {
                v = convertView;
            }
            return v;
        }
	}
} 
