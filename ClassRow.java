import javafx.beans.property.*;

public class ClassRow {
    private final IntegerProperty id;
    private final StringProperty name;
    private final IntegerProperty games;
    private final DoubleProperty winrate;

    public ClassRow(int id, String name, int games, double winrate) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.games = new SimpleIntegerProperty(games);
        this.winrate = new SimpleDoubleProperty(winrate);;
    }

    public int getId() { return id.get(); }
    public String getName() { return name.get(); }

    public void setName(String name) { this.name.setValue(name); }

    public IntegerProperty idProperty() { return id; }
    public StringProperty nameProperty() { return name; }
    public IntegerProperty gamesProperty() { return games; }
    public DoubleProperty winrateProperty() { return winrate; }
}
