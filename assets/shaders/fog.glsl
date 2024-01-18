#version 330 core

/**
Shader de fragmentos común para los shaders WORLD y ENTITY. Dibuja la textura añadiendo una niebla al borde de la pantalla.
*/

/**
Atributos de los objetos a renderizar.
*/
uniform float uDaylight; //Intensidad lumínica de la luz del sol.
uniform int uIsPaused = 0; //Establece si el juego está en pausa. Si está en pausa se aplica un filtro gris.
uniform float uHappiness; //Índice de felicidad. Según sea la felicidad se desaturará más o menos la imagen.
uniform float uRotationAngle = 0; //Rotación del objeto.
uniform float uScale = 1; //Escala del objeto.

/**
Samplers de texturas.
*/
uniform sampler2D textureSampler0;
uniform sampler2D textureSampler1;
uniform sampler2D textureSampler2;
uniform sampler2D textureSampler3;
uniform sampler2D textureSampler4;


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
    //Calculamos desde el pixel al centro de la pantalla y calculamos el factor de niebla.
    vec2 windowCenter = vec2(1920 / 2.0, 1080 / 2.0);
    float distanceToCenter = length(gl_FragCoord.xy - windowCenter);
    float fogFactor = clamp(distanceToCenter / 1600, 0.0, 1.0);

    //Aplicamos posibles rotaciones.
    mat2 rotationMatrix = mat2(cos(uRotationAngle), -sin(uRotationAngle),
                               sin(uRotationAngle), cos(uRotationAngle));
    uvCoords = rotationMatrix * (uvCoords -0.5) +0.5;

    //Aplicamos la escala.
    uvCoords /= uScale;

    //Si el fragmento es trasparente no se aplica la nievla.
    vec4 fragmentColor = texture(textureSampler, uvCoords);
    if (fragmentColor.a == 0) {
        return fragmentColor;
    }
    vec3 color = vec3(mix(fragmentColor.rgb, vec3(36 /255.0, 22 /255.0, 33 /255.0), fogFactor));

    //Aplicamos la desaturación en funcion de la felicidad.
    vec3 grayScale = vec3(dot(color.rgb, vec3(0.2126, 0.7152, 0.0722)));
    color = mix(color, grayScale, 1 -uHappiness);

    //Aplicamos la luz del sol.
    color = color *uDaylight;

    //Si está en pausa se aplica un filtro gris.
    if (uIsPaused == 1) {
        float gray = (color.r + color.g + color.b) / 3.0;
        color = mix(color, vec3(gray), 0.4);
    }

    return vec4(color, fragmentColor.a);
}

void main() {
    //Según sea textureUnit se utilizará un sampler u otro.
    if (textureUnit == 0) {
        color = calculateColor(textureSampler0, fragmentUVCoords);
    } else if (textureUnit == 1) {
        color = calculateColor(textureSampler1, fragmentUVCoords);
    } else if (textureUnit == 2) {
        color = calculateColor(textureSampler2, fragmentUVCoords);
    } else if (textureUnit == 3) {
        color = calculateColor(textureSampler3, fragmentUVCoords);
    } else if (textureUnit == 4) {
        color = calculateColor(textureSampler4, fragmentUVCoords);
    }
}
