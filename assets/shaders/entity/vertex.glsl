#version 330 core
layout (location=0) in vec2 aPos;
layout (location=1) in vec2 aTextureCoords;

uniform vec2 inInstancePosition;
uniform vec2 inInstanceScale = vec2(1, 1);

uniform mat4 uProjection;
uniform mat4 uView;

out vec4 fColor;
out vec2 fTextureCoords;

void main() {
    vec4 transformedPosition = uProjection * uView * vec4(aPos.x * inInstanceScale.x + inInstancePosition.x, aPos.y * inInstanceScale.y + inInstancePosition.y, 0.0, 1.0);
    gl_Position = transformedPosition;

    fTextureCoords = aTextureCoords;
}