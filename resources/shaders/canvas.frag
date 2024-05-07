#version 330

in vec2 outTextureCoordinate;
out vec4 fragColor;

uniform sampler2D textureSampler;

void main()
{
    fragColor = texture(textureSampler, outTextureCoordinate);
    //fragColor = vec4(1, 1, 1, 1);
}