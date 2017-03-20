/*License (MIT)

 Copyright © 2013 Matt Diamond

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and
 to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of
 the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 DEALINGS IN THE SOFTWARE.
 */

function run() {
  var WORKER_PATH = 'recorderWorker.js';

  var Recorder = function(source, cfg) {
    var config = cfg || {};
    var bufferLen = config.bufferLen || 4096;
    this.context = source.context;
    if(!this.context.createScriptProcessor){
      this.node = this.context.createJavaScriptNode(bufferLen, 2, 2);
    } else {
      this.node = this.context.createScriptProcessor(bufferLen, 2, 2);
    }

    var worker = new Worker(config.workerPath || WORKER_PATH);
    worker.postMessage({
      command: 'init',
      config: {
        sampleRate: this.context.sampleRate
      }
    });
    var recording = false,
      currCallback;

    this.node.onaudioprocess = function(e) {
      if (!recording) return;
      worker.postMessage({
        command: 'record',
        buffer: [
          e.inputBuffer.getChannelData(0),
          e.inputBuffer.getChannelData(1)
        ]
      });
    };

    this.configure = function(cfg) {
      for (var prop in cfg){
        if (cfg.hasOwnProperty(prop)) {
          config[prop] = cfg[prop];
        }
      }
    };

    this.record = function() {
      recording = true;
    };

    this.stop = function() {
      recording = false;
    };

    this.clear = function() {
      worker.postMessage({ command: 'clear' });
    };

    this.getBuffers = function(cb) {
      currCallback = cb || config.callback;
      worker.postMessage({ command: 'getBuffers' })
    };

    this.exportWAV = function(cb, type) {
      currCallback = cb || config.callback;
      type = type || config.type || 'audio/wav';
      if (!currCallback) throw new Error('Callback not set');
      worker.postMessage({
        command: 'exportWAV',
        type: type
      });
    };

    this.exportMonoWAV = function(cb, type) {
      currCallback = cb || config.callback;
      type = type || config.type || 'audio/wav';
      if (!currCallback) throw new Error('Callback not set');
      worker.postMessage({
        command: 'exportMonoWAV',
        type: type
      });
    };

    worker.onmessage = function(e) {
      var blob = e.data;
      currCallback(blob);
    };

    source.connect(this.node);
    this.node.connect(this.context.destination);   // if the script node is not connected to an output the "onaudioprocess" event is not triggered in chrome.
  };

  $.ajax({
    type: 'GET',
    url: 'http://0.0.0.0:5546/wit_ai_config'
  }).done(function(data) {
    window.WITAI_API_SECRET = data.WITAI_API_SECRET;
  }).fail(function(req, textStatus, e) {
    console.log(e);
  });

  Recorder.speechToText = function(blob) {
    $.ajax({
      type: 'POST',
      url: 'https://api.wit.ai/speech?v=20170308',
      data: blob,
      processData: false,
      contentType: 'audio/wav',
      headers: {
        Authorization: 'Bearer ' + window.WITAI_API_SECRET
      }
    }).done(function(data) {
      $('#question').text(data._text);
      window.setTimeout(function () {
        $('#answer').text('Asking Anserini for answer...');
      }, 500);
      $.ajax({
        type: 'POST',
        url: 'http://0.0.0.0:5546/answer',
        data: JSON.stringify({question: data._text, k: 5}),
        contentType : 'application/json'
      }).done(function(data) {
        var answers = data.answers.map(function(a) {
            return '<li>' + a.passage + ' (' + Number((a.score).toFixed(4)) + ')</li>';
        });
        var formattedAnswers = '<ol>' + answers.join('\n') + '</ol>';
        $('#answer').html(formattedAnswers);
      }).fail(function(req, textStatus, e) {
        $('#answer').text(e);
      });
    }).fail(function(req, textStatus, e) {
      $('#question').text(e);
    });
  };

  window.Recorder = Recorder;
}

window.addEventListener('load', run);
