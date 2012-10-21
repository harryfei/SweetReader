package com.fxbandroid.bookreader.bookreader;

import android.support.v4.app.Fragment;
import android.widget.ListView;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.os.Bundle;
import java.io.File;
import android.widget.AdapterView;
import com.fxbandroid.bookreader.util.ExFile;
import android.os.Environment;
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
            //openBook(BookListManager.getInstance(getActivity()).addBook(file));
        }
    }

    private void listDirectory(File file){
            File[] file_list = file.listFiles();
            file_list = ExFile.sortFiles(file_list);
            FileAdapter files = new FileAdapter(getActivity(),file_list);
            fileList.setAdapter(files);
            currentDir = file.getPath();
    }

    private void goParentDirectory() {
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

}
