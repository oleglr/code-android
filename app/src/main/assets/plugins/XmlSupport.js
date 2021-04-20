function initPlugin() {}

function openFile(path) {
    return path.endsWith(".xml")
}

function loadPattern() {
    return "\\b()\\b";
}