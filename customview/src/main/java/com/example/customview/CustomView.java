package com.example.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by louis on 18-1-4.
 */

public class CustomView extends View {
    private int DEFAULT_LENGTH = 800;

    public CustomView(Context context) {
        this(context, null);
    }

    public CustomView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            // 宽和高都是wrap_content
            setMeasuredDimension(DEFAULT_LENGTH, DEFAULT_LENGTH);
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(DEFAULT_LENGTH, heightSpecSize);
        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSpecSize, DEFAULT_LENGTH);
        }


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(5);//设置画笔宽度
        paint.setStyle(Paint.Style.STROKE);
//        paint.setShadowLayer(10, 15, 15, Color.GREEN);//设置阴影

        float[] pts = {10, 10, 100, 100, 200, 200, 400, 400, 500, 500, 600, 600};

        //设置画布背景颜色
        canvas.drawRGB(255, 255, 255);
//        canvas.drawCircle(190, 200, 150, paint);
//        canvas.drawLine(100, 100, 200, 200, paint);
//        canvas.drawLines(pts, paint);

//        RectF rect1 = new RectF(100, 10, 300, 100);
//        canvas.drawArc(rect1, 0, 270, true, paint);
//        canvas.drawRect(rect1, paint);


//        Path path = new Path();
//        path.moveTo(10, 10);
//        path.lineTo(100, 100);
//        path.lineTo(50,100);
//        path.close();
//        canvas.drawPath(path, paint);

//        Path CCWRecPath = new Path();//逆时针画rect
//        RectF rect1 = new RectF(400, 300, 600, 400);
//        CCWRecPath.addRect(rect1, Path.Direction.CCW);
//        canvas.drawPath(CCWRecPath, paint);
//
////         依据路径生成文字
//        paint.setTextSize(40);
//        paint.setColor(Color.BLACK);
//        canvas.drawTextOnPath("风萧萧兮易水寒", CCWRecPath, 0, 20, paint);

        paint.setTextSize(80);//设置文字大小
        //绘图样式，设置为填充
        paint.setStyle(Paint.Style.FILL);
        canvas.drawText("欢迎光临Harvic的博客", 10,100, paint);


    }
}
