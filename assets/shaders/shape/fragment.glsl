#version 330 core

/**
Shader de fragmentos del shader HUD.
*/

/**
Color de salida.
*/
out vec4 color;

/**
Sampler de la textura.
*/
uniform vec4 uColor;

void main() {
    color = uColor;
}