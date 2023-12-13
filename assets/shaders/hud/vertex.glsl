#version 330 core

/**
Shader de vértices del shader HUD. Los vértices se colocan encima de la cámara, en vez de en el mundo.
*/

layout(location = 0) in vec2 position;

uniform mat4 uProjection;
uniform mat4 uView;

uniform vec2 uHudPosition; // Posición del HUD en pantalla
uniform vec2 uHudSize; // Tamaño del HUD en pantalla

out vec2 uvCoords;

void main() {
    //Transforma la posición del vértice usando las matrices de proyección y modelo-vista
    vec4 transformedPosition = uProjection * uView * vec4(position.x * uHudSize.x + uHudPosition.x, position.y * uHudSize.y + uHudPosition.y, 0.0, 1.0);
    gl_Position = transformedPosition;

    //Pasa las coordenadas de textura a través del shader
    uvCoords = position;
}