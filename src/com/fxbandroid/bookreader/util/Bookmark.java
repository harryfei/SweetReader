package com.fxbandroid.bookreader.util;

import java.text.SimpleDateFormat;
import java.util.Date;
class Bookmark
{
    private int offset;
    private String createTime;
    private String textSnapshot;


    public Bookmark()
    {
        offset = 0;
        textSnapshot = "";
        SimpleDateFormat format=new SimpleDateFormat( "MM-dd HH:mm");
        createTime=format.format((new Date()));
        
    }
    public Bookmark(int offset,String textSnapshot)
    {
        this.offset = offset;
        this.textSnapshot = textSnapshot;
        SimpleDateFormat format=new SimpleDateFormat( "MM-dd HH:mm");
        createTime=format.format((new Date()));
 
    }

}

