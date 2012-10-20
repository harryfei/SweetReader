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

public class BookListManager extends Observable {

    private static BookListManager instance = null;
    protected ArrayList<Book> books = new ArrayList<Book>();
    private Context context = null;

    /**
	 * Get the instance of BookListManager
	 *
	 * @return Book List Manager
	 */

    public BookListManager(Context context){
        super();

        this.context = context;
        BooksDB b_db = new BooksDB(context);
        //books = new ArrayList<Book>();
        b_db.outputList(books);
    }
    public static BookListManager getInstance(Context context){
		if(instance == null){
			instance = new BookListManager(context);
		}
		return instance;
	}

    public ArrayList<Book> getBooks(){
        return books;
    }

    public void sortByTime(){

        Collections.sort(books, new BookTimeComparaor());
        setChanged();
        notifyObservers();
    }

    public int addBook(File file){
        final String path = file.getPath();


        int count = books.size();
        int index = -1;
        for(int i=0;i<count;i++){
            if(books.get(i).path.equals(path)){
                return i;
            }
        }

        SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");

        Book book = new Book();
        book.path = path;
        book.Time = format.format(new Date());
        book.type = ExFile.getExtension(file);
        book.name = ExFile.getShortName(file);

        books.add(0,book);

        setChanged();
        notifyObservers();
        return 0;
    }

    public void removeBook(int index){
        books.remove(index);

        setChanged();
        notifyObservers();
    }

    public void clearBook(){
        books.clear();

        setChanged();
        notifyObservers();
    }

    public void updataBook(int position,Book object){
        books.remove(position);
        books.add(0,object);

        setChanged();
        notifyObservers();
//        sortByTime();
    }

    public void restoreDatabase(){
        BooksDB b_db = new BooksDB(context);

        b_db.inputList(books);
    }

    private class BookTimeComparaor implements Comparator<Book>{
        @Override
        public int compare(Book b1,Book b2){
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

}
