package com.fxbandroid.bookreader.bookreader;

import android.content.Context;  
import android.view.LayoutInflater;  
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.fxbandroid.bookreader.util.ExFile;
import java.io.File;




public class FileAdapter extends ArrayAdapter<File>
{ // 实现列表内容适配器
    private LayoutInflater mLayoutInflater;
    private int resourceId;
    public FileAdapter(Context context, File[] objects) {  
        super(context, R.layout.filelistitem, objects);  
        //获取LayoutInflater 服务,用来从预定义的xml布局创建view对象.  
        this.resourceId = resourceId;  
        mLayoutInflater = LayoutInflater.from(context);  
    }     // 设置每个列表项的显示
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){  
            //创建新的view视图.  
            convertView = mLayoutInflater.inflate(R.layout.filelistitem, null);  
        }  
        
        
        File path = getItem(position);

        ImageView img = (ImageView) convertView.findViewById(R.id.file_img);
        TextView title = (TextView) convertView.findViewById(R.id.file_title);
        TextView info = (TextView) convertView.findViewById(R.id.file_info);

               // 根据位置position设置具体内容
        if(path.isDirectory()){
            img.setImageResource(R.drawable.folder); 
        }
        else if(path.isFile()) {
            img.setImageResource(R.drawable.file);
        }
        
        title.setText(path.getName());

        return convertView;
    }

    
}
