package com.anthonycosenza.engine.ui;

import com.anthonycosenza.engine.space.ProjectSettings;
import com.anthonycosenza.engine.space.Window;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiConfigFlags;
import org.lwjgl.glfw.GLFW;

import static org.lwjgl.glfw.GLFW.glfwGetCurrentContext;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;

public class ImGuiImpl
{
    /*
     * More information about this can be found at
     * https://github.com/SpaiR/imgui-java/blob/main/imgui-app/src/main/java/imgui/app/Window.java#L211
     * the ImGuiImpl Glfw/Gl3 code can be found under the lwjgl section.
     */
    
    private final ImGuiViewportHandling imGuiGlfw;
    private final ImGuiRenderer imGuiGl3;
    
    public ImGuiImpl(Window window, ProjectSettings settings)
    {
        ImGui.createContext();
        ImGuiIO io = ImGui.getIO();
        io.setIniFilename(null);
        io.setDisplaySize(window.getWidth(), window.getHeight());
    
        if(settings.guiMultiViewport)
        {
            ImGui.getIO().addConfigFlags(ImGuiConfigFlags.ViewportsEnable);
        }
        if(settings.guiDocking)
        {
            ImGui.getIO().addConfigFlags(ImGuiConfigFlags.DockingEnable);
        }
        ImGui.getIO().setConfigViewportsNoTaskBarIcon(settings.guiHideViewportTaskbar);
        //ImGui.getIO().setConfigDockingWithShift(true);
        
        
        imGuiGlfw = new ImGuiViewportHandling();
        imGuiGlfw.init(window.getWindowHandle(), true);
        imGuiGl3 = new ImGuiRenderer();
        imGuiGl3.init(decideGlGlslVersions());
    }
    
    public void newFrame()
    {
        imGuiGlfw.newFrame();
        ImGui.newFrame();
    }
    
    public void endFrame()
    {
        ImGui.endFrame();
        ImGui.render();
        //imGuiGl3.renderDrawData(ImGui.getDrawData());
        if(ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable))
        {
            final long backupWindowPtr = glfwGetCurrentContext();
            ImGui.updatePlatformWindows();
            ImGui.renderPlatformWindowsDefault();
            glfwMakeContextCurrent(backupWindowPtr);
        }
    }
    public void render()
    {
        imGuiGl3.renderDrawData(ImGui.getDrawData());
    }
    private String decideGlGlslVersions()
    {
        final boolean isMac = System.getProperty("os.name").toLowerCase().contains("mac");
        String glslVersion;
        if(isMac)
        {
            glslVersion = "#version 150";
            GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
            GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 2);
            GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);  // 3.2+ only
            GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE);          // Required on Mac
        }
        else
        {
            glslVersion = "#version 130";
            GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
            GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 0);
        }
        return glslVersion;
    }
    
    public void clean()
    {
        imGuiGlfw.clean();
        imGuiGl3.clean();
        ImGui.destroyContext();
    }
}
