package com.nespresso.sofa.interview.parking;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder class to get a parking instance
 */
public class ParkingBuilder {

    private int laneSize;
    private List<Integer> pedestrianExits = new ArrayList<>();
    private List<Integer> disabledSpaces = new ArrayList<>();

    public ParkingBuilder() {
    }

    public ParkingBuilder withSquareSize(final int laneSize) {
        this.laneSize = laneSize;
        return this;
    }

    public ParkingBuilder withPedestrianExit(final int pedestrianExitIndex) {
        this.pedestrianExits.add(pedestrianExitIndex);
        return this;
    }

    public ParkingBuilder withDisabledBay(final int disabledBayIndex) {
        this.disabledSpaces.add(disabledBayIndex);
        return this;
    }

    public Parking build() {
        return new Parking(laneSize, pedestrianExits, disabledSpaces);
    }
}
