package ch.epfl.chacun;

import java.security.InvalidAlgorithmParameterException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * This class provides static methods for encoding and decoding game actions.
 *
 * @author Bjork Pedersen (376143)
 */
public class ActionEncoder {

    /**
     * Private constructor to prevent instantiation of this utility class.
     *
     * @throws AssertionError always
     */
    private ActionEncoder() {}

    /**
     * Encodes a tile placement action into a StateAction object.
     *
     * @param state The current game state.
     * @param placedTile The tile that was placed.
     * @return A StateAction object representing the action.
     * @throws IllegalArgumentException If the position index is invalid.
     */
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

    /**
     * Encodes a new occupant action into a StateAction object.
     *
     * @param state The current game state.
     * @param occ The new occupant.
     * @return A StateAction object representing the action.
     */
    public static StateAction withNewOccupant(GameState state, Occupant occ) {
        if (occ == null) {
            return new StateAction(state, "7");
        }
        int action = 0;
        if (occ.kind() != Occupant.Kind.PAWN) {
            action |= 1 << 4; // Set the most significant bit if the occupant is not a pawn
        }
        action |= (occ.zoneId() % 10) & 0b1111; // Set the four least significant bits to the zoneId
        return new StateAction(state.withNewOccupant(occ), Base32.encodeBits5(action));
    }

    /**
     * Encodes an occupant removal action into a StateAction object.
     *
     * @param state The current game state.
     * @param occ The occupant to be removed.
     * @return A StateAction object representing the action.
     */
    public static StateAction withOccupantRemoved(GameState state, Occupant occ) {
        if (occ == null) {
            return new StateAction(state , "7");
        }
        StringBuilder action = new StringBuilder();
        List<Occupant> boardOccupants = state.board().occupants().stream().toList();
        boardOccupants.sort(Comparator.comparingInt(Occupant::zoneId));
        int occIndex = boardOccupants.indexOf(occ);
        action.append(Integer.toBinaryString(occIndex));
        return new StateAction(state.withOccupantRemoved(occ), Base32.encodeBits5(Integer.parseInt(action.toString())));
    }

    /**
     * Decodes an action string and applies it to the game state.
     *
     * @param state The current game state.
     * @param action The action string to decode and apply.
     * @return A StateAction object representing the new game state and the action.
     * @throws InvalidActionMessageException If the action string is invalid.
     */
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

    /**
     * Checks if an action string is invalid for the current game state.
     *
     * @param state The current game state.
     * @param action The action string to check.
     * @throws InvalidActionMessageException If the action string is invalid.
     */
    private static void isInvalidAction(GameState state, String action) throws InvalidActionMessageException {
        // Decode the action from Base32 to an integer
        int newAction = Base32.decode(action);
        // Convert the integer to a binary string
        action = Integer.toBinaryString(newAction);

        // Check if the binary string is a valid Base32 string
        if (!Base32.isValid(action)) throw new InvalidActionMessageException("Character in action does not fit Base32");

        // Check the next action based on the current game state
        switch (state.nextAction()) {
            case PLACE_TILE -> {
                // Check if the binary string has the correct length for a PLACE_TILE action
                if (action.length() != 8) throw new InvalidActionMessageException("The message does not have the correct length");
                // Check if the fringe position is within the valid range
                if (Integer.parseInt(action.substring(0, 8), 10) > 190 ||
                        Integer.parseInt(action.substring(0, 8), 2) < 0) throw new InvalidActionMessageException("The fringe position is out of bounds");
            }
            case OCCUPY_TILE -> {
                // Check if the binary string has the correct length for an OCCUPY_TILE action
                if (action.length() != 5) throw new InvalidActionMessageException("The message does not have the correct length");
                // Check if the zone id is within the valid range
                if (Integer.parseInt(action.substring(1, 5)) > 9 ||
                        Integer.parseInt(action.substring(1, 5)) < 0 &&
                                !action.equals("11111")) throw new InvalidActionMessageException("The zone id is out of bounds");
            }
            case RETAKE_PAWN -> {
                // Check if the binary string has the correct length for a RETAKE_PAWN action
                if (action.length() != 5) throw new InvalidActionMessageException("The message does not have the correct length");
                // Check if the occupant index is within the valid range
                if (Integer.parseInt(action) > 24 ||
                        Integer.parseInt(action) < 0 &&
                                !action.equals("11111")) throw new InvalidActionMessageException("the occupant index is out of bounds");
            }
            default -> throw new InvalidActionMessageException("The next action is invalid");
        }
    }


    /**
     * This class represents a pair consisting of a game state and an action.
     */
    public static class StateAction {
        private final GameState gameState;
        private final String encodedAction;

        /**
         * Constructs a new StateAction object.
         *
         * @param gameState The game state.
         * @param encodedAction The encoded action.
         */
        public StateAction(GameState gameState, String encodedAction) {
            this.gameState = gameState;
            this.encodedAction = encodedAction;
        }

        /**
         * Returns the game state.
         *
         * @return The game state.
         */
        public GameState getGameState() {
            return gameState;
        }

        /**
         * Returns the encoded action.
         *
         * @return The encoded action.
         */
        public String getEncodedAction() {
            return encodedAction;
        }
    }

    /**
     * This exception is thrown when an action message is invalid.
     */
    public static class InvalidActionMessageException extends Exception {

        /**
         * Constructs a new InvalidActionMessageException with a specific message.
         *
         * @param message The exception message.
         */
        public InvalidActionMessageException(String message) {
            super(message);
        }

        /**
         * Constructs a new InvalidActionMessageException with no message.
         */
        public InvalidActionMessageException() {
            super();
        }
    }

}
