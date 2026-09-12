#version 410 core

layout(std140) uniform Uniforms {
    mat4 uProjection;
    vec4 uRect;
    vec2 uSize;
    vec4 uRadius;
    float uSmoothness;
    float uCornerSmoothness;
    float uGlobalAlpha;
    float uFresnelPower;
    vec4 uFresnelColor;
    float uBaseAlpha;
    int uFresnelInvert;
    float uFresnelMix;
    float uDistortStrength;
    float uZ;
    float _pad;
};

out vec2 vFragCoord;
out vec2 vTexCoord;

void main() {
    vec2 vertices[4] = vec2[](
        vec2(0.0, 0.0),
        vec2(1.0, 0.0),
        vec2(1.0, 1.0),
        vec2(0.0, 1.0)
    );
    
    int indices[6] = int[](0, 1, 2, 2, 3, 0);
    vec2 vertex = vertices[indices[gl_VertexID]];
    
    vFragCoord = vertex;
    
    float u = uRect.x / uSize.x;
    float v = (uSize.y - uRect.y - uRect.w) / uSize.y;
    float texW = uRect.z / uSize.x;
    float texH = uRect.w / uSize.y;
    
    vTexCoord.x = u + vertex.x * texW;
    vTexCoord.y = v + (1.0 - vertex.y) * texH;
    
    vec2 pos = uRect.xy + vertex * uRect.zw;
    gl_Position = uProjection * vec4(pos, uZ, 1.0);
}
