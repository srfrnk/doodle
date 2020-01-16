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
        radius: 10,
        opacity: 0.0,
        maxIntensity: 300,
        gradient: [
          'rgba(255, 0, 0, 0)',
          'rgba(255, 0, 0, 1)',
          'rgba(150, 0, 0, 1)',
          'rgba(100, 0, 0, 1)',
          'rgba(50, 0, 0, 1)',
          'rgba(20, 0, 0, 1)',
          'rgba(00, 0, 0, 1)'
        ],
        data: crimes.map(crime => (new google.maps.LatLng(crime.lat, crime.lon)))
      });
      return layer;
    }),
    homeRentalPrices: new Layer(async () => {
      const homeRentalPrices = await this.api.homeRentalPrices().toPromise();
      const layer = new google.maps.visualization.HeatmapLayer({
        map: this.map,
        radius: 10,
        opacity: 0.0,
        maxIntensity: 300,
        gradient: [
          'rgba(0, 0, 255, 0)',
          'rgba(0, 0, 255, 1)',
          'rgba(0, 0, 150, 1)',
          'rgba(0, 0, 100, 1)',
          'rgba(0, 0, 50, 1)',
          'rgba(0, 0, 20, 1)',
          'rgba(0, 0, 00, 1)'
        ],
        data: homeRentalPrices.map(homeRentalPrice => ({
          location: new google.maps.LatLng(homeRentalPrice.lat, homeRentalPrice.lon),
          weight: homeRentalPrice.weight
        }))
      });
      return layer;
    })
  };

  zoom = 11;
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
