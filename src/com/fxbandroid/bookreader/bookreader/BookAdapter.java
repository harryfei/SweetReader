package com.fxbandroid.bookreader.bookreader;

import android.content.Context;  
import android.view.LayoutInflater;  
import android.view.View;  
import android.view.ViewGroup;  
import android.widget.ArrayAdapter;  
import android.widget.TextView;
import com.fxbandroid.bookreader.util.Book;
import android.widget.ImageView;
import android.widget.LinearLayout;
  
public class BookAdapter extends ArrayAdapter<Book>{  
  
    LayoutInflater mLayoutInflater;  
    int resourceId;  
    Context mContext;

    boolean mode = false;

    public BookAdapter(Context context, int resourceId) 
    {  
        super(context, resourceId);  
          
        //获取LayoutInflater 服务,用来从预定义的xml布局创建view对象.  
        this.resourceId = resourceId;  
        mLayoutInflater = LayoutInflater.from(context);  
    }
      
    public BookAdapter(Context context, int resourceId, Book[] objects) {  
        super(context, resourceId, objects);  
          
        //获取LayoutInflater 服务,用来从预定义的xml布局创建view对象.  
        this.resourceId = resourceId;  
        mLayoutInflater = LayoutInflater.from(context);  
    }

    public void setDeleteMode(boolean DeleteMode) {
        mode = DeleteMode;
    }

    public boolean isDeleteMode(){
        return mode;
    }
  
    @Override  
    public View getView(int position, View convertView, ViewGroup parent)
    {  
        if(convertView == null){  
            //创建新的view视图.  
            convertView = mLayoutInflater.inflate(resourceId, null);  
        }  
        //获取当前要显示的数据  
        final Book book = getItem(position);  
  
        TextView name = (TextView) convertView.findViewById(R.id.name);  
        TextView finishRate = (TextView) convertView.findViewById(R.id.finish_rate);  
        TextView lastReadTime = (TextView) convertView.findViewById(R.id.last_read_time);  
        TextView type = (TextView) convertView.findViewById(R.id.type);  

        ImageView delete_im = (ImageView)convertView.findViewById(R.id.delete_button);
        delete_im.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                remove(book);
            }
        }); 

        if(mode == false){
            delete_im.setVisibility(View.GONE);
        }else if(mode == true){
            delete_im.setVisibility(View.VISIBLE);
        } 
        name.setText(book.name);  
        finishRate.setText(book.readPercent);  
        lastReadTime.setText(book.Time);
        type.setText(book.type);
       // delete_im.setImageResource(R.drawable.book_delete1);
       
        return convertView;
    }  
      
}  
