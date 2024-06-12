package com.anthonycosenza.engine.space.entity;

import org.lwjgl.opengl.GL30;
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

public abstract class Mesh
{
    private transient int vaoID = -1;
    private transient List<Integer> vboIDs;
    
    private transient int vertexCount;
    private transient boolean initialized = false;
    
    public Mesh()
    {
    
    }
    public Mesh(float[] vertices, int[] indices, float[] textureCoordinates)
    {
        set(vertices, indices, textureCoordinates);
    }
    
    
    public void set(float[] vertices, int[] indices, float[] textureCoordinates)
    {
        if(vaoID != -1) cleanup();
        
        if(textureCoordinates.length == 0)
        {
            textureCoordinates = new float[(vertices.length / 3) * 2];
        }
        
        this.vertexCount = indices.length;
        vboIDs = new ArrayList<>();

        //Vertex Array Object - This array holds all the stuff that gets passed into the Vertex shader.
        vaoID = glGenVertexArrays();
        //Setting this array as the active "working directory"
        glBindVertexArray(vaoID);
    
        int vbo = glGenBuffers();
        vboIDs.add(vbo);
        FloatBuffer pointsBuffer = MemoryUtil.memAllocFloat(vertices.length);
        //FloatBuffer pointsBuffer = stack.callocFloat(vertices.length);
        pointsBuffer.put(0, vertices);
        //Bind a new buffer within the vertex array context, and specifies the type of data stored.
        //More buffer types - https://registry.khronos.org/OpenGL-Refpages/gl4/html/glBindBuffer.xhtml
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
    
        //https://docs.gl/gl4/glBufferData
        //Add the data to the previously bound buffer. Re-specifying the type of data held within.
        //Usage is entirely for optimization and allows open gl to more intelligently handle the data but never interferes with its access.
        glBufferData(GL_ARRAY_BUFFER, pointsBuffer, GL_STATIC_DRAW);
    
        //Marks the buffer at index 0 as ready for use.
        glEnableVertexAttribArray(0);
        //Tells OpenGL how to use the data.
        //Since all data is passed as linear arrays, those arrays need to be split up into vectors of N size in this case 3 to represent x, y, and z coordinates for a single point.
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

        vbo = glGenBuffers();
        vboIDs.add(vbo);
        FloatBuffer textureBuffer = MemoryUtil.memCallocFloat(textureCoordinates.length);
        //FloatBuffer textureBuffer = stack.callocFloat(textureCoordinates.length);
        textureBuffer.put(0, textureCoordinates);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, textureBuffer, GL_STATIC_DRAW);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
        
        //Indices
        vbo = glGenBuffers();
        vboIDs.add(vbo);
        IntBuffer indicesBuffer = MemoryUtil.memCallocInt(indices.length);
        //IntBuffer indicesBuffer = stack.callocInt(indices.length);
        indicesBuffer.put(0, indices);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vbo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

        //Set buffer 0 and vertex array 0 as active.
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
        initialized = true;
    }
    
    public int getVertexCount()
    {
        return vertexCount;
    }
    
    public void bind()
    {
        if(!initialized)
        {
            initialize();
            if(!initialized) throw new RuntimeException("Mesh not initialized before attempting to bind it.");
        }
        glBindVertexArray(vaoID);
    }
    
    public void cleanup()
    {
        vboIDs.forEach(GL30::glDeleteBuffers);
        glDeleteVertexArrays(vaoID);
    }
    
    public abstract void initialize();
}
