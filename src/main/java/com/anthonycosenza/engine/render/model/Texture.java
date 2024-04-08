package com.anthonycosenza.engine.render.model;

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
        //Memory stack allows the creation of memory system wide memory that can be shared with OpenGL without having to create a copy of the data.
        //https://blog.lwjgl.org/memory-management-in-lwjgl-3/
        
        try(MemoryStack stack = MemoryStack.stackPush())
        {
            this.texturePath = texturePath;
            
            //STBI will load the relevant values into these buffers when it loads the image
            IntBuffer widthBuffer = stack.mallocInt(1);
            IntBuffer heightBuffer = stack.mallocInt(1);
            IntBuffer channelBuffer = stack.mallocInt(1);
            
            ByteBuffer imageBuffer = stbi_load(texturePath, widthBuffer, heightBuffer, channelBuffer, 4);
            if(imageBuffer == null)
            {
                throw new RuntimeException("Image file [" + texturePath + "] could not be loaded: " + stbi_failure_reason());
            }
            
            generateTexture(widthBuffer.get(), heightBuffer.get(), imageBuffer);
            stbi_image_free(imageBuffer);
        }
    }
    
    
   public void bind()
   {
       glBindTexture(GL_TEXTURE_2D, textureID);
   }
    
    public void generateTexture(int width, int height, ByteBuffer imageBuffer)
    {
        textureID = glGenTextures();
        
        glBindTexture(GL_TEXTURE_2D, textureID);
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0,
                GL_RGBA, GL_UNSIGNED_BYTE, imageBuffer);
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
