import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.Collectors;

public class MainController {
    private Scene scene;
    private Connection connection;

    @FXML private TableView<ClassRow> classesTable;
    @FXML private TableColumn<ClassRow, Integer> classesIdColumn;
    @FXML private TableColumn<ClassRow, String> classesNameColumn;
    @FXML private TableColumn<ClassRow, Integer> classesGamesColumn;
    @FXML private TableColumn<ClassRow, Double> classesWinrateColumn;
    @FXML private TableColumn<ClassRow, Void> classesDeleteColumn;
    private ObservableList<ClassRow> classesItems = FXCollections.observableArrayList();
    @FXML private TextField classesNameAddField;

    @FXML private TableView<DeckRow> decksTable;
    @FXML private TableColumn<DeckRow, Integer> decksIdColumn;
    @FXML private TableColumn<DeckRow, String> decksNameColumn;
    @FXML private TableColumn<DeckRow, String> decksAuthorColumn;
    @FXML private TableColumn<DeckRow, Integer> decksClassIdColumn;
    @FXML private TableColumn<DeckRow, Integer> decksCostColumn;
    @FXML private TableColumn<DeckRow, Integer> decksGamesTotalColumn;
    @FXML private TableColumn<DeckRow, Integer> decksGamesWonColumn;
    @FXML private TableColumn<DeckRow, Double> decksWinrateColumn;
    @FXML private TableColumn<DeckRow, Void> decksDeleteColumn;
    private ObservableList<DeckRow> decksItems = FXCollections.observableArrayList();
    @FXML private TextField decksNameAddField;
    @FXML private TextField decksAuthorAddField;
    @FXML private TextField decksClassIdAddField;
    @FXML private TextField decksCostAddField;
    @FXML private TextField decksGamesTotalAddField;
    @FXML private TextField decksGamesWonAddField;
    @FXML private TextField decksNameSearchField;

    @FXML private Label errorLabel;

    public MainController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/MainScene.fxml"));
        fxmlLoader.setController(this);
        try {
            Parent parent = fxmlLoader.load();
            scene = new Scene(parent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showScene(Stage stage) {
        stage.hide();
        stage.setTitle("HSReplay on minimals");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    @FXML
    public void initialize() {
        classesIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        classesNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        classesNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        classesNameColumn.setOnEditCommit(e -> {
            ClassRow row = e.getRowValue();
            try {
                updateClass(row.getId(), e.getNewValue());
                row.setName(e.getNewValue());
            } catch (SQLException sqlEx) {
                errorLabel.setText(sqlEx.getMessage());
            }
        });
        classesGamesColumn.setCellValueFactory(new PropertyValueFactory<>("games"));
        classesWinrateColumn.setCellValueFactory(new PropertyValueFactory<>("winrate"));
        classesDeleteColumn.setCellFactory(tableColumn -> new TableCell<>() {
            private final Button button = new Button("X");

            {
                button.setOnAction(evt -> {
                    ClassRow row = getTableRow().getItem();
                    if (row.getId() > 0) {
                        try {
                            deleteClass(row.getId());
                            classesItems.remove(row);
                        } catch (SQLException sqlEx) {
                            errorLabel.setText(sqlEx.getMessage());
                        }
                    }
                    else {
                        errorLabel.setText("Загрузите данные из базы, чтобы получить оттуда id этой записи.");
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : button);
            }
        });
        classesTable.setItems(classesItems);



        decksIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        decksNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        decksNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        decksNameColumn.setOnEditCommit(e -> {
            DeckRow row = e.getRowValue();
            String name = e.getNewValue();
            DeckRow newRow = new DeckRow(row, DeckRow.EditableFields.NAME, name);
            try {
                updateDeck(newRow);
                row.setName(name);
            } catch (SQLException sqlEx) {
                errorLabel.setText(sqlEx.getMessage());
            }
        });

        decksAuthorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        decksAuthorColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        decksAuthorColumn.setOnEditCommit(e -> {
            DeckRow row = e.getRowValue();
            String author = e.getNewValue();
            DeckRow newRow = new DeckRow(row, DeckRow.EditableFields.AUTHOR, author);
            try {
                updateDeck(newRow);
                row.setAuthor(author);
            } catch (SQLException sqlEx) {
                errorLabel.setText(sqlEx.getMessage());
            }
        });

        decksClassIdColumn.setCellValueFactory(new PropertyValueFactory<>("classId"));
        decksClassIdColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        decksClassIdColumn.setOnEditCommit(e -> {
            DeckRow row = e.getRowValue();
            int classId = e.getNewValue();
            DeckRow newRow = new DeckRow(row, DeckRow.EditableFields.CLASS_ID, classId);
            try {
                updateDeck(newRow);
                row.setClassId(classId);
            } catch (SQLException sqlEx) {
                errorLabel.setText(sqlEx.getMessage());
            }
        });

        decksCostColumn.setCellValueFactory(new PropertyValueFactory<>("cost"));
        decksCostColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        decksCostColumn.setOnEditCommit(e -> {
            DeckRow row = e.getRowValue();
            int cost = e.getNewValue();
            DeckRow newRow = new DeckRow(row, DeckRow.EditableFields.COST, cost);
            try {
                updateDeck(newRow);
                row.setCost(cost);
            } catch (SQLException sqlEx) {
                errorLabel.setText(sqlEx.getMessage());
            }
        });

        decksGamesTotalColumn.setCellValueFactory(new PropertyValueFactory<>("gamesTotal"));
        decksGamesTotalColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        decksGamesTotalColumn.setOnEditCommit(e -> {
            DeckRow row = e.getRowValue();
            int gamesTotal = e.getNewValue();
            DeckRow newRow = new DeckRow(row, DeckRow.EditableFields.GAMES_TOTAL, gamesTotal);
            try {
                updateDeck(newRow);
                row.setGamesTotal(gamesTotal);
            } catch (SQLException sqlEx) {
                errorLabel.setText(sqlEx.getMessage());
            }
        });

        decksGamesWonColumn.setCellValueFactory(new PropertyValueFactory<>("gamesWon"));
        decksGamesWonColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        decksGamesWonColumn.setOnEditCommit(e -> {
            DeckRow row = e.getRowValue();
            int gamesWon = e.getNewValue();
            DeckRow newRow = new DeckRow(row, DeckRow.EditableFields.GAMES_WON, gamesWon);
            try {
                updateDeck(newRow);
                row.setGamesWon(gamesWon);
            } catch (SQLException sqlEx) {
                errorLabel.setText(sqlEx.getMessage());
            }
        });

        decksWinrateColumn.setCellValueFactory(new PropertyValueFactory<>("winrate"));
        decksDeleteColumn.setCellFactory(tableColumn -> new TableCell<>() {
            private final Button button = new Button("X");

            {
                button.setOnAction(evt -> {
                    DeckRow row = getTableRow().getItem();
                    if (row.getId() > 0) {
                        try {
                            deleteDeck(row.getId());
                            decksItems.remove(row);
                        } catch (SQLException sqlEx) {
                            errorLabel.setText(sqlEx.getMessage());
                        }
                    }
                    else {
                        errorLabel.setText("Загрузите данные из базы, чтобы получить оттуда id этой записи.");
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : button);
            }
        });
        decksTable.setItems(decksItems);
    }



    private void showDecks() {
        try {
            CallableStatement statement = connection.prepareCall("call get_table_decks");
            ResultSet rs = statement.executeQuery();
            decksItems.clear();
            while (rs.next()) {
                decksItems.add(new DeckRow(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getInt(4),
                        rs.getInt(5),
                        rs.getInt(6),
                        rs.getInt(7),
                        rs.getDouble(8)));
            }
        } catch (SQLException sqlEx) {
            errorLabel.setText(sqlEx.getMessage());
        }
    }

    @FXML
    private void clearDecks() {
        try {
            CallableStatement statement = connection.prepareCall("call clear_table_decks");
            statement.executeQuery();
            decksItems.clear();
        } catch (SQLException sqlEx) {
            errorLabel.setText(sqlEx.getMessage());
        }
    }

    @FXML
    private void insertDeck() {
        try {
            String name = decksNameAddField.getText();
            String author = decksAuthorAddField.getText();
            int classId = Integer.parseInt(decksClassIdAddField.getText());
            int cost = Integer.parseInt(decksCostAddField.getText());
            int gamesTotal = Integer.parseInt(decksGamesTotalAddField.getText());
            int gamesWon = Integer.parseInt(decksGamesWonAddField.getText());
            CallableStatement statement = connection.prepareCall("{call insert_deck(?, ?, ?, ?, ?, ?)}");
            statement.setString(1, name);
            statement.setString(2, author);
            statement.setInt(3, classId);
            statement.setInt(4, cost);
            statement.setInt(5, gamesTotal);
            statement.setInt(6, gamesWon);
            statement.executeQuery();
            decksItems.add(new DeckRow(
                    0,
                    name,
                    author,
                    classId,
                    cost,
                    gamesTotal,
                    gamesWon,
                    (double)gamesWon / gamesTotal));
            errorLabel.setText("Id автоматически инкрементируется в базе данных и не возвращается в приложение. " +
                    "Чтобы увидеть id, перезагрузите данные.");
        } catch (SQLException sqlEx) {
            errorLabel.setText(sqlEx.getMessage());
        } catch (NumberFormatException numEx) {
            errorLabel.setText("Ошибка парсинга integer.");
        }
    }

    private void updateDeck(DeckRow row) throws SQLException {
        if (row.getId() > 0) {
            CallableStatement statement = connection.prepareCall("{call update_deck(?, ?, ?, ?, ?, ?, ?)}");
            statement.setInt(1, row.getId());
            statement.setString(2, row.getName());
            statement.setString(3, row.getAuthor());
            statement.setInt(4, row.getClassId());
            statement.setInt(5, row.getCost());
            statement.setInt(6, row.getGamesTotal());
            statement.setInt(7, row.getGamesWon());
            statement.executeQuery();
        }
        else {
            errorLabel.setText("Загрузите данные из базы, чтобы получить оттуда id этой записи.");
        }
    }

    private void deleteDeck(int id) throws SQLException {
        CallableStatement statement = connection.prepareCall("{call delete_deck(?)}");
        statement.setInt(1, id);
        statement.executeQuery();
    }

    @FXML void getDecksByName() {
        try {
            CallableStatement statement = connection.prepareCall("{call select_decks_by_name(?)}");
            statement.setString(1, decksNameSearchField.getText());
            ResultSet rs = statement.executeQuery();
            decksItems.clear();
            while (rs.next()) {
                decksItems.add(new DeckRow(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getInt(4),
                        rs.getInt(5),
                        rs.getInt(6),
                        rs.getInt(7),
                        rs.getDouble(8)));
            }
        } catch (SQLException sqlEx) {
            errorLabel.setText(sqlEx.getMessage());
        }
    }

    @FXML void deleteDecksByName() {
        try {
            String name = decksNameSearchField.getText();
            CallableStatement statement = connection.prepareCall("{call delete_decks_by_name(?)}");
            statement.setString(1, name);
            statement.executeQuery();
            Platform.runLater(() -> decksItems.setAll(decksItems
                    .stream()
                    .filter(i -> !i.getName().equals(name))
                    .collect(Collectors.toList())));
        } catch (SQLException sqlEx) {
            errorLabel.setText(sqlEx.getMessage());
        }
    }


    private void showClasses() {
        try {
            CallableStatement statement = connection.prepareCall("call get_table_classes");
            ResultSet rs = statement.executeQuery();
            classesItems.clear();
            while (rs.next()) {
                classesItems.add(new ClassRow(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getInt(3),
                        rs.getDouble(4)));
            }
        } catch (SQLException sqlEx) {
            errorLabel.setText(sqlEx.getMessage());
        }
    }

    @FXML
    private void clearClasses() {
        try {
            CallableStatement statement = connection.prepareCall("call clear_table_classes");
            statement.executeQuery();
            classesItems.clear();
        } catch (SQLException sqlEx) {
            errorLabel.setText(sqlEx.getMessage());
        }
    }

    @FXML
    private void insertClass() {
        try {
            String name = classesNameAddField.getText();
            CallableStatement statement = connection.prepareCall("{call insert_class(?)}");
            statement.setString(1, name);
            statement.executeQuery();
            classesItems.add(new ClassRow(0, name, 0, 0));
            errorLabel.setText("Id автоматически инкрементируется в базе данных и не возвращается в приложение. " +
                    "Чтобы увидеть id, перезагрузите данные.");
        } catch (SQLException sqlEx) {
            errorLabel.setText(sqlEx.getMessage());
        }
    }

    private void updateClass(int id, String name) throws SQLException {
        if (id > 0) {
            CallableStatement statement = connection.prepareCall("{call update_class(?, ?)}");
            statement.setInt(1, id);
            statement.setString(2, name);
            statement.executeQuery();
        }
        else {
            errorLabel.setText("Загрузите данные из базы, чтобы получить оттуда id этой записи.");
        }
    }

    private void deleteClass(int id) throws SQLException {
        CallableStatement statement = connection.prepareCall("{call delete_class(?)}");
        statement.setInt(1, id);
        statement.executeQuery();
    }


    @FXML
    private void showAll() {
        showDecks();
        showClasses();
    }

    @FXML
    private void hideErrorMessage() {
        errorLabel.setText("");
    }
}