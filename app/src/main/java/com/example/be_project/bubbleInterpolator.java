package com.example.be_project;

import android.view.animation.Interpolator;

public class bubbleInterpolator implements Interpolator {

    private double amplitude=1;
    private double frequency=10;

    public bubbleInterpolator(double amplitude,double frequency) {
        amplitude =amplitude;
        frequency =frequency;
    }

    @Override
    public float getInterpolation(float input) {
        return (float)(-1 * Math.pow(Math.E,- input/amplitude) * Math.cos(frequency*input));
    }
}
