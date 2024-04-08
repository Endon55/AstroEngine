package com.anthonycosenza.engine.render.model.animation;

import java.util.List;

public record Animation(String name, double duration, List<AnimatedFrame> frames)
{
}
