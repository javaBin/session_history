plugins {
    alias(libs.plugins.node)
}

node {
    download.set(true)
    version.set("20.11.0")
    npmVersion.set("10.2.4")
}