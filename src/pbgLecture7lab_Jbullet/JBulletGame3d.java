/*
/* Original code taken JBullet Demos, by Martin Dvorak <jezek2@advel.cz>
 * Modified by Mike Fairbank 2016-02-12
 *
 * Bullet Continuous Collision Detection and Physics Library
 * Copyright (c) 2003-2008 Erwin Coumans  http://www.bulletphysics.com/
 *
 * This software is provided 'as-is', without any express or implied warranty.
 * In no event will the authors be held liable for any damages arising from
 * the use of this software.
 * 
 * Permission is granted to anyone to use this software for any purpose, 
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 * 
 * 1. The origin of this software must not be misrepresented; you must not
 *    claim that you wrote the original software. If you use this software
 *    in a product, an acknowledgment in the product documentation would be
 *    appreciated but is not required.
 * 2. Altered source versions must be plainly marked as such, and must not be
 *    misrepresented as being the original software.
 * 3. This notice may not be removed or altered from any source distribution.
 */

package pbgLecture7lab_Jbullet;

import static com.bulletphysics.demos.opengl.IGL.GL_AMBIENT;
import static com.bulletphysics.demos.opengl.IGL.GL_COLOR_BUFFER_BIT;
import static com.bulletphysics.demos.opengl.IGL.GL_DEPTH_BUFFER_BIT;
import static com.bulletphysics.demos.opengl.IGL.GL_DEPTH_TEST;
import static com.bulletphysics.demos.opengl.IGL.GL_DIFFUSE;
import static com.bulletphysics.demos.opengl.IGL.GL_LESS;
import static com.bulletphysics.demos.opengl.IGL.GL_LIGHT0;
import static com.bulletphysics.demos.opengl.IGL.GL_LIGHT1;
import static com.bulletphysics.demos.opengl.IGL.GL_LIGHTING;
import static com.bulletphysics.demos.opengl.IGL.GL_MODELVIEW;
import static com.bulletphysics.demos.opengl.IGL.GL_POSITION;
import static com.bulletphysics.demos.opengl.IGL.GL_PROJECTION;
import static com.bulletphysics.demos.opengl.IGL.GL_SMOOTH;
import static com.bulletphysics.demos.opengl.IGL.GL_SPECULAR;

import javax.vecmath.Color3f;
import javax.vecmath.Matrix3f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import org.lwjgl.input.Keyboard;

import com.bulletphysics.BulletGlobals;
import com.bulletphysics.BulletStats;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.demos.opengl.GLShapeDrawer;
import com.bulletphysics.demos.opengl.IGL;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.TypedConstraint;
import com.bulletphysics.linearmath.CProfileIterator;
import com.bulletphysics.linearmath.CProfileManager;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.QuaternionUtil;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.linearmath.VectorUtil;

/**
 *
 * @author jezek2
 */
public abstract class JBulletGame3d {

	//protected final BulletStack stack = BulletStack.get();
	
	private static final float STEPSIZE = 5;
	
	public static int numObjects = 0;
	public static final int maxNumObjects = 16384;
	public static Transform[] startTransforms = new Transform[maxNumObjects];
	public static CollisionShape[] gShapePtr = new CollisionShape[maxNumObjects]; //1 rigidbody has 1 shape (no re-use of shapes)
	
	public static RigidBody pickedBody = null; // for deactivation state

	
	static {
		for (int i=0; i<startTransforms.length; i++) {
			startTransforms[i] = new Transform();
		}
	}
	// TODO: class CProfileIterator* m_profileIterator;
	
	// JAVA NOTE: added
	protected IGL gl;


	// this is the most important class
	protected DynamicsWorld dynamicsWorld = null;

	// constraint for mouse picking
	protected TypedConstraint pickConstraint = null;


	protected float cameraDistance = 30f;
	
	protected float ele = 20f;
	protected float azi = 0f;
	protected final Vector3f cameraPosition = new Vector3f(0f, 0f, 0f);
	protected final Vector3f cameraTargetPosition = new Vector3f(0f, 0f, 0f); // look at

	protected float scaleBottom = 0.5f;
	protected float scaleFactor = 2f;
	protected final Vector3f cameraUp = new Vector3f(0f, 1f, 0f);
	protected int forwardAxis = 2;

	protected int glutScreenWidth = 0;
	protected int glutScreenHeight = 0;

	
	protected boolean stepping = true;
	protected boolean singleStep = false;
	protected int lastKey;
	
	private CProfileIterator profileIterator;

	public JBulletGame3d(IGL gl) {
		this.gl = gl;
		
		BulletStats.setProfileEnabled(false);
		profileIterator = CProfileManager.getIterator();
	}
	
	public abstract void initPhysics() throws Exception;
	
	public void destroy() {
		// TODO: CProfileManager::Release_Iterator(m_profileIterator);
		//if (m_shootBoxShape)
		//	delete m_shootBoxShape;
	}

	public void myinit() {
		float[] light_ambient = new float[] { 0.2f, 0.2f, 0.2f, 1.0f };
		float[] light_diffuse = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
		float[] light_specular = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
		/* light_position is NOT default value */
		float[] light_position0 = new float[] { 1.0f, 10.0f, 1.0f, 0.0f };
		float[] light_position1 = new float[] { -1.0f, -10.0f, -1.0f, 0.0f };

		gl.glLight(GL_LIGHT0, GL_AMBIENT, light_ambient);
		gl.glLight(GL_LIGHT0, GL_DIFFUSE, light_diffuse);
		gl.glLight(GL_LIGHT0, GL_SPECULAR, light_specular);
		gl.glLight(GL_LIGHT0, GL_POSITION, light_position0);

		gl.glLight(GL_LIGHT1, GL_AMBIENT, light_ambient);
		gl.glLight(GL_LIGHT1, GL_DIFFUSE, light_diffuse);
		gl.glLight(GL_LIGHT1, GL_SPECULAR, light_specular);
		gl.glLight(GL_LIGHT1, GL_POSITION, light_position1);

		gl.glEnable(GL_LIGHTING);
		gl.glEnable(GL_LIGHT0);
		gl.glEnable(GL_LIGHT1);

		gl.glShadeModel(GL_SMOOTH);
		gl.glEnable(GL_DEPTH_TEST);
		gl.glDepthFunc(GL_LESS);

		gl.glClearColor(0.7f, 0.7f, 0.7f, 0f);

		//glEnable(GL_CULL_FACE);
		//glCullFace(GL_BACK);
	}

	public void setCameraDistance(float dist) {
		cameraDistance = dist;
	}

	public float getCameraDistance() {
		return cameraDistance;
	}

	
	public void updateCamera() {
		gl.glMatrixMode(GL_PROJECTION);
		gl.glLoadIdentity();
		float rele = ele * 0.01745329251994329547f; // rads per deg
		float razi = azi * 0.01745329251994329547f; // rads per deg

		Quat4f rot = new Quat4f();
		QuaternionUtil.setRotation(rot, cameraUp, razi);

		Vector3f eyePos = new Vector3f();
		eyePos.set(0f, 0f, 0f);
		VectorUtil.setCoord(eyePos, forwardAxis, -cameraDistance);

		Vector3f forward = new Vector3f();
		forward.set(eyePos.x, eyePos.y, eyePos.z);
		if (forward.lengthSquared() < BulletGlobals.FLT_EPSILON) {
			forward.set(1f, 0f, 0f);
		}
		Vector3f right = new Vector3f();
		right.cross(cameraUp, forward);
		Quat4f roll = new Quat4f();
		QuaternionUtil.setRotation(roll, right, -rele);

		Matrix3f tmpMat1 = new Matrix3f();
		Matrix3f tmpMat2 = new Matrix3f();
		tmpMat1.set(rot);
		tmpMat2.set(roll);
		tmpMat1.mul(tmpMat2);
		tmpMat1.transform(eyePos);

		cameraPosition.set(eyePos);

		if (glutScreenWidth > glutScreenHeight) {
			float aspect = glutScreenWidth / (float) glutScreenHeight;
			gl.glFrustum(-aspect, aspect, -1.0, 1.0, 1.0, 10000.0);
		}
		else {
			float aspect = glutScreenHeight / (float) glutScreenWidth;
			gl.glFrustum(-1.0, 1.0, -aspect, aspect, 1.0, 10000.0);
		}
		
		gl.glMatrixMode(GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.gluLookAt(cameraPosition.x, cameraPosition.y, cameraPosition.z,
				cameraTargetPosition.x, cameraTargetPosition.y, cameraTargetPosition.z,
				cameraUp.x, cameraUp.y, cameraUp.z);
	}
	
	public void stepLeft() {
		azi -= STEPSIZE;
		if (azi < 0) {
			azi += 360;
		}
		updateCamera();
	}

	public void stepRight() {
		azi += STEPSIZE;
		if (azi >= 360) {
			azi -= 360;
		}
		updateCamera();
	}

	public void stepFront() {
		ele += STEPSIZE;
		if (ele >= 360) {
			ele -= 360;
		}
		updateCamera();
	}

	public void stepBack() {
		ele -= STEPSIZE;
		if (ele < 0) {
			ele += 360;
		}
		updateCamera();
	}

	public void zoomIn() {
		cameraDistance -= 0.4f;
		updateCamera();
		if (cameraDistance < 0.1f) {
			cameraDistance = 0.1f;
		}
	}

	public void zoomOut() {
		cameraDistance += 0.4f;
		updateCamera();
	}

	public void reshape(int w, int h) {
		glutScreenWidth = w;
		glutScreenHeight = h;

		gl.glViewport(0, 0, w, h);
		updateCamera();
	}

	public void keyboardCallback(char key, int x, int y, int modifiers) {
		lastKey = 0;

		if (key >= 0x31 && key < 0x37) {
			int child = key - 0x31;
			profileIterator.enterChild(child);
		}
		if (key == 0x30) {
			profileIterator.enterParent();
		}

		switch (key) {
			case 'l':
				stepLeft();
				break;
			case 'r':
				stepRight();
				break;
			case 'f':
				stepFront();
				break;
			case 'b':
				stepBack();
				break;
			case 'z':
				zoomIn();
				break;
			case 'x':
				zoomOut();
				break;

			default:
				// std::cout << "unused key : " << key << std::endl;
				break;
		}


		//LWJGL.postRedisplay();
	}

	
	public void specialKeyboardUp(int key, int x, int y, int modifiers) {
		//LWJGL.postRedisplay();
	}

	public void specialKeyboard(int key, int x, int y, int modifiers) {
		switch (key) {
			case Keyboard.KEY_F1: {
				break;
			}
			case Keyboard.KEY_F2: {
				break;
			}
			case Keyboard.KEY_END: {
				int numObj = getDynamicsWorld().getNumCollisionObjects();
				if (numObj != 0) {
					CollisionObject obj = getDynamicsWorld().getCollisionObjectArray().getQuick(numObj - 1);

					getDynamicsWorld().removeCollisionObject(obj);
					RigidBody body = RigidBody.upcast(obj);
					if (body != null && body.getMotionState() != null) {
						//delete body->getMotionState();
					}
					//delete obj;
				}
				break;
			}
			case Keyboard.KEY_LEFT:
				stepLeft();
				break;
			case Keyboard.KEY_RIGHT:
				stepRight();
				break;
			case Keyboard.KEY_UP:
				stepFront();
				break;
			case Keyboard.KEY_DOWN:
				stepBack();
				break;
			case Keyboard.KEY_PRIOR /* TODO: check PAGE_UP */:
				zoomIn();
				break;
			case Keyboard.KEY_NEXT /* TODO: checkPAGE_DOWN */:
				zoomOut();
				break;
			default:
				// std::cout << "unused (special) key : " << key << std::endl;
				break;
		}

		//LWJGL.postRedisplay();
	}
	


	public RigidBody localCreateRigidBody(float mass, Transform startTransform, CollisionShape shape) {
		// rigidbody is dynamic if and only if mass is non zero, otherwise static
		boolean isDynamic = (mass != 0f);

		Vector3f localInertia = new Vector3f(0f, 0f, 0f);
		if (isDynamic) {
			shape.calculateLocalInertia(mass, localInertia);
		}

		// using motionstate is recommended, it provides interpolation capabilities, and only synchronizes 'active' objects

		//#define USE_MOTIONSTATE 1
		//#ifdef USE_MOTIONSTATE
		DefaultMotionState myMotionState = new DefaultMotionState(startTransform);
		
		RigidBodyConstructionInfo cInfo = new RigidBodyConstructionInfo(mass, myMotionState, shape, localInertia);
		
		RigidBody body = new RigidBody(cInfo);
		//#else
		//btRigidBody* body = new btRigidBody(mass,0,shape,localInertia);	
		//body->setWorldTransform(startTransform);
		//#endif//
		
		dynamicsWorld.addRigidBody(body);

		return body;
	}

	// See http://www.lighthouse3d.com/opengl/glut/index.php?bmpfontortho
	public void setOrthographicProjection() {
		// switch to projection mode
		gl.glMatrixMode(GL_PROJECTION);
		
		// save previous matrix which contains the 
		//settings for the perspective projection
		gl.glPushMatrix();
		// reset matrix
		gl.glLoadIdentity();
		// set a 2D orthographic projection
		gl.gluOrtho2D(0f, glutScreenWidth, 0f, glutScreenHeight);
		gl.glMatrixMode(GL_MODELVIEW);
		gl.glLoadIdentity();
		
		// invert the y axis, down is positive
		gl.glScalef(1f, -1f, 1f);
		// mover the origin from the bottom left corner
		// to the upper left corner
		gl.glTranslatef(0f, -glutScreenHeight, 0f);
	}
	
	public void resetPerspectiveProjection() {
		gl.glMatrixMode(GL_PROJECTION);
		gl.glPopMatrix();
		gl.glMatrixMode(GL_MODELVIEW);
		updateCamera();
	}
	
	

	private final Transform m = new Transform();
	private Vector3f wireColor = new Vector3f();
	protected Color3f TEXT_COLOR = new Color3f(0f, 0f, 0f);

	public void renderme() {
		gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		updateCamera();

		if (dynamicsWorld != null) {
			int numObjects = dynamicsWorld.getNumCollisionObjects();
			wireColor.set(1f, 0f, 0f);
			for (int i = 0; i < numObjects; i++) {
				CollisionObject colObj = dynamicsWorld.getCollisionObjectArray().getQuick(i);
				RigidBody body = RigidBody.upcast(colObj);

				if (body != null && body.getMotionState() != null) {
					DefaultMotionState myMotionState = (DefaultMotionState) body.getMotionState();
					m.set(myMotionState.graphicsWorldTrans);
				}
				else {
					colObj.getWorldTransform(m);
				}

				wireColor.set(1f, 1f, 0.5f); // wants deactivation
				if ((i & 1) != 0) {
					wireColor.set(0f, 0f, 1f);
				}

				// color differently for active, sleeping, wantsdeactivation states
				if (colObj.getActivationState() == 1) // active
				{
					if ((i & 1) != 0) {
						//wireColor.add(new Vector3f(1f, 0f, 0f));
						wireColor.x += 1f;
					}
					else {
						//wireColor.add(new Vector3f(0.5f, 0f, 0f));
						wireColor.x += 0.5f;
					}
				}
				if (colObj.getActivationState() == 2) // ISLAND_SLEEPING
				{
					if ((i & 1) != 0) {
						//wireColor.add(new Vector3f(0f, 1f, 0f));
						wireColor.y += 1f;
					}
					else {
						//wireColor.add(new Vector3f(0f, 0.5f, 0f));
						wireColor.y += 0.5f;
					}
				}

				GLShapeDrawer.drawOpenGL(gl, m, colObj.getCollisionShape(), wireColor, 0);
			}


			gl.glDisable(GL_LIGHTING);
			gl.glColor3f(0f, 0f, 0f);


			gl.glEnable(GL_LIGHTING);
		}
		
		updateCamera();
	}
	
	public void clientResetScene() {
		//#ifdef SHOW_NUM_DEEP_PENETRATIONS
		BulletStats.gNumDeepPenetrationChecks = 0;
		BulletStats.gNumGjkChecks = 0;
		//#endif //SHOW_NUM_DEEP_PENETRATIONS

		int numObjects = 0;
		if (dynamicsWorld != null) {
			dynamicsWorld.stepSimulation(1f / 60f, 0);
			numObjects = dynamicsWorld.getNumCollisionObjects();
		}

		for (int i = 0; i < numObjects; i++) {
			CollisionObject colObj = dynamicsWorld.getCollisionObjectArray().getQuick(i);
			RigidBody body = RigidBody.upcast(colObj);
			if (body != null) {
				if (body.getMotionState() != null) {
					DefaultMotionState myMotionState = (DefaultMotionState) body.getMotionState();
					myMotionState.graphicsWorldTrans.set(myMotionState.startWorldTrans);
					colObj.setWorldTransform(myMotionState.graphicsWorldTrans);
					colObj.setInterpolationWorldTransform(myMotionState.startWorldTrans);
					colObj.activate();
				}
				// removed cached contact points
				dynamicsWorld.getBroadphase().getOverlappingPairCache().cleanProxyFromPairs(colObj.getBroadphaseHandle(), getDynamicsWorld().getDispatcher());

				body = RigidBody.upcast(colObj);
				if (body != null && !body.isStaticObject()) {
					RigidBody.upcast(colObj).setLinearVelocity(new Vector3f(0f, 0f, 0f));
					RigidBody.upcast(colObj).setAngularVelocity(new Vector3f(0f, 0f, 0f));
				}
			}
		}
	}
	
	public DynamicsWorld getDynamicsWorld() {
		return dynamicsWorld;
	}

	public void setCameraUp(Vector3f camUp) {
		cameraUp.set(camUp);
	}

	public void setCameraForwardAxis(int axis) {
		forwardAxis = axis;
	}

	public Vector3f getCameraPosition() {
		return cameraPosition;
	}

	public Vector3f getCameraTargetPosition() {
		return cameraTargetPosition;
	}
	
	public void drawString(CharSequence s, int x, int y, Color3f color) {
		gl.drawString(s, x, y, color.x, color.y, color.z);
	}

	public void displayCallback() {
		gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		renderme();
	}
}
