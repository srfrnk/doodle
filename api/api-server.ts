import * as express from "express"
import * as bodyParser from 'body-parser';
import * as cors from 'cors';
import { Server } from '@overnightjs/core';
import { Logger } from '@overnightjs/logger';
import UkPoliceController from './controllers/uk-police-controller';
import RentalHomesController from './controllers/rental-homes';

export default class ChargerServer extends Server {
    constructor() {
        super(true);

        this.app.use(cors({
            origin: ['http://localhost:4200'],
            methods: ['GET', 'POST'],
            allowedHeaders: ['Content-Type', 'Authorization']
        }));
        this.app.use(bodyParser.json());
        this.app.use(bodyParser.urlencoded({ extended: true }));

        super.addControllers([new UkPoliceController(),new RentalHomesController()]);
    }

    public start(port: number): void {
        this.app.get('*', (req: express.Request, res: express.Response) => {
            res.status(200).send('Ok');
        });
        this.app.listen(port, () => {
            Logger.Imp(`Server started on port ${port}`);
        });
    }
}