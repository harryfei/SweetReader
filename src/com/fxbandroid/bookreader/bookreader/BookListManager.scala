package com.fxbandroid.bookreader.bookreader;

import com.fxbandroid.bookreader.util.Book;
import com.fxbandroid.bookreader.util.ExFile;
import java.util.Observable;
import java.lang.Comparable;
import java.io.File;
import java.util.Collections;
import java.util.ArrayList;
import android.content.Context;
import java.util.Comparator;
import java.util.Date;
import java.text.SimpleDateFormat;

object BookListManager{
    private var instance:BookListManager = null
    def getInstance(context:Context) :BookListManager = {
		if(instance == null){
			instance = new BookListManager(context);
		}
		instance
	}
}

class BookListManager(context:Context) extends Observable() {
	private val b_db = new BooksDB(context)
    protected val books = b_db.outputList

    def getBooks():ArrayList[Book] = books
    
    def sortByTime(){
    	class BookTimeComparaor extends Comparator[Book]{
    		override def compare(b1:Book, b2:Book):Int = -(b1.Time.compareTo(b2.Time))
    	}
        Collections.sort(books, new BookTimeComparaor());
        setChanged();
        notifyObservers();
    }

    def addBook(file:File):Int = {
        val path = file.getPath();
        val count = books.size();
        var index:Int = -1;
        var i = -1
        for(i <- 0 to count){
            if(books.get(i).path.equals(path)){
            	index = i
            }
        }
        if(index != -1){
          return index
        }
        val format = new SimpleDateFormat("MM-dd HH:mm");

        var book = new Book();
        book.path = path;
        book.Time = format.format(new Date());
        book.book_type = ExFile.getExtension(file);
        book.name = ExFile.getShortName(file);
        books.add(0,book);
        setChanged();
        notifyObservers();
        return 0;
    }

    def removeBook(index:Int){
        books.remove(index);
        setChanged();
        notifyObservers();
    }

    def clearBook(){
        books.clear();
        setChanged();
        notifyObservers();
    }

    def updataBook(position:Int,book:Book){
        books.remove(position);
        books.add(0,book);
        setChanged();
        notifyObservers();
    }

    def restoreDatabase(){
        b_db.inputList(books);
    }

}
