#version 330

layout(location = 0) in vec3 inPosition;
layout(location = 1) in vec3 inColor;

uniform mat4 projectionMatrix;
uniform mat4 modelMatrix;

out vec3 outColor;

void main()
{
    //Our vect3 input needs to be converted to a vec4 output because it plays nicer with matrix math.
    gl_Position = projectionMatrix * modelMatrix * vec4(inPosition, 1.0);
    outColor = inColor;
}