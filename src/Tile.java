public class Tile {
    public int tilesetRow;
    public int tilesetCol;
    public int mapRow;
    public int mapCol;

    public Tile(int _mapRow, int _mapCol, int _tilesetRow, int _tilesetCol) {
        mapRow = _mapRow;
        mapCol = _mapCol;
        tilesetRow = _tilesetRow;
        tilesetCol = _tilesetCol;
    }
}
