package com.anthonycosenza.engine.render.gui;

import com.anthonycosenza.engine.render.ShaderProgram;
import com.anthonycosenza.engine.render.model.Texture;
import com.anthonycosenza.engine.render.UniformsMap;
import com.anthonycosenza.engine.scene.Scene;
import com.anthonycosenza.engine.window.Window;
import imgui.ImDrawData;
import imgui.ImFontAtlas;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiKey;
import imgui.type.ImInt;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
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
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class GuiRenderer
{
    private GuiMesh guiMesh;
    private GLFWKeyCallback prevKeyCallback;
    private Vector2f scale;
    private ShaderProgram shaderProgram;
    private Texture texture;
    private UniformsMap uniformsMap;
    
    public GuiRenderer(Window window)
    {
        List<ShaderProgram.ShaderModuleData> shaderModuleDataList = new ArrayList<>();
        shaderModuleDataList.add(new ShaderProgram.ShaderModuleData("resources/shaders/gui.vert", GL_VERTEX_SHADER));
        shaderModuleDataList.add(new ShaderProgram.ShaderModuleData("resources/shaders/gui.frag", GL_FRAGMENT_SHADER));
        shaderProgram = new ShaderProgram(shaderModuleDataList);
        createUniforms();
        createUIResources(window);
        setupKeyCallBack(window);
    }
    
    public void render(Scene scene)
    {
        IGuiInstance guiInstance = scene.getGuiInstance();
        
        //No GUI has been created
        if(guiInstance == null)
        {
            return;
        }
        
        guiInstance.drawGui();
        
        shaderProgram.bind();
        
        //Enables transparency with GUI objects.
        glEnable(GL_BLEND);
        
        glBlendEquation(GL_FUNC_ADD);
        glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_DEPTH_TEST);
        glDisable(GL_CULL_FACE);
        
        glBindVertexArray(guiMesh.getVaoId());
        
        glBindBuffer(GL_ARRAY_BUFFER, guiMesh.getVerticesVBO());
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, guiMesh.getIndicesVBO());
        
        ImGuiIO io = ImGui.getIO();
        //Convert ImGui coordinates(0 to screenWidth/screenHeight) to OpenGL(-1 to 1)
        scale.x = 2.0f / io.getDisplaySizeX();
        scale.y = -2.0f / io.getDisplaySizeY();
        uniformsMap.setUniform("scale", scale);
    
        ImDrawData drawData = ImGui.getDrawData();
        int numLists = drawData.getCmdListsCount();
        for(int i = 0; i < numLists; i++)
        {
            //Send all the raw GUI data to an OpenGL buffer;
            glBufferData(GL_ARRAY_BUFFER, drawData.getCmdListVtxBufferData(i), GL_STREAM_DRAW);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, drawData.getCmdListIdxBufferData(i), GL_STREAM_DRAW);
            
            int numCmds = drawData.getCmdListCmdBufferSize(i);
            for(int j = 0; j < numCmds; j++)
            {
                final int elementCount = drawData.getCmdListCmdBufferElemCount(i, j);
                //Find where this element is in the buffer
                final int idxBufferOffset = drawData.getCmdListCmdBufferIdxOffset(i, j);
                //Find this elements indices in the buffer
                final int indices = idxBufferOffset * ImDrawData.SIZEOF_IM_DRAW_IDX;
                
                texture.bind();
                glDrawElements(GL_TRIANGLES, elementCount, GL_UNSIGNED_SHORT, indices);
            }
        }
        
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glDisable(GL_BLEND);
    }
    
    private void createUniforms()
    {
        uniformsMap = new UniformsMap(shaderProgram.getProgramID());
        uniformsMap.createUniform("scale");
        scale = new Vector2f();
    }
    
    private void createUIResources(Window window)
    {
        ImGui.createContext();
        
        ImGuiIO imGuiIO = ImGui.getIO();
        imGuiIO.setIniFilename(null);
        imGuiIO.setDisplaySize(window.getWidth(), window.getHeight());
        
        ImFontAtlas fontAtlas = ImGui.getIO().getFonts();
        ImInt width = new ImInt();
        ImInt height = new ImInt();
        ByteBuffer buffer = fontAtlas.getTexDataAsRGBA32(width, height);
        texture = new Texture(width.get(), height.get(), buffer);
        
        guiMesh = new GuiMesh();
    }
    
    private void setupKeyCallBack(Window window)
    {
        ImGuiIO io = ImGui.getIO();
        //Creates a mapping from GLFW specific bindings to ImGui bindings.
        io.setKeyMap(ImGuiKey.Tab, GLFW.GLFW_KEY_TAB);
        io.setKeyMap(ImGuiKey.LeftArrow, GLFW.GLFW_KEY_LEFT);
        io.setKeyMap(ImGuiKey.RightArrow, GLFW.GLFW_KEY_RIGHT);
        io.setKeyMap(ImGuiKey.UpArrow, GLFW.GLFW_KEY_UP);
        io.setKeyMap(ImGuiKey.DownArrow, GLFW.GLFW_KEY_DOWN);
        io.setKeyMap(ImGuiKey.PageUp, GLFW.GLFW_KEY_PAGE_UP);
        io.setKeyMap(ImGuiKey.PageDown, GLFW.GLFW_KEY_PAGE_DOWN);
        io.setKeyMap(ImGuiKey.Home, GLFW.GLFW_KEY_HOME);
        io.setKeyMap(ImGuiKey.End, GLFW.GLFW_KEY_END);
        io.setKeyMap(ImGuiKey.Insert, GLFW.GLFW_KEY_INSERT);
        io.setKeyMap(ImGuiKey.Delete, GLFW.GLFW_KEY_DELETE);
        io.setKeyMap(ImGuiKey.Backspace, GLFW.GLFW_KEY_BACKSPACE);
        io.setKeyMap(ImGuiKey.Space, GLFW.GLFW_KEY_SPACE);
        io.setKeyMap(ImGuiKey.Enter, GLFW.GLFW_KEY_ENTER);
        io.setKeyMap(ImGuiKey.Escape, GLFW.GLFW_KEY_ESCAPE);
        io.setKeyMap(ImGuiKey.KeyPadEnter, GLFW.GLFW_KEY_KP_ENTER);
        
        //Creates a key callback that forwards GLFW key presses to ImGui
        prevKeyCallback = glfwSetKeyCallback(window.getWindowHandle(), (handle, key, scancode, action, mods) ->
        {
            window.keyCallBack(key, action);
            if(!io.getWantCaptureKeyboard())
            {
                if(prevKeyCallback != null)
                {
                    prevKeyCallback.invoke(handle, key, scancode, action, mods);
                }
                
                return;
            }
            if(action == GLFW.GLFW_PRESS)
            {
                io.setKeysDown(key,  true);
            }
            else if(action == GLFW.GLFW_RELEASE)
            {
                io.setKeysDown(key, false);
            }
            io.setKeyCtrl(io.getKeysDown(GLFW.GLFW_KEY_LEFT_CONTROL) || io.getKeysDown(GLFW.GLFW_KEY_RIGHT_CONTROL));
            io.setKeyShift(io.getKeysDown(GLFW.GLFW_KEY_LEFT_SHIFT) || io.getKeysDown(GLFW.GLFW_KEY_RIGHT_SHIFT));
            io.setKeyAlt(io.getKeysDown(GLFW.GLFW_KEY_LEFT_ALT) || io.getKeysDown(GLFW.GLFW_KEY_RIGHT_ALT));
            //Windows key
            io.setKeySuper(io.getKeysDown(GLFW.GLFW_KEY_LEFT_SUPER) || io.getKeysDown(GLFW.GLFW_KEY_RIGHT_SUPER));
        });
        GLFW.glfwSetCharCallback(window.getWindowHandle(), (handle, c) ->
        {
            if(!io.getWantCaptureKeyboard())
            {
                return;
            }
            io.addInputCharacter(c);
        });
        
    }
    
    public void resize(int width, int height)
    {
        ImGuiIO imGuiIO = ImGui.getIO();
        imGuiIO.setDisplaySize(width, height);
    }
    
    public void cleanup()
    {
        shaderProgram.cleanup();
        texture.cleanup();
        if(prevKeyCallback != null)
        {
            prevKeyCallback.free();
        }
    }
}
