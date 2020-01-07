import { Request, Response } from 'express';
import { Controller, Middleware, Get, Post } from '@overnightjs/core';
import { Logger } from '@overnightjs/logger';
import { elasticsearch } from '../globals';
@Controller('uk-pol')
export default class UkPoliceController {
    @Get('crimes')
    public async crimes(req: Request, res: Response) {
        try {
            res.status(200).json(elasticsearch.crimes);
        } catch (err) {
            Logger.Err(err, true);
            return res.status(500).json({});
        }
    }
}