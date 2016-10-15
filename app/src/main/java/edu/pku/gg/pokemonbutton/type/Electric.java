package edu.pku.gg.pokemonbutton.type;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;

import edu.pku.gg.pokemonbutton.TypeView;
import edu.pku.gg.pokemonbutton.Utils;

/**
 * Created by Gg on 2016/10/12.
 */
public class Electric extends TypeView{

    private ArgbEvaluator argbEvaluator = new ArgbEvaluator();

    private int COLOR_1 = 0xFFEDE574;
    private int COLOR_2 = 0xFFE1F5C4;
    private int COLOR_3 = 0xFFFDC345;

    private Paint paintElectric;

    private Path pathElectric = new Path();


    public Electric(Context context) {
        super(context);
    }

    public Electric(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Electric(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void init() {

        paintElectric = new Paint();
        paintElectric.setAntiAlias(true);
        paintElectric.setStyle((Paint.Style.FILL));
    }

    @Override
    protected void drawType(float cx, float cy, float radius, Canvas canvas) {
        pathElectric.reset();

        pathElectric.moveTo(cx - radius/2,cy - radius/4);
        pathElectric.lineTo(cx + radius,cy - 3*radius/4);
        pathElectric.lineTo(cx + radius/2,cy + radius/4);
        pathElectric.lineTo(cx + 3*radius/4,cy + 3*radius/8);
        pathElectric.lineTo(cx - 3*radius/4,cy + 7*radius/8);
        pathElectric.lineTo(cx - radius/4,cy);
        pathElectric.lineTo(cx - radius/2,cy - radius/4);

        canvas.drawPath(pathElectric,paintElectric);
    }

    @Override
    protected void updateTypesPaints(float currentProgress) {
        if (currentProgress < 0.5f) {
            float progress = (float) Utils.mapValueFromRangeToRange(currentProgress, 0f, 0.5f, 0, 1f);
            paintElectric.setColor((Integer) argbEvaluator.evaluate(progress, COLOR_1, COLOR_2));
        } else {
            float progress = (float) Utils.mapValueFromRangeToRange(currentProgress, 0.5f, 1f, 0, 1f);
            paintElectric.setColor((Integer) argbEvaluator.evaluate(progress, COLOR_2, COLOR_3));
        }
    }

    @Override
    protected void updateTypesAlpha(int alpha) {
        paintElectric.setAlpha(alpha);
    }
}
