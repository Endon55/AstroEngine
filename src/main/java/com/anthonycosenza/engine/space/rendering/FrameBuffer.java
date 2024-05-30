package com.anthonycosenza.engine.space.rendering;

import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL20.glDrawBuffers;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_DEPTH24_STENCIL8;
import static org.lwjgl.opengl.GL30.GL_DEPTH_STENCIL_ATTACHMENT;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_COMPLETE;
import static org.lwjgl.opengl.GL30.GL_RENDERBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import static org.lwjgl.opengl.GL30.glBindRenderbuffer;
import static org.lwjgl.opengl.GL30.glCheckFramebufferStatus;
import static org.lwjgl.opengl.GL30.glFramebufferRenderbuffer;
import static org.lwjgl.opengl.GL30.glGenFramebuffers;
import static org.lwjgl.opengl.GL30.glGenRenderbuffers;
import static org.lwjgl.opengl.GL30.glRenderbufferStorage;
import static org.lwjgl.opengl.GL32.glFramebufferTexture;
import static org.lwjgl.system.MemoryUtil.NULL;

public class FrameBuffer
{
    int frameBufferID;
    int textureID;
    int depthBufferID;
    
    int width;
    int height;
    /*
     * https://www.opengl-tutorial.org/intermediate-tutorials/tutorial-14-render-to-texture/
     */
    public FrameBuffer(int width, int height)
    {
        this.width = width;
        this.height = height;
        
        //Create the frame buffer
        frameBufferID = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, frameBufferID);
        
        //Create an empty texture
        textureID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureID);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, NULL);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        
        //Create a buffer for depth testing
        depthBufferID = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, depthBufferID);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, width, height);
        
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, depthBufferID);
        //Bind the frame buffer to layout(location = 0) out vec3 color, at the texture location.
        glFramebufferTexture(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, textureID, 0);
        
        //We can omit the 1 parameter since LWJGL adds it internally.
        glDrawBuffers(new int[]{GL_COLOR_ATTACHMENT0});
        
        if(glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
        {
            throw new RuntimeException("Failed to create frame buffer.");
        }
    }
    
    public void resize(int width, int height)
    {
        this.width = width;
        this.height = height;
        
        glBindTexture(GL_TEXTURE_2D, textureID);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, NULL);
    
        glBindRenderbuffer(GL_RENDERBUFFER, depthBufferID);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, width, height);
    }
    
    public void bind()
    {
        glBindTexture(GL_TEXTURE_2D, 0);
        glEnable(GL_TEXTURE_2D);
        glBindFramebuffer(GL_FRAMEBUFFER, frameBufferID);
        glViewport(0, 0, width, height);
        
    }
    public void unbind()
    {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }
    
    public int getTextureID()
    {
        return textureID;
    }
}
