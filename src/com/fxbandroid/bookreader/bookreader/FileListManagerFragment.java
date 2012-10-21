package com.fxbandroid.bookreader.bookreader;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.fxbandroid.bookreader.util.ExFile;
import java.io.File;

public class FileListManagerFragment extends Fragment{
    private FileAdapter fileAdapter;
    private ListView fileList;
    private String currentDir = "/sdcard";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.filemanager, container, false);
        fileList = (ListView)view.findViewById(R.id.file_list);
        setFileView();
        return view;
    }
    private void setFileView() {

        listDirectory(new File(currentDir));
        fileList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                long arg3) {
                clickFile(arg2);
            }
        });

    }
    private void clickFile(int whichFile){
        File file = (File)(fileList.getAdapter().getItem(whichFile));

        if(file.isDirectory()){
            listDirectory(file);
        }
        else if(ExFile.getExtension(file).equals("txt")) {
            openBook(BookListManager.getInstance(getActivity()).addBook(file));
        }
    }

    private void listDirectory(File file){
        File[] file_list = file.listFiles();
        file_list = ExFile.sortFiles(file_list);
        FileAdapter files = new FileAdapter(getActivity(),file_list);
        fileList.setAdapter(files);
        currentDir = file.getPath();
    }

    public void goParentDirectory() {
        if(currentDir.equals("/")){
            return;
        }
        File file = new File(currentDir);
        listDirectory(new File(file.getParent()));
    }
    public String getSDPath() {
        File sdDir = null;
        if( Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED) )
        {
            sdDir = Environment.getExternalStorageDirectory();
        }
        return sdDir.toString();

    }
    public void openBook(int position) {

        Intent intent = new Intent(getActivity(),BookViewer.class);
        intent.setAction("book_open");
        intent.putExtra("book_position",position);
        getActivity().startActivity(intent);
    }
    private class FileAdapter extends ArrayAdapter<File>
    {
        private LayoutInflater mLayoutInflater;
        private int resourceId;
        public FileAdapter(Context context, File[] objects) {
            super(context, R.layout.filelistitem, objects);
            this.resourceId = resourceId;
            mLayoutInflater = LayoutInflater.from(context);
        }
        public View getView(int position, View convertView, ViewGroup parent) {

            if(convertView == null){
                convertView = mLayoutInflater.inflate(R.layout.filelistitem, null);
            }

            File path = getItem(position);

            ImageView img = (ImageView) convertView.findViewById(R.id.file_img);
            TextView title = (TextView) convertView.findViewById(R.id.file_title);
            TextView info = (TextView) convertView.findViewById(R.id.file_info);

            if(path.isDirectory()){
                img.setImageResource(R.drawable.folder);
            }
            else if(path.isFile()) {
                img.setImageResource(R.drawable.file);
            }

            title.setText(path.getName());

            return convertView;
        }


    }


}
