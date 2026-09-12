#version 410 core

layout(std140) uniform Uniforms {
    mat4 uProjection;
    vec4 uRect;       // x, y, width, height (screen pixels)
    vec4 uColor;      // rgba
    float uGlowSize;  // glow radius in pixels
    float uZ;
    vec2 _pad;
};

out vec2 vUV;
out vec2 vSize;
out vec2 vGlowSize;

void main() {
    int indices[6] = int[](0, 1, 2, 2, 3, 0);
    vec2 vertices[4] = vec2[](
        vec2(0.0, 0.0),
        vec2(1.0, 0.0),
        vec2(1.0, 1.0),
        vec2(0.0, 1.0)
    );

    vec2 vertex = vertices[indices[gl_VertexID]];
    float expand = uGlowSize;

    // Expand quad by glow size on all sides
    vec2 pos = (uRect.xy - expand) + vertex * (uRect.zw + expand * 2.0);

    vUV      = vertex;
    vSize    = uRect.zw;
    vGlowSize= vec2(expand);

    gl_Position = uProjection * vec4(pos, uZ, 1.0);
}
