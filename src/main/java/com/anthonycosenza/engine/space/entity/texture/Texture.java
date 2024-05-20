package com.anthonycosenza.engine.space.entity.texture;

import com.anthonycosenza.engine.loader.image.ImageLoader;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_SHORT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_UNPACK_ALIGNMENT;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glPixelStorei;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL11.glTexSubImage2D;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load;

public class Texture
{
    private int textureID;
    
    
    public Texture(int width, int height, ByteBuffer pixelData)
    {
        generate(width, height, pixelData);
    }
    public Texture(int width, int height, float[] pixelData)
    {
        generate(width, height, pixelData);
    }
    
    public Texture(int width, int height, int[] pixelData)
    {
        generate(width, height, pixelData);
    }
    public Texture(String filepath)
    {
        this(filepath, false);
    }
    public Texture(String filepath, boolean fast)
    {
        textureID = glGenTextures();
        
        if(fast)
        {
            try(MemoryStack stack = MemoryStack.stackPush())
            {
                IntBuffer w = stack.mallocInt(1);
                IntBuffer h = stack.mallocInt(1);
                IntBuffer channels = stack.mallocInt(1);
        
                ByteBuffer buf = stbi_load(filepath, w, h, channels, 4);
                if(buf == null)
                {
                    throw new RuntimeException("Image file [" + filepath + "] not loaded: " + stbi_failure_reason());
                }
                
                generate(w.get(), h.get(), buf);
                stbi_image_free(buf);
            }
        }
        else {
            int[] dimensions = new int[2];
            float[] pixelData = ImageLoader.load(dimensions, filepath);
    
            generate(dimensions[0], dimensions[1], pixelData);
        }


    }
    
    private void generate(int width, int height, ByteBuffer pixelData)
    {
        textureID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureID);
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, pixelData);
        glGenerateMipmap(GL_TEXTURE_2D);
    }
    private void generate(int width, int height, float[] pixelData)
    {
        textureID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureID);
        //Setting the alignment of the pixel data 1 = Byte Aligned
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA32F, width, height, 0, GL_RGBA, GL_FLOAT, pixelData);
        //System.out.println(glGetError());
        glGenerateMipmap(GL_TEXTURE_2D);
    }
    
    private void generate(int width, int height, int[] pixelData)
    {
        glBindTexture(GL_TEXTURE_2D, textureID);
        //Setting the alignment of the pixel data 1 = Byte Aligned
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA32F, width, height, 0, GL_RGBA, GL_SHORT, pixelData);
        //System.out.println(glGetError());
        glGenerateMipmap(GL_TEXTURE_2D);
    }
    
    private void generate(int width, int height, short[] pixelData)
    {
        glBindTexture(GL_TEXTURE_2D, textureID);
        //Setting the alignment of the pixel data 1 = Byte Aligned
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA32F, width, height, 0, GL_RGBA, GL_SHORT, pixelData);
        //System.out.println(glGetError());
        glGenerateMipmap(GL_TEXTURE_2D);
    }
    
    
    public void updateFullTexture(int width, int height, float[] pixelData)
    {
        generate(width, height, pixelData);
    }
    
    public void bind()
    {
        glBindTexture(GL_TEXTURE_2D, textureID);
    }
}
