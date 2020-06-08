import javafx.beans.property.*;

public class DeckRow {

    public enum EditableFields {
        NAME,
        AUTHOR,
        CLASS_ID,
        COST,
        GAMES_TOTAL,
        GAMES_WON
    }

    private final IntegerProperty id;
    private final StringProperty name;
    private final StringProperty author;
    private final IntegerProperty classId;
    private final IntegerProperty cost;
    private final IntegerProperty gamesTotal;
    private final IntegerProperty gamesWon;
    private final DoubleProperty winrate;

    public DeckRow(
            int id,
            String name,
            String author,
            int classId,
            int cost,
            int gamesTotal,
            int gamesWon,
            double winrate) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.author = new SimpleStringProperty(author);
        this.classId = new SimpleIntegerProperty(classId);
        this.cost = new SimpleIntegerProperty(cost);
        this.gamesTotal = new SimpleIntegerProperty(gamesTotal);
        this.gamesWon = new SimpleIntegerProperty(gamesWon);
        this.winrate = new SimpleDoubleProperty(winrate);
    }

    public DeckRow(DeckRow row, EditableFields fieldName, String fieldValue) {
        String name = row.getName();
        String author = row.getAuthor();
        switch (fieldName) {
            case NAME:
                name = fieldValue;
                break;
            case AUTHOR:
                author = fieldValue;
                break;
        }
        id = row.idProperty();
        this.name = new SimpleStringProperty(name);
        this.author = new SimpleStringProperty(author);
        classId = row.classIdProperty();
        cost = row.costProperty();
        gamesTotal = row.gamesTotalProperty();
        gamesWon = row.gamesWonProperty();
        winrate = row.winrateProperty();
    }

    public DeckRow(DeckRow row, EditableFields fieldName, int fieldValue) {
        int classId = row.getClassId();
        int cost = row.getCost();
        int gamesTotal = row.getGamesTotal();
        int gamesWon = row.getGamesWon();
        switch (fieldName) {
            case CLASS_ID:
                classId = fieldValue;
                break;
            case COST:
                cost = fieldValue;
                break;
            case GAMES_TOTAL:
                gamesTotal = fieldValue;
                break;
            case GAMES_WON:
                gamesWon = fieldValue;
                break;
        }
        id = row.idProperty();
        name = row.nameProperty();
        author = row.authorProperty();
        this.classId = new SimpleIntegerProperty(classId);
        this.cost = new SimpleIntegerProperty(cost);
        this.gamesTotal = new SimpleIntegerProperty(gamesTotal);
        this.gamesWon = new SimpleIntegerProperty(gamesWon);
        winrate = row.winrateProperty();
    }

    public int getId() { return id.get(); }
    public String getName() { return name.get(); }
    public String getAuthor() { return author.get(); }
    public int getClassId() { return classId.get(); }
    public int getCost() { return cost.get(); }
    public int getGamesTotal() { return gamesTotal.get(); }
    public int getGamesWon() { return gamesWon.get(); }

    public void setName(String name) { this.name.setValue(name); }
    public void setAuthor(String author) { this.author.setValue(author); }
    public void setClassId(int classId) { this.classId.setValue(classId); }
    public void setCost(int cost) { this.cost.setValue(cost); }
    public void setGamesTotal(int gamesTotal) { this.gamesTotal.setValue(gamesTotal); }
    public void setGamesWon(int gamesWon) { this.gamesWon.setValue(gamesWon); }

    public IntegerProperty idProperty() { return id; }
    public StringProperty nameProperty() { return name; }
    public StringProperty authorProperty() { return author; }
    public IntegerProperty classIdProperty() { return classId; }
    public IntegerProperty costProperty() { return cost; }
    public IntegerProperty gamesTotalProperty() { return gamesTotal; }
    public IntegerProperty gamesWonProperty() { return gamesWon; }
    public DoubleProperty winrateProperty() { return winrate; }
}
