#version 150
layout(std140) uniform MsdfData {
    mat4 uProjection;
    vec4 uColor;
    vec4 uParams;
    vec4 uZ_Padding;
};

in vec2 vTexCoord;
in vec4 vColor;
in float vPxRange;

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
    vec4 mtsdf = texture(Sampler0, vTexCoord);
    float sd = median(mtsdf.r, mtsdf.g, mtsdf.b);
    float pxRange = screenPxRange();
    float screenPxDist = pxRange * (sd - 0.5);
    float alpha = clamp(screenPxDist + 0.5, 0.0, 1.0);
    
    if (alpha < 0.01) discard;
    
    fragColor = vec4(vColor.rgb, vColor.a * alpha);
}
