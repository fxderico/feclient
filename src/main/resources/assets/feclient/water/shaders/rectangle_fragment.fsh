#version 410 core

layout(std140) uniform Uniforms {
    mat4 uProjection;
    vec4 uRect;
    vec4 uRadii; // x: top-right, y: bottom-right, z: top-left, w: bottom-left
    float uZ;
    float _pad;
    vec2 _pad2;
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

float dither(vec2 uv) {
    return (fract(sin(dot(uv.xy, vec2(12.9898, 78.233))) * 43758.5453123) - 0.5) * (1.0 / 255.0);
}

vec4 sampleColor(vec2 uv) {
    float x = uv.x * 2.0;
    float y = uv.y * 2.0;

    int ix = int(floor(x));
    int iy = int(floor(y));
    ix = clamp(ix, 0, 1);
    iy = clamp(iy, 0, 1);

    float fx = fract(x);
    float fy = fract(y);

    fx = fx * fx * fx * (fx * (fx * 6.0 - 15.0) + 10.0);
    fy = fy * fy * fy * (fy * (fy * 6.0 - 15.0) + 10.0);

    vec4 c00 = uColors[iy * 3 + ix];
    vec4 c10 = uColors[iy * 3 + ix + 1];
    vec4 c01 = uColors[(iy + 1) * 3 + ix];
    vec4 c11 = uColors[(iy + 1) * 3 + ix + 1];

    vec4 top = mix(c00, c10, fx);
    vec4 bot = mix(c01, c11, fx);
    return mix(top, bot, fy);
}

void main() {
    vec2 pixelPos = vUV * vSize;
    vec2 center = vSize * 0.5;

    float dist = sdRoundedBox(pixelPos - center, center, uRadii);
    float edge = fwidth(dist);
    float alpha = 1.0 - smoothstep(-edge, edge, dist);

    if (alpha <= 0.0) discard;

    vec4 color = sampleColor(vUV);
    // Apply dither to eliminate banding in dark gradients
    color.rgb += dither(pixelPos);

    fragColor = vec4(color.rgb, color.a * alpha);
}
