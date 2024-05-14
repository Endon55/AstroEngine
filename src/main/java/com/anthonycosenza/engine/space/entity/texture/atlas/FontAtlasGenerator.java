package com.anthonycosenza.engine.space.entity.texture.atlas;

import com.anthonycosenza.engine.loader.text.Font;
import com.anthonycosenza.engine.loader.text.tables.types.Glyph;
import com.anthonycosenza.engine.loader.text.tables.types.points.CurvedPoint;
import com.anthonycosenza.engine.loader.text.tables.types.points.FontPoint;
import com.anthonycosenza.engine.loader.text.tables.types.points.StraightPoint;
import com.anthonycosenza.engine.space.rendering.UI.Canvas;
import com.anthonycosenza.engine.util.math.BezierCurves;
import com.anthonycosenza.engine.util.math.Color;
import com.anthonycosenza.engine.util.math.Lines;
import com.anthonycosenza.engine.util.math.vector.Vector2;
import com.anthonycosenza.engine.util.math.vector.Vector2i;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FontAtlasGenerator
{
    
    private FontAtlasGenerator(){}
    

    public static Atlas getFilledAtlas(float fontSize, Font font)
    {
        List<Canvas> preAtlas = new ArrayList<>();
        //Numbers
        /*for(int i = 48; i <= 57; i++)
        {
            preAtlas.add(createLetter(i, fontSize, font));
        }*/
        //Uppercase
        /*for(int i = 65; i <= 90; i++)
        {
            preAtlas.add(createLetter(i, fontSize, font));
        }*/
        //preAtlas.add(createLetter('R', fontSize, font));
        //preAtlas.add(createLetter('W', fontSize, font));
        preAtlas.add(createLetter('C', fontSize, font));
        preAtlas.add(createLetter('Q', fontSize, font));
        //preAtlas.add(createLetter('O', fontSize, font));
        
        //preAtlas.add(getCanvas(74, fontSize, font));
        /*//Lowercase
        for(int i = 97; i <= 122; i++)
        {
            preAtlas.add(getCanvas(i, fontSize, font));
        }*/
        
        return new CanvasAtlas(1, 1920, preAtlas);
    }
    
    private static Vector2i findDimensions(List<List<Vector2i>> paths)
    {
        int xMax = 0;
        int yMax = 0;
        for(List<Vector2i> path : paths)
        {
            for(Vector2i point : path)
            {
                if(point.x() > xMax) xMax = point.x();
                if(point.y() > yMax) yMax = point.y();
            }
        }
        return new Vector2i(xMax, yMax);
    }
    
    private static Canvas createLetter(int charCode, float fontSize, Font font)
    {
        Color color = new Color(0, 0, 0, 255);
        Glyph glyph = font.getGlyph(charCode).getAtSize(font.getUnitsPerEm(), fontSize);
        glyph.removeCurves(5f);
        glyph.shiftOffset();
        
        /*List<List<Vector2i>> straightPaths = new ArrayList<>();
        
        for(List<FontPoint> path : glyph.getPaths())
        {
            List<Vector2i> newPath = new ArrayList<>();
            for(FontPoint point : path)
            {
                newPath.add(point.getPosition());
            
            }
            straightPaths.add(newPath);
        }*/
        System.out.println(glyph.getWidth());
        System.out.println(glyph.getHeight());
        int width = (int) Math.ceil(glyph.getWidth());
        int height = (int) Math.ceil(glyph.getHeight());
        Canvas canvas = new Canvas(width, height);
        
        evaluate(width, height, glyph, color, canvas);
        /*
         * The horizontal algorithm isn't perfect, it leaves strips out of the letter.
         * Those strips can be heavily minimized by doing a second pass vertically.
         * Suddenly strips become single pixels which can be filled easily enough
         * doing a third pass and checking for single empty pixels.
         *
         * It's not efficient but it works---mostly. There's still some slight artifacting
         * depending on Font Size.
         */
        //evaluateRows(glyph.getWidth(), glyph.getHeight(), color, straightPaths, canvas);
        //evaluateColumns(glyph.getWidth(), glyph.getHeight(), color, straightPaths, canvas);
        //canvas.fillStraglers(3, color);
        
        return canvas;
    }
    
    private static List<List<Vector2>> convertPoints(int xOffset, int yOffset, float fontSize, List<List<FontPoint>> paths)
    {
        List<List<Vector2>> straightPaths = new ArrayList<>();
        
        float smoothness = 11f;
        float timeInc = 1 / smoothness;
        List<Vector2> straightPoints;
    
        Vector2 previous = null;
        for(List<FontPoint> path : paths)
        {
            straightPoints = new ArrayList<>();
            for(int i = 0; i < path.size(); i++)
            {
                FontPoint point = path.get(i);
                if(point instanceof StraightPoint)
                {
                    Vector2 vect = new Vector2(point.getPosition().x(), point.getPosition().y());
                
                    vect.add(xOffset, yOffset).mult(fontSize);
                
                    if(!vect.equals(previous))
                    {
                        straightPoints.add(vect);
                    }
                    previous = vect;
                }
                else
                {
                    CurvedPoint cPoint = (CurvedPoint) point;
                    /*
                     * J needs to be inclusive of 1
                     * time into 2 sections means 3 points
                     * 0, .5, 1
                     */
                    for(int j = 0; j <= smoothness; j++)
                    {
                        FontPoint prevPoint = path.get(i - 1 >= 0 ? i - 1 : path.size() - 1);
                    
                        Vector2 curvedSegment = BezierCurves.bezier(timeInc * j,
                                prevPoint.getPosition(),
                                cPoint.getControlPoint1(),
                                cPoint.getControlPoint2(),
                                cPoint.getPosition());
                    
                        Vector2 curvedI = curvedSegment.add(xOffset, yOffset).mult(fontSize);
                        /*
                         * This is consolidating flat points into a single point.
                         * Once a curve is split into line segments and rounded to integers it's quite likely that multiple sequential points
                         * fall on the same vertical or horizontal plane, instead of having 3 line segments they're combined into 1 line segment,
                         * this makes checking way more efficient and reduces errors.
                         */
                        if(straightPoints.size() >= 2 && straightPoints.get(straightPoints.size() - 1)
                                .y() == curvedI.y() &&
                                straightPoints.get(straightPoints.size() - 2).y() == curvedI.y())
                        {
                            straightPoints.set(straightPoints.size() - 1, curvedI);
                        }
                        else if(straightPoints.size() >= 2 && straightPoints.get(straightPoints.size() - 1)
                                .x() == curvedI.x() &&
                                straightPoints.get(straightPoints.size() - 2).x() == curvedI.x())
                        {
                            straightPoints.set(straightPoints.size() - 1, curvedI);
                        }
                        else
                        {
                            straightPoints.add(curvedI);
                        }
                        previous = curvedI;
                    }
                
                }
            }
            straightPaths.add(straightPoints);
        }
        return straightPaths;
    }
    
    private static void evaluate(int width, int height, Glyph glyph, Color color, Canvas canvas)
    {
        
        List<List<FontPoint>> straightPaths = glyph.getPaths();
        System.out.println(straightPaths);
        for(List<FontPoint> path : straightPaths)
        {
            for(int i = 0; i < path.size(); i++)
            {
                FontPoint point = path.get(i);
                FontPoint nextPoint = (i + 1 < path.size() ? path.get(i + 1) : path.get(0));
                
                canvas.drawLine(color,
                        (int) point.getPosition().x(), (int) point.getPosition().y(),
                        (int) nextPoint.getPosition().x(), (int) nextPoint.getPosition().y());
            }
        }
        
        List<Vector2i> setPixels = new ArrayList<>();
        int leftIntersections = 0;
        int rightIntersections = 0;
        for(int y = 0; y < height; y++)
        {
            setPixels.clear();
            for(int x = 0; x < width; x++)
            {
                if( canvas.hasColor(x, y))
                {
                    setPixels.add(new Vector2i(x, y));
                }
            }
            
            for(int x = 0; x < width; x++)
            {
                leftIntersections = 0;
                rightIntersections = 0;
                
                for(Vector2i setPixel : setPixels)
                {
                    if(setPixel.x() > x)
                    {
                        rightIntersections++;
                    }
                    else if(setPixel.x() < x)
                    {
                        leftIntersections++;
                    }
                }
                if((rightIntersections & 1) == 1 && (leftIntersections & 1) == 1)
                {
                    canvas.setPixel(color, x, y);
                }
            }
        }
        
        
        /*
        for(int y = 0; y < height; y++)
        {
            r0.set(0, y);
            r1.set(width, y);
            
            intersections.clear();
            //Pre-cache all the collisions for this y value.
            for(List<Vector2i> path : straightPaths)
            {
                for(int i = 0; i < path.size(); i++)
                {
                    Vector2i point = path.get(i);
                    Vector2i nextPoint = (i + 1 < path.size() ? path.get(i + 1) : path.get(0));
                    
                    Vector2 inter = Lines.intersection(r0, r1, point, nextPoint);
                    if(inter != null)
                    {
                        Vector2i intersection = inter.getVector2i();
                        
                        //nextPoint.y() + (nextPoint.y() > point.y() ? -.0001f : .0001f)
                        if(Lines.checkY(intersection.y(), point.y(),
                                nextPoint.y() + (nextPoint.y() > point.y() ? -.0001f : .0001f)))
                        {
                            if(point.y() == y)
                            {
                                Vector2i previousPoint = (i - 1 < 0 ? path.get(path.size() - 1) : path.get(i - 1));
                                double angle = Lines.angle(point, previousPoint, nextPoint);
                                if(angle > 1 && angle < 5)
                                {
                                    intersections.add(intersection);
                                }
                            }
                            else intersections.add(intersection);
                        }
                    }
                }
            }
            //Iterate through the row and evaluate each pixel using the pre-cached value.
            for(int x = 0; x < width; x++)
            {
                leftIntersections = 0;
                rightIntersections = 0;
                //Check all points to the right
                for(Vector2i intersection : intersections)
                {
                    *//*
                     * Check if the line intersection is to the right or to the left, and increment that side.
                     *//*
                    if(Lines.checkX(intersection.x(), x, r1.x()))
                    {
                        rightIntersections++;
                    }
                    else if(Lines.checkX(intersection.x(), r0.x(), x))
                    {
                        leftIntersections++;
                    }
                }
                *//*
                 * This is probably overly complicated but fuck it, it works.
                 *
                 * When evaluating left and right hits, if one of them is 0 then it necessarily means it's outside the letter either to the left or right.
                 * If we're left or right of the shape we need to be as strict as possible when determining if we should draw a pixel.
                 * Otherwise, errors lead to horizontal lines that span the whole canvas.
                 *
                 * If we're pretty sure we're in the shape then we want to double-check that we have an odd number of hits in either direction,
                 * even on one and odd on the other usually means that we double counted a point on the even one.
                 *//*
                if((rightIntersections & 1) == 1 && (leftIntersections & 1) == 1)
                {
                    canvas.setPixel(color, x, y);
                }
            }
            *//*Color color1 = new Color(50, 100, 150);
            for(Vector2i intersection :
                    intersections)
            {
                canvas.setPixel(color1, intersection.x(), y);
            }*//*
        }*/
    }
    private static void evaluateRows(int width, int height, Color color, List<List<Vector2i>> straightPaths,  Canvas canvas)
    {
        Vector2i r0 = new Vector2i(0, 0);
        Vector2i r1 = new Vector2i(width, 0);
        int leftIntersections = 0;
        int rightIntersections = 0;
        List<Vector2i> intersections = new ArrayList<>();
    
        for(int y = 0; y < height; y++)
        {
            r0.set(0, y);
            r1.set(width, y);
            
            intersections.clear();
            //Pre-cache all the collisions for this y value.
            for(List<Vector2i> path : straightPaths)
            {
                for(int i = 0; i < path.size(); i++)
                {
                    Vector2i point = path.get(i);
                    Vector2i nextPoint = (i + 1 < path.size() ? path.get(i + 1) : path.get(0));
                
                    Vector2 inter = Lines.intersection(r0, r1, point, nextPoint);
                    if(inter != null)
                    {
                        Vector2i intersection = inter.getVector2i();
                    
                        //nextPoint.y() + (nextPoint.y() > point.y() ? -.0001f : .0001f)
                        if(Lines.checkY(intersection.y(), point.y(),
                                nextPoint.y() + (nextPoint.y() > point.y() ? -.0001f : .0001f)))
                        {
                            if(point.y() == y)
                            {
                                Vector2i previousPoint = (i - 1 < 0 ? path.get(path.size() - 1) : path.get(i - 1));
                                double angle = Lines.angle(point, previousPoint, nextPoint);
                                if(angle > 1 && angle < 5)
                                {
                                    intersections.add(intersection);
                                }
                            }
                            else intersections.add(intersection);
                        }
                    }
                }
            }
            //Iterate through the row and evaluate each pixel using the pre-cached value.
            for(int x = 0; x < width; x++)
            {
                leftIntersections = 0;
                rightIntersections = 0;
                //Check all points to the right
                for(Vector2i intersection : intersections)
                {
                    /*
                     * Check if the line intersection is to the right or to the left, and increment that side.
                     */
                    if(Lines.checkX(intersection.x(), x, r1.x()))
                    {
                        rightIntersections++;
                    }
                    else if(Lines.checkX(intersection.x(), r0.x(), x))
                    {
                        leftIntersections++;
                    }
                }
                /*
                 * This is probably overly complicated but fuck it, it works.
                 *
                 * When evaluating left and right hits, if one of them is 0 then it necessarily means it's outside the letter either to the left or right.
                 * If we're left or right of the shape we need to be as strict as possible when determining if we should draw a pixel.
                 * Otherwise, errors lead to horizontal lines that span the whole canvas.
                 *
                 * If we're pretty sure we're in the shape then we want to double-check that we have an odd number of hits in either direction,
                 * even on one and odd on the other usually means that we double counted a point on the even one.
                 */
                if((rightIntersections & 1) == 1 && (leftIntersections & 1) == 1)
                {
                    canvas.setPixel(color, x, y);
                }
            }
            /*Color color1 = new Color(50, 100, 150);
            for(Vector2i intersection :
                    intersections)
            {
                canvas.setPixel(color1, intersection.x(), y);
            }*/
        }
    }
    private static void evaluateColumns(int width, int height, Color color, List<List<Vector2i>> straightPaths, Canvas canvas)
    {
        Vector2i c0 = new Vector2i(0, 0);
        Vector2i c1 = new Vector2i(0, height);
        int topIntersections = 0;
        int bottomIntersections = 0;
        List<Vector2i> intersections = new ArrayList<>();
    
        for(int x = 0; x < width; x++)
        {
            c0.set(x, 0);
            c1.set(x, height);
        
            intersections.clear();
            //Pre-cache all the collisions for this x value.
            for(List<Vector2i> path : straightPaths)
            {
                for(int i = 0; i < path.size(); i++)
                {
                    Vector2i point = path.get(i);
                    Vector2i nextPoint = (i + 1 < path.size() ? path.get(i + 1) : path.get(0));
                
                    Vector2 inter = Lines.intersection(c0, c1, point, nextPoint);
                    if(inter != null)
                    {
                        Vector2i intersection = inter.getVector2i();
                    
                        //(nextPoint.y() > point.y() ? nextPoint.y() -.0001 : nextPoint.y() + .0001)
                        if(Lines.checkX(intersection.x(), point.x(), nextPoint.x() + (nextPoint.x() > point.x() ? -.0001f : .0001f)))
                        {
                            if(point.x() == x)
                            {
                                Vector2i previousPoint = (i - 1 < 0 ? path.get(path.size() - 1) : path.get(i - 1));
                                double angle = Lines.angle(point, previousPoint, nextPoint);
                                if(angle > 1.5 && angle < 3.5)
                                {
                                    intersections.add(intersection);
                                }
                            }
                            else intersections.add(intersection);
                        }
                    }
                }
            }
            //Iterate through the column and evaluate each pixel using the pre-cached value.
            for(int y = 0; y < height; y++)
            {
                topIntersections = 0;
                bottomIntersections = 0;
                //Check all points to the right
                for(Vector2i intersection : intersections)
                {
                    /*
                     * Check if the line intersection is to the right or to the left, and increment that side.
                     */
                    if(Lines.checkY(intersection.y(), y, c1.y()))
                    {
                        bottomIntersections++;
                    }
                    else if(Lines.checkY(intersection.y(), y, c0.y()))
                    {
                        topIntersections++;
                    }
                }
                if((topIntersections & 1) == 1 && (bottomIntersections & 1) == 1)
                {
                    canvas.setPixel(color, x, y);
                }
            }
            /*Color color1 = new Color(50, 100, 150);
            for(Vector2i intersection :
                    intersections)
            {
                canvas.setPixel(color1, x, intersection.y());
            }*/
        }
    }
}
