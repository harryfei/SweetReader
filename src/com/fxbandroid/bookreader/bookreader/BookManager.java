package com.fxbandroid.bookreader.bookreader;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import com.fxbandroid.bookreader.widget.TabSwitcher;

public class BookManager extends FragmentActivity {

    private TabSwitcher tab ;
    private MainPagerAdapter pageAdapter;


    /* Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setViewPagers();
    }
    private void setViewPagers() {
        setContentView(R.layout.main);

        Fragment[] fragments = new Fragment[2];
        fragments[0] = new BookListManagerFragment();
        fragments[1] = new FileListManagerFragment();
        pageAdapter = new MainPagerAdapter(getSupportFragmentManager(),fragments);

        ViewPager view_pager = (ViewPager)findViewById(R.id.viewpager);
        view_pager.setAdapter(pageAdapter);


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
                    if(toggleBookMode()){
                        tab.setBackgroundForMenu(R.drawable.green_bg);
                    } else {
                        tab.setBackgroundForMenu(R.drawable.bule_bg);
                    }
                }else if(tab.getCurrentPage() == 1){
                    goParentDirectory();
                }
            }
        };
        tab.setOnClickListenerForMenu(tab_menu_click);
    }
    @Override
    public void onStop() {

        BookListManager.getInstance(this).restoreDatabase();
        super.onStop();
    }
    private  void goParentDirectory(){
        FileListManagerFragment fileFragment = (FileListManagerFragment) pageAdapter.getItem(1);
        fileFragment.goParentDirectory();
    }

    private boolean toggleBookMode(){
        BookListManagerFragment bookFragment = (BookListManagerFragment) pageAdapter.getItem(0);
        return bookFragment.toggleEditMode();
    }

    private class MainPagerAdapter extends FragmentPagerAdapter {
        private Fragment[] fragments;

        public MainPagerAdapter(FragmentManager fm,Fragment[] fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public int getCount() {
            return fragments.length;
        }

    }

}
