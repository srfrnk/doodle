export class Point {
  lat: number;
  lon: number;
}

export class Layer {
  constructor(private color: string, private loadData: () => Promise<Point[]>) {
  }

  layer: any;
  options: any /*:  L.HexbinLayerOptions */ = { opacity: 0, colorRange: ['transparent', this.color] };
  data: [number, number][];
  async load() {
    const data = await this.loadData();
    this.data = data.map(point => [point.lon, point.lat]);
  }

  setOpacity(opacity) {
    this.layer.opacity(opacity / 100.0);
    this.layer.redraw();
  }

  ready(layer: any) {
    this.layer = layer;
  }
}
