package com.cqf.flappybird;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.cqf.flappybird.model.Bird;
import com.cqf.flappybird.model.Floor;
import com.cqf.flappybird.model.Pipe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by roy on 16/3/15.
 */
public class GameFlabbyBird extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private final Paint mPaint;
    private int mSpeed = Util.dp2px(getContext(), 2);
    private SurfaceHolder mHolder;
    private final int[] mNums = new int[]{R.mipmap.n0, R.mipmap.n1,
            R.mipmap.n2, R.mipmap.n3, R.mipmap.n4, R.mipmap.n5,
            R.mipmap.n6, R.mipmap.n7, R.mipmap.n8, R.mipmap.n9};
    private Bitmap[] mNumBitmap;
    /**
     * 与SurfaceHolder绑定的Canvas
     */
    private Canvas mCanvas;
    /**
     * 用于绘制的线程
     */
    private Thread t;
    /**
     * 线程的控制开关
     */
    private boolean isRunning;
    private Bitmap mFloorBg;
    private int mHeight;
    private int mWidth;
    private Floor mFloor;
    private Bitmap mBg;
    private RectF mGamePanelRect = new RectF();

    private Bitmap mBirdBitmap;
    /**
     * *********管道相关**********************
     */
    /**
     * 管道
     */
    private Bitmap mPipeTop;
    private Bitmap mPipeBottom;
    private RectF mPipeRect;
    private int mPipeWidth;
    private ArrayList<Pipe> mPipes = new ArrayList();
    private int mRemovedPipe = 0;
    /**
     * 管道的宽度 60dp
     */
    private static final int PIPE_WIDTH = 60;
    private Bird mBird;
    private int mAutoDownSpeed = Util.dp2px(getContext(), 2);
    private int mTmpBirdDis;

    /**
     * 两个管道间距离
     */
    private final int PIPE_DIS_BETWEEN_TWO = Util.dp2px(getContext(), 300);
    /**
     * 记录移动的距离，达到 PIPE_DIS_BETWEEN_TWO 则生成一个管道
     */
    private int mTmpMoveDistance;
    private int mGrade = 0;
    private int mSingleGradeWidth;
    private int mSingleGradeHeight;
    private RectF mSingleNumRectF;

    private enum GameStatus {
        WAITTING, RUNNING, STOP;
    }

    /**
     * 记录游戏的状态
     */
    private GameStatus mStatus = GameStatus.RUNNING;

    /**
     * 触摸上升的距离，因为是上升，所以为负值
     */
    private static final int TOUCH_UP_SIZE = -30;
    /**
     * 将上升的距离转化为px；这里多存储一个变量，变量在run中计算
     */
    private final int mBirdUpDis = Util.dp2px(getContext(), TOUCH_UP_SIZE);
    private static final float RADIO_SINGLE_NUM_HEIGHT = 1 / 15f;
    /**
     * 记录需要移除的管道
     */
    private List<Pipe> mNeedRemovePipe = new ArrayList<Pipe>();

    public GameFlabbyBird(Context context) {
        this(context, null);
    }

    public GameFlabbyBird(Context context, AttributeSet attrs) {
        super(context, attrs);
        mHolder = getHolder();
        mHolder.addCallback(this);

        setZOrderOnTop(true);// 设置画布 背景透明
        mHolder.setFormat(PixelFormat.TRANSLUCENT);

        // 设置可获得焦点
        setFocusable(true);
        setFocusableInTouchMode(true);
        // 设置常亮
        this.setKeepScreenOn(true);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);

        initBitmaps();

        // 初始化速度
        mPipeWidth = Util.dp2px(getContext(), PIPE_WIDTH);
    }

    private void initBitmaps() {
        mFloorBg = BitmapFactory.decodeResource(getResources(), R.mipmap.floor_bg2);
        mBg = BitmapFactory.decodeResource(getResources(), R.mipmap.bg1);
        mBirdBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.b1);
        mPipeTop = BitmapFactory.decodeResource(getResources(), R.mipmap.g2);
        mPipeBottom = BitmapFactory.decodeResource(getResources(), R.mipmap.g1);
        mNumBitmap = new Bitmap[mNums.length];
        for (int i = 0; i < mNumBitmap.length; i++) {
            mNumBitmap[i] = BitmapFactory.decodeResource(getResources(), mNums[i]);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        isRunning = true;
        t = new Thread(this);
        t.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isRunning = false;
    }

    @Override
    public void run() {
        while (isRunning) {
            long start = System.currentTimeMillis();
            logic();
            draw();
            long end = System.currentTimeMillis();
            try {
                if (end - start < 50) {
                    Thread.sleep(50 - (end - start));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void logic() {
        switch (mStatus) {
            case RUNNING:
                // 更新我们地板绘制的x坐标，地板移动
                mFloor.setX(mFloor.getX() - mSpeed);

                logicPipe();

                // 默认下落，点击时瞬间上升
                mTmpBirdDis += mAutoDownSpeed;
                mBird.setY(mBird.getY() + mAutoDownSpeed);

                for (Pipe pipe : mPipes) {
                    if (pipe.getX() + mPipeWidth < mBird.getX()) {
                    }
                }
                checkGameOver();
                break;
            case STOP: // 鸟落下
                // 如果鸟还在空中，先让它掉下来
                if (mBird.getY() < mFloor.getY() - mBird.getWidth())
                {
                    mTmpBirdDis += mAutoDownSpeed;
                    mBird.setY(mBird.getY() + mTmpBirdDis);
                } else
                {
                    mStatus = GameStatus.WAITTING;
                    initPos();
                }
                break;
            default:
                break;
        }
    }


    /**
     * 重置鸟的位置等数据
     */
    private void initPos()
    {
        mPipes.clear();
        //立即增加一个
        mPipes.add(new Pipe(getContext(), getWidth(), getHeight(), mPipeTop,
                mPipeBottom));
        mNeedRemovePipe.clear();
        // 重置鸟的位置
        // mBird.setY(mHeight * 2 / 3);
        mBird.resetHeigt();
        // 重置下落速度
        mTmpBirdDis = 0;
        mTmpMoveDistance = 0 ;
        mRemovedPipe = 0;
    }

    /**
     * 检查游戏是否GG
     */
    private void checkGameOver() {
        if (mBird.getY() > mFloor.getY() - mBird.getHeight())
        {
            mStatus = GameStatus.STOP;
        }
        // 如果撞到管道
        for (Pipe wall : mPipes)
        {
            // 已经穿过的
            if (wall.getX() + mPipeWidth < mBird.getX())
            {
                continue;
            }
            if (wall.touchBird(mBird))
            {
                mStatus = GameStatus.STOP;
                break;
            }
        }
    }

    /**
     * 生成管子
     */
    private void logicPipe() {
        for (Pipe pipe : mPipes) {
            if (pipe.getX() < -mPipeWidth) {
                mNeedRemovePipe.add(pipe);
                mRemovedPipe++;
                continue;
            }
            pipe.setX(pipe.getX() - mSpeed);
        }
        // 移除管道
        mPipes.removeAll(mNeedRemovePipe);
        mNeedRemovePipe.clear();

        // Log.e("TAG", "现存管道数量：" + mPipes.size());

        // 管道
        mTmpMoveDistance += mSpeed;
        // 生成一个管道
        if (mTmpMoveDistance >= PIPE_DIS_BETWEEN_TWO) {
            Pipe pipe = new Pipe(getContext(), getWidth(), getHeight(),
                    mPipeTop, mPipeBottom);
            mPipes.add(pipe);
            mTmpMoveDistance = 0;
        }
    }

    private void draw() {
        try {
            // 获得canvas
            mCanvas = mHolder.lockCanvas();
            if (mCanvas != null) {
                drawBg();
                drawPipes();
                drawFloor();
                drawBird();
                drawGrads();
                // 更新我们地板绘制的x坐标
                mFloor.setX(mFloor.getX() - mSpeed);
            }
        } catch (Exception e) {
        } finally {
            if (mCanvas != null)
                mHolder.unlockCanvasAndPost(mCanvas);
        }
    }

    private void drawGrads() {
        String grade = mGrade + "";
        mCanvas.save(Canvas.MATRIX_SAVE_FLAG);
        mCanvas.translate(mWidth / 2 - grade.length() * mSingleGradeWidth / 2,
                1f / 8 * mHeight);
        // draw single num one by one
        for (int i = 0; i < grade.length(); i++) {
            String numStr = grade.substring(i, i + 1);
            int num = Integer.valueOf(numStr);
            mCanvas.drawBitmap(mNumBitmap[num], null, mSingleNumRectF, null);
            mCanvas.translate(mSingleGradeWidth, 0);
        }
        mCanvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getAction();

        if (action == MotionEvent.ACTION_DOWN) {
            switch (mStatus) {
                case WAITTING:
                    mStatus = GameStatus.RUNNING;
                    break;
                case RUNNING:
                    mBird.setY(mBird.getY() + mBirdUpDis);
                    break;
            }

        }
        return true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mFloor = new Floor(mWidth, mHeight, mFloorBg);

        mGamePanelRect.set(0, 0, w, h);

        // 初始化管道范围
        mPipeRect = new RectF(0, 0, mPipeWidth, mHeight);
        mBird = new Bird(getContext(), mWidth, mHeight, mBirdBitmap);
        Pipe pipe = new Pipe(getContext(), w, h, mPipeTop, mPipeBottom);
        mPipes.add(pipe);

        mSingleGradeHeight = (int) (h * RADIO_SINGLE_NUM_HEIGHT);
        mSingleGradeWidth = (int) (mSingleGradeHeight * 1.0f
                / mNumBitmap[0].getHeight() * mNumBitmap[0].getWidth());
        mSingleNumRectF = new RectF(0, 0, mSingleGradeWidth, mSingleGradeHeight);
    }

    private void drawPipes() {
        for (Pipe pipe : mPipes) {
            pipe.setX(pipe.getX() - mSpeed);
            pipe.draw(mCanvas, mPipeRect);
        }
    }

    private void drawFloor() {
        mFloor.draw(mCanvas, mPaint);
    }

    private void drawBird() {
        mBird.draw(mCanvas);
    }

    private void drawBg() {
        mCanvas.drawBitmap(mBg, null, mGamePanelRect, null);
    }
}