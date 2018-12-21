package com.example.yasse.wain_nakul.ui.classes;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;

public class HorizontalDotsProgress extends View{
    private static final String TAG = "HorizontalDotsProgress";
    private Paint paint = new Paint();
    private int bounceDotRadius = 11;
    private int dotPosition;
    private int dotAmount = 5;

    public HorizontalDotsProgress(Context context){
        super(context);
    }

    public HorizontalDotsProgress(Context context, AttributeSet attribute){
        super(context, attribute);
    }

    public HorizontalDotsProgress(Context context, AttributeSet attribute, int defaultStyle){
        super(context, attribute, defaultStyle);
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        paint.setColor(Color.parseColor("#FFFFFF"));
        createDot(canvas, paint);
    }

    @Override
    protected void onAttachedToWindow(){
        super.onAttachedToWindow();
        startAnimation();
    }

    private void createDot(Canvas canvas, Paint paint){
        for (int i = 0; i < dotAmount; i++){
            if (i == dotPosition){
                canvas.drawCircle(11 + (i * 69), bounceDotRadius, bounceDotRadius, paint);
            } else{
                int dotRadius = 3;
                canvas.drawCircle(11 + (i * 69), bounceDotRadius, dotRadius, paint);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width;
        int height;

        width = (44 * 7);
        height = (bounceDotRadius * 2);

        setMeasuredDimension(width, height);
    }

    private void startAnimation(){
        BounceAnimation bounceAnimation = new BounceAnimation();
        bounceAnimation.setDuration(100);
        bounceAnimation.setRepeatCount(Animation.INFINITE);
        bounceAnimation.setInterpolator(new LinearInterpolator());
        bounceAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                dotPosition++;

                if (dotPosition == dotAmount){
                    dotPosition = 0;
                }
                Log.d(TAG, "Check out deez animations: ----On Animation Repeat----");
            }
        });
        startAnimation(bounceAnimation);
    }

    private class BounceAnimation extends Animation {
        @Override
        protected void applyTransformation(float interpolatedTime, Transformation transformation){
            super.applyTransformation(interpolatedTime, transformation);
            invalidate();
        }
    }
}
