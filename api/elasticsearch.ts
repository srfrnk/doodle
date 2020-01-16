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
        return Elasticsearch.loadIndex('crimes', (hit) => hit, this.crimes, this.elasticsearchUrl,
            (hit: CrimeHit) => ({ lat: hit.location.lat, lon: hit.location.lon }));
    }

    private async loadRentalHomes() {
        return Elasticsearch.loadIndex('home_rentals', (hit) => hit, this.rentalHomes, this.elasticsearchUrl,
            (hit: RentalHomeHit) => ({
                lat: hit.location.lat,
                lon: hit.location.lon,
                weight: Elasticsearch.calcRentalHomeWeight(hit)
            }));
    }

    private static calcRentalHomeWeight(hit: RentalHomeHit): number {
        const value = hit.pricePerMonth / (hit.bedroomNumber + hit.bathroomNumber + hit.roomNumber);
        return Math.max(0, isNaN(value) ? 0 : value / 10.0);
    }

    private static async loadIndex<I, O>(index: string, locationFunc: (i: O) => { lat: number, lon: number }, array: O[], elasticsearchUrl: string, mapFunc: (hit: I) => O) {
        const res = await (await fetch(`${elasticsearchUrl}/${index}/_count`)).json();
        const total = res.count;
        const size = 1000;
        let scrollId: string | null = null;
        const locations = new Map<String, number>();

        while (array.length < total) {
            const url = `${elasticsearchUrl}/${!scrollId ? index + '/_search?scroll=1m' : '_search/scroll'}`;
            const body = !scrollId ? { 'size': size } : { 'scroll': '1m', 'scroll_id': scrollId };
            const res: any = await (await fetch(url, {
                method: 'POST', headers: { 'content-type': 'application/json' }, body: JSON.stringify(body)
            })).json();
            scrollId = res._scroll_id;
            const newItems: O[] = res.hits.hits.map((hit: any) => (hit._source) as I).map(mapFunc);
            for (const item of newItems) {
                if (!locationFunc || Elasticsearch.checkLocation(item, locations, locationFunc)) {
                    array.push(item);
                }
            }
        }
    }

    private static checkLocation<O>(item: O, locations: Map<String, number>, locationFunc: (i: O) => { lat: number, lon: number }): boolean {
        const hitLocation = locationFunc(item);
        if (!hitLocation) {
            return false;
        }
        const hitKey = `${hitLocation.lat}_${hitLocation.lon}`;
        let hitCache = locations.get(hitKey);

        if (!!hitCache) {
            hitCache++;
            locations.set(hitKey, hitCache);
        }
        else {
            locations.set(hitKey, 1);
            hitCache = 1;
        }

        return hitCache < 10;
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
