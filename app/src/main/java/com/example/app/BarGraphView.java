package com.example.app;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.text.DecimalFormat;

/**
 * Created by Steven on 2015-03-15.
 */
public class BarGraphView extends View{
    private double mBarValue;
    private String mLabelText;
    private int mBarColor;
    private int mTextColor;
    private String[] mAllTexts;
    private int mSmallestTextSize;
    private DecimalFormat mDecimalFormat = new DecimalFormat("#.##");

    private Paint mTextPaint, mBarPaint;
    private int mBarHeight;
    private float mTextHeight, mTextWidth, mNumberHeight, mNumberWidth;

    public BarGraphView (Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.BarGraphView,
                0, 0);

        try {
            mLabelText = a.getString(R.styleable.BarGraphView_label_text);
            mBarValue = a.getFloat(R.styleable.BarGraphView_bar_value, 0.0f);
            mBarColor = a.getColor(R.styleable.BarGraphView_bar_color, Color.rgb(52, 152, 219));
            mTextColor = a.getColor(R.styleable.BarGraphView_text_color, Color.BLACK);

            final int id = a.getResourceId(R.styleable.BarGraphView_text_array, 0);
            if (id != 0) {
                mAllTexts =getResources().getStringArray(id);
            }
        } finally {
            a.recycle();
        }

        setupPaints();
    }

    private void setupPaints() {
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(mTextColor);

        mBarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBarPaint.setStyle(Paint.Style.FILL);
        mBarPaint.setColor(mBarColor);
    }

    public String getLabelText() {
        return mLabelText;
    }

    public void setLablText(String labelText) {
        mLabelText = mLabelText;
        invalidate();
        requestLayout();
    }

    public double getBarValue() {
        return mBarValue;
    }

    public void setBarValue(double barValue) {
        mBarValue = barValue;
        invalidate();
        requestLayout();
    }

    public void setBarColor(int color) {
        mBarColor = color;
        invalidate();
        requestLayout();
    }

    public void setTextColor(int color) {
        mTextColor = color;
        invalidate();
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int minw = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();
        int w = resolveSizeAndState(minw, widthMeasureSpec, 1);

        int minh = MeasureSpec.getSize(w) + getPaddingBottom() + getPaddingTop();
        int h = resolveSizeAndState(MeasureSpec.getSize(w), heightMeasureSpec, 0);

        Rect textBounds = new Rect();
        for (String s: mAllTexts) {
            findSmallestTextSize(s, textBounds, w - getPaddingRight() - getPaddingLeft(), 200);
        }

        mTextPaint.setTextSize(mSmallestTextSize);

        mTextPaint.getTextBounds(mDecimalFormat.format(mBarValue), 0, mDecimalFormat.format(mBarValue).length(), textBounds);
        mNumberWidth = textBounds.width();
        mNumberHeight = textBounds.height();

        mTextPaint.getTextBounds(mLabelText, 0, mLabelText.length(), textBounds);
        mTextWidth = textBounds.width();
        mBarHeight = (int) (((h - mTextHeight - (getPaddingTop()*3) - (getPaddingBottom()*2) - mNumberHeight) * (mBarValue/10.0)) + mNumberHeight + getPaddingBottom() + getPaddingTop());
        setMeasuredDimension(w, h);
    }

    private void findSmallestTextSize(String text, Rect rect, int width, int textSize) {
        if (mSmallestTextSize != 0 && mSmallestTextSize > 30 && textSize > mSmallestTextSize) {
            textSize = mSmallestTextSize;
        }

        mTextPaint.setTextSize(textSize);
        mTextPaint.getTextBounds(text, 0, text.length(), rect);
        if (textSize > 30 && rect.width() >= width) {
            findSmallestTextSize(text, rect, width, textSize - 1);
        } else if (textSize > 30 && (mSmallestTextSize == 0 || textSize < mSmallestTextSize)){
            mSmallestTextSize = textSize;
            if (mTextHeight == 0 || rect.height() > mTextHeight) {
                mTextHeight = rect.height();
            }
        }
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawRect(getPaddingLeft(),
                getHeight() - mTextHeight - getPaddingBottom() - getPaddingTop() - mBarHeight,
                getWidth() - getPaddingRight(),
                getHeight() - mTextHeight - getPaddingBottom(),
                mBarPaint);

        canvas.drawText(mLabelText, (getWidth() - mTextWidth)/2, getHeight() - getPaddingBottom(), mTextPaint);

        canvas.drawText(mDecimalFormat.format(mBarValue),
                (getWidth() - mNumberWidth)/2,
                getHeight() - mTextHeight - getPaddingBottom() - getPaddingBottom() - mBarHeight + mNumberHeight + getPaddingTop(),
                mTextPaint);

    }
}
