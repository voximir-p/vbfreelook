package org.voximir.vbfreelook.utilities;

@FunctionalInterface
public interface Transition {
    double apply(double t);
}
