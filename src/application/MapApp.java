/** JavaFX application which interacts with the Google
 * Maps API to provide a mapping interface with which
 * to test and develop graph algorithms and data structures
 * 
 * @author UCSD MOOC development team
 *
 */
package application;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.web.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import application.controllers.FetchController;
import application.controllers.RouteController;
import application.services.GeneralService;
import application.services.RouteService;
import gmapsfx.GoogleMapView;
import gmapsfx.MapComponentInitializedListener;
import gmapsfx.javascript.object.GoogleMap;
import gmapsfx.javascript.object.LatLong;
import gmapsfx.javascript.object.MapOptions;
import gmapsfx.javascript.object.MapTypeIdEnum;

public class MapApp extends Application
implements MapComponentInitializedListener {

	protected GoogleMapView mapComponent;
	protected GoogleMap map;
	/**
	 * Application layout with top, left, right, bottom and center positions
	 */
	protected BorderPane bpLayout;
	/**
	 * Primary {@link Stage} of Map application
	 */
	protected Stage primaryStage;

	// CONSTANTS
	private static final double MARGIN_VAL = 10;
	private static final double FETCH_COMPONENT_WIDTH = 160.0;

	public static void main(String[] args){
		launch(args);
	}

	/**
	 * Application entry point
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		this.primaryStage = primaryStage;

		// MAIN CONTAINER
		bpLayout = new BorderPane();

		// set up map
		mapComponent = new GoogleMapView();
		mapComponent.addMapInitializedListener(this);

		// initialize tabs for data fetching and route controls
		//Tab routeTab = new Tab("Routing");

		// create components for fetch tab
		Button fetchDataButton = new Button("Fetch Data");
		Button displayIntersectionsButton = new Button("Show Intersections");
		TextField fetchDataTextField = new TextField();
		ComboBox<DataSet> mapComboBox = new ComboBox<DataSet>();

		// set on mouse pressed, this fixes Windows 10 / Surface bug
		mapComboBox.setOnMousePressed( e -> {
			mapComboBox.requestFocus();
		});
		
		//TextField and Button at the bottom-left corner, used to name and fetch map data
		HBox fetchControls = getBottomBox(fetchDataTextField, fetchDataButton);

		//Top-right corner, used to choose map and show intersections
		VBox fetchBox = getFetchBox(displayIntersectionsButton, mapComboBox);


		// create components for fetch tab
		Button routeButton = new Button("Show Route");
		Button hideRouteButton = new Button("Hide Route");
		Button resetButton = new Button("Reset");
		Button visualizationButton = new Button("Start Visualization");
		Image startImage = new Image(MarkerManager.startURL, 35, 0, true, true);
		Image destImage = new Image(MarkerManager.destinationURL);
		CLabel<geography.GeographicPoint> startLabel = 
				new CLabel<>("Empty.", new ImageView(startImage), null);
		CLabel<geography.GeographicPoint> destLabel = 
				new CLabel<>("Empty.", new ImageView(destImage), null);
		//TODO -- hot fix
		startLabel.setMinWidth(180);
		destLabel.setMinWidth(180);
		
		Button startButton = new Button("Start");
		Button destButton = new Button("Dest");

		// Radio buttons for selecting search algorithm
		final ToggleGroup searchToggleGroup = new ToggleGroup();

		List<RadioButton> searchOptions = setupToggle(searchToggleGroup);


		// Select and marker managers for route choosing and marker display/visuals
		// should only be one instance (singleton)
		SelectManager selectManager = new SelectManager();
		MarkerManager markerManager = new MarkerManager();
		markerManager.setSelectManager(selectManager);
		selectManager.setMarkerManager(markerManager);
		markerManager.setVisButton(visualizationButton);

		// create components for route tab
		CLabel<geography.GeographicPoint> pointLabel = 
				new CLabel<>("No point Selected.", null);
		selectManager.setPointLabel(pointLabel);
		selectManager.setStartLabel(startLabel);
		selectManager.setDestinationLabel(destLabel);
		setupRouteTab(routeTab, fetchBox, startLabel, destLabel, pointLabel, 
				routeButton, hideRouteButton, resetButton, visualizationButton, 
				startButton, destButton, searchOptions);

		// add tabs to pane, give no option to close
		TabPane routeTabPane = new TabPane(routeTab);
		routeTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

		// initialize Services and controllers after map is loaded
		mapComponent.addMapReadyListener(() -> { 
			GeneralService generalService = new GeneralService(mapComponent, selectManager, markerManager);
			RouteService routeService = new RouteService(mapComponent, markerManager);
			// initialize controllers
			new RouteController(routeService, routeButton, hideRouteButton, resetButton, 
					startButton, destButton, searchToggleGroup, searchOptions, visualizationButton,
					startLabel, destLabel, pointLabel, selectManager, markerManager);
			new FetchController(generalService, routeService, fetchDataTextField, 
					fetchDataButton, mapComboBox, displayIntersectionsButton);
		});

		// add components to border pane
		bpLayout.setRight(routeTabPane);
		bpLayout.setBottom(fetchControls);
		bpLayout.setCenter(mapComponent);

		Scene scene = new Scene(bpLayout);
		scene.getStylesheets().add("html/routing.css");
		primaryStage.setScene(scene);
		primaryStage.setTitle("Map App");
		primaryStage.show();

	}


	@Override
	public void mapInitialized() {

		LatLong center = new LatLong(32.8810, -117.2380);


		// set map options
		MapOptions options = new MapOptions();
		options.center(center)
			.mapMarker(false)
			.mapType(MapTypeIdEnum.ROADMAP)
			//maybe set false
			.mapTypeControl(true)
			.overviewMapControl(false)
			.panControl(true)
			.rotateControl(false)
			.scaleControl(false)
			.streetViewControl(false)
			.zoom(14)
			.zoomControl(true);

		// create map;
		map = mapComponent.createMap(options);
		setupJSAlerts(mapComponent.getWebView());
		
	}


	// SETTING UP THE VIEW

	private HBox getBottomBox(TextField tf, Button fetchButton) {
		HBox box = new HBox();
		tf.setPrefWidth(FETCH_COMPONENT_WIDTH);
		box.getChildren().add(tf);
		fetchButton.setPrefWidth(FETCH_COMPONENT_WIDTH);
		box.getChildren().add(fetchButton);
		return box;
	}
	/**
	 * Setup layout and controls for Fetch tab
	 * @param fetchTab
	 * @param fetchButton
	 * @param displayButton
	 * @param tf
	 */
	private VBox getFetchBox(Button displayButton, ComboBox<DataSet> cb) {
		// add button to tab, rethink design and add V/HBox for content
		VBox verticalBox = new VBox();
		HBox horizontalBox = new HBox();



		HBox intersectionControls = new HBox();
		//        cb.setMinWidth(displayButton.getWidth());
		cb.setPrefWidth(FETCH_COMPONENT_WIDTH);
		intersectionControls.getChildren().add(cb);
		displayButton.setPrefWidth(FETCH_COMPONENT_WIDTH);
		intersectionControls.getChildren().add(displayButton);

		horizontalBox.getChildren().add(verticalBox);
		verticalBox.getChildren().add(new Label("Choose map file: "));
		verticalBox.getChildren().add(intersectionControls);

		//v.setSpacing(MARGIN_VAL);
		return verticalBox;
	}

	/**	
	 * Setup layout of route tab and controls
	 *
	 * @param routeTab
	 * @param box
	 */
	private void setupRouteTab(Tab routeTab, VBox fetchBox, Label startLabel, Label endLabel, Label pointLabel,
			Button showButton, Button hideButton, Button resetButton, Button vButton, Button startButton,
			Button destButton, List<RadioButton> searchOptions) {

		//set up tab layout
		HBox h = new HBox();
		// v is inner container
		VBox v = new VBox();
		h.getChildren().add(v);



		VBox selectLeft = new VBox();


		selectLeft.getChildren().add(startLabel);
		HBox startBox = new HBox();
		startBox.getChildren().add(startLabel);
		startBox.getChildren().add(startButton);
		startBox.setSpacing(20);

		HBox destinationBox = new HBox();
		destinationBox.getChildren().add(endLabel);
		destinationBox.getChildren().add(destButton);
		destinationBox.setSpacing(20);


		VBox markerBox = new VBox();
		Label markerLabel = new Label("Selected Marker : ");


		markerBox.getChildren().add(markerLabel);

		markerBox.getChildren().add(pointLabel);

		VBox.setMargin(markerLabel, new Insets(MARGIN_VAL,MARGIN_VAL,MARGIN_VAL,MARGIN_VAL));
		VBox.setMargin(pointLabel, new Insets(0,MARGIN_VAL,MARGIN_VAL,MARGIN_VAL));
		VBox.setMargin(fetchBox, new Insets(0,0,MARGIN_VAL*2,0));

		HBox showHideBox = new HBox();
		showHideBox.getChildren().add(showButton);
		showHideBox.getChildren().add(hideButton);
		showHideBox.setSpacing(2*MARGIN_VAL);

		v.getChildren().add(fetchBox);
		v.getChildren().add(new Label("Start Position : "));
		v.getChildren().add(startBox);
		v.getChildren().add(new Label("Goal : "));
		v.getChildren().add(destinationBox);
		v.getChildren().add(showHideBox);
		for (RadioButton rb : searchOptions) {
			v.getChildren().add(rb);
		}
		v.getChildren().add(vButton);
		VBox.setMargin(showHideBox, new Insets(MARGIN_VAL,MARGIN_VAL,MARGIN_VAL,MARGIN_VAL));
		VBox.setMargin(vButton, new Insets(MARGIN_VAL,MARGIN_VAL,MARGIN_VAL,MARGIN_VAL));
		vButton.setDisable(true);
		v.getChildren().add(markerBox);
		//v.getChildren().add(resetButton);


		routeTab.setContent(h);


	}

	private void setupJSAlerts(WebView webView) {
		webView.getEngine().setOnAlert( e -> {
			Stage popup = new Stage();
			popup.initOwner(primaryStage);
			popup.initStyle(StageStyle.UTILITY);
			popup.initModality(Modality.WINDOW_MODAL);

			StackPane content = new StackPane();
			content.getChildren().setAll(
					new Label(e.getData())
					);
			content.setPrefSize(200, 100);

			popup.setScene(new Scene(content));
			popup.showAndWait();
		});
	}

	private LinkedList<RadioButton> setupToggle(ToggleGroup group) {

		// Use Dijkstra as default
		RadioButton rbD = new RadioButton("Dijkstra");
		rbD.setUserData("Dijkstra");
		rbD.setSelected(true);

		RadioButton rbA = new RadioButton("A*");
		rbA.setUserData("A*");

		RadioButton rbB = new RadioButton("BFS");
		rbB.setUserData("BFS");

		rbB.setToggleGroup(group);
		rbD.setToggleGroup(group);
		rbA.setToggleGroup(group);
		return new LinkedList<RadioButton>(Arrays.asList(rbB, rbD, rbA));
	}


	/*
	 * METHODS FOR SHOWING DIALOGS/ALERTS
	 */

	public void showLoadStage(Stage loadStage, String text) {
		loadStage.initModality(Modality.APPLICATION_MODAL);
		loadStage.initOwner(primaryStage);
		VBox loadVBox = new VBox(20);
		loadVBox.setAlignment(Pos.CENTER);
		Text tNode = new Text(text);
		tNode.setFont(new Font(16));
		loadVBox.getChildren().add(new HBox());
		loadVBox.getChildren().add(tNode);
		loadVBox.getChildren().add(new HBox());
		Scene loadScene = new Scene(loadVBox, 300, 200);
		loadStage.setScene(loadScene);
		loadStage.show();
	}

	public static void showInfoAlert(String header, String content) {
		Alert alert = getInfoAlert(header, content);
		alert.showAndWait();
	}

	public static Alert getInfoAlert(String header, String content) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Information");
		alert.setHeaderText(header);
		alert.setContentText(content);
		return alert;
	}

	public static void showErrorAlert(String header, String content) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("File Name Error");
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.showAndWait();
	}


}
