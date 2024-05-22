#version 330

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
    //fragColor = vec4(1, 0, 0, 1);
    if(hasTexture == 1)
    {
        fragColor = texture(textureSampler, outTextureCoordinate);
    }
    fragColor += material.diffuse;
}