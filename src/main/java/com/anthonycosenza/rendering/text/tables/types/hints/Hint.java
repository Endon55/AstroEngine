package com.anthonycosenza.rendering.text.tables.types.hints;

public class Hint
{
    private int lowerEdge;
    private int upperEdge;
    private HintType hintType;
    public Hint(int lowerEdge, int upperEdge, HintType hintType)
    {
        this.lowerEdge = lowerEdge;
        this.upperEdge = upperEdge;
        this.hintType = hintType;
    }
    
    public int getSecondEdge()
    {
        return upperEdge;
    }
    
    public int getFirstEdge()
    {
        return lowerEdge;
    }
    
    public HintType getHintType()
    {
        return hintType;
    }
    
    public enum HintType
    {
        VERTICAL,
        HORIZONTAL
    }
}
