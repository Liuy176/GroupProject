package com.mygdx.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class Shaders{
     private ShaderProgram shaderProgram;

     public Shaders(){
        String vertexShader = Gdx.files.internal("vertex_shader.glsl").readString();
        String fragmentShader = Gdx.files.internal("fragment_shader.glsl").readString();
        shaderProgram = new ShaderProgram(vertexShader, fragmentShader);
        if (!shaderProgram.isCompiled()) {
            throw new GdxRuntimeException("Could not compile shader: " + shaderProgram.getLog());
        }
     }

     public ShaderProgram getShaderProgram(){
        return shaderProgram;
     }
     public void dispose(){
        shaderProgram.dispose();
     }
}