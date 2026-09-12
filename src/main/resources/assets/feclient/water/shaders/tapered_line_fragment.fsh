#version 410 core

layout(std140) uniform Uniforms {
    mat4 uProjection;
    vec4 uRect;
    float uThickness;
    float uZ;
    float uLeftTaper;
    float uRightTaper;
    float uLeftRadius;
    float uRightRadius;
    vec2 _pad;
    vec4 uColor;
};

in vec2 vUV;
in vec2 vSize;

out vec4 fragColor;

float dither(vec2 uv) {
    return (fract(sin(dot(uv.xy, vec2(12.9898, 78.233))) * 43758.5453123) - 0.5) * (1.0 / 255.0);
}

void main() {
    vec2 pixelPos = vUV * vSize;
    float x = pixelPos.x;
    float y = pixelPos.y;
    
    float halfWidth = vSize.x * 0.5;
    float halfHeight = vSize.y * 0.5;
    
    float normalizedX = x / vSize.x;
    
    float leftFactor = uLeftTaper;
    float rightFactor = uRightTaper;
    
    float taperProgress;
    float taperAmount;
    
    if (normalizedX < 0.5) {
        taperProgress = normalizedX * 2.0;
        taperAmount = mix(leftFactor, 1.0, taperProgress);
    } else {
        taperProgress = (normalizedX - 0.5) * 2.0;
        taperAmount = mix(1.0, rightFactor, taperProgress);
    }
    
    float currentHalfThickness = (uThickness * 0.5) * taperAmount;
    float centerY = vSize.y * 0.5;
    
    // Signed Distance Field (SDF) for the line
    float dist = abs(y - centerY) - currentHalfThickness;
    
    // Smooth antialiasing using fwidth for screen-space edge softness
    float edgeSoftness = fwidth(dist);
    float alpha = 1.0 - smoothstep(-edgeSoftness, edgeSoftness, dist);
    
    float leftRadius = uLeftRadius;
    float rightRadius = uRightRadius;
    
    if (leftRadius > 0.0) {
        float d = length(vec2(max(leftRadius - x, 0.0), abs(y - centerY))) - currentHalfThickness;
        if (x < leftRadius) {
            alpha = 1.0 - smoothstep(-edgeSoftness, edgeSoftness, d);
        }
    }
    
    if (rightRadius > 0.0) {
        float rightX = vSize.x - x;
        float d = length(vec2(max(rightRadius - rightX, 0.0), abs(y - centerY))) - currentHalfThickness;
        if (rightX < rightRadius) {
            alpha = 1.0 - smoothstep(-edgeSoftness, edgeSoftness, d);
        }
    }
    
    if (alpha <= 0.0) discard;
    
    vec4 color = uColor;
    color.rgb += dither(pixelPos);
    
    fragColor = vec4(color.rgb, color.a * alpha);
}
