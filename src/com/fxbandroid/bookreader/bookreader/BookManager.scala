package com.fxbandroid.bookreader.bookreader;

import android.os.Bundle;
import android.support.v4.app._;
import android.support.v4.view._;
import android.view.Gravity;
import android.view._;
import com.fxbandroid.bookreader.widget.TabSwitcher;

class BookManager extends FragmentActivity {

    private var tab:TabSwitcher = null
    private var pageAdapter: MainPagerAdapter = null

    /* Called when the activity is first created. */
    override def onCreate(savedInstanceState:Bundle): Unit ={
        super.onCreate(savedInstanceState);
        setViewPagers();
    }

    def setViewPagers() = {
        setContentView(R.layout.main)
        var fragments:Array[Fragment] = Array(new BookListManagerFragment(),new FileListManagerFragment())
        pageAdapter = new MainPagerAdapter(getSupportFragmentManager(),fragments);

        var view_pager = findViewById(R.id.viewpager).asInstanceOf[ViewPager]
        view_pager.setAdapter(pageAdapter.asInstanceOf[PagerAdapter])


        import ViewPager.OnPageChangeListener
        import View.OnClickListener
        view_pager.setOnPageChangeListener(new OnPageChangeListener(){
            override def onPageScrollStateChanged(arg0:Int) ={}
            override def onPageScrolled(arg0:Int,arg1:Float,arg2:Int) {}
            override def onPageSelected(arg0:Int) {
                tab.setCurrentPage(arg0);
                if(arg0 == 0){
                    tab.setImageForMenu(R.drawable.book_delete);
                }else if(arg0 == 1){
                    tab.setImageForMenu(R.drawable.file_back);
                }
            }
        })

        tab =findViewById(R.id.tabbar).asInstanceOf[TabSwitcher]
        tab.setViewPager(view_pager);
        def goParentDirectory() = {
            var fileFragment = pageAdapter.getItem(1).asInstanceOf[FileListManagerFragment];
            fileFragment.goParentDirectory();
        }

        def toggleBookMode() = {
            var bookFragment = pageAdapter.getItem(0).asInstanceOf[BookListManagerFragment]
            bookFragment.toggleEditMode()
        }

        tab.setOnClickListenerForMenu(new OnClickListener(){
            override def onClick(v:View):Unit = {
                if(tab.getCurrentPage() == 0){
                    if(toggleBookMode()){
                        tab.setBackgroundForMenu(R.drawable.green_bg);
                    } else {
                        tab.setBackgroundForMenu(R.drawable.bule_bg);
                    }
                }else if(tab.getCurrentPage() == 1){
                    goParentDirectory();
                }
            }
        })
    }
    override def onStop() {
        BookListManager.getInstance(this).restoreDatabase();
        super.onStop();
    }

    class MainPagerAdapter(fm:FragmentManager, fragments:Array[Fragment])
    	extends FragmentPagerAdapter(fm) {
//        private var fragments = fragmentsc
        override def getItem(position:Int) = fragments(position)
        override def getCount() = fragments.length
    }

}
