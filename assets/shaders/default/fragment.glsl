#version 330 core

uniform float uTime;

in vec4 fColor;

out vec4 color;

void main()
{
    float noise = sin(uTime);
    color = fColor * noise;
}