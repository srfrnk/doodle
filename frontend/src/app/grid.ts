import { throws } from 'assert';

export class Grid<T> {
  private data: Map<string, T[]> = new Map<string, T[]>();
  private varLat: number;
  private varLon: number;
  private cellSzLat: number;
  private cellSzLon: number;

  // tslint:disable:align
  public constructor(private minLat: number, private minLon: number,
    private maxLat: number, private maxLon: number,
    private countLat: number, private countLon: number) {
    this.varLat = this.maxLat - this.minLat;
    this.varLon = this.maxLon - this.minLon;
    this.cellSzLat = this.varLat / this.countLat;
    this.cellSzLon = this.varLon / this.countLon;
  }

  private getCellKey(lat: number, lon: number) {
    const idxLat = Math.floor((lat - this.minLat) / this.cellSzLat);
    const idxLon = Math.floor((lon - this.minLon) / this.cellSzLon);
    return this.getCellKeyFromIdx(idxLat, idxLon);
  }

  private getCellKeyFromIdx(idxLat: number, idxLon: number) {
    return `${idxLat}_${idxLon}`;
  }

  public add(lat: number, lon: number, items: T[]) {
    if (lat < this.minLat || lat > this.maxLat || lon < this.minLon || lon > this.maxLon) {
      // throw new Error(`Out of bounding box: ${lat},${lon}`);
      return;
    }
    const cellKey = this.getCellKey(lat, lon);
    let cell = this.data.get(cellKey);
    if (!cell) {
      cell = new Array<T>(0);
      this.data.set(cellKey, cell);
    }
    Array.prototype.push.call(cell, items);
  }

  private getCellFeature(idxLat: number, idxLon: number): google.maps.Data.Feature {
    const minLat = this.minLat + idxLat * this.cellSzLat;
    const maxLat = minLat + this.cellSzLat;
    const minLon = this.minLon + idxLon * this.cellSzLon;
    const maxLon = minLon + this.cellSzLon;
    const cellKey = this.getCellKeyFromIdx(idxLat, idxLon);
    const cell = this.data.get(cellKey);
    if (!!cell) {
      return new google.maps.Data.Feature({
        id: cellKey,
        properties: {
          cell
        },
        geometry: new google.maps.Data.Polygon([[
          { lng: minLon, lat: minLat },
          { lng: minLon, lat: maxLat },
          { lng: maxLon, lat: maxLat },
          { lng: maxLon, lat: minLat },
          { lng: minLon, lat: minLat }]])
      });
    } else {
      return null;
    }
  }

  public getFeatures(): any {
    const features = [];
    for (let idxLat = 0; idxLat < this.countLat; idxLat++) {
      for (let idxLon = 0; idxLon < this.countLon; idxLon++) {
        const cellFeature = this.getCellFeature(idxLat, idxLon);
        if (!!cellFeature) {
          features.push(cellFeature);
        }
      }
    }
    return features;
  }

  /* private cellToGeoJson(idxLat: number, idxLon: number): any {
    const minLat = this.minLat + idxLat * this.cellSzLat;
    const maxLat = minLat + this.cellSzLat;
    const minLon = this.minLon + idxLon * this.cellSzLon;
    const maxLon = minLon + this.cellSzLon;
    const cellKey = this.getCellKeyFromIdx(idxLat, idxLon);
    const cell = this.data.get(cellKey);
    if (!!cell) {
      return {
        id: cellKey,
        type: 'Feature',
        properties: this.getCellStyle(cell),
        geometry: {
          type: 'Polygon',
          coordinates: [
            [
              [minLon, minLat],
              [minLon, maxLat],
              [maxLon, maxLat],
              [maxLon, minLat],
              [minLon, minLat]
            ]
          ]
        }
      };
    } else {
      return null;
    }
  } */

  /*
    public toGeoJson(): any {
      const features = [];
      for (let idxLat = 0; idxLat < this.countLat; idxLat++) {
        for (let idxLon = 0; idxLon < this.countLon; idxLon++) {
          const cellGeoJson = this.cellToGeoJson(idxLat, idxLon);
          if (!!cellGeoJson) {
            features.push(cellGeoJson);
          }
        }
      }
      return {
        id: 'layer',
        type: 'FeatureCollection',
        features
      };
    }
   */
}
