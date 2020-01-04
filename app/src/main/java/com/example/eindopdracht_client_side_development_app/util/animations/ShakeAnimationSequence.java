package com.example.eindopdracht_client_side_development_app.util.animations;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;

public class ShakeAnimationSequence extends AnimationSequence
{
    public ShakeAnimationSequence(View view, int maxLeft, int maxRight, int duration, int shakeCount)
    {
        super(view);

        setupStartAnimation(duration / 2, maxLeft);

        int animationDuration = (duration / 2) / (shakeCount + 1);
        boolean goingRight = true;
        for(int  i = 0; i < shakeCount; i++)
        {
            Animation rotateAnimation = null;
            if(goingRight)
                rotateAnimation = new RotateAnimation(maxLeft, maxRight, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            else
                rotateAnimation = new RotateAnimation(maxRight, maxLeft, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotateAnimation.setDuration(animationDuration);
            goingRight = !goingRight;
            addAnimation(rotateAnimation);
        }

        Animation rotateAnimation = null;
        if(goingRight)
            rotateAnimation = new RotateAnimation(maxLeft, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        else
            rotateAnimation = new RotateAnimation(maxRight, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(animationDuration);
        addAnimation(rotateAnimation);
    }

    private void setupStartAnimation(int startAnimationDuration, int maxLeft)
    {
        Animation startScaleAnimation = new ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        startScaleAnimation.setDuration(startAnimationDuration);

        Animation startRotateAnimation = new RotateAnimation(20, maxLeft, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        startRotateAnimation.setDuration(startAnimationDuration);

        AnimationSet startAnimation = new AnimationSet(true);
        startAnimation.addAnimation(startScaleAnimation);
        startAnimation.addAnimation(startRotateAnimation);
        addAnimation(startAnimation);
    }
}
