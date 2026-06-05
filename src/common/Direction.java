package common;

/**
 * ORTAK DOSYA — Robot yönlerini temsil eden enum.
 * dx/dy vektörleri dahil. View (robot çizimi), Model (hareket), Controller hepsi kullanır.
 */
public enum Direction {

    NORTH("Kuzey (↑)",  0, -1),
    SOUTH("Güney (↓)",  0,  1),
    EAST ("Doğu (→)",   1,  0),
    WEST ("Batı (←)",  -1,  0);

    private final String displayName;
    private final int dx;   // x değişimi
    private final int dy;   // y değişimi

    Direction(String displayName, int dx, int dy) {
        this.displayName = displayName;
        this.dx = dx;
        this.dy = dy;
    }

    public String getDisplayName() { return displayName; }
    public int    getDx()          { return dx; }
    public int    getDy()          { return dy; }

    /** 90 derece sola dön */
    public Direction turnLeft() {
        switch (this) {
            case NORTH: return WEST;
            case WEST:  return SOUTH;
            case SOUTH: return EAST;
            case EAST:  return NORTH;
            default:    return this;
        }
    }

    /** 90 derece sağa dön */
    public Direction turnRight() {
        switch (this) {
            case NORTH: return EAST;
            case EAST:  return SOUTH;
            case SOUTH: return WEST;
            case WEST:  return NORTH;
            default:    return this;
        }
    }

    /** Tam ters yön */
    public Direction opposite() {
        switch (this) {
            case NORTH: return SOUTH;
            case SOUTH: return NORTH;
            case EAST:  return WEST;
            case WEST:  return EAST;
            default:    return this;
        }
    }

    /** String'den Direction bul, bulamazsa EAST döner */
    public static Direction fromString(String s) {
        for (Direction d : values()) {
            if (d.name().equalsIgnoreCase(s)) return d;
        }
        return EAST;
    }
}
