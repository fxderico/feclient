#version 410 core

layout(std140) uniform Uniforms {
    mat4 uProjection;
    vec4 uRect;
    vec4 uRadii; // x: top-right, y: bottom-right, z: top-left, w: bottom-left
    vec4 uColor;
    float uTime;
    float uZ;
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

vec2 stanh(vec2 a) {
    return tanh(clamp(a, -40.0, 40.0));
}

void main() {
    vec2 pixelPos = vUV * vSize;
    vec2 center = vSize * 0.5;
    
    float dist = sdRoundedBox(pixelPos - center, center, uRadii);
    float edge = fwidth(dist);
    float alpha = 1.0 - smoothstep(-edge, edge, dist);
    
    if (alpha <= 0.0) discard;

    vec2 v = vSize;
    vec2 u = .12 * (2.0 * pixelPos - v) / v.y;
    
    vec4 o;
    vec4 z = o = vec4(1, 2, 3, 0);
    float t = uTime;
     
    for (float a = .5, i = 0.0; ++i < 19.; ) {
        v = cos(++t - 7. * u * pow(a += .03, i)) - 5. * u;
        
        // Matrix rotation and tanh update
        float angle = i + .02 * t;
        float s = sin(angle);
        float c = cos(angle);
        mat2 m = mat2(c, -s, s, c);
        
        u *= m;
        u += stanh(40. * dot(u, u) * cos(100.0 * u.yx + t)) / 200.0
           + .2 * a * u
           + cos(4.0 / exp(dot(o, o) / 100.0) + t) / 300.0;
           
        o += (1.0 + cos(z + t)) / length((1.0 + i * dot(v, v)) * sin(1.5 * u / (0.5 - dot(u, u)) - 9.0 * u.yx + t));
    }
              
    o = 25.6 / (min(o, 13.0) + 164.0 / o) - dot(u, u) / 250.0;
    
    // Applying user color (tinting)
    vec3 finalColor = o.rgb * uColor.rgb;
    fragColor = vec4(finalColor, uColor.a * alpha);
}
