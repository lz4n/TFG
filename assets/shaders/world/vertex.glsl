#version 330 core
layout (location=0) in vec2 aPos;
layout (location=1) in vec2 aTextureCoords;

uniform mat4 uProjection;
uniform mat4 uView;

out vec4 fColor;
out vec2 fTextureCoords;

void main()
{
    fTextureCoords = aTextureCoords;
    gl_Position = uProjection * uView * vec4(aPos, 0, 1.0);
}