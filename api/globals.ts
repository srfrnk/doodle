import Config from './config';
import { JwtManager } from '@overnightjs/jwt';

const config = Config();
const jwtMgr = new JwtManager(config.jwtSecret, '10h');

export {
    config,
    jwtMgr
};