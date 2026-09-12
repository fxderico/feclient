#version 410 core

layout(std140) uniform Uniforms {
    mat4 uProjection;
    vec4 uRect;
    float uRadius;
    float uZ;
    float uAlpha;
    float _pad;
};

in vec2 vUV;
in vec2 vSize;

out vec4 fragColor;

vec3 hsv2rgb(vec3 c) {
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

float sdRoundedBox(vec2 p, vec2 b, vec4 r) {
    r.xy = (p.x > 0.0) ? r.xy : r.zw;
    r.x  = (p.y > 0.0) ? r.x  : r.y;
    vec2 q = abs(p) - b + r.x;
    return min(max(q.x, q.y), 0.0) + length(max(q, 0.0)) - r.x;
}

void main() {
    vec2 pixelPos = vUV * vSize;
    vec2 center = vSize * 0.5;
    
    vec4 radii = vec4(uRadius, uRadius, uRadius, uRadius);
    float dist = sdRoundedBox(pixelPos - center, center, radii);
    float edge = fwidth(dist);
    float alpha = 1.0 - smoothstep(-edge, edge, dist);
    
    float finalAlpha = alpha * uAlpha;
    if (finalAlpha <= 0.0) discard;
    
    float hue = vUV.y;
    vec3 rgb = hsv2rgb(vec3(hue, 1.0, 1.0));
    
    fragColor = vec4(rgb, finalAlpha);
}
