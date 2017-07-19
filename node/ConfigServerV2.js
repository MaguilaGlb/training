// content of index.js
var fs = require('fs');
const http = require('http')  
const port = 3000

const requestHandler = (request, response) => {  
  console.log(request.url)
  response.writeHead(200, {"Content-Type": "application/json"});

	fs.readFile('config.json', 'utf8', function (err, data) {
	  if (err) throw err;
	  json = JSON.parse(data);
	  response.end(JSON.stringify(json));
	});
}

const server = http.createServer(requestHandler)

server.listen(port, (err) => {  
  if (err) {
    return console.log('something bad happened', err)
  }

  console.log(`server is listening on ${port}`)
})