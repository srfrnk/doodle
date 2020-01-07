import { Logger } from '@overnightjs/logger';
import fetch from 'node-fetch';

export default class Elasticsearch {
    private elasticsearchUrl: string;
    public readonly crimes: Crime[] = [];

    constructor(elasticsearchUrl: string) {
        this.elasticsearchUrl = elasticsearchUrl;
        this.loadCrimes();
    }

    private async loadCrimes() {
        const res = await (await fetch(`${this.elasticsearchUrl}/crimes/_count`)).json();
        const total = res.count;
        const size = 1000;
        let scrollId: string | null = null;
        while (this.crimes.length < total) {
            const url = `${this.elasticsearchUrl}/${!scrollId ? 'crimes/_search?scroll=1m' : '_search/scroll'}`;
            const body = !scrollId ? { 'size': size } : { 'scroll': '1m', 'scroll_id': scrollId };
            const res: any = await (await fetch(url, {
                method: 'POST', headers: { 'content-type': 'application/json' }, body: JSON.stringify(body)
            })).json();
            scrollId = res._scroll_id;
            const newCrimes = res.hits.hits.map((hit: any) => ({ lat: hit._source.location.lat, lon: hit._source.location.lon }));
            Array.prototype.push.apply(this.crimes, newCrimes);
        }
    }
}

export interface Crime {
    lat: number;
    lon: number;
}