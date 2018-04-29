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


/** 
 * JavaFX application which interacts with the Google
 * Maps API to provide a mapping interface with which
 * to test and develop graph algorithms and data structures
 * 
 * @author UCSD MOOC development team
 * @author Miri Yehezkel
 *
 */
public class MapApp extends Application implements MapComponentInitializedListener {
	
	protected GoogleMapView mapComponent;
	
	protected GoogleMap map;
	
	/** Application layout with top, left, right, bottom and center positions */
	protected BorderPane bpLayout;
	
	/** Primary {@link Stage} of Map application */
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

		
		// create components and layout for fetch data
		//TODO check controller
		Button fetchDataButton = new Button("Fetch Data");
		TextField fetchDataTextField = new TextField();
		HBox fetchLayout = getFetchDataLayout(fetchDataTextField, fetchDataButton);
		
		//TODO check controller
		Button displayIntersectionsButton = new Button("Show Intersections");
		ComboBox<DataSet> mapComboBox = new ComboBox<>();
		// set on mouse pressed, this fixes Windows 10 / Surface bug
		mapComboBox.setOnMousePressed( e -> {
			mapComboBox.requestFocus();
		});
		
		//Top-right corner, used to choose map and show intersections
		VBox displayBox = getDisplayMapLayout(displayIntersectionsButton, mapComboBox);


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
		
		// initialize tabs for data fetching and route controls
		Tab routeTab = new Tab("Routing");
		
		setupRouteTab(routeTab, displayBox, startLabel, destLabel, pointLabel, 
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
		bpLayout.setBottom(fetchLayout);
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

	/**
	 * Creates a horizontal layout for fetching map data.
	 * @param tf {@link TextField} to input name of map
	 * @param fetchButton {@link Button} to start fetch of data
	 * @return a {@link HBox} with given TextField and Button.
	 */
	private HBox getFetchDataLayout(TextField tf, Button fetchButton) {
		HBox layout = new HBox();
		tf.setPrefWidth(FETCH_COMPONENT_WIDTH);
		fetchButton.setPrefWidth(FETCH_COMPONENT_WIDTH);
		layout.getChildren().addAll(tf, fetchButton);
		return layout;
	}
	
	
	
	/**
	 * Creates a vertical layout for displaying map intersections.
	 * @param displayIntersectionsButton {@link Button} to start display
	 * @param mapComboBox {@link ComboBox} options to display
	 * @return a {@link VBox} with given Button and ComboBox.
	 */
	private VBox getDisplayMapLayout(Button displayIntersectionsButton, ComboBox<DataSet> mapComboBox) {
		VBox vLayout = new VBox();
		HBox hLayout = new HBox();
		//sets button and comboBox side by side 
		mapComboBox.setPrefWidth(FETCH_COMPONENT_WIDTH);
		displayIntersectionsButton.setPrefWidth(FETCH_COMPONENT_WIDTH);
		hLayout.getChildren().addAll(mapComboBox, displayIntersectionsButton);
		//sets label above hLayout
		vLayout.getChildren().addAll(new Label("Choose map file:"), hLayout);
		return vLayout;
	}

	/**	
	 * Setup layout of route tab and controls
	 */
	private void setupRouteTab(Tab routeTab, VBox displayMapLayout, Label startGeoData, Label destGeoData, 
			Label currentGeoData, Button routeButton, Button hideRouteButton, Button resetButton, 
			Button vizualButton, Button startPtButton, Button destPtButton, 
			List<RadioButton> searchOptions) {
		
		HBox h = new HBox(); //Set up tab layout
		VBox v = new VBox(); //v is inner container of h
		
		Label startPointLabel = new Label("Start Position:");
		HBox startComponentsLayout = new HBox(MARGIN_VAL * 2, startGeoData,startPtButton);

		Label destPointLabel = new Label("Goal Position:");
		HBox destComponentsLayout = new HBox(MARGIN_VAL * 2, destGeoData, destPtButton);
		
		HBox routeShowHideLayout = new HBox(MARGIN_VAL * 2, routeButton, hideRouteButton);
		
		Label currentPointLabel = new Label("Selected Position:");
		VBox currentPointLayout = new VBox(currentPointLabel, currentGeoData);
		
		VBox rbLayout = new VBox(2);
		for (RadioButton rb : searchOptions)
			rbLayout.getChildren().add(rb);
			
		
		v.getChildren().addAll(displayMapLayout,
				startPointLabel,
				startComponentsLayout,
				destPointLabel,
				destComponentsLayout,
				routeShowHideLayout,
				rbLayout,
				vizualButton,
				currentPointLayout
			);
		
		
		h.getChildren().add(v);
		
		vizualButton.setDisable(true);
		HBox.setMargin(v, new Insets(MARGIN_VAL,MARGIN_VAL,MARGIN_VAL,MARGIN_VAL));
		
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

	/**
	 * Sets up a {@link ToggleGroup} of available search options (BFS, Dijkstra and A*).
	 * @param group a {@link ToggleGroup} to associate searches to
	 * @return A {@link LinkedList} representing the search options as {@link RadioButton}(s).
	 */
	private LinkedList<RadioButton> setupToggle(ToggleGroup group) {
		final String bfs = "BFS";
		final String dijkstra = "Dijkstra";
		final String aStar = "A*";

		RadioButton rbBFS = new RadioButton(bfs);
		RadioButton rbDijkstra = new RadioButton(dijkstra);
		RadioButton rbAStar = new RadioButton(aStar);
		
		rbBFS.setUserData(bfs);
		rbDijkstra.setUserData(dijkstra);
		rbAStar.setUserData(aStar);

		rbAStar.setSelected(true); //default option

		rbBFS.setToggleGroup(group);
		rbDijkstra.setToggleGroup(group);
		rbAStar.setToggleGroup(group);
		return new LinkedList<>(Arrays.asList(rbBFS, rbDijkstra, rbAStar));
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
