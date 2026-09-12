#version 410 core

layout(std140) uniform Uniforms {
    mat4 uProjection;
    vec4 uParams;
    vec4 uColor;
};

in vec2 vUV;

out vec4 fragColor;

void main() {
    float dist = length(vUV);
    
    float normDist = dist * 1.5;
    float glow = exp(-normDist * normDist * 3.0);
    
    float alpha = glow * uColor.a;
    
    fragColor = vec4(uColor.rgb, alpha);
}
