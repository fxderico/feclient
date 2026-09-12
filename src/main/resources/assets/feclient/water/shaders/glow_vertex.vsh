#version 410 core

layout(std140) uniform Uniforms {
    mat4 uProjection;
    vec4 uParams;
    vec4 uColor;
};

out vec2 vUV;

void main() {
    float x = uParams.x;
    float y = uParams.y;
    float size = uParams.z;
    float z = uParams.w;
    
    float expand = size * 1.5;
    
    vec2 positions[6];
    positions[0] = vec2(0.0, 0.0);
    positions[1] = vec2(1.0, 0.0);
    positions[2] = vec2(1.0, 1.0);
    positions[3] = vec2(0.0, 0.0);
    positions[4] = vec2(1.0, 1.0);
    positions[5] = vec2(0.0, 1.0);
    
    vec2 pos = positions[gl_VertexID];
    vec2 pixelPos = vec2(x - expand, y - expand) + pos * expand * 2.0;
    
    vUV = pos * 2.0 - 1.0;
    
    gl_Position = uProjection * vec4(pixelPos, z, 1.0);
}
