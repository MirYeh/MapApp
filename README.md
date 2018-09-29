# MapApp

A desktop application for visualizing various route search algorithms on top of the Google Maps platform.

The application implements Breadth-First, Dijkstra and A* search algorithms and allows to visualize the search in order to see the different approach each search takes.




## Main Features

- [x] Plan route from start point to goal
- [x] Fetch real-world intersections of your desired area
- [x] Visualize search to see which nodes were visited during search




## Getting Started

1. Download or clone repository.
2. Add your generated [Google Maps API Key](https://developers.google.com/maps/documentation/javascript/get-api-key) to [index.html](./blob/master/src/html/index.html):
```
<script src="https://maps.googleapis.com/maps/api/js?key=INSERT_KEY_HERE&callback=initMap"></script>
```
3. Build the project with Apache Maven:
```
mvn package
```
4. Start planning your route!

[![Map App Demo](../master/extra/demo%20screenshot.png "Map App Demo")](https://www.youtube.com/watch?v=b0jn0Q3SCyw) 
