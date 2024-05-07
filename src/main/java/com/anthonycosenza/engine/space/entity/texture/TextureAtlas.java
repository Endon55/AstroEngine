package com.anthonycosenza.engine.space.entity.texture;

import com.anthonycosenza.engine.loader.image.ImageLoader;
import com.anthonycosenza.engine.util.math.vector.Vector2i;

public class TextureAtlas
{
    private int width;
    private int rowWidth;
    private int height;
    private float[] pixels;
    
    public TextureAtlas(int width, int height, float[] pixels)
    {
        this.width = width;
        this.height = height;
        this.pixels = pixels;
        this.rowWidth = width * 4;
        //TODO add better support for color channels.
    }
    public TextureAtlas(String filepath)
    {
        int[] dimensions = new int[2];
        pixels = ImageLoader.load(dimensions, filepath);
        width = dimensions[0];
        height = dimensions[1];
        this.rowWidth = width * 4;
    }
    
    public Texture getTexture(int xPos, int yPos, int width, int height)
    {
        if(xPos + width > this.width || yPos + height > this.height)
            throw new IndexOutOfBoundsException("Sub region exceeds bounds of source Atlas.");
        
        
        int w = width;
        //4 channels
        width = width * 4;
        int length = width * height;
        float[] region = new float[length];
    
        for(int y = 0; y < height; y++)
        {
            for(int x = 0; x < width; x++)
            {
                int aIndex = ((yPos + y) * rowWidth + xPos + x);
                int rIndex = y * width + x;
                region[rIndex] = pixels[aIndex];
            }
        }
        return new Texture(w, height, region);
    }
    public Texture getTexture(Vector2i position, Vector2i dimensions)
    {
        return getTexture(position.x(), position.y(), dimensions.x(), dimensions.y());
    }
    
    public Texture getFullTexture()
    {
        return new Texture(width, height, pixels);
    }
}
