#version 330 core

/**
Shader de vértices del shader HUD. Los vértices se colocan encima de la cámara, en vez de en el mundo.
*/

/**
Especifica el layout de las coordenadas UV de la entidad.
*/
layout(location = 0) in vec2 position;

/**
Matrices de proyección y vista de la cámara.
*/
uniform mat4 uProjection;
uniform mat4 uView;

/**
*/
uniform vec2 uPosition;

void main() {
    //Transforma la posición del vértice usando las matrices de proyección y modelo-vista
    gl_Position = uProjection * uView * vec4(position.x +uPosition.x, position.y +uPosition.y, 0.0, 1.0);
}