export interface VideoShort {
    title: string
    video: string
    tear: number
}

export interface Speaker {
    name: string
    twitter?: string
    bio?: string
}

export interface Session {
    title: string
    video?: string
    year: number
    abstract?: string
    format: string
    language: string
    speakers?: Speaker[]
    intendedAudience?: string
}
