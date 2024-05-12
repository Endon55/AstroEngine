package com.anthonycosenza.engine.loader.text.tables.types;

import com.anthonycosenza.engine.loader.text.tables.types.points.FontPoint;
import com.anthonycosenza.engine.util.math.vector.Vector2i;

import java.util.List;

public class CharacterGlyph implements Glyph
{
    private int xMin = 10000;
    private int xMax = 0;
    private int yMin = 10000;
    private int yMax = 0;
    private List<List<FontPoint>> paths;
    public CharacterGlyph(List<List<FontPoint>> paths)
    {
        this.paths = paths;
        setBoundingBox();
    }
    
    private void setBoundingBox()
    {
        if(paths.isEmpty())
        {
            xMin = 0;
            yMin = 0;
            return;
        }
        
        for(List<FontPoint> path : paths)
        {
            for(FontPoint point : path)
            {
                Vector2i position = point.getPosition();
                
                if(position.x() > xMax) xMax = position.x();
                if(position.x() < xMin) xMin = position.x();
    
                if(position.y() > yMax) yMax = position.y();
                if(position.y() < yMin) yMin = position.y();
            }
        }
    }
    
    
    
    @Override
    public List<List<FontPoint>> getPaths()
    {
        return paths;
    }
    
    
    @Override
    public int getMaxX()
    {
        return xMax;
    }
    
    @Override
    public int getMinX()
    {
        return xMin;
    }
    
    @Override
    public int getMaxY()
    {
        return yMax;
    }
    
    @Override
    public int getMinY()
    {
        return yMin;
    }
    
    @Override
    public int getWidth()
    {
        return getMaxX() - getMinX();
    }
    
    @Override
    public int getHeight()
    {
        return getMaxY() - getMinY();
    }
}
