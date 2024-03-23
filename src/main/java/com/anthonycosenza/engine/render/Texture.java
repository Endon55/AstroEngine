package com.anthonycosenza.engine.render;

import de.matthiasmann.twl.utils.PNGDecoder;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_UNPACK_ALIGNMENT;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glPixelStorei;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load;

public class Texture
{
    private int textureID;
    private String texturePath;
    
    public Texture(int width, int height, ByteBuffer buf)
    {
        this.texturePath = "";
        generateTexture(width, height, buf);
    }
    
    public Texture(String texturePath)
    {
        try(MemoryStack stack = MemoryStack.stackPush())
        {
            this.texturePath = texturePath;
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);
            ByteBuffer buf = stbi_load(texturePath, w, h, channels, 4);
            if(buf == null)
            {
                throw new RuntimeException("Image file [" + texturePath + "] could not be loaded: " + stbi_failure_reason());
            }
            int width = w.get();
            int height = h.get();
            
            generateTexture(width, height, buf);
            stbi_image_free(buf);
        }
    }
    
    
   public void bind()
   {
       glBindTexture(GL_TEXTURE_2D, textureID);
   }
    
    public void generateTexture(int width, int height, ByteBuffer buf)
    {
        textureID = glGenTextures();
        
        glBindTexture(GL_TEXTURE_2D, textureID);
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0,
                GL_RGBA, GL_UNSIGNED_BYTE, buf);
        glGenerateMipmap(GL_TEXTURE_2D);
    }
    
    public String getTexturePath()
    {
        return texturePath;
    }
    
    public void cleanup()
    {
        glDeleteTextures(textureID);
    }
    
}
