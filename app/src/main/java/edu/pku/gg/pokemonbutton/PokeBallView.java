package edu.pku.gg.pokemonbutton;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.util.Property;
import android.view.View;

/**
 * Created by Miroslaw Stanek on 21.12.2015.
 * Modified by Gg on 12.10.2016.
 */

public class PokeBallView extends View {

    private int TOP_HALF_BALL_COLOR = 0xFFED1B28;  //红色
    private int BOTTOM_HALF_BALL_COLOR = 0xFFFEFEFE;  //白色
    private int BLACK_COLOR = 0xFF214261;  //蓝黑色

    private ArgbEvaluator argbEvaluator = new ArgbEvaluator();
    

    private Paint paintBottomBall = new Paint();
    private Paint paintTopBall = new Paint();
    private Paint paintBlack = new Paint();

    private Path pathBottomBall = new Path();
    private Path pathTopBall = new Path();
    private Path pathBlack = new Path();

    private Paint maskPaint = new Paint();


    private Bitmap tempBitmap;
    private Canvas tempCanvas;

    private float outerCircleRadiusProgress = 0f;
    private float innerCircleRadiusProgress = 0f;

    private int width = 0;
    private int height = 0;

    private int maxCircleSize;

    public PokeBallView(Context context) {
        super(context);
        init();
    }

    public PokeBallView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PokeBallView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {

        paintTopBall.setAntiAlias(true);
        paintTopBall.setStyle(Paint.Style.FILL);

        paintBottomBall.setAntiAlias(true);
        paintBottomBall.setStyle(Paint.Style.FILL);

        paintBlack.setAntiAlias(true);
        paintBlack.setStyle(Paint.Style.STROKE);
        
        maskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
        invalidate();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (width != 0 && height != 0)
            setMeasuredDimension(width, height);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        paintBlack.setStrokeWidth(w/10);
        maxCircleSize = w / 2 - (int)paintBlack.getStrokeWidth();
        tempBitmap = Bitmap.createBitmap(getWidth(), getWidth(), Bitmap.Config.ARGB_8888);
        tempCanvas = new Canvas(tempBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        tempCanvas.drawColor(0xffffff, PorterDuff.Mode.CLEAR);

        drawPokeBall(getWidth() / 2, getHeight() / 2, outerCircleRadiusProgress * maxCircleSize,tempCanvas);
        tempCanvas.drawCircle(getWidth() / 2, getHeight() / 2, innerCircleRadiusProgress * maxCircleSize + 1, maskPaint);
        canvas.drawBitmap(tempBitmap, 0, 0, null);
    }


    public void drawPokeBall(float cx, float cy, float radius, Canvas canvas){
        RectF outerBall = new RectF(cx-radius, cy-radius, cx+radius, cy+radius);

        RectF innerBall = new RectF(cx-radius/3,cy-radius/3,cx + radius/3,cy + radius/3);

        pathTopBall.reset();
        pathBlack.reset();
        pathBottomBall.reset();

        pathTopBall.moveTo(cx - radius,cy);
        pathTopBall.lineTo(cx - radius/3,cy);
        pathTopBall.arcTo(new RectF(innerBall),180,180);
        pathTopBall.lineTo(cx + radius,cy);
        pathTopBall.arcTo(new RectF(outerBall),0,-180);
        pathTopBall.close();

        pathBottomBall.moveTo(cx - radius, cy);
        pathBottomBall.lineTo(cx - radius/3, cy);
        pathBottomBall.arcTo(new RectF(innerBall),180,-180);
        pathBottomBall.lineTo(cx + radius,cy);
        pathBottomBall.arcTo(new RectF(outerBall),0,180);
        pathBottomBall.close();

        pathBlack.moveTo(cx - radius, cy);
        pathBlack.lineTo(cx - radius/3,cy);
        pathBlack.moveTo(cx + radius/3, cy);
        pathBlack.lineTo(cx + radius, cy);

        canvas.drawPath(pathTopBall,paintTopBall);
        canvas.drawPath(pathBottomBall,paintBottomBall);
        canvas.drawPath(pathBlack,paintBlack);

        canvas.drawCircle(cx,cy,radius/3,paintBlack);

    }


    public void setInnerCircleRadiusProgress(float innerCircleRadiusProgress) {
        this.innerCircleRadiusProgress = innerCircleRadiusProgress;
        postInvalidate();
    }

    public float getInnerCircleRadiusProgress() {
        return innerCircleRadiusProgress;
    }

    public void setOuterCircleRadiusProgress(float outerCircleRadiusProgress) {
        this.outerCircleRadiusProgress = outerCircleRadiusProgress;
        updateCircleColor();
        postInvalidate();
    }

    private void updateCircleColor() {
        float colorProgress = (float) Utils.clamp(outerCircleRadiusProgress, 0.5, 1);
        colorProgress = (float) Utils.mapValueFromRangeToRange(colorProgress, 0.5f, 1f, 0f, 1f);
        this.paintTopBall.setColor((Integer) argbEvaluator.evaluate(colorProgress, 0, TOP_HALF_BALL_COLOR));
        this.paintBottomBall.setColor((Integer) argbEvaluator.evaluate(colorProgress, 0, BOTTOM_HALF_BALL_COLOR));
        this.paintBlack.setColor((Integer) argbEvaluator.evaluate(colorProgress, 0, BLACK_COLOR));
    }

    public float getOuterCircleRadiusProgress() {
        return outerCircleRadiusProgress;
    }

    public static final Property<PokeBallView, Float> INNER_CIRCLE_RADIUS_PROGRESS =
            new Property<PokeBallView, Float>(Float.class, "innerCircleRadiusProgress") {
                @Override
                public Float get(PokeBallView object) {
                    return object.getInnerCircleRadiusProgress();
                }

                @Override
                public void set(PokeBallView object, Float value) {
                    object.setInnerCircleRadiusProgress(value);
                }
            };

    public static final Property<PokeBallView, Float> OUTER_CIRCLE_RADIUS_PROGRESS =
            new Property<PokeBallView, Float>(Float.class, "outerCircleRadiusProgress") {
                @Override
                public Float get(PokeBallView object) {
                    return object.getOuterCircleRadiusProgress();
                }

                @Override
                public void set(PokeBallView object, Float value) {
                    object.setOuterCircleRadiusProgress(value);
                }
            };

    public void setTopHalfBallColor(@ColorInt int color) {
        TOP_HALF_BALL_COLOR = color;
        invalidate();
    }


}
