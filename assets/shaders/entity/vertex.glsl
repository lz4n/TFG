#version 330 core

/**
Shader de vértices del shader ENTITY.
*/

/**
Especifica el layout de las coordenadas UV de la entidad.
*/
layout(location = 0) in vec2 position;

/**
Posición y tamaño (en los ejes x e y) de la entidad a instanciar. El tamaño por defecto es (1, 1).
*/
uniform vec2 uPosition;
uniform vec2 uSize = vec2(1, 1);

/**
Matrices de proyección y vista de la cámara.
*/
uniform mat4 uProjection;
uniform mat4 uView;

/**
Establece la textura que se le va a aplicar a la entidad. Esta variable no se puede interpolar.
*/
flat out int textureUnit;

/**
Color y coordenadas UV del pixel.
*/
out vec4 color;
out vec2 fragmentUVCoords;

void main() {
    textureUnit = 0;
    fragmentUVCoords = position;

    //Transforma la posición del vértice usando las matrices de proyección y modelo-vista
    vec4 transformedPosition = uProjection * uView * vec4(position.x* uSize.x +uPosition.x, position.y* -uSize.y +uPosition.y, 0.0, 1.0);
    gl_Position = transformedPosition;
}