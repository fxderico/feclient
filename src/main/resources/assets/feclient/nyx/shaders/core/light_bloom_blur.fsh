#version 150

// Separable 9-tap Gaussian blur — direction switch via uDirection
// (1,0) for the horizontal pass, (0,1) for the vertical pass. uTexel is
// (1/width, 1/height) of the source texture. uSpread scales the tap
// offsets so the visual blur radius can be widened without adding taps
// (cheap "pseudo-large" blur — glow softens further, no extra cost).
uniform sampler2D Sampler0;
uniform vec2 uTexel;
uniform vec2 uDirection;
uniform float uSpread;

in vec2 vUv;
out vec4 fragColor;

const float W0 = 0.227027;
const float W1 = 0.194595;
const float W2 = 0.121622;
const float W3 = 0.054054;
const float W4 = 0.016216;

void main() {
    vec2 step = uDirection * uTexel * max(uSpread, 1.0);

    vec3 c = texture(Sampler0, vUv).rgb * W0;

    c += texture(Sampler0, vUv + step * 1.0).rgb * W1;
    c += texture(Sampler0, vUv - step * 1.0).rgb * W1;

    c += texture(Sampler0, vUv + step * 2.0).rgb * W2;
    c += texture(Sampler0, vUv - step * 2.0).rgb * W2;

    c += texture(Sampler0, vUv + step * 3.0).rgb * W3;
    c += texture(Sampler0, vUv - step * 3.0).rgb * W3;

    c += texture(Sampler0, vUv + step * 4.0).rgb * W4;
    c += texture(Sampler0, vUv - step * 4.0).rgb * W4;

    fragColor = vec4(c, 1.0);
}
