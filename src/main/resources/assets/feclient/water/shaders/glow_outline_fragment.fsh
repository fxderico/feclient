#version 410 core

layout(std140) uniform Uniforms {
    mat4 uProjection;
    vec4 uRect;
    vec4 uParams;
    vec4 uColor;
    vec4 uExtra;
};

in vec2 vUV;
in vec2 vSize;

out vec4 fragColor;

float sdRoundedBox(vec2 p, vec2 b, float r) {
    vec2 q = abs(p) - b + r;
    return min(max(q.x, q.y), 0.0) + length(max(q, 0.0)) - r;
}

float getPerimeter(vec2 size, float radius) {
    float straightH = max(0.0, size.x - 2.0 * radius) * 2.0;
    float straightV = max(0.0, size.y - 2.0 * radius) * 2.0;
    float corners = 2.0 * 3.14159 * radius;
    return straightH + straightV + corners;
}

float getPositionOnPerimeter(vec2 uv, vec2 size, float radius) {
    vec2 pos = uv * size;
    float perimeter = getPerimeter(size, radius);
    
    vec2 center = size * 0.5;
    vec2 fromCenter = pos - center;
    float angle = atan(fromCenter.y, fromCenter.x);
    
    return (angle + 3.14159) / (2.0 * 3.14159);
}

void main() {
    float radius = uParams.x;
    float thickness = uParams.y;
    float progress = uParams.z;
    float baseAlpha = uParams.w;
    
    vec2 pixelPos = vUV * vSize;
    vec2 center = vSize * 0.5;
    
    float dist = sdRoundedBox(pixelPos - center, center, radius);
    float edge = fwidth(dist);
    
    float glowSize = thickness * 3.0;
    float innerDist = abs(dist + thickness * 0.5) - thickness * 0.5;
    float outlineAlpha = 1.0 - smoothstep(-edge, edge, innerDist);
    
    float glowAlpha = 0.0;
    if (dist > -glowSize && dist < glowSize) {
        glowAlpha = 1.0 - abs(dist) / glowSize;
        glowAlpha = glowAlpha * glowAlpha * 0.5;
    }
    
    float posOnPerimeter = getPositionOnPerimeter(vUV, vSize, radius);
    
    float sweepStart = 0.0;
    float sweepEnd = progress;
    
    float sweepMask = 1.0;
    if (progress < 1.0) {
        float diff = posOnPerimeter - sweepStart;
        if (diff < 0.0) diff += 1.0;
        sweepMask = smoothstep(sweepEnd - 0.05, sweepEnd, diff);
        sweepMask = 1.0 - sweepMask;
        if (diff > sweepEnd) sweepMask = 0.0;
    }
    
    float finalAlpha = max(outlineAlpha, glowAlpha) * baseAlpha * sweepMask;
    
    if (finalAlpha <= 0.001) discard;
    
    vec3 color = uColor.rgb;
    float brightness = 1.0 + glowAlpha * 0.5;
    
    fragColor = vec4(color * brightness, uColor.a * finalAlpha);
}
