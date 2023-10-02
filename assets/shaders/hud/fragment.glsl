#version 330 core

in vec2 TexCoord; // Coordenadas de textura de entrada desde el vertex shader
out vec4 FragColor; // Color de salida

uniform sampler2D hudTexture; // Textura del HUD

void main()
{
    FragColor = texture(hudTexture, TexCoord);
}