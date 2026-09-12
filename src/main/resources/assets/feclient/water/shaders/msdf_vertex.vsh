#version 150

layout(std140) uniform MsdfData {
    mat4 uProjection;
    vec4 uColor;
    vec4 uParams;
    vec4 uZ_Padding;
};

layout(std140) uniform GlyphData {
    vec4 glyphs[256];
};

out vec2 vTexCoord;
out vec4 vColor;
out float vPxRange;

void main() {
    int glyphIndex = gl_VertexID / 6;
    int vertexIndex = gl_VertexID % 6;
    
    vec2 vertices[4] = vec2[](
        vec2(0.0, 0.0),
        vec2(1.0, 0.0),
        vec2(1.0, 1.0),
        vec2(0.0, 1.0)
    );
    int indices[6] = int[](0, 1, 2, 2, 3, 0);
    vec2 vertex = vertices[indices[vertexIndex]];
    
    vec4 rect = glyphs[glyphIndex * 2];
    vec4 texRect = glyphs[glyphIndex * 2 + 1];
    
    float angle = uParams.y;
    vec2 localPos = vertex * rect.zw;
    
    if (angle != 0.0) {
        vec2 center = rect.zw * 0.5;
        vec2 centered = localPos - center;
        float s = sin(angle);
        float c = cos(angle);
        vec2 rotated = vec2(centered.x * c - centered.y * s, centered.x * s + centered.y * c);
        localPos = rotated + center;
    }
    
    vec2 pos = rect.xy + localPos;
    gl_Position = uProjection * vec4(pos, uZ_Padding.x, 1.0);
    
    vTexCoord = texRect.xy + vertex * texRect.zw;
    vColor = uColor;
    vPxRange = uParams.x;
}
