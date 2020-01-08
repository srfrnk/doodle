import { Component, OnInit, Input } from '@angular/core';
import { ApiService } from '../api.service';
import { MatSliderChange } from '@angular/material/slider';

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

  @Input('crimesOpacity')
  set crimesOpacity(opacity: number) {
    this.heatmap.set('opacity', opacity / 100.0);
  }
  get crimesOpacity(): number {
    return !this.heatmap ? 0 : this.heatmap.get('opacity') * 100;
  }

  async onMapLoad(mapInstance: google.maps.Map) {
    this.map = mapInstance;
    if (!!this.heatmap) {
      this.heatmap.setMap(this.map);
    }
  }

  async ngOnInit() {
    const crimes = await this.api.crimes().toPromise();
    this.heatmap = new google.maps.visualization.HeatmapLayer({
      map: this.map,
      radius: 50,
      opacity: 0.0,
      data: crimes.map(crime => ({ location: new google.maps.LatLng(crime.lat, crime.lon), weight: 1 })),
    });
  }
}
