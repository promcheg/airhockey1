/***
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
***/
package com.airhockey.android;

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
import static android.opengl.Matrix.orthoM;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.airhockey.android.util.LoggerConfig;
import com.airhockey.android.util.RawResourceReader;
import com.airhockey.android.util.ShaderHelper;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;

public class AirHockeyRenderer implements Renderer {
	private static final int POSITION_COMPONENT_COUNT = 4;
	private static final int BYTES_PER_FLOAT = 4;
	private final FloatBuffer vertexData;
	private int program;
	private static final String A_POSITION ="a_Position";
	private static final String A_COLOR = "a_Color";
	private static final String U_MATRIX = "u_Matrix";
	private final float[] projectionMatrix = new float[16];
	
	private static final int COLOR_COMPONENT_COUNT = 3;
	private static final int STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT;
	private int aPositionLocation;
	private int aColorLocation;
	private int uMatrixLocation;
	
	
	/**
	 * Triangle fan:
	 * 5		4				0f,    0f,
	 * 						 -0.5f, -0.5f,
	 * 		1
	 *  
	 * 2,6		3
	 */

//	float[] tableVertices = {	//Triangle fan
//			   0f,    0f,
//			-0.5f, -0.5f,
//			 0.5f, -0.5f,
//			 0.5f,  0.5f,
//			-0.5f,  0.5f,
//			-0.5f, -0.5f,
//			
//			//Line 1
//			-0.5f, 0f,
//			0.5f, 0f,
//			//Mallets
//			0f, -0.25f,
//			0f, 0.25f};

	float[] tableVertices = {
						// Order of coordinates: X, Y, Z, W, R, G, B
						// Triangle fan
								   0f,    0f,   0f, 1.5f,   1f,   1f,   1f,
								-0.5f, -0.8f,   0f,   1f, 0.7f, 0.7f, 0.7f,
								 0.5f, -0.8f,   0f,   1f, 0.7f, 0.7f, 0.7f,
								 0.5f,  0.8f,   0f,   2f, 0.7f, 0.7f, 0.7f,
								-0.5f,  0.8f,   0f,   2f, 0.7f, 0.7f, 0.7f,
								-0.5f, -0.8f,   0f,   1f, 0.7f, 0.7f, 0.7f,
								
								//Line 1
								-0.5f, 0f,   0f, 1.5f,   1f, 0f, 0f,
								 0.5f, 0f,   0f, 1.5f,   1f, 0f, 0f,
								//Mallets
								0f, -0.4f,   0f, 1.25f,   0f, 0f, 1f,
								0f,  0.4f,   0f, 1.25f,   1f, 0f, 0f};
	private Context context;
	
	
    public AirHockeyRenderer(final Context activityContext) {
		super();
		vertexData = ByteBuffer.allocateDirect(tableVertices.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
		vertexData.put(tableVertices);
		context = activityContext;
	}

	@Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        // Set the background clear color to red. The first component is
        // red, the second is green, the third is blue, and the last
        // component is alpha, which we don't use in this lesson.
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        
        String vertexShaderSource = RawResourceReader.readTextFileFromRawResource(context, R.raw.simple_vertex_shader);
        String fragmentShaderSource = RawResourceReader.readTextFileFromRawResource(context, R.raw.simple_fragment_shader);
        int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
        int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);
        
        program = ShaderHelper.createAndLinkProgram(vertexShader, fragmentShader);
        
        if(LoggerConfig.ON) {
        	ShaderHelper.validateProgram(program);
        }
        
        glUseProgram(program);
        
        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
        
        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        aColorLocation = glGetAttribLocation(program, A_COLOR);
        
        vertexData.position(0);
        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, vertexData);
        glEnableVertexAttribArray(aPositionLocation);
        
        vertexData.position(POSITION_COMPONENT_COUNT);
        glVertexAttribPointer(aColorLocation, COLOR_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, vertexData);
        glEnableVertexAttribArray(aColorLocation);
    }

    /**
     * onSurfaceChanged is called whenever the surface has changed. This is
     * called at least once when the surface is initialized. Keep in mind that
     * Android normally restarts an Activity on rotation, and in that case, the
     * renderer will be destroyed and a new one created.
     * 
     * @param width
     *            The new width, in pixels.
     * @param height
     *            The new height, in pixels.
     */
    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        // Set the OpenGL viewport to fill the entire surface.
        glViewport(0, 0, width, height);
        
        final float aspectRatio = width > height ? (float) width / (float) height : (float) height/ (float) width;
        
        if(width > height) {
        	// Landscape
        	orthoM(projectionMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f);
        } else {
        	// Protrait or square
        	orthoM(projectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, -1f, 1f);
        }
        
    }

    /**
     * OnDrawFrame is called whenever a new frame needs to be drawn. Normally,
     * this is done at the refresh rate of the screen.
     */
    @Override
    public void onDrawFrame(GL10 glUnused) {
        // Clear the rendering surface.
        glClear(GL_COLOR_BUFFER_BIT);

        glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0);
        
        glDrawArrays(GL_TRIANGLE_FAN, 0, 6);

        //Draw line
        glDrawArrays(GL_LINES, 6, 2);
        
        //Draw first mallet blue
        glDrawArrays(GL_POINTS, 8, 1);

        //Draw the second mallet red
        glDrawArrays(GL_POINTS, 9, 1);
    }
}
