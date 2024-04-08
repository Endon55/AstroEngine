package com.anthonycosenza.engine.render.model.animation;

import com.anthonycosenza.engine.render.model.ModelLoader;
import org.joml.Matrix4f;

import java.util.Arrays;

public class AnimationData
{
    public static final Matrix4f[] DEFAULT_BONES_MATRICES = new Matrix4f[ModelLoader.MAX_BONES];
    
    static
    {
        Matrix4f zeroMatrix = new Matrix4f().zero();
        Arrays.fill(DEFAULT_BONES_MATRICES, zeroMatrix);
    }
    
    private Animation currentAnimation;
    private int currentFrameIndex;
    
    public AnimationData(Animation currentAnimation)
    {
        currentFrameIndex = 0;
        this.currentAnimation = currentAnimation;
    }
    
    public Animation getCurrentAnimation()
    {
        return currentAnimation;
    }
    
    public int getCurrentFrameIndex()
    {
        return currentFrameIndex;
    }
    
    public AnimatedFrame getCurrentFrame()
    {
        return currentAnimation.frames().get(currentFrameIndex);
    }
    
    public void nextFrame()
    {
        int nextFrame = currentFrameIndex + 1;
        if(nextFrame > currentAnimation.frames().size() - 1)
        {
            currentFrameIndex = 0;
        }
        else
        {
            currentFrameIndex = nextFrame;
        }
    }
    
    public void setCurrentAnimation(Animation currentAnimation)
    {
        currentFrameIndex = 0;
        this.currentAnimation = currentAnimation;
    }
}
