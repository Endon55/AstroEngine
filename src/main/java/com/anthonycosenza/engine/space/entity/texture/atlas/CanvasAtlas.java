package com.anthonycosenza.engine.space.entity.texture.atlas;

import com.anthonycosenza.engine.space.entity.texture.Texture;
import com.anthonycosenza.engine.space.rendering.UI.Canvas;
import com.anthonycosenza.engine.util.math.vector.Vector2i;

import java.util.Arrays;
import java.util.List;

public class CanvasAtlas implements Atlas
{
    public static int PADDING = 1;
    private static int MAX_ATLAS_WIDTH = 1000;
    private Vector2i[] subImagePositions;
    private Vector2i[] subImageDimensions;
    private int width;
    private int rowWidth;
    private int height;
    private float[] pixels;
    
    public CanvasAtlas(List<Canvas> canvasList)
    {
        assembleAtlas(canvasList);
    }
    private void assembleAtlas(List<Canvas> canvasList)
    {
        
        /*
         * Need to figure out the dimensions for the pixels float array
         * I do this setting by laying out all the images left to right, top to bottom, and assigning a Vector2i for each image.
         *
         * Then I can determine the largest x and y coordinate then fill the array.
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
            if(x != 0 && x + canvas.getWidth() > MAX_ATLAS_WIDTH)
            {
                y += rowMaxY + PADDING;
                x = 0;
                rowMaxY = 0;
            }
    
            subImagePositions[i] = new Vector2i(x, y); //Y might need + height() added to it
            subImageDimensions[i] = new Vector2i(canvas.getWidth(), canvas.getHeight());
        
            //Increment horizontally
            x += canvas.getWidth() + PADDING;
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
        
        width = maxX;
        height = rowMaxY + y;
        rowWidth = maxX * getColorChannels();
        pixels = new float[rowWidth * height];
        
        /*
         * Fill the newely created array.
         *
         * Loop each pixel in each canvas
         */
        for(int i = 0; i < canvasList.size(); i++)
        {
            Vector2i position = subImagePositions[i];
            Canvas canvas = canvasList.get(i);
            int posWidth = position.x() * canvas.getColorChannels();
            int rowWidth = canvas.getWidth() * canvas.getColorChannels();
            
            //Reuse original X and Y variables
            for(y = 0; y < canvas.getHeight(); y++)
            {
                for(x = 0; x < rowWidth; x++)
                {
                    int subIndex = y * rowWidth + x;
                    int atlasIndex = (y + position.y()) * getRowWidth() + (x + posWidth);
                    
                    pixels[atlasIndex] = canvas.getPixelData()[subIndex];
                }
            }
        }
    
        //System.out.println(Arrays.toString(pixels));
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
