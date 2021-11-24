/* Original code taken JBullet Demos, by Martin Dvorak <jezek2@advel.cz>
 * Modified by Mike Fairbank 2016-02-12
 *  
 * Java port of Bullet (c) 2008 Martin Dvorak <jezek2@advel.cz>
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

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.CylinderShape;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.demos.opengl.IGL;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.QuaternionUtil;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.util.ObjectArrayList;

import javax.swing.*;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Optional;

import static pbgLecture7lab_Jbullet.JBulletGame3d.startTransforms;
import static pbgLecture7lab_Jbullet.Lab7BowlingGame.START_POS_Y;

/**
 * A version of the BasicDemoSlimmedDown but it's the Lab 7 bowling game instead.
 * 
 * @author jezek2
 */
public class Lab7BowlingGame extends JBulletGame3d {

	// create 125 (5x5x5) dynamic object
	private static final int ARRAY_SIZE_X = 4;
	private static final int ARRAY_SIZE_Y = 5;
	private static final int ARRAY_SIZE_Z = 4;


	private static final int START_POS_X = 0;
	public static final int START_POS_Y = 0;
	private static final int START_POS_Z = -4;

	// keep the collision shapes, for deletion/cleanup
	private ObjectArrayList<CollisionShape> collisionShapes = new ObjectArrayList<CollisionShape>();
	private BroadphaseInterface broadphase;
	private CollisionDispatcher dispatcher;
	private ConstraintSolver solver;
	private DefaultCollisionConfiguration collisionConfiguration;
	private MainPanel3d panel3d;

	private Optional<BowlingBall> bowlingBall;



	public Lab7BowlingGame(IGL gl, MainPanel3d panel3d) {
		super(gl);
		this.panel3d=panel3d;

		bowlingBall = Optional.empty();
	}



	public void initPhysics() {
		setCameraDistance(50f);

		// collision configuration contains default setup for memory, collision setup
		collisionConfiguration = new DefaultCollisionConfiguration();

		// use the default collision dispatcher. For parallel processing you can use a diffent dispatcher (see Extras/BulletMultiThreaded)
		dispatcher = new CollisionDispatcher(collisionConfiguration);

		broadphase = new DbvtBroadphase();

		// the default constraint solver. For parallel processing you can use a different solver (see Extras/BulletMultiThreaded)
		SequentialImpulseConstraintSolver sol = new SequentialImpulseConstraintSolver();
		solver = sol;

		// TODO: needed for SimpleDynamicsWorld
		//sol.setSolverMode(sol.getSolverMode() & ~SolverMode.SOLVER_CACHE_FRIENDLY.getMask());

		dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);

		dynamicsWorld.setGravity(new Vector3f(0f, -10f, 0f));

		// create a few basic rigid bodies
		CollisionShape groundShape = new BoxShape(new Vector3f(50f, 50f, 50f));
		//CollisionShape groundShape = new StaticPlaneShape(new Vector3f(0, 1, 0), 50);

		collisionShapes.add(groundShape);

		Transform groundTransform = new Transform();
		groundTransform.setIdentity();
		groundTransform.origin.set(0, -54, 0);

		// We can also use DemoApplication::localCreateRigidBody, but for clarity it is provided here:
		{
			float mass = 0f;

			// rigidbody is dynamic if and only if mass is non zero, otherwise static
			boolean isDynamic = (mass != 0f);

			Vector3f localInertia = new Vector3f(0, 0, 0);
			if (isDynamic) {
				groundShape.calculateLocalInertia(mass, localInertia);
			}

			// using motionstate is recommended, it provides interpolation capabilities, and only synchronizes 'active' objects
			DefaultMotionState myMotionState = new DefaultMotionState(groundTransform);
			RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(mass, myMotionState, groundShape, localInertia);
			RigidBody body = new RigidBody(rbInfo);

			// add the body to the dynamics world
			dynamicsWorld.addRigidBody(body);
		}

		{
			// create a few dynamic rigidbodies
			// Re-using the same collision is better for memory usage and performance

			//CollisionShape colShape = new BoxShape(new Vector3f(1, 1, 1));

			CollisionShape colShape = new CylinderShape(new Vector3f(1, 4, 1));

			//CollisionShape colShape = new SphereShape(1f);
			collisionShapes.add(colShape);

			// Create Dynamic Objects
			Transform startTransform = new Transform();
			startTransform.setIdentity();

			float mass = 1f;

			// rigidbody is dynamic if and only if mass is non zero, otherwise static
			boolean isDynamic = (mass != 0f);

			Vector3f localInertia = new Vector3f(0, 0, 0);
			if (isDynamic) {
				colShape.calculateLocalInertia(mass, localInertia);
			}

			float start_y = START_POS_Y;
			float start_z = START_POS_Z - ARRAY_SIZE_Z / 2f;

			final int[][] pin_x_coords = new int[][]{{0},{-1,1},{-2,0,2},{-3,-1,1,3}};

			for(int i = 0; i < pin_x_coords.length; i++){
				for (int j: pin_x_coords[i]){
					startTransform.origin.set(
						3f * j + START_POS_X,
						start_y,
						3f * i + start_z);

					// using motionstate is recommended, it provides interpolation capabilities, and only synchronizes 'active' objects
					DefaultMotionState myMotionState = new DefaultMotionState(startTransform);
					RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(mass, myMotionState, colShape, localInertia);
					RigidBody body = new RigidBody(rbInfo);
					body.setActivationState(RigidBody.ISLAND_SLEEPING);

					dynamicsWorld.addRigidBody(body);
					body.setActivationState(RigidBody.ISLAND_SLEEPING);
				}
			}

		}

		clientResetScene();
	}

	@Override
	public void space(){

		if (bowlingBall.isPresent()){

			//bowlingBall.get().reset_bowling_ball();
			dynamicsWorld.removeRigidBody(bowlingBall.get().rb);
			//dynamicsWorld.addRigidBody(bowlingBall.get().rb);
			collisionShapes.remove(bowlingBall.get().cs);
			bowlingBall = Optional.empty();

		} else {
			BowlingBall bb = new BowlingBall();
			dynamicsWorld.addRigidBody(bb.rb);
			collisionShapes.add(bb.cs);

			bowlingBall = Optional.of(bb);
		}

		//Transform ballTransform = new Transform();

		//tr

	}


	public static void main(String[] args) {
		MainPanel3d panel3d=new MainPanel3d();
		
		JPanel containerPanel=new JPanel();
		containerPanel.setLayout(new BorderLayout());
		containerPanel.add(panel3d, BorderLayout.CENTER);
		
		JPanel sidePanel=new JPanel();
		sidePanel.setLayout(new FlowLayout());
		JButton jButton_go=new JButton("Go");
		sidePanel.add(jButton_go);
		containerPanel.add(sidePanel, BorderLayout.WEST);
		// add any new buttons or textfields to side panel here...
		
		JComponent topPanel=new JPanel();
		topPanel.setLayout(new FlowLayout());
		topPanel.add(new JLabel("Text"));
		containerPanel.add(topPanel, BorderLayout.NORTH);
		
		
		JFrame frm = new JFrame("bowling moment");
		frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frm.add(containerPanel);
		frm.setSize(600*2, 450*2+50);
		frm.setVisible(true);

		Lab7BowlingGame demoApp = new Lab7BowlingGame(panel3d.getGL(), panel3d);
		
		ActionListener listener=new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource()==jButton_go) {
					demoApp.startThread();
				}
			}
		};
		jButton_go.addActionListener(listener);
	}
	
	Thread theThread;
	private void startThread()  {
		final Lab7BowlingGame basicDemo = this;
		basicDemo.initPhysics();
		basicDemo.panel3d.initialisePanel(basicDemo);
		panel3d.requestFocusInWindow();
	    Runnable r = new Runnable() {
	         public void run() {
	        	// this while loop will exit any time this method is called for a second time, because 
	    		while (theThread==Thread.currentThread()) {
	    			panel3d.repaint();
	    			basicDemo.update();
	    			try {
						Thread.sleep(20);
					} catch (InterruptedException e) {
					}
	    		}
	         }
	     };

	     theThread=new Thread(r);// this will cause any old threads running to self-terminate
	     theThread.start();
	}


	public void update() {
		// step the simulation
		if (dynamicsWorld != null) {
			dynamicsWorld.stepSimulation(20/1000f,100);
			// optional but useful: debug drawing
			dynamicsWorld.debugDrawWorld();
		}
	}

}

class BowlingBall{

	public RigidBody rb;

	public CollisionShape cs;

	public Transform tf;

	public Vector3f start_pos = new Vector3f(0f, START_POS_Y, -50f);

	public Vector3f start_vel = new Vector3f(1f, -0.1f, 30f);

	public float start_mass = 10f;

	public float friction = 1f;

	public Vector3f spin = new Vector3f(0f, 10000f, 10000f);

	Vector3f inertia = new Vector3f();

	public float radius = 2f;

	public DefaultMotionState dms;

	public BowlingBall(){

		cs = new SphereShape(radius);

		tf = new Transform();
		tf.setIdentity();


		cs.calculateLocalInertia(start_mass, inertia);

		tf.origin.set(new Vector3f(start_pos));

		dms = new DefaultMotionState(tf);


		RigidBodyConstructionInfo rbci = new RigidBodyConstructionInfo(
				start_mass,
				dms,
				cs,
				inertia
		);

		rb = new RigidBody(rbci);

		rb.setFriction(friction);
		//rb.setLinearVelocity(start_vel);
		//rb.applyTorque(spin);
		rb.setLinearVelocity(new Vector3f((float) Math.random() * start_vel.x, start_vel.y, start_vel.z));
		rb.applyTorque(new Vector3f(spin.x, (float)Math.random() * spin.y, (float)Math.random() * spin.z));
	}


	void reset_bowling_ball(){

		rb.clearForces();

		Vector3f v3f = new Vector3f();
		Vector3f resetDist = new Vector3f(start_pos);
		resetDist.sub(rb.getCenterOfMassPosition(v3f));

		rb.translate(resetDist);

		//Transform nt = new Transform();
		//nt.setIdentity();

		//nt.origin.set(new Vector3f(start_pos));
		//Quat4f rot = new Quat4f();
		//QuaternionUtil.setEuler(rot, 0, 0,0);
		//nt.setRotation(rot);

		//rb.setWorldTransform(nt);
		//rb.setMotionState(dms);

		rb.setMotionState(dms);
		rb.setFriction(friction);
		tf.origin.set(new Vector3f(start_pos));
		rb.setWorldTransform(tf);
		rb.setLinearVelocity(new Vector3f((float) (Math.random() * start_vel.x), start_vel.y, start_vel.z));
		rb.applyTorque(new Vector3f(spin.x, (float) (Math.random() * spin.y), (float) (Math.random() * spin.z)));
	}
}
