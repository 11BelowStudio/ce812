/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package crappy;

/**
 * The interface of the CrappyConnector which only exposes the methods of it that a CrappyBody uses.
 */
public interface CrappyConnectorBodyInterface {

    /**
     * Can the attached bodies collide with each other?
     * @return true if the attached bodies are allowed to collide with each other, otherwise false.
     */
    boolean canBodiesCollide();

    /**
     * Returns the other body this connector connects
     * @param bod the body we want the other one to
     * @return bodyB if bod is bodyA, else return bodyA.
     */
    CrappyBody_Connector_Interface getOtherBody(final IHaveIdentifier bod);
}
