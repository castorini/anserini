'use strict';

 导入http模块:
var http = require('http');
const express = require('express');
const spawn = require('child_process').spawn;
const app = express();
const port = 3000;
app.get('/', (req, res) => {
 
    var dataToSend;
    // spawn new child process to call the python script
    const python = spawn('python3', ['scrape.py']);
    // collect data from script
    python.stdout.on('data', function (data) {
     console.log('Pipe data from python script ...');
     dataToSend = data.toString();
    });
    // in close event we are sure that stream from child process is closed
    python.on('close', (code) => {
    console.log(`child process close all stdio with code ${code}`);
    // send data to browser
    res.send(dataToSend)
    });
    
   })
app.listen(port, () => console.log(`Example app listening on port ${port}!`))

/* //创建http server，并传入回调函数:
var server = http.createServer(function (request, response) {
    // 回调函数接收request和response对象,
    // 获得HTTP请求的method和url:
    console.log(request.method + ': ' + request.url);
    // 将HTTP响应200写入response, 同时设置Content-Type: text/html:
    response.writeHead(200, {'Content-Type': 'text/html'});
    // 将HTTP响应的HTML内容写入response:
    response.end('<h1>Hello world!</h1>');
});

// 让服务器监听8080端口:
server.listen(8080);

console.log('Server is running at http://127.0.0.1:8080/');

//concurrent scraping by Python and Selenium and Beautiful Soup
//mongodb for data storage and caching
//front end: react(redux) etc
//back end:  Node.js | MongoDB | Express | (Rest APIs) | AJAX (Async) | Google Cloud

//level, city, province, country, 4/8 month, deadline, software, RegExp for compensation
*/