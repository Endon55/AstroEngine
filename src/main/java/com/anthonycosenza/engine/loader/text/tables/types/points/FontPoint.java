package com.anthonycosenza.engine.loader.text.tables.types.points;

import com.anthonycosenza.engine.util.math.vector.Vector2;

public interface FontPoint
{
    Vector2 getPosition();
    int getHintMask();
    FontPoint scale(float scale);
    FontPoint copy();
}
