var menubar = require('menubar');

var mb = menubar();

mb.on('ready', function ready () {
  console.log('Speech to text loaded..');
});
