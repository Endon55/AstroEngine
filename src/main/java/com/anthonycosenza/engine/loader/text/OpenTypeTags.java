package com.anthonycosenza.engine.loader.text;

public enum OpenTypeTags
{
    OS2("OS/2"),
    CMAP("cmap"),
    HEAD("head"),
    HHEA("hhea"),
    HMTX("hmtx"),
    MAXP("maxp"),
    NAME("name"),
    POST("post")
    
    ;
    
    private String tag;
    
    OpenTypeTags(String tag)
    {
        this.tag = tag;
    }
    
    public String getTag()
    {
        return tag;
    }
}
