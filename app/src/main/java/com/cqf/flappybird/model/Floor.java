package com.cqf.flappybird.model;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader.TileMode;

/**
 * Created by roy on 16/3/15.
 */
public class Floor {
    /*
    * 地板位置游戏面板高度的4/5到底部
    */
    private static final float FLOOR_Y_POS_RADIO = 4 / 5F; // height of 4/5

    /**
     * x坐标
     */
    private int x;
    /**
     * y坐标
     */
    private int y;
    /**
     * 填充物
     */
    private BitmapShader mFloorShader;

    private int mGameWidth;

    private int mGameHeight;

    public Floor(int gameWidth, int gameHeight, Bitmap floorBg) {
        mGameWidth = gameWidth;
        mGameHeight = gameHeight;
        y = (int) (gameHeight * FLOOR_Y_POS_RADIO);
        mFloorShader = new BitmapShader(floorBg, TileMode.REPEAT,
                TileMode.CLAMP);
    }

    /**
     * 绘制自己
     *
     * @param mCanvas
     * @param mPaint
     */
    public void draw(Canvas mCanvas, Paint mPaint) {
        Matrix shaderMatrix = new Matrix();
        shaderMatrix.postTranslate(x, y);
        mFloorShader.setLocalMatrix(shaderMatrix);
        if (-x > mGameWidth) {
            x = x % mGameWidth;
        }
        mCanvas.save(Canvas.MATRIX_SAVE_FLAG);
        //移动到指定的位置
        mPaint.setShader(mFloorShader);
        mCanvas.drawRect(0, y, mGameWidth, mGameHeight, mPaint);
        mCanvas.restore();
        mPaint.setShader(null);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
