#version 330

layout(location = 0) out vec3 color;

in vec2 outTextureCoordinate;
out vec4 FragColor;

struct Material
{
    vec4 diffuse;
};

uniform sampler2D textureSampler;
uniform Material material;
uniform int hasTexture;

void main()
{
    vec4 fragColor = vec4(0, 0, 0, 1);
    if(hasTexture == 1)
    {
        fragColor = texture(textureSampler, outTextureCoordinate);
    }
    fragColor += material.diffuse;
    color = fragColor.xyz;
}