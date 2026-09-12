#version 150

layout(std140) uniform Uniforms {
    mat4 uProjection;
    vec4 uRect;
    vec4 uColor;
    vec4 uRadius_Padding;
    vec4 uZ_Padding;
};

out vec2 vUV;
out vec2 vPos;

void main() {
    vec2 vertices[4] = vec2[](
        vec2(0.0, 0.0),
        vec2(1.0, 0.0),
        vec2(1.0, 1.0),
        vec2(0.0, 1.0)
    );
    int indices[6] = int[](0, 1, 2, 2, 3, 0);
    vec2 vertex = vertices[indices[gl_VertexID % 6]];
    
    vPos = vertex * uRect.zw;
    vec2 pos = uRect.xy + vPos;
    gl_Position = uProjection * vec4(pos, uZ_Padding.x, 1.0);
    
    vUV = vertex;
}
