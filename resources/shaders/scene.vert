#version 330

layout(location = 0) in vec3 inPosition;
layout(location = 1) in vec3 inColor;

uniform mat4 projectionMatrix;
uniform mat4 entityMatrix;
uniform mat4 cameraMatrix;

out vec3 outColor;

void main()
{
    //We pass our coordinate vector into the entity matrix which converts the vector from the models local space into world coordinates.
    //Then the world space vector gets converted from world space to camera space(remember the camera is stationary and the world moves around it).
    //Then the camera vector is sent into a projection matrix which distorts the position of the points to give the illusion of distance.(further objects look smaller).
    gl_Position = projectionMatrix * cameraMatrix * entityMatrix * vec4(inPosition, 1.0);
    outColor = inColor;
}