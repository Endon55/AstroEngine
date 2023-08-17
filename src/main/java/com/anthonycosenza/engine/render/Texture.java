package com.anthonycosenza.engine.render;

import de.matthiasmann.twl.utils.PNGDecoder;

import java.io.IOException;
import java.nio.ByteBuffer;

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
        this.texturePath = texturePath;
    
        try
        {
            System.out.println(texturePath);
            PNGDecoder decoder = new PNGDecoder(Texture.class.getResourceAsStream(texturePath));
            ByteBuffer buf = ByteBuffer.allocateDirect(4 * decoder.getWidth() * decoder.getHeight());
            decoder.decode(buf, decoder.getWidth() * 4, PNGDecoder.Format.RGBA);
            buf.flip();
            generateTexture(decoder.getWidth(), decoder.getHeight(), buf);
        } catch(IOException e)
        {
            throw new RuntimeException(e);
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
