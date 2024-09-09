export function displayLanguage(lang: string) {
    switch (lang) {
        case 'NO': {
            return "Norwegian";
        }
        case 'EN': {
            return "English";
        }
        default: {
            return "Unknown";
        }
    }
}

export function displayFormat(format: string) {
    switch (format) {
        case 'PRESENTATION': {
            return "Presentation";
        }
        case 'LIGHTNING_TALK': {
            return "Lightning talk";
        }
        case 'WORKSHOP': {
            return "Workshop";
        }
        case 'BIRDS_OF_A_FEATHER': {
            return "Birds of a feather";
        }
        case 'PANEL': {
            return "Panel";
        }
        default: {
            return "Unknown";
        }
    }
}