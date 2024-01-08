#version 330 core

/**
Shader de fragmentos común para los shaders WORLD y ENTITY. Dibuja la textura añadiendo una niebla al borde de la pantalla.
*/

uniform float uDaylight;
uniform int uIsPaused = 0;
uniform float uHappiness;
uniform float uRotationAngle = 0;
uniform float uScale = 1;

uniform sampler2D textureSampler0;
uniform sampler2D textureSampler1;
uniform sampler2D textureSampler2;
uniform sampler2D textureSampler3;
uniform sampler2D textureSampler4;

flat in int textureUnit;
in vec2 fragmentUVCoords;
out vec4 color;

vec4 calculateColor(sampler2D texture_sampler, vec2 uvCoords) {
    vec2 fragPos = gl_FragCoord.xy;
    vec2 windowCenter = vec2(1920 / 2.0, 1080 / 2.0);
    float distanceToCenter = length(fragPos - windowCenter);

    //Calculamos el factor de niebla
    float fogFactor = clamp(distanceToCenter / 1600, 0.0, 1.0);

    //Aplicamos la rotación
    mat2 rotationMatrix = mat2(cos(uRotationAngle), -sin(uRotationAngle),
                               sin(uRotationAngle), cos(uRotationAngle));
    uvCoords = rotationMatrix * (uvCoords -0.5) +0.5;

    //Aplicamos la escala. Usamos fract para asegurarnos de que las coordenadas UV estén en el rango 0..1.
    uvCoords /= uScale;

    //Si el fragmento es trasparente no se aplica la nievla.
    vec4 fragmentColor = texture(texture_sampler, uvCoords);
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
