package com.fxbandroid.bookreader.bookreader;

import android.support.v4.app.Fragment
import android.view._
import android.os.Bundle
import android.view.View
import java.util.Observer
import java.lang.Object
import java.util.Observable
import android.content._
import android.widget._
import com.fxbandroid.bookreader.util.Book
import java.util.ArrayList;

class BookListManagerFragment extends Fragment{
	private var  booksList: ListView = null
    private var bookAdapter: BookAdapter = null

    override def onCreateView(
    		 inflater: LayoutInflater,
    		 container: ViewGroup,
             savedInstanceState: Bundle): View =  {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.bookmanager, container, false);
        booksList = view.findViewById(R.id.book_list).asInstanceOf[ListView];
        setBookView();
        view;
    }

    private def setBookView(){
        var booksmanager = BookListManager.getInstance(getActivity());
        bookAdapter = new BookAdapter(getActivity(),R.layout.booklistitem,
              booksmanager.getBooks())

        booksmanager.addObserver(new Observer(){
            override def update( observable: Observable,  data: Object) =  bookAdapter.notifyDataSetChanged()
        })

        booksList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            override def onItemClick(arg0:AdapterView[_],arg1:View, arg2:Int,arg3: Long) = openBook(arg2)
        })

        booksList.setAdapter(bookAdapter)

    }
    def openBook(position:Int) {
        var intent = new Intent();
        intent.setAction("book_open");
        intent.putExtra("book_position",position);
        getActivity().startActivity(intent);
    }

    private def changeBookListMode(isDelete: Boolean){
        bookAdapter.setDeleteMode(isDelete);
    }
    def toggleEditMode() = {
        if (bookAdapter.getDeleteMode()){
            bookAdapter.setDeleteMode(false);
        }else{
            bookAdapter.setDeleteMode(true);
        }
        bookAdapter.notifyDataSetChanged();
        bookAdapter.getDeleteMode();
    }
    private class BookAdapter(context: Context, resourceId:Int, books:ArrayList[Book])
                        extends ArrayAdapter[Book](context, resourceId, books){

        val mLayoutInflater: LayoutInflater = LayoutInflater.from(context)

        var deleteMode:Boolean = false;

        def setDeleteMode(deleteMode: Boolean) { this.deleteMode = deleteMode; }

        def getDeleteMode() = deleteMode

        override def getView(position: Int, convertView:View, parent:ViewGroup) = {
            if(convertView == null){
                var convertView = mLayoutInflater.inflate(resourceId, null);
            }

            val book = getItem(position);
            val name =  convertView.findViewById(R.id.name).asInstanceOf[TextView];
            val finishRate = convertView.findViewById(R.id.finish_rate).asInstanceOf[TextView];
            val lastReadTime =  convertView.findViewById(R.id.last_read_time).asInstanceOf[TextView];
//            val file_type =  convertView.findViewById(R.id.typioue).asInstanceOf[TextView];
            val delete_im = convertView.findViewById(R.id.delete_button).asInstanceOf[ImageView];

            delete_im.setOnClickListener(new View.OnClickListener(){
                override def onClick(v: View)= BookListManager.getInstance(context).removeBook(position)
            })

            if(!deleteMode){
                delete_im.setVisibility(View.GONE);
            }else{
                delete_im.setVisibility(View.VISIBLE);
            }

            name.setText(book.name);
            finishRate.setText(book.readPercent);
            lastReadTime.setText(book.Time);
//            type.setText(book.type);
            convertView;
        }
    }
}
