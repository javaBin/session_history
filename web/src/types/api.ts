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

export interface LanguageAggregate {
    language: string
    count: number
}

export interface FormatAggregate {
    format: string
    count: number
}

export interface YearAggregate {
    year: number
    count: number
}

export interface Aggregate {
    languages: LanguageAggregate
    formats: FormatAggregate
    years: YearAggregate
}

export interface SearchResponse {
    sessionsResponse: List<Session>
    aggregateResponse: Aggregate
}