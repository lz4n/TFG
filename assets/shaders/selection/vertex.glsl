#version 330 core
layout (location=0) in vec3 aPos;

uniform mat4 uProjection;
uniform mat4 uView;

out vec4 fColor;
out vec2 fTextureCoords;

void main()
{
    fColor = vec4(1, 1, 1, 1);
    gl_Position = uProjection * uView * vec4(aPos, 1.0);
}