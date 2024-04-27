package com.anthonycosenza.text;

import com.anthonycosenza.Mesh;
import com.anthonycosenza.MeshBuilder;
import com.anthonycosenza.math.vector.Vector2;
import com.anthonycosenza.math.vector.Vector3;

public class TextStrip
{
    private String text;
    private int size;
    private Vector3 color;
    private Vector2 position;
    private Font font;
    private Mesh mesh;
    
    public TextStrip(String text, int size,Vector3 color, Vector2 position, Font font)
    {
        this.text = text;
        this.size = size;
        this.color = color;
        this.position = position;
        this.font = font;
        
        this.mesh = new MeshBuilder(this).build();
    }
    
    public String getText()
    {
        return text;
    }
    
    public int getSize()
    {
        return size;
    }
    
    public Vector3 getColor()
    {
        return color;
    }
    
    public Vector2 getPosition()
    {
        return position;
    }
    
    public Font getFont()
    {
        return font;
    }
    
    public Mesh getMesh()
    {
        return mesh;
    }
}

