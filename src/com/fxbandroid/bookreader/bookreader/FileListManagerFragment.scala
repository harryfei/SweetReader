package com.fxbandroid.bookreader.bookreader;

import android.content._;
import android.content._;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget._;
import com.fxbandroid.bookreader.util.ExFile;
import java.io.File;

class FileListManagerFragment extends Fragment{
    private  var fileAdapter:FileAdapter = null;
    private var fileList:ListView = null;
    private var currentDir = "/sdcard";

    override def onCreateView(inflater:LayoutInflater , container:ViewGroup , savedInstanceState:Bundle ) = {
        var view:View = inflater.inflate(R.layout.filemanager, container, false);
        fileList = view.findViewById(R.id.file_list).asInstanceOf[ListView];
        setFileView();
        view;
    }
    private def setFileView() {
        listDirectory(new File(currentDir));
        import AdapterView.OnItemClickListener
        fileList.setOnItemClickListener(new OnItemClickListener() {
            override def onItemClick(arg0:AdapterView[_], arg1:View , arg2:Int,arg3: Long):Unit =  clickFile(arg2)
        })
    }

    private def clickFile(whichFile:Int){
        val file = (fileList.getAdapter().getItem(whichFile)).asInstanceOf[File];

        if(file.isDirectory()){
            listDirectory(file);
        }
        else if(ExFile.getExtension(file).equals("txt")) {
            openBook(BookListManager.getInstance(getActivity()).addBook(file));
        }
    }

    private def listDirectory(file:File){
        var file_list = file.listFiles();
        file_list = ExFile.sortFiles(file_list);
        var files = new FileAdapter(getActivity(),file_list);
        fileList.setAdapter(files);
        currentDir = file.getPath();
    }

    def goParentDirectory() {
        if(currentDir.equals("/")){
            return;
        }
        val file = new File(currentDir);
        listDirectory(new File(file.getParent()));
    }
    def getSDPath():String = {
        var sdDir:File = null;
        if(Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
            sdDir = Environment.getExternalStorageDirectory();
        }
        sdDir.toString();

    }
    def openBook(position:Int) {
        var intent = new Intent();
        intent.setAction("book_open");
        intent.putExtra("book_position",position);
        getActivity().startActivity(intent);
    }

    private class FileAdapter(context:Context, objects:Array[File]) extends
                        ArrayAdapter[File](context,R.layout.filelistitem,objects) {
        private var mLayoutInflater:LayoutInflater = LayoutInflater.from(context);
        private var resourceId:Int = 0;

        override def getView(position:Int, convertView:View, parent:ViewGroup):View = {
        	var thisView = convertView
            if(thisView == null){
                thisView = mLayoutInflater.inflate(R.layout.filelistitem, null);
            }

            var path:File = getItem(position);

            var img =  thisView.findViewById(R.id.file_img).asInstanceOf[ImageView];
            var title =  thisView.findViewById(R.id.file_title).asInstanceOf[TextView];
            var info =  thisView.findViewById(R.id.file_info).asInstanceOf[TextView];

            if(path.isDirectory()){
                img.setImageResource(R.drawable.folder);
            }

            else if(path.isFile()) {
                img.setImageResource(R.drawable.file);
            }

            title.setText(path.getName());
            thisView;
        }
    }
}
