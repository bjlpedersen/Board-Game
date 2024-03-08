package ch.epfl.chacun;

import java.util.*;

public record Area<Z extends Zone>(Set<Z> zones, List<PlayerColor> occupants, int openConnections) {

    public Area {
        Preconditions.checkArgument(openConnections >= 0);
        if (occupants == null ) {
            occupants = new ArrayList<>();
        }
        zones = Set.copyOf(zones);
        occupants = new ArrayList<>(List.copyOf(occupants));
        Collections.sort(occupants);
    }

    public static boolean hasMenhir(Area<Zone.Forest> forest) {
        for (Zone.Forest zone : forest.zones) {
            if (zone.kind() == Zone.Forest.Kind.WITH_MENHIR) {
                return true;
            }
        }
        return false;
    }

    public static int mushroomGroupCount(Area<Zone.Forest> forest) {
        int count = 0;
        for (Zone.Forest zone : forest.zones) {
            if (zone.kind() == Zone.Forest.Kind.WITH_MUSHROOMS) {
                count++;
            }
        }
        return count;
    }

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

    public static int riverSystemFishCount(Area<Zone.Water> riverSystem) {
        int count = 0;
        for (Zone.Water zone : riverSystem.zones) {
            count = count + zone.fishCount();
        }
        return count;
    }

    public static int lakeCount(Area<Zone.Water> riverSystem) {
        int count = 0;
        for (Zone.Water zone: riverSystem.zones) {
            if (zone instanceof Zone.Lake) {
                count++;
            }
        }
        return count;
    }

    public boolean isClosed() {
        return openConnections == 0;
    }

    public boolean isOccupied() {
        if (occupants == null) {
            return false;
        }
        return !occupants.isEmpty();
    }

    public Set<PlayerColor> majorityOccupants() {
        Set<PlayerColor> redPlayers = new HashSet<>();
        int redCount = 0;
        Set<PlayerColor> bluePlayers = new HashSet<>();
        int blueCount = 0;
        Set<PlayerColor> yellowPlayers = new HashSet<>();
        int yellowCount = 0;
        Set<PlayerColor> greenPlayers = new HashSet<>();
        int greenCount = 0;
        Set<PlayerColor> purplePlayers = new HashSet<>();
        int purpleCount = 0;

        for (PlayerColor color : occupants) {
            switch (color) {
                case PlayerColor.RED:
                    redPlayers.add(color);
                    redCount++;
                    break;
                case PlayerColor.BLUE:
                    bluePlayers.add(color);
                    blueCount++;
                    break;
                case PlayerColor.GREEN:
                    greenPlayers.add(color);
                    greenCount++;
                    break;
                case PlayerColor.YELLOW:
                    yellowPlayers.add(color);
                    yellowCount++;
                    break;
                case PlayerColor.PURPLE:
                    purplePlayers.add(color);
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
                onlyMax.add(i);
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

    public Area<Z> withInitialOccupant(PlayerColor occupant) {
        Preconditions.checkArgument(occupants.isEmpty());
        List<PlayerColor> occupants = new ArrayList<>();
        occupants.add(occupant);
        return new Area<>(zones, occupants, openConnections);
    }

    public Area<Z> withoutOccupant(PlayerColor occupant) {
        Preconditions.checkArgument(occupants.contains(occupant));
        List<PlayerColor> occupants = new ArrayList<>(this.occupants);
        occupants.remove(occupant);
        return new Area<>(zones, occupants, openConnections);
    }

    public Area<Z> withoutOccupants() {
        return new Area<>(zones, new ArrayList<>(), openConnections);
    }

    public Set<Integer> tileIds() {
        Set<Integer> tileIds = new HashSet<>();
        for (Z zone : zones) {
            tileIds.add(zone.id());
        }
        return tileIds;
    }

    public Zone zoneWithSpecialPower(Zone.SpecialPower specialPower) {
        for (Zone z : zones) {
            if(z.specialPower() == specialPower) {
                return z;
            }
        }
        return null;
    }
}
