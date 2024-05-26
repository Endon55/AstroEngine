package com.anthonycosenza.engine.util;

import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.nfd.NFDFilterItem;
import org.lwjgl.util.nfd.NFDPathSetEnum;

import java.nio.ByteBuffer;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.memAllocPointer;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.util.nfd.NativeFileDialog.NFD_CANCEL;
import static org.lwjgl.util.nfd.NativeFileDialog.NFD_FreePath;
import static org.lwjgl.util.nfd.NativeFileDialog.NFD_GetError;
import static org.lwjgl.util.nfd.NativeFileDialog.NFD_Init;
import static org.lwjgl.util.nfd.NativeFileDialog.NFD_OKAY;
import static org.lwjgl.util.nfd.NativeFileDialog.NFD_PickFolder;
import static org.lwjgl.util.nfd.NativeFileDialog.NFD_SaveDialog;


public class NativeFileDialogue
{
    
    
    public static String open()
    {
        NFD_Init();
        
        PointerBuffer outPath = memAllocPointer(1);
    
        try
        {
            return getResult(NFD_PickFolder(outPath, (ByteBuffer) null),
                    outPath
            );
        } finally
        {
            memFree(outPath);
        }
    }

    
    private static void save()
    {
        try(MemoryStack stack = stackPush())
        {
            NFDFilterItem.Buffer filters = NFDFilterItem.malloc(2);
            filters.get(0)
                    .name(stack.UTF8("Documents"))
                    .spec(stack.UTF8("doc,pdf,txt"));
            filters.get(1)
                    .name(stack.UTF8("Images"))
                    .spec(stack.UTF8("png,jpg"));
            
            PointerBuffer pp = stack.mallocPointer(1);
            getResult(
                    NFD_SaveDialog(pp, filters, null, "test.txt"),
                    pp
            );
        }
    }
    
    private static String getResult(int result, PointerBuffer path)
    {
        switch(result)
        {
            case NFD_OKAY:
                NFD_FreePath(path.get(0));
                return path.getStringUTF8();
            case NFD_CANCEL:
                return "";
            default: // NFD_ERROR
                System.err.format("Error: %s\n", NFD_GetError());
                return "";
        }
    }
}
