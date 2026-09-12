#version 150

in vec2 texCoord;
out vec4 fragColor;

uniform sampler2D Sampler0; // ESP geometry
uniform sampler2D Sampler1; // Blurred glow

uniform float uGlowStrength;

void main() {
    vec4 esp  = texture(Sampler0, texCoord);
    vec4 glow = texture(Sampler1, texCoord);

    // Additive glow behind ESP geometry
    vec3  glowRGB   = glow.rgb * uGlowStrength;
    float glowAlpha = min(1.0, glow.a * uGlowStrength);

    // Final: glow + esp on top, both additive blended onto game via GL blend
    vec3  finalRGB   = mix(glowRGB, esp.rgb, esp.a);
    float finalAlpha = max(esp.a, glowAlpha * 0.8);

    if (finalAlpha < 0.01) discard;
    fragColor = vec4(finalRGB, finalAlpha);
}
