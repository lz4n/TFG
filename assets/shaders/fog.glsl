#version 330 core

/**
Shader de fragmentos común para los shaders WORLD y ENTITY. Dibuja la textura añadiendo una niebla al borde de la pantalla.
*/

uniform sampler2D texture_sampler;
uniform float uDaylight;

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
    vec4 fragmentColor = texture(texture_sampler, uvCoords, 1);
    if (fragmentColor.a == 0) {
        return fragmentColor;
    }
    return vec4(mix(fragmentColor.rgb, fogColor, fogFactor), fragmentColor.a);
}

void main() {
    color = calculateFog(texture_sampler, fragmentUVCoords);
}
