import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.scss']
})
export class MapComponent implements OnInit {
  constructor() { }

  private map: google.maps.Map = null;
  private heatmap: google.maps.visualization.HeatmapLayer = null;

  zoom = 8;
  lat = 51.673858;
  lng = 7.815982;

  markers: marker[] = [
    {
      lat: 51.673858,
      lng: 7.815982,
      label: 'A',
      draggable: true
    },
    {
      lat: 51.373858,
      lng: 7.215982,
      label: 'B',
      draggable: false
    },
    {
      lat: 51.723858,
      lng: 7.895982,
      label: 'C',
      draggable: true
    }
  ];

  onMapLoad(mapInstance: google.maps.Map) {
    this.map = mapInstance;
    this.heatmap = new google.maps.visualization.HeatmapLayer({
      map: this.map,
      data: [
        new google.maps.LatLng(51.7255858, 7.895982),
        new google.maps.LatLng(51.7236858, 7.895382),
        new google.maps.LatLng(51.724358, 7.895482),
        new google.maps.LatLng(51.728358, 7.895682),
        new google.maps.LatLng(51.727858, 7.895782),
        new google.maps.LatLng(51.72333858, 7.895982),
        new google.maps.LatLng(51.7233858, 7.895582),
        new google.maps.LatLng(51.72463858, 7.895482),
        new google.maps.LatLng(51.7293858, 7.895582),
      ],
    });
  }

  ngOnInit() {
  }

  clickedMarker(label: string, index: number) {
    console.log(`clicked the marker: ${label || index}`);
  }

  mapClicked($event: MouseEvent) {
    this.markers.push({
      lat: ($event as any).coords.lat,
      lng: ($event as any).coords.lng,
      draggable: true
    });
  }

  markerDragEnd(m: marker, $event: MouseEvent) {
    console.log('dragEnd', m, $event);
  }
}

// just an interface for type safety.
interface marker {
  lat: number;
  lng: number;
  label?: string;
  draggable: boolean;
}
