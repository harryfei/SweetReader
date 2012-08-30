package com.fxbandroid.bookreader.util; 

import java.io.Serializable;

public class Book implements Serializable 
{  
       
    public String name;  
    public String type; 
    public String Time;
    public String path; 
    public int readPosition;
    public String readPercent;  

    public Book() { 
        name ="";
        readPercent = "0%";
        Time = "";
        type = "";
        path ="";
        readPosition = 0;
    }
    public Book(String name,String type,String lastReadTime,
                String location,int readPosition,String finishRate) {  
        this.name = name;  
        this.readPercent = finishRate;  
        this.Time = lastReadTime;  
        this.type = type; 
        this.path = location;
        this.readPosition = readPosition;
    }

}   
