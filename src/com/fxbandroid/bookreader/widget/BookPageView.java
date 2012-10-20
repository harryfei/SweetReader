package com.fxbandroid.bookreader.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Region;
import android.graphics.drawable.GradientDrawable;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

import android.util.AttributeSet;
import android.graphics.PaintFlagsDrawFilter;

public class BookPageView extends View {

	 //View的特效显示

	private int mWidth = 320;
	private int mHeight = 480;
	private int cornerX = 0; // 拖拽点对应的页脚
	private int cornerY = 0;
    private Path path0;     //拖曳时用于计算的路径线
	private Path path1;

    Bitmap curPageBitmap = null; // 当前页
	Bitmap nextPageBitmap = null;//下一页
    //Bitmap prePageBitmap = null;//上一页

	PointF touchPoint = new PointF(); //拖拽点

	PointF bezierStart1 = new PointF(); // 贝塞尔曲线起始点
	PointF bezierControl1 = new PointF(); // 贝塞尔曲线控制点
	PointF beziervertex1 = new PointF(); // 贝塞尔曲线顶点
	PointF bezierEnd1 = new PointF(); // 贝塞尔曲线结束点
	PointF bezierStart2 = new PointF(); // 另一条贝塞尔曲线
	PointF bezierControl2 = new PointF();
	PointF beziervertex2 = new PointF();
	PointF bezierEnd2 = new PointF();

	float middleX;
	float middleY;
	float degrees;
	float touchToCornerDis;
	ColorMatrixColorFilter colorMatrixFilter;
	Matrix matrix;
	float[] matrixArray = { 0, 0, 0, 0, 0, 0, 0, 0, 1.0f };

	boolean isRTandLB; // 是否属于右上左下
	float maxLength = (float) Math.hypot(mWidth, mHeight);
	int[] backShadowColors;
	int[] frontShadowColors;
	GradientDrawable backShadowDrawableLR;
	GradientDrawable backShadowDrawableRL;
	GradientDrawable folderShadowDrawableLR;
	GradientDrawable folderShadowDrawableRL;

	GradientDrawable frontShadowDrawableHBT;
	GradientDrawable frontShadowDrawableHTB;
	GradientDrawable frontShadowDrawableVLR;
	GradientDrawable frontShadowDrawableVRL;

	Paint buf_paint;

	Scroller scroller;

	public BookPageView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub

        init();

	}
    public BookPageView(Context context,AttributeSet attr)
    {
        super(context,attr);
        init();

    }

    private void init()
    {

        path0 = new Path();
		path1 = new Path();
		createDrawable();

		buf_paint = new Paint();
		buf_paint.setStyle(Paint.Style.FILL);

		ColorMatrix cm = new ColorMatrix();
		float array[] = { 0.55f, 0, 0, 0, 80.0f, 0, 0.55f, 0, 0, 80.0f, 0, 0,
				0.55f, 0, 80.0f, 0, 0, 0, 0.2f, 0 };
		cm.set(array);
		colorMatrixFilter = new ColorMatrixColorFilter(cm);
		matrix = new Matrix();
		scroller = new Scroller(getContext());

		touchPoint.x = 0.01f; // 不让x,y为0,否则在点计算时会有问题
		touchPoint.y = 0.01f;
    }

    /**
     * touch事件的响应与处理
     */
	public boolean doTouchEvent(MotionEvent event) {

		if (event.getAction() == MotionEvent.ACTION_MOVE) {
			touchPoint.x = event.getX();
			touchPoint.y = event.getY();
			this.postInvalidate();
		}
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			touchPoint.x = event.getX();
			touchPoint.y = event.getY();

		}
		if (event.getAction() == MotionEvent.ACTION_UP) {
			if (canDragOver()) {
				startAnimation(1200);
			} else {
				touchPoint.x = cornerX - 0.09f;
				touchPoint.y = cornerY - 0.09f;
			}

			this.postInvalidate();
		}
		return true;
	}
    /**
     * 实际显示的处理
     */
    @Override
	protected void onDraw(Canvas canvas) {
		//canvas.drawColor(0xFFAAAAAA);
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0,Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));
        if(true)
        {
            canvas.drawBitmap(nextPageBitmap,0,0,null);
        }
        else
        {
            canvas.drawColor(0xFFAAAAAA);
            calcPoints();
            drawCurrentPageArea(canvas, curPageBitmap, path0);
            drawNextPageAreaAndShadow(canvas, nextPageBitmap);
            drawCurrentPageShadow(canvas);
            drawCurrentBackArea(canvas, curPageBitmap);
        }
	}


	public void setBitmaps(Bitmap bm1, Bitmap bm2) {
		curPageBitmap = bm1;
		nextPageBitmap = bm2;
	}


	public void setScreen(int w, int h) {
		mWidth = w;
		mHeight = h;
	}

   	/**
	 * 判断是否从左边翻向右边
	 */
	public boolean dragToRight() {
		if (cornerX > 0)
			return false;
		return true;
	}


	/**
	 *  计算拖拽点对应的拖拽脚
	 */
	public void calcCornerXY(float x, float y) {
		if (x <= mWidth / 2)
			cornerX = 0;
		else
			cornerX = mWidth;
		if (y <= mHeight / 2)
			cornerY = 0;
		else
			cornerY = mHeight;
		if ((cornerX == 0 && cornerY == mHeight)
				|| (cornerX == mWidth && cornerY == 0))
			isRTandLB = true;
		else
			isRTandLB = false;
	}





	/**
	 * 创建阴影的GradientDrawable
	 */
	private void createDrawable() {
		int[] color = { 0x333333, 0xb0333333 };
		folderShadowDrawableRL = new GradientDrawable(
				GradientDrawable.Orientation.RIGHT_LEFT, color);
		folderShadowDrawableRL
				.setGradientType(GradientDrawable.LINEAR_GRADIENT);

		folderShadowDrawableLR = new GradientDrawable(
				GradientDrawable.Orientation.LEFT_RIGHT, color);
		folderShadowDrawableLR
				.setGradientType(GradientDrawable.LINEAR_GRADIENT);

		backShadowColors = new int[] { 0xff111111, 0x111111 };
		backShadowDrawableRL = new GradientDrawable(
				GradientDrawable.Orientation.RIGHT_LEFT, backShadowColors);
		backShadowDrawableRL.setGradientType(GradientDrawable.LINEAR_GRADIENT);

		backShadowDrawableLR = new GradientDrawable(
				GradientDrawable.Orientation.LEFT_RIGHT, backShadowColors);
		backShadowDrawableLR.setGradientType(GradientDrawable.LINEAR_GRADIENT);

		frontShadowColors = new int[] { 0x80111111, 0x111111 };
		frontShadowDrawableVLR = new GradientDrawable(
				GradientDrawable.Orientation.LEFT_RIGHT, frontShadowColors);
		frontShadowDrawableVLR
				.setGradientType(GradientDrawable.LINEAR_GRADIENT);
		frontShadowDrawableVRL = new GradientDrawable(
				GradientDrawable.Orientation.RIGHT_LEFT, frontShadowColors);
		frontShadowDrawableVRL
				.setGradientType(GradientDrawable.LINEAR_GRADIENT);

		frontShadowDrawableHTB = new GradientDrawable(
				GradientDrawable.Orientation.TOP_BOTTOM, frontShadowColors);
		frontShadowDrawableHTB
				.setGradientType(GradientDrawable.LINEAR_GRADIENT);

		frontShadowDrawableHBT = new GradientDrawable(
				GradientDrawable.Orientation.BOTTOM_TOP, frontShadowColors);
		frontShadowDrawableHBT
				.setGradientType(GradientDrawable.LINEAR_GRADIENT);
	}


    //--------------------卷脚翻页动作-----------------------------------

	/**
	 * 求解直线P1P2和直线P3P4的交点坐标
	 */
	private PointF getCross(PointF P1, PointF P2, PointF P3, PointF P4) {
		PointF CrossP = new PointF();
		// 二元函数通式： y=ax+b
		float a1 = (P2.y - P1.y) / (P2.x - P1.x);
		float b1 = ((P1.x * P2.y) - (P2.x * P1.y)) / (P1.x - P2.x);

		float a2 = (P4.y - P3.y) / (P4.x - P3.x);
		float b2 = ((P3.x * P4.y) - (P4.x * P3.y)) / (P3.x - P4.x);
		CrossP.x = (b2 - b1) / (a1 - a2);
		CrossP.y = a1 * CrossP.x + b1;
		return CrossP;
	}

	private void calcPoints() {
		middleX = (touchPoint.x + cornerX) / 2;
		middleY = (touchPoint.y + cornerY) / 2;
		bezierControl1.x = middleX - (cornerY - middleY)
				* (cornerY - middleY) / (cornerX - middleX);
		bezierControl1.y = cornerY;
		bezierControl2.x = cornerX;
		bezierControl2.y = middleY - (cornerX - middleX)
				* (cornerX - middleX) / (cornerY - middleY);

		bezierStart1.x = bezierControl1.x - (cornerX - bezierControl1.x)
				/ 2;
		bezierStart1.y = cornerY;

		// 当mBezierStart1.x < 0或者mBezierStart1.x > 480时
		// 如果继续翻页，会出现BUG故在此限制
		if (touchPoint.x > 0 && touchPoint.x < mWidth) {
			if (bezierStart1.x < 0 || bezierStart1.x > mWidth) {
				if (bezierStart1.x < 0)
					bezierStart1.x = mWidth - bezierStart1.x;

				float f1 = Math.abs(cornerX - touchPoint.x);
				float f2 = mWidth * f1 / bezierStart1.x;
				touchPoint.x = Math.abs(cornerX - f2);

				float f3 = Math.abs(cornerX - touchPoint.x)
						* Math.abs(cornerY - touchPoint.y) / f1;
				touchPoint.y = Math.abs(cornerY - f3);

				middleX = (touchPoint.x + cornerX) / 2;
				middleY = (touchPoint.y + cornerY) / 2;

				bezierControl1.x = middleX - (cornerY - middleY)
						* (cornerY - middleY) / (cornerX - middleX);
				bezierControl1.y = cornerY;

				bezierControl2.x = cornerX;
				bezierControl2.y = middleY - (cornerX - middleX)
						* (cornerX - middleX) / (cornerY - middleY);
				// Log.i("hmg", "mTouchX --> " + mTouch.x + "  mTouchY-->  "
				// + mTouch.y);
				// Log.i("hmg", "mBezierControl1.x--  " + mBezierControl1.x
				// + "  mBezierControl1.y -- " + mBezierControl1.y);
				// Log.i("hmg", "mBezierControl2.x -- " + mBezierControl2.x
				// + "  mBezierControl2.y -- " + mBezierControl2.y);
				bezierStart1.x = bezierControl1.x
						- (cornerX - bezierControl1.x) / 2;
			}
		}
		bezierStart2.x = cornerX;
		bezierStart2.y = bezierControl2.y - (cornerY - bezierControl2.y)
				/ 2;

		touchToCornerDis = (float) Math.hypot((touchPoint.x - cornerX),
				(touchPoint.y - cornerY));

		bezierEnd1 = getCross(touchPoint, bezierControl1, bezierStart1,
				bezierStart2);
		bezierEnd2 = getCross(touchPoint, bezierControl2, bezierStart1,
				bezierStart2);

		beziervertex1.x = (bezierStart1.x + 2 * bezierControl1.x + bezierEnd1.x) / 4;
		beziervertex1.y = (2 * bezierControl1.y + bezierStart1.y + bezierEnd1.y) / 4;
		beziervertex2.x = (bezierStart2.x + 2 * bezierControl2.x + bezierEnd2.x) / 4;
		beziervertex2.y = (2 * bezierControl2.y + bezierStart2.y + bezierEnd2.y) / 4;
	}

	/**
	 * 绘制当前页
	 */
	private void drawCurrentPageArea(Canvas canvas, Bitmap bitmap, Path path) {
		path0.reset();
		path0.moveTo(bezierStart1.x, bezierStart1.y);
		path0.quadTo(bezierControl1.x, bezierControl1.y, bezierEnd1.x,
				bezierEnd1.y);
		path0.lineTo(touchPoint.x, touchPoint.y);
		path0.lineTo(bezierEnd2.x, bezierEnd2.y);
		path0.quadTo(bezierControl2.x, bezierControl2.y, bezierStart2.x,
				bezierStart2.y);
		path0.lineTo(cornerX, cornerY);
		path0.close();

		canvas.save();
		canvas.clipPath(path, Region.Op.XOR);
		canvas.drawBitmap(bitmap, 0, 0, null);
		canvas.restore();

	}

	/**
	 * 绘制下一页 (或为此页码的上页，或为此页码的下页）
	 */
	private void drawNextPageAreaAndShadow(Canvas canvas, Bitmap bitmap) {
		path1.reset();
		path1.moveTo(bezierStart1.x, bezierStart1.y);
		path1.lineTo(beziervertex1.x, beziervertex1.y);
		path1.lineTo(beziervertex2.x, beziervertex2.y);
		path1.lineTo(bezierStart2.x, bezierStart2.y);
		path1.lineTo(cornerX, cornerY);
		path1.close();

		degrees = (float) Math.toDegrees(Math.atan2(bezierControl1.x
				- cornerX, bezierControl2.y - cornerY));
		int leftx;
		int rightx;
		GradientDrawable mBackShadowDrawable;
		if (isRTandLB) {
			leftx = (int) (bezierStart1.x);
			rightx = (int) (bezierStart1.x + touchToCornerDis / 4);
			mBackShadowDrawable = backShadowDrawableLR;
		} else {
			leftx = (int) (bezierStart1.x - touchToCornerDis / 4);
			rightx = (int) bezierStart1.x;
			mBackShadowDrawable = backShadowDrawableRL;
		}
		canvas.save();
		canvas.clipPath(path0);
		canvas.clipPath(path1, Region.Op.INTERSECT);
		canvas.drawBitmap(bitmap, 0, 0, null);
		canvas.rotate(degrees, bezierStart1.x, bezierStart1.y);
		mBackShadowDrawable.setBounds(leftx, (int) bezierStart1.y, rightx,
				(int) (maxLength + bezierStart1.y));
		mBackShadowDrawable.draw(canvas);
		canvas.restore();
	}

	/**
	 * 绘制翻起页的阴影
	 */
	private void drawCurrentPageShadow(Canvas canvas) {
		double degree;
		if (isRTandLB) {
			degree = Math.PI
					/ 4
					- Math.atan2(bezierControl1.y - touchPoint.y, touchPoint.x
							- bezierControl1.x);
		} else {
			degree = Math.PI
					/ 4
					- Math.atan2(touchPoint.y - bezierControl1.y, touchPoint.x
							- bezierControl1.x);
		}
		// 翻起页阴影顶点与touch点的距离
		double d1 = (float) 25 * 1.414 * Math.cos(degree);
		double d2 = (float) 25 * 1.414 * Math.sin(degree);
		float x = (float) (touchPoint.x + d1);
		float y;
		if (isRTandLB) {
			y = (float) (touchPoint.y + d2);
		} else {
			y = (float) (touchPoint.y - d2);
		}
		path1.reset();
		path1.moveTo(x, y);
		path1.lineTo(touchPoint.x, touchPoint.y);
		path1.lineTo(bezierControl1.x, bezierControl1.y);
		path1.lineTo(bezierStart1.x, bezierStart1.y);
		path1.close();
		float rotateDegrees;
		canvas.save();

		canvas.clipPath(path0, Region.Op.XOR);
		canvas.clipPath(path1, Region.Op.INTERSECT);
		int leftx;
		int rightx;
		GradientDrawable mCurrentPageShadow;
		if (isRTandLB) {
			leftx = (int) (bezierControl1.x);
			rightx = (int) bezierControl1.x + 25;
			mCurrentPageShadow = frontShadowDrawableVLR;
		} else {
			leftx = (int) (bezierControl1.x - 25);
			rightx = (int) bezierControl1.x + 1;
			mCurrentPageShadow = frontShadowDrawableVRL;
		}

		rotateDegrees = (float) Math.toDegrees(Math.atan2(touchPoint.x
				- bezierControl1.x, bezierControl1.y - touchPoint.y));
		canvas.rotate(rotateDegrees, bezierControl1.x, bezierControl1.y);
		mCurrentPageShadow.setBounds(leftx,
				(int) (bezierControl1.y - maxLength), rightx,
				(int) (bezierControl1.y));
		mCurrentPageShadow.draw(canvas);
		canvas.restore();

		path1.reset();
		path1.moveTo(x, y);
		path1.lineTo(touchPoint.x, touchPoint.y);
		path1.lineTo(bezierControl2.x, bezierControl2.y);
		path1.lineTo(bezierStart2.x, bezierStart2.y);
		path1.close();
		canvas.save();
		canvas.clipPath(path0, Region.Op.XOR);
		canvas.clipPath(path1, Region.Op.INTERSECT);
		if (isRTandLB) {
			leftx = (int) (bezierControl2.y);
			rightx = (int) (bezierControl2.y + 25);
			mCurrentPageShadow = frontShadowDrawableHTB;
		} else {
			leftx = (int) (bezierControl2.y - 25);
			rightx = (int) (bezierControl2.y + 1);
			mCurrentPageShadow = frontShadowDrawableHBT;
		}
		rotateDegrees = (float) Math.toDegrees(Math.atan2(bezierControl2.y
				- touchPoint.y, bezierControl2.x - touchPoint.x));
		canvas.rotate(rotateDegrees, bezierControl2.x, bezierControl2.y);
		float temp;
		if (bezierControl2.y < 0)
			temp = bezierControl2.y - mHeight;
		else
			temp = bezierControl2.y;

		int hmg = (int) Math.hypot(bezierControl2.x, temp);
		if (hmg > maxLength)
			mCurrentPageShadow
					.setBounds((int) (bezierControl2.x - 25) - hmg, leftx,
							(int) (bezierControl2.x + maxLength) - hmg,
							rightx);
		else
			mCurrentPageShadow.setBounds(
					(int) (bezierControl2.x - maxLength), leftx,
					(int) (bezierControl2.x), rightx);

		// Log.i("hmg", "mBezierControl2.x   " + mBezierControl2.x
		// + "  mBezierControl2.y  " + mBezierControl2.y);
		mCurrentPageShadow.draw(canvas);
		canvas.restore();
	}

	/**
	 * 绘制翻起页背面
	 */
	private void drawCurrentBackArea(Canvas canvas, Bitmap bitmap) {
		int i = (int) (bezierStart1.x + bezierControl1.x) / 2;
		float f1 = Math.abs(i - bezierControl1.x);
		int i1 = (int) (bezierStart2.y + bezierControl2.y) / 2;
		float f2 = Math.abs(i1 - bezierControl2.y);
		float f3 = Math.min(f1, f2);
		path1.reset();
		path1.moveTo(beziervertex2.x, beziervertex2.y);
		path1.lineTo(beziervertex1.x, beziervertex1.y);
		path1.lineTo(bezierEnd1.x, bezierEnd1.y);
		path1.lineTo(touchPoint.x, touchPoint.y);
		path1.lineTo(bezierEnd2.x, bezierEnd2.y);
		path1.close();
		GradientDrawable folderShadowDrawable;
		int left;
		int right;
		if (isRTandLB) {
			left = (int) (bezierStart1.x - 1);
			right = (int) (bezierStart1.x + f3 + 1);
			folderShadowDrawable = folderShadowDrawableLR;
		} else {
			left = (int) (bezierStart1.x - f3 - 1);
			right = (int) (bezierStart1.x + 1);
			folderShadowDrawable = folderShadowDrawableRL;
		}
		canvas.save();
		canvas.clipPath(path0);
		canvas.clipPath(path1, Region.Op.INTERSECT);

		buf_paint.setColorFilter(colorMatrixFilter);

		float dis = (float) Math.hypot(cornerX - bezierControl1.x,
				bezierControl2.y - cornerY);
		float f8 = (cornerX - bezierControl1.x) / dis;
		float f9 = (bezierControl2.y - cornerY) / dis;
		matrixArray[0] = 1 - 2 * f9 * f9;
		matrixArray[1] = 2 * f8 * f9;
		matrixArray[3] = matrixArray[1];
		matrixArray[4] = 1 - 2 * f8 * f8;
		matrix.reset();
		matrix.setValues(matrixArray);
		matrix.preTranslate(-bezierControl1.x, -bezierControl1.y);
		matrix.postTranslate(bezierControl1.x, bezierControl1.y);
		canvas.drawBitmap(bitmap, matrix, buf_paint);
		// canvas.drawBitmap(bitmap, mMatrix, null);
		buf_paint.setColorFilter(null);
		canvas.rotate(degrees, bezierStart1.x, bezierStart1.y);
		folderShadowDrawable.setBounds(left, (int) bezierStart1.y, right,
				(int) (bezierStart1.y + maxLength));
		folderShadowDrawable.draw(canvas);
		canvas.restore();
	}

	public void computeScroll() {
		super.computeScroll();
		if (scroller.computeScrollOffset()) {
            float x = scroller.getCurrX();
			float y = scroller.getCurrY();
			touchPoint.x = x;
			touchPoint.y = y;
			postInvalidate();
		}
	}

	private void startAnimation(int delayMillis) {
		int dx, dy;
		// dx 水平方向滑动的距离，负值会使滚动向左滚动
		// dy 垂直方向滑动的距离，负值会使滚动向上滚动
		if (cornerX > 0) {
			dx = -(int) (mWidth + touchPoint.x);
		} else {
			dx = (int) (mWidth - touchPoint.x + mWidth);
		}
		if (cornerY > 0) {
			dy = (int) (mHeight - touchPoint.y);
		} else {
			dy = (int) (1 - touchPoint.y); // 防止mTouch.y最终变为0
		}
		scroller.startScroll((int) touchPoint.x, (int) touchPoint.y, dx, dy,
				delayMillis);
	}

	public void abortAnimation() {
		if (!scroller.isFinished()) {
			scroller.abortAnimation();
		}
	}

	public boolean canDragOver() {
		if (touchToCornerDis > mWidth / 10)
			return true;
		return false;
	}

}

