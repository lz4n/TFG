#version 330 core

layout(location = 0) in vec2 inPosition; // Atributo de posición de los vértices
out vec2 TexCoord; // Coordenadas de textura de salida para el fragment shader

uniform mat4 uProjection; // Matriz de proyección
uniform mat4 uView; // Matriz de modelo-vista
uniform vec2 hudPosition = vec2(0, 0); // Posición del HUD en pantalla
uniform vec2 hudSize = vec2(1920, 1080); // Tamaño del HUD en pantalla

void main()
{
    // Transforma la posición del vértice usando las matrices de proyección y modelo-vista
    vec4 transformedPosition = uProjection * uView * vec4(inPosition.x * hudSize.x + hudPosition.x, inPosition.y * hudSize.y + hudPosition.y, 0.0, 1.0);
    gl_Position = transformedPosition;

    // Pasa las coordenadas de textura a través del shader
    TexCoord = inPosition;
}