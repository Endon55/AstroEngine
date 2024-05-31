package com.anthonycosenza.editor.scene;

public enum SaveType
{
    Scene(".scene"),
    
    ;
    
    private final String extension;
    
    SaveType(String extension)
    {
        this.extension = extension;
    }
    
    public String getExtension()
    {
        return extension;
    }
}
