#version 330 core

/**
Shader de vértices del shader WORLD.
*/

layout (location=0) in vec2 position;
layout (location=1) in vec2 uvCoords;

uniform mat4 uProjection;
uniform mat4 uView;

out vec4 fColor;
out vec2 fragmentUVCoords;

void main() {
    fragmentUVCoords = uvCoords;
    gl_Position = uProjection * uView * vec4(position, 0, 1.0);
}