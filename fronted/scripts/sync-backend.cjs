const fs = require('node:fs');
const path = require('node:path');

const source = path.resolve(__dirname, '../dist');
const destination = path.resolve(__dirname, '../../fx-server/src/main/resources/frontend');
if (!fs.existsSync(path.join(source, 'index.html'))) {
  throw new Error('Missing dist/index.html; build the frontend before syncing.');
}
// Keep hashed assets from previous builds available for pages already open.
fs.mkdirSync(destination, { recursive: true });
fs.cpSync(source, destination, { recursive: true });
console.log('Frontend build synced to fx-server/src/main/resources/frontend');
