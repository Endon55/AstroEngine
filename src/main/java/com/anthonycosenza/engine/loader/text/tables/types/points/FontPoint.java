package com.anthonycosenza.engine.loader.text.tables.types.points;


import org.joml.Vector2f;

public interface FontPoint
{
    Vector2f getPosition();
    int getHintMask();
    FontPoint scale(float scale);
    FontPoint copy();
}
