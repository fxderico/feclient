#version 150

// Mask-fill fragment shader — writes solid packed-color into the light mask
// FBO. The mask FBO is HDR-ish (rgba8 for now, no depth) and this just dumps
// the vertex color through unmodified. Blur passes then read this texture.
in vec4 vColor;

out vec4 fragColor;

void main() {
    fragColor = vColor;
}
