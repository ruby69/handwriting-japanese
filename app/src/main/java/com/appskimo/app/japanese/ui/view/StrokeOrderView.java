package com.appskimo.app.japanese.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.appskimo.app.japanese.R;
import com.appskimo.app.japanese.domain.Word;

import java.util.List;

public class StrokeOrderView extends View {
    private Paint pencilPaint;

    private Matrix matrix = new Matrix();
    private List<Word.OrderPoint> orders;

    public StrokeOrderView(Context context) {
        super(context);
        init(context);
    }

    public StrokeOrderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        pencilPaint = new Paint();
        pencilPaint.setAntiAlias(true);
        pencilPaint.setDither(true);
        pencilPaint.setColor(context.getResources().getColor(R.color.black_trans20));
        pencilPaint.setStyle(Paint.Style.STROKE);
        pencilPaint.setStrokeJoin(Paint.Join.ROUND);
        pencilPaint.setStrokeCap(Paint.Cap.ROUND);
        pencilPaint.setStyle(Paint.Style.FILL);
        pencilPaint.setTextSize(5);
    }

    public void onDraw(Canvas canvas) {
        canvas.setMatrix(matrix);
        if (orders != null && !orders.isEmpty()) {
            for (Word.OrderPoint op : orders) {
                canvas.drawText(String.valueOf(op.getOrder()), op.getX(), op.getY(), pencilPaint);
            }
        }
    }

    public void drawOrder(List<Word.OrderPoint> orders, Matrix scaleMatrix, Matrix translateMatrix) {
        this.orders = orders;
        matrix.setConcat(translateMatrix, scaleMatrix);
        matrix = scaleMatrix;
        invalidate();
    }
}