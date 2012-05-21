package com.fxbandroid.bookreader.bookreader; 

import android.app.Activity;  
import android.content.Intent; 
import android.os.Bundle;
import android.os.Environment; 
import android.os.Handler;  
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.text.Layout;
import android.text.TextUtils;  
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;  
import android.view.View.OnClickListener;
import android.view.WindowManager; 
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter; 
import android.widget.Button;  
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import com.fxbandroid.bookreader.util.Book;
import com.fxbandroid.bookreader.util.ExFile;
import com.fxbandroid.bookreader.widget.TabMenu;
import com.fxbandroid.bookreader.widget.TabSwitcher;

import java.io.File;
import java.io.Serializable; 
import java.text.SimpleDateFormat; 
import java.util.ArrayList; 
import java.util.Date;
import android.graphics.Color;
import android.widget.Toast;
import android.view.Gravity;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener; 
import java.util.Observer;
import java.util.Observable;
import android.content.Context;


public class BookManager extends Activity {

    //private BooksDB booksDB;
    
    //private static final  
    
    private BookAdapter bookAdapter;
    private FileAdapter fileAdapter;
    private ListView booksList;
    private ListView fileList;
    private TabSwitcher tab ;

    private String currentDir = "/mnt/sdcard";


    private TabMenu tabMenu;
    private TabMenu.MenuBodyAdapter []bodyAdapter=new TabMenu.MenuBodyAdapter[3];  
    private TabMenu.MenuTitleAdapter titleAdapter;
    private int setTitle = 0;

    /* Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setViewPagers();

        setFileView(); 

        //initDB();

        setBookView();

        setTabMenu();
       
    }
    private void setViewPagers() {
        setContentView(R.layout.main); 
        
        ViewPager view_pager = (ViewPager)findViewById(R.id.viewpager); 
        //将要分页显示的View装入数组中
        LayoutInflater mLi = LayoutInflater.from(this);
        View view1 = mLi.inflate(R.layout.bookmanager, null); 
        View view2 = mLi.inflate(R.layout.filemanager, null); 

        booksList = (ListView)view1.findViewById(R.id.book_list);
        fileList = (ListView)view2.findViewById(R.id.file_list);

        ArrayList<View> views = new ArrayList<View>();
        views.add(view1);
        views.add(view2); 
        
        MyPagerAdapter pages = new MyPagerAdapter(views); 
        view_pager.setAdapter(pages); 
        ViewPager.OnPageChangeListener page_change_listener = new ViewPager.OnPageChangeListener(){
              @Override
            public void onPageScrollStateChanged(int arg0) {
            }
            @Override
                public void onPageScrolled(int arg0, float arg1, int arg2) {
                }
            @Override
                public void onPageSelected(int arg0) {
                    tab.setCurrentPage(arg0);
                    if(arg0 == 0){
                        tab.setImageForMenu(R.drawable.book_delete);
                    }else if(arg0 == 1){
                        tab.setImageForMenu(R.drawable.file_back);
                    }
                }
        }; 
        view_pager.setOnPageChangeListener(page_change_listener);


        tab = (TabSwitcher)findViewById(R.id.tabbar);
        tab.setViewPager(view_pager); 
        View.OnClickListener tab_menu_click = new View.OnClickListener(){
            @Override
            public void onClick(View v){
                    if(tab.getCurrentPage() == 0){
                        if(bookAdapter.isDeleteMode()){
                            changeBookListMode(false);
                            tab.setBackgroundForMenu(R.drawable.bule_bg);
                        } else {
                            changeBookListMode(true);
                            tab.setBackgroundForMenu(R.drawable.green_bg);
                        }
                    }else if(tab.getCurrentPage() == 1){
                        goParentDirectory();
                    }
                }
        }; 
        tab.setOnClickListenerForMenu(tab_menu_click); 
    }

   

    private void setBookView() {
        
        final BookListManager booksmanager = BookListManager.getInstance(this); 
        final Context context = this;
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

    private void setFileView() {
        //setContentView(R.layout.filemanager);
        //fileList = (ListView) findViewById(R.id.file_list);
        //

        listDirectory(new File(currentDir));
        fileList.setOnItemClickListener(new OnItemClickListener() {  
  
            @Override  
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,  
                    long arg3) {  
                    clickFile(arg2); 
            }  
        }); 
     
    }

    /*
    private void setMiddleMenu(){
        //MyAnimations.initOffset(PathMenuActivity.this);
		RelativeLayout middleButtonWrapper = (RelativeLayout) findViewById(R.id.middle_menu_wrapper);
		composerButtonsShowHideButton = (RelativeLayout) findViewById(R.id.composer_buttons_show_hide_button);
		composerButtonsShowHideButtonIcon = (ImageView) findViewById(R.id.composer_buttons_show_hide_button_icon);

		composerButtonsShowHideButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!areButtonsShowing) {
					MyAnimations.startAnimationsIn(composerButtonsWrapper, 300);
					composerButtonsShowHideButtonIcon
							.startAnimation(MyAnimations.getRotateAnimation(0,
									-270, 300));
				} else {
					MyAnimations
							.startAnimationsOut(composerButtonsWrapper, 300);
					composerButtonsShowHideButtonIcon
							.startAnimation(MyAnimations.getRotateAnimation(
									-270, 0, 300));
				}
				areButtonsShowing = !areButtonsShowing;
			}
		});
		for (int i = 0; i < composerButtonsWrapper.getChildCount(); i++) {
			composerButtonsWrapper.getChildAt(i).setOnClickListener(
					new View.OnClickListener() {
						@Override
						public void onClick(View arg0) {
						}
					});
		}
		
		composerButtonsShowHideButton.startAnimation(MyAnimations
				.getRotateAnimation(0, 360, 200));

	}

    */


    private void setTabMenu() { 
        //设置分页栏的标题  
        titleAdapter = new TabMenu.MenuTitleAdapter(this,
                new String[] { "常用", "设置", "工具" }, 16, 0xFF222222,Color.LTGRAY,Color.WHITE);  
        //定义每项分页栏的内容  
        bodyAdapter[0]=new TabMenu.MenuBodyAdapter(this,new String[] { "常用1", "常用2", },   
                 new int[] { R.drawable.file,  R.drawable.file},13, 0xFFFFFFFF);  
           
        bodyAdapter[1]=new TabMenu.MenuBodyAdapter(this,new String[] { "设置1", "设置2",  
                                    "设置3"}, new int[] { R.drawable.file, 
                                    R.drawable.file, R.drawable.file},13, 0xFFFFFFFF);  
           
        bodyAdapter[2]=new TabMenu.MenuBodyAdapter(this,new String[] { "工具1", "工具2",  
                                                    "工具3", "工具4" }, new int[] { R.drawable.file,  
                                                    R.drawable.file, R.drawable.file,  
                                                    R.drawable.file },13, 0xFFFFFFFF);  
        tabMenu=new TabMenu(this, 
                        new TitleClickEvent(),  
                        new BodyClickEvent(),  
                        titleAdapter,  
                        0x55123456,//TabMenu的背景颜色  
                        R.style.PopupAnimation);//出现与消失的动画  
           
         tabMenu.update();  
         tabMenu.SetTitleSelect(0);  
         tabMenu.SetBodyAdapter(bodyAdapter[0]); 

    }
    private class TitleClickEvent implements OnItemClickListener{  
        @Override  
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,  
                long arg3) {  
            setTitle=arg2;  
            tabMenu.SetTitleSelect(arg2);  
            tabMenu.SetBodyAdapter(bodyAdapter[arg2]);  
        }  
    }  
      
    private class BodyClickEvent implements OnItemClickListener{  
        @Override  
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,  
                long arg3) {  
            tabMenu.SetBodySelect(arg2,Color.GRAY);  
            String str="第"+String.valueOf(setTitle)+"栏/n/r"  
            +"第"+String.valueOf(arg2)+"项";  
            Toast.makeText(BookManager.this, str, 500).show();  
              
        }  
          
    }  


    private void initDB() {
        Book[] t = new Book[]
        {  
            new Book("全职高手","txt","02-07 13:25","/mnt/sdcard/1.txt",0,"0%"),  
            new Book("大航海时代","txt","02-07 11:00","/mnt/sdcard/1.txt",0,"0%"),  
            new Book("网游之近战法师","txt","02-01 13:45","/mnt/sdcard/1.txt",0,"0%"),  
            new Book("天行健","txt","01-15 8:40","/mnt/sdcard/1.txt",0,"0%"),  
            new Book("人生若只若初见","txt","01-23 23:08","/mnt/sdcard/1.txt",0,"0%")  
        };  

        BooksDB booksDB = new BooksDB(this); 
        //BookAdapter adp = new BookAdapter(this,R.layout.booklistitem,t); 
        //booksDB.inputAdapter(adp);


    }
    @Override 
    public void onStop() { 
        
        BookListManager.getInstance(this).restoreDatabase();
        super.onStop();
    }
    

    public String getSDPath() {
        File sdDir = null;
        //判断sd卡是否存在
        if( Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED) )  
        {                              
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
        }  
        return sdDir.toString();

    }


    private void changeBookListMode(boolean isDelete){
        bookAdapter.setDeleteMode(isDelete);
        booksList.setAdapter(bookAdapter);
    }

    public void openBook(int position) {

        Intent intent = new Intent(this,BookViewer.class);
        intent.setAction("book_open"); 
        intent.putExtra("book_position",position); 

     //   startActivityForResult(intent,whichBook);
        startActivity(intent);

    }


    private void clickFile(int whichFile){
        File file = (File)(fileList.getAdapter().getItem(whichFile));
        
        if(file.isDirectory()){
            listDirectory(file);
        }
        else if(ExFile.getExtension(file).equals("txt")) {
            openBook(BookListManager.getInstance(this).addBook(file)); 
        }
    } 

    private void listDirectory(File file){
            File[] file_list = file.listFiles(); 
            file_list = ExFile.sortFiles(file_list); 
            FileAdapter files = new  FileAdapter(this,file_list);
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
 

   @Override  
    public boolean onCreateOptionsMenu(Menu menu) {//初始化Menu菜单选择项 

        return super.onCreateOptionsMenu(menu);          
    }
    public boolean onPrepareOptionsMenu(Menu menu){
      /*   if (tabMenu != null) {  
            if (tabMenu.isShowing())  
                tabMenu.dismiss();  
            else {  
                tabMenu.showAtLocation(findViewById(R.id.main_layout),  
                        Gravity.BOTTOM, 0, 0);  
            }  
        } */
        return true;
    }

    
    private class MyPagerAdapter extends PagerAdapter {//implements TitleProvider{  
       
        private ArrayList<View> views;
  
        public MyPagerAdapter(ArrayList<View> views) {
            super();

            this.views = views;

        }
        @Override  
        public void destroyItem(View arg0, int arg1, Object arg2) {  
                ((ViewPager) arg0).removeView(views.get(arg1));  
        }  
  
        
        @Override  
        public int getCount() {  
            return views.size();  
        }  
  
        @Override  
        public Object instantiateItem(View arg0, int arg1) {  

            ((ViewPager) arg0).addView(views.get(arg1),0);  
            return views.get(arg1);  
        }  
  
        @Override  
        public boolean isViewFromObject(View arg0, Object arg1) {  

            return arg0==(arg1);  
        }  

    } 
    
}
