#ifdef GL_ES
precision mediump float;
#endif

varying vec2 v_texCoords;
uniform sampler2D u_texture;

// Uniform to control the application of the damage effect
uniform float u_damageEffect;

void main() {
    vec4 texColor = texture2D(u_texture, v_texCoords);

    // Check if the pixel is not fully transparent
    if (texColor.a > 0.0) {
        // Define a darker red color
        vec4 darkRed = vec4(0.8, 0.0, 0.0, 1.0); // Adjust RGB values for desired darkness

        // Apply the dark red effect based on the damage effect uniform
        // The mix function blends the original texture color with the dark red
        // The effect is only applied to non-transparent pixels
        vec4 finalColor = mix(texColor, darkRed, u_damageEffect * texColor.a);
        gl_FragColor = finalColor;
    } else {
        // Keep fully transparent pixels unchanged
        gl_FragColor = texColor;
    }
}


