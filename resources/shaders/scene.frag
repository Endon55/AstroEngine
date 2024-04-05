#version 330

//The job of the Fragment shader is to convert the output of the vertex shader into individual pixels and assign the final color that will be displayed on the screen.

in vec2 outTextCoord;

out vec4 fragColor;

struct Material
{
    vec4 diffuse;
};

uniform sampler2D textureSampler;
uniform Material material;

void main()
{
    fragColor = texture(textureSampler, outTextCoord) + material.diffuse;
}