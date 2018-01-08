package com.example.util;

import java.util.List;

/**
 * File Description:
 * Author:weisc
 * Create Date:18-1-3
 * Change List:
 */

public class AnimationList {

    private String defaultAnimation;

    private List<Animation> animations;

    public AnimationList(String defaultAnimation, List<Animation> animations) {
        this.defaultAnimation = defaultAnimation;
        this.animations = animations;
    }

    public String getDefaultAnimation() {
        return defaultAnimation;
    }

    public List<Animation> getAnimations() {
        return animations;
    }

    public void setDefaultAnimation(String defaultAnimation) {
        this.defaultAnimation = defaultAnimation;
    }

}
