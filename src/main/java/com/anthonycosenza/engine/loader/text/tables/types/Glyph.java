package com.anthonycosenza.engine.loader.text.tables.types;

import com.anthonycosenza.engine.loader.text.tables.types.points.FontPoint;

import java.util.List;

public interface Glyph
{
    List<List<FontPoint>> getPoints();
}
