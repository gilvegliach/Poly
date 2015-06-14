package it.gilvegliach.poly.sample;

import android.graphics.Color;

import javax.inject.Inject;

public class ColorProcessor {

    @Inject
    public ColorProcessor() {}

    public int getColor() {
        return Color.BLUE;
    }
}
