#version 410 core

layout(std140) uniform Uniforms {
    mat4 uProjection;
    vec4 uRect;
    vec4 uRadii; // x: top-right, y: bottom-right, z: top-left, w: bottom-left
    float uThickness;
    float uZ;
    vec2 _pad;
    vec4 uColors[9];
};

in vec2 vUV;
in vec2 vSize;

out vec4 fragColor;

float sdRoundedBox(vec2 p, vec2 b, vec4 r) {
    r.xy = (p.x > 0.0) ? r.xy : r.zw;
    r.x  = (p.y > 0.0) ? r.x  : r.y;
    vec2 q = abs(p) - b + r.x;
    return min(max(q.x, q.y), 0.0) + length(max(q, 0.0)) - r.x;
}

vec4 sampleColor(vec2 uv) {
    float wx[3];
    wx[0] = clamp(1.0 - uv.x * 2.0, 0.0, 1.0);
    wx[1] = 1.0 - abs(uv.x * 2.0 - 1.0);
    wx[2] = clamp(uv.x * 2.0 - 1.0, 0.0, 1.0);
    
    float wy[3];
    wy[0] = clamp(1.0 - uv.y * 2.0, 0.0, 1.0);
    wy[1] = 1.0 - abs(uv.y * 2.0 - 1.0);
    wy[2] = clamp(uv.y * 2.0 - 1.0, 0.0, 1.0);
    
    for(int i = 0; i < 3; i++) {
        wx[i] = wx[i] * wx[i] * (3.0 - 2.0 * wx[i]);
        wy[i] = wy[i] * wy[i] * (3.0 - 2.0 * wy[i]);
    }
    
    vec4 result = vec4(0.0);
    float totalWeight = 0.0;
    
    for(int j = 0; j < 3; j++) {
        for(int i = 0; i < 3; i++) {
            float w = wx[i] * wy[j];
            result += uColors[j * 3 + i] * w;
            totalWeight += w;
        }
    }
    
    return result / max(totalWeight, 0.001);
}

void main() {
    vec2 pixelPos = vUV * vSize;
    vec2 center = vSize * 0.5;
    
    float dist = sdRoundedBox(pixelPos - center, center, uRadii);
    float edge = fwidth(dist);
    
    float d2 = abs(dist + uThickness * 0.5) - uThickness * 0.5;
    float alpha = 1.0 - smoothstep(-edge, edge, d2);
    
    if (alpha <= 0.0) discard;
    
    vec4 color = sampleColor(vUV);
    fragColor = vec4(color.rgb, color.a * alpha);
}
