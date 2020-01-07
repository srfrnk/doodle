import { Logger } from '@overnightjs/logger';

class Config {
    public readonly jwtSecret: string = '';

    constructor(config: Partial<Config>) {
        Object.assign(this, config);
    }
}

let config: Config;

function initConfig() {
    if (!config) {
        const envName = process.env.env || 'local';
        Logger.Imp(`Using env ${envName}`);
        const envConfig = require(`./env/${envName}.ts`);
        config = new Config(envConfig);
    }
    return config;
}

export default initConfig;