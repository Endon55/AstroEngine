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
import com.anthonycosenza.engine.util.math.vector.LineSegment;
import com.anthonycosenza.engine.util.math.vector.Vector2;
import com.anthonycosenza.engine.util.math.vector.Vector2i;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
        //preAtlas.add(createLetter('C', fontSize, font));
        //preAtlas.add(createLetter('Q', fontSize, font));
        preAtlas.add(createLetter('R', fontSize, font));
        //preAtlas.add(createLetter('O', fontSize, font));
        
        //preAtlas.add(getCanvas(74, fontSize, font));
        /*//Lowercase
        for(int i = 97; i <= 122; i++)
        {
            preAtlas.add(getCanvas(i, fontSize, font));
        }*/
        
        return new CanvasAtlas(1, 1920, preAtlas);
    }
    
    private static Canvas createLetter(int charCode, float fontSize, Font font)
    {
        Color color = new Color(0, 0, 0, 255);
        Glyph glyph = font.getGlyph(charCode).getAtSize(font.getUnitsPerEm(), fontSize);
        glyph.removeCurves(11f);
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
        int width = (int) Math.ceil(glyph.getWidth());
        int height = (int) Math.ceil(glyph.getHeight());
        Canvas canvas = new Canvas(width, height);
        //canvas.setPixel(new Color(0, 255, 0), 0, 0);
        signedTrapezoid(width, height, glyph, color, canvas);
        //evaluate(width, height, glyph, color, canvas);
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
    
    
    private static void signedTrapezoid(int width, int height, Glyph glyph, Color color, Canvas canvas)
    {
        float subPixelSize = 4;
        float subPixelRatio = 1 / subPixelSize;
        List<List<Vector2>> expandedPaths = expandPoints(subPixelSize, glyph.getPaths());
    
        //Turn the points into line segments.
        List<LineSegment> edges = new ArrayList<>();
        for(List<Vector2> path : expandedPaths)
        {
            for(int i = 0; i < path.size(); i++)
            {
                Vector2 point = path.get(i);
                Vector2 nextPoint = (i + 1 < path.size() ? path.get(i + 1) : path.get(0));
                edges.add(new LineSegment(point, nextPoint));
            }
        }
        //Sort the edges.
        
            //val *= subPixelSize;
/*            edges.sort(Comparator.comparingInt((LineSegment segment) -> (int) segment.getPoint2().x())
                    .thenComparingInt(segment -> (int) segment.getPoint2().y()));
    */
        float subPixelWidth = subPixelSize * subPixelSize;
        float subPixelScalar = subPixelWidth / 256;
        int bads = 0;
        for(int y = 0; y < height; y++)
        {
            //Accumulates along the scanline
            float area = 0;
            float cover = 0;
            System.out.println("------------");
            for(int x = 0; x < width; x++)
            {
                for(int i = 0; i < edges.size(); i++)
                {
                    LineSegment edge = edges.get(i);
                    int x1i = (int) edge.getPoint1().x();
                    int y1i = (int) edge.getPoint1().y();
                    int x2i = (int) edge.getPoint1().x();
                    int y2i = (int) edge.getPoint1().y();
                    
                    if(x == Math.min(x1i, x2i) && y == Math.min(y1i, y2i))
                    {
                        /*
                         * Flip the points so that instead of measuring area of the pixel left of the line,
                         * we measure the area to the right of the line
                         */
                        float y1 = (edge.getPoint1().y() - y) / subPixelRatio;
                        float y2 = (edge.getPoint2().y() - y) / subPixelRatio;
    
                        float x1 = (edge.getPoint1().x() - x) / subPixelRatio;
                        float x2 = (edge.getPoint2().x() - x) / subPixelRatio;
    
                        float segCover = y2 - y1;
                        if(segCover == 0) continue;
                        y1 = subPixelSize - y1;
                        y2 = subPixelSize - y2;
                        x1 = subPixelSize -  x1;
                        x2 = subPixelSize - x2;
    
                        cover += segCover;
                        //System.out.println(edge.getPoint2().x() + ", " + x);
                        //System.out.println(x1 + ", " + y1 + " | " + x2 + ", " + y2);
                        /*
                         * seg area needs to be multiplied by 2 for the next part of the formula, fortunately triangles and right trapezoids
                         * have dividing by 2 in the formula, so we can skip the extra multiplication.
                         */
                        //Means it's a rectangle
                        if(x1 == x2)
                        {
                            area += (x1 * segCover) * 2;
                        }
                        //It's a triangle
                        else if(x1 == 0 || x2 == 0)
                        {
                            area += (segCover * (x2 - x1));
                        }
                        //It's a right trapezoid, split into a triangle and a square component
                        else
                        {
                            area += (x1 + x2) * segCover;
                        }
                        System.out.println(cover);
                        //Means we're dealing with a triangle not a trapezoid
                    }
                }
                //Once we've updated the rolling values we set the pixel.
                float effectiveArea = subPixelSize * cover - (area / 2);
                //System.out.println(effectiveArea);
                //scale from 0 to 256
                //System.out.println("Cover: " + cover + " EA: " + effectiveArea + " Area: " + area);
                effectiveArea *= subPixelScalar;
                canvas.addPixel(0, 0, 0, effectiveArea, x, y);
            }
            if(cover != 0)
            {
                System.out.println("bad");
            }
            if(cover != 0) bads++;
        }
        float badsRatio = ((int)((bads / (float) height) * 100)) / 100f;
        System.out.println(badsRatio);
        /*
        
        for(LineSegment edge : edges)
        {
                int x = (int) edge.getPoint1().x();
                int y = (int) edge.getPoint1().y();
                float x1 = edge.getPoint1().x() - x;
                float y1 = edge.getPoint1().y() - y;
                
                float x2 = edge.getPoint2().x() - x;
                float y2 = edge.getPoint2().y() - y;
                
                float area = 0;
                float cover = 0;
                float width
                //Means it's a rectangle
                if(x2 == x1)
                {
                    area = x1 * (y2 - y1);
                }
                if(y2 == y1)
                {
                
                }
                //Means we're dealing with a triangle not a trapezoid
                
    
    
        }
        */
        /*for(int y = 0; y < height; y++)
        {
            for(int x = 0; x < width; x++)
            {
                
            
            }
        }*/
        
        //width == subPixelSize
        for(List<Vector2> path : expandedPaths)
        {
            int rowCover = 0;
            for(int i = 0; i < path.size(); i++)
            {
                Vector2 point = path.get(i);
                Vector2 nextPoint = (i + 1 < path.size() ? path.get(i + 1) : path.get(0));
    
                int cover = (int) ((nextPoint.y() - point.y()) / subPixelSize);
                //float area = ();
            }
        }
        
        
/*        Vector2 r0 = new Vector2(0, 0);
        Vector2 r1 = new Vector2(width, 0);
        System.out.println(glyph.getPaths());
        List<Vector2> intersections = new ArrayList<>();
        Vector2 lastHit = new Vector2();
        for(int y = 0; y < height; y++)
        {
            r0.set(0, y);
            r1.set(width, y);
        
            intersections.clear();
            //Pre-cache all the collisions for this y value.
            for(List<FontPoint> path : glyph.getPaths())
            {
                for(int i = 0; i < path.size(); i++)
                {
                    Vector2 point = path.get(i).getPosition();
                    Vector2 nextPoint = (i + 1 < path.size() ? path.get(i + 1) : path.get(0)).getPosition();
                
                    Vector2 intersection = Lines.intersection(r0, r1, point, nextPoint);
                    if(intersection == null) continue;
                
                    //nextPoint.y() + (nextPoint.y() > point.y() ? -.0001f : .0001f)
                    if(Lines.checkY(intersection.y(), point.y(),
                            nextPoint.y()))
                    {
                        intersections.add(intersection);
                    }
                }
            }
            //Iterate through the row and evaluate each pixel using the pre-cached value.
            for(int x = 0; x < width; x++)
            {
                //Check all points to the right
                for(Vector2 intersection : intersections)
                {
                    *//*
                     * Check if the line intersection is to the right or to the left, and increment that side.
                     *//*
                    
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
                *//*if((rightIntersections & 1) == 1 && (leftIntersections & 1) == 1)
                {
                    canvas.setPixel(color, x, y);
                }*//*
            }*/
            /*Color color1 = new Color(50, 100, 150);
            for(Vector2i intersection :
                    intersections)
            {
                canvas.setPixel(color1, intersection.x(), y);
            }
        }*/
    }
    
    private static List<List<Vector2>> expandPoints(float subPixelSize, List<List<FontPoint>> fontPaths)
    {
        List<List<Vector2>> snappedPaths = new ArrayList<>();
        float pixelSize = subPixelSize;
        //Round each point to the nearest subpixel
        Vector2 previous = null;
        for(List<FontPoint> path : fontPaths)
        {
            List<Vector2> snappedPoints = new ArrayList<>();
            for(FontPoint point : path)
            {
                Vector2 vector = new Vector2(Math.round(point.getPosition().x() * pixelSize) / pixelSize,
                        Math.round(point.getPosition().y() * pixelSize) / pixelSize);
                if(!vector.equals(previous))
                {
                    snappedPoints.add(vector);
                    previous = vector;
                }
            }
            snappedPaths.add(snappedPoints);
        }
        List<List<Vector2>> snappedPaths2 = new ArrayList<>();
        //Add point to include every y
        for(List<Vector2> path : snappedPaths)
        {
            List<Vector2> snappedPoints = new ArrayList<>();
            for(int i = 0; i < path.size(); i++)
            {
                Vector2 point = path.get(i);
                //Always add the first point
                snappedPoints.add(point);
            
                Vector2 nextPoint = (i + 1 < path.size() ? path.get(i + 1) : path.get(0));
            
                float y = point.y();
                float m = (nextPoint.y() - point.y()) / (nextPoint.x() - point.x());
                //System.out.println(m);
                if(point.y() < nextPoint.y())
                {
                    while(y < nextPoint.y())
                    {
                        if(y == (int) y)
                        {
                            y++;
                        }
                        else
                        {
                            y = (float) Math.ceil(y);
                        }
                        if(y < nextPoint.y())
                        {
                            float x = point.x() + ((y - point.y()) / m);
                            Vector2 vector = new Vector2(
                                    Math.round(x * pixelSize) / pixelSize, y);
                            snappedPoints.add(vector);
                        }
                    }
                }
                else
                {
                    while(y > nextPoint.y())
                    {
                        if(y == Math.ceil(y))
                        {
                            y--;
                        }
                        else
                        {
                            y = (int) y;
                        }
                        if(y > nextPoint.y())
                        {
                            float x = point.x() + ((y - point.y()) / m);
                            Vector2 vector = new Vector2(
                                    Math.round(x * pixelSize) / pixelSize, y);
                            snappedPoints.add(vector);
                        }
                    }
                }
            }
            snappedPaths2.add(snappedPoints);
        }
        //Add point to include every x
    
        List<List<Vector2>> snappedPaths3 = new ArrayList<>();
        for(List<Vector2> path : snappedPaths2)
        {
            List<Vector2> snappedPoints = new ArrayList<>();
            for(int i = 0; i < path.size(); i++)
            {
                Vector2 point = path.get(i);
                //Always add the first point
                snappedPoints.add(point);
            
                Vector2 nextPoint = (i + 1 < path.size() ? path.get(i + 1) : path.get(0));
                float x = point.x();
            
                float m = (nextPoint.y() - point.y()) / (nextPoint.x() - point.x());
                //System.out.println(m);
                if(point.x() < nextPoint.x())
                {
                    while(x < nextPoint.x())
                    {
                        if(x == (int) x)
                        {
                            x++;
                        }
                        else
                        {
                            x = (float) Math.ceil(x);
                        }
                        if(x < nextPoint.x())
                        {
                            float y = (x - point.x()) * m + point.y();
                            Vector2 vector = new Vector2(
                                    x, Math.round(y * pixelSize) / pixelSize);
                            snappedPoints.add(vector);
                        }
                    }
                }
                else
                {
                    while(x > nextPoint.x())
                    {
                        if(x == Math.ceil(x))
                        {
                            x--;
                        }
                        else
                        {
                            x = (int) x;
                        }
                        if(x > nextPoint.x())
                        {
                            float y = (x - point.x()) * m + point.y();
                            Vector2 vector = new Vector2(
                                    x, Math.round(y * pixelSize) / pixelSize);
                            snappedPoints.add(vector);
                        }
                    }
                }
            }
            snappedPaths3.add(snappedPoints);
        }
        return snappedPaths3;
    }
    
    
    
    private static void evaluate(int width, int height, Glyph glyph, Color color, Canvas canvas)
    {
    
        Vector2 r0 = new Vector2(0, 0);
        Vector2 r1 = new Vector2(width, 0);
        System.out.println(glyph.getPaths());
        int leftIntersections = 0;
        int rightIntersections = 0;
        List<Vector2> intersections = new ArrayList<>();
        Vector2 lastHit = new Vector2();
        for(int y = 0; y < height; y++)
        {
            r0.set(0, y);
            r1.set(width, y);
        
            intersections.clear();
            //Pre-cache all the collisions for this y value.
            for(List<FontPoint> path : glyph.getPaths())
            {
                for(int i = 0; i < path.size(); i++)
                {
                    Vector2 point = path.get(i).getPosition();
                    Vector2 nextPoint = (i + 1 < path.size() ? path.get(i + 1) : path.get(0)).getPosition();
                
                    Vector2 intersection = Lines.intersection(r0, r1, point, nextPoint);
                    if(intersection == null) continue;
                    
                    //nextPoint.y() + (nextPoint.y() > point.y() ? -.0001f : .0001f)
                    if(Lines.checkY(intersection.y(), point.y(),
                            nextPoint.y()))
                    {
                        if(point.y() == y)
                        {
                            Vector2 previousPoint = (i - 1 < 0 ? path.get(path.size() - 1) : path.get(i - 1)).getPosition();
                            double angle = Lines.angle(point,previousPoint, nextPoint);
                            if(angle > 1 && angle < 5)
                            {
                                if(!intersection.approxEquals(lastHit))
                                {
                                    intersections.add(intersection);
                                    lastHit = intersection;
                                }
                            }
                        }
                        else if(!intersection.approxEquals(lastHit))
                        {
                            intersections.add(intersection);
                            lastHit = intersection;
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
                for(Vector2 intersection : intersections)
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
    
}
