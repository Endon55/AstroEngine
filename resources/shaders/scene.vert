#version 330

//The job of the vertex shader is to translate vertices into screen coordinates.


layout (location=0) in vec3 position;
layout (location=1) in vec3 normal;
layout (location=2) in vec2 texCoord;

out vec3 outPosition;
out vec3 outNormal;
out vec2 outTextCoord;


uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;

void main()
{
    mat4 modelViewMatrix = viewMatrix * modelMatrix;
    vec4 mvPosition = modelViewMatrix * vec4(position, 1.0);
    gl_Position = projectionMatrix * viewMatrix * modelMatrix * vec4(position, 1.0);
    outPosition = mvPosition.xyz;
    outNormal = normalize(modelViewMatrix * vec4(normal, 0.0)).xyz;
    outTextCoord = texCoord;
}