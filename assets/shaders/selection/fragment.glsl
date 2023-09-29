#version 330 core

uniform sampler2D texture_sampler;

in vec4 fcolor;

out vec4 color;

void main()
{
    color = fColor;
}