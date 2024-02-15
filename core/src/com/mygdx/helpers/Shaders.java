package com.mygdx.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

// shaders used for changing player's and enemy texture color when they get hit by a bullet
public class Shaders{
     private final ShaderProgram shaderProgram;

     public Shaders(){
        String vertex = Gdx.files.internal("vertex_shader.glsl").readString();
        String fragment = Gdx.files.internal("fragment_shader.glsl").readString();
        shaderProgram = new ShaderProgram(vertex, fragment);
     }

     public ShaderProgram getShaderProgram(){
        return shaderProgram;
     }
     public void dispose(){
        shaderProgram.dispose();
     }
}