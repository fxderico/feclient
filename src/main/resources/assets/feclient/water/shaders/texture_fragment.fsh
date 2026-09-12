#version 150

layout(std140) uniform Uniforms {
    mat4 uProjection;
    vec4 uRect;
    vec4 uColor;
    vec4 uRadius_Padding;
    vec4 uZ_Padding;
};

in vec2 vUV;
in vec2 vPos;
out vec4 fragColor;

uniform sampler2D Sampler0;

float sdRoundedRect(vec2 p, vec2 b, float r) {
    vec2 q = abs(p - b) - b + r;
    return length(max(q, 0.0)) + min(max(q.x, q.y), 0.0) - r;
}

void main() {
    vec2 size = uRect.zw;
    float dist = sdRoundedRect(vPos, size * 0.5, min(uRadius_Padding.x, min(size.x, size.y) * 0.5));
    float alpha = 1.0 - smoothstep(-1.0, 0.0, dist);
    
    if (alpha <= 0.0) discard;

    vec4 texColor = texture(Sampler0, vUV);
    fragColor = texColor * uColor;
    fragColor.a *= alpha;
}
