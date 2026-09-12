#version 410 core

layout(std140) uniform MsdfData {
    mat4 uProjection;
    vec4 uTextColor;
    vec4 uGlowColor;
    vec4 uParams;
    vec4 uZ_Padding;
};

in vec2 vTexCoord;
in vec4 vTextColor;
in vec4 vGlowColor;
in float vPxRange;
in float vGlowIntensity;

out vec4 fragColor;

uniform sampler2D Sampler0;

float median(float r, float g, float b) {
    return max(min(r, g), min(max(r, g), b));
}

float screenPxRange() {
    vec2 unitRange = vec2(vPxRange) / vec2(textureSize(Sampler0, 0));
    vec2 screenTexSize = vec2(1.0) / fwidth(vTexCoord);
    return max(0.5 * dot(unitRange, screenTexSize), 1.0);
}

void main() {
    vec2 texSize = vec2(textureSize(Sampler0, 0));
    vec2 texelSize = 1.0 / texSize;
    
    vec4 mtsdf = texture(Sampler0, vTexCoord);
    float sd = median(mtsdf.r, mtsdf.g, mtsdf.b);
    float pxRange = screenPxRange();
    float screenPxDist = pxRange * (sd - 0.5);
    
    float textAlpha = clamp(screenPxDist + 0.5, 0.0, 1.0);
    
    float glowSd = sd;
    float blurRadius = vGlowIntensity * 0.5;
    
    for (float ox = -2.0; ox <= 2.0; ox += 1.0) {
        for (float oy = -2.0; oy <= 2.0; oy += 1.0) {
            if (ox == 0.0 && oy == 0.0) continue;
            vec2 offset = vec2(ox, oy) * texelSize * blurRadius;
            vec4 tex = texture(Sampler0, vTexCoord + offset);
            float sampleSd = median(tex.r, tex.g, tex.b);
            glowSd = max(glowSd, sampleSd * exp(-length(vec2(ox, oy)) * 0.3));
        }
    }
    
    float glowDist = pxRange * (glowSd - 0.5);
    float rawGlow = clamp(glowDist * 0.3 + 0.5, 0.0, 1.0);
    float glowAlpha = rawGlow * (1.0 - textAlpha) * vGlowColor.a;
    
    vec3 finalColor = vTextColor.rgb * textAlpha * vTextColor.a + vGlowColor.rgb * glowAlpha;
    float finalAlpha = textAlpha * vTextColor.a + glowAlpha;
    
    if (finalAlpha < 0.01) discard;
    
    fragColor = vec4(finalColor / max(finalAlpha, 0.001), finalAlpha);
}
