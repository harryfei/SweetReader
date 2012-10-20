package com.fxbandroid.bookreader.bookreader;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Layout;
import android.text.method.ScrollingMovementMethod ;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout ;
import android.widget.LinearLayout.LayoutParams;
import com.fxbandroid.bookreader.util.Book;
import com.fxbandroid.bookreader.util.TextFile;
import com.fxbandroid.bookreader.widget.BookPageFactory;
import com.fxbandroid.bookreader.widget.BookPageView;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader ;
import java.io.IOException;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;



public class BookViewer extends Activity// implements OnTouchListener,OnGestureListener
{
    private BookPageView pageView;//用来显示的上下文
    private BookPageFactory pages;


    private Book book;//书(全地址）
    private int bookPosition;

    private DisplayMetrics metrics;//屏幕分辨率
    private int screenWidth, screenHeight;// 屏幕尺寸
    private GestureDetector detector;// 手势监听者
    private String displayTxt = "";

    private int currentX = 0, currentY = 0;// TextView左上角的像素值

    private int textLength;

    private Bitmap curPageBitmap;
    private Bitmap nextPageBitmap;


    /* Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);// 全屏

        setContentView(R.layout.bookviewer);

        pageView = (BookPageView)findViewById(R.id.text_show);
        //scroll = (ScrollView)findViewById(R.id.scroll_view);

        book = getBook();

        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;// 获得屏幕分辨率


        resetTextView();
        pages.setBgBitmap(BitmapFactory.decodeResource(
		                        this.getResources(), R.drawable.shelf_bkg));
        //pages.setBold(true);
        //pages.setItalic(true);
        loadFile();
        pageView.setBitmaps(curPageBitmap, curPageBitmap);

       	pageView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent e) {
				// TODO Auto-generated method stub

				boolean ret = false;
				if (v == pageView) {
					if (e.getAction() == MotionEvent.ACTION_DOWN) {
						pageView.abortAnimation();
						pageView.calcCornerXY(e.getX(), e.getY());

						pages.drawTo(curPageBitmap);
                        if (pageView.dragToRight()) {
							try {
								    pages.prePage();
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							if (pages.isFirstPage())
								return false;
							pages.drawTo(nextPageBitmap);
						} else {
							try {
								pages.nextPage();
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							if (pages.isLastPage()) {
								return false;
							}
							pages.drawTo(nextPageBitmap);
						}
						pageView.setBitmaps(curPageBitmap, nextPageBitmap);
					}

					ret = pageView.doTouchEvent(e);
					return ret;
				}

				return false;
			}

		});

    }

    @Override
    public void onResume() {

        super.onResume();
    }


    @Override
    public void finish()
    {
        book.readPercent = pages.getReadedPercent();
        book.readPosition = pages.getCurrentOffset();
        SimpleDateFormat format=new SimpleDateFormat( "MM-dd HH:mm");
        book.Time=format.format((new Date()));

        BookListManager.getInstance(this).updataBook(bookPosition,book);


        super.finish();

    }


// 加载文件

    private void loadFile()
    {
        // TODO Auto-generated method stub

        try {
			pages.openbook(book.path);
            pages.exchangeToOffset(book.readPosition);
            pages.drawTo(curPageBitmap);
		} catch (IOException e1) {

            return;
		}

    }

// 重置TextView的大小
    private void resetTextView()
    {
        // TODO Auto-generated method stub

        curPageBitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888);
        nextPageBitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888);
        pages = new BookPageFactory(screenWidth,screenHeight);
        pageView.setScreen(screenWidth,screenHeight);
    }

    private Book getBook()
    {
        Intent intent = getIntent();

        bookPosition = intent.getIntExtra("book_position",-1);

        return BookListManager.getInstance(this).getBooks().get(bookPosition);
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent event)
    {
        //setTitle(Float.toString(getRateOfReadedText()));
        return super.dispatchTouchEvent(event);
    }


 /*   @Override
    public boolean dispatchKeyEvent(KeyEvent event)
    { // 点击键即可触发此方法，在onKeyDown，onKeyUp前
            switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_VOLUME_UP:
            case KeyEvent.KEYCODE_VOLUME_DOWN:
               // Log.v(TAG, "KEYCODE_VOLUME_UP/KEYCODE_VOLUME_DOWN");
                // TODO
                setTitle(Float.toString(getRateOfReadedText()));

                return true; // 不接受往下传，到此结束

            case KeyEvent.KEYCODE_BACK:
                //Log.v(TAG, "KEYCODE_BACK");
                // TODO
                return false; // 接受往下传，接着执行父类的这个事件
            }
            return false; // 将其他键往下传
        }

        public boolean onKeyUp(int keyCode, KeyEvent event) { // 键松开时执行
            Log.v(TAG, "onKeyUp");
            return super.onKeyUp(keyCode, event);
        }

        public boolean onKeyDown(int keyCode, KeyEvent event) { // 键按下时执行
            Log.v(TAG, "onKeyDown - keyCode");
            return super.onKeyDown(keyCode, event);
        }

    // 触摸TextView

    @Override

    public boolean onTouch(View v, MotionEvent event)
    {
        // TODO Auto-generated method stub
        return detector.onTouchEvent(event);// 工作交给手势监听者
        //return true;
    }



    // 下面的各个函数是OnGestureListener的实现，具体动作这里不做赘述

    @Override

    public boolean onDown(MotionEvent e)
    {
        // TODO Auto-generated method stub
        //setTitle(Integer.toString(content.getLayout().getLineStart(content.getLayout().
         //               getLineForVertical(content.getScrollY()))));
                    //getOffsetForHorizontal(content.getLayout().getLineForVertical(content.getScrollY()),
                     //   content.getTextScaleX())));
       // setTitle("OK");

        return false;
    }



    @Override

    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
    {
        // TODO Auto-generated method stub
        return false;
    }



    @Override

    public void onLongPress(MotionEvent e)
    {
        // TODO Auto-generated method stub
    }



    @Override

    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
    {
        // TODO Auto-generated method stub
        int layoutWidth = content.getLayoutParams().width; // 获得TextView的宽度
        int layoutHeight = content.getLineCount() * content.getLineHeight(); // 获得TextView的实际高度
        if (currentX  >= 0)
        {
            if (currentX  > layoutWidth - screenWidth)
            {
                currentX = layoutWidth - screenWidth;
            }
            else
            {
                currentX = (int) (currentX );
            }
        }
        else
        {
            currentX = 0;
        }

        if (currentY +distanceY>= 0)
        {
            if (currentY +distanceY> layoutHeight - screenHeight)
            {
                currentY = layoutHeight - screenHeight;
            }
            else
            {
                currentY = (int) (currentY + distanceY);
            }

        }
        else
        {
            currentY = 0;
        }
        content.scrollTo(currentX, currentY); // 使文本滚动到指定的地方
        return false;


       setTitle(Float.toString(getRateOfReadedText()));

        return false;


    }



    @Override

    public void onShowPress(MotionEvent e)
    {
        // TODO Auto-generated method stub
        //setTitle(Integer.toString(content.getScrollX())+"    "+Integer.toString(content.getScrollY()));

    }



    @Override
    public boolean onSingleTapUp(MotionEvent e)
    {
        // TODO Auto-generated method stub
        return false;
    }



    private int getCurrentBeginOffset()
    {
        ExTextView tv = content;
        int topLine = tv.getLayout().getLineForVertical(scroll.getScrollY());
        int beginOffset =  tv.getLayout().getLineStart(topLine);
        return beginOffset;

    }

    private int getCurrentEndOffset()
    {
        ExTextView tv = content;
        int w=0,h=0;
        content.measure(w,h);
        int bottomLine = tv.getLayout().getLineForVertical(scroll.getScrollY()+h);
        int endOffset =  (tv.getLayout().getLineEnd(bottomLine));
        return endOffset;
    }

    private float getRateOfReadedText()
    {
        int readed = getCurrentEndOffset();

        float rate = ((float)getCurrentEndOffset()/(float)textLength)*100f;

        float   nrate   =   (float)(Math.round(rate*100))/100;
        return nrate;
    }


    private void goPositionForOffset(int offset)
    {
        ExTextView tv = content;

        int line = tv.getLayout().getLineForOffset(offset);
        int y = tv.getLayout().getLineTop(line);

        class scrollRun implements Runnable{
            private int y = 0;
            public scrollRun(int y){
                this.y = y;
            }
            @Override
            public void run() {
               scroll.scrollTo(0,y);
            }
        }

        scroll.post(new scrollRun(y));


    }

*/



    public boolean onCreateOptionsMenu(Menu menu)
    {//初始化Menu菜单选择项
        //super.onCreateOptionsMenu(menu);

        //添加菜单项，比如：

        //menu.add(0, OK_ID, 0, R.string.ok).setShortcut('2', 'v');//设置快捷键
        //添加其他菜单项。。。。。。

        return true;
    }


/*
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);

        //这里可以事先设置菜单的可见性，如果都可见，可以不设置

        menu.findItem(OK_ID).setVisible(true);//设置菜单项可见性
        return true;
    }



    public boolean onOptionsItemSelected(MenuItem item)
    {//选择了一个菜单项的时候调用

        //这里可以预先处理想要的变量
        switch (item.getItemId()) {
        case BACK_ID://一项一项的处理想要做的，不用我介绍了吧


            return true;

            //.............
        }

        return super.onOptionsItemSelected(item);
    }

*/

}



