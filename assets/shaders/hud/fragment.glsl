#version 330 core

/**
Shader de fragmentos del shader HUD.
*/

/**
Coordenadas de textura de entrada desde el vertex shader.
*/
in vec2 uvCoords;

/**
Color de salida.
*/
out vec4 color;

/**
Sampler de la textura.
*/
uniform sampler2D uHudTexture;

void main() {
    color = texture(uHudTexture, uvCoords);
}