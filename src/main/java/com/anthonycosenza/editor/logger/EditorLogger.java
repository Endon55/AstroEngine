package com.anthonycosenza.editor.logger;

import imgui.ImColor;

import java.util.ArrayList;
import java.util.List;

public class EditorLogger
{
    public static final int ERROR_COLOR = ImColor.rgba(255, 0, 0, 255);
    public static final int WARNING_COLOR = ImColor.rgba(211, 165, 12, 255);
    
    private static final List<Message> messages = new ArrayList<>();
    
    public static void log(MessageType type, String message)
    {
        messages.add(new Message(type, message));
    }
    
    public static void log(String message)
    {
        messages.add(new Message(MessageType.NORMAL, message));
    }
    
    public static void error(String message)
    {
        messages.add(new Message(MessageType.ERROR, message));
    }
    public static void log(Exception e)
    {
        messages.add(new Message(MessageType.ERROR, e.getMessage()));
    }
    
    public static void warn(String message)
    {
        messages.add(new Message(MessageType.WARNING, message));
    }
    
    public static List<Message> getMessages()
    {
        return messages;
    }
}
