#version 150

in vec2 texCoord;
in vec2 offset;

out vec4 fragColor;

uniform sampler2D Sampler0;

void main() {
    vec4 sum = texture(Sampler0, texCoord) * 4.0;
    sum += texture(Sampler0, texCoord + vec2(-offset.x, -offset.y));
    sum += texture(Sampler0, texCoord + vec2( offset.x, -offset.y));
    sum += texture(Sampler0, texCoord + vec2(-offset.x,  offset.y));
    sum += texture(Sampler0, texCoord + vec2( offset.x,  offset.y));
    fragColor = sum / 8.0;
}
