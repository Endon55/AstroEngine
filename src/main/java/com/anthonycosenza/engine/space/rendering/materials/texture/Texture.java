package com.anthonycosenza.engine.space.rendering.materials.texture;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;

public abstract class Texture
{
    protected int textureID;
    protected boolean generated = false;
    
    
    public int getTextureID()
    {
        return textureID;
    }
    
    
    public void bind()
    {
        //This essentially lazy loads everything, hopefully that's not a problem in a game engine....
        if(!isGenerated())
        {
            generate();
            generated = true;
        }
        
        glBindTexture(GL_TEXTURE_2D, textureID);
    }
    
    protected boolean isGenerated()
    {
        return generated;
    }
    protected abstract void generate();

}
