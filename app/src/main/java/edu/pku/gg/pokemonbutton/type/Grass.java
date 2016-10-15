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
public class Grass extends TypeView{

    private ArgbEvaluator argbEvaluator = new ArgbEvaluator();

    private int COLOR_1 = 0xFF3CA55C;
    private int COLOR_2 = 0xFFB5AC49;
    private int COLOR_3 = 0xFF4BCB8E;

    private Paint paintGrass;

    private Path pathGrass = new Path();

    public Grass(Context context) {
        super(context);
    }

    public Grass(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Grass(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init() {

        paintGrass = new Paint();
        paintGrass.setAntiAlias(true);
        paintGrass.setStyle((Paint.Style.FILL));

    }

    @Override
    protected void drawType(float cx, float cy, float radius, Canvas canvas) {
        pathGrass.reset();

        pathGrass.moveTo(cx ,cy - radius);
        pathGrass.quadTo(cx - radius,cy,cx,cy + radius);
        pathGrass.quadTo(cx + radius,cy,cx,cy - radius);

        pathGrass.close();

        canvas.drawPath(pathGrass,paintGrass);

    }

    @Override
    protected void updateTypesPaints(float currentProgress) {
        if (currentProgress < 0.5f) {
            float progress = (float) Utils.mapValueFromRangeToRange(currentProgress, 0f, 0.5f, 0, 1f);
            paintGrass.setColor((Integer) argbEvaluator.evaluate(progress, COLOR_1, COLOR_2));
        } else {
            float progress = (float) Utils.mapValueFromRangeToRange(currentProgress, 0.5f, 1f, 0, 1f);
            paintGrass.setColor((Integer) argbEvaluator.evaluate(progress, COLOR_2, COLOR_3));
        }
    }

    @Override
    protected void updateTypesAlpha(int alpha) {
        paintGrass.setAlpha(alpha);
    }
}
