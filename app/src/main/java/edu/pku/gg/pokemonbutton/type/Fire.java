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
public class Fire extends TypeView {

    private ArgbEvaluator argbEvaluator = new ArgbEvaluator();

    private int COLOR_1 = 0xFFFF512F;
    private int COLOR_2 = 0xFFF09819;
    private int COLOR_3 = 0xFFFD7B1D;

    private Paint paintFire;

    private Path pathFire = new Path();

    public Fire(Context context) {
        super(context);
    }

    public Fire(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Fire(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init() {
        paintFire = new Paint();
        paintFire.setAntiAlias(true);
        paintFire.setStyle((Paint.Style.FILL));
    }

    @Override
    protected void drawType(float cx, float cy, float radius, Canvas canvas) {
        pathFire.reset();

        pathFire.moveTo(cx ,cy - radius);
        pathFire.arcTo(new RectF(cx - radius,cy - radius,cx + radius, cy + radius),-90, -180);
        pathFire.arcTo(new RectF(cx - radius/2,cy,cx + radius/2, cy + radius),90, -160);
        pathFire.quadTo(cx - radius/2,cy + radius,cx - radius/3, cy - radius/3);

        canvas.drawPath(pathFire,paintFire);
    }

    @Override
    protected void updateTypesPaints(float currentProgress) {
        if (currentProgress < 0.5f) {
            float progress = (float) Utils.mapValueFromRangeToRange(currentProgress, 0f, 0.5f, 0, 1f);
            paintFire.setColor((Integer) argbEvaluator.evaluate(progress, COLOR_1, COLOR_2));
        } else {
            float progress = (float) Utils.mapValueFromRangeToRange(currentProgress, 0.5f, 1f, 0, 1f);
            paintFire.setColor((Integer) argbEvaluator.evaluate(progress, COLOR_2, COLOR_3));
        }
    }

    @Override
    protected void updateTypesAlpha(int alpha) {
        paintFire.setAlpha(alpha);
    }
}
