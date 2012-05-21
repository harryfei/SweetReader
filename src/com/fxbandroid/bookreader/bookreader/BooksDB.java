package com.fxbandroid.bookreader.bookreader;

import android.content.ContentValues;  
import android.content.Context;  
import android.database.Cursor;  
import android.database.sqlite.SQLiteDatabase;  
import android.database.sqlite.SQLiteOpenHelper;

import com.fxbandroid.bookreader.util.Book;
import java.util.ArrayList;



public class BooksDB extends SQLiteOpenHelper 
{  
    private final static String DATABASE_NAME = "com.fxbandroid.BookReader_books.db";  
    private final static int DATABASE_VERSION = 1;  
    private final static String TABLE_NAME = "books_table";

    public final static String BOOK_ID = "book_id"; 
    public final static String BOOK_NAME = "book_name";  
    public final static String BOOK_TYPE = "book_type";
    public final static String BOOK_LOCATION = "book_location";
    public final static String BOOK_READPOSITION = "boo_readposition";
    public final static String BOOK_LASTREADTIME = "book_lastreadtime"; 
    public final static String BOOK_FINISHED = "book_finished";

    private SQLiteDatabase theDB; 
    
    public BooksDB(Context context) 
    {  
        // TODO Auto-generated constructor stub  
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        theDB = this.getReadableDatabase();  
    
    }  
        //创建table  
    @Override  
    public void onCreate(SQLiteDatabase db) 
    {  
          String sql = "CREATE TABLE " + TABLE_NAME + " ("
                +BOOK_ID + " INTEGER primary key autoincrement, " 
                +BOOK_NAME + " text, "
                +BOOK_TYPE +" text, "
                +BOOK_LASTREADTIME+" text, " 
                +BOOK_LOCATION + " text, "
                +BOOK_READPOSITION + " INTEGER, "
                +BOOK_FINISHED+" REAL" 
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
     private long insert(Book book)  
      {  
        /* ContentValues */  
        ContentValues cv = new ContentValues();  
        cv.put(BOOK_NAME, book.name);  
        cv.put(BOOK_TYPE,book.type); 
        cv.put(BOOK_LASTREADTIME,book.Time);
        cv.put(BOOK_LOCATION,book.path);
        cv.put(BOOK_READPOSITION,book.readPosition);
        cv.put(BOOK_FINISHED, book.readPercent);         
        long row = theDB.insert(TABLE_NAME, null, cv);  
        return row;  
      }  
     //删除操作  
    private void delete(String location)  
    {  

        String where = BOOK_LOCATION + " = ?";  
        String[] whereValue ={ location };  
        theDB.delete(TABLE_NAME, where, whereValue);  
    }  
      //修改操作  
    private void update(String location, Book book)  
    {  
        String where = BOOK_LOCATION + " = ?";  
        String[] whereValue = { location };  
        
        ContentValues cv = new ContentValues();  
        cv.put(BOOK_NAME, book.name);  
        cv.put(BOOK_TYPE,book.type); 
        cv.put(BOOK_LASTREADTIME,book.Time);
        cv.put(BOOK_LOCATION,book.path);
        cv.put(BOOK_READPOSITION,book.readPosition);
        cv.put(BOOK_FINISHED, book.readPercent); 
        theDB.update(TABLE_NAME, cv, where, whereValue);
    }


    public void clear()
    {
        String sql = "DROP TABLE IF EXISTS " + TABLE_NAME +";";  
        theDB.execSQL(sql);  
        onCreate(theDB); 
    }

    public void inputList(ArrayList<Book> book_list)
    {
        
        clear();
        int p = 0;
        int size = book_list.size();
        while(p < size)
        {
            Book book = book_list.get(p);
            
            insert(book);

            p++;
        }
        
    }
    public  void outputList(ArrayList<Book> book_list)
    {

        Cursor cursor = this.select();

        int p = 0;
        
        while(p < cursor.getCount())
        {
            cursor.moveToPosition(p);
            
            Book book = new Book() ;
            book.name = cursor.getString(1);
            book.type = cursor.getString(2); 
            book.Time = cursor.getString(3);
            book.path = cursor.getString(4);
            book.readPosition = cursor.getInt(5); 
            book.readPercent = cursor.getString(6);
           
            book_list.add(book);
            p++;
        }
    }
} 
