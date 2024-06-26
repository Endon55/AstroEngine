#version 460

layout (location = 0) in vec2 inPosition;
layout (location = 1) in vec2 inTextureCoordinate;
layout (location = 2) in vec4 inColor;


out vec2 outTextureCoordinate;
out vec4 outColor;

uniform vec2 scale;

//Vertex
void main()
{
    outTextureCoordinate = inTextureCoordinate;
    outColor = inColor;
    //Multiply the position by scale, then recenter it with the offset.
    gl_Position =  vec4(inPosition * scale + vec2(-1.0, 1.0), 0.0, 1.0);
}