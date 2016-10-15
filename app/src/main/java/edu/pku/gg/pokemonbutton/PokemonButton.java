package edu.pku.gg.pokemonbutton;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;


import java.util.List;

import edu.pku.gg.pokemonbutton.type.Electric;
import edu.pku.gg.pokemonbutton.type.Fire;
import edu.pku.gg.pokemonbutton.type.Grass;
import edu.pku.gg.pokemonbutton.type.Water;


public class PokemonButton extends FrameLayout implements View.OnClickListener {
    private static final DecelerateInterpolator DECCELERATE_INTERPOLATOR = new DecelerateInterpolator();
    private static final AccelerateDecelerateInterpolator ACCELERATE_DECELERATE_INTERPOLATOR = new AccelerateDecelerateInterpolator();
    private static final OvershootInterpolator OVERSHOOT_INTERPOLATOR = new OvershootInterpolator(4);

    private ImageView icon;
    private TypeView typeView;
    private PokeBallView pokeBallView;
    private Pokemon currentPokemon;
    private PokemonType currentPokemonType;
    private OnCatchListener catchListener;
    private int topHalfBallColor;
    private int iconSize;


    private float animationScaleFactor;

    private boolean isChecked;


    private boolean isEnabled;
    private AnimatorSet animatorSet;

    private Drawable catchDrawable;
    private Drawable uncatchDrawable;

    public PokemonButton(Context context) {
        this(context, null);
    }

    public PokemonButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PokemonButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    /**
     * Does all the initial setup of the button such as retrieving all the attributes that were
     * set in xml and inflating the like button's view and initial state.
     * @param context
     * @param attrs
     * @param defStyle
     */
    private void init(Context context, AttributeSet attrs, int defStyle) {
        LayoutInflater.from(getContext()).inflate(R.layout.pokemon_view, this, true);
        icon = (ImageView) findViewById(R.id.icon);

        pokeBallView = (PokeBallView) findViewById(R.id.pokeball);

        final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.PokemonButton, defStyle, 0);

        iconSize = array.getDimensionPixelSize(R.styleable.PokemonButton_icon_size, -1);
        if (iconSize == -1){
            iconSize = 40;
        }

        String pokemonName = array.getString(R.styleable.PokemonButton_pokemon_name);
        String pokemonType = array.getString(R.styleable.PokemonButton_pokemon_type);

        catchDrawable = getDrawableFromResource(array, R.styleable.PokemonButton_pokemon_catched_drawable);

        if(catchDrawable!=null) {
            setCatchDrawable(catchDrawable);
        }

        uncatchDrawable = getDrawableFromResource(array, R.styleable.PokemonButton_pokemon_uncatched_drawable);

        if(uncatchDrawable !=null) {
            setUncatchDrawable(uncatchDrawable);
        }

        if (pokemonName != null) {
            if (!pokemonName.isEmpty()) {
                currentPokemon = parsePokemonName(pokemonName);
            }
        }


        topHalfBallColor = array.getColor(R.styleable.PokemonButton_top_half_ball_color, 0);

        if (topHalfBallColor != 0) {
            pokeBallView.setTopHalfBallColor(topHalfBallColor);
        }

        currentPokemonType = parsePokemonType(pokemonType);

        switch (currentPokemonType){
            case ELECTRIC:
                typeView = new Electric(getContext());
                break;
            case WATER:
                typeView = new Water(getContext());
                break;
            case FIRE:
                typeView = new Fire(getContext());
                break;
            case GRASS:
                typeView = new Grass(getContext());
                break;
            default:
                typeView = new Electric(getContext());
                break;
        }

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.addRule(Gravity.CENTER);
        this.addView(typeView,lp);

        if (catchDrawable == null && uncatchDrawable == null) {
            if (currentPokemon != null) {
                setCatchDrawableRes(currentPokemon.getOnIconResourceId());
                setUncatchDrawableRes(currentPokemon.getOffIconResourceId());
            } else {
                currentPokemon = parsePokemonName(PokemonName.Pikachu);
                setCatchDrawableRes(currentPokemon.getOnIconResourceId());
                setUncatchDrawableRes(currentPokemon.getOffIconResourceId());
            }
        }

        setEnabled(array.getBoolean(R.styleable.PokemonButton_is_enabled,true));
        Boolean status = array.getBoolean(R.styleable.PokemonButton_catched,false);
        setAnimationScaleFactor(array.getFloat(R.styleable.PokemonButton_anim_scale_factor,3));
        setCatched(status);
        setOnClickListener(this);
        array.recycle();
    }

    private Drawable getDrawableFromResource(TypedArray array, int styleableIndexId)
    {
        int id = array.getResourceId(styleableIndexId, -1);

        return (-1 != id) ? ContextCompat.getDrawable(getContext(), id) : null;
    }

    /**
     * This triggers the entire functionality of the button such as icon changes,
     * animations, listeners etc.
     * @param v
     */
    @Override
    public void onClick(View v) {

        if(!isEnabled)
            return;

        isChecked = !isChecked;

        icon.setImageDrawable(isChecked ? catchDrawable : uncatchDrawable);

        if (catchListener != null) {
            if (isChecked) {
                catchListener.catched(this);
            } else {
                catchListener.unCatched(this);
            }
        }

        if (animatorSet != null) {
            animatorSet.cancel();
        }

        if (isChecked) {
            icon.animate().cancel();
            icon.setScaleX(0);
            icon.setScaleY(0);
            pokeBallView.setInnerCircleRadiusProgress(0);
            pokeBallView.setOuterCircleRadiusProgress(0);
            typeView.setCurrentProgress(0);

            animatorSet = new AnimatorSet();

            ObjectAnimator outerCircleAnimator = ObjectAnimator.ofFloat(pokeBallView, pokeBallView.OUTER_CIRCLE_RADIUS_PROGRESS, 0.1f, 1f);
            outerCircleAnimator.setDuration(250);
            outerCircleAnimator.setInterpolator(DECCELERATE_INTERPOLATOR);

            ObjectAnimator innerCircleAnimator = ObjectAnimator.ofFloat(pokeBallView, pokeBallView.INNER_CIRCLE_RADIUS_PROGRESS, 0.1f, 1f);
            innerCircleAnimator.setDuration(200);
            innerCircleAnimator.setStartDelay(200);
            innerCircleAnimator.setInterpolator(DECCELERATE_INTERPOLATOR);

            ObjectAnimator starScaleYAnimator = ObjectAnimator.ofFloat(icon, ImageView.SCALE_Y, 0.2f, 1f);
            starScaleYAnimator.setDuration(350);
            starScaleYAnimator.setStartDelay(250);
            starScaleYAnimator.setInterpolator(OVERSHOOT_INTERPOLATOR);

            ObjectAnimator starScaleXAnimator = ObjectAnimator.ofFloat(icon, ImageView.SCALE_X, 0.2f, 1f);
            starScaleXAnimator.setDuration(350);
            starScaleXAnimator.setStartDelay(250);
            starScaleXAnimator.setInterpolator(OVERSHOOT_INTERPOLATOR);

            ObjectAnimator dotsAnimator = ObjectAnimator.ofFloat(typeView, typeView.TYPES_PROGRESS, 0, 1f);
            dotsAnimator.setDuration(900);
            dotsAnimator.setStartDelay(50);
            dotsAnimator.setInterpolator(ACCELERATE_DECELERATE_INTERPOLATOR);

            animatorSet.playTogether(
                    outerCircleAnimator,
                    innerCircleAnimator,
                    starScaleYAnimator,
                    starScaleXAnimator,
                    dotsAnimator
            );

            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationCancel(Animator animation) {
                    pokeBallView.setInnerCircleRadiusProgress(0);
                    pokeBallView.setOuterCircleRadiusProgress(0);
                    typeView.setCurrentProgress(0);
                    icon.setScaleX(1);
                    icon.setScaleY(1);
                }
            });

            animatorSet.start();
        }
    }

    /**
     * Used to trigger the scale animation that takes places on the
     * icon when the button is touched.
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(!isEnabled)
            return true;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                /*
                Commented out this line and moved the animation effect to the action up event due to
                conflicts that were occurring when library is used in sliding type views.
                icon.animate().scaleX(0.7f).scaleY(0.7f).setDuration(150).setInterpolator(DECCELERATE_INTERPOLATOR);
                */
                setPressed(true);
                break;

            case MotionEvent.ACTION_MOVE:
                float x = event.getX();
                float y = event.getY();
                boolean isInside = (x > 0 && x < getWidth() && y > 0 && y < getHeight());
                if (isPressed() != isInside) {
                    setPressed(isInside);
                }
                break;

            case MotionEvent.ACTION_UP:
                icon.animate().scaleX(0.7f).scaleY(0.7f).setDuration(150).setInterpolator(DECCELERATE_INTERPOLATOR);
                icon.animate().scaleX(1).scaleY(1).setInterpolator(DECCELERATE_INTERPOLATOR);
                if (isPressed()) {
                    performClick();
                    setPressed(false);
                }
                break;
        }
        return true;
    }


    /**
     * This drawable is shown when the button is a liked state.
     * @param resId
     */
    public void setCatchDrawableRes(@DrawableRes int resId) {
        catchDrawable = ContextCompat.getDrawable(getContext(), resId);

        if (iconSize != 0) {
            catchDrawable = Utils.resizeDrawable(getContext(), catchDrawable, iconSize, iconSize);
        }
    }

    /**
     * This drawable is shown when the button is in a liked state.
     * @param catchDrawable
     */
    public void setCatchDrawable(Drawable catchDrawable) {

        this.catchDrawable = catchDrawable;

        if (iconSize != 0) {
            this.catchDrawable = Utils.resizeDrawable(getContext(), catchDrawable, iconSize, iconSize);
        }

    }


    /**
     * This drawable will be shown when the button is in on unLiked state.
     * @param resId
     */
    public void setUncatchDrawableRes(@DrawableRes int resId) {
        uncatchDrawable = ContextCompat.getDrawable(getContext(), resId);

        if (iconSize != 0) {
            uncatchDrawable = Utils.resizeDrawable(getContext(), uncatchDrawable, iconSize, iconSize);
        }
        icon.setImageDrawable(uncatchDrawable);
    }

    /**
     * This drawable will be shown when the button is in on unLiked state.
     * @param uncatchDrawable
     */
    public void setUncatchDrawable(Drawable uncatchDrawable) {

        this.uncatchDrawable = uncatchDrawable;

        if (iconSize != 0) {
            this.uncatchDrawable = Utils.resizeDrawable(getContext(), uncatchDrawable, iconSize, iconSize);
        }
        icon.setImageDrawable(uncatchDrawable);


    }

    /**
     * Sets one of the three icons that are bundled with the library.
     * @param currentpokemonName
     */
    public void setPokemon(PokemonName currentpokemonName) {
        currentPokemon = parsePokemonName(currentpokemonName);
        setCatchDrawableRes(currentPokemon.getOnIconResourceId());
        setUncatchDrawableRes(currentPokemon.getOffIconResourceId());
    }

    /**
     * Sets the size of the drawable/icon that's being used. The views that generate
     * the like effect are also updated to reflect the size of the icon.
     * @param iconSize
     */

    public void setIconSizeDp(int iconSize)
    {
        setIconSizePx((int)Utils.dipToPixels(getContext(),(float)iconSize));
    }
    /**
     * Sets the size of the drawable/icon that's being used. The views that generate
     * the like effect are also updated to reflect the size of the icon.
     * @param iconSize
     */
    public void setIconSizePx(int iconSize) {
        this.iconSize = iconSize;
        setEffectsViewSize();
        this.uncatchDrawable = Utils.resizeDrawable(getContext(), uncatchDrawable, iconSize, iconSize);
        this.catchDrawable = Utils.resizeDrawable(getContext(), catchDrawable, iconSize, iconSize);
    }

    /**
     * * Parses the specific icon based on string
     * version of its enum.
     * These icons are bundled with the library and
     * are accessed via objects that contain their
     * resource ids and an enum with their name.
     * @param pokemonName
     * @return Icon
     */
    private Pokemon parsePokemonName(String pokemonName) {
        List<Pokemon> pokemons = Utils.getPokemons();

        for (Pokemon pokemon : pokemons) {
            if (pokemon.getPokemonName().name().toLowerCase().equals(pokemonName.toLowerCase())) {
                return pokemon;
            }
        }

        throw new IllegalArgumentException("Correct pokemon type not specified.");
    }

    private PokemonType parsePokemonType(String pokemonType){
        for(PokemonType p : PokemonType.values()){
            if(p.name().toLowerCase().equals(pokemonType.toLowerCase())){
                return p;
            }
        }

        return PokemonType.ELECTRIC;
    }

    /**
     * Parses the specific icon based on it's type.
     * These icons are bundled with the library and
     * are accessed via objects that contain their
     * resource ids and an enum with their name.
     * @param pokemonName
     * @return
     */
    private Pokemon parsePokemonName(PokemonName pokemonName) {
        List<Pokemon> pokemons = Utils.getPokemons();

        for (Pokemon pokemon : pokemons) {
            if (pokemon.getPokemonName().equals(pokemonName)) {
                return pokemon;
            }
        }

        throw new IllegalArgumentException("Correct pokemon type not specified.");
    }

    /**
     * Listener that is triggered once the
     * button is in a liked or unLiked state
     * @param catchListener
     */
    public void setOncatchListener(OnCatchListener catchListener) {
        this.catchListener = catchListener;
    }


    /**
     * This set sets the colours that are used for the little dots
     * that will be exploding once the like button is clicked.
     */
    //public void setExplodingDotColorsRes(@ColorRes int primaryColor, @ColorRes int secondaryColor) {
    //    lightningView.setColors(ContextCompat.getColor(getContext(),primaryColor), ContextCompat.getColor(getContext(),secondaryColor));
    //}

    public void setTopHalfBallColor(@ColorRes int topHalfBallColor) {
        this.topHalfBallColor = topHalfBallColor;
        pokeBallView.setTopHalfBallColor(ContextCompat.getColor(getContext(),topHalfBallColor));
    }


    /**
     * This function updates the dots view and the circle
     * view with the respective sizes based on the size
     * of the icon being used.
     */
    private void setEffectsViewSize() {
        if (iconSize != 0) {
            typeView.setSize((int)(iconSize * animationScaleFactor), (int)(iconSize * animationScaleFactor));
            pokeBallView.setSize(iconSize, iconSize);
        }
    }

    /**
     * Sets the initial state of the button to liked
     * or unliked.
     * @param status
     */
    public void setCatched(Boolean status)
    {
        if(status)
        {
            isChecked=true;
            icon.setImageDrawable(catchDrawable);
        }
        else
        {
            isChecked=false;
            icon.setImageDrawable(uncatchDrawable);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    /**
     *Sets the factor by which the dots should be sized.
     */
    public void setAnimationScaleFactor(float animationScaleFactor) {
        this.animationScaleFactor = animationScaleFactor;

        setEffectsViewSize();
    }

}
