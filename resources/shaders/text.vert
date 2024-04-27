#version 330

layout (location = 0) in vec3 inPosition;
layout (location = 1) in vec3 inColor;

uniform mat4 projectionMatrix;

out vec3 outColor;

//Vertex
void main()
{
    //gl_Position = projectionMatrix * vec4(inPosition, 0);
    gl_Position = projectionMatrix * vec4(inPosition, 1);
    outColor = inColor;
}