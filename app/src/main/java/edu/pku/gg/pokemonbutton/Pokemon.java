package edu.pku.gg.pokemonbutton;

import android.support.annotation.DrawableRes;

/**
 * Created by Joel on 23/12/2015.
 * Modified by Gg on 12/10/2016.
 */
public class Pokemon {
    private int onIconResourceId;
    private int offIconResourceId;
    private PokemonName pokemonName;

    public Pokemon(@DrawableRes int onIconResourceId,@DrawableRes int offIconResourceId, PokemonName pokemonName) {
        this.onIconResourceId = onIconResourceId;
        this.offIconResourceId = offIconResourceId;
        this.pokemonName = pokemonName;
    }

    public int getOffIconResourceId() {
        return offIconResourceId;
    }

    public void setOffIconResourceId(@DrawableRes int offIconResourceId) {
        this.offIconResourceId = offIconResourceId;
    }

    public int getOnIconResourceId() {
        return onIconResourceId;
    }

    public void setOnIconResourceId(@DrawableRes int onIconResourceId) {
        this.onIconResourceId = onIconResourceId;
    }

    public PokemonName getPokemonName() {
        return pokemonName;
    }

    public void setPokemonName(PokemonName pokemonName) {
        this.pokemonName = pokemonName;
    }

}
