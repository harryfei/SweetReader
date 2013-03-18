package com.fxbandroid.bookreader.bookreader;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.fxbandroid.bookreader.util.Book;
import java.util.ArrayList;


object BooksDB{
    private val DATABASE_NAME = "com.fxbandroid.BookReader_books.db";
    private val DATABASE_VERSION = 1;
    private val TABLE_NAME = "books_table";
    val BOOK_ID = "book_id";
    val BOOK_NAME = "book_name";
    val BOOK_TYPE = "book_type";
    val BOOK_LOCATION = "book_location";
    val BOOK_READPOSITION = "boo_readposition";
    val BOOK_LASTREADTIME = "book_lastreadtime";
    val BOOK_FINISHED = "book_finished";
}

import BooksDB._
class BooksDB(context: Context) extends SQLiteOpenHelper(context,DATABASE_NAME,null,DATABASE_VERSION)
{
    private var theDB:SQLiteDatabase = null;
    theDB = this.getReadableDatabase();

    override def onCreate(db:SQLiteDatabase) {
      
          val sql:String = ("CREATE TABLE " + TABLE_NAME + " ("
                +BOOK_ID + " INTEGER primary key autoincrement, "
                +BOOK_NAME + " text, "
                +BOOK_TYPE +" text, "
                +BOOK_LASTREADTIME+" text, "
                +BOOK_LOCATION + " text, "
                +BOOK_READPOSITION + " INTEGER, "
                +BOOK_FINISHED+" REAL"
                +");")
          db.execSQL(sql);
    }
    override def onUpgrade(db:SQLiteDatabase ,oldVersion:Int,newVersion:Int) {
        val sql = "DROP TABLE IF EXISTS " + TABLE_NAME + ";"
        db.execSQL(sql)
        onCreate(db)
    }

    def select():Cursor = {
         theDB.query(TABLE_NAME, null, null, null, null, null, null);
    }

    private def insert(book:Book):Long = {
        /* ContentValues */
        var cv:ContentValues  = new ContentValues()
        cv.put(BOOK_NAME, book.name);
//        cv.put(BOOK_TYPE,book.type);
        cv.put(BOOK_LASTREADTIME,book.Time);
        cv.put(BOOK_LOCATION,book.path);
//        cv.put(BOOK_READPOSITION,book.readPosition);
        cv.put(BOOK_FINISHED, book.readPercent);
        val row:Long = theDB.insert(TABLE_NAME, null, cv);
        row
    }
    private def delete(location:String) {

        val where = BOOK_LOCATION + " = ?";
        val whereValue:Array[String] = Array(location);
        theDB.delete(TABLE_NAME, where, whereValue);
    }

    private def update(location:String , book:Book) {
        val where = BOOK_LOCATION + " = ?";
        val whereValue:Array[String] = Array( location );

        val cv = new ContentValues();
        cv.put(BOOK_NAME, book.name);
//        cv.put(BOOK_TYPE,book.type);
        cv.put(BOOK_LASTREADTIME,book.Time);
        cv.put(BOOK_LOCATION,book.path);
//        cv.put(BOOK_READPOSITION,book.readPosition);
        cv.put(BOOK_FINISHED, book.readPercent);
        theDB.update(TABLE_NAME, cv, where, whereValue);
    }


    def clear() {
        var sql = "DROP TABLE IF EXISTS " + TABLE_NAME +";";
        theDB.execSQL(sql);
        onCreate(theDB);
    }

    def inputList( book_list:ArrayList[Book]) {
        clear();
        var p = 0;
        var size = book_list.size();
        while(p < size)
        {
            var book = book_list.get(p);
            insert(book);
            p = p+1;
        }

    }
    def outputList( ):ArrayList[Book] = {
    	var book_list:ArrayList[Book] = new ArrayList[Book]
        var cursor = this.select();
        var p = 0;
        while(p < cursor.getCount())
        {
            cursor.moveToPosition(p);

            var book = new Book() ;
            book.name = cursor.getString(1);
            //book.type = cursor.getString(2);
            book.Time = cursor.getString(3);
            book.path = cursor.getString(4);
            book.readPosition = cursor.getInt(5);
            book.readPercent = cursor.getString(6);

            book_list.add(book);
            p = p+1;
        }
        book_list
    }
}
