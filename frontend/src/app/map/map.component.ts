import { Component, OnInit, Input } from '@angular/core';
import { ApiService } from '../api.service';
import { tileLayer, latLng, Map } from 'leaflet';
import { Layer } from '../layer';

@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.scss']
})
export class MapComponent implements OnInit {
  constructor(private api: ApiService) {
  }

  map: any;
  mapOptions = {
    layers: [
      tileLayer('https://{s}.tile.osm.org/{z}/{x}/{y}.png',
        {
          maxZoom: 18,
          attribution: '&copy OpenStreetMap'
        }),
    ],
  };
  mapZoom = 11;
  mapCenter = latLng(51.5, -0.12);

  crimes = new Layer('red', async () => {
    const crimes = await this.api.crimes().toPromise();
    return crimes.map(crime => ({ lon: crime.lon, lat: crime.lat }));
  });

  homeRentalPrices = new Layer('blue', async () => {
    const homeRentalPrices = await this.api.homeRentalPrices().toPromise();
    return homeRentalPrices.map(crime => ({ lon: crime.lon, lat: crime.lat }));
  });

  async onMapReady(map: Map) {
    this.map = map;
    this.crimes.load();
    this.homeRentalPrices.load();
  }

  ngOnInit() {
  }
}
