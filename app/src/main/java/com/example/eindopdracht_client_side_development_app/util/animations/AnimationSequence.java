package com.example.eindopdracht_client_side_development_app.util.animations;

import android.view.View;
import android.view.animation.Animation;

import java.util.ArrayList;

public class AnimationSequence implements Animation.AnimationListener
{
    private ArrayList<Animation> animations;
    private int currentAnimationIndex;
    private boolean isRunning;

    private View animatedView;

    public AnimationSequence(View view)
    {
        this.animations = new ArrayList<Animation>();
        this.animatedView = view;
        this.currentAnimationIndex = 0;
        this.isRunning = false;
    }

    public void addAnimation(Animation animation)
    {
        if(animation != null)
        {
            animation.setAnimationListener(this);
            this.animations.add(animation);
        }
    }

    public void addAnimationToFront(Animation animation)
    {
        if(animation != null)
        {
            animation.setAnimationListener(this);
            ArrayList<Animation> animationList = new ArrayList<Animation>();
            animationList.add(animation);
            animationList.addAll(this.animations);
            this.animations = animationList;
        }
    }

    public void start()
    {
        if(!this.isRunning)
        {
            this.isRunning = true;
            this.animatedView.startAnimation(this.animations.get(this.currentAnimationIndex));
        }
    }

    public void stop()
    {
        this.isRunning = false;
        this.animatedView.clearAnimation();
    }

    @Override
    public void onAnimationStart(Animation animation)
    {

    }

    @Override
    public void onAnimationEnd(Animation animation)
    {
        this.currentAnimationIndex++;
        if(this.currentAnimationIndex != this.animations.size() && this.isRunning)
        {
            this.animatedView.startAnimation(this.animations.get(this.currentAnimationIndex));
        }
        else
        {
            this.currentAnimationIndex = 0;
            this.isRunning = false;
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation)
    {

    }
}
