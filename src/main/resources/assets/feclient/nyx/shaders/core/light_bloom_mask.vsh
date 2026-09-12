#version 150

// Mask-fill vertex shader — used when EspBoxRenderer.submitBoxFilled draws
// the yellow light-block quads into the mask FBO. Passes POSITION_COLOR
// straight through the standard MC MVP + projection matrices (bound as
// uniforms by the vanilla RenderPipelines POSITION_COLOR_SNIPPET path).
in vec3 Position;
in vec4 Color;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;

out vec4 vColor;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
    vColor = Color;
}
