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
    
    public Mesh(float[] positions, float[] textureCoords, int[] indices)
    {
        try(MemoryStack stack = MemoryStack.stackPush())
        {
            this.vertexCount = indices.length;
            vboIDList = new ArrayList<>();
    
            vaoID = glGenVertexArrays();
            glBindVertexArray(vaoID);
    
            //Coordinate Data Attribute
            int positionsVboID = glGenBuffers();
            vboIDList.add(positionsVboID);
            FloatBuffer positionsBuffer = stack.callocFloat(positions.length);
            positionsBuffer.put(0, positions);
            glBindBuffer(GL_ARRAY_BUFFER, positionsVboID);
            glBufferData(GL_ARRAY_BUFFER, positionsBuffer, GL_STATIC_DRAW);
            glEnableVertexAttribArray(0);
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
    
            //Coordinate Texture Attribute
            int textureVboID = glGenBuffers();
            vboIDList.add(textureVboID);
            FloatBuffer textureBuffer = stack.callocFloat(textureCoords.length);
            textureBuffer.put(0, textureCoords);
            glBindBuffer(GL_ARRAY_BUFFER, textureVboID);
            glBufferData(GL_ARRAY_BUFFER, textureBuffer, GL_STATIC_DRAW);
            glEnableVertexAttribArray(1);
            glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
    
            //Coordinate Index Attribute
            int idxVboID = glGenBuffers();
            vboIDList.add(idxVboID);
            IntBuffer indicesBuffer = stack.callocInt(indices.length);
            indicesBuffer.put(0, indices);
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, idxVboID);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);
    
            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
        }
    }
    
    public int getVertexCount()
    {
        return vertexCount;
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
