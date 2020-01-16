import { Request, Response } from 'express';
import { Controller, Middleware, Get, Post } from '@overnightjs/core';
import { Logger } from '@overnightjs/logger';
import { elasticsearch } from '../globals';
@Controller('rental-homes')
export default class RentalHomesController {
    @Get('prices')
    public async prices(req: Request, res: Response) {
        try {
            res.status(200).json(elasticsearch.rentalHomes);
        } catch (err) {
            Logger.Err(err, true);
            return res.status(500).json({});
        }
    }
}