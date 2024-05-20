package com.anthonycosenza.engine.space.entity.texture.atlas;

import com.anthonycosenza.engine.space.entity.texture.Texture;
import com.anthonycosenza.engine.space.rendering.UI.Canvas;
import com.anthonycosenza.engine.util.math.vector.Vector2i;

import java.util.Arrays;
import java.util.List;

public class CanvasAtlas implements Atlas
{
    public static final int DEFAULT_PADDING = 1;
    private static final int DEFAULT_MAX_ATLAS_WIDTH = 2000;
    
    
    private final int maxAtlasWidth;
    private final int padding;
    private Vector2i[] subImagePositions;
    private Vector2i[] subImageDimensions;
    private int width;
    private int rowWidth;
    private int height;
    private float[] pixels;
    
    public CanvasAtlas(List<Canvas> canvasList)
    {
        this(DEFAULT_PADDING, DEFAULT_MAX_ATLAS_WIDTH, canvasList);
    }
    public CanvasAtlas(int padding, int maxAtlasWidth, List<Canvas> canvasList)
    {
        this.padding = padding;
        this.maxAtlasWidth = maxAtlasWidth;
        assembleAtlas(canvasList);
    }

    private void assembleAtlas(List<Canvas> canvasList)
    {
        /*
         * Remembering the subImage coordinate as well as it's dimensions is all we need to grab the pixels from the atlas.
         */
        subImagePositions = new Vector2i[canvasList.size()];
        subImageDimensions = new Vector2i[canvasList.size()];
        
        int x = 0;
        int y = 0;
        int rowMaxY = 0;
        int maxX = 0;
        
        for(int i = 0; i < canvasList.size(); i++)
        {
            Canvas canvas = canvasList.get(i);
        
            /*
             * We always add at least 1 element, and we keep adding until the next element would exceed the edge limit,
             * then wrap around to the next line.
             */
            if(x != 0 && x + canvas.getWidth() > getMaxAtlasWidth())
            {
                y += rowMaxY + getPadding();
                x = 0;
                rowMaxY = 0;
            }
    
            subImagePositions[i] = new Vector2i(x, y); //Y might need + height() added to it
            subImageDimensions[i] = new Vector2i(canvas.getWidth(), canvas.getHeight());
        
            //Increment horizontally
            x += canvas.getWidth() + getPadding();
            
            //Keep track of the largest row x value.
            if(x > maxX)
            {
                maxX = x;
            }
            //Track the tallest image for the row so we know where to start the next row.
            if(canvas.getHeight() > rowMaxY)
            {
                rowMaxY = canvas.getHeight();
            }
        }
        
        width = maxX + getPadding();
        height = rowMaxY + y + getPadding();
        rowWidth = width * getColorChannels();
        pixels = new float[rowWidth * height];
        
        /*
         * Fill the newly created array.
         *
         * Loop through each pixel in each canvas
         */
        for(int i = 0; i < canvasList.size(); i++)
        {
            Vector2i position = subImagePositions[i];
            Vector2i dimension = subImageDimensions[i];
            Canvas canvas = canvasList.get(i);
            int subRowOffset = position.x() * canvas.getColorChannels();
            int rowWidth = canvas.getRowWidth();
            
            //Reuse original X and Y variables
            for(y = 0; y < dimension.y(); y++)
            {
                for(x = 0; x < rowWidth; x++)
                {
                    
                    
                    int subIndex = y * rowWidth + x;
                    int atlasIndex = (y + position.y()) * getRowWidth() + (x + subRowOffset);
                    
                    pixels[atlasIndex] = canvas.getPixelData()[subIndex];
                }
            }
        }
    }
    
    public int getMaxAtlasWidth()
    {
        return maxAtlasWidth;
    }
    
    public int getPadding()
    {
        return padding;
    }
    
    public float getAspectRatio()
    {
        return width / (float)height;
    }
    
    public Vector2i getPosition(int index)
    {
        return subImagePositions[index];
    }
    
    public Vector2i getDimension(int index)
    {
        return subImageDimensions[index];
    }
    
    @Override
    public Texture getTexture(int xPos, int yPos, int width, int height)
    {
        return null;
    }
    
    @Override
    public Texture getFullTexture()
    {
        return new Texture(getWidth(), getHeight(), getPixelData());
    }

    @Override
    public float[] getPixelData()
    {
        return pixels;
    }
    
    @Override
    public int getColorChannels()
    {
        return DEFAULT_COLOR_CHANNELS;
    }
    
    @Override
    public int getWidth()
    {
        return width;
    }
    
    @Override
    public int getHeight()
    {
        return height;
    }
    
    @Override
    public int getRowWidth()
    {
        return rowWidth;
    }
}
