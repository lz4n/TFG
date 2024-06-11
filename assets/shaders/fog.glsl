#version 330 core

/**
Shader de fragmentos común para los shaders WORLD y ENTITY. Dibuja la textura añadiendo una niebla al borde de la pantalla.
*/

/**
Cantidad de samplers de texturas.
*/
const int SAMPLERS_COUNT = 20;

/**
Atributos de los objetos a renderizar.
*/
uniform float uRotationAngle = 0; //Rotación del objeto.
uniform float uScale = 1; //Escala del objeto.
uniform float uTime;
uniform int uIsSelected = 0; //Si el objeto está seleccionado o no.

/**
Array de samplers de texturas.
*/
uniform sampler2D textureSamplers[SAMPLERS_COUNT];


/**
Establece la textura que se le va a aplicar a la entidad. Esta variable no se puede interpolar.
*/
flat in int textureUnit;

/**
Color y coordenadas UV del pixel.
*/
in vec2 fragmentUVCoords;
out vec4 color;

/**
Calcula el color del pixel. Se le pasaon los parámetros:
    - textureSampler: es el sampler que se va a utilizar para dibujar la textura.
    - uvCoords: coordenadas UV del pixel dentro de la textura.
*/
vec4 calculateColor(sampler2D textureSampler, vec2 uvCoords) {
    //Aplicamos posibles rotaciones.
    mat2 rotationMatrix = mat2(cos(uRotationAngle), -sin(uRotationAngle),
                               sin(uRotationAngle), cos(uRotationAngle));
    uvCoords = rotationMatrix * (uvCoords -0.5) +0.5;

    //Aplicamos la escala.
    uvCoords /= uScale;

    vec4 fragmentColor = texture(textureSampler, uvCoords);
    vec3 color = fragmentColor.rgb;
    if (uIsSelected == 1) {
        color = vec3(mix(color, vec3(1, 1, 1), abs(sin(uTime)) *0.5));
    }

    return vec4(color, fragmentColor.a);
}

void main() {
    //Según sea textureUnit se utilizará un sampler u otro.
    if (textureUnit >= 0 && textureUnit < SAMPLERS_COUNT) {
        color = calculateColor(textureSamplers[textureUnit], fragmentUVCoords);
    }
}
