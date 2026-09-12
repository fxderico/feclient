#version 410 core

layout(std140) uniform Uniforms {
    mat4 uProjection;
    vec4 uRect;
    vec4 uColor;
    float uGlowSize;
    float uZ;
    vec2 _pad;
};

in vec2 vUV;
in vec2 vSize;
in vec2 vGlowSize;

out vec4 fragColor;

float sdBox(vec2 p, vec2 b) {
    vec2 d = abs(p) - b;
    return length(max(d, 0.0)) + min(max(d.x, d.y), 0.0);
}

void main() {
    float expand = uGlowSize;
    vec2 totalSize = vSize + expand * 2.0;
    vec2 pixelPos  = vUV * totalSize;
    vec2 center    = totalSize * 0.5;

    // SDF distance to box edge (negative = inside, positive = outside)
    float dist = sdBox(pixelPos - center, vSize * 0.5);

    float edge = fwidth(dist);

    // Fill: inside the box
    float fillAlpha = 1.0 - smoothstep(-edge, edge, dist);

    // Glow: outside the box, exponential falloff
    float glowDist   = max(0.0, dist);
    float glowAlpha  = exp(-glowDist * glowDist / (uGlowSize * uGlowSize * 0.3));
    glowAlpha = glowAlpha * (1.0 - fillAlpha); // only outside

    // Bright inner edge
    float innerEdge  = abs(dist + 1.5) - 1.5;
    float edgeAlpha  = 1.0 - smoothstep(-edge * 0.5, edge * 2.0, innerEdge);

    float finalAlpha = max(fillAlpha * uColor.a, max(glowAlpha * 0.8, edgeAlpha));
    if (finalAlpha < 0.005) discard;

    // Brighter color at the edge
    float brightness = 1.0 + edgeAlpha * 0.6 + glowAlpha * 0.3;
    vec3  finalColor = uColor.rgb * brightness;

    fragColor = vec4(finalColor, finalAlpha);
}
