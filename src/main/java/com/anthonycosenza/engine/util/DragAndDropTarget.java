package com.anthonycosenza.engine.util;

public interface DragAndDropTarget
{
    void peek(Object payload);
    void accept(Object payload);
}
