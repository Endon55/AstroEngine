package com.anthonycosenza.engine.space.entity.texture;

public interface Image
{
    int DEFAULT_COLOR_CHANNELS = 4;
    
    float[] getPixelData();
    int getColorChannels();
    
    int getWidth();
    int getHeight();
    
    int getRowWidth();
}
