private VBox createPalette() {
    VBox palette = new VBox(10);
    palette.setSpacing(10);

    // Keine feste Breite mehr, sondern nur Pref/Min/Max mit etwas Spielraum
    palette.setPrefWidth(180);
    palette.setMinWidth(160);
    palette.setMaxWidth(220);

    // Padding sorgt für Abstand „innen” zur VBox-Border
    palette.setStyle(
            "-fx-padding: 10;" +
            "-fx-background-color: #2b2b2b;" +
            "-fx-background-radius: 12;" +
            "-fx-border-radius: 12;" +
            "-fx-border-color: #444;"
    );

    for (String assetKey : assets.getKeys()) {
        ImageView preview = new ImageView(assets.get(assetKey));

        // 1:1-Format: gleiche Fit-Werte + preserveRatio
        preview.setFitWidth(64);
        preview.setFitHeight(64);
        preview.setPreserveRatio(true);

        StackPane tileButton = new StackPane(preview);

        tileButton.setStyle(
                "-fx-border-color: #666;" +
                "-fx-padding: 6;" +
                "-fx-background-color: #1e1e1e;"
        );

        tileButton.setOnMouseEntered(e ->
                tileButton.setStyle(
                        "-fx-border-color: white;" +
                        "-fx-padding: 6;" +
                        "-fx-background-color: #2e2e2e;"
                )
        );

        tileButton.setOnMouseExited(e ->
                tileButton.setStyle(
                        "-fx-border-color: #666;" +
                        "-fx-padding: 6;" +
                        "-fx-background-color: #1e1e1e;"
                )
        );

        tileButton.setOnMouseClicked(e -> state.setEditorSelection(assetKey));

        palette.getChildren().add(tileButton);
    }

    return palette;
}

private void paint(ImageView view) {
    if (view == null) return;
    if (view == lastPainted) return;

    Image img = assets.get(state.getEditorSelection().getAsset());
    view.setImage(img);

    lastPainted = view;
}

public void showLevelEditor() {

    // =========================
    // GRID
    // =========================
    editorField = new StackPane[columns][rows];

    editor = new GridPane();
    editor.setHgap(0);
    editor.setVgap(0);
    editor.setPadding(new Insets(0)); // Padding hier nicht nötig, wir nutzen外层 Container

    // GridPane soll nicht eigenständig wachsen, außer über unsere Pref-Size
    editor.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
    editor.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

    editor.getColumnConstraints().clear();
    editor.getRowConstraints().clear();

    for (int col = 0; col < columns; col++) {
        ColumnConstraints cc = new ColumnConstraints();
        cc.setHgrow(Priority.NEVER); // Wichtiger: kein ALWAYS, wir steuern Größe selbst
        cc.setFillWidth(true);
        editor.getColumnConstraints().add(cc);
    }

    for (int row = 0; row < rows; row++) {
        RowConstraints rc = new RowConstraints();
        rc.setVgrow(Priority.NEVER);
        rc.setFillHeight(true);
        editor.getRowConstraints().add(rc);
    }

    for (int row = 0; row < rows; row++) {
        for (int col = 0; col < columns; col++) {

            StackPane cell = new StackPane();

            // 1:1-Format wird in updateGridScale() gesetzt
            cell.setMinSize(0, 0);
            cell.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

            cell.setStyle("-fx-background-color: white; -fx-border-color: black;");

            ImageView view = new ImageView();
            view.setPreserveRatio(true); // Wichtig für saubere 1:1-Zellen
            view.setSmooth(false);

            // Bind an die Cell-Größe, aber Cell wird quadratisch gehalten
            view.fitWidthProperty().bind(cell.widthProperty());
            view.fitHeightProperty().bind(cell.heightProperty());

            cell.getChildren().add(view);

            editorField[col][row] = cell;
            editor.add(cell, col, row);
        }
    }

    // =========================
    // PALETTE
    // =========================
    VBox palette = createPalette();

    // Keine festen Pref-Überschreibungen hier, createPalette() kümmert sich

    // Abstand zum Fenster-Rand über BorderPane-Margin
    BorderPane.setMargin(palette, new Insets(15, 15, 15, 15));

    // =========================
    // ROOT LAYOUT
    // =========================
    BorderPane root = createEditorLayout(editor, palette);

    // Haupt-Abstand zur Fenster-Border (alle Seiten)
    root.setPadding(new Insets(15));

    // =========================
    // SINGLE SCENE (FIXED)
    // =========================
    editorScene = new Scene(root, baseWidth, baseHeight);
    iStage.setScene(editorScene);

    // =========================
    // SCALING (ATTACH TO REAL SCENE)
    // =========================
    editorScene.widthProperty().addListener((obs, o, n) -> updateGridScale());
    editorScene.heightProperty().addListener((obs, o, n) -> updateGridScale());

    Platform.runLater(this::updateGridScale);

    // =========================
    // PAINT SYSTEM
    // =========================
    editorScene.setOnMousePressed(e -> {
        mouseDown = true;
        handlePaint(e.getPickResult().getIntersectedNode());
    });

    editorScene.setOnMouseDragged(e -> {
        if (mouseDown) {
            handlePaint(e.getPickResult().getIntersectedNode());
        }
    });

    editorScene.setOnMouseReleased(e -> mouseDown = false);
}

private BorderPane createEditorLayout(GridPane editor, VBox palette) {

    StackPane gridBox = new StackPane(editor);

    gridBox.setStyle(
            "-fx-padding: 20;" +
            "-fx-background-color: #1e1e1e;" +
            "-fx-background-radius: 12;" +
            "-fx-border-radius: 12;" +
            "-fx-border-color: #444;"
    );

    // gridBox darf maximal den verfügbaren Platz nutzen, aber nicht mehr
    gridBox.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    gridBox.setMinSize(0, 0);

    BorderPane root = new BorderPane();

    root.setCenter(gridBox);
    root.setRight(palette);

    // Abstand der Palette innerhalb des BorderPane (zusätzlich zum root.setPadding)
    BorderPane.setMargin(palette, new Insets(15, 15, 15, 15));

    // GridBox zentrieren, damit Ränder symmetrisch aussehen
    BorderPane.setAlignment(gridBox, Pos.CENTER);

    return root;
}

private void handlePaint(Object target) {
    if (target == null) return;

    Node node = (Node) target;

    while (node != null && !(node instanceof StackPane)) {
        node = node.getParent();
    }

    if (node == null) return;

    StackPane cell = (StackPane) node;

    // Nur Zellen aus dem Editor-Grid
    if (cell.getParent() != editor) return;
    if (cell.getChildren().isEmpty()) return;

    ImageView view = (ImageView) cell.getChildren().get(0);
    paint(view);
}

private void updateGridScale() {
    if (editor == null || editor.getParent() == null) return;

    // Verfügbarer Platz innerhalb des StackPane (gridBox)
    Region parent = (Region) editor.getParent();
    double availableWidth = parent.getWidth();
    double availableHeight = parent.getHeight();

    // Palette-Breite dynamisch ermitteln (nicht hardcoded)
    Node paletteNode = ((BorderPane) parent.getParent()).getRight();
    double paletteWidth = paletteNode != null ?
            paletteNode.getBoundsInParent().getWidth() : 180;

    // Zusätzlicher Puffer für Margin/Padding
    double padding = 40; // kann je nach Design angepasst werden

    // Platz für Grid berechnen
    availableWidth -= paletteWidth;
    availableWidth -= padding; // horizontaler Puffer
    availableHeight -= padding; // vertikaler Puffer

    // Quadratische Zellen (1:1)
    double cellSize = Math.min(
            availableWidth / columns,
            availableHeight / rows
    );

    // Sicherheitspuffer, damit nichts überläuft
    cellSize = Math.max(0, cellSize);

    double gridWidth = cellSize * columns;
    double gridHeight = cellSize * rows;

    editor.setPrefSize(gridWidth, gridHeight);
    editor.setMaxSize(gridWidth, gridHeight);
    editor.setMinSize(gridWidth, gridHeight);

    // Alle Zellen exakt quadratisch
    for (int y = 0; y < rows; y++) {
        for (int x = 0; x < columns; x++) {
            StackPane cell = editorField[x][y];

            cell.setPrefSize(cellSize, cellSize);
            cell.setMinSize(cellSize, cellSize);
            cell.setMaxSize(cellSize, cellSize);
        }
    }
}
