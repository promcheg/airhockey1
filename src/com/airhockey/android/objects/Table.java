package com.airhockey.android.objects;

import com.airhockey.android.Constants;
import com.airhockey.android.data.VertexArray;
import com.airhockey.android.programs.TextureShaderProgram;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.translateM;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.rotateM;
/**
 * 
 * @author waldemar
 *
 */
public class Table {
	private static final int POSITION_COMPONENT_COUNT = 2;
	private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;

	private static final int STRIDE = 
					(POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT)
					* Constants.BYTES_PER_FLOAT;
	
	private static final float[] VERTEX_DATA = {
			// order of coordinates: X, A, S, T
			// Triangle Fan
			   0f, 	  0f, 	0.5f, 	0.5f,
			-0.5f, -0.8f, 	  0f, 	0.9f,
			 0.5f, -0.8f, 	  1f, 	0.9f, 
			 0.5f,	0.8f,	  1f,	0.1f,
			-0.5f,	0.8f,	  0f,	0.1f,
			-0.5f, -0.8f,	  0f,	0.9f
	};
	
	private final VertexArray vertexArray;
	
	public Table() {
		vertexArray = new VertexArray(VERTEX_DATA);
	}
	
	public void bindData(TextureShaderProgram textureProgram) {
		vertexArray.setVertexAttribPointer(
					0, textureProgram.getPositionAttributeLocation(), 
					POSITION_COMPONENT_COUNT, STRIDE);
		
		vertexArray.setVertexAttribPointer(
					POSITION_COMPONENT_COUNT, textureProgram.getTextureCoordinatesAttributeLocation(), 
					TEXTURE_COORDINATES_COMPONENT_COUNT, STRIDE);
	}
	
	public void draw() {
		glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
	}
}
