import android.support.v4.app.Fragment;
package com.fxbandroid.bookreader.bookreader;


class BookListManagerFragment extends Fragment{


    private ListView booksList;
    private BookAdapter bookAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view1 = mLi.inflate(R.layout.bookmanager, null);
        booksList = (ListView)view1.findViewById(R.id.book_list);
        setBookView();
        return inflater.inflate(R.layout.bookmanager, container, false);
    }

    private void setBookView() {

        final BookListManager booksmanager = BookListManager.getInstance(this);
        //final Context context = this;
        bookAdapter = new BookAdapter(this,R.layout.booklistitem,
                                    booksmanager.getBooks());
        booksmanager.addObserver(new Observer(){
			@Override
			public void update(Observable observable, Object data) {
                //refreshBookList();
				bookAdapter.notifyDataSetChanged();
            }

        });


        booksList.setAdapter(bookAdapter);
        booksList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                    long arg3) {
                        openBook(arg2);
            }

        });

    }
    public void openBook(int position) {

        Intent intent = new Intent(this,BookViewer.class);
        intent.setAction("book_open");
        intent.putExtra("book_position",position);
        getActivity().startActivity(intent);
    }

    private void changeBookListMode(boolean isDelete){
        bookAdapter.setDeleteMode(isDelete);
        booksList.setAdapter(bookAdapter);
    }

}
