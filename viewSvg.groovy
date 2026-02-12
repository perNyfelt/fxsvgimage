import javafx.application.Application
import javafx.geometry.Insets
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.stage.FileChooser
import javafx.stage.Stage

class SvgViewerApp extends Application {
   private final Group svgGroup = new Group()
   private final Label fileLabel = new Label("No file loaded")

   @Override
   void start(Stage stage) {
      Button openButton = new Button("Open SVG...")
      openButton.setOnAction { event ->
         FileChooser chooser = new FileChooser()
         chooser.title = "Select SVG"
         chooser.extensionFilters.add(new FileChooser.ExtensionFilter("SVG files", "*.svg"))
         File file = chooser.showOpenDialog(stage)
         if (file != null) {
            loadSvg(file)
         }
      }

      HBox topBar = new HBox(8, openButton, fileLabel)
      topBar.setPadding(new Insets(10))

      ScrollPane scrollPane = new ScrollPane(svgGroup)
      scrollPane.setPannable(true)

      BorderPane root = new BorderPane()
      root.setTop(topBar)
      root.setCenter(scrollPane)

      Scene scene = new Scene(root, 800, 600)
      stage.setTitle("SVG Viewer")
      stage.setScene(scene)
      stage.show()

      List<String> args = getParameters().getRaw()
      if (args != null && !args.isEmpty()) {
         File file = new File(args.get(0))
         if (file.exists()) {
            loadSvg(file)
         } else {
            fileLabel.setText("File not found: " + file.getPath())
         }
      }
   }

   private void loadSvg(File file) {
      try {
         org.girod.javafx.svgimage.SVGImage image = org.girod.javafx.svgimage.SVGLoader.load(file)
         svgGroup.getChildren().clear()
         if (image != null) {
            svgGroup.getChildren().add(image)
            fileLabel.setText(file.getAbsolutePath())
         } else {
            fileLabel.setText("Failed to load: " + file.getAbsolutePath())
         }
      } catch (Exception ex) {
         fileLabel.setText("Error loading: " + file.getAbsolutePath())
         ex.printStackTrace()
      }
   }
}

Application.launch(SvgViewerApp, args)
