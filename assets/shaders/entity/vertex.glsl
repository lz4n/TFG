#version 330 core

/**
Shader de vértices del shader ENTITY.
*/

layout (location=0) in vec2 position;
layout (location=1) in vec2 uvCoords;

uniform vec2 uInstancePosition;
uniform vec2 uInstanceScale = vec2(1, 1);

uniform mat4 uProjection;
uniform mat4 uView;

out vec4 color;
out vec2 fragmentUVCoords;

void main() {
    fragmentUVCoords = uvCoords;

    //Instanciamos la entidad.
    vec4 transformedPosition = uProjection * uView * vec4(position.x * uInstanceScale.x + uInstancePosition.x, position.y * uInstanceScale.y + uInstancePosition.y, 0.0, 1.0);
    gl_Position = transformedPosition;
}