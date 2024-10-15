const routeRulesProxy = () => {
    if (process.env.NODE_ENV === 'development') {
        return {
            '/api/**': {
                proxy: {
                    to: 'http://127.0.0.1:8080/api/**'
                }
            }

        }
    } else {
        return {}
    }
}

// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
    compatibilityDate: '2024-04-03',
    future: {
        compatibilityVersion: 4
    },
    devtools: {
        enabled: true
    },
    modules: ['vuetify-nuxt-module', '@nuxt/scripts', '@nuxt/icon'],
    routeRules: routeRulesProxy(),
    icon: {
        clientBundle: {
            scan: true,
            sizeLimitKb: 256,
        },
    },
})
