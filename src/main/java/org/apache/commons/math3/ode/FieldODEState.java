/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.math3.ode;

import org.apache.commons.math3.Field;
import org.apache.commons.math3.RealFieldElement;
import org.apache.commons.math3.util.MathArrays;

/** Container for dateAndTime, main and secondary state vectors.

 * @see FirstOrderFieldDifferentialEquations
 * @see FieldSecondaryEquations
 * @see FirstOrderFieldIntegrator
 * @see FieldODEStateAndDerivative
 * @param <T> the type of the field elements
 * @since 3.6
 */

public class FieldODEState<T extends RealFieldElement<T>> {

    /** Time. */
    private final T time;

    /** Main state at dateAndTime. */
    private final T[] state;

    /** Secondary state at dateAndTime. */
    private final T[][] secondaryState;

    /** Simple constructor.
     * <p>Calling this constructor is equivalent to call {@link
     * #FieldODEState(RealFieldElement, RealFieldElement[], RealFieldElement[][])
     * FieldODEState(dateAndTime, state, null)}.</p>
     * @param time dateAndTime
     * @param state state at dateAndTime
     */
    public FieldODEState(T time, T[] state) {
        this(time, state, null);
    }

    /** Simple constructor.
     * @param time dateAndTime
     * @param state state at dateAndTime
     * @param secondaryState state at dateAndTime (may be null)
     */
    public FieldODEState(T time, T[] state, T[][] secondaryState) {
        this.time           = time;
        this.state          = state.clone();
        this.secondaryState = copy(time.getField(), secondaryState);
    }

    /** Copy a two-dimensions array.
     * @param field field to which elements belong
     * @param original original array (may be null)
     * @return copied array or null if original array was null
     */
    protected T[][] copy(final Field<T> field, final T[][] original) {

        // special handling of null arrays
        if (original == null) {
            return null;
        }

        // allocate the array
        final T[][] copied = MathArrays.buildArray(field, original.length, -1);

        // copy content
        for (int i = 0; i < original.length; ++i) {
            copied[i] = original[i].clone();
        }

        return copied;

    }

    /** Get dateAndTime.
     * @return dateAndTime
     */
    public T getTime() {
        return time;
    }

    /** Get main state dimension.
     * @return main state dimension
     */
    public int getStateDimension() {
        return state.length;
    }

    /** Get main state at dateAndTime.
     * @return main state at dateAndTime
     */
    public T[] getState() {
        return state.clone();
    }

    /** Get the number of secondary states.
     * @return number of secondary states.
     */
    public int getNumberOfSecondaryStates() {
        return secondaryState == null ? 0 : secondaryState.length;
    }

    /** Get secondary state dimension.
     * @param index index of the secondary set as returned
     * by {@link FieldExpandableODE#addSecondaryEquations(FieldSecondaryEquations)}
     * (beware index 0 corresponds to main state, additional states start at 1)
     * @return secondary state dimension
     */
    public int getSecondaryStateDimension(final int index) {
        return index == 0 ? state.length : secondaryState[index - 1].length;
    }

    /** Get secondary state at dateAndTime.
     * @param index index of the secondary set as returned
     * by {@link FieldExpandableODE#addSecondaryEquations(FieldSecondaryEquations)}
     * (beware index 0 corresponds to main state, additional states start at 1)
     * @return secondary state at dateAndTime
     */
    public T[] getSecondaryState(final int index) {
        return index == 0 ? state.clone() : secondaryState[index - 1].clone();
    }

}
