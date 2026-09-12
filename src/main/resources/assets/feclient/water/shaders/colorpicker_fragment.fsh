#version 410 core

layout(std140) uniform Uniforms {
    mat4 uProjection;
    vec4 uRect;
    float uHue;
    float uRadius;
    float uZ;
    float uAlpha;
};

in vec2 vUV;
in vec2 vSize;

out vec4 fragColor;

vec3 hsv2rgb(vec3 c) {
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

float sdRoundedBox(vec2 p, vec2 b, float r) {
    vec2 q = abs(p) - b + r;
    return min(max(q.x, q.y), 0.0) + length(max(q, 0.0)) - r;
}

void main() {
    vec2 pixelPos = vUV * vSize;
    vec2 center = vSize * 0.5;
    
    float dist = sdRoundedBox(pixelPos - center, center, uRadius);
    float edge = fwidth(dist);
    float alpha = 1.0 - smoothstep(-edge, edge, dist);
    
    float finalAlpha = alpha * uAlpha;
    if (finalAlpha <= 0.0) discard;
    
    float saturation = vUV.x;
    float brightness = 1.0 - vUV.y;
    
    vec3 rgb = hsv2rgb(vec3(uHue, saturation, brightness));
    
    fragColor = vec4(rgb, finalAlpha);
}
