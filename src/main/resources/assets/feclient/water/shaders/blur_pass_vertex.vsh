#version 150

layout(std140) uniform BlurData {
    mat4 uProjection;
    vec4 rect;
    vec4 screen;
    vec4 framebufferSize;
    vec4 radii;
    vec4 color;
    float uZ;
};

out vec2 texCoord;
out vec2 offset;

void main() {
    vec2 positions[6] = vec2[](
        vec2(0.0, 0.0),
        vec2(1.0, 0.0),
        vec2(1.0, 1.0),
        vec2(0.0, 0.0),
        vec2(1.0, 1.0),
        vec2(0.0, 1.0)
    );

    vec2 pos = positions[gl_VertexID];
    gl_Position = vec4(pos * 2.0 - 1.0, uZ, 1.0);
    texCoord = pos;
    offset = screen.w / framebufferSize.xy;
}
