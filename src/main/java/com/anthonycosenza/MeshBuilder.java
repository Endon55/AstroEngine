package com.anthonycosenza;


import com.anthonycosenza.text.TextStrip;

import java.util.Arrays;

public class MeshBuilder
{
    private float[] vertices;
    private int[] indices;
    private float[] colors;
    
    public MeshBuilder(){}
    

    public MeshBuilder(TextStrip strip)
    {
        //Generate a mesh based on the text within the strip.
        int textLength = strip.getText().length();
        
        if(textLength == 0) throw new RuntimeException("Text strip is invalid");
        int zValue = 0;
        int charWidth = 15;
        //Every mesh takes 4 verts for the first character(4-x,y,z coordinates 12 total) and then it only needs 2(6 total) more for each subsequent character.
        float[] vertices = new float[12 + 6 * (textLength - 1)];
        int vertIndex = 0;
        float textHeight = .1f;
        float textWidth = .1f;
        
        //+1 to include the leftmost vertices and then each loop adds the 2 on the right for each letter.
        for(int i = 0; i < textLength + 1; i++)
        {
            float x = i * textWidth;
            //Bottom coordinate for each letter
            vertices[vertIndex++] = x;
            vertices[vertIndex++] = 0;
            vertices[vertIndex++] = zValue;
            //Top coordinate for each letter
            vertices[vertIndex++] = x;
            vertices[vertIndex++] = textHeight;
            vertices[vertIndex++] = zValue;
        }
        //2 Triangles for each character, and 3 vertices for each triangle.
        int indices[] = new int[textLength * 2 * 3];
        int indIndex = 0;
        for(int i = 0; i < textLength; i++)
        {
            int topLeft = (1 + i * 2);
            int bottomLeft = (i * 2);
            int topRight = (1 + (i + 1) * 2);
            int bottomRight = ((i + 1) * 2);
            
            //First Triangle
            indices[indIndex++] = topLeft;
            indices[indIndex++] = bottomLeft;
            indices[indIndex++] = bottomRight;
    
            //Second Triangle
            indices[indIndex++] = bottomRight;
            indices[indIndex++] = topRight;
            indices[indIndex++] = topLeft;
            
            /*
            //First Triangle
            indices[indIndex++] = bottomRight;
            indices[indIndex++] = bottomLeft;
            indices[indIndex++] = topLeft;
    
            //Second Triangle
            indices[indIndex++] = topLeft;
            indices[indIndex++] = topRight;
            indices[indIndex++] = bottomRight;
             */
        }
        //Set a 3 digit color for each indices
        float[] colors = new float[indices.length];
        int colIndex = 0;
        for(int i = 0; i < indices.length / 3; i++)
        {
            colors[colIndex++] = .10f * i;
            colors[colIndex++] = .10f * i;
            colors[colIndex++] = .10f * i;
        }
    
        System.out.println("Vertices: " + Arrays.toString(vertices));
        System.out.println("Indices: " + Arrays.toString(indices));
        System.out.println("Colors: " + Arrays.toString(colors));
        setVertices(vertices);
        setIndices(indices);
        setColors(colors);
    }
    
    public MeshBuilder setVertices(float[] vertices)
    {
        this.vertices = vertices;
        return this;
    }
    
    public MeshBuilder setColors(float[] colors)
    {
        this.colors = colors;
        return this;
    }
    
    public MeshBuilder setIndices(int[] indices)
    {
        this.indices = indices;
        return this;
    }
    
    public Mesh build()
    {
        return new Mesh(vertices, indices, colors);
    }
}
