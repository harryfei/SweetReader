package com.fxbandroid.bookreader.util;

import java.io.File; 
import java.util.ArrayList;
import java.util.Arrays;

public class ExFile extends File {

    public ExFile(String path) {
        super(path); 
    }

    public static String getExtension(File file) {
        String filename = file.getName();
        if ((filename != null) && (filename.length() > 0)) {
            int i = filename.lastIndexOf('.');

            if ((i >-1) && (i < (filename.length() - 1))) {
                return filename.substring(i + 1);
            }
        }
        return "";
    }

    public static String getShortName(File file){
        String filename = file.getName();
        if ((filename != null) && (filename.length() > 0)) {
            int i = filename.lastIndexOf('.');

            if ((i >-1) && (i < (filename.length() - 1))) {
                return filename.substring(0,i);
            }
        }
        return "";
    }

    public static File[] sortFiles(File[] files){
        class FileWrapper implements Comparable { 
          /** File */
            private File file;
   
            public FileWrapper(File file) {
                this.file = file;
            }
   
            //倒序排序
            public int compareTo(Object obj) {       
       
                FileWrapper castObj = (FileWrapper)obj;
               
                if (this.file.getName().compareTo(castObj.getFile().getName()) > 0) {
                    return 1;
                } else if (this.file.getName().compareTo(castObj.getFile().getName()) < 0) {
                    return -1;
                } else {
                    return 0;
                }
            }
   
            public File getFile() {
                return this.file;
            }

        }

        
         if(files != null && files.length > 0){
            FileWrapper [] fileWrappers = new FileWrapper[files.length];
            for (int i=0; i<files.length; i++) {
                fileWrappers[i] = new FileWrapper(files[i]);
             }
            Arrays.sort(fileWrappers); 
            ArrayList<File> list = new ArrayList<File>();
            //放入所有目录  
            for (FileWrapper f : fileWrappers)  {  
                if (f.getFile().isDirectory())  {  
                    list.add(f.getFile());  
                }  
            }  
            //放入所有文件  
            for (FileWrapper f : fileWrappers)  {  
                if (f.getFile().isFile())  {  
                    list.add(f.getFile());  
                }  
            }  
          
            return list.toArray(new File[files.length]);   
        } 

        return files;
    } 
     

}
