package com.cqf.flappybird;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by roy on 16/3/15.
 */
public class Util {
    public static int dp2px(Context context, float dp) {
        int px = Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources()
                        .getDisplayMetrics()));
        return px;
    }
}
