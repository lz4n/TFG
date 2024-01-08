#version 330 core

/**
Shader de vértices del shader ENTITY.
*/

layout(location = 0) in vec2 position;

uniform sampler2D uTextureSampler;

uniform vec2 uPosition;
uniform vec2 uSize = vec2(1, 1);

uniform mat4 uProjection;
uniform mat4 uView;

flat out int textureUnit;
out vec4 color;
out vec2 fragmentUVCoords;

void main() {
    textureUnit = 0;
    fragmentUVCoords = position;

    //Instanciamos la entidad.
    vec4 transformedPosition = uProjection * uView * vec4(position.x * uSize.x + uPosition.x, position.y * -uSize.y + uPosition.y, 0.0, 1.0);
    gl_Position = transformedPosition;
}