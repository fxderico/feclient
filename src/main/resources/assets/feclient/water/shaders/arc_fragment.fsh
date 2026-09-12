#version 410 core

layout(std140) uniform Uniforms {
    mat4 uProjection;
    vec4 uRect;
    vec4 uParams;
    vec4 uZ_Padding;
    vec4 uColors[9];
};

in vec2 vUV;
in vec2 vSize;

out vec4 fragColor;

#define PI 3.14159265359

float sdArc(in vec2 p, in float sca, in float scb, in float ra, in float rb) {
    p.x = abs(p.x);
    return ((scb*p.x > sca*p.y) ? length(p-vec2(sca,scb)*ra) : 
                                  abs(length(p)-ra)) - rb;
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
    vec2 p = (vUV - 0.5) * vSize;
    
    float rotRad = radians(uParams.w);
    float degRad = radians(uParams.z) * 0.5;
    
    mat2 rotMat = mat2(cos(rotRad), -sin(rotRad), sin(rotRad), cos(rotRad));
    p = rotMat * p;
    
    float ra = uParams.x * 0.5 - uParams.y * 0.5;
    float rb = uParams.y * 0.5;
    
    vec2 sc = vec2(sin(degRad), cos(degRad));
    
    float dist = sdArc(p, sc.x, sc.y, ra, rb);
    float edge = fwidth(dist);
    float alpha = 1.0 - smoothstep(-edge, edge, dist);
    
    if (alpha <= 0.0) discard;
    
    vec4 color = sampleColor(vUV);
    fragColor = vec4(color.rgb, color.a * alpha);
}
