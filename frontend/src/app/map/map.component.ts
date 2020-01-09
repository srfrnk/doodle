import { Component, OnInit, Input } from '@angular/core';
import { ApiService } from '../api.service';

class Layer {
  constructor(private setup: () => Promise<google.maps.visualization.HeatmapLayer>) {
  }

  private layer: google.maps.visualization.HeatmapLayer;

  async load() {
    this.layer = await this.setup();
  }

  get opacity(): number {
    return !this.layer ? 0 : this.layer.get('opacity') * 100;
  }
  set opacity(value: number) {
    if (!!this.layer) {
      this.layer.set('opacity', value / 100.0);
    }
  }
}

@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.scss']
})
export class MapComponent implements OnInit {
  constructor(private api: ApiService) {
  }

  private map: google.maps.Map = null;

  public layers = {
    crimes: new Layer(async () => {
      const crimes = await this.api.crimes().toPromise();
      const layer = new google.maps.visualization.HeatmapLayer({
        map: this.map,
        radius: 50,
        opacity: 0.0,
        gradient: [
          'rgba(255, 0, 0, 0)',
          'rgba(255, 0, 0, 1)',
        ],
        data: crimes.map(crime => ({ location: new google.maps.LatLng(crime.lat, crime.lon), weight: 1 }))
      });
      return layer;
    })
  };

  zoom = 14;
  lat = 51.51;
  lng = -0.12;

  onMapLoad(mapInstance: google.maps.Map) {
    this.map = mapInstance;
    for (const layer of Object.values(this.layers)) {
      layer.load();
    }
  }

  ngOnInit() {
  }
}
