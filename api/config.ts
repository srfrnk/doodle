import { Logger } from '@overnightjs/logger';

export default class Config {
    public readonly jwtSecret: string = '';
    public readonly elasticsearchUrl: string = '';

    private constructor(config: Partial<Config>) {
        Object.assign(this, config);
    }

    public static init(): Config {
        const envName = process.env.env || 'local';
        Logger.Imp(`Using env ${envName}`);
        const envConfig = require(`./env/${envName}.ts`);
        return new Config(envConfig);
    }
}
