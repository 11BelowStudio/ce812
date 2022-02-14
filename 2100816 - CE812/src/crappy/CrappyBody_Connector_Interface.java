package crappy;

import crappy.math.I_Rot2D;
import crappy.math.I_Vect2D;
import crappy.math.Rot2D;
import crappy.math.Vect2D;

/**
 * An interface which the CrappyConnectors can use to look at the CrappyBodies.
 * @author Rachel Lowe
 */
public interface CrappyBody_Connector_Interface extends I_Transform, IHaveIdentifier{
    /*
     * This Source Code Form is subject to the terms of the Mozilla Public
     * License, v. 2.0. If a copy of the MPL was not distributed with this
     * file, You can obtain one at https://mozilla.org/MPL/2.0/.
     */

    Vect2D getPos();

    I_Vect2D getTempPos();

    Vect2D getVel();

    I_Vect2D getTempVel();

    Rot2D getRot();

    I_Rot2D getTempRot();

    double getAngVel();

    double getTempAngVel();

    void applyForce(final I_Vect2D force, final I_Vect2D localForcePos, final CrappyBody.FORCE_SOURCE source);

    void applyMidTimestepForce(final I_Vect2D force, final I_Vect2D localForcePos, final CrappyBody.FORCE_SOURCE source);

    boolean isActive();

    void __addConnector_internalPlsDontUseManually(final CrappyConnectorBodyInterface c);

    void __removeConnector_internalPlsDontUseManually(final CrappyConnectorBodyInterface c);


}
