function initPlugin() {}

function openFile(path) {
    return path.endsWith(".js")
}

function loadPattern() {
    return "\\b(function|var|this|if|else|break|case|try|catch|while|return|switch)\\b";
}