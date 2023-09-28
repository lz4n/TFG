#version 330 core

uniform sampler2D texture_sampler;
in vec2 fTextureCoords;
out vec4 color;

void main()
{
    //Obtenemos la posición del fragmento en la ventana y calculamos su distancia hasta el centro
    vec2 fragPos = gl_FragCoord.xy;
    vec2 windowCenter = vec2(1920 / 2.0, 1080 / 2.0);
    float distanceToCenter = length(fragPos - windowCenter);

    //Calculamos el factor de niebla
    float fogFactor = clamp(distanceToCenter / 1600, 0.0, 1.0);

    // Define el color de la niebla
    vec3 fogColor = vec3(0.1, 0.1, 0.1); // Color de la niebla

    //Si el fragmento es trasparente no se aplica la nievla.
    vec4 fragmentColor = texture(texture_sampler, fTextureCoords);
    if (fragmentColor.a == 0) {
        color = texture(texture_sampler, fTextureCoords);
    } else {
        color = vec4(mix(fragmentColor.rgb, fogColor, fogFactor), 1);
    }
}
