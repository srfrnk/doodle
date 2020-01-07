import Config from './config';
import { JwtManager } from '@overnightjs/jwt';
import Elasticsearch from './elasticsearch';

const config = Config.init();
const jwtMgr = new JwtManager(config.jwtSecret, '10h');
const elasticsearch = new Elasticsearch(config.elasticsearchUrl);

export {
    config,
    jwtMgr,
    elasticsearch
};