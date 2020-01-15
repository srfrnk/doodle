import { Logger } from '@overnightjs/logger';
import fetch from 'node-fetch';

export default class Elasticsearch {
    private elasticsearchUrl: string;
    public readonly crimes: Crime[] = [];
    public readonly rentalHomes: RentalHome[] = [];

    constructor(elasticsearchUrl: string) {
        this.elasticsearchUrl = elasticsearchUrl;
        this.loadCrimes();
        this.loadRentalHomes();
    }

    private loadCrimes() {
        return Elasticsearch.loadIndex('crimes', this.crimes, this.elasticsearchUrl,
            (hit: CrimeHit) => ({ lat: hit.location.lat, lon: hit.location.lon }));
    }

    private async loadRentalHomes() {
        return Elasticsearch.loadIndex('home_rentals', this.rentalHomes, this.elasticsearchUrl,
            (hit: RentalHomeHit) => ({
                lat: hit.location.lat,
                lon: hit.location.lon,
                weight: Elasticsearch.calcRentalHomeWeight(hit)
            }));
    }

    private static calcRentalHomeWeight(hit: RentalHomeHit): number {
        return hit.pricePerMonth;
    }

    private static async loadIndex<I, O>(index: string, array: O[], elasticsearchUrl: string, mapFunc: (hit: I) => O) {
        const res = await (await fetch(`${elasticsearchUrl}/${index}/_count`)).json();
        const total = res.count;
        const size = 1000;
        let scrollId: string | null = null;
        while (array.length < total) {
            const url = `${elasticsearchUrl}/${!scrollId ? index + '/_search?scroll=1m' : '_search/scroll'}`;
            const body = !scrollId ? { 'size': size } : { 'scroll': '1m', 'scroll_id': scrollId };
            const res: any = await (await fetch(url, {
                method: 'POST', headers: { 'content-type': 'application/json' }, body: JSON.stringify(body)
            })).json();
            scrollId = res._scroll_id;
            const newItems = res.hits.hits.map((hit: any) => (hit._source) as I).map(mapFunc);
            Array.prototype.push.apply(array, newItems);
        }
    }
}

export interface Crime {
    lat: number;
    lon: number;
}

export interface CrimeHit {
    location: {
        lat: number;
        lon: number;
    }
}

export interface RentalHome {
    lat: number;
    lon: number;
    weight: number;
}

export interface RentalHomeHit {
    location: {
        lat: number;
        lon: number;
    }

    roomNumber: number;
    bathroomNumber: number;
    bedroomNumber: number;
    carSpaces: number;
    commission: number;
    constructionYear: number;
    pricePerMonth: number;
    propertyType: string;
    size: number;
}
