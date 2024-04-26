package ch.epfl.chacun;

import java.util.*;

/**
 * This class represents an Area in the game. An Area is a record that contains a set of zones, a list of occupants, and the number of open connections.
 * @param <Z> The type of Zone this Area can contain.
 * @author Bjork Pedersen (376143)
 */
public record Area<Z extends Zone>(Set<Z> zones, List<PlayerColor> occupants, int openConnections) {

    /**
     * Constructor for the Area record.
     * It checks if the number of open connections is greater than or equal to 0.
     * If the occupants list is null, it initializes it as an empty list.
     * It also makes a copy of the zones set and the occupants list, and sorts the occupants list.
     * @throws IllegalArgumentException if the number of open connections is less than 0
     */
    public Area {
        Preconditions.checkArgument(openConnections >= 0);
        if (occupants == null ) {
            occupants = new ArrayList<>();
        }
        zones = Set.copyOf(zones);
        List<PlayerColor> sortedOccupants = new ArrayList<>(occupants);
        Collections.sort(sortedOccupants);
        occupants = Collections.unmodifiableList(sortedOccupants);
    }

    /**
     * Checks if a forest area has a menhir.
     * @param forest The forest area to check.
     * @return true if the forest has a menhir, false otherwise.
     */
    public static boolean hasMenhir(Area<Zone.Forest> forest) {
        for (Zone.Forest zone : forest.zones) {
            if (zone.kind() == Zone.Forest.Kind.WITH_MENHIR) {
                return true;
            }
        }
        return false;
    }

    /**
     * Counts the number of mushroom groups in a forest area.
     * @param forest The forest area to count the mushroom groups in.
     * @return The number of mushroom groups in the forest.
     */
    public static int mushroomGroupCount(Area<Zone.Forest> forest) {
        int count = 0;
        for (Zone.Forest zone : forest.zones) {
            if (zone.kind() == Zone.Forest.Kind.WITH_MUSHROOMS) {
                count++;
            }
        }
        return count;
    }

    /**
     * Gets the set of animals in a meadow area, excluding the cancelled animals.
     * @param meadow The meadow area to get the animals from.
     * @param cancelledAnimals The set of cancelled animals.
     * @return The set of animals in the meadow, excluding the cancelled animals.
     */
    public static Set<Animal> animals(Area<Zone.Meadow> meadow, Set<Animal> cancelledAnimals) {
        Set<Animal> animals = new HashSet<>();
        for (Zone.Meadow zone: meadow.zones) {
            for (Animal animal : zone.animals()) {
                if (!(cancelledAnimals.contains(animal))) {
                    animals.add(animal);
                }
            }
        }
        return animals;
    }

    /**
     * Counts the number of fish in a river area.
     * @param river The river area to count the fish in.
     * @return The number of fish in the river.
     */
    public static int riverFishCount(Area<Zone.River> river) {
        int count = 0;
        Set<Zone.Lake> countedLakes = new HashSet<>();
        for (Zone.River individualRiver : river.zones) {
            count = count + individualRiver.fishCount();
            if (individualRiver.hasLake() && !(countedLakes.contains(individualRiver.lake()))) {
                count = count + individualRiver.lake().fishCount();
                countedLakes.add(individualRiver.lake());
            }
        }
        return count;
    }

    /**
     * Counts the number of fish in a river system area.
     * @param riverSystem The river system area to count the fish in.
     * @return The number of fish in the river system.
     */
    public static int riverSystemFishCount(Area<Zone.Water> riverSystem) {
        int count = 0;
        for (Zone.Water zone : riverSystem.zones) {
            count = count + zone.fishCount();
        }
        return count;
    }

    /**
     * Counts the number of lakes in a river system area.
     * @param riverSystem The river system area to count the lakes in.
     * @return The number of lakes in the river system.
     */
    public static int lakeCount(Area<Zone.Water> riverSystem) {
        int count = 0;
        for (Zone.Water zone: riverSystem.zones) {
            if (zone instanceof Zone.Lake) {
                count++;
            }
        }
        return count;
    }

    /**
     * Checks if the area is closed.
     * @return true if the area is closed, false otherwise.
     */
    public boolean isClosed() {
        return openConnections == 0;
    }

    /**
     * Checks if the area is occupied.
     * @return true if the area is occupied, false otherwise.
     */
    public boolean isOccupied() {
        if (occupants == null) {
            return false;
        }
        return !occupants.isEmpty();
    }

    /**
     * Gets the set of occupants who have the majority.
     * @return The set of occupants who have the majority.
     */
    public Set<PlayerColor> majorityOccupants() {
        int redCount = 0;
        int blueCount = 0;
        int yellowCount = 0;
        int greenCount = 0;
        int purpleCount = 0;

        for (PlayerColor color : occupants) {
            switch (color) {
                case PlayerColor.RED:
                    redCount++;
                    break;
                case PlayerColor.BLUE:
                    blueCount++;
                    break;
                case PlayerColor.GREEN:
                    greenCount++;
                    break;
                case PlayerColor.YELLOW:
                    yellowCount++;
                    break;
                case PlayerColor.PURPLE:
                    purpleCount++;
                    break;
            }
        }
        List<Integer> counts = new ArrayList<>(List.of(redCount, blueCount, greenCount, yellowCount, purpleCount));
        Set<PlayerColor> result = new HashSet<>();
        Collections.sort(counts);
        Collections.reverse(counts);
        if(counts.get(0) == 0) {
            return result;
        }

        List<Integer> onlyMax = new ArrayList<>();
        onlyMax.add(counts.get(0));
        for (int i = 1; i < counts.size(); i++) {
            if (Objects.equals(counts.get(i), counts.get(0))) {
                onlyMax.add(counts.get(i));
            }
        }
        for (int i : onlyMax) {
            if (i == redCount) {
                result.add(PlayerColor.RED);
            }if (i == blueCount) {
                result.add(PlayerColor.BLUE);
            } if (i == greenCount) {
                result.add(PlayerColor.GREEN);
            } if (i == yellowCount) {
                result.add(PlayerColor.YELLOW);
            } if (i == purpleCount) {
                result.add(PlayerColor.PURPLE);
            }
        }
    return result;
    }

    /**
     * Connects this area to another area.
     * @param that The other area to connect to.
     * @return The new area after the connection.
     */
    public Area<Z> connectTo(Area<Z> that) {

        int totalOpenConnextion = 0;
        // I know I'm comparing by reference
        if (this == that) {
            totalOpenConnextion = this.openConnections - 2;
            return new Area<>(this.zones, this.occupants, totalOpenConnextion);
        } else {
            totalOpenConnextion = this.openConnections + that.openConnections - 2;
            Set<Z> zones = new HashSet<>();
            zones.addAll(this.zones);
            zones.addAll(that.zones);
            List<PlayerColor> occupants = new ArrayList<>();
            occupants.addAll(this.occupants);
            occupants.addAll(that.occupants);
            return new Area<>(zones, occupants, totalOpenConnextion);
        }
    }

    /**
     * Adds an initial occupant to the area.
     * @param occupant The initial occupant to add.
     * @return The new area with the initial occupant.
     * @throws IllegalArgumentException if the area already has occupants
     */
    public Area<Z> withInitialOccupant(PlayerColor occupant) {
        Preconditions.checkArgument(occupants.isEmpty());
        List<PlayerColor> occupants = new ArrayList<>();
        occupants.add(occupant);
        return new Area<>(zones, occupants, openConnections);
    }

    /**
     * Removes an occupant from the area.
     * @param occupant The occupant to remove.
     * @return The new area without the occupant.
     * @throws IllegalArgumentException if the occupant is not in the area
     */
    public Area<Z> withoutOccupant(PlayerColor occupant) {
        Preconditions.checkArgument(occupants.contains(occupant));
        List<PlayerColor> occupants = new ArrayList<>(this.occupants);
        occupants.remove(occupant);
        return new Area<>(zones, occupants, openConnections);
    }

    /**
     * Removes all occupants from the area.
     * @return The new area without any occupants.
     */
    public Area<Z> withoutOccupants() {
        return new Area<>(zones, new ArrayList<>(), openConnections);
    }

    /**
     * Gets the set of tile IDs in the area.
     * @return The set of tile IDs in the area.
     */
    public Set<Integer> tileIds() {
        Set<Integer> tileIds = new HashSet<>();
        for (Z zone : zones) {
            tileIds.add(zone.tileId());
        }
        return tileIds;
    }

    /**
     * Gets the zone with a special power in the area.
     * @param specialPower The special power to look for.
     * @return The zone with the special power, or null if no such zone exists.
     */
    public Zone zoneWithSpecialPower(Zone.SpecialPower specialPower) {
        for (Zone z : zones) {
            if(z.specialPower() == specialPower) {
                return z;
            }
        }
        return null;
    }
}
