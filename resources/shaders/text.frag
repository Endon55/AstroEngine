#version 330


in vec3 outColor;
out vec4 FragColor;

//Fragment
void main()
{
    FragColor = vec4(outColor, 1.0);
}