package com.anthonycosenza.engine.render;

import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class Mesh
{
    private int vertexCount;
    private final int vaoID;
    private final List<Integer> vboIDList;
    
    public Mesh(float[] positions, float[] colors, int[] indices)
    {
        this.vertexCount = indices.length;
        vboIDList = new ArrayList<>();

        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);
        
        //Coordinate Data Attribute
        int positionsVboID = glGenBuffers();
        vboIDList.add(positionsVboID);
        FloatBuffer positionsBuffer = MemoryUtil.memAllocFloat(positions.length);
        positionsBuffer.put(positions).flip();
        glBindBuffer(GL_ARRAY_BUFFER, positionsVboID);
        glBufferData(GL_ARRAY_BUFFER, positionsBuffer, GL_STATIC_DRAW);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
    
        //Coordinate Color Attribute
        int colorVboID = glGenBuffers();
        vboIDList.add(colorVboID);
        FloatBuffer colorBuffer = MemoryUtil.memAllocFloat(colors.length);
        colorBuffer.put(colors).flip();
        glBindBuffer(GL_ARRAY_BUFFER, colorVboID);
        glBufferData(GL_ARRAY_BUFFER, colorBuffer, GL_STATIC_DRAW);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);
        
        //Coordinate Index Attribute
        int idxVboID = glGenBuffers();
        vboIDList.add(idxVboID);
        IntBuffer indicesBuffer = MemoryUtil.memAllocInt(indices.length);
        indicesBuffer.put(indices).flip();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, idxVboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

        
        MemoryUtil.memFree(positionsBuffer);
        MemoryUtil.memFree(colorBuffer);
        MemoryUtil.memFree(indicesBuffer);
        
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

    }
    
    public Mesh(float[] positions, float[] textureCoords, int[] indices, Texture texture)
    {
        this.vertexCount = indices.length;
        vboIDList = new ArrayList<>();
        
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);
        
        //Coordinate Data Attribute
        int positionsVboID = glGenBuffers();
        vboIDList.add(positionsVboID);
        FloatBuffer positionsBuffer = MemoryUtil.memAllocFloat(positions.length);
        positionsBuffer.put(positions).flip();
        glBindBuffer(GL_ARRAY_BUFFER, positionsVboID);
        glBufferData(GL_ARRAY_BUFFER, positionsBuffer, GL_STATIC_DRAW);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        
        //Coordinate Color Attribute
        int textCoordsVboID = glGenBuffers();
        vboIDList.add(textCoordsVboID);
        FloatBuffer textureCoordsBuffer = MemoryUtil.memAllocFloat(textureCoords.length);
        textureCoordsBuffer.put(textureCoords).flip();
        glBindBuffer(GL_ARRAY_BUFFER, textCoordsVboID);
        glBufferData(GL_ARRAY_BUFFER, textureCoordsBuffer, GL_STATIC_DRAW);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
        
        //Coordinate Index Attribute
        int idxVboID = glGenBuffers();
        vboIDList.add(idxVboID);
        IntBuffer indicesBuffer = MemoryUtil.memAllocInt(indices.length);
        indicesBuffer.put(indices).flip();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, idxVboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);
        
        
        MemoryUtil.memFree(positionsBuffer);
        MemoryUtil.memFree(textureCoordsBuffer);
        MemoryUtil.memFree(indicesBuffer);
        
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
        
    }
    
    public int getVertexCount()
    {
        return vertexCount;
    }
    
    public void setNumVertices(int numVertices)
    {
        this.vertexCount = numVertices;
    }
    
    public int getVaoID()
    {
        return vaoID;
    }
    
    public void cleanup()
    {
        vboIDList.forEach(GL30::glDeleteBuffers);
        glDeleteVertexArrays(vaoID);
    }
    
}
