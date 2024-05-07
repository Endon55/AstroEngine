package com.anthonycosenza.engine.space.entity.texture;

import com.anthonycosenza.engine.loader.text.Font;
import com.anthonycosenza.engine.loader.text.tables.types.GlyphPath;
import com.anthonycosenza.engine.loader.text.tables.types.points.CurvedPoint;
import com.anthonycosenza.engine.loader.text.tables.types.points.FontPoint;
import com.anthonycosenza.engine.loader.text.tables.types.points.StraightPoint;
import com.anthonycosenza.engine.space.rendering.UI.Canvas;
import com.anthonycosenza.engine.util.math.Color;
import com.anthonycosenza.engine.util.math.vector.Vector2i;

import java.util.Arrays;
import java.util.List;

public class FontAtlasGenerator
{
    
    public FontAtlasGenerator(Font font)
    {
        //Read a character.
        //Get glyph data.
        //Draw line from point to point.
        //return texture.
    }
    
    
    
    public static Canvas getAtlas(int fontSize, Font font)
    {
        Canvas canvas = new Canvas(2000, 2000);
        Color black = new Color(0, 0, 255, 255);
        List<FontPoint> path = font.getGlyph(1);
        for(int i = 0; i < path.size(); i++)
        {
            FontPoint point = path.get(i);
            FontPoint nextPoint = (i + 1 < path.size() ? path.get(i + 1) : path.get(0));
            
            if(nextPoint instanceof StraightPoint)
            {
                canvas.drawLine(black, point.getPosition().x(), point.getPosition().y() + 100,
                        nextPoint.getPosition().x(), nextPoint.getPosition().y() + 100);
            }
            else if(nextPoint instanceof CurvedPoint cPoint)
            {
                canvas.bezier(black,
                        point.getPosition().add(0, 100, new Vector2i()),
                        cPoint.getControlPoint1().add(0, 100, new Vector2i()),
                        cPoint.getControlPoint2().add(0, 100, new Vector2i()),
                        cPoint.getPosition().add(0, 100, new Vector2i()));
            }
            else throw new RuntimeException("What kind of point are you? " + point.getClass().getSimpleName());
        
        }
        
        
        return canvas;
    }
    
    
    
}
