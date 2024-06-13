#version 330 core
out vec4 fragmentColor;

in vec2 textureCoords;

uniform sampler2D uScreenTexture;
uniform int uIsPaused = 0; // Establece si el juego está en pausa. Si está en pausa se aplica un filtro gris.
uniform float uDaylight; //Intensidad lumínica de la luz del sol.
uniform float uHappiness; //Índice de felicidad. Según sea la felicidad se desaturará más o menos la imagen.
uniform vec2 uLightPos; // Posición de la luz.

const float offset = 1.0 / 300.0;

void main()
{
    vec2 offsets[25] = vec2[](
        vec2(-2.0*offset,  2.0*offset), vec2(-1.0*offset,  2.0*offset), vec2(0.0*offset,  2.0*offset), vec2(1.0*offset,  2.0*offset), vec2(2.0*offset,  2.0*offset),
        vec2(-2.0*offset,  1.0*offset), vec2(-1.0*offset,  1.0*offset), vec2(0.0*offset,  1.0*offset), vec2(1.0*offset,  1.0*offset), vec2(2.0*offset,  1.0*offset),
        vec2(-2.0*offset,  0.0*offset), vec2(-1.0*offset,  0.0*offset), vec2(0.0*offset,  0.0*offset), vec2(1.0*offset,  0.0*offset), vec2(2.0*offset,  0.0*offset),
        vec2(-2.0*offset, -1.0*offset), vec2(-1.0*offset, -1.0*offset), vec2(0.0*offset, -1.0*offset), vec2(1.0*offset, -1.0*offset), vec2(2.0*offset, -1.0*offset),
        vec2(-2.0*offset, -2.0*offset), vec2(-1.0*offset, -2.0*offset), vec2(0.0*offset, -2.0*offset), vec2(1.0*offset, -2.0*offset), vec2(2.0*offset, -2.0*offset)
    );

    // Kernel de 5x5 para desenfoque Gaussiano
    float kernel[25] = float[](
        1.0/273,  4.0/273,  7.0/273,  4.0/273,  1.0/273,
        4.0/273, 16.0/273, 26.0/273, 16.0/273,  4.0/273,
        7.0/273, 26.0/273, 41.0/273, 26.0/273,  7.0/273,
        4.0/273, 16.0/273, 26.0/273, 16.0/273,  4.0/273,
        1.0/273,  4.0/273,  7.0/273,  4.0/273,  1.0/273
    );

    vec3 color;

    //Si está en pausa hacemos un desenfoque Gaussiano (blur)
    if (uIsPaused == 1) {
        vec3 sampleTex[25];
        for(int i = 0; i < 25; i++) {
            sampleTex[i] = vec3(texture(uScreenTexture, textureCoords.st + offsets[i]));
        }

        color = vec3(0.0);
        for(int i = 0; i < 25; i++) {
            color += sampleTex[i] * kernel[i];
        }
    } else {
        color = texture(uScreenTexture, textureCoords).rgb;
    }

    //Aplicamos la luz del sol.
    color = color *uDaylight;

    //Aplicamos la desaturación en funcion de la felicidad.
    vec3 grayScale = vec3(dot(color.rgb, vec3(0.2126, 0.7152, 0.0722)));
    color = mix(color, grayScale, 1 -uHappiness);

    //Colocamos un foco de luz en el ratón
    float distanceToLight = length(textureCoords - uLightPos);
    float attenuation = 1 -smoothstep(0.0, 0.2, distanceToLight);
    color += vec3(0.065, 0.06, 0.04) * attenuation;

    //Calculamos desde el pixel al centro de la pantalla y aplicamos la niebla.
    vec2 windowCenter = vec2(1920 / 2.0, 1080 / 2.0);
    float distanceToCenter = length(gl_FragCoord.xy - windowCenter);
    float fogFactor = clamp(distanceToCenter / 1600, 0.0, 1.0);

    color = vec3(mix(color.rgb, vec3(36 /255.0, 22 /255.0, 33 /255.0), fogFactor));

    fragmentColor = vec4(color, 1.0);
}
