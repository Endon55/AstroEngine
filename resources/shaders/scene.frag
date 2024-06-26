#version 460

in vec2 outTextureCoordinate;
out vec4 fragColor;


struct Material
{
    vec4 diffuse;
};

uniform sampler2D textureSampler;
uniform Material material;
uniform int hasTexture;

void main()
{
    //fragColor = texture(textureSampler, outTextureCoordinate);
    fragColor += material.diffuse;
}