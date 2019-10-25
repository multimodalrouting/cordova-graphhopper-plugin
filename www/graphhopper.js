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
    cordova.exec(successCallback, function (err) {
      console.log('Error');
      console.error(err);
    }, 'Graphhopper', 'loadMap', [mapArea]);
  };
  
  Template.prototype.route = function (routeOptions, successCallback) {
    cordova.exec(successCallback, function (err) {
      console.log('Error')
      console.error(err);
    }, 'Graphhopper', 'route', [routeOptions]);
  };

  return Template;

})();

var graphhopper = new Graphhopper();
module.exports = graphhopper;