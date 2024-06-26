package com.anthonycosenza.engine.space.rendering.materials.texture;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.GL_READ_WRITE;
import static org.lwjgl.opengl.GL15.GL_WRITE_ONLY;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;
import static org.lwjgl.opengl.GL42.glBindImageTexture;
import static org.lwjgl.system.MemoryUtil.NULL;

public class BufferTexture extends Texture
{
    public long resourceID = -1;
    private int width;
    private int height;
    private int format;
    
    public BufferTexture(int format, int width, int height)
    {
        this.width = width;
        this.height = height;
        this.format = format;
        generate();
    }
    public BufferTexture(int width, int height)
    {
        this(GL_RGBA32F, width, height);
    }
    
    @Override
    public void bind()
    {
        if(!isGenerated())
        {
            generate();
            generated = true;
        }
        glBindTexture(GL_TEXTURE_2D, textureID);
        //glBindImageTexture(0, textureID, 0, false, 0, GL_READ_WRITE, format);
    }
    
    public void bindImageTexture()
    {
        if(!isGenerated())
        {
            generate();
            generated = true;
        }
        glBindImageTexture(0, textureID, 0, false, 0, GL_READ_WRITE, format);
    }
    
    @Override
    protected void generate()
    {
        //https://stackoverflow.com/questions/9224300/what-does-gltexstorage-do
        
        textureID = glGenTextures();
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, textureID);
        
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        //the GL_RGBA works but for whatever reason GL_RGBA32F doesn't, investigate further how this is stored.
        glTexImage2D(GL_TEXTURE_2D, 0, format, width, height, 0, GL_RGBA, GL_FLOAT, NULL);
        glBindImageTexture(0, textureID, 0, false, 0, GL_WRITE_ONLY, format);
    }
}
