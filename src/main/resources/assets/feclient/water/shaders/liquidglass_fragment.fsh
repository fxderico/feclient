#version 410 core

layout(std140) uniform Uniforms {
    mat4 uProjection;
    vec4 uRect;
    vec2 uSize;
    vec4 uRadius;
    float uSmoothness;
    float uCornerSmoothness;
    float uGlobalAlpha;
    float uFresnelPower;
    vec4 uFresnelColor;
    float uBaseAlpha;
    int uFresnelInvert;
    float uFresnelMix;
    float uDistortStrength;
    float uZ;
    float _pad;
};

uniform sampler2D Sampler0;

in vec2 vFragCoord;
in vec2 vTexCoord;

out vec4 fragColor;

float roundedBoxSDF(vec2 p, vec2 b, vec4 r, float smoothness) {
    r.xy = (p.x > 0.0) ? r.xy : r.zw;
    r.x = (p.y > 0.0) ? r.x : r.y;
    vec2 q = abs(p) - b + r.x;
    vec2 q_clamped = max(q, 0.0);
    float len = pow(pow(q_clamped.x, smoothness) + pow(q_clamped.y, smoothness), 1.0 / smoothness);
    return min(max(q.x, q.y), 0.0) + len - r.x;
}

void main() {
    vec2 rectSize = uRect.zw;
    vec2 center = rectSize * 0.5;
    vec2 box_half_size = center - 1.0;
    vec2 pos = (vFragCoord * rectSize) - center;

    float dist = roundedBoxSDF(-pos, box_half_size, uRadius, uCornerSmoothness);
    float alpha = 1.0 - smoothstep(1.0 - uSmoothness, 1.0, dist);

    float distToEdge = abs(roundedBoxSDF(pos, box_half_size, uRadius, uCornerSmoothness));

    float max_dist_norm = min(box_half_size.x, box_half_size.y);
    float edge_gradient = 1.0 - clamp(distToEdge / max_dist_norm, 0.0, 1.0);

    float fresnel;
    float base = (uFresnelInvert != 0) ? edge_gradient : (1.0 - edge_gradient);

    if (uFresnelPower > 20.0) {
        fresnel = exp(uFresnelPower * log(clamp(base, 0.001, 1.0)));
    } else {
        fresnel = pow(base, uFresnelPower);
    }
    fresnel = clamp(fresnel, 0.0, 1.0);

    vec2 dir = (length(pos) > 0.001) ? normalize(pos) : vec2(0.0);
    vec2 distortedTexCoord = vTexCoord + dir * fresnel * uDistortStrength;

    vec4 texColor = texture(Sampler0, distortedTexCoord);

    vec3 finalColor = mix(texColor.rgb, uFresnelColor.rgb, fresnel * uFresnelMix);
    float finalAlpha = mix(uBaseAlpha, uFresnelColor.a, fresnel) * alpha;

    if (finalAlpha < 0.001) discard;

    fragColor = vec4(finalColor, finalAlpha * uGlobalAlpha);
}
