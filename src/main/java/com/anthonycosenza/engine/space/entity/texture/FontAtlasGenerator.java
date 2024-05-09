package com.anthonycosenza.engine.space.entity.texture;

import com.anthonycosenza.engine.loader.text.Font;
import com.anthonycosenza.engine.loader.text.tables.types.points.CurvedPoint;
import com.anthonycosenza.engine.loader.text.tables.types.points.FontPoint;
import com.anthonycosenza.engine.loader.text.tables.types.points.StraightPoint;
import com.anthonycosenza.engine.space.rendering.UI.Canvas;
import com.anthonycosenza.engine.util.math.Color;
import com.anthonycosenza.engine.util.math.vector.Vector2i;

import java.util.List;

public class FontAtlasGenerator
{
    
    private FontAtlasGenerator(){}
    
    
    
    public static Canvas getAtlas(int fontSize, Font font)
    {
        Canvas canvas = new Canvas(2000, 1000);
        Color color = new Color(0, 0, 0, 255);
        //White gutter line.
        canvas.drawLine(100, 100, 100, 255, 0, 100, 1999, 100);
        
        List<List<FontPoint>> paths = font.getGlyph(1);
        
        /*
         * Each character is made up of multiple paths. The hole cut out from a D or the dot over an i.
         * We loop through each of those paths and draw them.
         */
        for(List<FontPoint> path: paths)
        {
            for(int i = 0; i < path.size(); i++)
            {
                FontPoint point = path.get(i);
                FontPoint nextPoint = (i + 1 < path.size() ? path.get(i + 1) : path.get(0));
                
                if(nextPoint instanceof StraightPoint)
                {
                    canvas.drawLine(color,
                            nextPoint.getPosition().x(), nextPoint.getPosition().y() + 100,
                            point.getPosition().x(), point.getPosition().y() + 100);
                }
                else if(nextPoint instanceof CurvedPoint cPoint)
                {
                    canvas.bezier(color,
                            point.getPosition().add(0, 100, new Vector2i()),
                            cPoint.getControlPoint1().add(0, 100, new Vector2i()),
                            cPoint.getControlPoint2().add(0, 100, new Vector2i()),
                            cPoint.getPosition().add(0, 100, new Vector2i()));
                }
                else throw new RuntimeException("What kind of point are you? " + nextPoint.getClass());
            }
            
            //canvas.fill(color, 150, 105);
        }
        
        return canvas;
    }
}
