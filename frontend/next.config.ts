import { NextConfig } from 'next';

const config: NextConfig = {
    async rewrites() {
        return [
            {
                source: '/members/:path*',
                destination: 'http://localhost:8080/members/:path*', // backend server address
            },
        ];
    },
};

export default config;


