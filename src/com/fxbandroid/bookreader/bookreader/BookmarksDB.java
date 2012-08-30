package com.fxbandroid.bookreader.bookreader;

import android.content.ContentValues;  
import android.content.Context;  
import android.database.Cursor;  
import android.database.sqlite.SQLiteDatabase;  
import android.database.sqlite.SQLiteOpenHelper;

import com.fxbandroid.bookreader.util.Bookmark;
import java.util.ArrayList;



public class BookmarksDB extends SQLiteOpenHelper 
{  
    private final static String DATABASE_NAME = "com.fxbandroid.BookReader_books.db";  
    private final static int DATABASE_VERSION = 1;
    private final static String TABLE_NAME = "bookmarks_table";

    public final static String BOOKMARK_ID = "bookmark_id"; 
    public final static String BOOKMARK_POSITION = "bookmark_position";  
    public final static String BOOKMARK_TEXT = "bookmark_text";
    public final static String BOOKMARK_PATH = "bookmark_path";
    //public final static String BOOKMARK_READPOSITION = "bookmark_readposition";
    public final static String BOOKMARK_CREATETIME = "bookmark_createtime"; 
    //public final static String BOOKMARK_FINISHED = "bookmark_finished";

    private SQLiteDatabase theDB; 
    
    public BookmarksDB(Context context) {  
        // TODO Auto-generated constructor stub  
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        theDB = this.getReadableDatabase();  
    }  
        //创建table  
    @Override  
    public void onCreate(SQLiteDatabase db) {  
          String sql = "CREATE TABLE " + TABLE_NAME + " ("
                +BOOKMARK_ID + " INTEGER primary key autoincrement, " 
                +BOOKMARK_PATH + " text, "
                +BOOKMARK_TEXT +" text, "
                +BOOKMARK_CREATETIME+" text, " 
                //+BOOKMARK_LOCATION + " text, "
                +BOOKMARK_POSITION + " INTEGER, "
                //+BOOKMARK_FINISHED+" REAL" 
                +");";  
          db.execSQL(sql);
    }  
    @Override  
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
    {  
        String sql = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";  
        db.execSQL(sql);  
        onCreate(db);  
    }  
      
    public Cursor select() 
    {    
        Cursor cursor = theDB  
                .query(TABLE_NAME, null, null, null, null, null, null);  
        return cursor;
    } 

     //增加操作  
     private long insert(Bookmark bm)  
      {  
        /* ContentValues */  
        ContentValues cv = new ContentValues();  
        cv.put(BOOKMARK_POSITION, bm.offset);  
        cv.put(BOOKMARK_PATH,bm.path); 
        cv.put(BOOKMARK_TEXT,bm.textSnapshot);
        cv.put(BOOKMARK_CREATETIME,bm.createTime);
        //cv.put(BOOKMARK_READPOSITION,book.readPosition);
        //cv.put(BOOKMARK_FINISHED, book.readPercent);         
        long row = theDB.insert(TABLE_NAME, null, cv);  
        return row;  
      }  
     //删除操作  
    private void delete(String location)  {  

        String where = BOOKMARK_PATH + " = ?";  
        String[] whereValue ={ location };  
        theDB.delete(TABLE_NAME, where, whereValue);  
    }  
      //修改操作  
    private void update(String location, Bookmark bm)  {  
        String where = BOOKMARK_PATH + " = ?";  
        String[] whereValue = { location };  
        
        ContentValues cv = new ContentValues();  
        cv.put(BOOKMARK_POSITION, bm.offset);  
        cv.put(BOOKMARK_PATH,bm.path); 
        cv.put(BOOKMARK_TEXT,bm.textSnapshot);
        cv.put(BOOKMARK_CREATETIME,bm.createTime);
        theDB.update(TABLE_NAME, cv, where, whereValue);
    }


    public void clear()
    {
        String sql = "DROP TABLE IF EXISTS " + TABLE_NAME +";";  
        theDB.execSQL(sql);  
        onCreate(theDB); 
    }

    public void inputList(ArrayList<Bookmark> bm_list) {
        
        clear();
        int p = 0;
        int size = bm_list.size();
        while(p < size) {
            Bookmark bm = bm_list.get(p);
            
            insert(bm);

            p++;
        }
        
    }
    public ArrayList<Bookmark> outputList(Book book)
    {

        Cursor cursor = this.select();

        int p = 0;
        
        while(p < cursor.getCount())
        {
            cursor.moveToPosition(p);
            
            Bookmark bm = new Bookmark() ;
            bm.path = cursor.getString(1);
            bm.path = cursor.getString(2); 
            bm.textSnapshot = cursor.getString(3);
            //book. = cursor.getString(4);
            bm.offset = cursor.getInt(4); 
            //book.readPercent = cursor.getString(6);
           
            bm_list.add(bm);
            p++;
        }
        return p;
    }
} 
