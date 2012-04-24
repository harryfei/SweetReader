package com.fxbandroid.bookreader.widget;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader; 
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.channels.FileChannel;
import java.nio.MappedByteBuffer;
import java.text.DecimalFormat;
import java.util.Vector;
import org.mozilla.universalchardet.CharsetListener ;
import org.mozilla.universalchardet.Constants;
import org.mozilla.universalchardet.UniversalDetector;
import android.graphics.Typeface;

public class BookPageFactory {

       //文本页面管理器
    private File bookFile = null;

    private MappedByteBuffer showBuf = null; 
    private int showBufLen = 0;// 总长度
    private int showBufBegin = 0;// 当前长度
    private int showBufEnd = 0;

    private String encoding = "GBK";

    private Bitmap bookBG = null;

    private int width;
    private int height;

    private Vector<String> showLines = new Vector<String>();
            //config
    private int fontSize = 19;
    private int textColor = Color.BLACK;
    private int backColor = 0xffff9e85; // 背景颜色
    private int marginWidth = 15; // 左右与边缘的距离
    private int marginHeight = 20; // 上下与边缘的距离
    private int lineSpacing =  7;
    private Typeface font  = Typeface.create(Typeface.DEFAULT,Typeface.NORMAL);

    private int lineCount; // 每页可以显示的行数
    private float visibleHeight; // 绘制内容的宽
    private float visibleWidth; // 绘制内容的宽

    private boolean isFirstPage, isLastPage;

    private Paint paint;

        // 进度格式
    private final static DecimalFormat df = new DecimalFormat("##.##");

    public BookPageFactory(int w, int h) {

        width = w;
        height = h;
        initPage();
    }
    
    private void initPage(){
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextAlign(Align.LEFT);
        paint.setTextSize(fontSize);
        paint.setColor(textColor);
        paint.setTypeface(font);
        colcPage();     
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
        paint.setTextSize(fontSize);
        colcPage();
    }
    public void setMarginWidth(int width) {
        this.marginWidth = width;
        colcPage();
    }
    public void setMarginHeight(int height) {
        this.marginHeight = height;
        colcPage();
    }
    public void setLineSpacing(int spacing){
        this.lineSpacing = spacing;
        colcPage();
    }
    public void setTextColor(int color){
        this.textColor = color;
        paint.setColor(color);
    }

    public void setBold(boolean b){
        /*int style = font.getStyle();
        if (b){
            style |= Typeface.BOLD; 
        }else{
            style &= ~Typeface.BOLD;
        }

         font = Typeface.create(this.font,style);
         paint.setTypeface(font);
         */
        paint.setFakeBoldText(b); 
    }
    public void setItalic(boolean b){
      /*  int style = font.getStyle();
        if (b){
            style |= Typeface.ITALIC; 
        }else{
            style &= ~Typeface.ITALIC;
        }
         this.font = Typeface.create(this.font,style);
         paint.setTypeface(font);*/
        if (b){
            paint.setTextSkewX(-0.25f);  
        }else{
            paint.setTextSkewX(0);
        }

        
        
    }
    public void setFont(String familyname){
    }

    private void colcPage() {
        visibleWidth = width - marginWidth * 2;
        visibleHeight = height - marginHeight * 2;
        lineCount = (int) (visibleHeight / (fontSize+lineSpacing)); // 可显示的行数 
    }


    public void openbook(String file_path) throws IOException {
        bookFile = new File(file_path);
        long lLen = bookFile.length();

        showBufLen = (int) lLen;
        showBuf = new RandomAccessFile(bookFile, "r").getChannel().map(
                FileChannel.MapMode.READ_ONLY, 0, lLen);
        // 构造一个探测器
        UniversalDetector det = new UniversalDetector(null);
            int len = 4098;
            MappedByteBuffer sbuf = showBuf;
            int buf_len = showBufLen;
            if (len>buf_len)
                len =buf_len; 
            byte[] buf = new byte[len];
            for (int i = 0; i < len; i++) {
                buf[i] = sbuf.get(i);
            }

            det.handleData(buf,0,len);
            det.dataEnd();
            encoding = det.getDetectedCharset();
            // 也可以同步得到探测结果
            if(encoding == "GB18030".intern()) 
                encoding = "GBK"; 

    }

    // 读取上一段落
    protected byte[] readParagraphBack(int from_pos) {
        int end_pos = from_pos;
        int i;
        byte b0, b1;
        MappedByteBuffer buf = showBuf;
        if (encoding.equals("UTF-16LE")) {
            i = end_pos - 2;
            while (i > 0) {
                b0 = buf.get(i);
                b1 = buf.get(i + 1);
                if (b0 == 0x0a && b1 == 0x00 && i != end_pos - 2) {
                    i += 2;
                    break;
                }
                i--;
            }

        } else if (encoding.equals("UTF-16BE")) {
            i = end_pos - 2;
            while (i > 0) {
                b0 = buf.get(i);
                b1 = buf.get(i + 1);
                if (b0 == 0x00 && b1 == 0x0a && i != end_pos - 2) {
                    i += 2;
                    break;
                }
                i--;
            }
        } else {
            i = end_pos - 1;
            while (i > 0) {
                b0 = buf.get(i);
                if (b0 == 0x0a && i != end_pos - 1) {
                    i++;
                    break;
                }
                i--;
            }
        }
        if (i < 0)
            i = 0;
        int para_size = end_pos - i;
        int j;
        byte[] r_buf = new byte[para_size];
        for (j = 0; j < para_size; j++) {
            r_buf[j] = buf.get(i + j);
        }
        return r_buf;
    }

    // 读取下一段落
    protected byte[] readParagraphForward(int from_pos) {
        int start_pos = from_pos;
        int i = start_pos;
        byte b0, b1;
        MappedByteBuffer buf = showBuf;
        int buf_len = showBufLen;
        // 根据编码格式判断换行
        if (encoding.equals("UTF-16LE")) {
            while (i < buf_len - 1) {
                b0 = buf.get(i++);
                b1 = buf.get(i++);
                if (b0 == 0x0a && b1 == 0x00) {
                    break;
                }
            }
        } else if (encoding.equals("UTF-16BE")) {
            while (i < buf_len - 1) {
                b0 = buf.get(i++);
                b1 = buf.get(i++);
                if (b0 == 0x00 && b1 == 0x0a) {
                    break;
                }
            }
        } else {
            while (i < buf_len) {
                b0 = buf.get(i++);
                if (b0 == 0x0a) {
                    break;
                }
            }
        }
        int para_size = i - start_pos;
        byte[] r_buf = new byte[para_size];
        for (i = 0; i < para_size; i++) {
            r_buf[i] = buf.get(from_pos + i);
        }
        return r_buf;
    }

    protected Vector<String> pageDown() {
        int buf_end = showBufEnd;
        int buf_len = showBufLen;
        int line_count = lineCount;
        float v_w = visibleWidth;

        String paragraph = "";
        Vector<String> lines = new Vector<String>(); 
        while (lines.size() < line_count && buf_end < buf_len) {
            byte[] para_buf = readParagraphForward(buf_end); // 读取一个段落
            buf_end += para_buf.length;
            try {
                    paragraph = new String(para_buf, encoding);
            } catch (UnsupportedEncodingException e) {

                e.printStackTrace();
            }

            String str_return = "";
            if (paragraph.indexOf("\r\n") != -1) {
                str_return = "\r\n";
                paragraph = paragraph.replaceAll("\r\n", "");
            } else if (paragraph.indexOf("\n") != -1) {
                str_return = "\n";
                paragraph = paragraph.replaceAll("\n", "");
            }

            if (paragraph.length() == 0) {
                lines.add(paragraph);
            }
            while (paragraph.length() > 0) {
                int size = paint.breakText(paragraph, true, v_w,
                        null);
                lines.add(paragraph.substring(0, size));
                paragraph = paragraph.substring(size);
                if (lines.size() >= line_count) {
                    break;
                }
            }
            if (paragraph.length() != 0) {
                try {
                    buf_end -= (paragraph + str_return)
                            .getBytes(encoding).length;
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }

        showBufBegin = showBufEnd;
        showBufEnd = buf_end;

        return lines;
    }

    protected Vector<String> pageUp() {
        if (showBufBegin < 0) {
            showBufBegin = 0;
        }

        int buf_begin = showBufBegin;
        int buf_Len = showBufLen;
        int line_count = lineCount;
        float v_w = visibleWidth;
        
        Vector<String> lines = new Vector<String>();
        String paragraph = "";
        while (lines.size() < line_count && buf_begin > 0) {
            Vector<String> para_lines = new Vector<String>();
            byte[] para_buf = readParagraphBack(buf_begin);
            buf_begin -= para_buf.length;
            try {
                paragraph = new String(para_buf,encoding);
            } catch (UnsupportedEncodingException e) { 
                e.printStackTrace();
            }
            paragraph = paragraph.replaceAll("\r\n", "");
            paragraph = paragraph.replaceAll("\n", "");

            if (paragraph.length() == 0) {
                para_lines.add(paragraph);
            }
            while (paragraph.length() > 0) {
                int size = paint.breakText(paragraph, true, v_w,
                        null);
                //para_lines.add(paragraph.substring(0, size));
                //paragraph = paragraph.substring(size);
                para_lines.add(paragraph.substring(0,size));
                paragraph =  paragraph.substring(size);
            }
            lines.addAll(0, para_lines);
        }
        while (lines.size() > line_count) {
            try {
                buf_begin += lines.get(0).getBytes(encoding).length;
                lines.remove(0);
            } catch (UnsupportedEncodingException e) { 
                e.printStackTrace();
            }
        }
        showBufEnd = showBufBegin;
        showBufBegin = buf_begin;
        return lines;
    }

    public void prePage() throws IOException {
        if (showBufBegin <= 0) {
            showBufBegin = 0;
            isFirstPage = true;
            return;
        } else {
            isFirstPage = false;
        }
        showLines.clear();
        showLines = pageUp();
    }

    public void nextPage() throws IOException {
        if (showBufEnd >= showBufLen) {
            isLastPage = true;
            return;
        } else {
            isLastPage = false;
        }
        showLines.clear();
        //showBufBegin = showBufEnd;
        showLines = pageDown();
    }

    public void drawTo(Bitmap bitmap) {
        Canvas c = new Canvas(bitmap);
        if (showLines.size() == 0) {
            showLines = pageDown();
        }
        if (showLines.size() > 0) {
            if (bookBG == null)
                c.drawColor(backColor);
            else
                c.drawBitmap(bookBG, 0, 0, null);
            int y = marginHeight;
            for (String strLine : showLines) {
                y += fontSize+lineSpacing;
                c.drawLine(marginWidth,y+4,width-marginWidth,y+4,paint);
                c.drawText(strLine, marginWidth, y, paint);
            }
        }

        String strPercent = getReadedPercent();
        int nPercentWidth = (int) paint.measureText("999.9%") + 1;
        c.drawText(strPercent, width - nPercentWidth, height - 5, paint);
    }

    public void setBgBitmap(Bitmap BG) {
        bookBG = BG;
    }

    public boolean isFirstPage() {
        return isFirstPage;
    }

    public boolean isLastPage() {
        return isLastPage;
    }
    public int getCurrentOffset() {
        return showBufBegin;
    }
    public String getReadedPercent() {
        float percent = (float) (showBufEnd * 1.0 / showBufLen);
        return df.format(percent * 100) + "%"; 
    }
    public void exchangeToOffset(int offset) {
        if (offset <0) {
            return;
        }
        showBufBegin = offset;
        showBufEnd = offset;
        
        showLines.clear();
        showLines = pageDown();
    }

}
