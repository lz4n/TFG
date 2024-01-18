#version 330 core

/**
Shader de vértices del shader WORLD.
*/

/**
Especifica los layout de la hubicación, coordenadas UV y textura del terreno a dibujar.
*/
layout (location=0) in vec2 position;
layout (location=1) in vec2 uvCoords;
layout (location=2) in vec2 layoutTextureUnit;

/**
Matrices de proyección y vista de la cámara.
*/
uniform mat4 uProjection;
uniform mat4 uView;

/**
Establece si se va a utilizar una textura diferente a la que se pasa con el layout. Si es -1 se va a utilizar la textura
del layout, si no, se utiliza esta textura.
*/
uniform int customTextureUnit = -1;

/**
Número de repeticiones que va a tener la textura.
*/
uniform int repeatingTimes = 1;

/**
Establece la textura que se le va a aplicar a la entidad. Esta variable no se puede interpolar.
*/
flat out int textureUnit;

/**
Color y coordenadas UV del pixel.
*/
out vec4 fColor;
out vec2 fragmentUVCoords;

void main() {
    fragmentUVCoords = uvCoords *repeatingTimes;

    //Si customTexturaUnit es diferente de -1 se aplica la textura que se pase en el layout.
    if (customTextureUnit == -1) {
        textureUnit = int(layoutTextureUnit.x);
    } else {
        textureUnit = customTextureUnit;
    }

    gl_Position = uProjection * uView * vec4(position, 0, 1.0);
}