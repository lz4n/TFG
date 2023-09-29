#version 330 core

uniform sampler2D texture_sampler;
uniform int num_frames;

in vec2 fTextureCoords;

out vec4 color;

void main()
{
    // Calcular las coordenadas de textura para un solo frame
    float frameWidth = 1.0 / float(numFrames);  // Ancho de un frame en coordenadas de textura
    float frameIndex = floor(TexCoord.x / frameWidth);  // Índice del frame actual
    vec2 frameTexCoord = vec2((frameIndex + 0.5) * frameWidth, TexCoord.y);

    // Muestrear el color de la textura para el frame actual
    color = texture(texture_sampler, frameTexCoord);
}