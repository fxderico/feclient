#version 150

in vec2 texCoord;
in vec2 pixelCoord;
in vec2 rectSize;
in vec4 cornerRadii;

out vec4 fragColor;

uniform sampler2D Sampler0;

float roundedBoxSDF(vec2 p, vec2 b, vec4 r) {
    r.xy = (p.x > 0.0) ? r.yz : r.xw;
    r.x = (p.y > 0.0) ? r.y : r.x;
    vec2 q = abs(p) - b + r.x;
    return min(max(q.x, q.y), 0.0) + length(max(q, 0.0)) - r.x;
}

void main() {
    vec3 blurred = texture(Sampler0, texCoord).rgb;

    vec2 halfSize = rectSize * 0.5;
    vec2 center = pixelCoord - halfSize;
    float maxRadius = min(halfSize.x, halfSize.y);
    vec4 rRadii = min(cornerRadii, vec4(maxRadius));
    float dist = roundedBoxSDF(center, halfSize, rRadii);

    float smoothing = max(fwidth(dist), 0.5);
    float alpha = 1.0 - smoothstep(-smoothing, smoothing, dist);

    if (alpha < 0.01) discard;

    fragColor = vec4(blurred, alpha);
}
