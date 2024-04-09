package com.anthonycosenza.engine.render.light.shadow;

import com.anthonycosenza.engine.render.ArrayTexture;

import static org.lwjgl.opengl.GL11.GL_DEPTH_COMPONENT;
import static org.lwjgl.opengl.GL11.GL_NONE;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDrawBuffer;
import static org.lwjgl.opengl.GL11.glReadBuffer;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.GL_DEPTH_ATTACHMENT;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_COMPLETE;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import static org.lwjgl.opengl.GL30.glCheckFramebufferStatus;
import static org.lwjgl.opengl.GL30.glDeleteFramebuffers;
import static org.lwjgl.opengl.GL30.glFramebufferTexture2D;
import static org.lwjgl.opengl.GL30.glGenFramebuffers;

public class ShadowBuffer
{
    public static final int SHADOW_MAP_WIDTH = 4096;
    public static final int SHADOW_MAP_HEIGHT = SHADOW_MAP_WIDTH;
    
    private final ArrayTexture depthMap;
    private final int depthMapFBO;
    
    public ShadowBuffer()
    {
        depthMapFBO = glGenFramebuffers();
        
        depthMap = new ArrayTexture(CascadeShadow.SHADOW_MAP_CASCADE_COUNT, SHADOW_MAP_WIDTH, SHADOW_MAP_HEIGHT, GL_DEPTH_COMPONENT);
        glBindFramebuffer(GL_FRAMEBUFFER, depthMapFBO);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthMap.getIds()[0], 0);
        
        glDrawBuffer(GL_NONE);
        glReadBuffer(GL_NONE);
        
        if(glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
        {
            throw new RuntimeException("Could not create FrameBuffer");
        }
        
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }
    
    public void bindTextures(int start)
    {
        for(int i = 0; i < CascadeShadow.SHADOW_MAP_CASCADE_COUNT; i++)
        {
            glActiveTexture(start + i);
            glBindTexture(GL_TEXTURE_2D, depthMap.getIds()[i]);
        }
    }
    
    public ArrayTexture getDepthMapTexture()
    {
        return depthMap;
    }
    
    public int getDepthMapFBO()
    {
        return depthMapFBO;
    }
    
    public void cleanup()
    {
        glDeleteFramebuffers(depthMapFBO);
        depthMap.cleanup();
    }
}
