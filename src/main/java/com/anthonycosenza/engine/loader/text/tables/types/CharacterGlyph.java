package com.anthonycosenza.engine.loader.text.tables.types;

import com.anthonycosenza.engine.loader.text.tables.types.hints.Hint;
import com.anthonycosenza.engine.loader.text.tables.types.points.CurvedPoint;
import com.anthonycosenza.engine.loader.text.tables.types.points.FontPoint;
import com.anthonycosenza.engine.loader.text.tables.types.points.StraightPoint;
import com.anthonycosenza.engine.util.math.BezierCurves;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class CharacterGlyph implements Glyph
{
    private float xMin = 10000;
    private float xMax = 0;
    private float yMin = 10000;
    private float yMax = 0;
    private int gutter = 0;
    private List<List<FontPoint>> paths;
    private List<Hint> hints;
    private float fontSize;
    private boolean hasCurves;
    
    public CharacterGlyph(float fontSize, List<Hint> hints, List<List<FontPoint>> paths)
    {
        this.fontSize = fontSize;
        this.paths = paths;
        this.hints = hints;
        setBoundingBox();
    }
    public CharacterGlyph(List<Hint> hints, List<List<FontPoint>> paths)
    {
        fontSize = -1;
        this.paths = paths;
        this.hints = hints;
        setBoundingBox();
    }
    
    @Override
    public List<Hint> getHints()
    {
        return hints;
    }
    
    private void setBoundingBox()
    {
        hasCurves = false;
        if(paths.isEmpty())
        {
            xMin = 0;
            yMin = 0;
            gutter = 0;
            return;
        }
        
        for(List<FontPoint> path : paths)
        {
            for(FontPoint point : path)
            {
                if(point instanceof CurvedPoint) hasCurves = true;
                Vector2f position = point.getPosition();
                
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
    public float getMaxX()
    {
        return xMax;
    }
    
    @Override
    public float getMinX()
    {
        return xMin;
    }
    
    @Override
    public float getMaxY()
    {
        return yMax;
    }
    
    @Override
    public float getMinY()
    {
        return yMin;
    }
    
    @Override
    public float getWidth()
    {
        return getMaxX() - getMinX();
    }
    
    @Override
    public float getSize()
    {
        return fontSize;
    }
    
    @Override
    public float getHeight()
    {
        return getMaxY() - getMinY();
    }
    
    @Override
    public void removeCurves(float smoothness)
    {
        List<List<FontPoint>> straightPaths = new ArrayList<>();
    
        float timeInc = 1 / smoothness;
        List<FontPoint> straightPoints;
    
        for(List<FontPoint> path : paths)
        {
            straightPoints = new ArrayList<>();
            for(int i = 0; i < path.size(); i++)
            {
                FontPoint point = path.get(i);
                if(point instanceof StraightPoint)
                {
                    straightPoints.add(point);
                }
                else
                {
                    CurvedPoint cPoint = (CurvedPoint) point;
                    /*
                     * J needs to be inclusive of 1
                     * time into 2 sections means 3 points
                     * 0, .5, 1
                     */
                    FontPoint prevPoint = path.get(i - 1 >= 0 ? i - 1 : path.size() - 1);
                    List<Vector2f> points = new ArrayList<>();
                    
                    for(int j = 0; j <= smoothness; j++)
                    {
                        Vector2f curvedSegment = BezierCurves.bezier(timeInc * j,
                                prevPoint.getPosition(),
                                cPoint.getControlPoint1(),
                                cPoint.getControlPoint2(),
                                cPoint.getPosition());
                    
                        /*
                         * This is consolidating flat points into a single point.
                         * Once a curve is split into line segments and rounded to integers it's quite likely that multiple sequential points
                         * fall on the same vertical or horizontal plane, instead of having 3 line segments they're combined into 1 line segment,
                         * this makes checking way more efficient and reduces errors.
                         */
                        if(points.size() >= 2 && points.get(points.size() - 1).y() == curvedSegment.y() &&
                                points.get(points.size() - 2).y() == curvedSegment.y())
                        {
                            points.set(points.size() - 1, curvedSegment);
                        }
                        else if(points.size() >= 2 && points.get(points.size() - 1)
                                .x() == curvedSegment.x() &&
                                points.get(points.size() - 2).x() == curvedSegment.x())
                        {
                            points.set(points.size() - 1, curvedSegment);
                        }
                        else
                        {
                            points.add(curvedSegment);
                        }
                    }
                    
                    for(Vector2f line : points)
                    {
                        straightPoints.add(new StraightPoint(cPoint.getHintMask(), line.x(), line.y()));
                    }
                    
                
                }
            }
            straightPaths.add(straightPoints);
        }
        hasCurves = false;
        paths = straightPaths;
    }
    
    @Override
    public void shiftOffset()
    {
        float xOffset = xMin * -1;
        float yOffset = yMin * -1;
        for(List<FontPoint> path : paths)
        {
            for(FontPoint point : path)
            {
                point.getPosition().add(xOffset, yOffset);
            }
        }
    }
    
    @Override
    public boolean hasCurves()
    {
        return hasCurves;
    }
    
    @Override
    public Glyph getAtSize(int unitsPerEm, float fontSize)
    {
        if(this.fontSize != -1) throw new RuntimeException("Glyph is already resized, create a new one from the master glyph.");
        if(fontSize < 0) throw new RuntimeException("Font size must be greater than 0: " + fontSize);
        
        List<List<FontPoint>> paths = new ArrayList<>();
        
        float ratio = fontSize / unitsPerEm;
        
        for(List<FontPoint> path : getPaths())
        {
            List<FontPoint> newPath = new ArrayList<>();
            for(FontPoint point : path)
            {
                newPath.add(point.copy().scale(ratio));
            
            }
            paths.add(newPath);
        }
        return new CharacterGlyph(fontSize, hints, paths);
    }
    
}
