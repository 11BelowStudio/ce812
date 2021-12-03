package pbgLecture8lab_wrapperForJBox2D;

import org.jbox2d.common.Vec2;

import java.awt.*;

public class LaunchedParticle extends BasicParticle {

    static float min_vel_before_despawn = 0.01f;

    static float despawn_timer_len = 2f;

    private float despawn_timer;

    private boolean despawn_timer_started;


    public LaunchedParticle(Vec2 p, Vec2 v, float radius, Color col, float mass, float linearDragForce){
        this(p.x, p.y, v.x, v.y, radius, col, mass, linearDragForce);
    }

    public LaunchedParticle(float sx, float sy, float vx, float vy, float radius, Color col, float mass, float linearDragForce) {
        super(sx, sy, vx, vy, radius, col, mass, linearDragForce);
        despawn_timer_started = false;
        despawn_timer = despawn_timer_len;
    }


    @Override
    public void notificationOfNewTimestep() {
        super.notificationOfNewTimestep();
        if (despawn_timer_started) {
            despawn_timer -= BasicPhysicsEngineUsingBox2D.DELTA_T;
            if (despawn_timer <= 0){
                body.setActive(false);
            }
        }
        else if (Math.sqrt(body.getLinearVelocity().lengthSquared()) <= min_vel_before_despawn){
            despawn_timer_started = true;
        }
    }
}
