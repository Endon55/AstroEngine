package com.anthonycosenza.engine.render;

import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
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

/**
 * A mesh consists of 3 parts
 */
public class Mesh
{
    private int vertexCount;
    //Vertex array object
    //The collection of data sets that define an object, position, color, texture data etc.
    private final int vaoID;
    //Vertex buffer object
    //Converts a float array into OpenGL form and sends it to OpenGL to manage, giving us a reference.
    private final List<Integer> vboIDList;
    
    /**
     *
     * @param positions, all of the unique vertices contained in our mesh in a flattened format. x1, y1, z1, x2, y2, z2...
     * @param normals, vector3 representing the average angle of the triangles facing direction for every vertex.
     * @param textureCoords, how the vertices of this mesh map to a 2d texture in a flattened 0 to 1 format. x1, y1, x2, y2...
     * @param indices, used to define triangles within a mesh, each element of the indices array needs to reference the index of the corresponding vertex from the positions array in a counter-clockwise fashion.
     */
    public Mesh(float[] positions, float[] normals, float[] textureCoords, int[] indices)
    {
        try(MemoryStack stack = MemoryStack.stackPush())
        {
            this.vertexCount = indices.length;
            vboIDList = new ArrayList<>();
            //Creating a new Vertex Array within OpenGL and storing its id.
            vaoID = glGenVertexArrays();
            //Set this array as the "Active" one we're working on.
            glBindVertexArray(vaoID);
    
            //Coordinate Data Attribute
            
            //Create an OpenGL buffer for position.
            int positionsVboID = glGenBuffers();
            vboIDList.add(positionsVboID);
            //Create a buffer and store the position data within it.
            FloatBuffer positionsBuffer = stack.callocFloat(positions.length);
            positionsBuffer.put(0, positions);
            //Set the position buffer as active
            glBindBuffer(GL_ARRAY_BUFFER, positionsVboID);
            //Transfer the FloatBuffer to OpenGL
            glBufferData(GL_ARRAY_BUFFER, positionsBuffer, GL_STATIC_DRAW);
            //Enable the newly created array
            glEnableVertexAttribArray(0);
            //Tells OpenGL how to use the data in the buffer. Size 3 because there's an X, Y, and Z component to each Float
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
    
            //Vertex normal Attribute
            int normalsVboID = glGenBuffers();
            vboIDList.add(normalsVboID);
            FloatBuffer normalsBuffer = stack.callocFloat(normals.length);
            normalsBuffer.put(0, normals);
            glBindBuffer(GL_ARRAY_BUFFER, normalsVboID);
            glBufferData(GL_ARRAY_BUFFER, normalsBuffer, GL_STATIC_DRAW);
            glEnableVertexAttribArray(1);
            glVertexAttribPointer(1, 3, GL_FLOAT, false,0, 0);
            
            
            //Coordinate Texture Attribute
            int textureVboID = glGenBuffers();
            vboIDList.add(textureVboID);
            FloatBuffer textureBuffer = stack.callocFloat(textureCoords.length);
            textureBuffer.put(0, textureCoords);
            glBindBuffer(GL_ARRAY_BUFFER, textureVboID);
            glBufferData(GL_ARRAY_BUFFER, textureBuffer, GL_STATIC_DRAW);
            glEnableVertexAttribArray(2);
            glVertexAttribPointer(2, 2, GL_FLOAT, false, 0, 0);
    
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
