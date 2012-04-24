package com.fxbandroid.bookreader.util;

import java.io.File;   
import java.io.FileInputStream;  
import java.io.FileNotFoundException;  
import java.io.FileOutputStream; 
import java.io.IOException;
import org.apache.http.util.EncodingUtils;
public class TextFile 
{
    private byte[] fileData; 

    public TextFile(String filename)
    {
        fileData = openFile(filename);
    }
    public String getString(String code_type)
    {
        String r_data = EncodingUtils.getString(fileData, code_type);
        return r_data;
    }
    private byte[] openFile(String file_name)
    {
		try 
        {
			File file = new File(file_name);
			FileInputStream in = new FileInputStream(file);
			int length = (int)file.length();
			byte[] temp = new byte[length];
			in.read(temp, 0, length);
            in.close();
			return temp;
		} 
        catch (IOException e) 
        {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//Log.d(TAG, e.toString());
			return null;
		}
	}

}

