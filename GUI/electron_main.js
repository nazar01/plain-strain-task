const { app, BrowserWindow } = require('electron');

function createWindow () {
    // Create the browser window.
    let win = new BrowserWindow({ width: 800, height: 600 });

    // and load the index.html of the app.
    win.loadFile('index.html');
    win.setMenu(null);
}

app.on('ready', createWindow);

