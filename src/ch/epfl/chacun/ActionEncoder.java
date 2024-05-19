package ch.epfl.chacun;

import java.security.InvalidAlgorithmParameterException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class ActionEncoder {

    private ActionEncoder() {}

    public static StateAction withPlacedTile(GameState state, PlacedTile placedTile) {
        List<Pos> insertionPositions = new ArrayList<>(state.board().insertionPositions().stream().toList());
        insertionPositions.sort(Comparator.comparingInt(Pos::x).thenComparingInt(Pos::y));
        int indexOfPos = insertionPositions.indexOf(placedTile.pos());
        if (indexOfPos < 0 || indexOfPos > 255) {
            throw new IllegalArgumentException("Invalid position index: " + indexOfPos);
        }

        int rotationBits;
        switch (placedTile.rotation()) {
            case NONE -> rotationBits = 0b00;
            case RIGHT -> rotationBits = 0b01;
            case HALF_TURN -> rotationBits = 0b10;
            case LEFT -> rotationBits = 0b11;
            default -> throw new IllegalArgumentException("Invalid rotation");
        }

        int action = (indexOfPos << 2) | rotationBits;
        return new StateAction(state.withPlacedTile(placedTile), Base32.encodeBits10(action));
    }

    public static StateAction withNewOccupant(GameState state, Occupant occ) {
        if (occ == null) {
            return new StateAction(state, "11111");
        }
        int action = 0;
        if (occ.kind() != Occupant.Kind.PAWN) {
            action |= 1 << 4; // Set the most significant bit if the occupant is not a pawn
        }
        action |= (occ.zoneId() % 10) & 0b1111; // Set the four least significant bits to the zoneId
        return new StateAction(state.withNewOccupant(occ), Base32.encodeBits5(action));
    }

    public static StateAction withOccupantRemoved(GameState state, Occupant occ) {
        if (occ == null) {
            return new StateAction(state , "11111");
        }
        StringBuilder action = new StringBuilder();
        List<Occupant> boardOccupants = state.board().occupants().stream().toList();
        boardOccupants.sort(Comparator.comparingInt(Occupant::zoneId));
        int occIndex = boardOccupants.indexOf(occ);
        action.append(Integer.toBinaryString(occIndex));
        return new StateAction(state.withOccupantRemoved(occ), Base32.encodeBits5(Integer.parseInt(action.toString())));
    }

    public static StateAction decodeAndApply(GameState state, String action) {
        try {
            isInvalidAction(state, action);
        } catch (InvalidActionMessageException e) {
            int actionDecoded = Base32.decode(action);
            final String formatOccupyAndRetake = String.format("%05d",
                    Integer.parseInt(Integer.toBinaryString(actionDecoded)));

            switch (state.nextAction()) {
                case PLACE_TILE -> {
                    String actionDecodedBinary = Integer.toBinaryString(actionDecoded);
                    String actionDecodedString = String.format("%10s", actionDecodedBinary).replace(' ', '0');
                    List<Pos> insertionPositions = new ArrayList<>(state.board().insertionPositions().stream().toList());
                    insertionPositions.sort(Comparator.comparingInt(Pos::x).thenComparingInt(Pos::y));
                    Pos pos = insertionPositions.get(Integer.parseInt(actionDecodedString.substring(0, 8), 2));
                    Rotation rot = Rotation.ALL.get(Integer.parseInt(actionDecodedString.substring(8, 10), 2));
                    PlacedTile placedTile = new PlacedTile(state.tileToPlace(), state.currentPlayer(), rot, pos, null);
                    return new StateAction(state.withPlacedTile(placedTile), action);
                }
                case OCCUPY_TILE -> {
                    String actionDecodedString = formatOccupyAndRetake;
                    if (actionDecodedString.equals("11111")) return new StateAction(state, action);
                    int zoneId = Integer.parseInt(actionDecodedString.substring(1, 5), 2);
                    zoneId = zoneId + state.board().lastPlacedTile().id() * 10;
                    if (actionDecodedString.charAt(0) == 1) {
                        Occupant occ = new Occupant(Occupant.Kind.HUT, zoneId);
                        return new StateAction(state.withNewOccupant(occ), action);
                    }
                    Occupant occ = new Occupant(Occupant.Kind.PAWN, zoneId);
                    return new StateAction(state.withNewOccupant(occ), action);
                }
                case RETAKE_PAWN -> {
                    String actionDecodedString = formatOccupyAndRetake;
                    if (actionDecodedString.equals("11111")) return new StateAction(state, action);
                    int pawnIndex = Integer.parseInt(actionDecodedString, 2);
                    List<Occupant> allOccupants = new ArrayList<>(state.board().occupants().stream().toList());
                    allOccupants.sort(Comparator.comparingInt(Occupant::zoneId));
                    Occupant occ = allOccupants.get(pawnIndex);
                    return new StateAction(state.withOccupantRemoved(occ), action);
                }
            }
        }
        return null;
    }

    private static void isInvalidAction(GameState state, String action) throws InvalidActionMessageException {
        int newAction = Base32.decode(action);
        action = Integer.toBinaryString(newAction);
        if (!Base32.isValid(action)) throw new InvalidActionMessageException("Character in action does not fit Base32");
        switch (state.nextAction()) {
            case PLACE_TILE -> {
                if (action.length() != 8) throw new InvalidActionMessageException("The message does not have the correct length");
                if (Integer.parseInt(action.substring(0, 8), 10) > 190 ||
                Integer.parseInt(action.substring(0, 8), 2) < 0) throw new InvalidActionMessageException("The fringe position is out of bounds");
            }
            case OCCUPY_TILE -> {
                if (action.length() != 5) throw new InvalidActionMessageException("The message does not have the correct length");
                if (Integer.parseInt(action.substring(1, 5)) > 9 ||
                        Integer.parseInt(action.substring(1, 5)) < 0 &&
                        !action.equals("11111")) throw new InvalidActionMessageException("The zone id is out of bounds");
            }
            case RETAKE_PAWN -> {
                if (action.length() != 5) throw new InvalidActionMessageException("The message does not have the correct length");
                if (Integer.parseInt(action) > 24 ||
                        Integer.parseInt(action) < 0 &&
                        !action.equals("11111")) throw new InvalidActionMessageException("the occupant index is out of bounds");
            }
            default -> throw new InvalidActionMessageException("The next action is invalid");
        }
    }


    public static class StateAction {
        private final GameState gameState;
        private final String encodedAction;

        public StateAction(GameState gameState, String encodedAction) {
            this.gameState = gameState;
            this.encodedAction = encodedAction;
        }

        public GameState getGameState() {
            return gameState;
        }

        public String getEncodedAction() {
            return encodedAction;
        }
    }

    public static class InvalidActionMessageException extends Exception {
        public InvalidActionMessageException(String message) {
            super(message);
        }

        public InvalidActionMessageException() {
            super();
        }
    }

}
