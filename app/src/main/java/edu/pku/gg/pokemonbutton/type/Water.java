package edu.pku.gg.pokemonbutton.type;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

import edu.pku.gg.pokemonbutton.TypeView;
import edu.pku.gg.pokemonbutton.Utils;

/**
 * Created by Gg on 2016/10/12.
 */
public class Water extends TypeView {

    private ArgbEvaluator argbEvaluator = new ArgbEvaluator();

    private int COLOR_1 = 0xFF4CB8C4;
    private int COLOR_2 = 0xFF3CD3AD;
    private int COLOR_3 = 0xFF58C3E0;

    private Paint paintWater;

    private Path pathWater = new Path();

    public Water(Context context) {
        super(context);
    }

    public Water(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Water(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init() {
        paintWater = new Paint();
        paintWater.setAntiAlias(true);
        paintWater.setStyle((Paint.Style.FILL));
    }

    @Override
    protected void drawType(float cx, float cy, float radius, Canvas canvas) {
        pathWater.reset();

        pathWater.moveTo(cx,cy - radius);
        pathWater.lineTo(cx - 2*radius/3, cy);
        pathWater.arcTo(new RectF(cx-2*radius/3,cy-2*radius/3,cx+2*radius/3,cy+2*radius/3),180,-180);
        pathWater.lineTo(cx,cy-radius);
        pathWater.close();

        canvas.drawPath(pathWater,paintWater);
    }

    @Override
    protected void updateTypesPaints(float currentProgress) {
        if (currentProgress < 0.5f) {
            float progress = (float) Utils.mapValueFromRangeToRange(currentProgress, 0f, 0.5f, 0, 1f);
            paintWater.setColor((Integer) argbEvaluator.evaluate(progress, COLOR_1, COLOR_2));
        } else {
            float progress = (float) Utils.mapValueFromRangeToRange(currentProgress, 0.5f, 1f, 0, 1f);
            paintWater.setColor((Integer) argbEvaluator.evaluate(progress, COLOR_2, COLOR_3));
        }
    }

    @Override
    protected void updateTypesAlpha(int alpha) {
        paintWater.setAlpha(alpha);
    }
}
