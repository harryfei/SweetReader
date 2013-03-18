package com.fxbandroid.bookreader.util;

import java.text.SimpleDateFormat;
import java.util.Date;
public class Bookmark
{
    public int offset;
    public String createTime;
    public String textSnapshot;
    public String path;

    public Bookmark()
    {
        offset = 0;
        textSnapshot = "";
        SimpleDateFormat format=new SimpleDateFormat( "MM-dd HH:mm");
        createTime=format.format((new Date())); 
    }
    public Bookmark(int offset,String textSnapshot,Book book)
    {
        this.offset = offset;
        this.textSnapshot = textSnapshot;
        SimpleDateFormat format=new SimpleDateFormat( "MM-dd HH:mm");
        createTime=format.format((new Date())); 
        path = book.path;
    }

}

