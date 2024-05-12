package com.anthonycosenza.engine.space.entity.texture.atlas;

import com.anthonycosenza.engine.space.entity.texture.Image;
import com.anthonycosenza.engine.space.entity.texture.Texture;

public interface Atlas extends Image
{
    Texture getTexture(int xPos, int yPos, int width, int height);
    Texture getFullTexture();
}
