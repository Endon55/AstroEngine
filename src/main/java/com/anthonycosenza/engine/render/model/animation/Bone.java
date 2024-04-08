package com.anthonycosenza.engine.render.model.animation;

import org.joml.Matrix4f;

public record Bone(int boneId, String boneName, Matrix4f offsetMatrix)
{

}
