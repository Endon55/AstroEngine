package com.anthonycosenza.editor.scene.popups;

public interface Popup
{
    void create();
    
    boolean isFinished();
    Object finish();
}
