#version 330 core

/**
Shader de vértices del shader WORLD.
*/

layout (location=0) in vec2 position;
layout (location=1) in vec2 uvCoords;
layout (location=2) in vec2 layoutTextureUnit;

uniform int customTextureUnit = -1;
uniform mat4 uProjection;
uniform mat4 uView;
uniform int repeatingTimes = 1;

flat out int textureUnit;
out vec4 fColor;
out vec2 fragmentUVCoords;

void main() {
    fragmentUVCoords = uvCoords *repeatingTimes;
    if (customTextureUnit == -1) {
        textureUnit = int(layoutTextureUnit.x);
    } else {
        textureUnit = customTextureUnit;
    }
    gl_Position = uProjection * uView * vec4(position, 0, 1.0);
}