package com.cqf.flappybird.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;

import com.cqf.flappybird.Util;

/**
 * Created by roy on 16/3/15.
 */
public class Bird {
    /**
     * 鸟在屏幕高度的2/3位置
     */
    private static final float RADIO_POS_HEIGHT = 1 / 2F;
    /**
     * 鸟的宽度 30dp
     */
    private static final int BIRD_SIZE = 30;
    private final int mGameHeight;

    /**
     * 鸟的横坐标
     */
    private int x;
    /**
     * 鸟的纵坐标
     */
    private int y;
    /**
     * 鸟的宽度
     */
    private int mWidth;
    /**
     * 鸟的高度
     */
    private int mHeight;

    /**
     * 鸟的bitmap
     */
    private Bitmap bitmap;
    /**
     * 鸟绘制的范围
     */
    private RectF rect = new RectF();

    public Bird(Context context, int gameWith, int gameHeight, Bitmap bitmap) {

        this.bitmap = bitmap;
        //鸟的位置
        x = gameWith / 2 - bitmap.getWidth() / 2;
        y = (int) (gameHeight * RADIO_POS_HEIGHT);

        mGameHeight = gameHeight;

        // 计算鸟的宽度和高度
        mWidth = Util.dp2px(context, BIRD_SIZE);
        mHeight = (int) (mWidth * 1.0f / bitmap.getWidth() * bitmap.getHeight());
    }

    /**
     * 绘制自己
     *
     * @param canvas
     */
    public void draw(Canvas canvas) {
        rect.set(x, y, x + mWidth, y + mHeight);
        canvas.drawBitmap(bitmap, null, rect, null);
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void resetHeigt() {
        y = (int) (mGameHeight * RADIO_POS_HEIGHT);
    }
}
