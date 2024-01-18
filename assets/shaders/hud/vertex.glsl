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
Posición y tamaño del componente del HUD que se va a instanciar.
*/
uniform vec2 uPosition;
uniform vec2 uSize;

/**
Coordenadas UV del pixel.
*/
out vec2 uvCoords;

void main() {
    //Transforma la posición del vértice usando las matrices de proyección y modelo-vista
    vec4 transformedPosition = uProjection * uView * vec4(position.x * uSize.x + uPosition.x, position.y * uSize.y + uPosition.y, 0.0, 1.0);
    gl_Position = transformedPosition;

    //Pasa las coordenadas de textura a través del shader
    uvCoords = position;
}