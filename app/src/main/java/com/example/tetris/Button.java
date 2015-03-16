package com.example.tetris;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

public class Button {

	private int current_texture;
	private int[] textures2;
	
	private FloatBuffer vertexBuffer;	// buffer holding the vertices

	public float[] vertices;
	
	private FloatBuffer textureBuffer;	// buffer holding the texture coordinates
	private float texture[] = {        
			// Mapping coordinates for the vertices
			0.0f, 1.0f,     // top left     (V2)
			0.0f, 0.0f,     // bottom left  (V1)
			1.0f, 1.0f,     // top right    (V4)
			1.0f, 0.0f      // bottom right (V3)
	};
	
	/** The texture pointer */
	private int[] textures = new int[2];

	public Button(float[] vertices2, int texture1, int texture2) {
		
		vertices = vertices2;
		textures2 = new int[2];
		textures2[0] = texture1;
		textures2[1] = texture2;
		
		current_texture = 0;
		
		// a float has 4 bytes so we allocate for each coordinate 4 bytes
		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
		byteBuffer.order(ByteOrder.nativeOrder());
		
		// allocates the memory from the byte buffer
		vertexBuffer = byteBuffer.asFloatBuffer();
		
		// fill the vertexBuffer with the vertices
		vertexBuffer.put(vertices);
		
		// set the cursor position to the beginning of the buffer
		vertexBuffer.position(0);
		
		byteBuffer = ByteBuffer.allocateDirect(texture.length * 4);
		byteBuffer.order(ByteOrder.nativeOrder());
		textureBuffer = byteBuffer.asFloatBuffer();
		textureBuffer.put(texture);
		textureBuffer.position(0);
	}

	/**
	 * Load the texture for the square
	 * @param gl
	 * @param context
	 */
	public void loadGLTexture(GL10 gl, Context context) {
		// generate two texture pointers
		gl.glGenTextures(2, textures, 0);
		for (int i = 0; i < textures2.length; ++i) {
			// loading texture
			Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), textures2[i]);
			
			//...and bind it to our array
			gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[i]);
		
			// create nearest filtered texture
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		
			// Use Android GLUtils to specify a two-dimensional texture image from our bitmap 
			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
		
			// Clean up
			bitmap.recycle();
		}
	}

	
	/** The draw method for the square with the GL context */
	public void draw(GL10 gl) {
		// bind the previously generated texture
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[current_texture]);
		
		//gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
		//gl.glColor4f(1.0f, 1.0f, 1.0f, 0.0f);
		//gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
		//gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		
		// Point to our buffers
		//gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		
		
		// Set the face rotation
		gl.glFrontFace(GL10.GL_CW);
		
		// Point to our vertex buffer
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);
		
		// Draw the vertices as triangle strip
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, vertices.length / 3);
		
		
		//Disable the client state before leaving
		//gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		//gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
	}
	
	public void setTexture(int number) {
		current_texture = number;
	}
}
