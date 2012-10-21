package com.fxbandroid.bookreader.bookreader;

import android.support.v4.app.Fragment;
import android.widget.ListView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.os.Bundle;
import android.view.View;
import java.util.Observer;
import java.lang.Object;
import android.widget.AdapterView;
import java.util.Observable;
import android.content.Intent;
import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.fxbandroid.bookreader.util.Book;
import android.widget.ImageView;
import java.util.ArrayList;

public class BookListManagerFragment extends Fragment{


    private ListView booksList;
    private BookAdapter bookAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.bookmanager, container, false);
        booksList = (ListView)view.findViewById(R.id.book_list);
        setBookView();
        return view;
    }

    private void setBookView() {

        final BookListManager booksmanager = BookListManager.getInstance(getActivity());
        bookAdapter = new BookAdapter(getActivity(),R.layout.booklistitem,
                booksmanager.getBooks());
        booksmanager.addObserver(new Observer(){
            @Override
            public void update(Observable observable, Object data) {
                bookAdapter.notifyDataSetChanged();
            }

        });

        booksList.setAdapter(bookAdapter);
        booksList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                openBook(arg2);
            }

        });

    }
    public void openBook(int position) {

        Intent intent = new Intent(getActivity(),BookViewer.class);
        intent.setAction("book_open");
        intent.putExtra("book_position",position);
        getActivity().startActivity(intent);
    }

    private void changeBookListMode(boolean isDelete){
        bookAdapter.setDeleteMode(isDelete);
    }
    public boolean toggleEditMode(){
        if (bookAdapter.isDeleteMode()){
            bookAdapter.setDeleteMode(false);
        }else{
            bookAdapter.setDeleteMode(true);
        }
        bookAdapter.notifyDataSetChanged();
        return bookAdapter.isDeleteMode();
    }
    private class BookAdapter extends ArrayAdapter<Book>{

        LayoutInflater mLayoutInflater;
        int resourceId;
        Context context;

        boolean mode = false;

        public BookAdapter(Context context, int resourceId) {
            super(context, resourceId);
            this.context = context;
            //获取LayoutInflater 服务,用来从预定义的xml布局创建view对象.
            this.resourceId = resourceId;
            mLayoutInflater = LayoutInflater.from(context);
        }

        public BookAdapter(Context context,int resourceId,ArrayList<Book> objects) {
            super(context, resourceId, objects);
            this.context = context;
            this.resourceId = resourceId;
            mLayoutInflater = LayoutInflater.from(context);
        }

        public void setDeleteMode(boolean DeleteMode) {
            mode = DeleteMode;
        }

        public boolean isDeleteMode(){
            return mode;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            if(convertView == null){
                convertView = mLayoutInflater.inflate(resourceId, null);
            }
            final int p = position;
            Book book = getItem(position);

            TextView name = (TextView) convertView.findViewById(R.id.name);
            TextView finishRate = (TextView) convertView.findViewById(R.id.finish_rate);
            TextView lastReadTime = (TextView) convertView.findViewById(R.id.last_read_time);
            TextView type = (TextView) convertView.findViewById(R.id.type);

            ImageView delete_im = (ImageView)convertView.findViewById(R.id.delete_button);
            delete_im.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    BookListManager.getInstance(context).removeBook(p);
                }
            });

            if(mode == false){
                delete_im.setVisibility(View.GONE);
            }else if(mode == true){
                delete_im.setVisibility(View.VISIBLE);
            }
            name.setText(book.name);
            finishRate.setText(book.readPercent);
            lastReadTime.setText(book.Time);
            type.setText(book.type);
            return convertView;
        }

    }

}
