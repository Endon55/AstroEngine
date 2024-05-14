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
            preAtlas.add(getCanvas(i, fontSize, font));
        }*/
        //Uppercase
        /*for(int i = 65; i <= 90; i++)
        {
            preAtlas.add(createLetter(i, fontSize, font));
        }*/
        preAtlas.add(createLetter('R', fontSize, font));
        //preAtlas.add(createLetter('W', fontSize, font));
        //preAtlas.add(createLetter('I', fontSize, font));
        //preAtlas.add(getFilledLetter('Q', fontSize, font));
        //preAtlas.add(getFilledLetter('O', fontSize, font));
        
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
        glyph.removeCurves(11f);
        
        
    
        //System.out.println(glyph.getHints());
        
        
        List<List<Vector2i>> straightPaths = new ArrayList<>();
        
        for(List<FontPoint> path : glyph.getPaths())
        {
            List<Vector2i> newPath = new ArrayList<>();
            for(FontPoint point : path)
            {
                newPath.add(point.getPosition());
            
            }
            straightPaths.add(newPath);
        }
    
        Canvas canvas = new Canvas(glyph.getWidth(), glyph.getHeight());
        
        /*
         * First I need to replace each curve with some # of straight points.
         *
         * Second I iterate through each pixel and count intersections and light up pixel.
         */
        evaluateRows(glyph.getWidth(), glyph.getHeight(), color, straightPaths, canvas);
        evaluateColumns(glyph.getWidth(), glyph.getHeight(), color, straightPaths, canvas);
        
        canvas.fillStraglers(3, color);
        
        return canvas;
    }
    
    public static Atlas getOutlinedAtlas(float fontSize, Font font)
    {
        List<Canvas> preAtlas = new ArrayList<>();
        //Numbers
        /*for(int i = 48; i <= 57; i++)
        {
            preAtlas.add(getCanvas(i, fontSize, font));
        }*/
        //Uppercase
        /*for(int i = 65; i <= 90; i++)
        {
            preAtlas.add(getOutlinedLetter(i, fontSize, font));
        }*/
        //preAtlas.add(getOutlinedLetter('P', fontSize, font));
        //preAtlas.add(getOutlinedLetter('P', fontSize, font));
        preAtlas.add(getOutlinedLetter('R', fontSize, font));
        preAtlas.add(getOutlinedLetter('C', fontSize, font));
        //preAtlas.add(getOutlinedLetter('O', fontSize, font));
        
        //preAtlas.add(getCanvas(74, fontSize, font));
        /*//Lowercase
        for(int i = 97; i <= 122; i++)
        {
            preAtlas.add(getCanvas(i, fontSize, font));
        }*/
        
        return new CanvasAtlas(1, 1920, preAtlas);
    }
    
    
    private static List<List<Vector2i>> convertPoints(int xOffset, int yOffset, float fontSize, List<List<FontPoint>> paths)
    {
        List<List<Vector2i>> straightPaths = new ArrayList<>();
        
        float smoothness = 11f;
        float timeInc = 1 / smoothness;
        List<Vector2i> straightPoints;
    
        Vector2i previous = null;
        for(List<FontPoint> path : paths)
        {
            straightPoints = new ArrayList<>();
            for(int i = 0; i < path.size(); i++)
            {
                FontPoint point = path.get(i);
                if(point instanceof StraightPoint)
                {
                    Vector2i vect = new Vector2i(point.getPosition().x(), point.getPosition().y());
                
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
                    
                        Vector2i curvedI = curvedSegment.add(xOffset, yOffset).mult(fontSize).getVector2i();
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
            Color color1 = new Color(50, 100, 150);
            for(Vector2i intersection :
                    intersections)
            {
                canvas.setPixel(color1, x, intersection.y());
            }
        }
    }
    

    
    
    
    
    
    /*
    private static Canvas getFilledLetter(int charCode, float fontSize, Font font)
    {
        //72 pixels per point * how many points you want.
        fontSize = fontSize / font.getUnitsPerEm();
        //fontSize = 1;
        
        Color color = new Color(0, 0, 0, 255);
        //White gutter line.
        Glyph glyph = font.getGlyph(charCode);
        List<List<FontPoint>> paths = glyph.getPaths();
        System.out.println(paths);
        
        List<List<StraightPoint>> straightPaths = new ArrayList<>();
        //flip it to shift it the other way.
        int yOffset = glyph.getMinY() * -1;
        int xOffset = glyph.getMinX() * -1;
        int width = (int) (glyph.getWidth() * fontSize);
        int height = (int) (glyph.getHeight() * fontSize);
        
        Canvas canvas = new Canvas(width, height);
        //canvas.drawLine(100, 100, 100, 255, 0, 100, 1999, 100);
        
        *//*
         * First I need to replace each curve with some # of straight points.
         *
         * Second I iterate through each pixel and count intersections and light up pixel.
         *//*
        float smoothness = 20;
        float timeInc = 1 / smoothness;
        List<StraightPoint> straightPoints;
        
        Vector2i previous = null;
        for(List<FontPoint> path : paths)
        {
            straightPoints = new ArrayList<>();
            for(int i = 0; i < path.size(); i++)
            {
                FontPoint point = path.get(i);
                if(point instanceof StraightPoint)
                {
                    Vector2i vect = new Vector2i(point.getPosition());
                    vect.add(xOffset, yOffset).mult(fontSize);
                    
                    StraightPoint newStraight = new StraightPoint(vect);
                    straightPoints.add(newStraight);
                    previous = newStraight.getPosition();
                }
                else
                {
                    CurvedPoint cPoint = (CurvedPoint) point;
                    *//*
                     * J needs to be inclusive of 1
                     * time into 2 sections means 3 points
                     * 0, .5, 1
                     *//*
                    for(int j = 0; j <= smoothness; j++)
                    {
                        FontPoint prevPoint = path.get(i - 1 >= 0 ? i - 1 : path.size() - 1);
                        
                        Vector2i curvedPoint = BezierCurves.bezier(timeInc * j,
                                prevPoint.getPosition(),
                                cPoint.getControlPoint1(),
                                cPoint.getControlPoint2(),
                                cPoint.getPosition());
                        
                        curvedPoint.add(xOffset, yOffset).mult(fontSize);
                        *//*
                         * This is consolidating flat points into a single point.
                         * Once a curve is split into line segments and rounded to integers it's quite likely that multiple sequential points
                         * fall on the same vertical or horizontal plane, instead of having 3 line segments they're combined into 1 line segment,
                         * this makes checking way more efficient and reduces errors.
                         *//*
                        if(previous != null && !straightPoints.isEmpty() &&
                                (Lines.isHorizontal(previous, curvedPoint) || Lines.isVertical(previous, curvedPoint)))
                        {
                            straightPoints.set(straightPoints.size() - 1, new StraightPoint(curvedPoint));
                        }
                        else straightPoints.add(new StraightPoint(curvedPoint));
                        previous = curvedPoint;
                    }
                    
                }
            }
            straightPaths.add(straightPoints);
        }
        
        
        Vector2i l0 = new Vector2i(0, 0);
        Vector2i l1 = new Vector2i();
        Vector2i r0 = new Vector2i();
        Vector2i r1 = new Vector2i(glyph.getWidth(), 0);
        int leftIntersections = 0;
        int rightIntersections = 0;
        List<Vector2i> intersectedPoints = new ArrayList<>();
        
        for(int y = 0; y < height; y++)
        {
            for(int x = 0; x < width; x++)
            {
                leftIntersections = 0;
                rightIntersections = 0;
                r0.set(x, y);
                r1.set(glyph.getWidth(), y);
                l0.set(0, y);
                l1.set(x, y);
                intersectedPoints.clear();
                //Check all points to the right
                for(List<StraightPoint> path : straightPaths)
                {
                    for(int i = 0; i < path.size(); i++)
                    {
                        StraightPoint point = path.get(i);
                        StraightPoint nextPoint = (i + 1 < path.size() ? path.get(i + 1) : path.get(0));
                        
                        Vector2i intersection = Lines.intersection(r0, r1, point.getPosition(), nextPoint.getPosition());
                        
                        *//*
                         * I track each intersected point so that I can avoid double counting the 1px overlap from each line.
                         *
                         * and check to make sure that if there is an intersection, that it's within the y bounds of the line segment.
                         *//*
                        if(intersection == null
                                || intersectedPoints.contains(intersection)
                                || !Lines.checkY(intersection.y(), point.getPosition().y(), nextPoint.getPosition()
                                .y()))
                        {
                            continue;
                        }
                        *//*
                         * Check if the line intersection is to the right or to the left, and increment that side.
                         *//*
                        if(Lines.checkX(intersection.x(), r0.x(), r1.x()))
                        {
                            rightIntersections++;
                            intersectedPoints.add(intersection);
                        }
                        else if(Lines.checkX(intersection.x(), l0.x(), l1.x()))
                        {
                            leftIntersections++;
                            intersectedPoints.add(intersection);
                        }
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
                if((rightIntersections & 1) == 1 && (leftIntersections != 0))
                {
                    canvas.setPixel(color, x, y);
                }
                else if((leftIntersections & 1) == 1 && (rightIntersections != 0))
                {
                    canvas.setPixel(color, x, y);
                }
                else if((rightIntersections & 1) == 1 && (leftIntersections & 1) == 1)
                {
                    canvas.setPixel(color, x, y);
                }
            }
        }
        //Shows the pixels that form intersections.
        *//*Color dotColor = new Color(255, 0, 0);
        for(List<StraightPoint> path : straightPaths)
        {
            for(int i = 0; i < path.size(); i++)
            {
                FontPoint point = path.get(i);
             
                canvas.setPixel(dotColor, point.getPosition().x(), point.getPosition().y());
                //canvas.drawCircle(dotColor, 5, point.getPosition().x(), point.getPosition().y(), false);
            }
        }*//*
        return canvas;
    }*/
    private static Canvas getOutlinedLetter(int charCode, float fontSize, Font font)
    {
        //72 pixels per point * how many points you want.
        fontSize = fontSize / font.getUnitsPerEm();
        //fontSize = 1;
    
        Color color = new Color(255, 0, 0, 255);
        //White gutter line.
        Glyph glyph = font.getGlyph(charCode);
        List<List<FontPoint>> paths = glyph.getPaths();
        /*System.out.println(paths.stream().map(fontPoints ->
                fontPoints.stream().filter(fontPoint -> (fontPoint instanceof StraightPoint)).collect(Collectors.toList()))
                .collect(Collectors.toList()));*/
        //System.out.println(paths);
        //flip it to shift it the other way.
        int yOffset = glyph.getMinY() * -1;
        int xOffset = glyph.getMinX() * -1;
        int width = (int) (glyph.getWidth() * fontSize) + 1;
        int height = (int) (glyph.getHeight() * fontSize) + 1;
    
        Canvas canvas = new Canvas(width, height);
    
        /*
         * Each character is made up of multiple paths. The hole cut out from a D or the dot over an i.
         * We loop through each of those paths and draw them.
         */
    
        for(List<FontPoint> path : paths)
        {
            for(int i = 0; i < path.size(); i++)
            {
                FontPoint point = path.get(i);
                FontPoint nextPoint = (i + 1 < path.size() ? path.get(i + 1) : path.get(0));
                //System.out.println("FP: " + point + " NP: " + nextPoint);
                if(nextPoint instanceof StraightPoint)
                {
                    canvas.drawLine(color,
                            (int) ((nextPoint.getPosition().x() + xOffset) * fontSize), (int) ((nextPoint.getPosition().y() + yOffset) * fontSize),
                            (int) ((point.getPosition().x() + xOffset) * fontSize), (int) ((point.getPosition().y() + yOffset) * fontSize));
                }
                else if(nextPoint instanceof CurvedPoint cPoint)
                {
                    canvas.bezier(color,
                            point.getPosition().add(xOffset, yOffset, new Vector2i()).mult(fontSize),
                            cPoint.getControlPoint1().add(xOffset, yOffset, new Vector2i()).mult(fontSize),
                            cPoint.getControlPoint2().add(xOffset, yOffset, new Vector2i()).mult(fontSize),
                            cPoint.getPosition().add(xOffset, yOffset, new Vector2i()).mult(fontSize));
                }
                else throw new RuntimeException("What kind of point are you? " + nextPoint.getClass());
            }
        }
        
        return canvas;
    }

    public static Canvas getAtlas(int fontSize, Font font)
    {
        Canvas canvas = new Canvas(2000, 1000);
        Color color = new Color(0, 0, 0, 255);
        //White gutter line.
        canvas.drawLine(100, 100, 100, 255, 0, 100, 1999, 100);
        
        List<List<FontPoint>> paths = font.getGlyph(2).getPaths();
        
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
        }
        canvas.fill(color, 150, 105);
        
        
        //canvas.updateTexture();
        return canvas;
    }
}
