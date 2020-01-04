package com.example.eindopdracht_client_side_development_app.util.animations;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;

public class ScaleBounceAnimationSequence extends AnimationSequence
{

    public ScaleBounceAnimationSequence(View view, float minScale, float maxScale, int duration, int bounceCount)
    {
        super(view);

        float minScaleStep = (1.0f - minScale);
        float maxScaleStep = (maxScale - 1.0f);

        float stepScale = 1.0f;

        int startEndDuration = duration / (bounceCount + 1);
        int stepsDuration = duration - startEndDuration;

        Animation startScaleAnimation = new ScaleAnimation(1.0f, 1.0f + (stepScale * maxScaleStep), 1.0f, 1.0f + (stepScale * maxScaleStep),
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        startScaleAnimation.setDuration((long)((startEndDuration / 2) * stepScale));
        addAnimation(startScaleAnimation);

        for(int i = 0; i < bounceCount; i++)
        {
            Animation scaleAnimation1 = new ScaleAnimation(1.0f + (stepScale * maxScaleStep), 1.0f - (stepScale * minScaleStep), 1.0f + (stepScale * maxScaleStep), 1.0f - (stepScale * minScaleStep),
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            scaleAnimation1.setDuration((long)((stepsDuration / 2) * stepScale));

            Animation scaleAnimation2 = new ScaleAnimation(1.0f - (stepScale * minScaleStep), 1.0f + (stepScale * maxScaleStep), 1.0f - (stepScale * minScaleStep), 1.0f + (stepScale * maxScaleStep),
                                                                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            scaleAnimation2.setDuration((long)((stepsDuration / 2) * stepScale));

            addAnimation(scaleAnimation1);
            addAnimation(scaleAnimation2);

            stepScale /= 2;
        }

        Animation endScaleAnimation = new ScaleAnimation(1.0f + (stepScale * maxScaleStep), 1.0f, 1.0f + (stepScale * maxScaleStep), 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        endScaleAnimation.setDuration((long)((startEndDuration / 2) * stepScale));
        addAnimation(endScaleAnimation);
    }
}
