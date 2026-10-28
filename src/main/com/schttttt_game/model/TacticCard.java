package main.com.schttttt_game.model;

public class TacticCard implements Card {
    private final TacticType type;

    public TacticCard(TacticType type) {
        this.type = type;
    }

    public TacticType getType() { return type; }

    public boolean isRuse() {
        return type == TacticType.RECRUITER
            || type == TacticType.STRATEGIST
            || type == TacticType.BANSHEE
            || type == TacticType.TRAITOR;
    }

    public String getShortName() {
    switch (type) {
        case JOKER: return "J?";
        case SPY: return "S7";
        case SHIELD_BEARER: return "B?";
        case BLIND_MAN_BLUFF: return "BL"; // Bluff
        case MUD_FIGHT: return "MU"; // Mud
        case RECRUITER: return "RE";
        case STRATEGIST: return "ST";
        case BANSHEE: return "BA";
        case TRAITOR: return "TR";
        default: return "??";
    }
}

    @Override
    public int getValue() {
        switch (type) {
            case JOKER:
                return 0; // We have to compute better value at the moment when claim possible (limit zone full)
                // Else no value ?
            case SPY:
                return 7; 
            case SHIELD_BEARER:
                return 0; // We have to compute the better value in 1 / 2 / 3 when claim possible.
            default:
                return 0; // No value for other Tactic Cards
        }
    }

    @Override
    public String getColor() {
        switch (type) {
            case JOKER:
            case SPY:
            case SHIELD_BEARER:
                return "Grey";  // Have to be a new Tactic Color => Changed at claim for the best color possible.
            default:
                return null;
        }
    }

    @Override
    public String toString() {
        return "[" + type + "]";
    }
    
}