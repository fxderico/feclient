#version 150

// Additive bloom composite — samples the blurred mask and writes
// (bloom * intensity) with alpha=1; the composite RenderPipeline is
// bound with additive blend (SRC=ONE, DST=ONE) so this contribution is
// added on top of the main framebuffer without touching depth. Sampler0
// is the final vertical-blur output (the bloom color). The scene is not
// re-sampled — additive hardware blend folds it in.
uniform sampler2D Sampler0;
uniform float uIntensity;

in vec2 vUv;
out vec4 fragColor;

void main() {
    vec3 bloom = texture(Sampler0, vUv).rgb * max(uIntensity, 0.0);
    fragColor = vec4(bloom, 1.0);
}
