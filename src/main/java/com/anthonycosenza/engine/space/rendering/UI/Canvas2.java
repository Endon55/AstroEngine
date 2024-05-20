package com.anthonycosenza.engine.space.rendering.UI;

import com.anthonycosenza.engine.space.entity.Mesh;
import com.anthonycosenza.engine.space.entity.texture.Image;
import com.anthonycosenza.engine.space.entity.texture.Texture;
import com.anthonycosenza.engine.space.entity.texture.atlas.CanvasAtlas;
import com.anthonycosenza.engine.space.shape.ShapeBuilder;
import com.anthonycosenza.engine.util.math.Color;
import com.anthonycosenza.engine.util.math.vector.Vector2i;

import java.util.Stack;

public class Canvas2
{
    private int width;
    private int height;
    private int rowWidth;
    private int[] pixels;
    private int channels;
    private Mesh mesh;
    
    /*
     * Canvas coordinates start bottom left of the screen, up is positive y, and right is positive x.
     */

    

    public Canvas2(int width, int height, Color fill)
    {
        this.channels =4;
        this.width = width;
        this.height = height;
        pixels = new int[width * height];
    
        for(int i = 0; i < width * height; i++)
        {
            setPixel(fill, i % width, i / width);
        }
        mesh = ShapeBuilder.square((2f * width) / 1920, (2f * height) / 1080);
    }

    
    public boolean hasColor(int x, int y)
    {
        y = (height - 1) - y;
        int index = y * rowWidth + (x * channels);
        if(index >= getPixelData().length || index < 0) return false;
        //Skip rgb by adding 3 and only evaluate alpha.
        return pixels[index + 3] != 0;
    }
    
    public Color getColor(int x, int y)
    {
        Color color = new Color();
        y = (height - 1) - y;
        int index = y * rowWidth + (x * channels);
        color.r(pixels[index++]);
        color.g(pixels[index++]);
        color.b(pixels[index++]);
        color.a(pixels[index++]);
        return color;
    }
    public void setPixel(Color color, int posX, int posY)
    {
        setPixel(color.r(), color.g(), color.b(), color.a(), posX, posY);
    }
    /*
     * Does not update the OpenGL texture.
     */
    private void setPixel(float r, float g, float b, float a, int posX, int posY)
    {
        if(posY >= height || posY < 0 || posX >= width || posX < 0) return;
        posY = (height - 1) - posY;
        
        int color  = (int)(r * 255) << 24 | (int) (g * 255) << 16 | (int) (b * 255) << 8 | (int) (a * 255);
        int index = width * posY + posX;
        pixels[index] = color;
    }
    
    public void addPixel(Color color, int posX, int posY)
    {
        addPixel(color.r(), color.g(), color.b(), color.a(), posX, posY);
    }
    public void addPixel(float r, float g, float b, float a, int posX, int posY)
    {
        posY = (height - 1) - posY;
        int index = posY * rowWidth + (posX * channels);
        if(index >= getPixelData().length || index < 0) return;
        pixels[index] += r;
        pixels[index++] %= 255;
        pixels[index] += g;
        pixels[index++] %= 255;
        pixels[index] += b;
        pixels[index++] %= 255;
        pixels[index] += a;
        pixels[index] %= 255;
    }
    
    public void drawVerticalLine(float r, float g, float b, float a, int x, int y0, int y1)
    {
        if(y0 > y1)
        {
            int temp = y0;
            y0 = y1;
            y1 = temp;
        }
        int dist = y1 - y0;
        for(int i = 0; i < dist; i++)
        {
            setPixel(r, g, b, a, x, y0 + i);
        }
    }
    
    public void drawHorizontalLine(float r, float g, float b, float a, int x0, int x1, int y)
    {
        if(x0 > x1)
        {
            int temp = x0;
            x0 = x1;
            x1 = temp;
        }
        int dist = x1 - x0;
        for(int i = 0; i < dist; i++)
        {
            setPixel(r, g, b, a, x0 + i, y);
        }
    }
    
    public void drawFlatLine(float r, float g, float b, float a, int x0, int y0, int x1, int y1)
    {
        //Horizontal
        if(y0 == y1)
        {
            drawHorizontalLine(r, g, b, a, x0, x1, y0);
        }
        else //Vertical
        {
            drawVerticalLine(r, g, b, a, x0, y0, y1);
        }
    }
    public void drawLine(Color color, int x0, int y0, int x1, int y1)
    {
        drawLine(color.r(), color.g(), color.b(), color.a(), x0, y0, x1, y1);
    }
    
    /*
     * https://www.baeldung.com/cs/bresenhams-line-algorithm
     *
     * For whatever reason the function can't do flat lines so there's a check to do that at the beginning.
     */
    public void drawLine(float r, float g, float b, float a, int x0, int y0, int x1, int y1)
    {
        if(x0 == x1) drawVerticalLine(r, g, b, a, x0, y0, y1);
        if(y0 == y1) drawHorizontalLine(r, g, b, a, x0, x1, y0);
        

        
        int distX = Math.abs(x1 - x0);
        int slopeX = (x0 < x1 ? 1 : -1);
        
        int distY = -Math.abs(y1 - y0);
        int slopeY = (y0 < y1 ? 1 : -1);
        
        int error = distX + distY;
        int error2;
        while(true)
        {
            setPixel(r, g, b, a, x0, y0);
            
            if(x0 == x1 && y0 == y1) break;
            
            error2 = error * 2;
            
            if(error >= distY)
            {
                if(x0 == x1) break;
                
                error += distY;
                x0 = x0 + slopeX;
            }
            if(error2 <= distX)
            {
                if(y0 == y1) break;
                
                error += distX;
                y0 += slopeY;
            }
        }
    }
    
    private void setCircleArc(float r, float g, float b, float a, int xc, int yc, int x, int y)
    {
        setPixel(r, g, b, a, xc + x, yc + y);
        setPixel(r, g, b, a, xc - x, yc + y);
        setPixel(r, g, b, a, xc + x, yc - y);
        setPixel(r, g, b, a, xc - x, yc - y);

        setPixel(r, g, b, a, xc + y, yc + x);
        setPixel(r, g, b, a, xc - y, yc + x);
        setPixel(r, g, b, a, xc + y, yc - x);
        setPixel(r, g, b, a, xc - y, yc - x);
    }
    
    public void drawCircle(Color color, float radius, int xPos, int yPos, boolean filled)
    {
        int x = 0;
        int y = (int) radius;
        int d = (int) (3 - 2 * radius);
        setCircleArc(color.r(), color.g(), color.b(), color.a(), xPos, yPos, x, y);
        while(y >= x)
        {
            // for each pixel we will
            // draw all eight pixels
        
            x++;
        
            // check for decision parameter
            // and correspondingly
            // update d, x, y
            if(d > 0)
            {
                y--;
                d = d + 4 * (x - y) + 10;
            }
            else
                d = d + 4 * x + 6;
            setCircleArc(color.r(), color.g(), color.b(), color.a(), xPos, yPos, x, y);
        }
    
        if(filled)
        {
            fill(color, xPos, yPos);
        }
    }
    
    
    public void drawPoint(Color color, float radius, int xPos, int yPos)
    {
        drawCircle(color, radius, xPos, yPos, true);
    }
    
    public void fillStraglers(int fillEdges, Color color)
    {
        if(fillEdges > 4) throw new RuntimeException("Fill Edges can't be larger than 4: " + fillEdges);
        int edges = 0;
        for(int y = 0; y < height; y++)
        {
            for(int x = 0; x < width; x++)
            {
            
                //No color in current pixel, but has color in the 4 cardinal pixels.
                if(!hasColor(x, y))
                {
                    edges = 0;
                    edges += (hasColor(x + 1, y) ? 1 : 0);
                    edges += (hasColor(x - 1, y) ? 1 : 0);
                    edges += (hasColor(x, y + 1) ? 1 : 0);
                    edges += (hasColor(x, y - 1) ? 1 : 0);
                }
                if(edges >= fillEdges)
                {
                    setPixel(color, x, y);
                }
            }
        }
    }
    
    public void fill(Color color, int xPos, int yPos)
    {
        Stack<Vector2i> positions = new Stack<>();
        if(xPos > width - 1 || yPos > height - 1) return;
        positions.add(new Vector2i(xPos, yPos));
        while(!positions.isEmpty())
        {
            Vector2i pixel = positions.pop();
            setPixel(color, pixel.x(), pixel.y());
            
            //Left Pixel
            if(pixel.x() > 0 && pixel.x() < width && pixel.y() < height && !color.equals(getColor(pixel.x() - 1, pixel.y())))
            {
                positions.push(new Vector2i(pixel.x() - 1, pixel.y()));
            }
            //Right Pixel
            if(pixel.x() > 0 && pixel.x() < width - 1 &&pixel.y() < height && !color.equals(getColor(pixel.x() + 1, pixel.y())))
            {
                positions.push(new Vector2i(pixel.x() + 1, pixel.y()));
            }
            //Down Pixel
            if(pixel.x() < width && pixel.y() > 0 && pixel.y() < height && !color.equals(getColor(pixel.x(), pixel.y() - 1)))
            {
                positions.push(new Vector2i(pixel.x(), pixel.y() - 1));
            }
            //Up Pixel
            if(pixel.x() < width && pixel.y() > 0 && pixel.y() < height - 1 && !color.equals(getColor(pixel.x(), pixel.y() + 1)))
            {
                positions.push(new Vector2i(pixel.x(), pixel.y() + 1));
            }
        }
    }
    
    
    
    public void bezier(Color color, Vector2i... points)
    {
        float distance = 0;
        Vector2i lastPoint = null;
        for(Vector2i point : points)
        {
            if(lastPoint != null)
            {
                distance += lastPoint.distance(point);
            }
            else lastPoint = point;
        }
        //System.out.println("Distance: " + distance);
        //System.out.println("Raw Distance: " + (points[0].distance(points[points.length - 1])));
        bezier(color, (int) distance * 20, points);
    }
    /*
     * This Bézier curve algorithm calculates the x, y pixel coordinate at time t,
     * to get an accurate graphing the number of timesteps needs to be the number of pixels.
     * Obviously you can't know that easily so call the other function instead where it's
     * estimated based on distance.
     */
    public void bezier(Color color, int timeSteps, Vector2i... points)
    {
        //System.out.println(Arrays.toString(points));
        int pointCount = points.length;
        if(pointCount < 3 || pointCount > 7) throw new RuntimeException("Bezier doesn't support less than 3 or more than 7 points : " + pointCount);
        
        /*
         * Determine which function to call with the number of arguments given.
         */
        float timeStep = 1f / timeSteps;
        float time = 0;
        float x;
        float y;
        int x2 = 0;
        int y2 = 0;
        while(time <= 1)
        {
            switch(pointCount)
            {
                case 3 ->
                {
                    x = bezierQuadratic(points[0].x(), points[1].x(), points[2].x(), time);
                    y = bezierQuadratic(points[0].y(), points[1].y(), points[2].y(), time);
                }
                case 4 ->
                {
                    x = bezierCubic(points[0].x(), points[1].x(), points[2].x(), points[3].x(), time);
                    y = bezierCubic(points[0].y(), points[1].y(), points[2].y(), points[3].y(), time);
                }
                case 5 ->
                {
                    x = bezierQuartic(points[0].x(), points[1].x(), points[2].x(), points[3].x(), points[4].x(),time);
                    y = bezierQuartic(points[0].y(), points[1].y(), points[2].y(), points[3].y(), points[4].y(), time);
                }
                case 6 ->
                {
                    x = bezierQuintic(points[0].x(), points[1].x(), points[2].x(), points[3].x(), points[4].x(), points[5].x(), time);
                    y = bezierQuintic(points[0].y(), points[1].y(), points[2].y(), points[3].y(), points[4].y(), points[5].y(), time);
                }
                case 7 ->
                {
                    x = bezierSextic(points[0].x(), points[1].x(), points[2].x(), points[3].x(), points[4].x(), points[5].x(), points[6].x(),time);
                    y = bezierSextic(points[0].y(), points[1].y(), points[2].y(), points[3].y(), points[4].y(), points[5].y(), points[6].y(), time);
                }
                default -> throw new RuntimeException("We shouldn't be here.");
            }
            setPixel(color, (int) x, (int) y);
            int xi = Math.round(x);
            int yi = Math.round(y);
            
            setPixel(color, xi, yi);
            
            time += timeStep;
        }
    }
    
    private float mixBezier(float a, float b, float t)
    {
        // degree 1
        return a * (1.0f - t) + b * t;
    }
    
    private float bezierQuadratic(float A, float B, float C, float t)
    {
        // degree 2
        float AB = mixBezier(A, B, t);
        float BC = mixBezier(B, C, t);
        return mixBezier(AB, BC, t);
    }
    
    private float bezierCubic(float A, float B, float C, float D, float t)
    {
        // degree 3
        float ABC = bezierQuadratic(A, B, C, t);
        float BCD = bezierQuadratic(B, C, D, t);
        return mixBezier(ABC, BCD, t);
    }
    
    private float bezierQuartic(float A, float B, float C, float D, float E, float t)
    {
        // degree 4
        float ABCD = bezierCubic(A, B, C, D, t);
        float BCDE = bezierCubic(B, C, D, E, t);
        return mixBezier(ABCD, BCDE, t);
    }
    
    private float bezierQuintic(float A, float B, float C, float D, float E, float F, float t)
    {
        // degree 5
        float ABCDE = bezierQuartic(A, B, C, D, E, t);
        float BCDEF = bezierQuartic(B, C, D, E, F, t);
        return mixBezier(ABCDE, BCDEF, t);
    }
    
    private float bezierSextic(float A, float B, float C, float D, float E, float F, float G, float t)
    {
        // degree 6
        float ABCDEF = bezierQuintic(A, B, C, D, E, F, t);
        float BCDEFG = bezierQuintic(B, C, D, E, F, G, t);
        return mixBezier(ABCDEF, BCDEFG, t);
    }

    /*public void updateTexture()
    {
        texture.updateFullTexture(width, height, pixels);
    }*/
    
    
    public int[] getPixelData()
    {
        return pixels;
    }

    public int getWidth()
    {
        return width;
    }
    
    public int getHeight()
    {
        return height;
    }
    

    public Texture getTexture()
    {
        return new Texture(width, height, pixels);
    }
    
    public Mesh getMesh()
    {
        return mesh;
    }
}
