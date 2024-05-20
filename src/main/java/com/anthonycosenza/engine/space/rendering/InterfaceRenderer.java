package com.anthonycosenza.engine.space.rendering;

import com.anthonycosenza.engine.loader.text.TextStrip;
import com.anthonycosenza.engine.space.Window;
import com.anthonycosenza.engine.space.entity.UIMesh;
import com.anthonycosenza.engine.space.entity.texture.Texture;
import com.anthonycosenza.engine.space.rendering.projection.Projection2d;
import com.anthonycosenza.engine.space.rendering.shader.ShaderData;
import com.anthonycosenza.engine.space.rendering.shader.ShaderPipeline;
import com.anthonycosenza.engine.space.rendering.shader.UniformMap;
import com.anthonycosenza.engine.util.math.vector.Vector2;
import imgui.ImDrawData;
import imgui.ImFontAtlas;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiKey;
import imgui.type.ImInt;
import org.lwjgl.glfw.GLFWKeyCallback;

import java.nio.ByteBuffer;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_BACKSPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DELETE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_END;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ENTER;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_HOME;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_INSERT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_ENTER;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_PAGE_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_PAGE_UP;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_TAB;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_SHORT;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL14.GL_FUNC_ADD;
import static org.lwjgl.opengl.GL14.glBlendEquation;
import static org.lwjgl.opengl.GL14.glBlendFuncSeparate;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STREAM_DRAW;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;

public class InterfaceRenderer
{
    private ShaderPipeline shaderPipeline;
    private UniformMap uniforms;
    private Texture atlasTexture;
    private UIMesh uiMesh;
    private Vector2 scale;
    GLFWKeyCallback keyCallback;
    
    
    public InterfaceRenderer(Window window)
    {
        scale = new Vector2();
        shaderPipeline = new ShaderPipeline(new ShaderData("resources/shaders/interface.vert", GL_VERTEX_SHADER),
                new ShaderData("resources/shaders/interface.frag", GL_FRAGMENT_SHADER));
        
        uniforms = new UniformMap(shaderPipeline.getProgramID());
        uniforms.createUniform("projectionMatrix");
        createInterface(window);
    }

    private void setKeyCallback(Window window)
    {
        ImGuiIO io = ImGui.getIO();
        io.setKeyMap(ImGuiKey.Tab, GLFW_KEY_TAB);
        io.setKeyMap(ImGuiKey.LeftArrow, GLFW_KEY_LEFT);
        io.setKeyMap(ImGuiKey.RightArrow, GLFW_KEY_RIGHT);
        io.setKeyMap(ImGuiKey.UpArrow, GLFW_KEY_UP);
        io.setKeyMap(ImGuiKey.DownArrow, GLFW_KEY_DOWN);
        io.setKeyMap(ImGuiKey.PageUp, GLFW_KEY_PAGE_UP);
        io.setKeyMap(ImGuiKey.PageDown, GLFW_KEY_PAGE_DOWN);
        io.setKeyMap(ImGuiKey.Home, GLFW_KEY_HOME);
        io.setKeyMap(ImGuiKey.End, GLFW_KEY_END);
    
        io.setKeyMap(ImGuiKey.Insert, GLFW_KEY_INSERT);
        io.setKeyMap(ImGuiKey.Delete, GLFW_KEY_DELETE);
        io.setKeyMap(ImGuiKey.Backspace, GLFW_KEY_BACKSPACE);
        io.setKeyMap(ImGuiKey.Space, GLFW_KEY_SPACE);
        io.setKeyMap(ImGuiKey.Enter, GLFW_KEY_ENTER);
        io.setKeyMap(ImGuiKey.Escape, GLFW_KEY_ESCAPE);
        io.setKeyMap(ImGuiKey.KeyPadEnter, GLFW_KEY_KP_ENTER);
        

    }
    
    private void createInterface(Window window)
    {
        uiMesh = new UIMesh();
        ImGui.createContext();
        ImGuiIO io = ImGui.getIO();
        io.setIniFilename(null);
        io.setDisplaySize(window.getWidth(), window.getHeight());
        
        ImFontAtlas fontAtlas = io.getFonts();
        ImInt width = new ImInt();
        ImInt height = new ImInt();
        ByteBuffer buffer = fontAtlas.getTexDataAsRGBA32(width, height);
        atlasTexture = new Texture(width.get(), height.get(), buffer);
        
        uniforms.createUniform("scale");
    }
    public void resize(int width, int height)
    {
        ImGui.getIO().setDisplaySize(width, height);
    }
    
    public void render(Scene scene)
    {
        shaderPipeline.bind();
        
        //Seting up the rendering context
        glEnable(GL_BLEND);
        glBlendEquation(GL_FUNC_ADD);
        glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_DEPTH_TEST);
        glDisable(GL_CULL_FACE);
    
        
        uiMesh.bind();
        
        ImGuiIO io = ImGui.getIO();
        scale.set(2.0f / io.getDisplaySizeX(), -2.0f / io.getDisplaySizeY());
        uniforms.setUniform("scale", scale);
        
        ImDrawData drawData = ImGui.getDrawData();
        int numLists = drawData.getCmdListsCount();
        for(int i = 0; i < numLists; i++)
        {
            //We keep replacing the data in the mesh buffers.
            glBufferData(GL_ARRAY_BUFFER, drawData.getCmdListVtxBufferData(i), GL_STREAM_DRAW);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, drawData.getCmdListIdxBufferData(i), GL_STREAM_DRAW);
            
            int numCmds = drawData.getCmdListCmdBufferSize(i);
            for(int j = 0; j < numCmds; j++)
            {
                final int elements = drawData.getCmdListCmdBufferElemCount(i, j);
                final int indexOffset = drawData.getCmdListCmdBufferIdxOffset(i, j);
                final int indices = indexOffset * ImDrawData.SIZEOF_IM_DRAW_IDX;
                
                atlasTexture.bind();
                glDrawElements(GL_TRIANGLES, elements, GL_UNSIGNED_SHORT, indices);
            }
        }
        
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glDisable(GL_BLEND);
        
        
    }
    
    public void cleanup()
    {
        shaderPipeline.cleanup();
        uiMesh.cleanup();
    }
}
