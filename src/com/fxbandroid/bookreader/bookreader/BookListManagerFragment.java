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
        //BookAdapter.setDataOnChanged();
    }
    public boolean toggleListMode(boolean is){
        return true;
    }


}
