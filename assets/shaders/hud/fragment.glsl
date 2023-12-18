#version 330 core

/**
Shader de fragmentos del shader HUD.
*/

in vec2 uvCoords; // Coordenadas de textura de entrada desde el vertex shader
out vec4 color; // Color de salida

uniform sampler2D uHudTexture; // Textura del HUD.

void main() {
    color = texture(uHudTexture, uvCoords);
}