import { Component, OnInit, Input, ViewChild, ElementRef } from '@angular/core';
import { ApiService, Crime } from '../api.service';
import { Grid } from '../grid';
import { DataLayerManager, AgmDataLayer } from '@agm/core';

class Layer {

  opacity_ = 1;
  layer = null;
  data = null;

  constructor(private api: ApiService) {
  }

  get opacity(): number {
    return this.opacity_;
  }

  set opacity(value: number) {
    this.opacity_ = value;
    this.setStyle();
  }

  setStyle() {
    if (!this.layer) {
      return;
    }
    this.layer.setStyle((feature) => {
      const cell = feature.getProperty('cell');
      return {
        strokeOpacity: 0,
        strokeWeight: 0,
        fillOpacity: this.opacity_,
        fillColor: `rgba(${255 - cell.length},0,0,1)`,
      };
    });
  }

  render(map: google.maps.Map) {
    this.setStyle();
    const bounds = map.getBounds();
    const neBounds = bounds.getNorthEast();
    const swBounds = bounds.getSouthWest();

    const crimeGrid = new Grid<Crime>(
      Math.min(neBounds.lat(), swBounds.lat()),
      Math.min(neBounds.lng(), swBounds.lng()),
      Math.max(neBounds.lat(), swBounds.lat()),
      Math.max(neBounds.lng(), swBounds.lng()),
      100, 100);
    this.data.forEach(crime => {
      crimeGrid.add(crime.lat, crime.lon, [crime]);
    });

    const features = crimeGrid.getFeatures();

    for (const feature of features) {
      this.layer.add(feature);
    }
  }

  async load(map: google.maps.Map) {
    this.layer = new google.maps.Data({ map });
    this.data = await this.api.crimes().toPromise();
    this.render(map);
  }
}

@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.scss']
})
export class MapComponent implements OnInit {
  constructor(private api: ApiService, private dataLayerManager: DataLayerManager) {
  }

  private map: google.maps.Map = null;

  public layers = {
    crimes: new Layer(this.api),
    homeRentalPrices: new Layer(this.api/* async () => {
      const homeRentalPrices = await this.api.homeRentalPrices().toPromise();
      const layer = new google.maps.visualization.HeatmapLayer({
        map: this.map,
        radius: 10,
        opacity: 0.0,
        maxIntensity: 1000,
        gradient: [
          'rgba(0, 0, 255, 0)',
          'rgba(0, 0, 255, 1)',
          'rgba(0, 0, 150, 1)',
          'rgba(0, 0, 100, 1)',
          'rgba(0, 0, 50, 1)',
          'rgba(0, 0, 20, 1)',
          'rgba(0, 0, 0, 1)',
        ],
        data: homeRentalPrices.map(home => ({ location: new google.maps.LatLng(home.lat, home.lon), weight: home.weight / 10 }))
      });
      return layer;
    } */)
  };

  zoom = 11;
  lat = 51.51;
  lng = -0.12;

  crimeData = null;
  crimeGrid = null;
  drawLayer = null;

  async onMapLoad(mapInstance: google.maps.Map) {
    this.map = mapInstance;
    for (const layer of Object.values(this.layers)) {
      layer.load(this.map);
    }
  }

  ngOnInit() {
  }

  zoomChange() {
    for (const layer of Object.values(this.layers)) {
      layer.render(this.map);
    }
  }
}
