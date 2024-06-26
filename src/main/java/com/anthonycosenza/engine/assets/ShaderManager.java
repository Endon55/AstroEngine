package com.anthonycosenza.engine.assets;

import com.anthonycosenza.engine.space.rendering.shader.ComputeShader;
import com.anthonycosenza.engine.space.rendering.shader.FragmentShader;
import com.anthonycosenza.engine.space.rendering.shader.Shader;
import com.anthonycosenza.engine.space.rendering.shader.ShaderPipeline;
import com.anthonycosenza.engine.space.rendering.shader.VertexShader;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glShaderSource;

public class ShaderManager
{
    private static final Map<Long, ShaderPipeline> shaderPipelines = new HashMap<>();
    private static final Map<Long, Integer> shaderResCompileIDs = new HashMap<>();
    private static ShaderPipeline defaultPipeline;
    
    
    private static int getCompileID(Shader shader)
    {
        Integer compileID = shaderResCompileIDs.get(shader.getResourceID());
        if(compileID == null)
        {
            compileID = compile(shader);
            shaderResCompileIDs.put(shader.getResourceID(), compileID);
        }
        return compileID;
    }
    
    public static ShaderPipeline getDefaultPipeline()
    {
        if(defaultPipeline == null)
        {
            defaultPipeline = createPipeline(VertexShader.DEFAULT, FragmentShader.DEFAULT);
            shaderPipelines.put(defaultPipeline.getResourceID(), defaultPipeline);
        }
        return defaultPipeline;
    }
    public static ShaderPipeline getPipeline(long pipelineID)
    {
        return shaderPipelines.get(pipelineID);
    }
 
    public static long hashShaders(VertexShader vertexShader, FragmentShader fragmentShader)
    {
        if(vertexShader == null) vertexShader = VertexShader.DEFAULT;
        if(fragmentShader == null) fragmentShader = FragmentShader.DEFAULT;
    
       return hash(vertexShader, fragmentShader);
    }
    private static long hash(Shader... shaders)
    {
        long total = 0;
        for(Shader shader : shaders)
        {
            total += shader.getResourceID() << 2;
        }
        return total;
    }
   
    /*
     * The goal here is to verify that we're not making more ShaderPipelines than we should.
     * If 2 pipelines are using the same shaders then there should only be 1 pipeline.
     */
    public static ShaderPipeline createPipeline(VertexShader vertex, FragmentShader fragment)
    {
        if(vertex == null) vertex = VertexShader.DEFAULT;
        if(fragment == null) fragment = FragmentShader.DEFAULT;
        
        return createPipe(vertex, fragment);
    }

    public static ShaderPipeline createPipeline(ComputeShader compute)
    {
        return createPipe(compute);
    }
    
    private static ShaderPipeline createPipe(Shader... shaders)
    {
        long hash = hash(shaders);
        ShaderPipeline pipeline = shaderPipelines.get(hash);
        if(pipeline == null)
        {
            int[] shaderIDs = new int[shaders.length];
            for(int i = 0; i < shaderIDs.length; i++)
            {
                shaderIDs[i] = compile(shaders[i]);
            }
            pipeline = new ShaderPipeline(hash, shaderIDs);
            shaderPipelines.put(hash, pipeline);
        }
        return pipeline;
    }
    
    public static int compile(Shader shader)
    {
        //Register a new shader with OpenGL
        int shaderID = glCreateShader(shader.getShaderType());
        
        //Load Shader code.
        String shaderCode = shader.getShaderCode();
        
        glShaderSource(shaderID, shaderCode);//FileUtils.getFileContents(shaderData.filePath));
        //Compile the loaded shader code.
        glCompileShader(shaderID);
        
        if(glGetShaderi(shaderID, GL_COMPILE_STATUS) == 0)
        {
            throw new RuntimeException("Error while compiling shader[" + shader + "] - " + glGetShaderInfoLog(shaderID, 1024) + " - Source - " + shader.getShaderCode());
        }
        
        return shaderID;
    }
    
    public static void cleanup()
    {
        for(Map.Entry<Long, Integer> compileID : shaderResCompileIDs.entrySet())
        {
            glDeleteShader(compileID.getValue());
        }
        for(Map.Entry<Long, ShaderPipeline> pipeline: shaderPipelines.entrySet())
        {
            pipeline.getValue().cleanup();
        }
    }
    
}
