package com.nespresso.sofa.interview.parking;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles the parking mechanisms: park/unpark a car (also for disabled-only bays)
 * and provides a string representation of its state.
 */
public class Parking {

    public static final Character PEDESTRIAN_SLOT = '=';
    public static final Character DISABLED_SLOT_FREE = '@';
    public static final Character DISABLED_SLOT_TAKEN = 'D';
    public static final Character SLOT_FREE = 'U';

    private int total;

    private int laneSize;
    private List<Integer> pedestrianExits;
    private List<Integer> disabledSpaces;

    private int parkedCars = 0;

    private List<Character> bays;


    public Parking(int laneSize, List<Integer> pedestrianExits, List<Integer> disabledSpaces) {
        this.total = laneSize * laneSize;
        this.laneSize = laneSize;
        this.pedestrianExits = pedestrianExits;
        this.disabledSpaces = disabledSpaces;
        this.bays = buildBays();
    }

    private List<Character> buildBays() {
        final List<Character> bays = new ArrayList<>();
        for (int i = 0; i < total; i++) {
            bays.add(SLOT_FREE);
        }

        pedestrianExits.stream().forEach(e -> bays.set(e, PEDESTRIAN_SLOT));
        disabledSpaces.stream().forEach(e -> bays.set(e, DISABLED_SLOT_FREE));
        return bays;

    }

    /**
     * @return the number of available parking bays left
     */
    public int getAvailableBays() {
        int pedestrianExitsSize = 0;
        if (CollectionUtils.isNotEmpty(pedestrianExits)) {
            pedestrianExitsSize = pedestrianExits.size();
        }
        return total - pedestrianExitsSize - parkedCars;
    }

    /**
     * Park the car of the given type ('D' being dedicated to disabled people)
     * in closest -to pedestrian exit- and first (starting from the parking's entrance)
     * available bay. Disabled people can only park on dedicated bays.
     *
     * @param carType the car char representation that has to be parked
     * @return bay index of the parked car, -1 if no applicable bay found
     */
    public int parkCar(final char carType) {
        if (getAvailableBays() == 0) {
            return -1;
        }

        //check slots near pedestrian exists
        //indent should increase if both sides of pedestrian exist are taken for all exits
        int indent = 1;
        boolean slotExists = true;
        while (slotExists) {
            for (int i = 0; i < pedestrianExits.size(); i++) {
                int pedestrianExistSlot = pedestrianExits.get(i);
                if (pedestrianExistSlot == 0 && SLOT_FREE.equals(bays.get(1))) {
                    bays.set(1, carType);
                    parkedCars++;
                    return 1;
                }

                //going left of pedestrian exit
                int potentialSlotIndexLeft = pedestrianExistSlot - indent;
                if (findFreeSlot(carType, potentialSlotIndexLeft)) {
                    return potentialSlotIndexLeft;
                }

                //going right of pedestrian exit
                int potentialSlotIndexRight = pedestrianExistSlot + indent;
                if (findFreeSlot(carType, potentialSlotIndexRight)) {
                    return potentialSlotIndexRight;
                }
                slotExists = !inValidIndexSlot(potentialSlotIndexLeft)
                        && !inValidIndexSlot(potentialSlotIndexRight);
            }
            indent++;
        }
        return -1;
    }

    private boolean findFreeSlot(char carType, int potentialSlotIndex) {
        if (inValidIndexSlot(potentialSlotIndex)) {
            return false;
        }
        if (DISABLED_SLOT_TAKEN.equals(carType)) {
            if (DISABLED_SLOT_FREE.equals(bays.get(potentialSlotIndex))) {
                bays.set(potentialSlotIndex, carType);
                parkedCars++;
                return true;
            }
        } else if (SLOT_FREE.equals(bays.get(potentialSlotIndex))) {
            bays.set(potentialSlotIndex, carType);
            parkedCars++;
            return true;
        }
        return false;
    }

    private boolean inValidIndexSlot(int potentialSlotIndex) {
        if (potentialSlotIndex < 0 || potentialSlotIndex >= bays.size()) {
            return true;
        }
        return false;
    }


    /**
     * Unpark the car from the given index
     *
     * @param index
     * @return true if a car was parked in the bay, false otherwise
     */
    public boolean unparkCar(final int index) {
        char type = bays.get(index);
        if (SLOT_FREE.equals(type)
                || DISABLED_SLOT_FREE.equals(type)
                || PEDESTRIAN_SLOT.equals(type)) {
            return false;
        } else {
            if (DISABLED_SLOT_TAKEN.equals(type)) {
                bays.set(index, DISABLED_SLOT_FREE);
                parkedCars--;
                return true;
            } else {
                bays.set(index, SLOT_FREE);
                parkedCars--;
                return true;
            }
        }
    }

    /**
     * Print a 2-dimensional representation of the parking with the following rules:
     * <ul>
     * <li>'=' is a pedestrian exit
     * <li>'@' is a disabled-only empty bay
     * <li>'U' is a non-disabled empty bay
     * <li>'D' is a disabled-only occupied bay
     * <li>the char representation of a parked vehicle for non-empty bays.
     * </ul>
     * U, D, @ and = can be considered as reserved chars.
     * <p>
     * Once an end of lane is reached, then the next lane is reversed (
     * to represent the fact that cars need to turn around)
     *
     * @return the string representation of the parking as a 2-dimensional square.
     * Note that cars do a U turn to continue to the next lane.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int startIndex = 0;
        while (startIndex < total - laneSize - 1) {
            startIndex = printOddLane(sb, startIndex);
            startIndex = printEvenLane(sb, startIndex + laneSize);
        }

        return sb.toString();
    }

    private int printOddLane(StringBuilder sb, int startIndex) {
        //-->
        int step = 0;
        while (step < laneSize) {
            sb.append(bays.get(startIndex + step));
            step++;
        }
        sb.append("\n");
        return step;
    }

    private int printEvenLane(StringBuilder sb, int startIndex) {
        //<--
        int step = 0;
        while (step < laneSize) {
            sb.append(bays.get(startIndex - step));
            step++;
        }
        sb.append("\n");
        return startIndex + laneSize + 1;
    }

}
