#version 330

layout (location = 0) in vec3 inPosition;
layout (location = 1) in vec2 textureCoordinate;

uniform mat4 projectionMatrix;

out vec2 outTextureCoordinate;

//Vertex
void main()
{
    gl_Position = vec4(inPosition, 1);
    //gl_Position = projectionMatrix * vec4(inPosition, 1);
    outTextureCoordinate = textureCoordinate;
}