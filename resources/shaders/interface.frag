#version 330


in vec2 outTextureCoordinate;
in vec4 outColor;

uniform sampler2D textureSampler;

out vec4 FragColor;

//Fragment
void main()
{
    FragColor = outColor * texture(textureSampler, outTextureCoordinate);
}