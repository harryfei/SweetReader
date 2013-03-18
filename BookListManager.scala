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

class BookListManager(contextc:Context) extends Observable {
	
    protected var books = new ArrayList[Book]()
    private var context: Context = contextc
    var b_db:BooksDB = new BooksDB(context)
    b_db.outputList(books)
    
    def getBooks() =  books

    private class BookTimeComparaor extends Comparator[Book]{
        override def compare(b1:Book,b2:Book): Int ={
            if(b1.Time.compareTo(b2.Time) > 0) {
                return -1;
            }
            else if(b1.Time.compareTo(b2.Time) <0){
                return 1;
            }
            else {
                return 0;
            }
        }
    }

    def sortByTime() = {
        Collections.sort(books, new BookTimeComparaor());
        setChanged();
        notifyObservers();
    }

    def addBook(file:File): Int ={
        val path = file.getPath();
        var count = books.size();
        var i = 0;
        for(i <- 0 to count) {
            if(books.get(i).path.equals(path)){
                return i;
            }
        }

        var format = new SimpleDateFormat("MM-dd HH:mm");
        var book = new Book();
        book.path = path;
        book.Time = format.format(new Date());
//        book.type = ExFile.getExtension(file);
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
        books.clear
        setChanged()
        notifyObservers()
    }

    def updataBook(position:Int,book: Book){
        books.remove(position);
        books.add(0,book);
        setChanged();
        notifyObservers();
    }

    def restoreDatabase(){
        var b_db = new BooksDB(context);
        b_db.inputList(books);
    }
}

object BookListMananger extends Observable {
	def getInstance(context: Context) = new BookListManager(context)
}
