/**
 * Cordova Graphhopper Plugin
 */

var exec = require('cordova/exec'),
    channel = require('cordova/channel');

var Graphhopper = (function () {

  function Template() {
  }

  Template.prototype.echo = function (echoValue, successCallback) {
    cordova.exec(successCallback, function () {
      console.log('Error')
    }, 'Graphhopper', 'echo', [echoValue]);
  };
  
  Template.prototype.loadMap = function (mapArea, successCallback) {
    cordova.exec(successCallback, function () {
      console.log('Error')
    }, 'Graphhopper', 'loadMap', [mapArea]);
  };

  return Template;

})();

var graphhopper = new Graphhopper();
module.exports = graphhopper;