package edu.pku.gg.pokemonbutton;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Property;
import android.view.View;

/**
 * Created by Miroslaw Stanek on 21.12.2015.
 * Modified by Joel Dean
 * Modified by Gg on 12.10.2016
 */
public abstract class TypeView extends View {
    private static final int TYPES_COUNT = 7;
    private static final int OUTER_TYPES_POSITION_ANGLE = 51;

    private int width = 0;
    private int height = 0;
    
    private int centerX;
    private int centerY;

    private float maxOuterTypesRadius;
    private float maxInnerTypesRadius;
    private float maxTypeSize;

    private float currentProgress = 0;

    private float currentRadius1 = 0;
    private float currentTypeSize1 = 0;

    private float currentTypeSize2 = 0;
    private float currentRadius2 = 0;

    private ArgbEvaluator argbEvaluator = new ArgbEvaluator();

    public TypeView(Context context) {
        super(context);
        init();
    }

    public TypeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TypeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    protected abstract void init();


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = w / 2;
        centerY = h / 2;
        maxTypeSize = w / 20;
        maxOuterTypesRadius = w / 2 - maxTypeSize * 2;
        maxInnerTypesRadius = 0.8f * maxOuterTypesRadius;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawOuterTypesFrame(canvas);
        drawInnerTypesFrame(canvas);
    }

    private void drawOuterTypesFrame(Canvas canvas) {
        for (int i = 0; i < TYPES_COUNT; i++) {
            int cX = (int) (centerX + currentRadius1 * Math.cos(i * OUTER_TYPES_POSITION_ANGLE * Math.PI / 180));
            int cY = (int) (centerY + currentRadius1 * Math.sin(i * OUTER_TYPES_POSITION_ANGLE * Math.PI / 180));
            drawType(cX, cY, currentTypeSize1, canvas);
        }
    }

    private void drawInnerTypesFrame(Canvas canvas) {
        for (int i = 0; i < TYPES_COUNT; i++) {
            int cX = (int) (centerX + currentRadius2 * Math.cos((i * OUTER_TYPES_POSITION_ANGLE - 10) * Math.PI / 180));
            int cY = (int) (centerY + currentRadius2 * Math.sin((i * OUTER_TYPES_POSITION_ANGLE - 10) * Math.PI / 180));
            drawType(cX, cY, currentTypeSize2, canvas);
        }
    }

    protected abstract void drawType(float cx, float cy, float radius, Canvas canvas);

    public void setCurrentProgress(float currentProgress) {
        this.currentProgress = currentProgress;

        updateInnerTypesPosition();
        updateOuterTypesPosition();

        updateTypesPaints(currentProgress);

        float AlphaProgress = (float) Utils.clamp(currentProgress, 0.6f, 1f);
        int alpha = (int) Utils.mapValueFromRangeToRange(AlphaProgress, 0.6f, 1f, 255, 0);

        updateTypesAlpha(alpha);

        postInvalidate();
    }

    public float getCurrentProgress() {
        return currentProgress;
    }

    private void updateInnerTypesPosition() {
        if (currentProgress < 0.3f) {
            this.currentRadius2 = (float) Utils.mapValueFromRangeToRange(currentProgress, 0, 0.3f, 0.f, maxInnerTypesRadius);
        } else {
            this.currentRadius2 = maxInnerTypesRadius;
        }
        if (currentProgress == 0) {
            this.currentTypeSize2 = 0;
        } else if (currentProgress < 0.2) {
            this.currentTypeSize2 = maxTypeSize;
        } else if (currentProgress < 0.5) {
            this.currentTypeSize2 = (float) Utils.mapValueFromRangeToRange(currentProgress, 0.2f, 0.5f, maxTypeSize, 0.3 * maxTypeSize);
        } else {
            this.currentTypeSize2 = (float) Utils.mapValueFromRangeToRange(currentProgress, 0.5f, 1f, maxTypeSize * 0.3f, 0);
        }

    }

    private void updateOuterTypesPosition() {
        if (currentProgress < 0.3f) {
            this.currentRadius1 = (float) Utils.mapValueFromRangeToRange(currentProgress, 0.0f, 0.3f, 0, maxOuterTypesRadius * 0.8f);
        } else {
            this.currentRadius1 = (float) Utils.mapValueFromRangeToRange(currentProgress, 0.3f, 1f, 0.8f * maxOuterTypesRadius, maxOuterTypesRadius);
        }
        if (currentProgress == 0) {
            this.currentTypeSize1 = 0;
        } else if (currentProgress < 0.7) {
            this.currentTypeSize1 = maxTypeSize;
        } else {
            this.currentTypeSize1 = (float) Utils.mapValueFromRangeToRange(currentProgress, 0.7f, 1f, maxTypeSize, 0);
        }
    }

    protected abstract void updateTypesPaints(float currentProgress);

    protected abstract void updateTypesAlpha(int alpha);

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


    public static final Property<TypeView, Float> TYPES_PROGRESS = new Property<TypeView, Float>(Float.class, "typesProgress") {
        @Override
        public Float get(TypeView object) {
            return object.getCurrentProgress();
        }

        @Override
        public void set(TypeView object, Float value) {
            object.setCurrentProgress(value);
        }
    };
}