#version 150

in vec2 texCoord;
out vec4 fragColor;

uniform sampler2D Sampler0;
uniform vec2 uResolution;

void main() {
    vec2 texel = 1.0 / uResolution;
    float weights[9] = float[](0.0625, 0.125, 0.0625,
                                0.125,  0.25,  0.125,
                                0.0625, 0.125, 0.0625);
    vec4 result = vec4(0.0);
    // Horizontal 9-tap Gaussian
    float w[5] = float[](0.2270270270, 0.1945945946, 0.1216216216, 0.0540540541, 0.0162162162);
    result += texture(Sampler0, texCoord) * w[0];
    for (int i = 1; i < 5; i++) {
        result += texture(Sampler0, texCoord + vec2(texel.x * float(i), 0.0)) * w[i];
        result += texture(Sampler0, texCoord - vec2(texel.x * float(i), 0.0)) * w[i];
    }
    fragColor = result;
}
