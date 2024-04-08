#version 330

//We want to recieve vertices in batches of 3 that form a triangle.
layout(triangles) in;
//We want to output vertices in batches of 3 that form a triangle.
layout(line_strip, max_vertices = 3) out;



void main()
{
    gl_Position = gl_in[0].gl_Position;
    EmitVertex();

    gl_Position = gl_in[1].gl_Position;
    EmitVertex();

    gl_Position = gl_in[2].gl_Position;
    EmitVertex();
    EndPrimitive();
}