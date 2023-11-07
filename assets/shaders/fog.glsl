#version 330 core

/**
Shader de fragmentos común para los shaders WORLD y ENTITY. Dibuja la textura añadiendo una niebla al borde de la pantalla.
*/


uniform float uDaylight;
uniform sampler2D textureSampler0;
uniform sampler2D textureSampler1;
uniform sampler2D textureSampler2;
uniform sampler2D textureSampler3;
uniform sampler2D textureSampler4;

flat in int textureUnit;
in vec2 fragmentUVCoords;
out vec4 color;

vec4 calculateFog(sampler2D texture_sampler, vec2 uvCoords) {
    vec2 fragPos = gl_FragCoord.xy;
    vec2 windowCenter = vec2(1920 / 2.0, 1080 / 2.0);
    float distanceToCenter = length(fragPos - windowCenter);

    //Calculamos el factor de niebla
    float fogFactor = clamp(distanceToCenter / 1600, 0.0, 1.0);

    // Define el color de la niebla
    vec3 fogColor = vec3(0.1, 0.1, 0.1);

    //Si el fragmento es trasparente no se aplica la nievla.
    vec4 fragmentColor = texture(texture_sampler, uvCoords);
    if (fragmentColor.a == 0) {
        return fragmentColor;
    }
    return vec4(mix(fragmentColor.rgb, fogColor, fogFactor), fragmentColor.a);
}

void main() {;
    if (textureUnit == 0) {
        color = calculateFog(textureSampler0, fragmentUVCoords);
    } else if (textureUnit == 1) {
        color = calculateFog(textureSampler1, fragmentUVCoords);
    } else if (textureUnit == 2) {
        color = calculateFog(textureSampler2, fragmentUVCoords);
    } else if (textureUnit == 3) {
        color = calculateFog(textureSampler3, fragmentUVCoords);
    } else if (textureUnit == 4) {
        color = calculateFog(textureSampler4, fragmentUVCoords);
    }
}
