var exec = require('cordova/exec');

var CordovaHTMLOverlay = {
    showOnLoad: function (htmlStr, successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, 'CordovaHTMLOverlay', 'show', [htmlStr]);
    },
    hide: function (successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, 'CordovaHTMLOverlay', 'hide', []);
    }
};

module.exports = CordovaHTMLOverlay;
