#version 330

in vec2 outTextureCoord;
out vec4 fragColor;

uniform vec4 diffuse;
uniform sampler2D txtSampler;
uniform int hasTexture;


void main()
{
    if(hasTexture == 1)
    {
        fragColor = texture(txtSampler, outTextureCoord);
    }
    else
    {
        fragColor = diffuse;
    }
}