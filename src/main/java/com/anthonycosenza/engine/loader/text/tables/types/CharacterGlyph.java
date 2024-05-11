package com.anthonycosenza.engine.loader.text.tables.types;

import com.anthonycosenza.engine.loader.text.tables.types.points.FontPoint;

import java.util.List;

public class CharacterGlyph implements Glyph
{
    public List<List<FontPoint>> points;
    public CharacterGlyph(List<List<FontPoint>> points)
    {
        this.points = points;
    }
    @Override
    public List<List<FontPoint>> getPoints()
    {
        return null;
    }
}
