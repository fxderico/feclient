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

out vec2 vUV;
out vec2 vSize;

void main() {
    vec2 vertices[4] = vec2[](
        vec2(0.0, 0.0),
        vec2(1.0, 0.0),
        vec2(1.0, 1.0),
        vec2(0.0, 1.0)
    );

    int indices[6] = int[](0, 1, 2, 2, 3, 0);
    vec2 vertex = vertices[indices[gl_VertexID]];

    vUV = vertex;
    vSize = uRect.zw;

    vec2 pos = uRect.xy + vertex * uRect.zw;
    gl_Position = uProjection * vec4(pos, uZ, 1.0);
}
