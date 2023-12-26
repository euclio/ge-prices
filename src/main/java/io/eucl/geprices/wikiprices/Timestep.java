package io.eucl.geprices.wikiprices;

public enum Timestep {
    FIVE_MINUTES,
    ONE_HOUR,
    SIX_HOURS,
    TWENTY_FOUR_HOURS;

    @Override
    public String toString() {
        switch (this) {
            case FIVE_MINUTES:
                return "5m";
            case ONE_HOUR:
                return "1h";
            case SIX_HOURS:
                return "6h";
            case TWENTY_FOUR_HOURS:
                return "24h";
            default:
                throw new IllegalArgumentException("unhandled timestep value: " + this);
        }
    }
}
