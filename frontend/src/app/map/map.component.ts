import { Component, OnInit } from '@angular/core';
import { ApiService } from '../api.service';

@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.scss']
})
export class MapComponent implements OnInit {
  constructor(private api: ApiService) { }

  private map: google.maps.Map = null;
  private heatmap: google.maps.visualization.HeatmapLayer = null;

  zoom = 14;
  lat = 51.51;
  lng = -0.12;

  async onMapLoad(mapInstance: google.maps.Map) {
    this.map = mapInstance;

    const crimes = await this.api.crimes().toPromise();
    this.heatmap = new google.maps.visualization.HeatmapLayer({
      map: this.map,
      radius: 50,
      opacity: 0.4,
      data: crimes.map(crime => ({ location: new google.maps.LatLng(crime.lat, crime.lon), weight: 1 })),
    });
  }

  ngOnInit() {
  }
}
