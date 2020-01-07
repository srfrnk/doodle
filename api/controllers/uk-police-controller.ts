import { Request, Response } from 'express';
import { Controller, Middleware, Get, Post } from '@overnightjs/core';
import { ISecureRequest } from '@overnightjs/jwt';
import { Logger } from '@overnightjs/logger';
import { jwtMgr } from '../globals';

@Controller('uk-pol')
export default class UkPoliceController {
    @Get('crimes')
    public crimes(req: Request, res: Response) {
        try {
            res.status(200).json([]);
        } catch (err) {
            Logger.Err(err, true);
            return res.status(500).json({});
        }
    }

    @Post('post1')
    public async post1(req: Request, res: Response) {
        try {
            const { } = req.body;

            return res.status(200).send('');
        } catch (err) {
            Logger.Err(err, true);
            return res.status(500).json({});
        }
    }

    @Post('post2')
    @Middleware(jwtMgr.middleware)
    public async post2(req: ISecureRequest, res: Response) {
        try {
            const id = req.payload.id;
            res.status(200).json({});
        } catch (err) {
            Logger.Err(err, true);
            return res.status(500).json({});
        }
    }
}