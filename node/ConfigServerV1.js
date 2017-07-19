// content of index.js
var fs = require('fs');
const http = require('http')  
const port = 3000
var counter = 0
var change = 4000
var max = 8000

const requestHandler = (request, response) => {  
  console.log(request.url)
  response.writeHead(200, {"Content-Type": "application/json"});
  //var json = JSON.stringify({config: configObj});
  //var json = JSON.parse(fs.readFileSync('config.json', 'utf8'));
  counter ++;
  if (counter<change){
	fs.readFile('config_close.json', 'utf8', function (err, data) {
	  if (err) throw err;
	  json = JSON.parse(data);
	  response.end(JSON.stringify(json));
	});
  } else if (counter >= change){
	fs.readFile('config_open.json', 'utf8', function (err, data) {
	  if (err) throw err;
	  json = JSON.parse(data);
	  response.end(JSON.stringify(json));
	});
  } 
  if (counter == max){
	counter = 0;
  }

  
  
}

const server = http.createServer(requestHandler)

server.listen(port, (err) => {  
  if (err) {
    return console.log('something bad happened', err)
  }

  console.log(`server is listening on ${port}`)
})